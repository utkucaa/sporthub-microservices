package com.sporthub.payment_service.controller;

import com.sporthub.payment_service.dto.request.PaymentConfirmRequest;
import com.sporthub.payment_service.dto.request.PaymentIntentRequest;
import com.sporthub.payment_service.dto.response.PaymentHistoryResponse;
import com.sporthub.payment_service.dto.response.PaymentIntentResponse;
import com.sporthub.payment_service.dto.response.PaymentResponse;
import com.sporthub.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-intent")
    public ResponseEntity<PaymentIntentResponse> createPaymentIntent(
            @Valid @RequestBody PaymentIntentRequest request,
            Authentication authentication) {
        
        Long userId = authentication != null ? (Long) authentication.getPrincipal() : 1L;
        
        logger.info("Creating payment intent: userId={}, reservationId={}, amount={}", 
                   userId, request.getReservationId(), request.getAmount());

        PaymentIntentResponse response = paymentService.createPaymentIntent(request, userId);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<PaymentResponse> confirmPayment(
            @Valid @RequestBody PaymentConfirmRequest request,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        
        logger.info("Confirming payment: userId={}, paymentIntentId={}", 
                   userId, request.getPaymentIntentId());

        PaymentResponse response = paymentService.confirmPayment(request, userId);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        
        logger.info("Getting payment: userId={}, paymentId={}", userId, id);

        PaymentResponse response = paymentService.getPaymentById(id, userId);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<PaymentHistoryResponse> getPaymentsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        Long authenticatedUserId = (Long) authentication.getPrincipal();
        
        if (!userId.equals(authenticatedUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        logger.info("Getting payments for user: userId={}, page={}, size={}", userId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        PaymentHistoryResponse response = paymentService.getPaymentsByUserId(userId, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<PaymentResponse> getPaymentByReservationId(
            @PathVariable Long reservationId,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        
        logger.info("Getting payment by reservation: userId={}, reservationId={}", userId, reservationId);

        PaymentResponse response = paymentService.getPaymentByReservationId(reservationId, userId);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<String> getPaymentStatus(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        
        logger.info("Getting payment status: userId={}, paymentId={}", userId, id);

        PaymentResponse response = paymentService.getPaymentById(id, userId);
        
        return ResponseEntity.ok(response.getStatus().name());
    }
}
