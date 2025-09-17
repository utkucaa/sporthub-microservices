package com.sporthub.payment_service.service.impl;

import com.sporthub.payment_service.dto.event.RefundEvent;
import com.sporthub.payment_service.dto.event.ReservationEvent;
import com.sporthub.payment_service.dto.request.RefundRequest;
import com.sporthub.payment_service.dto.response.RefundResponse;
import com.sporthub.payment_service.exception.PaymentNotFoundException;
import com.sporthub.payment_service.exception.RefundNotFoundException;
import com.sporthub.payment_service.kafka.producer.PaymentEventProducer;
import com.sporthub.payment_service.model.Payment;
import com.sporthub.payment_service.model.PaymentStatus;
import com.sporthub.payment_service.model.Refund;
import com.sporthub.payment_service.model.RefundStatus;
import com.sporthub.payment_service.repository.RefundRepository;
import com.sporthub.payment_service.service.PaymentService;
import com.sporthub.payment_service.service.RefundService;
import com.sporthub.payment_service.service.StripeService;
import com.stripe.exception.StripeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of RefundService
 */
@Service
@Transactional
public class RefundServiceImpl implements RefundService {

    private static final Logger logger = LoggerFactory.getLogger(RefundServiceImpl.class);

    private final RefundRepository refundRepository;
    private final PaymentService paymentService;
    private final StripeService stripeService;
    private final PaymentEventProducer paymentEventProducer;

    public RefundServiceImpl(RefundRepository refundRepository,
                            PaymentService paymentService,
                            StripeService stripeService,
                            PaymentEventProducer paymentEventProducer) {
        this.refundRepository = refundRepository;
        this.paymentService = paymentService;
        this.stripeService = stripeService;
        this.paymentEventProducer = paymentEventProducer;
    }

    @Override
    public RefundResponse processRefund(RefundRequest request, Long userId) {
        try {
            logger.info("Processing refund request: paymentId={}, userId={}, amount={}", 
                       request.getPaymentId(), userId, request.getAmount());

            // Get payment
            Payment payment = paymentService.getPaymentEntityById(request.getPaymentId());

            // Verify user ownership
            if (!payment.getUserId().equals(userId)) {
                throw new IllegalArgumentException("Payment does not belong to user: " + userId);
            }

            // Verify payment is refundable
            validateRefundEligibility(payment, request.getAmount());

            // Determine refund amount (full refund if amount not specified)
            BigDecimal refundAmount = request.getAmount() != null ? 
                request.getAmount() : payment.getAmount();

            // Create refund entity
            Refund refund = new Refund(payment, refundAmount, request.getReason(), "USER_" + userId);
            refund = refundRepository.save(refund);

            // Process refund in Stripe
            com.stripe.model.Refund stripeRefund = stripeService.createRefund(
                payment.getStripePaymentIntentId(), refundAmount, request.getReason());

            // Update refund with Stripe refund ID
            refund.setStripeRefundId(stripeRefund.getId());
            refund.setStatus(mapStripeStatusToRefundStatus(stripeRefund.getStatus()));
            refund.setProcessedAt(LocalDateTime.now());
            refund = refundRepository.save(refund);

            // Update payment status if fully refunded
            BigDecimal totalRefunded = refundRepository.getTotalRefundedAmountByPaymentId(payment.getId());
            if (totalRefunded.compareTo(payment.getAmount()) >= 0) {
                paymentService.updatePaymentStatus(payment.getId(), PaymentStatus.REFUNDED);
            }

            // Publish refund completed event
            RefundEvent refundEvent = createRefundEvent(refund);
            paymentEventProducer.publishRefundCompleted(refundEvent);

            logger.info("Refund processed successfully: refundId={}, stripeRefundId={}", 
                       refund.getId(), stripeRefund.getId());

            return convertToRefundResponse(refund);

        } catch (StripeException e) {
            logger.error("Stripe error processing refund: paymentId={}, error={}", 
                        request.getPaymentId(), e.getMessage(), e);
            throw new RuntimeException("Failed to process refund: " + e.getMessage(), e);
        }
    }

