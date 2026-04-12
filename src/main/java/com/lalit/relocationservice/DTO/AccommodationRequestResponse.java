package com.lalit.relocationservice.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lalit.relocationservice.entity.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccommodationRequestResponse {
    private Long id;
    private Long userId;
    private Long listingId;
    private String listingTitle;
    private String listingCity;
    private Double monthlyRent;
    private RequestStatus status;
    private LocalDate moveInDate;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
