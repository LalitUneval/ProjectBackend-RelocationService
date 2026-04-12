package com.lalit.relocationservice.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccommodationRequestRequest {


    private Long userId;



    private Long listingId;

    private LocalDate  moveInDate;
    private String message;
}
