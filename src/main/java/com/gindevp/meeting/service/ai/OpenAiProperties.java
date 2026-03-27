package com.gindevp.meeting.service.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.ai.openai")
public class OpenAiProperties {

    /**
     * OpenAI API base URL.
     */
    private String baseUrl = "https://api.openai.com/v1";

    /**
     * OpenAI API key. Prefer configuring via environment variable OPENAI_API_KEY.
     */
    private String apiKey;

    /**
     * Model name, e.g. "gpt-4o-mini".
     */
    private String model = "gpt-4o-mini";

    /**
     * Request timeout in milliseconds.
     */
    private long timeoutMs = 30000;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public long getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public boolean hasApiKey() {
        return apiKey != null && !apiKey.trim().isBlank();
    }
}
