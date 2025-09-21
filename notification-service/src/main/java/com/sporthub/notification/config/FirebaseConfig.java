package com.sporthub.notification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${notification.push.firebase.service-account-path}")
    private String serviceAccountPath;

    @PostConstruct
    public void initializeFirebase() {
        try {
            // firebase config kontrolü
            if (serviceAccountPath == null || serviceAccountPath.startsWith("${")) {
                logger.warn("Firebase service account path not configured, push notifications will be disabled");
                return;
            }

            FileInputStream serviceAccount = new FileInputStream(serviceAccountPath);
            
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                logger.info("Firebase initialized successfully");
            } else {
                logger.info("Firebase already initialized");
            }

        } catch (IOException e) {
            logger.error("Firebase initialization failed: {}", e.getMessage());
            logger.warn("Push notifications will be disabled");
        } catch (Exception e) {
            logger.error("Unexpected error during Firebase initialization", e);
            logger.warn("Push notifications will be disabled");
        }
    }
}
