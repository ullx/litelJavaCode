package com.games;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public abstract class DictionaryUpdater {
	
	private String alphabet = "abcdefghijklmnopqrstuvwxyz";
	protected int WORD_LENGTH = 0;
	private File SAVE_FILE = null;
	
	protected DictionaryUpdater(int wordlength, File saveFile) {
		this.WORD_LENGTH = wordlength;
		this.SAVE_FILE = saveFile;
	}
	
	public static DictionaryUpdater getDictionaryUpdater(LANG language, int wordlength, File saveFile) {
		DictionaryUpdater updater = null; 
		if (language == LANG.SPANISH) {
			updater = new DictionaryUpdaterSpanish2(wordlength, saveFile);
		} else {
			updater = new DictionaryUpdaterEnglish(wordlength, saveFile);
		}
		
		return updater;
	}

	protected abstract URL getURL(String composedChar, int wordLength);
	protected abstract String nextWord();
	protected abstract void initializeReader(Reader reader);
	
	public void downloadDictionary() {
		FileWriter fileWriter = null;
		BufferedWriter writer = null;
		try {
			URL url = null;
			
			fileWriter = new FileWriter(SAVE_FILE, true);
			writer = new BufferedWriter(fileWriter);
			for (char letter : alphabet.toCharArray()) {
				url = getURL(letter + "", WORD_LENGTH);
				System.out.println(url);
				URLConnection conn = url.openConnection();
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				
				initializeReader(reader);
				
				String word;
				while ((word = nextWord()) != null) {
					writer.append(word);
					writer.append("\n");
				} 
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeWriterQuietly(writer);
		}
	}

	public void downloadDictionaryExtraAlphabet() {
		OutputStreamWriter writer = null;
		HttpURLConnection conn = null;
		try {
			URL url = null;
			
			writer = new OutputStreamWriter(new FileOutputStream(SAVE_FILE, true), "UTF-8");
			int count = 0;
			for (char letter : alphabet.toCharArray()) {
				for (char letter2 : alphabet.toCharArray()) {
					String composedChar = letter + "" + letter2;
					url = getURL(composedChar, WORD_LENGTH);
					System.out.println(url);
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.103 Safari/537.36");
					conn.setRequestMethod("GET");
					
					conn.connect();
					
					BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
					
					initializeReader(reader);
					String word;
					while ((word = nextWord()) != null) {
//						System.out.println(word);
						writer.append(word);
						writer.append("\n");
					}
				}
			}
		} catch (IOException e) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			String line = null;
			try {
				while((line = reader.readLine()) != null){
					System.out.println(line);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			System.out.println("finalizing");
			closeWriterQuietly(writer);
		}
	}
	
	private void closeWriterQuietly(Closeable str) {
		try {
			str.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		String as = "<\"asd>";
		System.out.println(as.replaceAll("[\"><]", ""));
		System.exit(0);
		
		CookieManager man  = new CookieManager();
		man.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		CookieHandler.setDefault(man);
		File file = new File("C:\\Ulises_codebase\\Dictionary4LettersSPANISHTEST.txt");
		 DictionaryUpdater d =  DictionaryUpdater.getDictionaryUpdater(LANG.SPANISH, 4, file); 
		 d.downloadDictionaryExtraAlphabet();
		
//		testConnection();
	}
	
	
	private static void testConnection() {
		HttpURLConnection conn = null;
		try {
			URL url = new URL(
					"http://www.palabrasque.com/buscador.php?i=ab&f=&tv=4&button=Buscar+palabras&ms=&mns=&m=&mn=&fs=0&fnl=6&fa=0&d=0");
			System.out.println(url);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
			conn.connect();

			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String word;
			while ((word = reader.readLine()) != null) {
				System.out.println(word);
			}
		} catch (IOException e) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			String line = null;
			
			try {
				System.out.println(conn.getResponseCode());
				System.out.println(conn.getResponseMessage());
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
