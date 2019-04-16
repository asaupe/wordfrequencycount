package com.asaupe.wordfrequencycount.file;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Scanner;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.asaupe.wordfrequencycount.util.StemRule;
import com.asaupe.wordfrequencycount.util.Stemming;
import com.asaupe.wordfrequencycount.util.StopWords;

public class FileProcessor {
	
	//TODO: Maybe overloading this to much think about splitting for stem words and actual words
	public static void addNewWordCount(Hashtable<String, WordCount> wordCounts, ArrayList<String> stopWords, ArrayList<StemRule> stemRules, String word, String originalWord) {
		WordCount wordCount = new WordCount();
		wordCount.setWord(word);
		wordCount.setStopWord(StopWords.IsStopWord(stopWords, wordCount.getWord()));
		//TODO: Think about if stem word is also stop word (weird edge case).
		if (!wordCount.isStopWord()) {
			wordCount.setStemWords(Stemming.stemWords(stemRules, word));
		}
		if (originalWord == null) {
			wordCount.addActualCount();
		} else {
			wordCount.addStemCount();
			wordCount.getOriginalWords().add(originalWord);
		}
		wordCounts.put(wordCount.getWord(), wordCount);
		
		for (String stemWord:wordCount.getStemWords()) {
			processStemWord(wordCounts, stemWord, word);
		}
	}
	
	public static void processStemWord(Hashtable<String, WordCount> wordCounts, String stemWord, String originalWord) {
		WordCount wc = wordCounts.get(stemWord);
		if (wc != null) {
			addStemCount(wc, originalWord);
		} else {
			addNewWordCount(wordCounts, new ArrayList<String>(), new ArrayList<StemRule>(), stemWord, originalWord);
		}
	}
	
	public static void addStemCount(WordCount stemWord, String originalWord) {
		stemWord.addStemCount();
		if (!stemWord.getOriginalWords().contains(originalWord)) {
			stemWord.getOriginalWords().add(originalWord);
		}
	}
	
	public static void addActualCount(Hashtable<String, WordCount> wordCounts, WordCount word) {
		word.addActualCount();
		for (String stemWord:word.getStemWords()) {
			addStemCount(wordCounts.get(stemWord), word.getWord());
		}
	}
	
	public static void processLine(Hashtable<String, WordCount> wordCounts, ArrayList<String> stopWords, ArrayList<StemRule> stemRules, String line) {
		List<String> words = Arrays.asList(line.split(" "));
		for (String inputWord:words) {
			String word = inputWord.replaceAll("[^a-zA-Z']", "");
		
			//Skip if it wasn't a word (number of special character only)
			if (word.trim().length() == 0) continue;
			
			WordCount wc = wordCounts.get(word.toLowerCase());
			if (wc != null) {
				addActualCount(wordCounts, wc);
			} else {
				addNewWordCount(wordCounts, stopWords, stemRules, word, null);
			}
		}
	}
	
    public static void readFile(Hashtable<String, WordCount> wordCounts, ArrayList<String> stopWords, ArrayList<StemRule> stemRules, String filePath){
    	final Charset ENCODING = StandardCharsets.UTF_8;
    	
    	try {
	        Path path = Paths.get(filePath);
	        try (Scanner scanner =  new Scanner(path, ENCODING.name())){
	        	while (scanner.hasNextLine()){
	        		processLine(wordCounts, stopWords, stemRules, scanner.nextLine());
	        	}      
	        }
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    }

	public static void main(String[] args) {
		try {
			ArrayList<StemRule> stemRules = new ArrayList<StemRule>();
			stemRules.add(new StemRule("s"));
			stemRules.add(new StemRule("es"));
			stemRules.add(new StemRule("ves", "fe"));
			stemRules.add(new StemRule("ves", "f"));
			
			ArrayList<String>stopWordsList = new ArrayList<String>();
			StopWords.addStopWord(stopWordsList, "A");
			StopWords.addStopWord(stopWordsList, "tEst");
			StopWords.addStopWord(stopWordsList, "word");
			StopWords.addStopWord(stopWordsList, "Stop");
			StopWords.addStopWord(stopWordsList, "tHe");
			StopWords.addStopWord(stopWordsList, "at");
			StopWords.addStopWord(stopWordsList, "in");
			StopWords.addStopWord(stopWordsList, "EU");
			
			String file = "./documents/SampleTextFile_1000kb.txt";
			
			Hashtable<String, WordCount> wordCounts = new Hashtable<String, WordCount>();
			FileProcessor.readFile(wordCounts, stopWordsList, stemRules, file);
	        Set<String> keys = wordCounts.keySet();
	        for(String key: keys){
	            System.out.print("Value of "+key+" is: ");
	            System.out.print("Stop Word = " + wordCounts.get(key).isStopWord());
	            System.out.print(" Actual count = " + wordCounts.get(key).getActualCount());
	            System.out.println(" - Stem count = " + wordCounts.get(key).getStemCount());
	        }
	        System.out.println(wordCounts.size());
			
		} catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		

	}

}
