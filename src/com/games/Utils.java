package com.games;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Utils {

	public static String mapToString(Map m) {
		if (m == null) {
			throw new NullPointerException("Map can't be null");
		}

		Iterator it = m.keySet().iterator();
		StringBuilder sb = new StringBuilder();
		Object o = null;
		if (it.hasNext()) {
			o = it.next();
		}

		while (o != null) {
			sb.append("{" + o);
			Object value = m.get(o);
			if (value instanceof Object[]) {
				sb.append("[");
				Object[] array = (Object[]) value;
				for (int i = 0; i < array.length; i++) {
					sb.append(array[i]);
					if (i + 1 < array.length) {
						sb.append(",");
					}
				}
				sb.append("]");
			}
			
			if(value instanceof List) {
				sb.append("[");
				List list = (List) value;
				for (int i = 0; i < list.size(); i++) {
					sb.append(list.get(i));
					if (i + 1 < list.size()) {
						sb.append(",");
					}
				}
				sb.append("]");
			}

			if (it.hasNext()) {
				sb.append("}, ");
				o = it.next();
			} else {
				sb.append("}");
				o = null;
			}
		}
		return sb.toString();
	}

	
	public static void printGrid(String[][] grid, boolean withCoords, ArrayList<Coordinate> markCoords) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				if(markCoords != null && lookCoordinate(markCoords, new Coordinate(i,j))) {
					sb.append("*");
				}
				if(withCoords) {
					sb.append(grid[i][j]).append("[").append(i).append(",").append(j).append("]");
				} else {
					sb.append(grid[i][j]);
				}
			}
			sb.append("\n");
		}
		System.out.println(sb);
	}
	
	private static boolean lookCoordinate(ArrayList<Coordinate> list, Coordinate coord) {
		Iterator<Coordinate> it = list.iterator();
		while(it.hasNext()) {
			Coordinate co = it.next();
			if(co.equals(coord)){
				return true;
			}
		}
		return false;
	}
	
	public static void printGridWithCoords(String[][] grid) {
		printGrid(grid, true, null);
	}
	
	public static void printGridWithoutCoords(String[][] grid) {
		printGrid(grid, false, null);
	}
	
	public static void printAndMarkCoordsInGrid(String[][] grid, ArrayList<Coordinate> toRemove) {
		printGrid(grid, true, toRemove);
	}

	public ArrayList<String> getIndexesByLetter(Character[][] grid, Character ch) {
		
		ArrayList<String> indexes = new ArrayList<String>();
		for (int i = 0; i < grid.length; i++) {
			for (int y = 0; y < grid[i].length; y++) {
				if(grid[i][y].equals(ch)) {
					indexes.add(i + "," + y);
				}
			}
		}
		return indexes;
	}

	public static void printFlatGrid(String[][] grid) {
		StringBuilder sb = new StringBuilder();
		for(int i =0; i < grid.length; i ++) {
			for(int j =0; j < grid.length; j ++) {
				sb.append(grid[i][j]);
			}
		}
		System.out.println(sb);
	}

	public static ArrayList<Coordinate> getIndexesByLetter(Character ch, String[][] grid) {
//		printLog("Getting indexes by letter " + ch);
		ArrayList<Coordinate> indexes = new ArrayList<Coordinate>();
		for (int idx = 0; idx < grid.length; idx++) {
			for (int idy = 0; idy < grid[idx].length; idy++) {
				if (grid[idx][idy].equals(ch + "")) {
					indexes.add(new Coordinate(idx, idy));
				}
			}
		}
		return indexes;
	}
	
}
