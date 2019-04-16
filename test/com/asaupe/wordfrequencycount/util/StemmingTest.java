package com.asaupe.wordfrequencycount.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StemmingTest {
	
	ArrayList<StemRule> stemRules;

	@BeforeEach
	void setUp() throws Exception {
		stemRules = new ArrayList<StemRule>();
		stemRules.add(new StemRule("s"));
		stemRules.add(new StemRule("es"));
		stemRules.add(new StemRule("ves", "fe"));
		stemRules.add(new StemRule("ves", "f"));
	}

	@AfterEach
	void tearDown() throws Exception {
		stemRules = null;
	}

	@Test
	void test() {
		ArrayList<String> stemWords;
		stemWords = Stemming.stemWords(stemRules, "CATS");
        assertTrue(stemWords.size() == 1);
        assertTrue(stemWords.get(0).equals("cat"));
        
		stemWords = Stemming.stemWords(stemRules, "matches");
        assertTrue(stemWords.size() == 2);
        assertTrue(stemWords.contains("match"));
        assertTrue(stemWords.contains("matche"));
        
		stemWords = Stemming.stemWords(stemRules, "scarVes");
        assertTrue(stemWords.size() == 4);
        assertTrue(stemWords.contains("scarve"));
        assertTrue(stemWords.contains("scarv"));
        assertTrue(stemWords.contains("scarf"));
        assertTrue(stemWords.contains("scarfe"));
        
		stemWords = Stemming.stemWords(stemRules, "kniveS");
        assertTrue(stemWords.size() == 4);
        assertTrue(stemWords.contains("knive"));
        assertTrue(stemWords.contains("kniv"));
        assertTrue(stemWords.contains("knif"));
        assertTrue(stemWords.contains("knife"));
	}

}
