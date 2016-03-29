package com.games;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class WordFinder {

	public static ArrayList<Coordinate> USED_COORDINATES = new ArrayList<Coordinate>();
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
		System.out.println("java -jar WordFinder.jar lettersGrid wordLength language[e|s] [dictionaryDirPath]\n");
		System.out.println("lettersGrid  Required string of letters where the words are");
		System.out.println("             are to be found.");
		System.out.println("wordLength   Required a number indicating the length of");
		System.out.println("             the word to find." );
		System.out.println("gridSize     Required just a number indicating the size");
		System.out.println("             of the grid, if the grid is of 3x3 just put 3.");
		System.out.println("language     Required [e|s] e: english s: spanish.");
		System.out.println("[dictionaryDirPath  Optional the path to a Dir where to");
		System.out.println("                    find the dictionaries or a path to");
		System.out.println("                    a file to use as dictionary.");
		System.exit(0);
	}
	
	public WordFinder(String lettersGrid, int letterLength, int gridSize, LANG language, File dictionarySourceFile) {
		
		if (dictionarySourceFile.exists() == false) {
			try {
				dictionarySourceFile.createNewFile();

				DictionaryUpdater d = new DictionaryUpdater(letterLength, dictionarySourceFile, language);
				System.out.println("Downloading dictionary "+ dictionarySourceFile);
				d.downloadDictionary();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		GRID = loadGrid(lettersGrid, gridSize);
		findWords(dictionarySourceFile);
	}
	
	private void findWords(File dictionarySource) {
		System.out.println("Using dictionary " + dictionarySource);
		BufferedReader reader = null;
		InputStreamReader input = null;
		ArrayList<Coordinate> initialLetterStartIndex = new ArrayList<Coordinate>();
		try {
			input = new InputStreamReader(new FileInputStream(dictionarySource), "UTF-8");
			reader = new BufferedReader(input);
			String dictionaryWord;

			while ((dictionaryWord = reader.readLine()) != null) {
				dictionaryWord = dictionaryWord.trim().toLowerCase().replace(" ", "");

				USED_COORDINATES.clear();
				currentWord = dictionaryWord;
				boolean found = false;
				initialLetterStartIndex = getIndexesByLetter(dictionaryWord.charAt(0));
				for (int i = 0; i < initialLetterStartIndex.size()
						&& found == false; i++) {
					if (printLog()) {System.out.println(dictionaryWord + " "+ dictionaryWord.charAt(0) + " "+ initialLetterStartIndex.toString());}
					Coordinate coord = initialLetterStartIndex.get(i);
					if (printLog()) {System.out.println("InitialCoord " + coord);}

					USED_COORDINATES.add(coord);

					if (printLog()) {System.out.println("UsedCoordinates " + USED_COORDINATES.toString());}
					found = findWord(dictionaryWord.substring(1), coord, false);
					if (printLog()) { System.out.println("outter found " + found); }
					if (found) { System.out.println(" ********Found word " + currentWord); }
					
					USED_COORDINATES.remove(coord);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e2) {
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
		if(printLog()) {System.out.println("Adjacent Free letters " + nextChar + adjacentLetters.toString());}
		boolean found2 = false;
		for (int i = 0 ; found2 == false && i < adjacentLetters.size(); i++) {
			Coordinate coord = adjacentLetters.get(i);
			USED_COORDINATES.add(coord);
			
			if(printLog()){System.out.println("Adding to used " + nextChar + coord);}
			found2 = findWord(word, coord, true);
			if(found2 == false ) {
				 USED_COORDINATES.remove(coord);
//				 if(printLog()){System.out.println("Removing from used " + nextChar + coord);}
			}
		}
//		if(printLog()) {System.out.println("found2 " + found2);}
		return found2;
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
						if (ch.equals(charToFind) && USED_COORDINATES.contains(coordinate) == false) {
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		args = new String[]{"anoroaeetllkpabj","7","4", "s"};
		if (args == null || args.length < 3) {
			System.err.println("Not enough arguments.");
			printUsage();
		}

		String defaultPath = "C:\\Ulises_codebase";
		try {
			String lettersGrid = args[0];
			
			int wordLength = Integer.parseInt(args[1]);
			int gridSize = Integer.parseInt(args[2]);
			
			LANG lang = LANG.getEnum(args[3]);
			
			String defaultDictionaryPath = String.format("Dictionary%sLetters", wordLength);
			if (lang == LANG.SPANISH) {
				defaultDictionaryPath += "Spanish";
			}
			defaultDictionaryPath += ".txt";
			
			File dictionarySourceFile = null;
			if (args.length == 5) {
				File givenPath = new File(args[4]);
				if (givenPath.isDirectory()) {
					dictionarySourceFile = new File(givenPath, defaultDictionaryPath);
				} else if (givenPath.isFile()) {
					dictionarySourceFile = givenPath;
				} else {
					throw new IOException("Can't find the given file to load the dictionary: " + args[4]);
				}
			} else {
				File dictionaryDir = new File(defaultPath);
				dictionarySourceFile = new File(dictionaryDir,defaultDictionaryPath);
			}

			new WordFinder(lettersGrid, wordLength, gridSize, lang, dictionarySourceFile);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			printUsage();
		}
	}

}

enum LANG {
	SPANISH("s"), ENGLISH("e");
	
	private final String value;
	
	private LANG(String s) {
		this.value = s;
	}
	
	public String value() {
		return this.value;
	}
	
	public static LANG getEnum(String name) {
		for(LANG l : values()) {
			if(l.value().equals(name)) {
				return l;
			}
		}
		return null; 
	}
}




