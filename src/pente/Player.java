package pente;

import pente.Move;

public abstract class Player{
	public String idJoueur;
        public String nomJoueur;
        // Number representing player. Between 1 and the number of players in the game
        public int numJoueur;

	public Player(int num, String id, String nom){
            idJoueur = id;
            nomJoueur = nom;
            numJoueur = num;
	}

	public abstract Move getMove(Board boardState);
}