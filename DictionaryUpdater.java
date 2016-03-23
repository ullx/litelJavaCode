package com.cert;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
	int WORD_LENGTH = 0;
	File SAVE_FILE = null;
	
	public static void main(String[] args) {
		 DictionaryUpdater d = new DictionaryUpdater(5, new File("C:\\Ulises_codebase\\Dictionary5Letters.txt"));
		 d.downloadDictionary();
	}

	public DictionaryUpdater(int wordlength, File saveFile) {
		WORD_LENGTH = wordlength;
		this.SAVE_FILE = saveFile;
	}

	private URL getURL(char ch, int wordLength) {
		URL url = null;
		String createdURL = String.format("http://www.morewords.com/wordsbylength/%s%s/", wordLength,ch);
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
				downloadDictionary(url);
			} catch (Exception e) {
				e.printStackTrace();
			}
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
