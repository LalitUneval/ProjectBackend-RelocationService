package com.lalit.relocationservice.controller;

import com.lalit.relocationservice.DTO.AccommodationRequestResponse;
import com.lalit.relocationservice.DTO.CreateAccommodationRequestRequest;
import com.lalit.relocationservice.service.AccommodationRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relocation")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class AccommodationRequestController {

    private final AccommodationRequestService accommodationRequestService;

    /**
     * Create accommodation request
     * POST /api/relocation/accommodations/{listingId}/request
     */
    @PostMapping("/accommodations/{listingId}/request")
    public ResponseEntity<AccommodationRequestResponse> createRequest(
            @PathVariable Long listingId,
            @Valid @RequestBody CreateAccommodationRequestRequest request,
            @RequestHeader("X-User-Id") Long userId) {

        request.setUserId(userId);
        request.setListingId(listingId);
        AccommodationRequestResponse response = accommodationRequestService.createRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get pending requests for owner
     * GET /api/relocation/requests/owner/pending
     */
    @GetMapping("/requests/owner/pending")
    public ResponseEntity<List<AccommodationRequestResponse>> getPendingRequestsForOwner(
            @RequestHeader("X-User-Id") Long ownerId) {

        List<AccommodationRequestResponse> requests = accommodationRequestService.getPendingRequestsForOwner(ownerId);
        return ResponseEntity.ok(requests);
    }


    /**
     * Get request by ID
     * GET /api/relocation/requests/{requestId}
     */
    @GetMapping("/requests/{requestId}")
    public ResponseEntity<AccommodationRequestResponse> getRequest(@PathVariable Long requestId) {
        AccommodationRequestResponse response = accommodationRequestService.getRequest(requestId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user's requests
     * GET /api/relocation/requests/user/{userId}
     */
    @GetMapping("/requests/user/{userId}")
    public ResponseEntity<List<AccommodationRequestResponse>> getUserRequests(
            @PathVariable Long userId,
            @RequestHeader("X-User-Id") Long authUserId) {

        if (!userId.equals(authUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<AccommodationRequestResponse> requests = accommodationRequestService.getUserRequests(userId);
        return ResponseEntity.ok(requests);
    }

    /**
     * Get requests for owner's listings
     * GET /api/relocation/requests/owner
     */
    @GetMapping("/requests/owner")
    public ResponseEntity<List<AccommodationRequestResponse>> getRequestsForOwner(
            @RequestHeader("X-User-Id") Long ownerId) {

        List<AccommodationRequestResponse> requests = accommodationRequestService.getRequestsForOwner(ownerId);
        return ResponseEntity.ok(requests);
    }


    /**
     * Approve request
     * PUT /api/relocation/requests/{requestId}/approve
     */
    @PutMapping("/requests/{requestId}/approve")
    public ResponseEntity<AccommodationRequestResponse> approveRequest(
            @PathVariable Long requestId,
            @RequestHeader("X-User-Id") Long ownerId) {

        AccommodationRequestResponse response = accommodationRequestService.approveRequest(requestId, ownerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Reject request
     * PUT /api/relocation/requests/{requestId}/reject
     */
    @PutMapping("/requests/{requestId}/reject")
    public ResponseEntity<AccommodationRequestResponse> rejectRequest(
            @PathVariable Long requestId,
            @RequestHeader("X-User-Id") Long ownerId) {

        AccommodationRequestResponse response = accommodationRequestService.rejectRequest(requestId, ownerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Withdraw request
     * PUT /api/relocation/requests/{requestId}/withdraw
     */
    @PutMapping("/requests/{requestId}/withdraw")
    public ResponseEntity<Void> withdrawRequest(
            @PathVariable Long requestId,
            @RequestHeader("X-User-Id") Long userId) {

        accommodationRequestService.withdrawRequest(requestId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Delete request
     * DELETE /api/relocation/requests/{requestId}
     */
    @DeleteMapping("/requests/{requestId}")
    public ResponseEntity<Void> deleteRequest(
            @PathVariable Long requestId,
            @RequestHeader("X-User-Id") Long userId) {

        accommodationRequestService.deleteRequest(requestId, userId);
        return ResponseEntity.noContent().build();
    }
}

