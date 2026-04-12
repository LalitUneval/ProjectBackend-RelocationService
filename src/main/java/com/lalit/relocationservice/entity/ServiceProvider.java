package com.lalit.relocationservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "service_providers")
public class ServiceProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;

    @Column(nullable = false)
    private String city;

    private String contactNumber;
    private String email;

    @Column(length = 2000)
    private String description;

    @Builder.Default
    private Double rating = 0.0;

    @Builder.Default
    private Integer totalReviews = 0;

    @Builder.Default
    private Boolean isVerified = false;

    /**
     * NEW FIELD: The userId of the user who applied and owns this provider profile.
     *
     * Set automatically when ApplicationService.approveApplication() creates
     * the ServiceProvider — it passes the applicantUserId here so we always
     * know who "owns" this provider account.
     *
     * Used for:
     *  1. Self-booking guard  — block userId == ownerUserId from booking
     *  2. Provider confirm    — only ownerUserId can confirm incoming bookings
     *  3. Provider dashboard  — frontend uses this to show "My Incoming Bookings" tab
     *
     * Nullable because admin-created providers (direct POST) may not have an owner.
     */
    @Column(name = "owner_user_id")
    private Long ownerUserId;

    @OneToMany(mappedBy = "serviceProvider", cascade = CascadeType.ALL)
    private List<ServiceBooking> bookings;
}
