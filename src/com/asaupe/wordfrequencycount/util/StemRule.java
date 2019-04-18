package com.asaupe.wordfrequencycount.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import com.google.gson.Gson;

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
	
	public static ArrayList<StemRule> loadStemRules(String filePath) {
    	final Charset ENCODING = StandardCharsets.UTF_8;
    	ArrayList<StemRule> stemRules = new ArrayList<StemRule>();
        Path path = Paths.get(filePath);
        try (Scanner scanner =  new Scanner(path, ENCODING.name())){
	    	while (scanner.hasNextLine()){
	    		String line = scanner.nextLine();
	    		//Lines starting with # are comment lines
	    		if (line.trim().startsWith("#")) continue;
	    		
	    		Gson gson = new Gson();
	    		StemRule stemRule = gson.fromJson(line, StemRule.class);
	    		stemRules.add(stemRule);
	    	}   
	    	scanner.close();
        } catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
        return stemRules;
	}
}
