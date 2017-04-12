package pente;

import pente.Move;
import pente.Board;
import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileReader;

public class MoveEvaluation implements Comparable<MoveEvaluation>{
    public static final int[][] PLAYER_WEIGHTS = MoveEvaluation.loadMatrixFromFile("/Users/julien_mathe/Documents/PlayerWeights.csv");
    public static final int[][] DISTANCE_WEIGHTS = MoveEvaluation.loadMatrixFromFile("/Users/julien_mathe/Documents/DistanceWeights.csv");
    public final Move move;
    public final int evaluation;

    public MoveEvaluation(Move m, Board context, int player){
        this.move = m;
        this.evaluation = evaluate(m, context, player);
    }

    /**
    *
    */
    public int evaluate(Move m, Board b, int player){
        int x,y;
        int evaluation = 0;
        int range = (MoveEvaluation.DISTANCE_WEIGHTS.length - 1)/2;
        int[][] board = b.getBoard();
        for(y = m.row-range; y <= m.row+range; y++){
            for(x = m.col-range; x <= m.col+range; x++){
                if(x > -1 && y > -1 && x < b.getSize() && y < b.getSize()){
                    evaluation += MoveEvaluation.PLAYER_WEIGHTS[board[y][x]][player-1] * MoveEvaluation.DISTANCE_WEIGHTS[y-m.row+range][x-m.col+range];
                }
            }
        }
        return evaluation;
    }

    public int compareTo(MoveEvaluation other){
        if(this.evaluation < other.evaluation)
            return -1;
        if(this.evaluation > other.evaluation)
            return 1;
        return this.move.compareTo(other.move);
    }

    /**
    * Load matrix from resource
    * @param url    File location of the resource to be loaded
    * @return       A matrix of long values from the text file
    */
    private static int[][] loadMatrixFromFile(String url){
        try{
            BufferedReader reader = new BufferedReader(new FileReader(url));
            int r = 1;
            List<int[]> matrix = new ArrayList<int[]>();
            String line = reader.readLine();
            while(line != null){
                String[] nums = line.split(",");
                matrix.add(new int[nums.length]);
                for(int c = 0; c < nums.length; c++){
                    matrix.get(r-1)[c] = Integer.parseInt(nums[c]);
                }
                r++;
                line = reader.readLine();
            }
            reader.close();
            
            return matrix.toArray(new int[matrix.size()][matrix.get(0).length]);
        }
        catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
}