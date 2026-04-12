package com.lalit.relocationservice.DTO;

import com.lalit.relocationservice.entity.ServiceType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateApplicationRequest {

    @NotNull(message = "User ID is required")
    private Long applicantUserId;

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
}
