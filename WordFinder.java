package com.cert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WordFinder {

	public static Map<Character, Integer[]> usedPositions = new HashMap<Character, Integer[]>();
	public static ArrayList<Coordinate> usedCoordinates = new ArrayList<Coordinate>();
	public static Character[][] GRID = null;
	
	//logging purposes
	private static String currentWord = "";
	private static ArrayList<String> wordsToLog = new ArrayList<String>();
	static {
//		wordsToLog.add("same");
	}

	private static boolean printLog(String word) {
		return wordsToLog.contains(currentWord);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String letters = "nhcaeefse";
		int gridSize = 3;
		int firstWordLength = 3;
		
		String dictionaryFileName = String.format("C:\\Ulises_codebase\\Dictionary%sLetters.txt", firstWordLength);
		
		ArrayList<String> dictionary = new ArrayList<String>();
		File dictionarySourceFile = new File(dictionaryFileName);
		
		if (dictionarySourceFile.exists() == false) {
			try {
				dictionarySourceFile.createNewFile();
				DictionaryUpdater d = new DictionaryUpdater(firstWordLength, dictionarySourceFile);
				d.downloadDictionary();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		 
		dictionary = loadDictionary(dictionarySourceFile);
		GRID = loadGrid(letters, gridSize);
		findWords(dictionary);
	}

	private static void findWords(ArrayList<String> dictionary) {
	/* *
	  	slide
		l[0,0]s[0,1]e[0,2]
		l[1,0]i[1,1]d[1,2]
		l[2,0]o[2,1]d[2,2]
	 * */	
		
		ArrayList<Coordinate> initialLetterStartIndex = new ArrayList<Coordinate>();
		for(String dictionaryWord : dictionary ) {
			usedCoordinates.clear();
			currentWord = dictionaryWord;
			boolean found = false;
			initialLetterStartIndex = getIndexesByLetter(dictionaryWord.charAt(0));
			for(int i = 0; i < initialLetterStartIndex.size() && found == false; i++) {
				if(printLog(dictionaryWord)) {System.out.println(dictionaryWord + " " + dictionaryWord.charAt(0) + " " + initialLetterStartIndex.toString());}
				Coordinate coord = initialLetterStartIndex.get(i);
				if(printLog(dictionaryWord)){System.out.println("InitialCoord " + coord);}
				
				usedCoordinates.add(coord);
				
				if(printLog("")) {System.out.println("UsedCoordinates " + usedCoordinates.toString());}
				found = findWord(dictionaryWord.substring(1), coord, false);
				if(printLog("")){ System.out.println("outter found " + found);}
				if(found ) {
					System.out.println(" ********Found word " + currentWord);
				}
			}
		}
	}
	
	private static boolean findWord(String word, Coordinate baseCoor, boolean found) {
		
		if(word == null || word.isEmpty()) {
			return true;
		}
		
		Character nextChar = word.charAt(0);
		word = word.substring(1);
		
		ArrayList<Coordinate> adjacentLetters = getAdjacentIndexesByLetter(baseCoor.getX_POSITION(), baseCoor.getY_POSITON(), nextChar);
		if(printLog(word)) {System.out.println("Adjacent Free letters " + nextChar + adjacentLetters.toString());}
		boolean found2 = false;
		for (int i = 0 ; found2 == false && i < adjacentLetters.size(); i++) {
			Coordinate coord = adjacentLetters.get(i);
			usedCoordinates.add(coord);
			
			if(printLog(word)){System.out.println("Adding to used " + nextChar + coord);}
			found2 = findWord(word, coord, true);
			if(found2 == false ) {
				 usedCoordinates.remove(coord);
				 if(printLog(word)){System.out.println("Removing from used " + nextChar + coord);}
			}
		}
		if(printLog("")) {System.out.println("found2 " + found2);}
		return found2;
	}
	
	
	
	private static ArrayList<String> loadDictionary(File path) {
		ArrayList<String> a = new ArrayList<String>();
		BufferedReader r = null;
		try {
			//BufferedInputStream r = new BufferedInputStream(new FileInputStream(path));
			r = new BufferedReader(new FileReader(path));
			String l;
			while((l = r.readLine()) != null ) {
				 l = l.trim().toLowerCase().replace(" ", "");
				 a.add(l);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if( r != null) {
				try {
					r.close();
				} catch (Exception e2) {}
			}
		}
		return a;
	}
	
	private static Character[][] loadGrid(String letters, int size) {
		Character[][] grid = new Character[size][size];
		StringBuilder sb = new StringBuilder();
		int charIdx = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				Character value  = letters.charAt(charIdx++); 
				grid[i][j] = value;
				sb.append(value).append("[").append(i).append(",").append(j).append("]");
			}
			sb.append("\n");
		}
		System.out.println(sb);
		return grid;
	}
	
	public static ArrayList<String> getIndexesByLetter(Character[][] grid, Character ch) {
		
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
	
	public static ArrayList<Coordinate> getIndexesByLetter(Character ch) {
		
		ArrayList<Coordinate> indexes = new ArrayList<Coordinate>();
		for (int idx = 0; idx < GRID.length; idx++) {
			for (int idy = 0; idy < GRID[idx].length; idy++) {
				if(GRID[idx][idy].equals(ch)) {
					indexes.add(new Coordinate(idx, idy));
				}
			}
		}
		return indexes;
	}
	
	public static ArrayList<Coordinate> getAdjacentIndexesByLetter(int x, int y, Character charToFind) {
		ArrayList<Coordinate> cords = new ArrayList<Coordinate>();
		for (int dx = -1; dx <= 1; ++dx) {
			for (int dy = -1; dy <= 1; ++dy) {
				try {
					if (dx != 0 || dy != 0) {

						Character ch = GRID[x + dx][y + dy];
						Integer[] newPosition = new Integer[] { x + dx, y + dy };
//						Integer[] usedPosition = usedPositions.get(ch);
						Coordinate coordinate = new Coordinate(newPosition);
						if (ch.equals(charToFind) && usedCoordinates.contains(coordinate) == false) {
							cords.add(coordinate);
						}
					}
				} catch (IndexOutOfBoundsException e) {
					// System.out.println("Out of bounds");
				}
			}
		}
		return cords;
	}
	
	public static Map<Character, ArrayList<Coordinate>> getAdjacentFreeLetters(int x, int y) {
		Map<Character, ArrayList<Coordinate>> result = new HashMap<Character, ArrayList<Coordinate>>();
		
		for (int dx = -1; dx <= 1; ++dx) {
			for (int dy = -1; dy <= 1; ++dy) {
				try {
					if (dx != 0 || dy != 0) {

						Character ch = GRID[x + dx][y + dy];
						Integer[] newPosition = new Integer[] { x + dx, y + dy };
						
						if (usedPositions.containsKey(ch)) {
							Integer[] usedCoords = usedPositions.get(ch);
							if (Arrays.equals(usedCoords, newPosition) == false) {
								
								if(result.containsKey(ch)) {
									result.get(ch).add(new Coordinate(newPosition));
								} else {
									ArrayList<Coordinate> co = new ArrayList<Coordinate>();
									co.add(new Coordinate(newPosition));
									result.put(ch, co);
								}
								
							} else {
								// System.out.println("This position was already in use "
								// + Arrays.toString(newPosition));
							}

							// result.Add(grid[x + dx][y + dy]);
						} else {
							ArrayList<Coordinate> co = new ArrayList<Coordinate>();
							co.add(new Coordinate(newPosition));
							result.put(ch, co);
						}
					}
				} catch (IndexOutOfBoundsException e) {
					// System.out.println("Out of bounds");
				}
			}
		}
		return result;
	}
	
}


