package com.lalit.relocationservice.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfileDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String fullName;
    private String phoneNumber;

}
