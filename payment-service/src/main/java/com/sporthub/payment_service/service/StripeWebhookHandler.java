package com.sporthub.payment_service.service;

import com.sporthub.payment_service.config.StripeConfig;
import com.sporthub.payment_service.model.Payment;
import com.sporthub.payment_service.model.PaymentStatus;
import com.sporthub.payment_service.model.PaymentWebhook;
import com.sporthub.payment_service.model.Refund;
import com.sporthub.payment_service.repository.PaymentWebhookRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for handling Stripe webhook events
 */
@Service
@Transactional
public class StripeWebhookHandler {

    private static final Logger logger = LoggerFactory.getLogger(StripeWebhookHandler.class);

    private final StripeConfig stripeConfig;
    private final PaymentService paymentService;
    private final RefundService refundService;
    private final PaymentWebhookRepository webhookRepository;

    public StripeWebhookHandler(StripeConfig stripeConfig,
                               PaymentService paymentService,
                               RefundService refundService,
                               PaymentWebhookRepository webhookRepository) {
        this.stripeConfig = stripeConfig;
        this.paymentService = paymentService;
        this.refundService = refundService;
        this.webhookRepository = webhookRepository;
    }

    /**
     * Process webhook event from Stripe
     */
    public void processWebhookEvent(String payload, String signature) {
        try {
            // Verify webhook signature
            Event event = Webhook.constructEvent(payload, signature, stripeConfig.getWebhookSecret());
            
            logger.info("Processing Stripe webhook event: id={}, type={}", event.getId(), event.getType());

            // Check if event already processed
            if (webhookRepository.existsByStripeEventIdAndProcessed(event.getId(), true)) {
                logger.info("Webhook event already processed: id={}", event.getId());
                return;
            }

            // Save webhook event
            PaymentWebhook webhookRecord = new PaymentWebhook(event.getId(), event.getType(), payload);
            webhookRecord = webhookRepository.save(webhookRecord);

            try {
                // Process event based on type
                switch (event.getType()) {
                    case "payment_intent.succeeded":
                        handlePaymentIntentSucceeded(event);
                        break;
                    case "payment_intent.payment_failed":
                        handlePaymentIntentPaymentFailed(event);
                        break;
                    case "payment_intent.canceled":
                        handlePaymentIntentCanceled(event);
                        break;
                    case "charge.dispute.created":
                        handleChargeDisputeCreated(event);
                        break;
                    case "refund.created":
                        handleRefundCreated(event);
                        break;
                    case "refund.updated":
                        handleRefundUpdated(event);
                        break;
                    default:
                        logger.info("Unhandled webhook event type: {}", event.getType());
                }

                // Mark as processed
                webhookRecord.markAsProcessed();
                webhookRepository.save(webhookRecord);

                logger.info("Successfully processed webhook event: id={}, type={}", event.getId(), event.getType());

            } catch (Exception e) {
                logger.error("Error processing webhook event: id={}, type={}, error={}", 
                           event.getId(), event.getType(), e.getMessage(), e);
                
                // Mark as failed
                webhookRecord.markAsFailed(e.getMessage());
                webhookRepository.save(webhookRecord);
                
                throw e;
            }

        } catch (SignatureVerificationException e) {
            logger.error("Invalid webhook signature: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid webhook signature", e);
        } catch (Exception e) {
            logger.error("Error processing webhook: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process webhook", e);
        }
    }

    private void handlePaymentIntentSucceeded(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getData().getObject();
        
        logger.info("Processing payment intent succeeded: paymentIntentId={}", paymentIntent.getId());

        try {
            Payment payment = paymentService.updatePaymentStatusByStripeId(
                paymentIntent.getId(), PaymentStatus.SUCCEEDED, null);
            
            logger.info("Updated payment status to SUCCEEDED: paymentId={}", payment.getId());
        } catch (Exception e) {
            logger.error("Error updating payment status for succeeded intent: paymentIntentId={}, error={}", 
                        paymentIntent.getId(), e.getMessage());
            throw e;
        }
    }

    private void handlePaymentIntentPaymentFailed(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getData().getObject();
        
        logger.info("Processing payment intent failed: paymentIntentId={}", paymentIntent.getId());

        try {
            String failureReason = paymentIntent.getLastPaymentError() != null ? 
                paymentIntent.getLastPaymentError().getMessage() : "Payment failed";

            Payment payment = paymentService.updatePaymentStatusByStripeId(
                paymentIntent.getId(), PaymentStatus.FAILED, failureReason);
            
            logger.info("Updated payment status to FAILED: paymentId={}, reason={}", 
                       payment.getId(), failureReason);
        } catch (Exception e) {
            logger.error("Error updating payment status for failed intent: paymentIntentId={}, error={}", 
                        paymentIntent.getId(), e.getMessage());
            throw e;
        }
    }

    private void handlePaymentIntentCanceled(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getData().getObject();
        
        logger.info("Processing payment intent canceled: paymentIntentId={}", paymentIntent.getId());

        try {
            Payment payment = paymentService.updatePaymentStatusByStripeId(
                paymentIntent.getId(), PaymentStatus.CANCELLED, "Payment canceled");
            
            logger.info("Updated payment status to CANCELLED: paymentId={}", payment.getId());
        } catch (Exception e) {
            logger.error("Error updating payment status for canceled intent: paymentIntentId={}, error={}", 
                        paymentIntent.getId(), e.getMessage());
            throw e;
        }
    }

    private void handleChargeDisputeCreated(Event event) {
        // Handle charge dispute - this would typically involve notifying administrators
        logger.warn("Charge dispute created: eventId={}", event.getId());
        // Additional dispute handling logic can be added here
    }

    private void handleRefundCreated(Event event) {
        com.stripe.model.Refund stripeRefund = (com.stripe.model.Refund) event.getData().getObject();
        
        logger.info("Processing refund created: refundId={}", stripeRefund.getId());

        try {
            Refund refund = refundService.updateRefundStatusByStripeId(
                stripeRefund.getId(), stripeRefund.getStatus());
            
            logger.info("Updated refund status: refundId={}, status={}", 
                       refund.getId(), stripeRefund.getStatus());
        } catch (Exception e) {
            logger.error("Error updating refund status for created refund: stripeRefundId={}, error={}", 
                        stripeRefund.getId(), e.getMessage());
            // Don't throw here as the refund might not exist in our system yet
        }
    }

    private void handleRefundUpdated(Event event) {
        com.stripe.model.Refund stripeRefund = (com.stripe.model.Refund) event.getData().getObject();
        
        logger.info("Processing refund updated: refundId={}, status={}", 
                   stripeRefund.getId(), stripeRefund.getStatus());

        try {
            Refund refund = refundService.updateRefundStatusByStripeId(
                stripeRefund.getId(), stripeRefund.getStatus());
            
            logger.info("Updated refund status: refundId={}, status={}", 
                       refund.getId(), stripeRefund.getStatus());
        } catch (Exception e) {
            logger.error("Error updating refund status for updated refund: stripeRefundId={}, error={}", 
                        stripeRefund.getId(), e.getMessage());
            throw e;
        }
    }
}
