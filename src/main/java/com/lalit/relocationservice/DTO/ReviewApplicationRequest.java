package com.lalit.relocationservice.DTO;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewApplicationRequest {
    // Optional note from admin explaining the decision
    private String adminNote;
}