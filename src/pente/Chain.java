package pente;

import java.util.List;
import java.util.ArrayList;

public class Chain implements Comparable<Chain>{
    // Pairs of integers describing x and y directions of possible chains
    public static final int[][] DIRECTIONS = {{1,-1},{1,0},{1,1},{0,1}};
    // Directions
    public static final int RIGHT_UP = 0;
    public static final int RIGHT = 1;
    public static final int RIGHT_DOWN = 2;
    public static final int DOWN = 3;
    // Default length for chains arraylist, defined by the number of chains in an empty 19x19 board
    public static final int MAX_CHAINS = 361; //?
    // Integer referring to one of previously mentioned directions
    public final int direction;
    // Start index on board of chain
    public final int startR, startC;
    // Length of chain
    public final int length;
    // Player comprising chain
    public final int player;
    // If chain is of length 2 and has one end covered
    public final boolean canBeCaptured;
    // Number of open ends on this chain (0, 1, or 2)
    public final int openEnds;

    public Chain(int r, int c, int dir, int len, int p, boolean canCap, int open){
        startR = r;
        startC = c;
        direction = dir;
        length = len;
        player = p;
        this.canBeCaptured = canCap;
        this.openEnds = open;
    }

    public boolean isInChain(int r, int c){
        if(direction == RIGHT){
            return (r - startR <= length) && (r - startR >= 0);
        }
        if(direction == DOWN){
            return (c - startC <= length) && (c - startC >= 0);
        }
        if(direction == RIGHT_UP){
            return (r - startR <= length) && (r - startR >= 0) && (startC - c <= length) && (startC - c >= 0);
        }
        return (r - startR <= length) && (r - startR >= 0) && (c - startC <= length) && (c - startC >= 0);
    }

    /**
    * Finds chains originating at r, c in board b
    * @param b  The board array to search
    * @param r  The row index of the position to test
    * @param c  The column index of the position to test
    */
    public static List<Chain> findChains(int[][] b, int r, int c){
        List<Chain> chains = new ArrayList<Chain>(Chain.MAX_CHAINS);
        int size = b.length;
        // Iterate through possible directions
        for(int i = 0; i < Chain.DIRECTIONS.length; i++){
            // The board position that would come before this position in a chain
            int[] prevLoc = {r - Chain.DIRECTIONS[i][0], c - Chain.DIRECTIONS[i][1]};
            // Check if previous board position is valid or if it is already in a chain
            int openEnds = 0;
            if(prevLoc[0] >= 0 && prevLoc[1] >= 0 && prevLoc[1] < size){
                if(b[prevLoc[0]][prevLoc[1]] == b[r][c])
                    continue;
                else{
                    // Valid chain, check if starting end is open
                    if(b[prevLoc[0]][prevLoc[1]] == 0)
                        openEnds++;
                }
            }
            
            int length = 1;
            int[] nextLoc = {r + Chain.DIRECTIONS[i][0], c + Chain.DIRECTIONS[i][1]};
            // Continue searching through board until edge is reached
            while(nextLoc[0] < size && nextLoc[1] < size && nextLoc[1] >= 0){
                // Still in chain, keep searching
                if(b[nextLoc[0]][nextLoc[1]] == b[r][c]){
                    length++;
                }
                else{
                    // Chain is blocked
                    if(b[nextLoc[0]][nextLoc[1]] != 0)
                        break;
                    openEnds++;
                    break;
                }
                nextLoc[0] += Chain.DIRECTIONS[i][0];
                nextLoc[1] += Chain.DIRECTIONS[i][1];    
            }
            // Create chain
            if(length > 1){
                boolean canCap = length == 2 && openEnds == 1;
                chains.add(new Chain(r, c, i, length, b[r][c], canCap, openEnds));
            }
        }
        return chains;
    }

    /**
    * Compares two chains by the following order: player number, length, direction, start column, start row
    * @param other  The chain to compare to
    * @return       0 if the chains are identical, +/- 1 if Chain is greater/less according to properties
    */
    public int compareTo(Chain other){
        if(this.player < other.player)
            return -1;
        if(this.player > other.player)
            return 1;
        if(this.length < other.length)
            return -1;
        if(this.length > other.length)
            return 1;
        if(this.direction < other.direction)
            return -1;
        if(this.direction > other.direction)
            return 1;
        if(this.startC < other.startC)
            return -1;
        if(this.startC > other.startC)
            return 1;
        if(this.startR == other.startR)
            return 0;
        return this.startR < other.startR ? -1:1;
    }

    @Override
    public String toString(){
        return String.format("Chain at %d, %d with direction %d of length %d for player %d \n", startR, startC, direction, length, player);
    }
}