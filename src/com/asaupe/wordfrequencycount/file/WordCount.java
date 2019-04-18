package com.asaupe.wordfrequencycount.file;

import java.util.ArrayList;

import org.bson.types.ObjectId;

public class WordCount {
	private String word;
	private long actualCount;
	private long stemCount;
	private long total;
	private boolean stopWord;
	private String fileId;
	private ArrayList<String> stemWords = new ArrayList<String>();
	private ArrayList<String> originalWords = new ArrayList<String>();
	
	public long getActualCount() {
		return actualCount;
	}
	public void addActualCount() {
		this.actualCount++;
		this.total++;
	}
	public long getStemCount() {
		return stemCount;
	}
	public void addStemCount() {
		this.stemCount++;
		this.total++;
	}
	public boolean isStopWord() {
		return stopWord;
	}
	public void setStopWord(boolean stopWord) {
		this.stopWord = stopWord;
	}
	public ArrayList<String> getStemWords() {
		return stemWords;
	}
	public void setStemWords(ArrayList<String> stemWords) {
		this.stemWords = stemWords;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word.toLowerCase();
	}
	public ArrayList<String> getOriginalWords() {
		return originalWords;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	
	//Shortcut because I don't want to figure out the Mongo aggregate
	public long getTotal() {
		return this.total;
	}
}
