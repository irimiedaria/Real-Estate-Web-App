package com.projectps.buildingmanagement.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ContractDTO {

    private UUID id;
    private LocalDateTime startDate;
    private int duration;
    private String details;
    private UUID user_id;
    private UUID property_id;
}
