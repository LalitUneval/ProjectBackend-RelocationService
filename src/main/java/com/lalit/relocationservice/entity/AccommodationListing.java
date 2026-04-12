package com.lalit.relocationservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity(name = "accommodation_listings")
public class AccommodationListing {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListingType listingType;

    @Column(nullable = false)
    private String city;

    private String neighborhood;

    private String address;

    @Column(nullable = false)
    private Double monthlyRent;

    private Double securityDeposit;

    private Boolean isUtilitiesIncluded = false;

    private Boolean isFurnished = false;

    private LocalDate availableFrom;

    private Integer numberOfBedrooms;

    private Integer numberOfBathrooms;

    //this contain the extra things like : wifi , parking etc
    private String amenities; // Comma-separated or JSON

    @Column(length = 2000)
    private String description;

    // Foreign key to UserProfile.id (person who posted the listing)
    @Column(nullable = false)
    private Long postedBy;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;


    // One-to-Many relationship with AccommodationRequest
    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL)
    private List<AccommodationRequest> requests;


}