    @Override
    public void processAutomaticRefund(ReservationEvent reservationEvent) {
        try {
            logger.info("Processing automatic refund for cancelled reservation: reservationId={}", 
                       reservationEvent.getReservationId());

            // Find payment for the reservation
            Payment payment = paymentService.getPaymentEntityById(reservationEvent.getReservationId());

            // Only refund if payment was successful
            if (payment.getStatus() != PaymentStatus.SUCCEEDED) {
                logger.info("Skipping refund for non-successful payment: paymentId={}, status={}", 
                           payment.getId(), payment.getStatus());
                return;
            }

            // Create refund request
            RefundRequest refundRequest = new RefundRequest();
            refundRequest.setPaymentId(payment.getId());
            refundRequest.setReason("Automatic refund for cancelled reservation");

            // Process the refund
            processRefund(refundRequest, payment.getUserId());

        } catch (PaymentNotFoundException e) {
            logger.warn("No payment found for cancelled reservation: reservationId={}", 
                       reservationEvent.getReservationId());
        } catch (Exception e) {
            logger.error("Error processing automatic refund: reservationId={}, error={}", 
                        reservationEvent.getReservationId(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RefundResponse> getRefundsByPaymentId(Long paymentId, Long userId) {
        // Verify payment belongs to user
        Payment payment = paymentService.getPaymentEntityById(paymentId);
        if (!payment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Payment does not belong to user: " + userId);
        }

        List<Refund> refunds = refundRepository.findByPaymentIdOrderByCreatedAtDesc(paymentId);
        return refunds.stream()
            .map(this::convertToRefundResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RefundResponse getRefundById(Long refundId, Long userId) {
        Refund refund = refundRepository.findById(refundId)
            .orElseThrow(() -> new RefundNotFoundException("Refund not found: " + refundId));

        // Verify refund belongs to user
        if (!refund.getPayment().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Refund does not belong to user: " + userId);
        }

        return convertToRefundResponse(refund);
    }

    @Override
    public Refund updateRefundStatusByStripeId(String stripeRefundId, String status) {
        Refund refund = refundRepository.findByStripeRefundId(stripeRefundId)
            .orElseThrow(() -> new RefundNotFoundException("Refund not found for Stripe refund: " + stripeRefundId));

        RefundStatus refundStatus = mapStripeStatusToRefundStatus(status);
        refund.setStatus(refundStatus);
        refund.setProcessedAt(LocalDateTime.now());

        return refundRepository.save(refund);
    }

    @Override
    @Transactional(readOnly = true)
    public Refund getRefundEntityById(Long refundId) {
        return refundRepository.findById(refundId)
            .orElseThrow(() -> new RefundNotFoundException("Refund not found: " + refundId));
    }

    @Override
    public Refund saveRefund(Refund refund) {
        return refundRepository.save(refund);
    }

    private void validateRefundEligibility(Payment payment, BigDecimal requestedAmount) {
        // Check if payment is in a refundable state
        if (payment.getStatus() != PaymentStatus.SUCCEEDED) {
            throw new IllegalStateException("Payment is not in a refundable state: " + payment.getStatus());
        }

        // Check if there's already a full refund
        BigDecimal totalRefunded = refundRepository.getTotalRefundedAmountByPaymentId(payment.getId());
        if (totalRefunded.compareTo(payment.getAmount()) >= 0) {
            throw new IllegalStateException("Payment has already been fully refunded");
        }

        // Check if requested amount is valid
        if (requestedAmount != null) {
            BigDecimal availableAmount = payment.getAmount().subtract(totalRefunded);
            if (requestedAmount.compareTo(availableAmount) > 0) {
                throw new IllegalArgumentException("Requested refund amount exceeds available amount: " + availableAmount);
            }
        }
    }

    private RefundResponse convertToRefundResponse(Refund refund) {
        RefundResponse response = new RefundResponse();
        response.setId(refund.getId());
        response.setPaymentId(refund.getPayment().getId());
        response.setStripeRefundId(refund.getStripeRefundId());
        response.setAmount(refund.getAmount());
        response.setReason(refund.getReason());
        response.setStatus(refund.getStatus());
        response.setInitiatedBy(refund.getInitiatedBy());
        response.setCreatedAt(refund.getCreatedAt());
        response.setProcessedAt(refund.getProcessedAt());
        return response;
    }

    private RefundEvent createRefundEvent(Refund refund) {
        RefundEvent event = new RefundEvent();
        event.setRefundId(refund.getId());
        event.setPaymentId(refund.getPayment().getId());
        event.setReservationId(refund.getPayment().getReservationId());
        event.setUserId(refund.getPayment().getUserId());
        event.setStripeRefundId(refund.getStripeRefundId());
        event.setAmount(refund.getAmount());
        event.setReason(refund.getReason());
        event.setStatus(refund.getStatus().name());
        event.setInitiatedBy(refund.getInitiatedBy());
        return event;
    }

    private RefundStatus mapStripeStatusToRefundStatus(String stripeStatus) {
        return switch (stripeStatus) {
            case "succeeded" -> RefundStatus.SUCCEEDED;
            case "pending" -> RefundStatus.PENDING;
            case "failed" -> RefundStatus.FAILED;
            case "canceled" -> RefundStatus.CANCELLED;
            default -> RefundStatus.FAILED;
        };
    }
}
