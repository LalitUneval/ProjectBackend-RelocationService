package com.lalit.relocationservice.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lalit.relocationservice.entity.ApplicationStatus;
import com.lalit.relocationservice.entity.ServiceType;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationResponse {
    private Long id;
    private Long applicantUserId;
    private String name;
    private ServiceType serviceType;
    private String city;
    private String contactNumber;
    private String email;
    private String description;
    private ApplicationStatus status;
    private String adminNote;
    private Instant appliedAt;
    private Instant reviewedAt;
}
