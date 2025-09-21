package com.sporthub.notification.service;

import com.sporthub.notification.entity.NotificationTemplate;
import com.sporthub.notification.enums.NotificationCategory;
import com.sporthub.notification.enums.NotificationType;
import com.sporthub.notification.repository.NotificationTemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class NotificationTemplateService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationTemplateService.class);

    @Autowired
    private NotificationTemplateRepository templateRepository;

    @Autowired
    private TemplateEngine templateEngine;

    @Transactional(readOnly = true)
    public Optional<NotificationTemplate> getTemplate(String templateName, NotificationType type) {
        return templateRepository.findByName(templateName);
    }

    @Transactional(readOnly = true)
    public Optional<NotificationTemplate> getTemplateByTypeAndCategory(NotificationType type, NotificationCategory category) {
        return templateRepository.findByTypeAndCategoryAndIsActiveTrue(type, category);
    }

    // template işle
    public String processTemplate(String templateName, Map<String, Object> variables) {
        try {
            Context context = new Context();
            if (variables != null) {
                context.setVariables(variables);
            }

            return templateEngine.process(templateName, context);

        } catch (Exception e) {
            logger.error("Error processing template: {}", templateName, e);
            throw new RuntimeException("Failed to process template: " + templateName, e);
        }
    }

    // template oluştur
    public NotificationTemplate createTemplate(NotificationTemplate template) {
        logger.info("Creating template: {}", template.getName());

        if (templateRepository.existsByName(template.getName())) {
            throw new RuntimeException("Template with name already exists: " + template.getName());
        }

        return templateRepository.save(template);
    }

    // template güncelle
    public NotificationTemplate updateTemplate(Long templateId, NotificationTemplate updatedTemplate) {
        logger.info("Updating template: {}", templateId);

        Optional<NotificationTemplate> existingTemplateOpt = templateRepository.findById(templateId);
        if (existingTemplateOpt.isEmpty()) {
            throw new RuntimeException("Template not found: " + templateId);
        }

        NotificationTemplate existingTemplate = existingTemplateOpt.get();
        
        if (updatedTemplate.getName() != null) {
            existingTemplate.setName(updatedTemplate.getName());
        }
        if (updatedTemplate.getType() != null) {
            existingTemplate.setType(updatedTemplate.getType());
        }
        if (updatedTemplate.getCategory() != null) {
            existingTemplate.setCategory(updatedTemplate.getCategory());
        }
        if (updatedTemplate.getSubject() != null) {
            existingTemplate.setSubject(updatedTemplate.getSubject());
        }
        if (updatedTemplate.getTemplateContent() != null) {
            existingTemplate.setTemplateContent(updatedTemplate.getTemplateContent());
        }
        if (updatedTemplate.getVariables() != null) {
            existingTemplate.setVariables(updatedTemplate.getVariables());
        }
        if (updatedTemplate.getIsActive() != null) {
            existingTemplate.setIsActive(updatedTemplate.getIsActive());
        }

        return templateRepository.save(existingTemplate);
    }

    @Transactional(readOnly = true)
    public List<NotificationTemplate> getAllTemplates() {
        return templateRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<NotificationTemplate> getActiveTemplates() {
        return templateRepository.findByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<NotificationTemplate> getActiveTemplatesByType(NotificationType type) {
        return templateRepository.findByTypeAndIsActiveTrue(type);
    }

    public void deleteTemplate(Long templateId) {
        logger.info("Deleting template: {}", templateId);

        if (!templateRepository.existsById(templateId)) {
            throw new RuntimeException("Template not found: " + templateId);
        }

        templateRepository.deleteById(templateId);
    }

    public void deactivateTemplate(Long templateId) {
        logger.info("Deactivating template: {}", templateId);

        Optional<NotificationTemplate> templateOpt = templateRepository.findById(templateId);
        if (templateOpt.isEmpty()) {
            throw new RuntimeException("Template not found: " + templateId);
        }

        NotificationTemplate template = templateOpt.get();
        template.setIsActive(false);
        templateRepository.save(template);
    }

    public void activateTemplate(Long templateId) {
        logger.info("Activating template: {}", templateId);

        Optional<NotificationTemplate> templateOpt = templateRepository.findById(templateId);
        if (templateOpt.isEmpty()) {
            throw new RuntimeException("Template not found: " + templateId);
        }

        NotificationTemplate template = templateOpt.get();
        template.setIsActive(true);
        templateRepository.save(template);
    }
}
