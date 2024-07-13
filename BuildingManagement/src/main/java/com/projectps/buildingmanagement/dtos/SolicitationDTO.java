package com.projectps.buildingmanagement.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SolicitationDTO {

    private UUID id;
    private LocalDateTime date;
    private UUID user_id;
    private UUID property_id;
}
