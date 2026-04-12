package com.lalit.relocationservice.repository;

import com.lalit.relocationservice.entity.ApplicationStatus;
import com.lalit.relocationservice.entity.ServiceProviderApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceProviderApplicationRepository
        extends JpaRepository<ServiceProviderApplication, Long> {

    // All applications for a specific user
    List<ServiceProviderApplication> findByApplicantUserIdOrderByAppliedAtDesc(Long userId);

    // All applications by status (admin: see all pending)
    List<ServiceProviderApplication> findByStatusOrderByAppliedAtAsc(ApplicationStatus status);

    // All applications regardless of status (admin: full list)
    List<ServiceProviderApplication> findAllByOrderByAppliedAtDesc();
}
