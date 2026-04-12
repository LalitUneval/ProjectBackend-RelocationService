package com.lalit.relocationservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "service_provider_applications")
public class ServiceProviderApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The user who submitted this application
    @Column(nullable = false)
    private Long applicantUserId;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;   // PENDING, APPROVED, REJECTED

    private String adminNote;           // optional rejection/approval note from admin

    @Column(nullable = false)
    private Instant appliedAt;

    private Instant reviewedAt;  // set when admin acts
}



