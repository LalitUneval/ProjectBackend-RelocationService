package com.lalit.relocationservice.service;

import com.lalit.relocationservice.DTO.ApplicationResponse;
import com.lalit.relocationservice.DTO.CreateApplicationRequest;
import com.lalit.relocationservice.DTO.ReviewApplicationRequest;
import com.lalit.relocationservice.entity.ApplicationStatus;
import com.lalit.relocationservice.entity.ServiceProvider;
import com.lalit.relocationservice.entity.ServiceProviderApplication;
import com.lalit.relocationservice.exception.ResourceNotFoundException;
import com.lalit.relocationservice.repository.ServiceProviderApplicationRepository;
import com.lalit.relocationservice.repository.ServiceProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ApplicationService {

    private final ServiceProviderApplicationRepository applicationRepository;
    private final ServiceProviderRepository serviceProviderRepository;

    public ApplicationResponse submitApplication(CreateApplicationRequest request) {
        ServiceProviderApplication application = ServiceProviderApplication.builder()
                .applicantUserId(request.getApplicantUserId())
                .name(request.getName())
                .serviceType(request.getServiceType())
                .city(request.getCity())
                .contactNumber(request.getContactNumber())
                .email(request.getEmail())
                .description(request.getDescription())
                .status(ApplicationStatus.PENDING)
                .appliedAt(Instant.now())
                .build();

        return mapToResponse(applicationRepository.save(application));
    }

    public List<ApplicationResponse> getMyApplications(Long userId) {
        return applicationRepository
                .findByApplicantUserIdOrderByAppliedAtDesc(userId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<ApplicationResponse> getAllApplications() {
        return applicationRepository
                .findAllByOrderByAppliedAtDesc()
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<ApplicationResponse> getPendingApplications() {
        return applicationRepository
                .findByStatusOrderByAppliedAtAsc(ApplicationStatus.PENDING)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Approve application and auto-create the ServiceProvider.
     *
     * KEY FIX: We now pass applicantUserId as ownerUserId on the ServiceProvider.
     * This links Raj's user account to the provider profile so:
     *  - Self-booking guard works (Raj can't book himself)
     *  - Raj can confirm incoming bookings (ownership verified)
     *  - Frontend can detect "this user is a provider" and show the provider dashboard
     */
    public ApplicationResponse approveApplication(Long applicationId, ReviewApplicationRequest request) {
        ServiceProviderApplication application = findOrThrow(applicationId);

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalStateException("Only PENDING applications can be approved.");
        }

        application.setStatus(ApplicationStatus.APPROVED);
        application.setAdminNote(request != null ? request.getAdminNote() : null);
        application.setReviewedAt(Instant.now());
        applicationRepository.save(application);

        // Auto-create ServiceProvider with ownerUserId linked to the applicant
        ServiceProvider provider = ServiceProvider.builder()
                .name(application.getName())
                .serviceType(application.getServiceType())
                .city(application.getCity())
                .contactNumber(application.getContactNumber())
                .email(application.getEmail())
                .description(application.getDescription())
                .rating(0.0)
                .totalReviews(0)
                .isVerified(true)
                .ownerUserId(application.getApplicantUserId())  // ← KEY: links provider to user
                .build();

        serviceProviderRepository.save(provider);

        return mapToResponse(application);
    }

    public ApplicationResponse rejectApplication(Long applicationId, ReviewApplicationRequest request) {
        ServiceProviderApplication application = findOrThrow(applicationId);

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalStateException("Only PENDING applications can be rejected.");
        }

        application.setStatus(ApplicationStatus.REJECTED);
        application.setAdminNote(request != null ? request.getAdminNote() : null);
        application.setReviewedAt(Instant.now());

        return mapToResponse(applicationRepository.save(application));
    }

    private ServiceProviderApplication findOrThrow(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + id));
    }

    private ApplicationResponse mapToResponse(ServiceProviderApplication a) {
        return ApplicationResponse.builder()
                .id(a.getId())
                .applicantUserId(a.getApplicantUserId())
                .name(a.getName())
                .serviceType(a.getServiceType())
                .city(a.getCity())
                .contactNumber(a.getContactNumber())
                .email(a.getEmail())
                .description(a.getDescription())
                .status(a.getStatus())
                .adminNote(a.getAdminNote())
                .appliedAt(a.getAppliedAt())
                .reviewedAt(a.getReviewedAt())
                .build();
    }
}