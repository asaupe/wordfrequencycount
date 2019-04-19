package com.asaupe.wordfrequencycount.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.bson.Document;
import org.json.JSONObject;

import com.asaupe.wordfrequencycount.file.FileStatus;
import com.codahale.metrics.annotation.Timed;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;

@javax.ws.rs.Path("/file")
@Produces(MediaType.TEXT_PLAIN)
public class FileResource {
	private MongoCollection<Document> fileCollection;
	protected String documentPath;
	
    public FileResource(String documentPath, MongoCollection<Document> fileCollection) {
    	this.fileCollection = fileCollection;
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
    
    protected void SaveFile(String fileName, String fileData) throws Exception {
    	try (PrintWriter out = new PrintWriter(this.documentPath.concat(fileName).concat(".txt"))) {
    	    out.println(fileData);
    	} catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    @Timed
    @POST
    @javax.ws.rs.Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadFile(@Context HttpServletRequest request) {
        if (ServletFileUpload.isMultipartContent(request)) {
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            List<FileItem> items = null;
            try {
                items = upload.parseRequest(request);
                Iterator<FileItem> iter = items.iterator();
                while (iter.hasNext()) {
                    FileItem item = iter.next();
	                if (!item.isFormField() && item.getSize() > 0) {
                        try {
                        	//Extracting the filename from the filepath
	                        String fileName = item.getName().split("[.]")[0];
	                        String data = item.getString();

							SaveFile(fileName, data);
							Document fileJson = new Document();
							fileJson.append("status", FileStatus.ready.name());
							fileJson.append("fileName", fileName);
							fileCollection.insertOne(fileJson);
						} catch (Exception e) {
							e.printStackTrace();
							return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"success\":\"false\"}").build();
						}
	                } else {
	                	System.out.println("getFieldName:" + item.getFieldName());
	                	System.out.println(item.getString());
	                	return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"success\":\"false\"}").build();
	                }
	            }
            } catch (FileUploadException e) {
                e.printStackTrace();
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"success\":\"false\"}").build();
            }
        }
        return Response.status(Status.OK).entity("{\"success\":\"true\"}").build();
    }
}
