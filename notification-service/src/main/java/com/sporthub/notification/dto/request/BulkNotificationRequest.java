package com.sporthub.notification.dto.request;

import com.sporthub.notification.enums.NotificationCategory;
import com.sporthub.notification.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Bulk Notification Request DTO
 */
public class BulkNotificationRequest {

    @NotEmpty(message = "User IDs are required")
    private List<Long> userIds;

    @NotNull(message = "Notification type is required")
    private NotificationType type;

    @NotNull(message = "Notification category is required")
    private NotificationCategory category;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private Map<String, Object> metadata;

    // Default constructor
    public BulkNotificationRequest() {}

    // Constructor
    public BulkNotificationRequest(List<Long> userIds, NotificationType type, NotificationCategory category,
                                  String title, String content) {
        this.userIds = userIds;
        this.type = type;
        this.category = category;
        this.title = title;
        this.content = content;
    }

    // Getters and Setters
    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public NotificationCategory getCategory() {
        return category;
    }

    public void setCategory(NotificationCategory category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
