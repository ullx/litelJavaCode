package com.games;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;


public class WordFinder {

	private final ArrayList<Coordinate> usedCoordinates = new ArrayList<Coordinate>();


	private Grid GRID;
	private Grid gridBackup;

	private static CommandInterpreter inputCommands;

	//logging tools
	private static boolean VERBOSE = false;
	private static String CURRENT_WORD = "";
	private static final ArrayList<String> wordsToLog = new ArrayList<String>();
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
		GRID = Grid.createGrid(lettersGrid, gridSize);
		Grid.printGridWithCoords(GRID);
	}


	
	public ArrayList<String> findWord(int letterLength, LANG language, File dictionarySourceFile) {
		if (!dictionarySourceFile.exists()) {
			try {
				if( dictionarySourceFile.createNewFile()) {
					DictionaryUpdater d = DictionaryUpdater.getDictionaryUpdater(language, letterLength, dictionarySourceFile);
					System.out.println("Downloading dictionary "+ dictionarySourceFile);
					d.downloadDictionaryExtraAlphabet(); //downloadDictionary();
				} else {
					throw new IOException("File not created");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return findWords(dictionarySourceFile);
	}

	
	private ArrayList<String> findWords(File dictionarySource) {
		System.out.println("Using dictionary " + dictionarySource);
		BufferedReader reader = null;
		InputStreamReader input = null;
		ArrayList<String> foundWords = new ArrayList<String>();
		ArrayList<Coordinate> initialLetterStartIndex;
		try {
			input = new InputStreamReader(new FileInputStream(dictionarySource), StandardCharsets.UTF_8);
			reader = new BufferedReader(input);
			String dictionaryWord;
			
			while ((dictionaryWord = reader.readLine()) != null) {
				dictionaryWord = dictionaryWord.trim().toLowerCase().replace(" ", "");

				usedCoordinates.clear();
				CURRENT_WORD = dictionaryWord;
				boolean found = false;
				initialLetterStartIndex = GRID.getIndexesByLetter(dictionaryWord.charAt(0));
				for (int i = 0; i < initialLetterStartIndex.size() && !found; i++) {
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
		
		ArrayList<Coordinate> adjacentLetters = GRID.getAdjacentIndexesByLetter(baseCoor.getX_POSITION(), baseCoor.getY_POSITON(), nextChar);
		printLog("Adjacent Free letters " + nextChar + adjacentLetters.toString());
		boolean found2 = false;
		for (int i = 0; !found2 && i < adjacentLetters.size(); i++) {
			Coordinate coord = adjacentLetters.get(i);
			usedCoordinates.add(coord);
			
			printLog("Adding to used " + nextChar + coord);
			found2 = findWord(word, coord, true);
			if(!found2) {
				 usedCoordinates.remove(coord);
				 if(VERBOSE){printLog("Removing from used " + nextChar + coord);}
			}
		}
		if(VERBOSE) {printLog("found2 " + found2);}
		return found2;
	}

	public ArrayList<ArrayList<Coordinate>> findWordPaths(String word) {
		ArrayList<Coordinate> initialLetterStartIndex = GRID.getIndexesByLetter(word.charAt(0));
		ArrayList<ArrayList<Coordinate>> paths = new ArrayList<ArrayList<Coordinate>>();
		
		boolean found = false;
		for (Coordinate coord : initialLetterStartIndex) {
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
		
		ArrayList<Coordinate> neighbors = GRID.getAdjacentIndexesByLetter(baseCoord.getX_POSITION(), baseCoord.getY_POSITON(), nextChar);
		boolean found = false;
		
		if (neighbors.size() > 1) {
			// it has different paths from here.
			for (Coordinate current : neighbors) {
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
	

	private void findWords1 ( String optionalDictionaryPath, LANG lang, int wordLength) {
		try {
			File dictionarySourceFile = getSourceFile(optionalDictionaryPath, lang, wordLength);
			ArrayList<String> words = findWord(wordLength, lang, dictionarySourceFile);
			for(String word : words) {
				System.out.println("Found word " + word);
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			printUsage();
		}
	}
	
	private void findPaths1 (String word ) {
		
		ArrayList<ArrayList<Coordinate>> paths = findWordPaths(word);
		System.out.println();
		for(int i = 0; i < paths.size(); i++) {
			ArrayList<Coordinate> co = paths.get(i);
			System.out.println("Num " + i);
			Grid.printAndMarkCoordsInGrid(co, GRID);
		}
		
		Scanner scan = new Scanner(System.in);
		int selection = scan.nextInt();
		
		ArrayList<Coordinate> coords = paths.get(selection);
		System.out.println("Will remove coords " + coords);
		GRID.removeCordinatesFromGRID(coords);
		Grid.printGridWithoutCoords(GRID);
		Grid.printFlatGrid(GRID);
	}
	
//	private String[][] cloneGrid(String[][] backup) {
//		return doGridBackup(backup);
//	}
	
	private boolean resolveGrid(ArrayList<Integer> wordLengths, LANG lang) throws IOException, CloneNotSupportedException {
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
		File dictionarySourceFile =  new File("C:\\Users\\ulise\\Projects\\litelJavaCode\\src\\resource\\esp-dic.txt"); //getSourceFile(null, lang, wordLength);
		ArrayList<String> possibleWords = findWord(wordLength, lang, dictionarySourceFile);
		System.out.println("Possible words for grid ");
		Grid.printGridWithCoords(GRID);
		System.out.println("possible words " + possibleWords.size());

		for (String possibleWord : possibleWords) {
			usedCoordinates.clear();
			String word = possibleWord;
			System.out.println("looking for paths of word " + word);
			CURRENT_WORD = word;
			ArrayList<ArrayList<Coordinate>> paths = findWordPaths(word);
			System.out.println("word " + word + " paths " + paths.size() + " coords " + paths);
			if (paths.size() == 0) {
				//return to see if the past word has another path
				//do that path and then try again with the next word.
				continue;
			}
			Grid tempBack = null;
			resolved = false;

			for (int j = 0; j < paths.size() && !resolved; j++) {
				ArrayList<Coordinate> pathCords = paths.get(j);

				System.out.println("Creating tempBack");
				tempBack = GRID.cloneGrid();
				Grid.printGridWithCoords(tempBack);

				GRID.removeCordinatesFromGRID(pathCords);

				System.out.println("Current GRID AFTER remove coordinates");
				Grid.printGridWithCoords(GRID);
				int removed = wordLengths.remove(0);
				System.out.println("removed " + removed);
				resolved = resolveGrid(wordLengths, lang);
				if (resolved) {
//					resolvedWords.add(word);  
					System.out.println("PRINT OMG " + word);
					return resolved;
				} else {
					wordLengths.add(0, removed);
					System.out.println("Reseting grid");
					GRID = tempBack.cloneGrid(); // wordFinder.resetGrid();
					Grid.printGridWithCoords(gridBackup);
					Grid.printGridWithCoords(GRID);
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
		String defaultPath = "esp-dic.txt";
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
		System.out.println("java -jar WordFinder.jar <command> lettersGrid gridSize <otherCommandsDependingOn FirstCommand>" );
		//lettersGrid gridSize wordLength language[e|s] [dictionaryDirPath]\n");
		System.out.println(" <findWords> needs: [lettersGrid] [gridSize] [wordLength] [language] optional:FilePath<Not tested in this version>");
		System.out.println(" <findPaths> needs: [lettersGrid] [gridSize] [word]");
		System.out.println("lettersGrid:  Required string of letters where the words are");
		System.out.println("             are to be found.");
		System.out.println("wordLength:   Required a number indicating the length of");
		System.out.println("             the word to find." );
		System.out.println("gridSize:     Required just a number indicating the size");
		System.out.println("             of the grid, if the grid is of 3x3 just put 3.");
		System.out.println("language:     Required [e|s] e: english s: spanish.");
		System.out.println("[dictionaryDirPath]  Optional the path to a Dir where to");
		System.out.println("                    find the dictionaries or a path to");
		System.out.println("                    a file to use as dictionary.");
		System.out.println("word:    The word that will be taken to find ways to create it.");
		System.exit(0);
	}



	public static void main(String[] args) throws IOException, CloneNotSupportedException {

		//eoaagohbollocnvailraltfrmbcocditrlao
		args = new String[]{"findWords", "imaeracde", "3", "4", "s", "C:\\Users\\ulise\\Projects\\litelJavaCode\\src\\resource\\esp-dic.txt"};

		/**
		 *
		 * l--ra--ev--ja--a
		 *
		 * posible words 3
		 * word aval paths 0 coords []
		 * word lava paths 0 coords []
		 * word reja paths 1 coords [[[2,0], [0,3], [1,3], [2,3], [3,3]]]
		 */

//		args =  new String[]{"resolveGrid", "l--ra--ev--ja--a", "4", "4,4", "s","esp-dic.txt"};
//		args =  new String[]{"resolveGrid", "horairalrezllano", "4", "4,6,6", "s"};
		//args =  new String[]{"resolveGrid", "pirckathniidnosw", "4", "6,5,5", "e"};

		//work in progress
		//4,5
		//boca,mundo
//		args =  new String[]{"resolveGrid", "modunaboc", "3", "4,5", "s"};
		//bate, músculo, ancla
//		args =  new String[]{"resolveGrid", "meaaúotbslanuclc", "4", "4,7,5", "s"};
//		args =  new String[]{"resolveGrid", "learaneevmijahca", "4", "8,4,4", "s"};

//		args =  new String[]{"resolveGrid", "learaneevmijahca", "4", "8,4,4", "s"};


		try {
			inputCommands = CommandInterpreter.interpretCommands(args);
		} catch (WrongCommandsException e) {
			e.printStackTrace();
			printUsage();
		}

//		WordFinder wordFinder = new WordFinder(inputCommands.getLettersGrid(), inputCommands.getGridSize());
//		wordFinder.run();
	}

	public void run() throws CloneNotSupportedException {

		if(inputCommands.command == CommandInterpreter.Command.FIND_WORDS) {
			int wordLength = Integer.parseInt(inputCommands.getWordLength());
			System.out.println("wordLength = " + wordLength);
			String optionalDictionaryPath = null;
			LANG lang = LANG.getEnum(inputCommands.getLang());
			System.out.println("lang = " + lang);

			optionalDictionaryPath = inputCommands.getDicPath();

			findWords1(optionalDictionaryPath, lang, wordLength);
		} else if(CommandInterpreter.Command.FIND_PATHS ==  inputCommands.command) {
			String word = inputCommands.getWord();
			findPaths1(word);
		} else if(CommandInterpreter.Command.RESOLVE_GRID ==  inputCommands.command) {
			String[] sWordLengths =  inputCommands.getWordLength().split(",");
			ArrayList<Integer> wordLengths = new ArrayList<Integer>();

			for (int i = 0; i < sWordLengths.length; i++) {
				try {
					wordLengths.add(Integer.parseInt(sWordLengths[i]));
				} catch (Exception e) {
					System.out.println("Error parsing passed lengths " + wordLengths.get(i));
				}
			}
			gridBackup =  GRID.cloneGrid(); // doGridBackup(GRID);
//			gridBackup = Arrays.copyOf(GRID, GRID.length);
			LANG lang = LANG.getEnum(inputCommands.getLang());
			try {
				resolveGrid(wordLengths, lang);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}


