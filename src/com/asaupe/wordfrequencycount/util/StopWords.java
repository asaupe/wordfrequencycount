package com.asaupe.wordfrequencycount.util;

import java.util.ArrayList;

public class StopWords {
	
	public static void addStopWord(ArrayList<String> stopWords, String word) throws Exception {
		//TODO: Add Regex verify the stop word contains only letters
		stopWords.add(word.toLowerCase());
	}

	//TODO: Look at replacing with public API call (want one that works with individual words)
	public static boolean IsStopWord(ArrayList<String> stopWords, String word) {
		return stopWords.contains(word.toLowerCase());
	}
}
