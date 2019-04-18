package com.asaupe.wordfrequencycount.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class StopWords {
	
	public static void addStopWord(ArrayList<String> stopWords, String word) throws Exception {
		//TODO: Add Regex verify the stop word contains only letters
		stopWords.add(word.toLowerCase());
	}

	//TODO: Look at replacing with public API call (want one that works with individual words)
	public static boolean IsStopWord(ArrayList<String> stopWords, String word) {
		return stopWords.contains(word.toLowerCase());
	}
	
	public static ArrayList<String> loadStopWords(String filePath) {
    	final Charset ENCODING = StandardCharsets.UTF_8;
    	ArrayList<String> stopWords = new ArrayList<String>();
        Path path = Paths.get(filePath);
        try (Scanner scanner =  new Scanner(path, ENCODING.name())){
	    	while (scanner.hasNextLine()){
	    		String line = scanner.nextLine();
	    		//Lines starting with # are comment lines
	    		if (line.trim().startsWith("#")) continue;
	    		stopWords.add(line);
	    	}   
        } catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
        return stopWords;
	}
}
