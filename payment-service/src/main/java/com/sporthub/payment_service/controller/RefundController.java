package com.sporthub.payment_service.controller;

import com.sporthub.payment_service.dto.request.RefundRequest;
import com.sporthub.payment_service.dto.response.RefundResponse;
import com.sporthub.payment_service.service.RefundService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/payments")
public class RefundController {

    private static final Logger logger = LoggerFactory.getLogger(RefundController.class);

    private final RefundService refundService;

    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    
    @PostMapping("/{id}/refund")
    public ResponseEntity<RefundResponse> processRefund(
            @PathVariable("id") Long paymentId,
            @Valid @RequestBody RefundRequest request,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        
        
        request.setPaymentId(paymentId);
        
        logger.info("Processing refund: userId={}, paymentId={}, amount={}", 
                   userId, paymentId, request.getAmount());

        RefundResponse response = refundService.processRefund(request, userId);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    
    @GetMapping("/{id}/refunds")
    public ResponseEntity<List<RefundResponse>> getRefundsByPaymentId(
            @PathVariable("id") Long paymentId,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        
        logger.info("Getting refunds for payment: userId={}, paymentId={}", userId, paymentId);

        List<RefundResponse> refunds = refundService.getRefundsByPaymentId(paymentId, userId);
        
        return ResponseEntity.ok(refunds);
    }

            
    @GetMapping("/refunds/{refundId}")
    public ResponseEntity<RefundResponse> getRefundById(
            @PathVariable Long refundId,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        
        logger.info("Getting refund: userId={}, refundId={}", userId, refundId);

        RefundResponse response = refundService.getRefundById(refundId, userId);
        
        return ResponseEntity.ok(response);
    }
}
