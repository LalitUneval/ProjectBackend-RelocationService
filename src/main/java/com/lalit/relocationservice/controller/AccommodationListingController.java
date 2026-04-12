package com.lalit.relocationservice.controller;

import com.lalit.relocationservice.DTO.AccommodationListingResponse;
import com.lalit.relocationservice.DTO.AccommodationSearchRequest;
import com.lalit.relocationservice.DTO.CreateListingRequest;
import com.lalit.relocationservice.DTO.UpdateListingRequest;
import com.lalit.relocationservice.entity.ListingType;
import com.lalit.relocationservice.service.AccommodationListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/relocation/accommodations")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class AccommodationListingController {

    private final AccommodationListingService accommodationListingService;

    /**
     * Create accommodation listing
     * POST /api/relocation/accommodations
     */

    @PostMapping
    public ResponseEntity<AccommodationListingResponse> createListing(
            @Valid @RequestBody CreateListingRequest request,
            @RequestHeader("X-User-Id") Long userId) {

        // Set postedBy from header
        request.setPostedBy(userId);
        AccommodationListingResponse response = accommodationListingService.createListing(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get listing by ID
     * GET /api/relocation/accommodations/{listingId}
     */
    @GetMapping("/{listingId}")
    public ResponseEntity<AccommodationListingResponse> getListing(@PathVariable Long listingId) {
        AccommodationListingResponse response = accommodationListingService.getListing(listingId);
        return ResponseEntity.ok(response);
    }

    /**
     * Search listings with filters
     * GET /api/relocation/accommodations/search
     */
    @GetMapping("/search")
    public ResponseEntity<Page<AccommodationListingResponse>> searchListings(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String listingType,
            @RequestParam(required = false) Double maxRent,
            @RequestParam(required = false) Integer minBedrooms,
            @RequestParam(required = false) Boolean isFurnished,
            @RequestParam(required = false) Boolean isUtilitiesIncluded,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        AccommodationSearchRequest searchRequest = AccommodationSearchRequest.builder()
                .city(city)
                .listingType(listingType != null ? ListingType.valueOf(listingType) : null)
                .maxRent(maxRent)
                .minBedrooms(minBedrooms)
                .isFurnished(isFurnished)
                .isUtilitiesIncluded(isUtilitiesIncluded)
                .page(page)
                .size(size)
                .build();

        Page<AccommodationListingResponse> listings = accommodationListingService.searchListings(searchRequest);
        return ResponseEntity.ok(listings);
    }

    /**
     * Get listings by city
     * GET /api/relocation/accommodations/city/{city}
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<List<AccommodationListingResponse>> getListingsByCity(@PathVariable String city) {
        List<AccommodationListingResponse> listings = accommodationListingService.getListingsByCity(city);
        return ResponseEntity.ok(listings);
    }

    /**
     * Get user's listings
     * GET /api/relocation/accommodations/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccommodationListingResponse>> getUserListings(@PathVariable Long userId) {
        List<AccommodationListingResponse> listings = accommodationListingService.getUserListings(userId);
        return ResponseEntity.ok(listings);
    }

    /**
     * Get latest listings
     * GET /api/relocation/accommodations/latest
     */
    @GetMapping("/latest")
    public ResponseEntity<List<AccommodationListingResponse>> getLatestListings() {
        List<AccommodationListingResponse> listings = accommodationListingService.getLatestListings();
        return ResponseEntity.ok(listings);
    }

    /**
     * Update listing
     * PUT /api/relocation/accommodations/{listingId}
     */
    @PutMapping("/{listingId}")
    public ResponseEntity<AccommodationListingResponse> updateListing(
            @PathVariable Long listingId,
            @Valid @RequestBody UpdateListingRequest request,
            @RequestHeader("X-User-Id") Long userId) {

        AccommodationListingResponse response = accommodationListingService.updateListing(listingId, request, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Deactivate listing
     * PUT /api/relocation/accommodations/{listingId}/deactivate
     */
    @PutMapping("/{listingId}/deactivate")
    public ResponseEntity<Void> deactivateListing(
            @PathVariable Long listingId,
            @RequestHeader("X-User-Id") Long userId) {

        accommodationListingService.deactivateListing(listingId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Delete listing
     * DELETE /api/relocation/accommodations/{listingId}
     */
    @DeleteMapping("/{listingId}")
    public ResponseEntity<Void> deleteListing(
            @PathVariable Long listingId,
            @RequestHeader("X-User-Id") Long userId) {

        accommodationListingService.deleteListing(listingId, userId);
        return ResponseEntity.noContent().build();
    }
}
