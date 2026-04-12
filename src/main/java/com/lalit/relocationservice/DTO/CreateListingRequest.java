package com.lalit.relocationservice.DTO;

import com.lalit.relocationservice.entity.ListingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateListingRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Listing type is required")
    private ListingType listingType;

    @NotBlank(message = "City is required")
    private String city;

    private String neighborhood;
    private String address;

    @NotNull(message = "Monthly rent is required")
    private Double monthlyRent;

    private Double securityDeposit;
    private Boolean isUtilitiesIncluded;
    private Boolean isFurnished;
    private LocalDate availableFrom;
    private Integer numberOfBedrooms;
    private Integer numberOfBathrooms;
    private String amenities;
    private String description;


    private Long postedBy;
}
