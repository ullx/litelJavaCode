package com.games;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class WordFinder {

	private ArrayList<Coordinate> usedCoordinates = new ArrayList<Coordinate>();
	public ArrayList<ArrayList<Coordinate>> paths = new ArrayList<ArrayList<Coordinate>>();
	public static String[][] GRID = null;
	
	//logging purposes
	private static boolean VERBOSE = false;
	private static String CURRENT_WORD = "";
	private static ArrayList<String> wordsToLog = new ArrayList<String>();
	static {
//		wordsToLog.add("lápiz");
	}

	private void printLog(String log) {
		if(wordsToLog.contains(CURRENT_WORD)) {
			System.out.println(log);
		}
	}
	
	//Default constructor
	public WordFinder(String lettersGrid, int gridSize) {
		GRID = loadGrid(lettersGrid, gridSize);
		printGridWithCoords(GRID);
	}
	
	public void findWord(int letterLength, LANG language, File dictionarySourceFile) {
		if (dictionarySourceFile.exists() == false) {
			try {
				dictionarySourceFile.createNewFile();

				DictionaryUpdater d = DictionaryUpdater.getDictionaryUpdater(language, letterLength, dictionarySourceFile);
				System.out.println("Downloading dictionary "+ dictionarySourceFile);
				d.downloadDictionaryExtraAlphabet(); //downloadDictionary();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		findWords(dictionarySourceFile);
	}
	
	private String[][] loadGrid(String letters, int size) {
		String[][] grid = new String[size][size];
		int charIdx = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				String value  =  letters.charAt(charIdx++) + ""; 
				grid[i][j] = value;
			}
		}
		return grid;
	}
	
	
	public void printGrid(String[][] grid, boolean withCoords, ArrayList<Coordinate> markCoords) {
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
	
	public void printGridWithCoords(String[][] grid) {
		printGrid(grid, true, null);
	}
	
	public void printGridWithoutCoords(String[][] grid) {
		printGrid(grid, false, null);
	}
	
	public void printAndMarkCoordsInGrid(String[][] grid, ArrayList<Coordinate> toRemove) {
		printGrid(grid, true, toRemove);
	}
	
	private boolean lookCoordinate(ArrayList<Coordinate> list, Coordinate coord) {
		Iterator<Coordinate> it = list.iterator();
		while(it.hasNext()) {
			Coordinate co = it.next();
			if(co.equals(coord)){
				return true;
			}
		}
		return false;
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
				if(dictionaryWord.startsWith("l")) {
//					System.out.println("----------- " + dictionaryWord);
				}
				dictionaryWord = dictionaryWord.trim().toLowerCase().replace(" ", "");

				usedCoordinates.clear();
				CURRENT_WORD = dictionaryWord;
				boolean found = false;
				initialLetterStartIndex = getIndexesByLetter(dictionaryWord.charAt(0));
				for (int i = 0; i < initialLetterStartIndex.size() && found == false; i++) {
					printLog(dictionaryWord + " "+ dictionaryWord.charAt(0) + " "+ initialLetterStartIndex.toString());
					Coordinate coord = initialLetterStartIndex.get(i);
					printLog("InitialCoord " + coord);

					usedCoordinates.add(coord);

					printLog("UsedCoordinates " + usedCoordinates.toString());
					found = findWord(dictionaryWord.substring(1), coord, false);
					printLog("outter found " + found);
					if (found) { System.out.println(" ********Found word " + CURRENT_WORD); }
					
					usedCoordinates.remove(coord);
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
	
	private boolean findWord(String word, Coordinate baseCoor, boolean found) {
		
		if(word.isEmpty()) {
			return true;
		}
		
		String nextChar = word.charAt(0) + ""; 
		word = word.substring(1);
		
		ArrayList<Coordinate> adjacentLetters = getAdjacentIndexesByLetter(baseCoor.getX_POSITION(), baseCoor.getY_POSITON(), nextChar);
		printLog("Adjacent Free letters " + nextChar + adjacentLetters.toString());
		boolean found2 = false;
		for (int i = 0 ; found2 == false && i < adjacentLetters.size(); i++) {
			Coordinate coord = adjacentLetters.get(i);
			usedCoordinates.add(coord);
			
			printLog("Adding to used " + nextChar + coord);
			found2 = findWord(word, coord, true);
			if(found2 == false ) {
				 usedCoordinates.remove(coord);
				 if(VERBOSE){printLog("Removing from used " + nextChar + coord);}
			}
		}
		if(VERBOSE) {printLog("found2 " + found2);}
		return found2;
	}

	public ArrayList<ArrayList<Coordinate>> findWordPaths(String word) {
		ArrayList<Coordinate> initialLetterStartIndex = getIndexesByLetter(word.charAt(0));
		boolean found = false;
		for (int i = 0; i < initialLetterStartIndex.size() ; i++) {
			Coordinate coord = initialLetterStartIndex.get(i);
			printLog("InitialCoord " + coord);

			usedCoordinates.add(coord);

			printLog("UsedCoordinates " + usedCoordinates.toString());
			found = findWordPaths(word.substring(1), coord);
			usedCoordinates.remove(coord);
		}
		
		return paths;
	}
	
	public boolean findWordPaths(String word, Coordinate baseCoord) {
		/*
	 hombro
caororbrnómatloh
c[0,0]a[0,1]o[0,2]r[0,3]
o[1,0]r[1,1]b[1,2]r[1,3]
n[2,0]ó[2,1]m[2,2]a[2,3]
t[3,0]l[3,1]o[3,2]h[3,3]
pahts:
  h[3,3], o[3,2], m[2,2], b[1,2], r[1,3], o[0,2] 
  h[3,3], o[3,2], m[2,2], b[1,2], r[0,3], o[0,2]
  h[3,3], o[3,2], m[2,2], b[1,2], r[1,1], o[0,2]
		 */
		if(word.isEmpty()) {
			System.out.println(usedCoordinates);
			paths.add((ArrayList<Coordinate>)usedCoordinates.clone());
			return true;
		}
		
		String nextChar = word.charAt(0) + "";
		
		ArrayList<Coordinate> adjacents = getAdjacentIndexesByLetter(baseCoord.getX_POSITION(), baseCoord.getY_POSITON(), nextChar);
//		System.out.println("Neighbors " + adjacents);
		boolean found = false;
		
		if (adjacents.size() > 1) {
			// it has different paths from here.
			for (int i = 0; i < adjacents.size(); i++) {
				Coordinate current = adjacents.get(i);
				usedCoordinates.add(current);
//				System.out.println("Not YET Found 1 " + i + " Current "+ current + " Used: " + usedCoordinates);
				found = findWordPaths(word.substring(1, word.length()), current);
				if (found) {
//					System.out.println("Found1 word: " + word +   " baseCoord " + baseCoord + " usedCoordinates.size() " + usedCoordinates.size() + " Used: " + usedCoordinates);
					Coordinate removed = usedCoordinates.remove(usedCoordinates.size() - 1);
//					System.out.println(removed);
				}
			}
		} else if (adjacents.size() == 1) {
			usedCoordinates.add(adjacents.get(0));
//			System.out.println("Not YET Found 2 " + "Current "+ adjacents.get(0) + " Used: " + usedCoordinates);
			found = findWordPaths(word.substring(1, word.length()), adjacents.get(0));
			if(found) {
//				System.out.println("Found2 word: " + word +   " baseCoord " + baseCoord + " usedCoordinates.size() " + usedCoordinates.size() + " Used: " + usedCoordinates);
				Coordinate removed = usedCoordinates.remove(usedCoordinates.size() - 1);
//				System.out.println(removed);
				return found;
			}
		} else {
			usedCoordinates.remove(usedCoordinates.size() - 1);
			printLog("No adjacent found " + word + " nextChar " + nextChar);
			return false;
		}
		
//		System.out.println("Finished");
//		usedCoordinates.clear();
		return found;
	}
	
	public void deleteCoordenades(ArrayList<Coordinate> toMark) {
		Iterator<Coordinate> it = toMark.iterator();
		while(it.hasNext()) {
			Coordinate c = it.next();
			GRID[c.getX_POSITION()][c.getY_POSITON()] = "-";
		}
		setDownCoords();
	}
	
	private void setDownCoords() {
		for (int i = 0; i < GRID.length; i++) {
			for(int j = 0; j < GRID.length; j++) {
				Coordinate co = new Coordinate(i, j);
				setDownCoords(co);
			}
		}
	}
	
	private void setDownCoords(Coordinate baseCoord) {
//		System.out.println("setDownCoords " + baseCoord + " " + baseCoord.getX_POSITION() + " " + GRID[baseCoord.getX_POSITION()][baseCoord.getY_POSITON()]);
		Coordinate above = getAboveNeighbour(baseCoord);
		if(above == null) {
			return ;
		} else {
			String charToMoveDown = GRID[above.getX_POSITION()][above.getY_POSITON()];
			String currentChar = GRID[baseCoord.getX_POSITION()][baseCoord.getY_POSITON()];
			
			if(currentChar.equals("-")) {
				GRID[baseCoord.getX_POSITION()][baseCoord.getY_POSITON()] = charToMoveDown;
				GRID[above.getX_POSITION()][above.getY_POSITON()] = "-";	
			}
			setDownCoords(above);
		}
	}
	
	public Coordinate getAboveNeighbour(Coordinate coor) {
		int baseX = coor.getX_POSITION();
		int baseY = coor.getY_POSITON();
		Coordinate aboveCoord = null;
		for(int i = 0; i < GRID.length && aboveCoord == null; i++) {
			for (int j = 0; j < GRID.length && aboveCoord == null; j++) {
				try {
					if(i == baseX - 1 && j == baseY) {
						aboveCoord =  new Coordinate(i,j);
					}
				} catch (IndexOutOfBoundsException e) {
//					e.printStackTrace();
				}
			}
		}
		return aboveCoord;
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
	
	public ArrayList<Coordinate> getIndexesByLetter(Character ch) {
		printLog("Getting indexes by letter " + ch);
		ArrayList<Coordinate> indexes = new ArrayList<Coordinate>();
		for (int idx = 0; idx < GRID.length; idx++) {
			for (int idy = 0; idy < GRID[idx].length; idy++) {
				if (GRID[idx][idy].equals(ch + "")) {
					indexes.add(new Coordinate(idx, idy));
				}
			}
		}
		return indexes;
	}
	
	public ArrayList<Coordinate> getAdjacentIndexesByLetter(int x, int y, String charToFind) {
		ArrayList<Coordinate> cords = new ArrayList<Coordinate>();
		
		for (int dx = -1; dx <= 1; ++dx) {
			for (int dy = -1; dy <= 1; ++dy) {
				try {
					if (dx != 0 || dy != 0) {
						String ch = GRID[x + dx][y + dy];
						Integer[] newPosition = new Integer[] { x + dx, y + dy };
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

	public void printFlatGrid() {
		StringBuilder sb = new StringBuilder();
		for(int i =0; i < GRID.length; i ++) {
			for(int j =0; j < GRID.length; j ++) {
				sb.append(GRID[i][j]);
			}
		}
		System.out.println(sb);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//eoaagohbollocnvailraltfrmbcocditrlao
//		args =  new String[]{"findWords", "ielodmanu", "3", "5", "s"};
		args =  new String[]{"findPaths", "ielodmanu", "3", "medio"};
		
		int cIdx = 0;
		if (args == null || args.length < 3) {
			System.err.println("Not enough arguments.");
			printUsage();
		}

		String command = args[cIdx++];
		String lettersGrid = args[cIdx++];
		int gridSize = Integer.parseInt(args[cIdx++]);
		System.out.println("lettersGrid " + lettersGrid);
		System.out.println("gridSize " + gridSize);
		
		WordFinder wordFinder = new WordFinder(lettersGrid, gridSize);
		if(command.equalsIgnoreCase("findWords")) {
			int wordLength = Integer.parseInt(args[cIdx++]);
			String optionalDictionaryPath = null;
			LANG lang = LANG.getEnum(args[cIdx++]);
			
			if(args.length >= 6) {
				optionalDictionaryPath = args[cIdx++];
			}
			
			
			findWords1(wordFinder, optionalDictionaryPath, lang, wordLength);
		} else if(command.equalsIgnoreCase("findPaths")) {
			String word = args[cIdx++];
			findPaths1(word, wordFinder);
		}
	}
	
	private static void findWords1 (WordFinder wordFinder, String optionalDictionaryPath, LANG lang, int wordLength) {
		try {
			File dictionarySourceFile = getSourceFile(optionalDictionaryPath, lang, wordLength);
			wordFinder.findWord(wordLength, lang, dictionarySourceFile);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			printUsage();
		}
	}
	
	private static void findPaths1 (String word , WordFinder wordFinder) {
		
		ArrayList<ArrayList<Coordinate>> paths = wordFinder.findWordPaths(word);
		System.out.println();
		for(int i = 0; i < paths.size(); i++) {
			ArrayList<Coordinate> co = paths.get(i);
			System.out.println("Num " + i);
			wordFinder.printAndMarkCoordsInGrid(GRID, co);
		}
		
		Scanner scan = new Scanner(System.in);
		int selection = scan.nextInt();
		
		ArrayList<Coordinate> coords = paths.get(selection);
		System.out.println("Will remove coords " + coords);
		wordFinder.deleteCoordenades(coords);
		wordFinder.printGridWithoutCoords(GRID);
		wordFinder.printFlatGrid();
	}
	
	private static File getSourceFile(String dictionaryPath, LANG lang, int wordLength) throws IOException {
		String defaultPath = "C:\\Ulises_codebase";
		String defaultDictionaryPath = String.format("Dictionary%sLetters", wordLength);
		if (lang == LANG.SPANISH) {
			defaultDictionaryPath += "Spanish";
		}
		defaultDictionaryPath += ".txt";
		File dictionarySourceFile = null;
		if (dictionaryPath != null) {
			File givenPath = new File(dictionaryPath);
			if (givenPath.isDirectory()) {
				dictionarySourceFile = new File(givenPath, defaultDictionaryPath);
			} else if (givenPath.isFile()) {
				dictionarySourceFile = givenPath;
			} else {
				throw new IOException("Can't find the given file to load the dictionary: " + dictionaryPath);
			}
		} else {
			File dictionaryDir = new File(defaultPath);
			dictionarySourceFile = new File(dictionaryDir,defaultDictionaryPath);
		}
		
		return dictionarySourceFile;
	}

	private static void printUsage() {
		System.out.println("java -jar WordFinder.jar <command findWords|findPaths> lettersGrid gridSize <otherCopmmandsDependingOn FirstCommand>" );
		//lettersGrid gridSize wordLength language[e|s] [dictionaryDirPath]\n");
		System.out.println();
		System.out.println("lettersGrid  Required string of letters where the words are");
		System.out.println("             are to be found.");
		System.out.println("wordLength   Required a number indicating the length of");
		System.out.println("             the word to find." );
		System.out.println("gridSize     Required just a number indicating the size");
		System.out.println("             of the grid, if the grid is of 3x3 just put 3.");
		System.out.println("language     Required [e|s] e: english s: spanish.");
		System.out.println("[dictionaryDirPath]  Optional the path to a Dir where to");
		System.out.println("                    find the dictionaries or a path to");
		System.out.println("                    a file to use as dictionary.");
		System.exit(0);
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




