package com.lalit.relocationservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "accommodation_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//This will request to the accomodation Listening that rooom are present or not like they want
//first request ----> recevied by host and accept if present if not then reject
public class AccommodationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign key to UserProfile.id (person requesting accommodation)
    @Column(nullable = false)
    private Long userId;

    // Many-to-One relationship with AccommodationListing
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private AccommodationListing listing;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    private LocalDate moveInDate;

    @Column(length = 1000)
    private String message;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;
}
