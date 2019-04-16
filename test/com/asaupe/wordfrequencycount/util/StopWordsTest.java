package com.asaupe.wordfrequencycount.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StopWordsTest {
	
	ArrayList<String> stopWordsList;

	@BeforeEach
	void setUp() throws Exception {
		stopWordsList = new ArrayList<String>();
		StopWords.addStopWord(stopWordsList, "tEst");
		StopWords.addStopWord(stopWordsList, "word");
		StopWords.addStopWord(stopWordsList, "Stop");
		StopWords.addStopWord(stopWordsList, "The");
	}

	@AfterEach
	void tearDown() throws Exception {
		stopWordsList = null;
	}

	@Test
	void test() {
        assertTrue(StopWords.IsStopWord(stopWordsList, "TEST"));
        assertTrue(StopWords.IsStopWord(stopWordsList, "word"));
        assertTrue(StopWords.IsStopWord(stopWordsList, "stoP"));
        assertFalse(StopWords.IsStopWord(stopWordsList, "field2"));
        assertFalse(StopWords.IsStopWord(stopWordsList, "field3"));
	}

}
