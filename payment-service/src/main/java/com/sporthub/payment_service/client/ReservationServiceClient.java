package com.sporthub.payment_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "rezervation-service", url = "${feign.client.config.reservation-service.url:http://localhost:8084}")
public interface ReservationServiceClient {

    @GetMapping("/api/reservations/{id}")
    ReservationDto getReservationById(@PathVariable("id") Long reservationId);

    @PutMapping("/api/reservations/{id}/status")
    void updateReservationStatus(@PathVariable("id") Long reservationId, 
                                @RequestParam("status") String status);

    @PutMapping("/api/reservations/{id}/payment-status")
    void updatePaymentStatus(@PathVariable("id") Long reservationId, 
                            @RequestParam("paymentStatus") String paymentStatus);

    class ReservationDto {
        private Long id;
        private Long userId;
        private Long courtId;
        private String status;
        private String paymentStatus;
        private Double totalPrice;
        private String startTime;
        private String endTime;

        public ReservationDto() {}

        
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getCourtId() {
            return courtId;
        }

        public void setCourtId(Long courtId) {
            this.courtId = courtId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
        }

        public Double getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(Double totalPrice) {
            this.totalPrice = totalPrice;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }
    }
}
