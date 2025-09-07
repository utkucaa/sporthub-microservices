package com.sporthub.rezervation_service.service;

import com.sporthub.rezervation_service.dto.request.CreateReservationRequest;
import com.sporthub.rezervation_service.dto.request.UpdateReservationRequest;
import com.sporthub.rezervation_service.dto.response.ReservationResponse;
import com.sporthub.rezervation_service.exception.ReservationNotFoundException;
import com.sporthub.rezervation_service.exception.TimeSlotNotAvailableException;
import com.sporthub.rezervation_service.model.Reservation;
import com.sporthub.rezervation_service.model.ReservationHistory;
import com.sporthub.rezervation_service.model.ReservationStatus;
import com.sporthub.rezervation_service.repository.ReservationHistoryRepository;
import com.sporthub.rezervation_service.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationHistoryRepository historyRepository;
    private final TimeSlotValidator timeSlotValidator;
    private final RedisDistributedLock distributedLock;

    public ReservationService(ReservationRepository reservationRepository,
                              ReservationHistoryRepository historyRepository,
                              TimeSlotValidator timeSlotValidator,
                              RedisDistributedLock distributedLock) {
        this.reservationRepository = reservationRepository;
        this.historyRepository = historyRepository;
        this.timeSlotValidator = timeSlotValidator;
        this.distributedLock = distributedLock;
    }

    @Transactional
    public ReservationResponse create(CreateReservationRequest request) {
        timeSlotValidator.validateBusinessRules(request.getStartTime(), request.getEndTime());
        String lockKey = String.format("reservation:lock:court:%d:%s", request.getCourtId(), request.getStartTime());
        return distributedLock.executeWithLock(lockKey, () -> {
            boolean conflict = !reservationRepository.findConflicts(
                    request.getCourtId(), request.getStartTime(), request.getEndTime()).isEmpty();
            if (conflict) throw new TimeSlotNotAvailableException("Time slot not available");
            Reservation reservation = new Reservation();
            reservation.setUserId(request.getUserId());
            reservation.setCourtId(request.getCourtId());
            reservation.setStartTime(request.getStartTime());
            reservation.setEndTime(request.getEndTime());
            reservation.setTotalPrice(request.getTotalPrice());
            reservation.setNotes(request.getNotes());
            reservation.setStatus(ReservationStatus.PENDING);
            Reservation saved = reservationRepository.save(reservation);
            saveHistory(saved.getId(), ReservationHistory.Action.CREATED, request.getUserId(), "created");
            return toResponse(saved);
        });
    }

    @Transactional(readOnly = true)
    public ReservationResponse get(Long id) {
        return reservationRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ReservationNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getByUser(Long userId) {
        return reservationRepository.findByUserIdOrderByStartTimeDesc(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationResponse update(Long id, UpdateReservationRequest request) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
        LocalDateTime newStart = request.getStartTime() != null ? request.getStartTime() : reservation.getStartTime();
        LocalDateTime newEnd = request.getEndTime() != null ? request.getEndTime() : reservation.getEndTime();
        timeSlotValidator.validateBusinessRules(newStart, newEnd);
        String lockKey = String.format("reservation:lock:court:%d:%s", reservation.getCourtId(), newStart);
        return distributedLock.executeWithLock(lockKey, () -> {
            boolean conflict = !reservationRepository.findConflicts(reservation.getCourtId(), newStart, newEnd).stream()
                    .filter(r -> !r.getId().equals(id)).findFirst().isPresent();
            if (conflict) throw new TimeSlotNotAvailableException("Time slot not available for update");
            reservation.setStartTime(newStart);
            reservation.setEndTime(newEnd);
            if (request.getTotalPrice() != null) reservation.setTotalPrice(request.getTotalPrice());
            if (request.getNotes() != null) reservation.setNotes(request.getNotes());
            Reservation saved = reservationRepository.save(reservation);
            saveHistory(saved.getId(), ReservationHistory.Action.UPDATED, null, "updated");
            return toResponse(saved);
        });
    }

    @Transactional
    public void cancel(Long id, Long performedBy) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        saveHistory(id, ReservationHistory.Action.CANCELLED, performedBy, "cancelled");
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> availability(Long courtId, LocalDateTime start, LocalDateTime end) {
        return reservationRepository.findByCourtIdAndStartTimeBetween(courtId, start, end)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public ReservationResponse extend(Long id, LocalDateTime newEndTime, Long performedBy) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
        timeSlotValidator.validateBusinessRules(reservation.getStartTime(), newEndTime);
        String lockKey = String.format("reservation:lock:court:%d:%s", reservation.getCourtId(), reservation.getStartTime());
        return distributedLock.executeWithLock(lockKey, () -> {
            boolean conflict = !reservationRepository.findConflicts(reservation.getCourtId(), reservation.getStartTime(), newEndTime).stream()
                    .filter(r -> !r.getId().equals(id)).findFirst().isPresent();
            if (conflict) throw new TimeSlotNotAvailableException("Time slot not available for extension");
            reservation.setEndTime(newEndTime);
            Reservation saved = reservationRepository.save(reservation);
            saveHistory(id, ReservationHistory.Action.UPDATED, performedBy, "extended");
            return toResponse(saved);
        });
    }

    private void saveHistory(Long reservationId, ReservationHistory.Action action, Long performedBy, String details) {
        ReservationHistory history = new ReservationHistory();
        history.setReservationId(reservationId);
        history.setAction(action);
        history.setPerformedBy(performedBy);
        history.setDetails(details);
        historyRepository.save(history);
    }

    private ReservationResponse toResponse(Reservation r) {
        ReservationResponse resp = new ReservationResponse();
        resp.setId(r.getId());
        resp.setUserId(r.getUserId());
        resp.setCourtId(r.getCourtId());
        resp.setStartTime(r.getStartTime());
        resp.setEndTime(r.getEndTime());
        resp.setTotalPrice(r.getTotalPrice());
        resp.setStatus(r.getStatus());
        resp.setPaymentId(r.getPaymentId());
        resp.setNotes(r.getNotes());
        return resp;
    }
}


