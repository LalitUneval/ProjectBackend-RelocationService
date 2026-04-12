package com.lalit.relocationservice.repository;

import com.lalit.relocationservice.entity.AccommodationListing;
import com.lalit.relocationservice.entity.ListingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AccommodationListingRepository extends JpaRepository<AccommodationListing,Long> {


    // Find listings by city
    List<AccommodationListing> findByCity(String city);

    // Find active listings by city
    List<AccommodationListing> findByCityAndIsActive(String city, Boolean isActive);

    // Find listings by type
    List<AccommodationListing> findByListingType(ListingType listingType);

    // Find listings by posted user
    List<AccommodationListing> findByPostedBy(Long postedBy);

    // Find active listings by user
    List<AccommodationListing> findByPostedByAndIsActive(Long postedBy, Boolean isActive);

    // Find listings by city and type
    List<AccommodationListing> findByCityAndListingType(String city, ListingType listingType);

    // Find furnished listings
    List<AccommodationListing> findByIsFurnished(Boolean isFurnished);

    // Find listings with utilities included
    List<AccommodationListing> findByIsUtilitiesIncluded(Boolean isUtilitiesIncluded);

    // Find listings by rent range
    @Query("SELECT a FROM accommodation_listings a WHERE a.monthlyRent BETWEEN :minRent AND :maxRent AND a.isActive = true")
    List<AccommodationListing> findByRentRange(@Param("minRent") Double minRent, @Param("maxRent") Double maxRent);

    // Find listings by number of bedrooms
    List<AccommodationListing> findByNumberOfBedrooms(Integer numberOfBedrooms);

    // Find listings available from a certain date
    List<AccommodationListing> findByAvailableFromBefore(LocalDate date);

    // Advanced search with multiple filters (with pagination)
    @Query("SELECT a FROM accommodation_listings a WHERE " +
            "a.isActive = true AND " +
            "(:city IS NULL OR a.city = :city) AND " +
            "(:listingType IS NULL OR a.listingType = :listingType) AND " +
            "(:maxRent IS NULL OR a.monthlyRent <= :maxRent) AND " +
            "(:minBedrooms IS NULL OR a.numberOfBedrooms >= :minBedrooms) AND " +
            "(:isFurnished IS NULL OR a.isFurnished = :isFurnished) AND " +
            "(:isUtilitiesIncluded IS NULL OR a.isUtilitiesIncluded = :isUtilitiesIncluded)")
    Page<AccommodationListing> searchListings(@Param("city") String city,
                                              @Param("listingType") ListingType listingType,
                                              @Param("maxRent") Double maxRent,
                                              @Param("minBedrooms") Integer minBedrooms,
                                              @Param("isFurnished") Boolean isFurnished,
                                              @Param("isUtilitiesIncluded") Boolean isUtilitiesIncluded,
                                              Pageable pageable);

    // Find latest active listings
    List<AccommodationListing> findTop10ByIsActiveTrueOrderByCreatedAtDesc();

    // Count active listings by city
    long countByCityAndIsActive(String city, Boolean isActive);

    // Search by neighborhood
    List<AccommodationListing> findByNeighborhoodContainingIgnoreCase(String neighborhood);


}
