package com.lalit.relocationservice.DTO;

import jakarta.validation.constraints.NotNull;
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
public class CreateServiceBookingRequest {


    private Long userId;


    private Long serviceProviderId;

    @NotNull(message = "Booking date is required")
    private Instant bookingDate;

    private String notes;
}
