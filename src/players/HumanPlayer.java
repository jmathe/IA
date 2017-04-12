package players;

import pente.*;
import java.util.Scanner;
import java.util.List;
import java.util.Collections;

public class HumanPlayer extends Player{
	private Scanner in;

	public HumanPlayer(int num, String id, String nom){
		super(num, id, nom);
		in = new Scanner(System.in);
	}

	/**
	* Gets move from user command line input
	*/
	public Move getMove(Board boardState){
		List<Move> moves = boardState.getMoves();
		Move move;
		do{
			System.out.println("Enter move as row index, column index with no spaces: ");
			String nextMove = in.nextLine();
			try{
				String[] parts = nextMove.split(",");
				if(parts.length != 2){
					System.out.println("Invalid move");
					continue;
				}
				int moveR = Integer.parseInt(parts[0]);
				int moveC = Integer.parseInt(parts[1]);
				move = new Move(this.numJoueur, moveR, moveC);
				// Move is in list. Exit loop
				if(Collections.binarySearch(moves, move) >= 0)
					break;
				// Not in list, invalid
				System.out.println("Invalid move");
			}
			catch(NumberFormatException e){
				System.out.println("Invalid move");
			}
		} while(true);

		return move;
	}	
}