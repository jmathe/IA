package pente;

import java.util.ArrayList;

public class Move implements Comparable<Move>{
	public final int player, row, col;

	public Move(int p, int r, int c){
		player = p;
		row = r;
		col = c;
	}

	/**
	* Compares two moves ignoring player. Compares row first, then column. Higher row/col returns
	* 1.
	* @param other 	The move to compare this move to
	* @return 		0 if the moves are the same, +/- 1 if this move is greater/less than
	*/
	public int compareTo(Move other){
		if(this.row < other.row)
			return -1;
		if(this.row > other.row)
			return 1;
		if(this.col < other.col)
			return -1;
		if(this.col > other.col)
			return 1;
		return 0;
	}

	@Override
	public String toString(){
		return String.format("Player:%d Row:%d Col:%d\n", this.player, this.row, this.col);
	}
}