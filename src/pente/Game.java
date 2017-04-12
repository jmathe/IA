package pente;

import pente.Board;
import pente.Player;
import java.util.ArrayList;

public class Game{
	private Board board;
	private Player[] players;

	public Game(int numPlayers, int dimension){
		players = new Player[numPlayers];
		board = new Board(dimension, numPlayers);
	}

	public void addPlayer(int which, Player p){
		players[which-1] = p;
	}

	/**
	* Plays the game, asking each player for their move on their turn. Displays board and captures between moves
	* @return The number of the winner
	*/
	public int playGame(){
		// First player is player 2 (first move automatically played)
		int nextPlayer = 1;
		while(!board.gameOver()){
			this.playMove(nextPlayer);
			nextPlayer = (nextPlayer + 1)%this.players.length;
		}

		return board.getWinner();
	}

	/**
	* Steps the game forward one move
	*/
	public void playMove(int nextPlayer){
		// Request move from player whose turn it is
		Move m = this.players[nextPlayer].getMove(this.board);
		System.out.println(m);
		boolean success = this.board.makeMove(m);
		if(!success){
			System.out.println("ERROR");
			System.exit(0);
		}

		printCaptures();
		displayBoard();
	}

	/**
	* Prints the board to stdout. Override this method to display by another means
	*/
	public void displayBoard(){
		System.out.println(board);
	}

	public void printCaptures(){
		int[] captures = this.board.getCaptures();
		for(int i = 0; i < captures.length; i++){
			System.out.println(String.format("Player %d: %d captures", i+1, captures[i]));
		}
	}

	// Getters

	public Board getBoard(){
		return this.board;
	}

	public int getCurrentPlayer(){
		return ((this.board.getMoveNum()-1) % this.players.length) + 1;
	}

}