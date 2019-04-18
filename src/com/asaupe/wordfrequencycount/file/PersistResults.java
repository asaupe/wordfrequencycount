package com.asaupe.wordfrequencycount.file;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import org.bson.Document;

import com.asaupe.wordfrequencycount.util.StemRule;
import com.asaupe.wordfrequencycount.util.StopWords;
import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class PersistResults {
	
	//TODO: Being kind of lazy here - I should probably create/call an endpoint but direct persist OK for the time limits
	public static void persistResults(Hashtable<String, WordCount> wordCounts, MongoCollection<Document> wcCollection) {
        Set<String> keys = wordCounts.keySet();
		Gson gson = new Gson();
        for(String key: keys){
            if (wordCounts.get(key).getActualCount() > 0) {
            	System.out.println("persist actual " + wordCounts.get(key).getWord());
            	System.out.println(gson.toJson(wordCounts.get(key)));
            	wcCollection.insertOne(Document.parse(gson.toJson(wordCounts.get(key))));
            } else if (wordCounts.get(key).getOriginalWords().size() > 1) {
            	System.out.println("persist stem word " + wordCounts.get(key).getWord());
            	System.out.println(gson.toJson(wordCounts.get(key)));
            	wcCollection.insertOne(Document.parse(gson.toJson(wordCounts.get(key))));
            } else {
            	System.out.println("skipped " + wordCounts.get(key).getWord());
            }
        }
	}

    //TESTING ONLY - this main is only provided for doing a testing run
	//WARNING - if the connection string is correct it will persist to your MongoDB (note: this user no longer exists in my database)
	//(first arg should be your connection string and second DB)
	public static void main(String[] args) {
    	final Charset ENCODING = StandardCharsets.UTF_8;
		final MongoClient mongoClient = new MongoClient(new MongoClientURI(args[0]));
		try {
			final MongoDatabase db = mongoClient.getDatabase(args[1]);
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
			persistResults(wordCounts, wcCollection);
			
			System.out.println("testing");
		} catch(Exception ex) {
			System.out.println(ex.getMessage());
		} finally {
			mongoClient.close();
		}
	}

}
