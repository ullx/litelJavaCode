package com.games;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DictionaryUpdaterSpanish extends DictionaryUpdater {
	String SPANISH_DICT_URL = "http://www.laspalabras.net/liste_mots_en.php?q=%s&lettres=%s";
	Reader reader = null;
//	int WORD_LENGTH = 0;
	
	public DictionaryUpdaterSpanish(int wordlength, File saveFile) {
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
		this.reader = reader;
	}
	
	@Override
	protected String nextWord() {
		String temp = null;
		BufferedReader br;
		if(reader instanceof BufferedReader) {
			br = (BufferedReader) reader;
		} else {
			return null;
		}
		
		try {
			String line = null;
			while((line = br.readLine()) != null) {
				Matcher wordMatcher =Pattern.compile(String.format("[a-záéíóúàèìòù]{%s},", WORD_LENGTH)).matcher(line);
				if(wordMatcher.find()) {
					temp = wordMatcher.group().replaceAll(",", "");
					temp = temp.replaceAll("à", "á");
					temp = temp.replaceAll("è", "é");
					temp = temp.replaceAll("ì", "í");
					temp = temp.replaceAll("ò", "ó");
					temp = temp.replaceAll("ù", "ú");
					return temp;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return temp;
	}
	
}
