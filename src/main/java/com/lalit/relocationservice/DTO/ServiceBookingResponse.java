package com.lalit.relocationservice.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lalit.relocationservice.entity.BookingStatus;
import com.lalit.relocationservice.entity.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceBookingResponse {
    private Long id;
    private Long userId;
    private Long serviceProviderId;
    private String serviceProviderName;
    private ServiceType serviceType;
    private BookingStatus status;
    private Instant bookingDate;
    private String notes;
    private Instant createdAt;
    private Instant completedAt;
}