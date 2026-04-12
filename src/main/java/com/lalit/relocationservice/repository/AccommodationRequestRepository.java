package com.lalit.relocationservice.repository;

import com.lalit.relocationservice.entity.AccommodationListing;
import com.lalit.relocationservice.entity.AccommodationRequest;
import com.lalit.relocationservice.entity.RequestStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccommodationRequestRepository extends JpaRepository<AccommodationRequest,Long> {

    // Find requests by user ID
    List<AccommodationRequest> findByUserId(Long userId);

    // Find requests by listing
    List<AccommodationRequest> findByListing(AccommodationListing listing);

    // Find requests by listing ID
    List<AccommodationRequest> findByListingId(Long listingId);

    // Find requests by status
    List<AccommodationRequest> findByStatus(RequestStatus status);

    // Find requests by user ID and status
    List<AccommodationRequest> findByUserIdAndStatus(Long userId, RequestStatus status);

    // Find requests by listing ID and status
    List<AccommodationRequest> findByListingIdAndStatus(Long listingId, RequestStatus status);

    List<AccommodationRequest> findByListingIdOrderByCreatedAtDesc(Long listingId);
    
    // Check if user already requested a listing
    boolean existsByUserIdAndListingId(Long userId, Long listingId);

    // Find request by user ID and listing ID
    Optional<AccommodationRequest> findByUserIdAndListingId(Long userId, Long listingId);

    // Find requests received by listing owner
    @Query("SELECT r FROM AccommodationRequest r WHERE r.listing.postedBy = :ownerId")
    List<AccommodationRequest> findRequestsForOwner(@Param("ownerId") Long ownerId);

    // Find pending requests for owner
    @Query("SELECT r FROM AccommodationRequest r WHERE r.listing.postedBy = :ownerId AND r.status = :status")
    List<AccommodationRequest> findRequestsForOwnerByStatus(@Param("ownerId") Long ownerId,
                                                            @Param("status") RequestStatus status);

    // Get user's requests ordered by date (most recent first)
    List<AccommodationRequest> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Count requests by user ID
    long countByUserId(Long userId);

    // Count requests by listing ID
    long countByListingId(Long listingId);

    // Count requests by status
    long countByStatus(RequestStatus status);

}
