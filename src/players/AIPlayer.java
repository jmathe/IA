package players;

import pente.Player;
import pente.Move;
import pente.MoveEvaluation;
import pente.Board;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import pente.BoardEvaluation;

public class AIPlayer extends Player{
    private static final int MOVE_LIMIT = 30;
    private HashMap<Long, BoardEvaluation> evaluated;
    private int depth;

    public AIPlayer(int num, String id, String nom, int evaluationDepth){
        super(num, id, nom);
        this.depth = evaluationDepth;
        this.evaluated = new HashMap<Long, BoardEvaluation>(1000);
    }

    /**
    * Stupid for now, takes move with best evaluation
    */
    public Move getMove(Board boardState){
        List<Move> movesToConsider = this.getMovesToConsider(boardState, this.numJoueur);

        // Search first layer, find best move from evaluations
        int best = Integer.MIN_VALUE;
        int eval;
        Move bestMove = movesToConsider.get(0);
        for(Move m : movesToConsider){
            // Deep copy current board before making move
            Board childState = new Board(boardState);
            childState.makeMove(new Move(this.numJoueur, m.row, m.col));
            eval = searchNode(childState, this.depth, best, Integer.MAX_VALUE, false);
            if(eval > best){
                best = eval;
                bestMove = m;
                System.out.println(eval);
            }
        }

        // Set the player for the move before returning it
        return new Move(this.numJoueur, bestMove.row, bestMove.col);
    }

    /**
    * Evaluates all possible moves to determine which the current player should consider
    * @param b  The current board state
    * @return   A list of valid moves that had an evaluation score above the specified threshold
    */
    private List<Move> getMovesToConsider(Board b, int player){
        List<Move> validMoves = b.getMoves();
        List<MoveEvaluation> moveEvaluations = new ArrayList<MoveEvaluation>(validMoves.size());
        for(Move m : validMoves){
            moveEvaluations.add(new MoveEvaluation(m, b, player));
        }

        // Add moves to list to consider ordered from best to worst
        Collections.sort(moveEvaluations);
        int nextEval;
        int i = moveEvaluations.size();

        List<Move> consideredMoves = new ArrayList(validMoves.size());        
        do{
            i--;
            nextEval = moveEvaluations.get(i).evaluation;
            consideredMoves.add(moveEvaluations.get(i).move);
        } while(i > 0 && consideredMoves.size() < AIPlayer.MOVE_LIMIT);

        return consideredMoves;
    }

    /**
    * Alpha-beta pruning search. Evaluates all children of the given board to the specified move
    * depth. Alpha and beta serve as the current evaluation baseline
    * @param b          The board state that is being searched
    * @param depth      How many levels deeper the search should continue. Decrements with each move
    * @param alpha      Current best maximizing evaluation
    * @param beta       Current best minimizing evaluation
    * @param maximize   Whether the search should maximize or minimize 
    * @return           The evaluation of this node
    */
    private int searchNode(Board b, int depth, int alpha, int beta, boolean maximize){
        // Game over, can't search further. Return win/lose/draw
        if(b.gameOver()){
            int winner = b.getWinner();
            if(winner == -1)
                return 0;
            return winner == this.numJoueur ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }

        // Don't search any deeper
        if(depth == 0){
            if(b.getLastPlayer() == this.numJoueur)
                return this.evaluateBoard(b).score;
            // Evaluation is for other player (positive is bad for us), take opposite
            return -this.evaluateBoard(b).score;
        }

        int currentPlayer = b.getCurrentPlayer();
        boolean maximizeNext = b.getNextPlayer() == this.numJoueur;
        List<Move> moves = this.getMovesToConsider(b, currentPlayer);
        if(maximize){
            int best = Integer.MIN_VALUE;
            for(Move m : moves){
                // Deep copy current board before making move
                Board childState = new Board(b);
                childState.makeMove(new Move(currentPlayer, m.row, m.col));
                best = Math.max(best, searchNode(childState, depth-1, alpha, beta, maximizeNext));
                alpha = Math.max(alpha, best);
                if(beta <= alpha)
                    break;
            }
            return best;
        }
        else{
            int best = Integer.MAX_VALUE;
            for(Move m : moves){
                // Deep copy current board before making move
                Board childState = new Board(b);
                childState.makeMove(new Move(currentPlayer, m.row, m.col));
                best = Math.min(best, searchNode(childState, depth-1, alpha, beta, maximizeNext));
                beta = Math.min(beta, best);
                if(beta <= alpha)
                    break;
            }
            return best;
        }
    }

    /**
    * Evaluates the given board if it is not already in the hashmap
    * @param b  The board to evaluate
    * @return   The newly created evaluation of this board or the already existing one
    */
    public BoardEvaluation evaluateBoard(Board b){
        if(this.evaluated.containsKey(b.getHash())){
            return evaluated.get(b.getHash());
        }
        else{
            BoardEvaluation newEvaluation = new BoardEvaluation(b);
            evaluated.put(b.getHash(), newEvaluation);
            return newEvaluation;
        }
    }
}