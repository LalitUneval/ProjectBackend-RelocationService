package com.lalit.relocationservice.DTO;

import com.lalit.relocationservice.entity.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProviderResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private ServiceType serviceType;
    private String city;
    private String contactNumber;
    private String email;
    private String description;
    private Double rating;
    private Integer totalReviews;
    private Boolean isVerified;
}