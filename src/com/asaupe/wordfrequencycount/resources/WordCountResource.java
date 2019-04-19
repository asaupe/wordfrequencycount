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

import com.asaupe.wordfrequencycount.file.FileStatus;
import com.codahale.metrics.annotation.Timed;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

@Path("/wordcount")
@Produces(MediaType.APPLICATION_JSON)
public class WordCountResource {
	private MongoCollection<Document> fileCollection;
	private MongoCollection<Document> wcCollection;
	
    public WordCountResource(MongoCollection<Document> fileCollection, MongoCollection<Document> wcCollection) {
    	this.fileCollection = fileCollection;
    	this.wcCollection = wcCollection;
    }

    protected List<Document> getWordCount(String fileId, boolean stopWords, boolean stemWords) {
		List<Document> wordCounts = new ArrayList<Document>();
		FindIterable<Document> query = wcCollection.find(Filters.eq("fileId", fileId)).limit(25);

		if (!stopWords) {
			query.filter(Filters.and(Filters.eq("fileId", fileId), Filters.eq("stopWord", false)));
		}
		if (stemWords) {
			query.sort(new Document().append("total", -1));
		} else {
			query.sort(new Document().append("actualCount", -1));
		}
		return query.into(wordCounts);
    }
    
    @GET
    @Timed
    public Response getFile(@QueryParam("includeStopWords") boolean stopWords, @QueryParam("includeStemWords") boolean stemWords) {
    	try {
    		ArrayList<Document> files = new ArrayList<Document>();
    		fileCollection.find(Filters.eq("status", FileStatus.done.name())).sort(new Document().append("actionTaken", -1)).limit(10).into(files);
    		
    		for(Document file:files) {
        		List<Document> wordCounts = getWordCount(file.getObjectId("_id").toString(), stopWords, stemWords);
        		file.append("wordCounts", wordCounts);
    		}
	        return Response.status(Status.OK).entity(new JSONObject().put("count", files.size()).put("files", files).toString()).build();
    	} catch (Exception ex) {
    		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
    	}
    }
}
