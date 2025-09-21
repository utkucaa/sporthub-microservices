package com.sporthub.notification.service;

import com.sporthub.notification.dto.request.UpdatePreferencesRequest;
import com.sporthub.notification.dto.response.PreferencesResponse;
import com.sporthub.notification.entity.NotificationPreferences;
import com.sporthub.notification.enums.NotificationCategory;
import com.sporthub.notification.enums.NotificationType;
import com.sporthub.notification.repository.NotificationPreferencesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class NotificationPreferencesService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationPreferencesService.class);

    @Autowired
    private NotificationPreferencesRepository preferencesRepository;

    @Transactional(readOnly = true)
    public PreferencesResponse getUserPreferences(Long userId) {
        Optional<NotificationPreferences> preferencesOpt = preferencesRepository.findByUserId(userId);
        
        if (preferencesOpt.isPresent()) {
            return mapToResponse(preferencesOpt.get());
        } else {
            NotificationPreferences defaultPreferences = createDefaultPreferences(userId);
            return mapToResponse(defaultPreferences);
        }
    }

    // kullanıcı tercihlerini güncelle
    public PreferencesResponse updateUserPreferences(Long userId, UpdatePreferencesRequest request) {
        logger.info("Updating preferences for user: {}", userId);

        NotificationPreferences preferences = preferencesRepository.findByUserId(userId)
            .orElse(new NotificationPreferences(userId));

        // null olmayan değerleri güncelle
        if (request.getEmailEnabled() != null) {
            preferences.setEmailEnabled(request.getEmailEnabled());
        }
        if (request.getSmsEnabled() != null) {
            preferences.setSmsEnabled(request.getSmsEnabled());
        }
        if (request.getPushEnabled() != null) {
            preferences.setPushEnabled(request.getPushEnabled());
        }
        if (request.getMarketingEnabled() != null) {
            preferences.setMarketingEnabled(request.getMarketingEnabled());
        }
        if (request.getReservationReminders() != null) {
            preferences.setReservationReminders(request.getReservationReminders());
        }
        if (request.getPaymentNotifications() != null) {
            preferences.setPaymentNotifications(request.getPaymentNotifications());
        }
        if (request.getReminderHours() != null) {
            preferences.setReminderHours(request.getReminderHours());
        }

        preferences = preferencesRepository.save(preferences);
        logger.info("Preferences updated for user: {}", userId);

        return mapToResponse(preferences);
    }

    // varsayılan tercihler oluştur
    public NotificationPreferences createDefaultPreferences(Long userId) {
        logger.info("Creating default preferences for user: {}", userId);

        NotificationPreferences preferences = new NotificationPreferences(userId);
        return preferencesRepository.save(preferences);
    }

    // bildirim izni kontrol et
    @Transactional(readOnly = true)
    public boolean isNotificationAllowed(Long userId, NotificationCategory category, NotificationType type) {
        Optional<NotificationPreferences> preferencesOpt = preferencesRepository.findByUserId(userId);
        
        if (preferencesOpt.isEmpty()) {
            return true;
        }

        NotificationPreferences preferences = preferencesOpt.get();

        switch (type) {
            case EMAIL:
                if (!preferences.getEmailEnabled()) {
                    return false;
                }
                break;
            case SMS:
                if (!preferences.getSmsEnabled()) {
                    return false;
                }
                break;
            case PUSH:
                if (!preferences.getPushEnabled()) {
                    return false;
                }
                break;
            case IN_APP:
                break;
        }

        switch (category) {
            case MARKETING:
                return preferences.getMarketingEnabled();
            case RESERVATION_REMINDER:
                return preferences.getReservationReminders();
            case PAYMENT_CONFIRMATION:
                return preferences.getPaymentNotifications();
            case RESERVATION_CONFIRMATION:
            case RESERVATION_CANCELLATION:
            case REFUND_NOTIFICATION:
            case USER_WELCOME:
            case SYSTEM_ALERT:
                return true;
            default:
                return true;
        }
    }

    @Transactional(readOnly = true)
    public Integer getReminderHours(Long userId) {
        return preferencesRepository.findByUserId(userId)
            .map(NotificationPreferences::getReminderHours)
            .orElse(1);
    }

    private PreferencesResponse mapToResponse(NotificationPreferences preferences) {
        PreferencesResponse response = new PreferencesResponse();
        response.setId(preferences.getId());
        response.setUserId(preferences.getUserId());
        response.setEmailEnabled(preferences.getEmailEnabled());
        response.setSmsEnabled(preferences.getSmsEnabled());
        response.setPushEnabled(preferences.getPushEnabled());
        response.setMarketingEnabled(preferences.getMarketingEnabled());
        response.setReservationReminders(preferences.getReservationReminders());
        response.setPaymentNotifications(preferences.getPaymentNotifications());
        response.setReminderHours(preferences.getReminderHours());
        return response;
    }
}
