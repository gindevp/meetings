package com.gindevp.meeting.config;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Properties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Provides a no-op {@link JavaMailSender} in prod when mail is not configured (e.g. Railway blocks SMTP).
 * Mail health is disabled in prod; this bean allows the app to start without real mail.
 */
@Configuration
@Profile("prod")
public class NoOpMailConfiguration {

    @Bean
    @ConditionalOnMissingBean(JavaMailSender.class)
    public JavaMailSender javaMailSender() {
        return new NoOpJavaMailSender();
    }

    /**
     * No-op implementation: createMimeMessage() returns a dummy message, send() does nothing.
     */
    private static final class NoOpJavaMailSender implements JavaMailSender {

        private static final Session SESSION = Session.getInstance(new Properties());

        @Override
        public MimeMessage createMimeMessage() {
            return new MimeMessage(SESSION);
        }

        @Override
        public MimeMessage createMimeMessage(InputStream contentStream) {
            try {
                return new MimeMessage(SESSION, contentStream);
            } catch (Exception e) {
                return new MimeMessage(SESSION);
            }
        }

        @Override
        public void send(MimeMessage mimeMessage) {
            // no-op
        }

        @Override
        public void send(MimeMessage... mimeMessages) {
            // no-op
        }

        @Override
        public void send(org.springframework.mail.SimpleMailMessage simpleMessage) {
            // no-op
        }

        @Override
        public void send(org.springframework.mail.SimpleMailMessage... simpleMessages) {
            // no-op
        }
    }
}
