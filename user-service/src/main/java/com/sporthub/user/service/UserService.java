package com.sporthub.user.service;

import com.sporthub.user.dto.request.UserRegistrationRequest;
import com.sporthub.user.dto.response.UserResponse;
import com.sporthub.user.model.Role;
import com.sporthub.user.model.User;
import com.sporthub.user.model.UserProfile;
import com.sporthub.user.repository.UserProfileRepository;
import com.sporthub.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// kullanıcı işlemlerini yöneten ana servis
@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserProfileRepository userProfileRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // yeni kullanıcı oluştur
    public UserResponse createUser(UserRegistrationRequest request) {
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with email " + request.getEmail() + " already exists");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setRole(Role.USER);
        user.setIsActive(true);
        
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        User savedUser = userRepository.save(user);
        
        UserProfile profile = new UserProfile(savedUser);
        userProfileRepository.save(profile);
        
        return convertToUserResponse(savedUser);
    }
    
    public Optional<UserResponse> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToUserResponse);
    }
    
    public Optional<UserResponse> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::convertToUserResponse);
    }
    
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }
    
    public List<UserResponse> getActiveUsers() {
        return userRepository.findByIsActive(true).stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }
    
    public List<UserResponse> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }
    
    public UserResponse updateUser(Long id, UserRegistrationRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        
        User updatedUser = userRepository.save(user);
        return convertToUserResponse(updatedUser);
    }
    
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setIsActive(false);
        userRepository.save(user);
    }
    
    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setIsActive(true);
        userRepository.save(user);
    }
    
    public void changeUserRole(Long id, Role newRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setRole(newRole);
        userRepository.save(user);
    }
    
    private UserResponse convertToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getRole(),
                user.getIsActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
