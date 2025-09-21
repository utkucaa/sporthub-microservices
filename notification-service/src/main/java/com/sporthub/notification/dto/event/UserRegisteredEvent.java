package com.sporthub.notification.dto.event;

import java.time.LocalDateTime;

/**
 * User Registered Event DTO
 */
public class UserRegisteredEvent {

    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDateTime registeredAt;

    // Default constructor
    public UserRegisteredEvent() {}

    // Constructor
    public UserRegisteredEvent(Long userId, String email, String firstName, String lastName,
                              String phoneNumber, LocalDateTime registeredAt) {
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.registeredAt = registeredAt;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }
}
