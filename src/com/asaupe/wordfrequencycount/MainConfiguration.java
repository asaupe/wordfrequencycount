package com.asaupe.wordfrequencycount;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class MainConfiguration extends Configuration {

    @NotEmpty
    private String stopWordsPath;
    @NotEmpty
    private String stemRulesPath;
    @NotEmpty
    private String documentPath;
    @NotEmpty
    private String connectionString;
    @NotEmpty
    private String database;
    @NotEmpty
    private String wordCountCollection;
    @NotEmpty
    private String fileCollection;

    @JsonProperty
	public String getStopWordsPath() {
		return stopWordsPath;
	}

    @JsonProperty
	public void setStopWordsPath(String stopWordsPath) {
		this.stopWordsPath = stopWordsPath;
	}

	public String getStemRulesPath() {
		return stemRulesPath;
	}

	public void setStemRulesPath(String stemRulesPath) {
		this.stemRulesPath = stemRulesPath;
	}

	public String getConnectionString() {
		return connectionString;
	}

	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getWordCountCollection() {
		return wordCountCollection;
	}

	public void setWordCountCollection(String wordCountCollection) {
		this.wordCountCollection = wordCountCollection;
	}

	public String getFileCollection() {
		return fileCollection;
	}

	public void setFileCollection(String fileCollection) {
		this.fileCollection = fileCollection;
	}

	public String getDocumentPath() {
		return documentPath;
	}

	public void setDocumentPath(String documentPath) {
		this.documentPath = documentPath;
	}
}
