package com.lalit.relocationservice.repository;

import com.lalit.relocationservice.entity.ServiceBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.time.Instant;

public interface ServiceBookingRepository extends JpaRepository<ServiceBooking, Long> {

    List<ServiceBooking> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<ServiceBooking> findByServiceProviderIdOrderByBookingDateDesc(Long serviceProviderId);

    @Query("SELECT b FROM ServiceBooking b " +
            "WHERE b.userId = :userId " +
            "AND b.bookingDate > :now " +
            "AND b.status IN ('PENDING', 'CONFIRMED') " +
            "ORDER BY b.bookingDate ASC")
    List<ServiceBooking> findUpcomingBookings(
            @Param("userId") Long userId,
            @Param("now") Instant now);

    @Query("SELECT b FROM ServiceBooking b " +
            "WHERE b.userId = :userId " +
            "AND b.status = com.lalit.relocationservice.entity.BookingStatus.COMPLETED " +
            "ORDER BY b.completedAt DESC")
    List<ServiceBooking> findCompletedBookings(@Param("userId") Long userId);

    /**
     * NEW: Availability conflict check.
     *
     * Returns true if the provider already has a PENDING or CONFIRMED booking
     * with a bookingDate that falls inside [windowStart, windowEnd].
     *
     * The 1-hour window is applied by the service layer before calling this:
     *   windowStart = requestedTime - 1 hour
     *   windowEnd   = requestedTime + 1 hour
     *
     * This prevents double-bookings and back-to-back slots with no buffer.
     */
    @Query("SELECT COUNT(b) > 0 FROM ServiceBooking b " +
            "WHERE b.serviceProvider.id = :providerId " +
            "AND b.status IN ('PENDING', 'CONFIRMED') " +
            "AND b.bookingDate BETWEEN :windowStart AND :windowEnd")
    boolean existsConflictingBooking(
            @Param("providerId") Long providerId,
            @Param("windowStart") Instant windowStart,
            @Param("windowEnd") Instant windowEnd);

    long countByUserId(Long userId);
    long countByServiceProviderId(Long serviceProviderId);
}

