package com.games;

public class Coordinate {
	
	private int X_POSITION = 0;
	private int Y_POSITON = 0;
	
	public Coordinate(int x, int y) {
		X_POSITION = x;
		Y_POSITON = y;
	}
	
	
	public Coordinate(Integer[] cords) {
		X_POSITION = cords[0];
		Y_POSITON = cords[1];
	}
	
	public Integer[] getPositions() {
		return new Integer[]{X_POSITION, Y_POSITON};
	}
	
	public int getX_POSITION() {
		return X_POSITION;
	}
	
	public int getY_POSITON() {
		return Y_POSITON;
	}
	
	public String toString() {
		return String.format("[%s,%s]", X_POSITION, Y_POSITON);
	}
	
	public boolean equals() {
		return false;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + X_POSITION;
		result = prime * result + Y_POSITON;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coordinate other = (Coordinate) obj;
		if (X_POSITION != other.X_POSITION)
			return false;
		if (Y_POSITON != other.Y_POSITON)
			return false;
		return true;
	}
	
}
