package com.sporthub.notification.controller;

import com.sporthub.notification.entity.NotificationTemplate;
import com.sporthub.notification.service.NotificationTemplateService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications/templates")
public class NotificationTemplateController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationTemplateController.class);

    @Autowired
    private NotificationTemplateService templateService;

    @GetMapping
    public ResponseEntity<List<NotificationTemplate>> getAllTemplates() {
        logger.info("Getting all templates");

        try {
            List<NotificationTemplate> templates = templateService.getAllTemplates();
            return ResponseEntity.ok(templates);

        } catch (Exception e) {
            logger.error("Error getting all templates", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<NotificationTemplate>> getActiveTemplates() {
        logger.info("Getting active templates");

        try {
            List<NotificationTemplate> templates = templateService.getActiveTemplates();
            return ResponseEntity.ok(templates);

        } catch (Exception e) {
            logger.error("Error getting active templates", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTemplate(@PathVariable Long id) {
        logger.info("Getting template: {}", id);

        try {
            // TODO: template detay metodu eklenecek
            Map<String, String> response = new HashMap<>();
            response.put("message", "Template detail endpoint - to be implemented");
            response.put("templateId", String.valueOf(id));
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting template", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to get template");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // yeni template oluştur
    @PostMapping
    public ResponseEntity<?> createTemplate(@Valid @RequestBody NotificationTemplate template) {
        logger.info("Creating new template: {}", template.getName());

        try {
            NotificationTemplate createdTemplate = templateService.createTemplate(template);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTemplate);

        } catch (Exception e) {
            logger.error("Error creating template", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to create template");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // template güncelle
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTemplate(
            @PathVariable Long id, 
            @Valid @RequestBody NotificationTemplate template) {
        
        logger.info("Updating template: {}", id);

        try {
            NotificationTemplate updatedTemplate = templateService.updateTemplate(id, template);
            return ResponseEntity.ok(updatedTemplate);

        } catch (Exception e) {
            logger.error("Error updating template", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to update template");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTemplate(@PathVariable Long id) {
        logger.info("Deleting template: {}", id);

        try {
            templateService.deleteTemplate(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Template deleted successfully");
            response.put("templateId", String.valueOf(id));
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error deleting template", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to delete template");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activateTemplate(@PathVariable Long id) {
        logger.info("Activating template: {}", id);

        try {
            templateService.activateTemplate(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Template activated successfully");
            response.put("templateId", String.valueOf(id));
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error activating template", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to activate template");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateTemplate(@PathVariable Long id) {
        logger.info("Deactivating template: {}", id);

        try {
            templateService.deactivateTemplate(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Template deactivated successfully");
            response.put("templateId", String.valueOf(id));
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error deactivating template", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to deactivate template");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
