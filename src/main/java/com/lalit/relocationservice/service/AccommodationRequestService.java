package com.lalit.relocationservice.service;


import com.lalit.relocationservice.DTO.AccommodationRequestResponse;
import com.lalit.relocationservice.DTO.CreateAccommodationRequestRequest;
import com.lalit.relocationservice.entity.AccommodationListing;
import com.lalit.relocationservice.entity.AccommodationRequest;
import com.lalit.relocationservice.entity.RequestStatus;
import com.lalit.relocationservice.exception.*;
import com.lalit.relocationservice.repository.AccommodationListingRepository;
import com.lalit.relocationservice.repository.AccommodationRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
@Transactional
public class AccommodationRequestService {


    private final AccommodationListingRepository accommodationListingRepository;
    private final AccommodationRequestRepository accommodationRequestRepository;

    public AccommodationRequestResponse createRequest(CreateAccommodationRequestRequest request) {

        AccommodationListing listing = accommodationListingRepository.findById(request.getListingId())
                .orElseThrow(() -> new ListingNotFoundException("Listing not found: " + request.getListingId()));

        if (!listing.getIsActive()) {
            throw new ListingNotActiveException("Listing is not active");
        }

        // ✅ FIX: Check moveInDate must be on or after listing's availableFrom date
        if (request.getMoveInDate().isBefore(listing.getAvailableFrom())) {
            throw new InvalidRequestException(
                    "Move-in date cannot be before listing available date: " + listing.getAvailableFrom()
            );
        }

        if (accommodationRequestRepository.existsByUserIdAndListingId(request.getUserId(), request.getListingId())) {
            throw new RequestAlreadyExistsException("Already requested this listing");
        }

        if (listing.getPostedBy().equals(request.getUserId())) {
            throw new InvalidRequestException("Cannot request your own listing");
        }

        AccommodationRequest accommodationRequest = AccommodationRequest.builder()
                .userId(request.getUserId())
                .listing(listing)
                .status(RequestStatus.PENDING)
                .moveInDate(request.getMoveInDate())
                .message(request.getMessage())
                .createdAt(LocalDateTime.now())
                .build();

        AccommodationRequest savedRequest = accommodationRequestRepository.save(accommodationRequest);
        return mapToResponse(savedRequest);
    }


    //this will return only one user
    public AccommodationRequestResponse getRequest(Long requestId) {
        AccommodationRequest request = accommodationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException("Request not found: " + requestId));

