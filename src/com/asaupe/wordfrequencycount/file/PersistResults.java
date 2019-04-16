package com.asaupe.wordfrequencycount.file;

import java.util.ArrayList;
import java.util.Hashtable;

import org.bson.Document;

import com.asaupe.wordfrequencycount.util.StemRule;
import com.asaupe.wordfrequencycount.util.StopWords;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class PersistResults {
	
	public static void persistResults(Hashtable<String, WordCount> wordCounts, MongoCollection<Document> wcCollection) {
		
	}

	public static void main(String[] args) {
		try {
			final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb+srv://arne:16isSixteen@arnetest-gtgp9.mongodb.net/test?retryWrites=true"));
			final MongoDatabase db = mongoClient.getDatabase("ArneTest");
			final MongoCollection<Document> wcCollection = db.getCollection("wordCounts");
			
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
			
			System.out.println("testing");
		} catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

}
