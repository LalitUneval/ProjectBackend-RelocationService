package com.lalit.relocationservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateServiceProviderRequest {
    private String contactNumber;
    private String email;
    private String description;
    private Boolean isVerified;
}