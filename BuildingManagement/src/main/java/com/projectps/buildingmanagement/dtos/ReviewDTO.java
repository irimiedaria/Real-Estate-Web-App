package com.projectps.buildingmanagement.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReviewDTO {

    private UUID id;
    private String message;
    private LocalDateTime date;
    private UUID user_id;
}
