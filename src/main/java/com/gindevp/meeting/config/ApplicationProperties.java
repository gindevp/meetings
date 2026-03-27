package com.gindevp.meeting.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Meetings.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Liquibase liquibase = new Liquibase();
    private final Ai ai = new Ai();
    private final Notifications notifications = new Notifications();

    // jhipster-needle-application-properties-property

    public Liquibase getLiquibase() {
        return liquibase;
    }

    public Ai getAi() {
        return ai;
    }

    public Notifications getNotifications() {
        return notifications;
    }

    // jhipster-needle-application-properties-property-getter

    public static class Liquibase {

        private Boolean asyncStart = true;

        public Boolean getAsyncStart() {
            return asyncStart;
        }

        public void setAsyncStart(Boolean asyncStart) {
            this.asyncStart = asyncStart;
        }
    }

    public static class Ai {

        private final OpenAi openai = new OpenAi();

        public OpenAi getOpenai() {
            return openai;
        }
    }

    public static class OpenAi {

        private String apiKey;
        private String model;
        private String baseUrl;
        private Long timeoutMs;

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

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public Long getTimeoutMs() {
            return timeoutMs;
        }

        public void setTimeoutMs(Long timeoutMs) {
            this.timeoutMs = timeoutMs;
        }
    }

    public static class Notifications {

        /**
         * Whether to send weekly report once at startup.
         * Useful to disable in dev to avoid noisy mail auth errors.
         */
        private Boolean weeklyReportOnStartup = false;

        public Boolean getWeeklyReportOnStartup() {
            return weeklyReportOnStartup;
        }

        public void setWeeklyReportOnStartup(Boolean weeklyReportOnStartup) {
            this.weeklyReportOnStartup = weeklyReportOnStartup;
        }
    }
    // jhipster-needle-application-properties-property-class
}