        return mapToResponse(request);
    }

    // This will return all the  accommodation by single user
    //userId means person who send request for room rent
    // it will return all the room which are request by user
    public List<AccommodationRequestResponse> getUserRequests(Long userId) {
        List<AccommodationRequest> requests = accommodationRequestRepository.findByUserIdOrderByCreatedAtDesc(userId);

        if (requests.isEmpty()){
            throw new ResourceNotFoundException("Resource Not find with: "+userId);
        }
        return requests.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

// it will return all the rooms which are posted by the owners
    public List<AccommodationRequestResponse> getListingRequests(Long listingId) {
        List<AccommodationRequest> requests = accommodationRequestRepository.findByListingIdOrderByCreatedAtDesc(listingId);

        if (requests.isEmpty()){
            throw new ResourceNotFoundException("Resource Not find with: "+listingId);
        }
        return requests.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get requests received by owner (all their listings)
     */
    //this return all the user who request for the room which owned by ownerid
    public List<AccommodationRequestResponse> getRequestsForOwner(Long ownerId) {
        List<AccommodationRequest> requests = accommodationRequestRepository.findRequestsForOwner(ownerId);

        if (requests.isEmpty()){
            throw new ResourceNotFoundException("Resource Not find with: "+ownerId);
        }
        return requests.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    // it will return all the pending requests which are not approved by the owner
    public List<AccommodationRequestResponse> getPendingRequestsForOwner(Long ownerId) {
        List<AccommodationRequest> requests = accommodationRequestRepository
                .findRequestsForOwnerByStatus(ownerId, RequestStatus.PENDING);

        if (requests.isEmpty()){
            throw new ResourceNotFoundException("Resource Not find with: "+ownerId);
        }

        return requests.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AccommodationRequestResponse approveRequest(Long requestId, Long ownerId) {
        AccommodationRequest request = accommodationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException("Request not found: " + requestId));

        if (!request.getListing().getPostedBy().equals(ownerId)) {
            throw new UnauthorizedException("Not authorized to approve this request");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new InvalidRequestException("Request already processed");
        }

        request.setStatus(RequestStatus.APPROVED);
        request.setUpdatedAt(LocalDateTime.now());

        // Step 1: Mark listing as inactive
        AccommodationListing listing = request.getListing();
        listing.setIsActive(false);
        accommodationListingRepository.save(listing);

        // ✅ Step 2: Reject all other PENDING requests for same listing  <-- ADD HERE
        List<AccommodationRequest> otherRequests = accommodationRequestRepository
                .findByListingIdOrderByCreatedAtDesc(listing.getId());

        for (AccommodationRequest other : otherRequests) {
            if (!other.getId().equals(requestId) && other.getStatus() == RequestStatus.PENDING) {
                other.setStatus(RequestStatus.REJECTED);
                other.setUpdatedAt(LocalDateTime.now());
                accommodationRequestRepository.save(other);
            }
        }

        // Step 3: Save the approved request
        AccommodationRequest updatedRequest = accommodationRequestRepository.save(request);

        return mapToResponse(updatedRequest);
    }


    public AccommodationRequestResponse rejectRequest(Long requestId, Long ownerId) {
        AccommodationRequest request = accommodationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException("Request not found: " + requestId));

        // Verify ownership
        if (!request.getListing().getPostedBy().equals(ownerId)) {
            throw new UnauthorizedException("Not authorized to reject this request");
        }

        // Check if already processed
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new InvalidRequestException("Request already processed");
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setUpdatedAt(LocalDateTime.now());

        AccommodationRequest updatedRequest = accommodationRequestRepository.save(request);

        return mapToResponse(updatedRequest);
    }

    public void withdrawRequest(Long requestId, Long userId) {
        AccommodationRequest request = accommodationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException("Request not found: " + requestId));

        if (!request.getUserId().equals(userId)) {
            throw new UnauthorizedException("Not authorized to withdraw this request");
        }

        // ✅ Add this check
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new InvalidRequestException("Can only withdraw PENDING requests");
        }

        request.setStatus(RequestStatus.WITHDRAWN);
        request.setUpdatedAt(LocalDateTime.now());
        accommodationRequestRepository.save(request);
    }



    public void deleteRequest(Long requestId, Long userId) {
        AccommodationRequest request = accommodationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException("Request not found: " + requestId));

        if (!request.getUserId().equals(userId)) {
            throw new UnauthorizedException("Not authorized to delete this request");
        }

        // ✅ Add this check
        if (request.getStatus() == RequestStatus.APPROVED) {
            throw new InvalidRequestException("Cannot delete an approved request");
        }

        accommodationRequestRepository.delete(request);
    }

    /**
     * Count user's requests
     */
    public long countUserRequests(Long userId) {
        return accommodationRequestRepository.countByUserId(userId);
    }

    /**
     * Count listing's requests
     */
    public long countListingRequests(Long listingId) {
        return accommodationRequestRepository.countByListingId(listingId);
    }


    private AccommodationRequestResponse mapToResponse(AccommodationRequest request) {
        AccommodationListing listing = request.getListing();
        return AccommodationRequestResponse.builder()
                .id(request.getId())
                .userId(request.getUserId())
                .listingId(listing.getId())
                .listingTitle(listing.getTitle())
                .listingCity(listing.getCity())
                .monthlyRent(listing.getMonthlyRent())
                .status(request.getStatus())
                .moveInDate(request.getMoveInDate())
                .message(request.getMessage())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }

}
