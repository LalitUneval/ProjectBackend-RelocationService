package com.lalit.relocationservice.service;

import com.lalit.relocationservice.DTO.CreateServiceBookingRequest;
import com.lalit.relocationservice.DTO.ServiceBookingResponse;
import com.lalit.relocationservice.entity.BookingStatus;
import com.lalit.relocationservice.entity.ServiceBooking;
import com.lalit.relocationservice.entity.ServiceProvider;
import com.lalit.relocationservice.exception.BookingNotFoundException;
import com.lalit.relocationservice.exception.InvalidBookingException;
import com.lalit.relocationservice.exception.ProviderNotFoundException;
import com.lalit.relocationservice.exception.UnauthorizedException;
import com.lalit.relocationservice.repository.ServiceBookingRepository;
import com.lalit.relocationservice.repository.ServiceProviderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ServiceBookingService {

    private final ServiceBookingRepository serviceBookingRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServiceProviderService serviceProviderService;

    /**
     * Create a booking.
     *
     * FIX 1: Provider cannot book their own service (self-booking guard).
     * FIX 2: Provider must be verified before anyone can book them.
     * FIX 3: Availability check — no two active bookings within 1-hour window.
     */
    public ServiceBookingResponse createBooking(CreateServiceBookingRequest request) {

        log.info("Before saving Request in databased :::{} {}", request.getBookingDate());

        ServiceProvider provider = serviceProviderRepository.findById(request.getServiceProviderId())
                .orElseThrow(() -> new ProviderNotFoundException(
                        "Provider not found: " + request.getServiceProviderId()));

        // FIX 1: Block self-booking
        if (provider.getOwnerUserId() != null &&
                provider.getOwnerUserId().equals(request.getUserId())) {
            throw new InvalidBookingException("You cannot book your own service.");
        }

        // FIX 2: Block unverified provider
        if (!Boolean.TRUE.equals(provider.getIsVerified())) {
            throw new InvalidBookingException(
                    "This service provider is not yet verified and cannot be booked.");
        }

        // Validate booking date is in the future
        if (request.getBookingDate().isBefore(Instant.now())) {
            throw new InvalidBookingException("Booking date must be in the future.");
        }

// FIX 3: Availability check using ChronoUnit
        Instant windowStart = request.getBookingDate().minus(1, ChronoUnit.HOURS);
        Instant windowEnd   = request.getBookingDate().plus(1, ChronoUnit.HOURS);

        boolean alreadyBooked = serviceBookingRepository
                .existsConflictingBooking(request.getServiceProviderId(), windowStart, windowEnd);

        if (alreadyBooked) {
            throw new InvalidBookingException(
                    "This provider is not available at that time. " +
                            "Please choose a slot at least 1 hour apart from existing bookings.");
        }

        ServiceBooking booking = ServiceBooking.builder()
                .userId(request.getUserId())
                .serviceProvider(provider)
                .status(BookingStatus.PENDING)
                .bookingDate(request.getBookingDate())
                .notes(request.getNotes())
                .createdAt(Instant.now())
                .build();
        log.info("After saving booking into database {} {}{}", booking.getBookingDate());

        return mapToResponse(serviceBookingRepository.save(booking));




    }

//    public ServiceBookingResponse getBooking(Long bookingId) {
//        return mapToResponse(serviceBookingRepository.findById(bookingId)
//                .orElseThrow(() -> new BookingNotFoundException("Booking not found: " + bookingId)));
//    }

    public List<ServiceBookingResponse> getUserBookings(Long userId) {
        List<ServiceBooking> bookings =
                serviceBookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
        if (bookings.isEmpty()) throw new BookingNotFoundException("No bookings found for user: " + userId);
        return bookings.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Provider sees all incoming bookings for their service.
     * This is what Raj needs — to see Milan's booking request.
     */
    public List<ServiceBookingResponse> getProviderBookings(Long providerId) {
        List<ServiceBooking> bookings =
                serviceBookingRepository.findByServiceProviderIdOrderByBookingDateDesc(providerId);
        if (bookings.isEmpty()) throw new BookingNotFoundException("No bookings found for provider: " + providerId);
        return bookings.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Given a userId, find the provider they own (if any).
     * Used by frontend to check: "Is this user also a provider?"
     */
    public Long getProviderIdByOwner(Long userId) {
        return serviceProviderRepository.findByOwnerUserId(userId)
                .map(ServiceProvider::getId)
                .orElse(null);
    }

    public List<ServiceBookingResponse> getUpcomingBookings(Long userId) {
        List<ServiceBooking> bookings =
                serviceBookingRepository.findUpcomingBookings(userId, Instant.now());
        if (bookings.isEmpty()) throw new BookingNotFoundException("No upcoming bookings for user: " + userId);
        return bookings.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

//    public List<ServiceBookingResponse> getCompletedBookings(Long userId) {
//        List<ServiceBooking> bookings = serviceBookingRepository.findCompletedBookings(userId);
//        if (bookings.isEmpty()) throw new BookingNotFoundException("No completed bookings for user: " + userId);
//        return bookings.stream().map(this::mapToResponse).collect(Collectors.toList());
//    }

    /**
     * Provider confirms a booking (Raj confirms Milan's request).
     * Only the owner of the provider account can confirm.
     */
    public ServiceBookingResponse confirmBooking(Long bookingId, Long providerUserId) {
        ServiceBooking booking = serviceBookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found: " + bookingId));

        ServiceProvider provider = booking.getServiceProvider();
        if (provider.getOwnerUserId() != null &&
                !provider.getOwnerUserId().equals(providerUserId)) {
            throw new UnauthorizedException("Only the service provider can confirm this booking.");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new InvalidBookingException("Only pending bookings can be confirmed.");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        return mapToResponse(serviceBookingRepository.save(booking));
    }

//    /**
//     * Admin confirm — no ownership check needed
//     */
//    public ServiceBookingResponse adminConfirmBooking(Long bookingId) {
//        ServiceBooking booking = serviceBookingRepository.findById(bookingId)
//                .orElseThrow(() -> new BookingNotFoundException("Booking not found: " + bookingId));
//        if (booking.getStatus() != BookingStatus.PENDING)
//            throw new InvalidBookingException("Only pending bookings can be confirmed.");
//        booking.setStatus(BookingStatus.CONFIRMED);
//        return mapToResponse(serviceBookingRepository.save(booking));
//    }

    public ServiceBookingResponse completeBooking(Long bookingId, Double rating) {
        ServiceBooking booking = serviceBookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found: " + bookingId));
        if (booking.getStatus() != BookingStatus.CONFIRMED)
            throw new InvalidBookingException("Only confirmed bookings can be completed.");

        booking.setStatus(BookingStatus.COMPLETED);
        booking.setCompletedAt(Instant.now());
        ServiceBooking updated = serviceBookingRepository.save(booking);

        if (rating != null && rating >= 1.0 && rating <= 5.0)
            serviceProviderService.updateRating(booking.getServiceProvider().getId(), rating);

        return mapToResponse(updated);
    }

    public void cancelBooking(Long bookingId, Long userId) {
        ServiceBooking booking = serviceBookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found: " + bookingId));
        if (!booking.getUserId().equals(userId))
            throw new UnauthorizedException("Not authorized to cancel this booking.");
        if (booking.getStatus() == BookingStatus.COMPLETED)
            throw new InvalidBookingException("Cannot cancel a completed booking.");
        if (booking.getStatus() == BookingStatus.CANCELLED)
            throw new InvalidBookingException("Booking is already cancelled.");
        booking.setStatus(BookingStatus.CANCELLED);
        serviceBookingRepository.save(booking);
    }

    public void deleteBooking(Long bookingId, Long userId) {
        ServiceBooking booking = serviceBookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found: " + bookingId));
        if (!booking.getUserId().equals(userId))
            throw new UnauthorizedException("Not authorized to delete this booking.");
        serviceBookingRepository.delete(booking);
    }

    public long countUserBookings(Long userId) { return serviceBookingRepository.countByUserId(userId); }
    public long countProviderBookings(Long providerId) { return serviceBookingRepository.countByServiceProviderId(providerId); }

    private ServiceBookingResponse mapToResponse(ServiceBooking booking) {
        ServiceProvider provider = booking.getServiceProvider();
        return ServiceBookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .serviceProviderId(provider.getId())
                .serviceProviderName(provider.getName())
                .serviceType(provider.getServiceType())
                .status(booking.getStatus())
                .bookingDate(booking.getBookingDate())
                .notes(booking.getNotes())
                .createdAt(booking.getCreatedAt())
                .completedAt(booking.getCompletedAt())
                .build();
    }
}
