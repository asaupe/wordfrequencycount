package com.asaupe.wordfrequencycount.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.bson.Document;
import org.json.JSONObject;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.GsonBuilder;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

@Path("/wordcount")
@Produces(MediaType.APPLICATION_JSON)
public class WordCountResource {
	private MongoCollection<Document> fileCollection;
	private MongoCollection<Document> wcCollection;
	
    public WordCountResource(MongoCollection<Document> fileCollection, MongoCollection<Document> wcCollection) {
    	this.fileCollection = fileCollection;
    	this.wcCollection = wcCollection;
    }

    @GET
    @Timed
    public Response getFile(@QueryParam("name") String name, @QueryParam("includeStopWords") boolean stopWords, @QueryParam("includeStemWords") boolean stemWords) {
    	try {
    		List<Document> wordCounts = new ArrayList<Document>();
    		FindIterable<Document> query = wcCollection.find().limit(25);

    		if (!stopWords) {
    			query.filter(new Document().append("stopWord", false));
    		}
    		if (stemWords) {
    			query.sort(new Document().append("total", -1));
    		} else {
    			query.sort(new Document().append("actualCount", -1));
    		}
    		query.into(wordCounts);

	        return Response.status(Status.OK).entity(new JSONObject().put("count", wordCounts.size()).put("wordCounts", wordCounts).toString()).build();
    	} catch (Exception ex) {
    		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
    	}
    }
}
