package com.lalit.relocationservice.service;


import com.lalit.relocationservice.DTO.*;
import com.lalit.relocationservice.client.UserProfileClient;
import com.lalit.relocationservice.entity.AccommodationListing;
import com.lalit.relocationservice.exception.ListingNotFoundException;
import com.lalit.relocationservice.exception.ResourceNotFoundException;
import com.lalit.relocationservice.exception.UnauthorizedException;
import com.lalit.relocationservice.repository.AccommodationListingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AccommodationListingService {

    private final AccommodationListingRepository accommodationListingRepository;
    private final UserProfileClient userClient;


    @Caching(evict = {
            @CacheEvict(value = "relocation_listings_city", allEntries = true),
            @CacheEvict(value = "relocation_listings_latest", allEntries = true),
            @CacheEvict(value = "relocation_listings_search", allEntries = true),
            @CacheEvict(value = "relocation_listings_user", allEntries = true)
    })
    public AccommodationListingResponse createListing(CreateListingRequest request) {

        AccommodationListing listing = AccommodationListing.builder()
                .title(request.getTitle())
                .listingType(request.getListingType())
                .city(request.getCity())
                .neighborhood(request.getNeighborhood())
                .address(request.getAddress())
                .monthlyRent(request.getMonthlyRent())
                .securityDeposit(request.getSecurityDeposit())
                .isUtilitiesIncluded(request.getIsUtilitiesIncluded() != null
                        ? request.getIsUtilitiesIncluded() : false)
                .isFurnished(request.getIsFurnished() != null
                        ? request.getIsFurnished() : false)
                .availableFrom(request.getAvailableFrom())
                .numberOfBedrooms(request.getNumberOfBedrooms())
                .numberOfBathrooms(request.getNumberOfBathrooms())
                .amenities(request.getAmenities())
                .description(request.getDescription())
                .postedBy(request.getPostedBy())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        AccommodationListing savedListing = accommodationListingRepository.save(listing);

        // Fetch user profile via Feign
        UserProfileDTO userProfile = userClient.getUserById(savedListing.getPostedBy());

        return mapToResponse(savedListing, userProfile);
    }


    @Cacheable(value = "relocation_listing_id", key = "#listingId")
    @Transactional(readOnly = true)
    public AccommodationListingResponse getListing(Long listingId) {

        AccommodationListing listing = accommodationListingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException(
                        "Listing not found with id: " + listingId));

        UserProfileDTO userProfile = userClient.getUserById(listing.getPostedBy());

        log.info("Request came or not {}"+userProfile);

        return mapToResponse(listing, userProfile);
    }


    @Cacheable(value = "relocation_listings_city", key = "#city")
    @Transactional(readOnly = true)
    public List<AccommodationListingResponse> getListingsByCity(String city) {

        //  Fetch listings from repository
        List<AccommodationListing> listings =
                accommodationListingRepository.findByCity(city);

        if (listings.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No accommodation listings found for city: " + city
            );
        }

        // Extract unique userIds (avoid N+1 problem)
        List<Long> userIds = listings.stream()
                .map(AccommodationListing::getPostedBy)
                .distinct()
                .toList();

        // Fetch users in batch (Feign call)
        List<UserProfileDTO> users = userClient.getUsersByIds(userIds);

        //  Convert to Map for fast lookup
        Map<Long, UserProfileDTO> userMap = users.stream()
                .collect(Collectors.toMap(UserProfileDTO::getId, u -> u));

        // 5️⃣ Convert Entity → Response DTO
        return listings.stream()
                .map(listing -> {
                    // Get the user from the map we created
                    UserProfileDTO user = userMap.get(listing.getPostedBy());

                    return AccommodationListingResponse.builder()
                            .id(listing.getId())
                            .title(listing.getTitle())
                            // ... other fields ...
                            .postedBy(listing.getPostedBy())

                            // THIS IS THE MISSING LINE:
                            .userProfileDTO(user)

                            .build();
                })
                .toList();
    }
    @Transactional(readOnly = true)
    public Page<AccommodationListingResponse> searchListings(
            AccommodationSearchRequest searchRequest) {

        // 1️⃣ Create Pageable
        Pageable pageable = PageRequest.of(
                searchRequest.getPage(),
                searchRequest.getSize()
        );


        Page<AccommodationListing> listingPage =
                accommodationListingRepository.searchListings(
                        searchRequest.getCity(),
                        searchRequest.getListingType(),
                        searchRequest.getMaxRent(),
                        searchRequest.getMinBedrooms(),
                        searchRequest.getIsFurnished(),
                        searchRequest.getIsUtilitiesIncluded(),
                        pageable
                );


        if (listingPage.isEmpty()) {
            return Page.empty(pageable);
        }


        List<Long> userIds = listingPage.getContent().stream()
                .map(AccommodationListing::getPostedBy)
                .distinct()
                .toList();


        List<UserProfileDTO> users = userClient.getUsersByIds(userIds);


        Map<Long, UserProfileDTO> userMap = users.stream()
                .collect(Collectors.toMap(UserProfileDTO::getId, u -> u));


        return listingPage.map(listing -> {

            UserProfileDTO user = userMap.get(listing.getPostedBy());

            return AccommodationListingResponse.builder()
                    .id(listing.getId())
                    .title(listing.getTitle())
                    .listingType(listing.getListingType())
                    .city(listing.getCity())
                    .monthlyRent(listing.getMonthlyRent())
                    .isFurnished(listing.getIsFurnished())
                    .isUtilitiesIncluded(listing.getIsUtilitiesIncluded())
                    .postedBy(listing.getPostedBy())
                    .build();
        });
    }


    @Cacheable(value = "relocation_listings_user", key = "#userId")
    @Transactional(readOnly = true)
    public List<AccommodationListingResponse> getUserListings(Long userId) {

        // 🔹 Check if user exists (Feign call)
        UserProfileDTO userProfile;
        try {
            userProfile = userClient.getUserById(userId);
        } catch (Exception ex) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        // 🔹 Fetch listings
        List<AccommodationListing> listings =
                accommodationListingRepository.findByPostedBy(userId);

        if (listings.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No accommodation listings found for user id: " + userId
            );
        }

        return listings.stream()
                .map(listing -> mapToResponse(listing, userProfile))
                .toList();
    }

    @Cacheable(value = "relocation_listings_latest", key = "'latest'")
    @Transactional(readOnly = true)
    public List<AccommodationListingResponse> getLatestListings() {

        List<AccommodationListing> listings =
                accommodationListingRepository
                        .findTop10ByIsActiveTrueOrderByCreatedAtDesc();

        if (listings.isEmpty()) {
            return List.of();
        }

        // 1Extract unique user IDs
        List<Long> userIds = listings.stream()
                .map(AccommodationListing::getPostedBy)
                .distinct()
                .toList();

        // Single Feign Call (Batch API)
        List<UserProfileDTO> users = userClient.getUsersByIds(userIds);

        // Convert List → Map for fast lookup
        Map<Long, UserProfileDTO> userMap = users.stream()
                .collect(Collectors.toMap(UserProfileDTO::getId, u -> u));

        //  Map Listings with User Data
        return listings.stream()
                .map(listing ->
                        mapToResponse(
                                listing,
                                userMap.get(listing.getPostedBy())
                        ))
                .toList();
    }

    @Caching(
            put = { @CachePut(value = "relocation_listing_id", key = "#listingId") },
            evict = {
                    @CacheEvict(value = "relocation_listings_city", allEntries = true),
                    @CacheEvict(value = "relocation_listings_latest", allEntries = true),
                    @CacheEvict(value = "relocation_listings_user", allEntries = true)
            }
    )
    public AccommodationListingResponse updateListing(
            Long listingId,
            UpdateListingRequest request,
            Long userId) {

        AccommodationListing listing = accommodationListingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException(
                        "Listing not found with id: " + listingId));

        // Ownership check
        if (!listing.getPostedBy().equals(userId)) {
            throw new UnauthorizedException(
                    "You are not authorized to update this listing");
        }

        // Update only provided fields
        if (request.getTitle() != null) {
            listing.setTitle(request.getTitle());
        }
        if (request.getMonthlyRent() != null) {
            listing.setMonthlyRent(request.getMonthlyRent());
        }
        if (request.getDescription() != null) {
            listing.setDescription(request.getDescription());
        }
        if (request.getIsActive() != null) {
            listing.setIsActive(request.getIsActive());
        }

        listing.setUpdatedAt(LocalDateTime.now());

        AccommodationListing updatedListing =
                accommodationListingRepository.save(listing);

        UserProfileDTO userProfile =
                userClient.getUserById(updatedListing.getPostedBy());

        return mapToResponse(updatedListing, userProfile);
    }


    public void deactivateListing(Long listingId, Long userId) {
        AccommodationListing listing = accommodationListingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found: " + listingId));

        // Verify ownership
        if (!listing.getPostedBy().equals(userId)) {
            throw new UnauthorizedException("Not authorized to deactivate this listing");
        }

        listing.setIsActive(false);
        listing.setUpdatedAt(LocalDateTime.now());
        accommodationListingRepository.save(listing);
    }

    @Caching(evict = {
            @CacheEvict(value = "relocation_listing_id", key = "#listingId"),
            @CacheEvict(value = "relocation_listings_city", allEntries = true),
            @CacheEvict(value = "relocation_listings_latest", allEntries = true),
            @CacheEvict(value = "relocation_listings_user", allEntries = true)
    })
    public void deleteListing(Long listingId, Long userId) {
        AccommodationListing listing = accommodationListingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found: " + listingId));

        // Verify ownership
        if (!listing.getPostedBy().equals(userId)) {
            throw new UnauthorizedException("Not authorized to delete this listing");
        }

        accommodationListingRepository.delete(listing);
    }

    public long countActiveListings(String city) {
        return accommodationListingRepository.countByCityAndIsActive(city, true);
    }



    private AccommodationListingResponse mapToResponse(
            AccommodationListing listing,
            UserProfileDTO userProfile) {

        return AccommodationListingResponse.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .listingType(listing.getListingType())
                .city(listing.getCity())
                .monthlyRent(listing.getMonthlyRent())
                .isUtilitiesIncluded(listing.getIsUtilitiesIncluded())
                .isFurnished(listing.getIsFurnished())
                .postedBy(listing.getPostedBy())
                .isActive(listing.getIsActive())
                .createdAt(listing.getCreatedAt())
                .updatedAt(listing.getUpdatedAt())
                .userProfileDTO(userProfile)
                .build();
    }
}