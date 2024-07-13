package com.projectps.buildingmanagement.dtos;

import lombok.*;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OfferDTO {

    private UUID id;
    private float offerProcent;
    private UUID property_id;
}
