package com.asaupe.wordfrequencycount;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import com.asaupe.wordfrequencycount.resources.FileResource;
import com.asaupe.wordfrequencycount.resources.WordCountResource;
import com.asaupe.wordfrequencycount.util.StemRule;
import com.asaupe.wordfrequencycount.util.StopWords;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Hashtable;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.bson.Document;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import com.asaupe.wordfrequencycount.file.FileProcessor;
import com.asaupe.wordfrequencycount.file.PersistResults;
import com.asaupe.wordfrequencycount.file.WordCount;

public class MainApplication extends Application<MainConfiguration> {
    public static void main(String[] args) throws Exception {
        new MainApplication().run(args);
    }

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<MainConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(MainConfiguration configuration,
                    Environment environment) {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI(configuration.getConnectionString()));
        final FilterRegistration.Dynamic cors =
        		environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        //Opening everything up for simplicity - not advisable for a production environment
        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "Cache-Control,If-Modified-Since,Pragma,Content-Type,Authorization,X-responseuested-With,Content-Length,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        
        try {
			final MongoDatabase db = mongoClient.getDatabase(configuration.getDatabase());
			final MongoCollection<Document> fileCollection = db.getCollection(configuration.getFileCollection());
			final MongoCollection<Document> wcCollection = db.getCollection(configuration.getWordCountCollection());
			
	        final FileResource fileResource = new FileResource(
	        		configuration.getDocumentPath());
	        final WordCountResource wcResource = new WordCountResource(fileCollection, wcCollection);

	        environment.jersey().register(fileResource);
	        environment.jersey().register(wcResource);
			
	        ArrayList<String> stopWords = StopWords.loadStopWords(configuration.getStopWordsPath());
	        ArrayList<StemRule> stemRules = StemRule.loadStemRules(configuration.getStemRulesPath());
	        
	/*		String file = "./documents/SampleTextFile_1000kb.txt";
			
			Hashtable<String, WordCount> wordCounts = new Hashtable<String, WordCount>();
			FileProcessor.readFile(wordCounts, stopWords, stemRules, file);
			PersistResults.persistResults(wordCounts, wcCollection);*/
	
	        //Should be it's own service but I think a thread works well for this exercise
	        Thread t = new Thread(new FileProcessor(fileCollection, wcCollection, stopWords, stemRules), "File Processor");
		    t.start();
			System.out.println("testing");
    	} finally {
    		//mongoClient.close();
    	}
    }

}