package com.projectps.buildingmanagement.dtos;

import com.projectps.buildingmanagement.entities.enums.PropertyStatus;
import com.projectps.buildingmanagement.entities.enums.PropertyType;
import lombok.*;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PropertyDTO {

    private UUID id;
    private String location;
    private double latitude;
    private double longitude;
    private int roomsNumber;
    private float initialPrice;
    private float priceAfterOffer;
    private boolean isRented;
    private boolean isOfferApplied;
    private PropertyType propertyType;
    private PropertyStatus propertyStatus;
    private String imageUrl;
    private ContractDTO rentalContract;
    private OfferDTO priceOffer;
    private SolicitationDTO propertySolicited;
}
