package com.games;

import java.io.BufferedReader;
import java.io.File;
import java.io.Reader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DictionaryUpdaterEnglish extends DictionaryUpdater {
	Pattern wordCounterRegex = Pattern.compile("([0-9]+\\swords)");
	Pattern wordsGetter = Pattern.compile(">[a-z]+<");
	String ENGLISH_DICT_URL = "http://www.morewords.com/wordsbylength/%s%s/";
	Matcher wordsMatcher = null;
	String line = null;
	BufferedReader reader = null;
	boolean positioned = false;

	public DictionaryUpdaterEnglish(int wordlength, File saveFile) {
		super(wordlength, saveFile);
	}

	@Override
	protected URL getURL(String ch, int wordLength) {
		URL url = null;
		String createdURL = null;
		createdURL = String.format(ENGLISH_DICT_URL, wordLength, ch);
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
			this.wordsMatcher = null;
			this.positioned = false;
			this.line = null;
			this.reader = (BufferedReader) reader;
		} else {
			throw new RuntimeException("Need a bufferedReader ");
		}
	}

	@Override
	protected String nextWord() {
		String word = null;
		if (positioned == false) {
			if((line = putInPosition()) == null) {
				//it means it reached the end of the file and 
				//it didn't find any starting point.
				return null;
			} else {
				positioned = true;
			}
		}

		try {
			if (wordsMatcher == null) {
				wordsMatcher = wordsGetter.matcher(line);
			}
			if (wordsMatcher.find()) {
				word = wordsMatcher.group().replaceAll("[<>]", "");
			} else {
				System.out.println("No word found" + line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return word;
	}

	private String putInPosition() {
		try {
			String line = null;
			while ((line = reader.readLine()) != null) {
				Matcher m = wordCounterRegex.matcher(line);
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
