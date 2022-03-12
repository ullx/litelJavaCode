package com.games;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DictionaryUpdaterSpanish2 extends DictionaryUpdater {
	String SPANISH_DICT_URL = "http://www.palabrasque.com/buscador.php?i=%s&f=&tv=4&button=Buscar+palabras&ms=&mns=&m=&mn=&fs=0&fnl=%s&fa=0&d=0";
	BufferedReader reader = null;
//	int WORD_LENGTH = 0;
	String currentLine = null;
	Matcher wordMatcher = null;
	String wordCounterRegex = "Palabras que tienen %s letras";
	boolean positioned = false;
	
	public DictionaryUpdaterSpanish2(int wordlength, File saveFile) {
		super(wordlength, saveFile);
	}

	@Override
	protected URL getURL(String ch, int wordLength) {
		URL url = null;
		String createdURL = null;
		createdURL = String.format(SPANISH_DICT_URL, ch, wordLength);
		try {
			url = new URL(createdURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url;
	}

	@Override
	protected void initializeReader(Reader reader) {
		if (reader instanceof BufferedReader) {
			this.wordMatcher = null;
			this.positioned = false;
			this.currentLine = null;
			this.reader = (BufferedReader) reader;
		} else {
			throw new RuntimeException("Need a bufferedReader ");
		}
	}
	
	@Override
	protected String nextWord() {
		if (positioned == false) {
			if((currentLine = putInPosition()) == null) {
				//it means it reached the end of the file and 
				//it didn't find any starting point.
				return null;
			} else {
				positioned = true;
			}
		}
		
		String word = null;
		Pattern pat = Pattern.compile(String.format("\">[a-záéíóúàèìòù]{%s}<",
				WORD_LENGTH));
//		System.out.println("getting words");
		if (wordMatcher == null) {
			wordMatcher = pat.matcher(currentLine);
		}
		if (wordMatcher.find()) {
			word = wordMatcher.group().replaceAll("[\"><]", "");
		} else {
//			System.out.println("No word found" + currentLine);
		}
		return word;
	}

	
	private String putInPosition() {
		wordCounterRegex = String.format(wordCounterRegex, WORD_LENGTH);
		Pattern wordCountPattern = Pattern.compile(wordCounterRegex);
		try {
			String line = null;
			while ((line = reader.readLine()) != null) {
				Matcher m = wordCountPattern.matcher(line);
				if (m.find()) {
					System.out.println(m.group());
					return line;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
