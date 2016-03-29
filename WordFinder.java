package com.games;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.games.DictionaryUpdater.LANG;

public class WordFinder {

	public static Map<Character, Integer[]> usedPositions = new HashMap<Character, Integer[]>();
	public static ArrayList<Coordinate> usedCoordinates = new ArrayList<Coordinate>();
	public static Character[][] GRID = null;
	
	//logging purposes
	private static String currentWord = "";
	private static ArrayList<String> wordsToLog = new ArrayList<String>();
	static {
//		wordsToLog.add("conejo");
	}

	private static boolean printLog() {
		return wordsToLog.contains(currentWord);
	}
	
	private static void printUsage() {
		System.out.println("java -jar WordFinder.jar gridOfLetters firstWordLength gridSize language:[e|s] optional:DictionaryDirPath");
		System.exit(1);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		args = new String[]{"alo-psd-run-ofa-","8","4", "s"};
		
		if(args == null || args.length < 3) {
			printUsage();
		}
		
		String letters = null;
		int firstWordLength = -1;
		int gridSize = -1;
		String dictionaryDirPath = "C:\\Ulises_codebase";
		File dictionaryDir = null;
		
		File dictionarySourceFile = null;
		String lang = null;
		DictionaryUpdater.LANG language = LANG.ENGLISH;  
		
		try {
			letters = args[0]; // "etnlictsa";
			firstWordLength = Integer.parseInt(args[1]);
			gridSize = Integer.parseInt(args[2]);
			
			lang = args[3];
			language = lang.equals("s") ? LANG.SPANISH : LANG.ENGLISH;
			
			String defaultDictionaryFileName = String.format("Dictionary%sLetters", firstWordLength);
			if(language == LANG.SPANISH) {
				defaultDictionaryFileName += "Spanish";
			}
			defaultDictionaryFileName +=".txt";
			
			if(args.length == 5) {
				File givenPath = new File(args[4]);
				if(givenPath.isDirectory()) {
					dictionarySourceFile = new File(givenPath, defaultDictionaryFileName);
				} else if(givenPath.isFile()) {
					dictionarySourceFile = givenPath;
				}
				
			} else {
				dictionaryDir = new File(dictionaryDirPath);
				dictionarySourceFile = new File(dictionaryDir, defaultDictionaryFileName);
			}
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			printUsage();
		}
		
		ArrayList<String> dictionary = new ArrayList<String>();
		
		if (dictionarySourceFile.exists() == false) {
			try {
				dictionarySourceFile.createNewFile();
				
				DictionaryUpdater d = new DictionaryUpdater(firstWordLength, dictionarySourceFile, language);
				System.out.println("Downloading dictionary " + dictionarySourceFile);
				d.downloadDictionary();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Using dictionary " + dictionarySourceFile);
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
				if(printLog()) {System.out.println(dictionaryWord + " " + dictionaryWord.charAt(0) + " " + initialLetterStartIndex.toString());}
				Coordinate coord = initialLetterStartIndex.get(i);
				if(printLog()){System.out.println("InitialCoord " + coord);}
				
				usedCoordinates.add(coord);
				
				if(printLog()) {System.out.println("UsedCoordinates " + usedCoordinates.toString());}
				found = findWord(dictionaryWord.substring(1), coord, false);
				if(printLog()){ System.out.println("outter found " + found);}
				if(found ) {
					System.out.println(" ********Found word " + currentWord);
				}
				usedCoordinates.remove(coord);
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
		if(printLog()) {System.out.println("Adjacent Free letters " + nextChar + adjacentLetters.toString());}
		boolean found2 = false;
		for (int i = 0 ; found2 == false && i < adjacentLetters.size(); i++) {
			Coordinate coord = adjacentLetters.get(i);
			usedCoordinates.add(coord);
			
			if(printLog()){System.out.println("Adding to used " + nextChar + coord);}
			found2 = findWord(word, coord, true);
			if(found2 == false ) {
				 usedCoordinates.remove(coord);
//				 if(printLog()){System.out.println("Removing from used " + nextChar + coord);}
			}
		}
//		if(printLog()) {System.out.println("found2 " + found2);}
		return found2;
	}
	
	
	
	private static ArrayList<String> loadDictionary(File path) {
		ArrayList<String> a = new ArrayList<String>();
		BufferedReader reader = null;
		InputStreamReader input = null;
		try {
			input = new InputStreamReader(new FileInputStream(path), "UTF-8");
			reader = new BufferedReader(input);
			String line;
			
			while((line = reader.readLine()) != null ) {
				 line = line.trim().toLowerCase().replace(" ", "");
				 a.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if( reader != null) {
				try {
					reader.close();
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


