package com.lalit.relocationservice.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lalit.relocationservice.entity.ListingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // if the some filed null then it will ignore this value
public class AccommodationListingResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private ListingType listingType;
    private String city;
    private Double monthlyRent;
    private Boolean isUtilitiesIncluded;
    private Boolean isFurnished;
    private Long postedBy;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserProfileDTO userProfileDTO;

}

