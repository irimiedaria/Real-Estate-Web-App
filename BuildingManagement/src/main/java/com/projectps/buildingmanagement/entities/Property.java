package com.projectps.buildingmanagement.entities;
import com.projectps.buildingmanagement.entities.enums.PropertyStatus;
import com.projectps.buildingmanagement.entities.enums.PropertyType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "location", nullable = false, unique = true)
    private String location;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    @Column(name = "rooms_number", nullable = false)
    private int roomsNumber;

    @Column(name = "initial_price", nullable = false)
    private float initialPrice;

    @Column(name = "price_after_offer")
    private float priceAfterOffer;

    @Column(name = "is_rented")
    private boolean isRented;

    @Column(name = "is_offer_applied")
    private boolean isOfferApplied;

    @Column(name = "type", nullable = false)
    private PropertyType propertyType;

    @Column(name = "status", nullable = false)
    private PropertyStatus propertyStatus;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToOne(mappedBy = "property", cascade = CascadeType.ALL)
    private Contract rentalContract;

    @OneToOne(mappedBy = "property", cascade = CascadeType.ALL)
    private Offer priceOffer;

    @OneToOne(mappedBy = "property", cascade = CascadeType.ALL)
    private Solicitation propertySolicited;
}
