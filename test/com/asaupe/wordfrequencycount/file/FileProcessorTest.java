package com.asaupe.wordfrequencycount.file;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Hashtable;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.asaupe.wordfrequencycount.util.StemRule;
import com.asaupe.wordfrequencycount.util.Stemming;
import com.asaupe.wordfrequencycount.util.StopWords;

class FileProcessorTest {
	ArrayList<StemRule> stemRules;
	ArrayList<String> stopWordsList;
	Hashtable<String, WordCount> wordCounts;

	@BeforeEach
	void setUp() throws Exception {
		stopWordsList = new ArrayList<String>();
		StopWords.addStopWord(stopWordsList, "tEst");
		StopWords.addStopWord(stopWordsList, "word");
		StopWords.addStopWord(stopWordsList, "Stop");
		StopWords.addStopWord(stopWordsList, "The");
		
		stemRules = new ArrayList<StemRule>();
		stemRules.add(new StemRule("s"));
		stemRules.add(new StemRule("es"));
		stemRules.add(new StemRule("ed"));
		stemRules.add(new StemRule("ing"));
		stemRules.add(new StemRule("ves", "fe"));
		stemRules.add(new StemRule("ves", "f"));
		
		wordCounts = new Hashtable<String, WordCount>();
	}

	@AfterEach
	void tearDown() throws Exception {
		stopWordsList = null;
		stemRules = null;
		wordCounts.clear();
	}

	@Test
	void testAddStemCount() {
		WordCount wordCount = new WordCount();
		wordCount.setWord("jump");

		FileProcessor.addStemCount(wordCount, "jumped");
        assertTrue(wordCount.getStemCount() == 1);
        assertTrue(wordCount.getOriginalWords().size() == 1);
        
        //Confirm adding count from existing word moves count but doesn't add original words
		FileProcessor.addStemCount(wordCount, "jumped");
        assertTrue(wordCount.getStemCount() == 2);
        assertTrue(wordCount.getOriginalWords().size() == 1);
        
        //Confirm adding count from new word increments count and original words
		FileProcessor.addStemCount(wordCount, "jumping");
        assertTrue(wordCount.getStemCount() == 3);
        assertTrue(wordCount.getOriginalWords().size() == 2);
	}
	
	@Test
	void testProcessStemWord() {
		//creates new wordcount
		FileProcessor.processStemWord(wordCounts, "jump", "jumping");
        assertTrue(wordCounts.containsKey("jump"));
        assertTrue(wordCounts.get("jump").getActualCount() == 0);
        assertTrue(wordCounts.get("jump").getStemCount() == 1);
        assertTrue(wordCounts.get("jump").getOriginalWords().contains("jumping"));
        assertFalse(wordCounts.get("jump").getStemWords().size() > 0);
        
        //add to stemCount (nothing else)
		FileProcessor.processStemWord(wordCounts, "jump", "jumping");
        assertTrue(wordCounts.containsKey("jump"));
        assertTrue(wordCounts.get("jump").getActualCount() == 0);
        assertTrue(wordCounts.get("jump").getStemCount() == 2);
        assertTrue(wordCounts.get("jump").getOriginalWords().contains("jumping"));
        assertTrue(wordCounts.get("jump").getOriginalWords().size() == 1);
        assertFalse(wordCounts.get("jump").getStemWords().size() > 0);
        
        //add to stemCount and adds to original words
		FileProcessor.processStemWord(wordCounts, "jump", "jumped");
        assertTrue(wordCounts.containsKey("jump"));
        assertTrue(wordCounts.get("jump").getActualCount() == 0);
        assertTrue(wordCounts.get("jump").getStemCount() == 3);
        assertTrue(wordCounts.get("jump").getOriginalWords().contains("jumped"));
        assertTrue(wordCounts.get("jump").getOriginalWords().size() == 2);
        assertFalse(wordCounts.get("jump").getStemWords().size() > 0);
        
        //creates new wordcount (doesn't impact "jump" wordCount)
		FileProcessor.processStemWord(wordCounts, "walk", "walking");
        assertTrue(wordCounts.containsKey("walk"));
        assertTrue(wordCounts.containsKey("jump"));
        assertTrue(wordCounts.get("walk").getActualCount() == 0);
        assertTrue(wordCounts.get("walk").getStemCount() == 1);
        assertTrue(wordCounts.get("walk").getOriginalWords().contains("walking"));
        assertTrue(wordCounts.get("walk").getOriginalWords().size() == 1);
        assertFalse(wordCounts.get("walk").getStemWords().size() > 0);
        assertTrue(wordCounts.size() == 2);
	}
	
