package com.games;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;
import java.util.prefs.BackingStoreException;


public class WordFinder {

	private ArrayList<Coordinate> usedCoordinates = new ArrayList<Coordinate>();
	public static String[][] GRID = null;
	public static String[][] gridBackup = null;
	
	//logging tools
	private static boolean VERBOSE = false;
	private static String CURRENT_WORD = "";
	private static ArrayList<String> wordsToLog = new ArrayList<String>();
	static {
		wordsToLog.add("reja");
	}

	private void printLog(String log) {
		if(wordsToLog.contains(CURRENT_WORD)) {
			System.out.println(log);
		}
	}
	
	//Default constructor
	public WordFinder(String lettersGrid, int gridSize) {
		GRID = loadGrid(lettersGrid, gridSize);
		Utils.printGridWithCoords(GRID);
	}
	
	public ArrayList<String> findWord(int letterLength, LANG language, File dictionarySourceFile) {
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
		
		return findWords(dictionarySourceFile);
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
	
	private ArrayList<String> findWords(File dictionarySource) {
		System.out.println("Using dictionary " + dictionarySource);
		BufferedReader reader = null;
		InputStreamReader input = null;
		ArrayList<String> foundWords = new ArrayList<String>();
		ArrayList<Coordinate> initialLetterStartIndex = new ArrayList<Coordinate>();
		try {
			input = new InputStreamReader(new FileInputStream(dictionarySource), "UTF-8");
			reader = new BufferedReader(input);
			String dictionaryWord;
			
			while ((dictionaryWord = reader.readLine()) != null) {
				dictionaryWord = dictionaryWord.trim().toLowerCase().replace(" ", "");

				usedCoordinates.clear();
				CURRENT_WORD = dictionaryWord;
				boolean found = false;
				initialLetterStartIndex = Utils.getIndexesByLetter(dictionaryWord.charAt(0), GRID);
				for (int i = 0; i < initialLetterStartIndex.size() && found == false; i++) {
					printLog(dictionaryWord + " "+ dictionaryWord.charAt(0) + " "+ initialLetterStartIndex.toString());
					Coordinate coord = initialLetterStartIndex.get(i);
					printLog("InitialCoord " + coord);

					usedCoordinates.add(coord);

					printLog("UsedCoordinates " + usedCoordinates.toString());
					found = findWord(dictionaryWord.substring(1), coord, false);
					printLog("outter found " + found);
					if (found) {
						foundWords.add(CURRENT_WORD);
					}
					
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
		return foundWords;
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
		ArrayList<Coordinate> initialLetterStartIndex = Utils.getIndexesByLetter(word.charAt(0), GRID);
		ArrayList<ArrayList<Coordinate>> paths = new ArrayList<ArrayList<Coordinate>>();
		
		boolean found = false;
		for (int i = 0; i < initialLetterStartIndex.size() ; i++) {
			Coordinate coord = initialLetterStartIndex.get(i);
			printLog("InitialCoord " + coord);

			usedCoordinates.add(coord);

			printLog("UsedCoordinates " + usedCoordinates.toString());
			found = findWordPaths(word.substring(1), coord, paths);
			usedCoordinates.remove(coord);
		}
		
		return paths;
	}
	
	public boolean findWordPaths(String word, Coordinate baseCoord, ArrayList<ArrayList<Coordinate>> paths) {
		if(word.isEmpty()) {
//			System.out.println(usedCoordinates);
			paths.add((ArrayList<Coordinate>) usedCoordinates.clone());
			return true;
		}
		
		String nextChar = word.charAt(0) + "";
		
		ArrayList<Coordinate> neighbors = getAdjacentIndexesByLetter(baseCoord.getX_POSITION(), baseCoord.getY_POSITON(), nextChar);
		boolean found = false;
		
		if (neighbors.size() > 1) {
			// it has different paths from here.
			for (int i = 0; i < neighbors.size(); i++) {
				Coordinate current = neighbors.get(i);
				usedCoordinates.add(current);
				found = findWordPaths(word.substring(1, word.length()), current, paths);
				if (found) {
					Coordinate removed = usedCoordinates.remove(usedCoordinates.size() - 1);
				}
			}
		} else if (neighbors.size() == 1) {
			usedCoordinates.add(neighbors.get(0));
			found = findWordPaths(word.substring(1, word.length()), neighbors.get(0), paths);
			if(found) {
				Coordinate removed = usedCoordinates.remove(usedCoordinates.size() - 1);
				return found;
			}
		} else {
			usedCoordinates.remove(usedCoordinates.size() - 1);
			printLog("No adjacent found " + word + " nextChar " + nextChar);
			return false;
		}
		return found;
	}
	
	public void removeCordinatesFromGRID(ArrayList<Coordinate> toMark) {
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

	private static String[][] doGridBackup(String[][] original) {
		String[][] backup = new String[original.length][original.length];
		for(int i = 0; i < original.length; i++) {
			for(int j = 0; j < original.length; j++) {
				backup[i][j] = new String(original[i][j]);
			}
		}
		return backup;
	}
	
	private static void findWords1 (WordFinder wordFinder, String optionalDictionaryPath, LANG lang, int wordLength) {
		try {
			File dictionarySourceFile = getSourceFile(optionalDictionaryPath, lang, wordLength);
			ArrayList<String> words = wordFinder.findWord(wordLength, lang, dictionarySourceFile);
			for(String word : words) {
				System.out.println("Found word " + word);
			}
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
			Utils.printAndMarkCoordsInGrid(GRID, co);
		}
		
		Scanner scan = new Scanner(System.in);
		int selection = scan.nextInt();
		
		ArrayList<Coordinate> coords = paths.get(selection);
		System.out.println("Will remove coords " + coords);
		wordFinder.removeCordinatesFromGRID(coords);
		Utils.printGridWithoutCoords(GRID);
		Utils.printFlatGrid(GRID);
	}
	
	private String[][] cloneGrid(String[][] backup) {
		return WordFinder.doGridBackup(backup);
	}
	
	private static boolean resolveGrid(ArrayList<Integer> wordLengths, WordFinder wordFinder, LANG lang) throws IOException {
		/*
		 * getPossibleWords
		 * 
		 * for(int listPossible words) 
		 *    for(getPathsForPossibleWord paths) 
		 *    	AdjustGridWithWithPath(idx)
		 *    
		 *    Then next word.
		 *    Get paths for next word
		 *    if no paths with this Modified grid
		 *    then goBack to previous word and try another path
		 */
		if(wordLengths.isEmpty()) {
			return true;
		}
		
		boolean resolved = false;
		
		int wordLength = wordLengths.get(0);
		File dictionarySourceFile = getSourceFile(null, lang, wordLength);
		ArrayList<String> possibleWords = wordFinder.findWord(wordLength, lang, dictionarySourceFile);
		System.out.println("Possible words for grid ");
		Utils.printGridWithCoords(GRID);
		System.out.println("possible words " + possibleWords.size());
		
		Iterator<String> it = possibleWords.iterator();
		
		while(it.hasNext()) {
			wordFinder.usedCoordinates.clear();
			String word = it.next();
			System.out.println("looking for paths of word " + word);
			CURRENT_WORD = word;
			ArrayList<ArrayList<Coordinate>> paths = wordFinder.findWordPaths(word);
			System.out.println("word " + word + " paths "  + paths.size()+ " coords " + paths);
			if(paths.size() == 0) {
				//return to see if the past word has another path
				//do that path and then try again with the next word.
				continue;
			}
			String[][] tempBack = null;
			resolved = false;
			
			for(int j = 0; j < paths.size() && resolved == false; j++) {
				ArrayList<Coordinate> pathCords = paths.get(j);

				System.out.println("Creating tempBack");
				tempBack = wordFinder.cloneGrid(GRID);
				Utils.printGridWithCoords(tempBack);
				
				wordFinder.removeCordinatesFromGRID(pathCords);
				
				System.out.println("Current GRID AFTER remove coordinates");
				Utils.printGridWithCoords(GRID);
				int removed = wordLengths.remove(0);
				System.out.println("removed " + removed);
				resolved = resolveGrid(wordLengths, wordFinder, lang);
				if(resolved) {
//					resolvedWords.add(word);  
					System.out.println("PRINT OMG " + word);
					return resolved;
				} else {
					wordLengths.add(0, removed);
					System.out.println("Reseting grid");
					GRID = wordFinder.cloneGrid(tempBack); // wordFinder.resetGrid();
					Utils.printGridWithCoords(gridBackup);
					Utils.printGridWithCoords(GRID);
				}
				System.out.println("Trying another path for " + word);
			}
//			GRID = wordFinder.cloneGrid(tempBack); // wordFinder.resetGrid();
		}
		
//			Scanner scan = new Scanner(System.in);
//			int selection = scan.nextInt();
		
//			ArrayList<Coordinate> coords = paths.get(selection);
//			System.out.println("Will remove coords " + coords);
//			wordFinder.deleteCoordenades(coords);
//			wordFinder.printGridWithoutCoords(GRID);
//			wordFinder.printFlatGrid();
		return resolved;
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
		System.out.println("java -jar WordFinder.jar <command> lettersGrid gridSize <otherCopmmandsDependingOn FirstCommand>" );
		//lettersGrid gridSize wordLength language[e|s] [dictionaryDirPath]\n");
		System.out.println(" findWords needs: lettersGrid gridSize wordLength language optional:FilePath<Not tested in this version>");
		System.out.println(" findPaths needs: lettersGrid gridSize word");
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
		System.out.println("word     The word that will be taken to find ways to create it.");
		System.exit(0);
	}
	
	public static void main(String[] args) throws IOException {
		
		//eoaagohbollocnvailraltfrmbcocditrlao
//		args =  new String[]{"findWords", "alo-psd-run-ofa-", "4", "8", "s"};
		
		/**
		 * 
		 * l--ra--ev--ja--a
		 * 
		 * posible words 3 
		 * word aval paths 0 coords [] 
		 * word lava paths 0 coords []
		 * word reja paths 1 coords [[[2,0], [0,3], [1,3], [2,3], [3,3]]]
		 */
		
//		args =  new String[]{"resolveGrid", "l--ra--ev--ja--a", "4", "4,4", "s"};
//		args =  new String[]{"resolveGrid", "horairalrezllano", "4", "4,6,6", "s"};
		args =  new String[]{"resolveGrid", "pirckathniidnosw", "4", "6,5,5", "e"};
		
		//work in progress
		//4,5
		//boca,mundo
//		args =  new String[]{"resolveGrid", "modunaboc", "3", "4,5", "s"};
		//bate, músculo, ancla
//		args =  new String[]{"resolveGrid", "meaaúotbslanuclc", "4", "4,7,5", "s"};
//		args =  new String[]{"resolveGrid", "learaneevmijahca", "4", "8,4,4", "s"};
		
		
		Integer[] numeros = new Integer[10];
		Integer[] numeros2 = new Integer[]{1,2,3,4};
		
		
		numeros[4] = 10;
		Integer value = numeros[9];
		
		
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
		} else if(command.equals("resolveGrid")) {
			String[] sWordLenghts = args[cIdx++].split(",");
			ArrayList<Integer> wordLenths = new ArrayList<Integer>();
			
			for (int i = 0; i < sWordLenghts.length; i++) {
				try {
					wordLenths.add(Integer.parseInt(sWordLenghts[i]));
				} catch (Exception e) {
					System.out.println("Error parsing passed lengths " + wordLenths.get(i));
				}
			}
			gridBackup =  doGridBackup(GRID);
//			gridBackup = Arrays.copyOf(GRID, GRID.length);
			LANG lang = LANG.getEnum(args[cIdx++]);
			try {
				WordFinder.resolveGrid(wordLenths, wordFinder, lang);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
}


