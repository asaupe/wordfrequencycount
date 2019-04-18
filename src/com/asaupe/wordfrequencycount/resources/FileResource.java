package com.asaupe.wordfrequencycount.resources;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Scanner;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.bson.Document;

import com.codahale.metrics.annotation.Timed;

@javax.ws.rs.Path("/file")
@Produces(MediaType.TEXT_PLAIN)
public class FileResource {
	protected String documentPath;
	
    public FileResource(String documentPath) {
    	this.documentPath = documentPath;
    }
    
    @GET
    @Timed
    public Response getFile(@QueryParam("file") String name) {
    	try {
	    	final Charset ENCODING = StandardCharsets.UTF_8;
	
	        Document doc = new Document().append("name", name);
	        java.nio.file.Path path = Paths.get(this.documentPath.concat(name).concat(".txt"));
	        String returnFile = "";
	        try (Scanner scanner =  new Scanner(path, ENCODING.name())){
		    	while (scanner.hasNextLine()){
		    		String line = scanner.nextLine();
		    		returnFile += line;
		    		returnFile += "\n";
		    	}   
		    	scanner.close();
	        }
	        return Response.status(Status.OK).entity(returnFile).build();
    	} catch (Exception ex) {
    		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
    	}
    }
}
