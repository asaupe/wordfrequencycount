package com.asaupe.wordfrequencycount.util;

import java.util.ArrayList;

public class Stemming {
	public static ArrayList<String> stemWords(ArrayList<StemRule> stemRules, String word) {
		ArrayList<String> stemWordsFound = new ArrayList<String>();
		
		for(StemRule stemRule : stemRules){
			if (word.toLowerCase().endsWith(stemRule.getEnding())) {
				stemWordsFound.add(Stemming.createStemWord(stemRule, word.toLowerCase()));
			}
		}
		
		return stemWordsFound;
	}
	
	private static String createStemWord(StemRule stemRule, String word) {
		int stemLocation = word.lastIndexOf(stemRule.getEnding());
		return word.substring(0, stemLocation).concat(stemRule.getLettersAdded());
	}

}
