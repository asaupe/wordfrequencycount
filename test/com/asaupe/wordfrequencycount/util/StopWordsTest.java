package com.asaupe.wordfrequencycount.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StopWordsTest {
	
	ArrayList<String> StopWordsList;

	@BeforeEach
	void setUp() throws Exception {
		StopWordsList = new ArrayList<String>();
		StopWords.addStopWord(StopWordsList, "tEst");
		StopWords.addStopWord(StopWordsList, "word");
		StopWords.addStopWord(StopWordsList, "Stop");
	}

	@AfterEach
	void tearDown() throws Exception {
		StopWordsList = null;
	}

	@Test
	void test() {
        assertTrue(StopWords.IsStopWord(StopWordsList, "TEST"));
        assertTrue(StopWords.IsStopWord(StopWordsList, "word"));
        assertTrue(StopWords.IsStopWord(StopWordsList, "stoP"));
        assertFalse(StopWords.IsStopWord(StopWordsList, "field2"));
        assertFalse(StopWords.IsStopWord(StopWordsList, "field3"));
	}

}
