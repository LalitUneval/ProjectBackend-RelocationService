package com.lalit.relocationservice.DTO;

import com.lalit.relocationservice.entity.ListingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationSearchRequest {
    private String city;
    private ListingType listingType;
    private Double maxRent;
    private Integer minBedrooms;
    private Boolean isFurnished;
    private Boolean isUtilitiesIncluded;
    private Integer page;
    private Integer size;
}
