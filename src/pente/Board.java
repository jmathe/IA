package pente;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileReader;

public class Board{
    // Height and width of board (board is assumed to be square)
    private final int size;
    // Array representing board. 
    private int[][] board;
    // Array indicating how many of each players pieces have been captured 
    private int[] capturedPieces;
    // Again, used to efficiently keep track of remaining moves
    private List<Move> validMoves;
    // Same dealio
    private int moveNum;
    // Number of winning player (-1 if none), used to see if game is over
    private int winner;
    // List of contiguous spaces containing same player
    private List<Chain> chains;
    // A unique 64 bit integer representing the board. Uses Zobrist hashing
    private long hash;
    // Number of players in game
    private final int numPlayers;
    // Length of chain needed to win
    private static final int WIN_LENGTH = 5;
    // Number of captures needed to win game
    private static final int WINNING_CAPTURES = 5;
    // Load hash table values
    private static final long[][] BOARD_KEYS = Board.loadMatrixFromFile("C:\\Users\\leo\\Documents\\BoardHashVals.csv");
    private static final long[][] CAPTURE_KEYS = Board.loadMatrixFromFile("C:\\Users\\leo\\Documents\\CaptureHashVals.csv");

    public Board(int dimension, int numPlayers){
        this.size = dimension;
        this.hash = 0;
        this.numPlayers = numPlayers;
        this.winner = -1;
        this.board = new int[size][size];
        this.validMoves = new ArrayList<Move>(dimension*dimension - 1);
        this.chains = new ArrayList<Chain>();
        this.updateChains();
        
        for(int r = 0; r < size; r++){
            for(int c = 0; c < size; c++){
                this.validMoves.add(new Move(0, r, c));
            }
        }
        // Play first move
        this.moveNum = 2;
        placePiece(new Move(1, 9, 9));
        this.capturedPieces = new int[numPlayers];
    }

    // Makes a deep copy of the specified board
    public Board(Board copy){
        this.size = copy.getSize();        
        this.hash = copy.getHash();
        this.numPlayers = copy.getNumPlayers();
        this.moveNum = copy.getMoveNum();
        this.winner = copy.getWinner();
        this.board = new int[this.size][this.size];

        // Deep copy board
        int[][] otherBoard = copy.getBoard();
        for(int r = 0; r < this.size; r++){
            this.board[r] = Arrays.copyOf(otherBoard[r], this.size);
        }
        // Deep copy captured pieces
        this.capturedPieces = Arrays.copyOf(copy.getCaptures(), this.numPlayers);

        // Valid moves doesn't have to be a deep copy since move is immutable
        this.validMoves = new ArrayList<Move>(copy.getMoves());
        
        // Deep copy is only done before making a move, therefore chain list will be cleared. Create
        // new array list for the copy
        this.chains = new ArrayList<Chain>(copy.getChains().size());
    }

    /**
    * Makes the specified move if it is valid and returns whether or not it was
    * @param m  The move the player attempted
    * @return   Whether or not the move was valid
    */
    public boolean makeMove(Move m){
        if(this.board[m.row][m.col] != 0)
            return false;

        // Add piece to board and update hash with new piece
        this.placePiece(m);
        // Remove any pairs and hash them out
        this.makeCaptures(m);

        // Update list of chains
        this.updateChains();

        moveNum++;

        return true;
    }

    /**
    * Finds all sequences locations with same player or no player
    */
    public void updateChains(){
        // Re-initialize list of chains
        this.chains.clear();
        int r, c, where;
        // Search for chains at all board positions
        for(r = 0; r < this.size; r++){
            for(c = 0; c < this.size; c++){
                // Find chains starting at index r,c
                List<Chain> newChains = Chain.findChains(this.board, r, c);
                // Insert new chains
                for(Chain chain : newChains){
                    where = -Collections.binarySearch(this.chains, chain) - 1;
                    if(where < 0)
                        continue;
                    chains.add(where, chain);
                    if(chain.player != 0 && chain.length >= Board.WIN_LENGTH)
                        this.winner = chain.player;
                }
            }
        }
    }
    
    /**
    * Gets all chains for a certain player
    * @return All chains on the board for the given player
    */
    public List<Chain> getPlayerChains(int player){
        List<Chain> playerChains = new ArrayList<Chain>();
        int start = 0;
        // Find first instance of a chain of this player
        while(start < this.chains.size() && this.chains.get(start).player != player){
            start++;
        }

        // Add all chains of this player
        int end = start;
        while(end < this.chains.size() && this.chains.get(end).player == player){
            end++;
        }
        return this.chains.subList(start, end);
    }

    /**
    * Checks if board is full or if someone has via pente or captures
    * @return   Whether or not the game is over
    */
    public boolean gameOver(){
        return this.winner != -1 || this.validMoves.size() == 0;
    }

    /**
    * Returns a string representing the board. Pieces are displayed according to the number of the
    * player that placed them. Empty locations are displayed as zeros
    * @return   the string representing the board
    */
    @Override
    public String toString(){
        String text = "     ";
        int i, r, c;
        // Column header
        for(i = 0; i < this.size; i++){
            text += String.format("%-3d", i);
        }
        text += "\n";
        for(r = 0; r < this.size; r++){
            text += String.format("%-5d", r);
            for(c = 0; c < this.size; c++){
                text += String.format("%-3d", this.board[r][c]);
            }
            text += "\n";
        }

        return text;
    }

