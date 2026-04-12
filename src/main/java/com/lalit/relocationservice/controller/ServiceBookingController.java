package com.lalit.relocationservice.controller;

import com.lalit.relocationservice.DTO.CreateServiceBookingRequest;
import com.lalit.relocationservice.DTO.ServiceBookingResponse;
import com.lalit.relocationservice.service.ServiceBookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relocation/bookings")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class ServiceBookingController {

    private final ServiceBookingService serviceBookingService;

    /** User request to provider
     * Post: /api/relocation/bookings
     * ok
     * */
    @PostMapping
    public ResponseEntity<ServiceBookingResponse> createBooking(
            @Valid @RequestBody CreateServiceBookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(serviceBookingService.createBooking(request));
    }

//    /** Get single booking
//     *  Get: /api/relocation/bookings/{bookingId}
//     * */
//    @GetMapping("/{bookingId}")
//    public ResponseEntity<ServiceBookingResponse> getBooking(@PathVariable Long bookingId) {
//        return ResponseEntity.ok(serviceBookingService.getBooking(bookingId));
//    }

    /** User get own request to providers
     * Get: /api/relocation/bookings/user/{userId}
     * ok
     * */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ServiceBookingResponse>> getUserBookings(@PathVariable Long userId) {
        return ResponseEntity.ok(serviceBookingService.getUserBookings(userId));
    }

    /** User: user get his/her all upcoming booking which he booked in past and not completed
     * /api/relocation/bookings/upcoming
     * ok
     * */
    @GetMapping("/upcoming")
    public ResponseEntity<List<ServiceBookingResponse>> getUpcomingBookings(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(serviceBookingService.getUpcomingBookings(userId));
    }

//    /** User: completed bookings
//     * Get: /api/relocation/bookings/completed
//     * */
//    @GetMapping("/completed")
//    public ResponseEntity<List<ServiceBookingResponse>> getCompletedBookings(
//            @RequestHeader("X-User-Id") Long userId) {
//        return ResponseEntity.ok(serviceBookingService.getCompletedBookings(userId));
//    }

    /**
     * Provider sees all incoming bookings for their service.
     * GET /api/relocation/bookings/provider/{providerId}
     * ok
     *
     * Raj calls this to see Milan's booking request.
     */
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<ServiceBookingResponse>> getProviderBookings(
            @PathVariable Long providerId) {
        return ResponseEntity.ok(serviceBookingService.getProviderBookings(providerId));
    }

    /**
     * to check current user is provider or not
     * GET /api/relocation/bookings/my-provider
     * ok
     *
     * Returns the providerId if the user is a provider, or 404 if not.
     * Frontend uses this on load to decide whether to show the "Provider Dashboard" tab.
     */
    @GetMapping("/my-provider")
    public ResponseEntity<Long> getMyProviderId(@RequestHeader("X-User-Id") Long userId) {
        Long providerId = serviceBookingService.getProviderIdByOwner(userId);
        if (providerId == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(providerId);
    }

    /**
     * Provider confirms an incoming booking (Raj confirms Milan's request).
     * PUT /api/relocation/bookings/{bookingId}/provider-confirm
     * ok
     *
     * Sends X-User-Id header so the service can verify the caller owns the provider.
     */
    @PutMapping("/{bookingId}/provider-confirm")
    public ResponseEntity<ServiceBookingResponse> providerConfirmBooking(
            @PathVariable Long bookingId,
            @RequestHeader("X-User-Id") Long providerUserId) {
        return ResponseEntity.ok(serviceBookingService.confirmBooking(bookingId, providerUserId));
    }

//    /**
//     * Admin confirm (existing) — no ownership check
//     * PUT /api/relocation/bookings/{bookingId}/confirm
//     */
//    @PutMapping("/{bookingId}/confirm")
//    public ResponseEntity<ServiceBookingResponse> adminConfirmBooking(
//            @PathVariable Long bookingId,
//            @RequestHeader("X-User-Role") String userRole) {
//        if (!userRole.equals("ADMIN")) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        return ResponseEntity.ok(serviceBookingService.adminConfirmBooking(bookingId));
//    }

    /** Complete booking with optional rating
     * Put : /api/relocation/bookings/{bookingId}/complete
     * ok
     * */
    @PutMapping("/{bookingId}/complete")
    public ResponseEntity<ServiceBookingResponse> completeBooking(
            @PathVariable Long bookingId,
            @RequestParam(required = false) Double rating) {
        return ResponseEntity.ok(serviceBookingService.completeBooking(bookingId, rating));
    }

    /** User cancels their own booking but it present as a record in db
     * Put: /api/reolcations/bookings/{bookingId}/cancel
     * ok
     * */
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable Long bookingId,
            @RequestHeader("X-User-Id") Long userId) {
        serviceBookingService.cancelBooking(bookingId, userId);
        return ResponseEntity.noContent().build();
    }

    /** User deletes their own booking and this deletes from db
     * Delete : /api/relocation/bookings/{bookingId}
     * ok
     * */
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBooking(
            @PathVariable Long bookingId,
            @RequestHeader("X-User-Id") Long userId) {
        serviceBookingService.deleteBooking(bookingId, userId);
        return ResponseEntity.noContent().build();
    }
}
