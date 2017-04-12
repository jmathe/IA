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
    private static final String URL = "http://5.196.89.227/app_dev.php";
	public static void main(String[] args){
            //Connexion
            try{
                System.out.println(URL);
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
                System.out.println(idJoueur + " ---- " + nomJoueur + " ---- " + numJoueur);
                AIPlayer joueur = new AIPlayer(numJoueur, idJoueur, nomJoueur, 3);
                Board board = new Board(19, 2);
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
                        turn.disconnect();
                        parser = new JSONParser();
                        json = (JSONObject) parser.parse(builder.toString());
                        //Vérifie si à moi de joué
                        aMoi = (boolean) json.get("status");
                        //Recup du plateau de jeu
                        int[][] copyOfBoard = (int[][]) json.get("tableau");
                        board.setBoard(copyOfBoard);
                        //Recup détails fin de partie
                        bPartieFinie = (boolean) json.get("finPartie");
                        String detailsFinPartie = (String) json.get("detailsFinPartie");
                        //Pause pendant 0.5s
                        Thread.sleep(500);
                    }
                    //Recup le mouvement de l'IA
                    Move m = joueur.getMove(board);
                    //Recup du json turn
                    URL urlPlay = new URL(URL+"/play/"+m.col+"/"+m.row+"/"+joueur.nomJoueur);
                    HttpURLConnection play = (HttpURLConnection) urlPlay.openConnection();
                    play.setRequestMethod("GET");
                    play.setRequestProperty("Accept", "application/json");
                    if (connect.getResponseCode() != 200) {
                        throw new RuntimeException("Failed : HTTP error code : " + connect.getResponseCode());
                    }
                    play.disconnect();
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