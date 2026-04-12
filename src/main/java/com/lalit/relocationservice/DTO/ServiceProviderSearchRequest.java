package com.lalit.relocationservice.DTO;

import com.lalit.relocationservice.entity.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProviderSearchRequest {
    private String city;
    private ServiceType serviceType;
    private Boolean isVerified;
    private Double minRating;
}