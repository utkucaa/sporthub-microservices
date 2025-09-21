package com.sporthub.payment_service.service.impl;

import com.sporthub.payment_service.client.ReservationServiceClient;
import com.sporthub.payment_service.client.UserServiceClient;
import com.sporthub.payment_service.dto.event.PaymentEvent;
import com.sporthub.payment_service.dto.event.ReservationEvent;
import com.sporthub.payment_service.dto.request.PaymentConfirmRequest;
import com.sporthub.payment_service.dto.request.PaymentIntentRequest;
import com.sporthub.payment_service.dto.response.PaymentHistoryResponse;
import com.sporthub.payment_service.dto.response.PaymentIntentResponse;
import com.sporthub.payment_service.dto.response.PaymentResponse;
import com.sporthub.payment_service.exception.PaymentNotFoundException;
import com.sporthub.payment_service.kafka.producer.PaymentEventProducer;
import com.sporthub.payment_service.model.Payment;
import com.sporthub.payment_service.model.PaymentStatus;
import com.sporthub.payment_service.repository.PaymentRepository;
import com.sporthub.payment_service.service.PaymentService;
import com.sporthub.payment_service.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// ödeme işlemlerini yöneten ana servis
@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final StripeService stripeService;
    private final PaymentEventProducer paymentEventProducer;
    private final ReservationServiceClient reservationServiceClient;
    private final UserServiceClient userServiceClient;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                             StripeService stripeService,
                             PaymentEventProducer paymentEventProducer,
                             ReservationServiceClient reservationServiceClient,
                             UserServiceClient userServiceClient) {
        this.paymentRepository = paymentRepository;
        this.stripeService = stripeService;
        this.paymentEventProducer = paymentEventProducer;
        this.reservationServiceClient = reservationServiceClient;
        this.userServiceClient = userServiceClient;
    }

    // ödeme niyeti oluştur - stripe entegrasyonu
    @Override
    public PaymentIntentResponse createPaymentIntent(PaymentIntentRequest request, Long userId) {
        try {
            logger.info("Creating payment intent: reservationId={}, userId={}, amount={}", 
                       request.getReservationId(), userId, request.getAmount());

            if (paymentRepository.existsByReservationId(request.getReservationId())) {
                throw new IllegalStateException("Payment already exists for reservation: " + request.getReservationId());
            }

            Payment payment = new Payment(request.getReservationId(), userId, 
                                        request.getAmount(), request.getCurrency());
            payment = paymentRepository.save(payment);

            Map<String, String> metadata = new HashMap<>();
            metadata.put("payment_id", payment.getId().toString());
            metadata.put("reservation_id", request.getReservationId().toString());
            metadata.put("user_id", userId.toString());
            if (request.getMetadata() != null) {
                metadata.putAll(request.getMetadata());
            }

            PaymentIntent stripePaymentIntent = stripeService.createPaymentIntent(
                request.getAmount(), request.getCurrency(), metadata);

            payment.setStripePaymentIntentId(stripePaymentIntent.getId());
            payment.setStatus(PaymentStatus.PROCESSING);
            payment = paymentRepository.save(payment);

            PaymentEvent paymentEvent = createPaymentEvent(payment);
            paymentEventProducer.publishPaymentInitiated(paymentEvent);

            logger.info("Payment intent created successfully: paymentId={}, stripePaymentIntentId={}", 
                       payment.getId(), stripePaymentIntent.getId());

            return new PaymentIntentResponse(
                stripePaymentIntent.getId(),
                stripePaymentIntent.getClientSecret(),
                request.getAmount(),
                request.getCurrency(),
                stripePaymentIntent.getStatus()
            );

        } catch (StripeException e) {
            logger.error("Stripe error creating payment intent: reservationId={}, error={}", 
                        request.getReservationId(), e.getMessage(), e);
            throw new RuntimeException("Failed to create payment intent: " + e.getMessage(), e);
        }
    }

    // rezervasyon için otomatik ödeme oluştur
    @Override
    public void createPaymentIntentForReservation(ReservationEvent reservationEvent) {
        try {
            logger.info("Creating payment intent for reservation event: reservationId={}, userId={}", 
                       reservationEvent.getReservationId(), reservationEvent.getUserId());

            PaymentIntentRequest request = new PaymentIntentRequest(
                reservationEvent.getReservationId(),
                reservationEvent.getTotalPrice(),
                reservationEvent.getCurrency() != null ? reservationEvent.getCurrency() : "USD"
            );

            createPaymentIntent(request, reservationEvent.getUserId());

        } catch (Exception e) {
            logger.error("Error creating payment intent for reservation: reservationId={}, error={}", 
                        reservationEvent.getReservationId(), e.getMessage(), e);
            throw e;
        }
    }

    // ödemeyi onayla ve tamamla
    @Override
    public PaymentResponse confirmPayment(PaymentConfirmRequest request, Long userId) {
        try {
            logger.info("Confirming payment: paymentIntentId={}, userId={}", 
                       request.getPaymentIntentId(), userId);

            Payment payment = paymentRepository.findByStripePaymentIntentId(request.getPaymentIntentId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for intent: " + request.getPaymentIntentId()));

            // Verify user ownership
            if (!payment.getUserId().equals(userId)) {
                throw new IllegalArgumentException("Payment does not belong to user: " + userId);
            }

            // Confirm payment in Stripe
            PaymentIntent confirmedPaymentIntent = stripeService.confirmPaymentIntent(
                request.getPaymentIntentId(), request.getPaymentMethodId());

            // Update payment status based on Stripe response
            PaymentStatus newStatus = mapStripeStatusToPaymentStatus(confirmedPaymentIntent.getStatus());
            payment.setStatus(newStatus);
            payment = paymentRepository.save(payment);

            // Publish appropriate event
            PaymentEvent paymentEvent = createPaymentEvent(payment);
            if (newStatus == PaymentStatus.SUCCEEDED) {
                paymentEventProducer.publishPaymentCompleted(paymentEvent);
                
                // Update reservation payment status
                try {
                    reservationServiceClient.updatePaymentStatus(payment.getReservationId(), "PAID");
                } catch (Exception e) {
                    logger.warn("Failed to update reservation payment status: reservationId={}, error={}", 
                               payment.getReservationId(), e.getMessage());
                }
            } else if (newStatus == PaymentStatus.FAILED) {
                paymentEventProducer.publishPaymentFailed(paymentEvent);
            }

            logger.info("Payment confirmed: paymentId={}, status={}", payment.getId(), newStatus);

            return convertToPaymentResponse(payment);

        } catch (StripeException e) {
            logger.error("Stripe error confirming payment: paymentIntentId={}, error={}", 
                        request.getPaymentIntentId(), e.getMessage(), e);
            throw new RuntimeException("Failed to confirm payment: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id, Long userId) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + id));

        if (!payment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Payment does not belong to user: " + userId);
        }

        return convertToPaymentResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentHistoryResponse getPaymentsByUserId(Long userId, Pageable pageable) {
        Page<Payment> paymentsPage = paymentRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        
        List<PaymentResponse> paymentResponses = paymentsPage.getContent().stream()
            .map(this::convertToPaymentResponse)
            .collect(Collectors.toList());

        return new PaymentHistoryResponse(
            paymentResponses,
            paymentsPage.getNumber(),
            paymentsPage.getTotalPages(),
            paymentsPage.getTotalElements()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByReservationId(Long reservationId, Long userId) {
        Payment payment = paymentRepository.findByReservationId(reservationId)
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found for reservation: " + reservationId));

        if (!payment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Payment does not belong to user: " + userId);
        }

        return convertToPaymentResponse(payment);
    }

    @Override
    public Payment updatePaymentStatus(Long id, PaymentStatus status) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + id));

        payment.setStatus(status);
        return paymentRepository.save(payment);
    }

    @Override
    public Payment updatePaymentStatusByStripeId(String stripePaymentIntentId, PaymentStatus status, String failureReason) {
        Payment payment = paymentRepository.findByStripePaymentIntentId(stripePaymentIntentId)
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found for Stripe intent: " + stripePaymentIntentId));

        payment.setStatus(status);
        if (failureReason != null) {
            payment.setFailureReason(failureReason);
        }
        
        return paymentRepository.save(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentEntityById(Long id) {
        return paymentRepository.findById(id)
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentEntityByStripeId(String stripePaymentIntentId) {
        return paymentRepository.findByStripePaymentIntentId(stripePaymentIntentId)
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found for Stripe intent: " + stripePaymentIntentId));
    }

    @Override
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    private PaymentResponse convertToPaymentResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setReservationId(payment.getReservationId());
        response.setUserId(payment.getUserId());
        response.setStripePaymentIntentId(payment.getStripePaymentIntentId());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());
        response.setStatus(payment.getStatus());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setFailureReason(payment.getFailureReason());
        response.setCreatedAt(payment.getCreatedAt());
        response.setUpdatedAt(payment.getUpdatedAt());
        return response;
    }

    private PaymentEvent createPaymentEvent(Payment payment) {
        PaymentEvent event = new PaymentEvent();
        event.setPaymentId(payment.getId());
        event.setReservationId(payment.getReservationId());
        event.setUserId(payment.getUserId());
        event.setStripePaymentIntentId(payment.getStripePaymentIntentId());
        event.setAmount(payment.getAmount());
        event.setCurrency(payment.getCurrency());
        event.setStatus(payment.getStatus().name());
        event.setPaymentMethod(payment.getPaymentMethod() != null ? payment.getPaymentMethod().name() : null);
        event.setFailureReason(payment.getFailureReason());
        return event;
    }

    private PaymentStatus mapStripeStatusToPaymentStatus(String stripeStatus) {
        return switch (stripeStatus) {
            case "succeeded" -> PaymentStatus.SUCCEEDED;
            case "processing" -> PaymentStatus.PROCESSING;
            case "requires_payment_method", "requires_confirmation", "requires_action" -> PaymentStatus.PENDING;
            case "canceled" -> PaymentStatus.CANCELLED;
            default -> PaymentStatus.FAILED;
        };
    }
}
