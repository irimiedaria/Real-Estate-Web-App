package com.projectps.buildingmanagement.dtos.builders;

import com.projectps.buildingmanagement.dtos.PropertyDTO;
import com.projectps.buildingmanagement.entities.Property;

public class PropertyBuilder {

    public static PropertyDTO toPropertyDTO(Property property) {
        PropertyDTO.PropertyDTOBuilder builder = PropertyDTO.builder()
                .id(property.getId())
                .location(property.getLocation())
                .latitude(property.getLatitude())
                .longitude(property.getLongitude())
                .roomsNumber(property.getRoomsNumber())
                .initialPrice(property.getInitialPrice())
                .priceAfterOffer(property.getPriceAfterOffer())
                .isRented(property.isRented())
                .isOfferApplied(property.isOfferApplied())
                .propertyType(property.getPropertyType())
                .propertyStatus(property.getPropertyStatus())
                .imageUrl(property.getImageUrl());

        if (property.getRentalContract() != null) {
            builder.rentalContract(ContractBuilder.toContractDTO(property.getRentalContract()));
        }
        if(property.getPriceOffer() != null) {
            builder.priceOffer(OfferBuider.toOfferDTO(property.getPriceOffer()));
        }
        if(property.getPropertySolicited() != null) {
            builder.propertySolicited(SolicitationBuilder.toSolicitationDTO(property.getPropertySolicited()));
        }

        return builder.build();
    }

    public static Property toEntity(PropertyDTO propertyDTO) {
        return Property.builder()
                .id(propertyDTO.getId())
                .location(propertyDTO.getLocation())
                .latitude(propertyDTO.getLatitude())
                .longitude(propertyDTO.getLongitude())
                .roomsNumber(propertyDTO.getRoomsNumber())
                .initialPrice(propertyDTO.getInitialPrice())
                .priceAfterOffer(propertyDTO.getPriceAfterOffer())
                .isRented(propertyDTO.isRented())
                .isOfferApplied(propertyDTO.isOfferApplied())
                .propertyType(propertyDTO.getPropertyType())
                .propertyStatus(propertyDTO.getPropertyStatus())
                .imageUrl(propertyDTO.getImageUrl())
                .build();
    }
}
