package com.sporthub.notification.controller;

import com.sporthub.notification.dto.request.UpdatePreferencesRequest;
import com.sporthub.notification.dto.response.PreferencesResponse;
import com.sporthub.notification.service.NotificationPreferencesService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications/preferences")
public class NotificationPreferencesController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationPreferencesController.class);

    @Autowired
    private NotificationPreferencesService preferencesService;

    // kullanıcı tercihlerini getir
    @GetMapping("/user/{userId}")
    public ResponseEntity<PreferencesResponse> getUserPreferences(@PathVariable Long userId) {
        logger.info("Getting preferences for user: {}", userId);

        try {
            PreferencesResponse preferences = preferencesService.getUserPreferences(userId);
            return ResponseEntity.ok(preferences);

        } catch (Exception e) {
            logger.error("Error getting user preferences", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // kullanıcı tercihlerini güncelle
    @PutMapping("/user/{userId}")
    public ResponseEntity<?> updateUserPreferences(
            @PathVariable Long userId, 
            @Valid @RequestBody UpdatePreferencesRequest request) {
        
        logger.info("Updating preferences for user: {}", userId);

        try {
            request.setUserId(userId);
            
            PreferencesResponse updatedPreferences = preferencesService.updateUserPreferences(userId, request);
            return ResponseEntity.ok(updatedPreferences);

        } catch (Exception e) {
            logger.error("Error updating user preferences", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to update preferences");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // varsayılan tercihler oluştur
    @PostMapping("/user/{userId}/default")
    public ResponseEntity<?> createDefaultPreferences(@PathVariable Long userId) {
        logger.info("Creating default preferences for user: {}", userId);

        try {
            preferencesService.createDefaultPreferences(userId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Default preferences created successfully");
            response.put("userId", String.valueOf(userId));
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error creating default preferences", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to create default preferences");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/user/{userId}/reminder-hours")
    public ResponseEntity<?> getReminderHours(@PathVariable Long userId) {
        logger.info("Getting reminder hours for user: {}", userId);

        try {
            Integer reminderHours = preferencesService.getReminderHours(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("reminderHours", reminderHours);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting reminder hours", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to get reminder hours");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
