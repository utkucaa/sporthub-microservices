package com.sporthub.notification.repository;

import com.sporthub.notification.entity.NotificationPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationPreferencesRepository extends JpaRepository<NotificationPreferences, Long> {

    Optional<NotificationPreferences> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
