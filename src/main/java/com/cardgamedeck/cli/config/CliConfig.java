package com.cardgamedeck.cli.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "card-game.cli")
public class CliConfig {
    // Default to localhost, but allow override
    //private String apiBaseUrl = "http://localhost:8080/api";
    private String apiBaseUrl = "http://localhost:8080/api";


    public String getApiBaseUrl() {
        // Check environment variable first
        String envUrl = System.getenv("API_BASE_URL");
        if (envUrl != null) return envUrl;

        // Use localhost when running locally
        return "http://localhost:8080/api";
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }
}