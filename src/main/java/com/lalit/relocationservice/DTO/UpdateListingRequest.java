package com.lalit.relocationservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateListingRequest {
    private String title;
    private Double monthlyRent;
    private String description;
    private Boolean isActive;
}
