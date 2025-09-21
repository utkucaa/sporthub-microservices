package com.sporthub.notification.repository;

import com.sporthub.notification.entity.NotificationTemplate;
import com.sporthub.notification.enums.NotificationCategory;
import com.sporthub.notification.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {

    Optional<NotificationTemplate> findByName(String name);

    Optional<NotificationTemplate> findByTypeAndCategoryAndIsActiveTrue(NotificationType type, NotificationCategory category);

    List<NotificationTemplate> findByIsActiveTrue();

    List<NotificationTemplate> findByTypeAndIsActiveTrue(NotificationType type);

    List<NotificationTemplate> findByCategoryAndIsActiveTrue(NotificationCategory category);

    boolean existsByName(String name);
}
