package com.asaupe.wordfrequencycount.file;

import java.util.Hashtable;
import java.util.Set;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;

public class PersistResults {
	
	//TODO: Being kind of lazy here - I should probably create/call an endpoint but direct persist OK for the time limits
	//Iterates through HashTable and persists to word counts to MongoDB
	//Skips stem words that were not confirmed
	//confirmed = An actual appearance or two original words create the stem word
	public static void persistResults(Hashtable<String, WordCount> wordCounts, MongoCollection<Document> wcCollection, ObjectId fileId) {
        Set<String> keys = wordCounts.keySet();
		Gson gson = new Gson();
        for(String key: keys){
        	wordCounts.get(key).setFileId(fileId.toString());
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
}
