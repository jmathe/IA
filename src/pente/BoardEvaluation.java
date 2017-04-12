package pente;

import pente.Board;
import java.util.List;

public class BoardEvaluation implements Comparable<BoardEvaluation>{
    // TODO: these
    private static final int[] CAPTURE_WEIGHTS = {0, 1, 2, 4, 8};
    private static final int[] CHAIN_WEIGHTS = {-1, 1, 8};
    public final int moveNum;
    public final long hash;
    public final int score;

    public BoardEvaluation(Board b){
        hash = b.getHash();
        score = evaluateBoard(b);
        moveNum = b.getMoveNum();
    }

    /**
    * Compares evaluations. Order: moveNum, score, hash
    */
    @Override    
    public int compareTo(BoardEvaluation other){
        if(this.moveNum < other.moveNum)
            return -1;
        if(this.moveNum > other.moveNum)
            return 1;
        if(this.score < other.score)
            return -1;
        if(this.score > other.score)
            return 1;
        if(this.hash == other.hash)
            return 0;
        return this.hash < other.hash ? -1 : 1;
    }

    /**
    *
    */
    public int evaluateBoard(Board b){
        int evaluation = 0;
        // Evaluating for player who just moved
        int player = b.getLastPlayer();

        // Weigh current captures
        int[] captures = b.getCaptures();
        for(int i = 0; i < captures.length; i++){
            if(player - 1 == i){
                evaluation += BoardEvaluation.CAPTURE_WEIGHTS[captures[i]];
            }
            else{
                evaluation -= BoardEvaluation.CAPTURE_WEIGHTS[captures[i]];
            }
        }

        // Weigh chains
        List<Chain> chains = b.getChains();
        for(Chain c : chains){
            if(c.player == player){
                evaluation += BoardEvaluation.CHAIN_WEIGHTS[c.length - 2];
            }
            else if(c.player != 0){
                evaluation -= BoardEvaluation.CHAIN_WEIGHTS[c.length - 2];
            }
        }

        return evaluation;
    }

}