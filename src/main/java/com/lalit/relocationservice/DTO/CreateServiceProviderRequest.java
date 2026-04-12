package com.lalit.relocationservice.DTO;

import com.lalit.relocationservice.entity.ServiceType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateServiceProviderRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Service type is required")
    private ServiceType serviceType;

    @NotBlank(message = "City is required")
    private String city;

    private String contactNumber;

    @Email(message = "Invalid email format")
    private String email;

    private String description;
    private Boolean isVerified;
}
