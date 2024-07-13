package com.projectps.buildingmanagement.dtos.builders;

import com.projectps.buildingmanagement.dtos.OfferDTO;
import com.projectps.buildingmanagement.entities.Offer;
import com.projectps.buildingmanagement.entities.Property;

public class OfferBuider {

    public static OfferDTO toOfferDTO(Offer offer) {
        return OfferDTO.builder()
                .id(offer.getId())
                .offerProcent(offer.getOfferProcent())
                .property_id(offer.getProperty().getId())
                .build();
    }

    public static Offer toEntity(OfferDTO offerDTO) {
        return Offer.builder()
                .id(offerDTO.getId())
                .offerProcent(offerDTO.getOfferProcent())
                .property(Property.builder()
                        .id(offerDTO.getProperty_id())
                        .build())
                .build();
    }
}
