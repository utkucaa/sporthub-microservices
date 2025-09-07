package com.sporthub.rezervation_service.service;

import com.sporthub.rezervation_service.client.CourtServiceClient;
import com.sporthub.rezervation_service.client.UserServiceClient;
import com.sporthub.rezervation_service.dto.request.CreateReservationRequest;
import org.springframework.stereotype.Service;

@Service
public class ReservationValidationService {
    private final UserServiceClient userServiceClient;
    private final CourtServiceClient courtServiceClient;
    private final TimeSlotValidator timeSlotValidator;

    public ReservationValidationService(UserServiceClient userServiceClient,
                                        CourtServiceClient courtServiceClient,
                                        TimeSlotValidator timeSlotValidator) {
        this.userServiceClient = userServiceClient;
        this.courtServiceClient = courtServiceClient;
        this.timeSlotValidator = timeSlotValidator;
    }

    public void validateCreate(CreateReservationRequest request) {
        timeSlotValidator.validateBusinessRules(request.getStartTime(), request.getEndTime());
        userServiceClient.getUser(request.getUserId());
        courtServiceClient.getCourt(request.getCourtId());
    }
}


