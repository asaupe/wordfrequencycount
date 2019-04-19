package com.asaupe.wordfrequencycount.file;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Scanner;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import com.asaupe.wordfrequencycount.util.StemRule;
import com.asaupe.wordfrequencycount.util.Stemming;
import com.asaupe.wordfrequencycount.util.StopWords;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;

public class FileProcessor implements Runnable {
	private MongoCollection<Document> fileCollection;
	private MongoCollection<Document> wcCollection;
    private ArrayList<String> stopWords;
    private ArrayList<StemRule> stemRules;
    private String filePathTemplate;

	
	public FileProcessor(MongoCollection<Document> fileCollection, MongoCollection<Document> wcCollection, ArrayList<String> stopWords,
			ArrayList<StemRule> stemRules, String filePathTemplate) {
		this.fileCollection = fileCollection;
		this.wcCollection = wcCollection;
		this.stopWords = stopWords;
		this.stemRules = stemRules;
		this.filePathTemplate = filePathTemplate;
	}
	
	//TODO: Maybe overloading this to much think about splitting for stem words and actual words
	public static void addNewWordCount(Hashtable<String, WordCount> wordCounts, ArrayList<String> stopWords, ArrayList<StemRule> stemRules, String word, String originalWord) {
		WordCount wordCount = new WordCount();
		wordCount.setWord(word);
		wordCount.setStopWord(StopWords.IsStopWord(stopWords, wordCount.getWord()));
		//TODO: Think about if stem word is also stop word (weird edge case).
		if (!wordCount.isStopWord()) {
			wordCount.setStemWords(Stemming.stemWords(stemRules, wordCount.getWord()));
		}
		if (originalWord == null) {
			wordCount.addActualCount();
		} else {
			wordCount.addStemCount();
			wordCount.getOriginalWords().add(originalWord);
		}
		wordCounts.put(wordCount.getWord(), wordCount);
		
		for (String stemWord:wordCount.getStemWords()) {
			processStemWord(wordCounts, stemWord, wordCount.getWord());
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
	        	scanner.close();
	        }
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    }
    

	@Override
	public void run()  {
		try {
	        BasicDBObject nextQuery = new BasicDBObject();
	        nextQuery.put("status", FileStatus.ready.name());
	        Document executionDoc = null;
	        
	        while (true) {
	        	try {
	        		executionDoc = fileCollection.findOneAndUpdate(nextQuery, new Document("$set", new Document("status", FileStatus.processing.name())));
					
					if (executionDoc != null) {
				        BasicDBObject thisQuery = new BasicDBObject();
				        thisQuery.put("_id", executionDoc.get("_id"));
				        
						Hashtable<String, WordCount> wordCounts = new Hashtable<String, WordCount>();
						String filePath = this.filePathTemplate.concat(executionDoc.getString("fileName")).concat(".txt");
						FileProcessor.readFile(wordCounts, stopWords, stemRules, filePath);
						PersistResults.persistResults(wordCounts, wcCollection, executionDoc.getObjectId("_id"));
				        
						Instant inst = Instant.now();
						Document statusUpdate = 
							new Document("status", FileStatus.done.name())
							.append("actionTaken", inst);
						fileCollection.updateOne(thisQuery, new Document("$set", statusUpdate));
						executionDoc = null;
					} else {
						Thread.sleep(1000);
					}
	            } catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
					break;
		        } catch (Exception ex) {
		        	//Slow down error loop if program gets caught in a circular error
		        	Thread.sleep(500);
					try {
			        	if (executionDoc != null) {
					        BasicDBObject thisQuery = new BasicDBObject();
					        thisQuery.put("_id", executionDoc.get("_id"));
							Instant inst = Instant.now();
							Document statusUpdate = new Document("status", FileStatus.error.name()).append("actionTaken", inst);
					        fileCollection.updateOne(thisQuery, statusUpdate);
			        	}
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
		        } finally {
		        	executionDoc = null;
		        }
			}
		}
		catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		finally {
			System.out.println("Shutting down File Processing");
		}
	}

    //TESTING ONLY - this main is only provided for doing a testing run
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
