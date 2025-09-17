package com.sporthub.payment_service.service;

import com.sporthub.payment_service.dto.event.ReservationEvent;
import com.sporthub.payment_service.dto.request.PaymentConfirmRequest;
import com.sporthub.payment_service.dto.request.PaymentIntentRequest;
import com.sporthub.payment_service.dto.response.PaymentHistoryResponse;
import com.sporthub.payment_service.dto.response.PaymentIntentResponse;
import com.sporthub.payment_service.dto.response.PaymentResponse;
import com.sporthub.payment_service.model.Payment;
import com.sporthub.payment_service.model.PaymentStatus;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

    PaymentIntentResponse createPaymentIntent(PaymentIntentRequest request, Long userId);

    void createPaymentIntentForReservation(ReservationEvent reservationEvent);

    PaymentResponse confirmPayment(PaymentConfirmRequest request, Long userId);

    PaymentResponse getPaymentById(Long id, Long userId);

    PaymentHistoryResponse getPaymentsByUserId(Long userId, Pageable pageable);

    PaymentResponse getPaymentByReservationId(Long reservationId, Long userId);

    Payment updatePaymentStatus(Long id, PaymentStatus status);

    Payment updatePaymentStatusByStripeId(String stripePaymentIntentId, PaymentStatus status, String failureReason);

    Payment getPaymentEntityById(Long id);

    Payment getPaymentEntityByStripeId(String stripePaymentIntentId);

    Payment savePayment(Payment payment);
}
