package com.asaupe.wordfrequencycount;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class MainConfiguration extends Configuration {
    @NotEmpty
    private String template;

    @NotEmpty
    private String defaultName = "Stranger";
    
    @NotEmpty
    private String stopWordsPath;
    @NotEmpty
    private String stemRulesPath;

    @JsonProperty
    public String getTemplate() {
        return template;
    }

    @JsonProperty
    public void setTemplate(String template) {
        this.template = template;
    }

    @JsonProperty
    public String getDefaultName() {
        return defaultName;
    }

    @JsonProperty
    public void setDefaultName(String name) {
        this.defaultName = name;
    }

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
}
