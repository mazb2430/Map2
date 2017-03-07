import java.io.Serializable;

public class Position implements Serializable, Comparable<Position> {
	private int x;
	private int y;
	private static final long serialVersionUID = -3753716621765159021L;
	
	public Position(int x, int y) {
		this.x = x-11;
		this.y = y-19;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public int hashCode() {
		return x+y;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof Position) {
			Position pos = (Position) obj;
			return x == pos.x && y == pos.y;
		} else {
			return false;
		}
	}
	
	public int compareTo(Position other) {
		int cmp = x - other.x;
		if(cmp != 0)
			return cmp;
		return y - other.y;		
	}
	
	public String toString() {
		return Integer.toString(hashCode()); 
		
	}
	
	
}
