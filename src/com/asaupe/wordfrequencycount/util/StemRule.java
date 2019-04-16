package com.asaupe.wordfrequencycount.util;

public class StemRule {
	private String ending;
	private String lettersAdded = "";
	
	public StemRule() {
	}
	
	public StemRule(String ending) {
		setEnding(ending);
	}
	
	public StemRule(String ending, String lettersAdded) {
		setEnding(ending);
		setLettersAdded(lettersAdded);
	}
	
	public String getEnding() {
		return ending;
	}
	
	public void setEnding(String ending) {
		this.ending = ending.toLowerCase();
	}
	
	public String getLettersAdded() {
		return lettersAdded;
	}
	
	public void setLettersAdded(String lettersAdded) {
		this.lettersAdded = lettersAdded.toLowerCase();
	}
}