	@Test
	void testAddAccountCount() {
		WordCount wordCount = new WordCount();
		wordCount.setWord("hairdryer");
		
		//Standard actual count
		FileProcessor.addActualCount(wordCounts, wordCount);
        assertTrue(wordCount.getActualCount() == 1);
        
        //Increment count
		FileProcessor.addActualCount(wordCounts, wordCount);
        assertTrue(wordCount.getActualCount() == 2);
        
        //Preparing for stem count check
		wordCount = new WordCount();
		wordCount.setWord("ounce");
		wordCounts.put(wordCount.getWord(), wordCount);
		
		wordCount = new WordCount();
		wordCount.setWord("ounc");
		wordCounts.put(wordCount.getWord(), wordCount);
        
		wordCount = new WordCount();
		wordCount.setWord("ounces");
		ArrayList<String> stemWords = new ArrayList<String>();
		stemWords.add("ounce");
		stemWords.add("ounc");
		wordCount.setStemWords(stemWords);
        
        //Test incrementing underlying stem words
		FileProcessor.addActualCount(wordCounts, wordCount);
        assertTrue(wordCount.getActualCount() == 1);
        assertTrue(wordCounts.get("ounce").getStemCount() == 1);
        assertTrue(wordCounts.get("ounc").getStemCount() == 1);
        
        //Test incrementing actual count on stem words
		FileProcessor.addActualCount(wordCounts, wordCounts.get("ounce"));
        assertTrue(wordCounts.get("ounce").getActualCount() == 1);   
        assertTrue(wordCounts.get("ounce").getStemCount() == 1);
	}
	
	@Test
	void testAddNewWordCount() {
		//Check if stop words are getting set
		FileProcessor.addNewWordCount(wordCounts, stopWordsList, stemRules, "test", null);
        assertTrue(wordCounts.containsKey("test"));
        assertTrue(wordCounts.get("test").isStopWord());
        
        //Check that stem words are getting created
		FileProcessor.addNewWordCount(wordCounts, stopWordsList, stemRules, "scarves", null);
        assertTrue(wordCounts.containsKey("scarves"));
        assertFalse(wordCounts.get("scarves").isStopWord());
        assertTrue(wordCounts.containsKey("scarv"));
        assertTrue(wordCounts.containsKey("scarve"));
        assertTrue(wordCounts.containsKey("scarfe"));
        assertTrue(wordCounts.containsKey("scarf"));
        
        //Check if stem words are getting created with the original words
		FileProcessor.addNewWordCount(wordCounts, stopWordsList, stemRules, "bill", "bills");
        assertTrue(wordCounts.containsKey("bill"));
        assertTrue(wordCounts.get("bill").getOriginalWords().contains("bills"));	
	}
	
	@Test
	void testProcessLine() {
		String file = "THE. brown (fox) ; 12342";
		
		//Check that alphanumeric data is removed and blank fields don't get counted
		FileProcessor.processLine(wordCounts, stopWordsList, stemRules, file);
		assertTrue(wordCounts.containsKey("the"));
		assertTrue(wordCounts.containsKey("brown"));
		assertTrue(wordCounts.containsKey("fox"));
		assertFalse(wordCounts.containsKey(""));
		assertFalse(wordCounts.containsKey(";"));
		assertFalse(wordCounts.containsKey("12342"));
	}
}
