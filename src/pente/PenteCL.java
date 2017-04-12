package pente;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import players.AIPlayer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class PenteCL{
    private static final String URL = "monUrl";
	public static void main(String[] args){
            //Connexion
            try{
                boolean bPartieFinie = false;
                URL urlConnect = new URL(URL+"/connect/Pastaoili");
                HttpURLConnection connect = (HttpURLConnection) urlConnect.openConnection();
                connect.setRequestMethod("GET");
                connect.setRequestProperty("Accept", "application/json");
                if (connect.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + connect.getResponseCode());
                }
                InputStream is = connect.getInputStream();
                BufferedReader bfr = new BufferedReader(new InputStreamReader(is));
                StringBuilder builder = new StringBuilder(2048);
                String line;
                while ((line = bfr.readLine()) != null) {
                    builder.append(line);
                }
                connect.disconnect();
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(builder.toString());
                String idJoueur	= (String) json.get("idJoueur");
                String nomJoueur = (String) json.get("nomJoueur");
                int numJoueur = (int) json.get("numJoueur");
                boolean aMoi = false;
                while(!bPartieFinie){
                    while(!aMoi){
                        //Recup du json turn
                        URL urlTurn = new URL(URL+"/turn/"+idJoueur);
                        HttpURLConnection turn = (HttpURLConnection) urlTurn.openConnection();
                        turn.setRequestMethod("GET");
                        turn.setRequestProperty("Accept", "application/json");
                        if (connect.getResponseCode() != 200) {
                            throw new RuntimeException("Failed : HTTP error code : " + connect.getResponseCode());
                        }
                        is = turn.getInputStream();
                        bfr = new BufferedReader(new InputStreamReader(is));
                        builder = new StringBuilder(2048);
                        while ((line = bfr.readLine()) != null) {
                            builder.append(line);
                        }
                        parser = new JSONParser();
                        json = (JSONObject) parser.parse(builder.toString());
                        //V�rifie si � moi de jou�
                        aMoi = (boolean) json.get("status");
                        //Recup du plateau de jeu
                        Board board = new Board(19, 2);
                        int[][] copyOfBoard = (int[][]) json.get("tableau");
                        board.setBoard(copyOfBoard);
                        //Recup d�tails fin de partie
                        bPartieFinie = (boolean) json.get("finPartie");
                        String detailsFinPartie = (String) json.get("detailsFinPartie");
                        //Pause pendant 0.5s
                        Thread.sleep(500);
                    }
                    
                }
            }catch(Exception e){
                
            }
            
            
		//Game game = new Game(2, 19);
		//game.addPlayer(1, new AIPlayer(1, 3));
                //game.addPlayer(2, new AIPlayer(2, 4));
		//int winner = game.playGame();

		//if(winner == -1){
		//	System.out.println("No one wins!");
		//}
		//else{
		//	System.out.println(String.format("Winner is player %d!", winner));
		//}
	}
}