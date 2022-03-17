package com.games;

import java.io.File;
import java.io.IOException;

public class Dictionary {

    public static File getSourceFile(String dictionaryPath, LANG lang, int wordLength) throws IOException {
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

    private void downloadDictionary(File dictionarySourceFile, LANG language, int wordlength ) {
        if (!dictionarySourceFile.exists()) {
            try {
                if( dictionarySourceFile.createNewFile()) {
                    DictionaryUpdater d = DictionaryUpdater.getDictionaryUpdater(language, wordlength, dictionarySourceFile);
                    System.out.println("Downloading dictionary "+ dictionarySourceFile);
                    d.downloadDictionaryExtraAlphabet(); //downloadDictionary();
                } else {
                    throw new IOException("File not created");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