    /**
    * Checks for any pairs captured by this move. Updates board, list of captures, and winner
    * (if necessary)
    * @param m  Current move
    */
    private void makeCaptures(Move m){
        int xDir, yDir;
        Move newMove;
        for(xDir = -1; xDir <= 1; xDir++){
            for(yDir = -1; yDir <= 1; yDir++){
                // Check if pair and opposite end is entirely on the board. Skip if not
                if(m.row + 3*xDir < 0 || m.row + 3*xDir >= this.size || m.col + 3*yDir < 0 || m.col + 3*yDir >= this.size)
                    continue;

                // Check that pair of locations are empty and do not belong to the moving player
                if(this.board[m.row + xDir][m.col + yDir] == m.player)
                    continue;
                if(this.board[m.row + xDir][m.col + yDir] == 0)
                    continue;
                if(this.board[m.row + 2*xDir][m.col + 2*yDir] == m.player)
                    continue;
                if(this.board[m.row + 2*xDir][m.col + 2*yDir] == 0)
                    continue;
                
                // Check if this player is covering opposite end of chain. Skip if not
                if(this.board[m.row + 3*xDir][m.col + 3*yDir] != m.player)
                    continue;
                
                // A capture was made, clear pair, add back those locations as valid moves and
                // update captures and hash
                removePiece(m.row + xDir, m.col + yDir, m.player);
                removePiece(m.row + 2*xDir, m.col + 2*yDir, m.player);
                this.capturedPieces[m.player-1]++;

                // Check if game has ended
                if(this.capturedPieces[m.player - 1] >= Board.WINNING_CAPTURES)
                    this.winner = m.player;
            }
        }
    }

    /**
    * Adds player to board and updates hash
    */
    private void placePiece(Move m){
        this.board[m.row][m.col] = m.player;
        // Update list of valid moves
        int where = Collections.binarySearch(this.validMoves, m);
        this.validMoves.remove(Collections.binarySearch(this.validMoves, m));
        // XOR in new player
        this.hash = this.hash ^ Board.BOARD_KEYS[m.row*this.size + m.col][m.player-1];
    }

    /**
    * Removes pair from board and updates hash
    */
    private void removePiece(int xPos, int yPos, int capturingPlayer){
        Move newMove = new Move(0, xPos, yPos);
        int where = -Collections.binarySearch(this.validMoves, newMove) - 1;
        this.validMoves.add(where, newMove);
        
        // XOR out removed player
        this.hash = this.hash ^ Board.BOARD_KEYS[xPos*this.size + yPos][this.board[xPos][yPos]-1];
        // XOR in a capture
        this.hash = this.hash ^ Board.CAPTURE_KEYS[this.capturedPieces[capturingPlayer-1]][capturingPlayer-1];

        this.board[xPos][yPos] = 0;
    }

    /**
    * Load matrix from resource
    * @param url    File location of the resource to be loaded
    * @return       A matrix of long values from the text file
    */
    private static long[][] loadMatrixFromFile(String url){
        try{
            BufferedReader reader = new BufferedReader(new FileReader(url));
            int r = 1;
            List<long[]> matrix = new ArrayList<long[]>();
            String line = reader.readLine();
            while(line != null){
                String[] nums = line.split(",");
                matrix.add(new long[nums.length]);
                for(int c = 0; c < nums.length; c++){
                    matrix.get(r-1)[c] = Long.parseLong(nums[c]);
                }
                r++;
                line = reader.readLine();
            }
            reader.close();
            

            return matrix.toArray(new long[matrix.size()][matrix.get(0).length]);
        }
        catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    // Getters

    public int[][] getBoard(){
        return this.board;
    }

    public int getNumPieces(){
        return this.size*this.size - this.validMoves.size();
    }

    public int getSize(){
        return this.size;
    }

    public int getMoveNum(){
        return this.moveNum;
    }

    public int getNumPlayers(){
        return this.numPlayers;
    }

    public int[] getCaptures(){
        return this.capturedPieces;
    }

    public List<Move> getMoves(){
        return this.validMoves;
    }

    public List<Chain> getChains(){
        return this.chains;
    }

    public int getWinner(){
        return this.winner;
    }

    public long getHash(){
        return this.hash;
    }

    public int getCurrentPlayer(){
        return (this.moveNum % this.numPlayers) + 1;
    }

    public int getNextPlayer(){
        return ((this.moveNum + 1) % this.numPlayers) + 1;
    }

    public int getLastPlayer(){
        return ((this.moveNum - 1) % this.numPlayers) + 1;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public void setCapturedPieces(int[] capturedPieces) {
        this.capturedPieces = capturedPieces;
    }

    public void setValidMoves(List<Move> validMoves) {
        this.validMoves = validMoves;
    }

    public void setMoveNum(int moveNum) {
        this.moveNum = moveNum;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public void setChains(List<Chain> chains) {
        this.chains = chains;
    }

    public void setHash(long hash) {
        this.hash = hash;
    }
    
    
}