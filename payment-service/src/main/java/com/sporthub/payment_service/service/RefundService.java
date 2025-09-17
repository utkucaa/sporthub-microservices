package com.sporthub.payment_service.service;

import com.sporthub.payment_service.dto.event.ReservationEvent;
import com.sporthub.payment_service.dto.request.RefundRequest;
import com.sporthub.payment_service.dto.response.RefundResponse;
import com.sporthub.payment_service.model.Refund;

import java.util.List;

public interface RefundService {

    RefundResponse processRefund(RefundRequest request, Long userId);

    void processAutomaticRefund(ReservationEvent reservationEvent);

    List<RefundResponse> getRefundsByPaymentId(Long paymentId, Long userId);

    RefundResponse getRefundById(Long refundId, Long userId);

    Refund updateRefundStatusByStripeId(String stripeRefundId, String status);

    Refund getRefundEntityById(Long refundId);

    Refund saveRefund(Refund refund);
}
