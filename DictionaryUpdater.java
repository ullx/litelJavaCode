package com.games;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ContentHandler;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DictionaryUpdater {

	Pattern wordCounterRegex = Pattern.compile("([0-9]+\\swords)");
	Pattern wordsGetter = Pattern.compile(">[a-z]+<");
	String abc = "abcdefghijklmnopqrstuvwxyz";
	String SPANISH = "http://www.laspalabras.net/liste_mots_en.php?q=%s&lettres=%s";
	String ENGLISH = "http://www.morewords.com/wordsbylength/%s%s/";
	int WORD_LENGTH = 0;
	File SAVE_FILE = null;
	LANG language = LANG.ENGLISH;
	
	enum LANG {
		SPANISH, ENGLISH;
	}
	
	public static void main(String[] args) {
		 DictionaryUpdater d = new DictionaryUpdater(4, new File("C:\\Ulises_codebase\\Dictionary4LettersSpanish.txt"));
		 d.downloadDictionary();
	}

	public DictionaryUpdater(int wordlength, File saveFile) {
		WORD_LENGTH = wordlength;
		this.SAVE_FILE = saveFile;
	}
	
	public DictionaryUpdater(int wordlength, File saveFile, LANG language) {
		WORD_LENGTH = wordlength;
		this.SAVE_FILE = saveFile;
		this.language = language;
	}

	private URL getURL(char ch, int wordLength) {
		URL url = null;
		String createdURL = String.format(SPANISH,ch, wordLength);
		System.out.println("CreatedURL " + createdURL);
		try {
			url = new URL(createdURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url;
	}
	
	public void downloadDictionary() {
		for(char letter : abc.toCharArray()) {
			URL url = getURL(letter, WORD_LENGTH);
			try {
				switch (language) {
				case SPANISH:
					downloadDictionarySpanish(url);
					break;
				case ENGLISH:
					downloadDictionary(url);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void downloadDictionarySpanish(URL url) throws IOException {
		OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(SAVE_FILE, true), "UTF-8");
		BufferedWriter writer = new BufferedWriter(fileWriter);
		
		try {
			URLConnection conn = url.openConnection();
			InputStream is = conn.getInputStream();
			InputStreamReader inputStream = new InputStreamReader(is, "UTF-8");
			BufferedReader reader = new BufferedReader(inputStream);

			String line;
			while ((line = reader.readLine()) != null) {
				Matcher m = Pattern.compile(String.format("[a-záéíóúàèìòù]{%s},", WORD_LENGTH)).matcher(line);
					while(m.find()) {
						String temp = m.group().replaceAll(",", "");
						temp = temp.replaceAll("à", "á");
						temp = temp.replaceAll("è", "é");
						temp = temp.replaceAll("ì", "í");
						temp = temp.replaceAll("ò", "ó");
						temp = temp.replaceAll("ù", "ú");
						writer.append(temp);
						writer.append("\n");
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			writer.close();
		}
	}
	
	private void downloadDictionary(URL url) throws IOException {
		FileWriter fileWriter = new FileWriter(SAVE_FILE, true);
		BufferedWriter writer = new BufferedWriter(fileWriter);

		try {

			URLConnection conn = url.openConnection();
			InputStream is = conn.getInputStream();
			InputStreamReader inputStream = new InputStreamReader(is);
			BufferedReader reader = new BufferedReader(inputStream);

			String line;
			while ((line = reader.readLine()) != null) {
				Matcher m = wordCounterRegex.matcher(line);

				if (m.find()) {
					System.out.println(m.group());
					m.usePattern(wordsGetter);
					while (m.find()) {
						writer.append(m.group().replaceAll("[<>]", ""));
						writer.append("\n");
					}
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			writer.close();
		}
	}
}
