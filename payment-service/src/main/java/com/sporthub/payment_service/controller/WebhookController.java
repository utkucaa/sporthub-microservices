package com.sporthub.payment_service.controller;

import com.sporthub.payment_service.service.StripeWebhookHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

    
@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    private final StripeWebhookHandler stripeWebhookHandler;

    public WebhookController(StripeWebhookHandler stripeWebhookHandler) {
        this.stripeWebhookHandler = stripeWebhookHandler;
    }

    /**
     * Handle Stripe webhook events
     */
    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature) {
        
        try {
            logger.info("Received Stripe webhook with signature: {}", signature.substring(0, 20) + "...");

            stripeWebhookHandler.processWebhookEvent(payload, signature);
            
            logger.info("Successfully processed Stripe webhook");
            return ResponseEntity.ok("Webhook processed successfully");

        } catch (IllegalArgumentException e) {
            logger.error("Invalid webhook signature: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");

        } catch (Exception e) {
            logger.error("Error processing webhook: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Webhook processing failed");
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Webhook endpoint is healthy");
    }
}
