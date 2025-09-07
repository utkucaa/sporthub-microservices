package com.sporthub.user.repository;

import com.sporthub.user.model.User;
import com.sporthub.user.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByKeycloakUserId(String keycloakUserId);
    
    boolean existsByEmail(String email);
    
    boolean existsByKeycloakUserId(String keycloakUserId);
    
    List<User> findByRole(Role role);
    
    List<User> findByIsActive(Boolean isActive);
    
    List<User> searchActiveUsers(@Param("searchTerm") String searchTerm);
    
    List<User> findActiveUsersByRole(@Param("role") Role role);
}
