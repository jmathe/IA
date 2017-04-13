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
            try{
                boolean bPartieFinie = false;
                boolean aMoi = false;
                int code = 0;
                int numJoueur = 0;
                
                //Connexion
                URL urlConnect = new URL(URL+"/connect/Pastaoili");
                HttpURLConnection connect = (HttpURLConnection) urlConnect.openConnection();
                connect.setRequestMethod("GET");
                connect.setRequestProperty("Accept", "application/json");
                InputStream is = connect.getInputStream();
                BufferedReader bfr = new BufferedReader(new InputStreamReader(is));
                StringBuilder builder = new StringBuilder(2048);
                String line;
                while ((line = bfr.readLine()) != null) {
                    builder.append(line);
                }
                connect.disconnect();
                //Fin connexion
                
                //Recup données json
                String stringBuilder = builder.toString();
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(stringBuilder);
                String idJoueur = (String) json.get("idJoueur");
                String nomJoueur = (String) json.get("nomJoueur");
                System.out.println("id : " + idJoueur);
                System.out.println("nom : " + nomJoueur);
                Object c = json.get("code");
                Object num = json.get("numJoueur");
                //Fin recup données
                
                //test type des variables
                if(!(c instanceof Integer)){
                    code = (int) (long) c;
                    System.out.println("code pas int : " + code);
                }else{
                    code = (int) c;
                    System.out.println("code : " + code);
                }
                if(!(num instanceof Integer)){
                    numJoueur = (int) (long) num;
                    System.out.println("num pas int : " + numJoueur);
                }else{
                    numJoueur = (int) num;
                    System.out.println("num : " + numJoueur);
                }
                //Fin test type
                
                //Test code reponse
                if(code == 401) {
                    System.out.println("Vous n'êtes pas autorisé à entrer dans la partie");
                    bPartieFinie = true;
                }
                if (code == 503){
                    System.out.println("Connexion impossible !");
                    bPartieFinie = true;
                }
                //Fin test code reponse
                
                //Création joueur IA 
                AIPlayer joueur = new AIPlayer(numJoueur, idJoueur, nomJoueur, 3);
                Board board = new Board(19, 2);
                while(!bPartieFinie){
                    System.out.println("dans partie");
                    while(!aMoi){
                        //Turn
                        URL urlTurn = new URL(URL+"/turn/"+idJoueur);
                        HttpURLConnection turn = (HttpURLConnection) urlTurn.openConnection();
                        turn.setRequestMethod("GET");
                        turn.setRequestProperty("Accept", "application/json");
                        is = turn.getInputStream();
                        bfr = new BufferedReader(new InputStreamReader(is));
                        builder = new StringBuilder(2048);
                        while ((line = bfr.readLine()) != null) {
                            builder.append(line);
                        }
                        turn.disconnect();
                        //Fin Turn
                        
                        parser = new JSONParser();
                        json = (JSONObject) parser.parse(builder.toString());
                        
                        Object turnObject = json.get("status");
                        //Vérifie si à moi de joué
                        if(!(turnObject instanceof Integer)){
                            int turnValue = (int) (long) turnObject;
                            if (turnValue == 0) {
                                aMoi = false;
                            } else {
                                aMoi = true;
                            }
                        }

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
                    
                    //Recup du json play
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
                System.out.print(e);
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