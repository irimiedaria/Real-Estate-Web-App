package com.projectps.buildingmanagement.services;

import com.projectps.buildingmanagement.dtos.OfferDTO;
import com.projectps.buildingmanagement.dtos.builders.OfferBuider;
import com.projectps.buildingmanagement.entities.Contract;
import com.projectps.buildingmanagement.entities.Offer;
import com.projectps.buildingmanagement.entities.Property;
import com.projectps.buildingmanagement.exceptions.ContractNotFoundException;
import com.projectps.buildingmanagement.exceptions.OfferNotFoundException;
import com.projectps.buildingmanagement.exceptions.PropertyNotFoundException;
import com.projectps.buildingmanagement.repositories.OfferRepository;
import com.projectps.buildingmanagement.repositories.PropertyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This service class provides various functionalities related to property offers.
 */
@Service
public class OfferService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OfferService.class);
    private OfferRepository offerRepository;

    @Autowired
    public OfferService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    @Autowired
    private PropertyRepository propertyRepository;

    /**
     * Retrieves all offers.
     *
     * @return a list of all offer DTOs
     */
    public List<OfferDTO> getAllOffers() {
        List<Offer> offerList = offerRepository.findAll();
        return offerList.stream()
                .map(OfferBuider::toOfferDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves an offer by ID.
     *
     * @param id the ID of the offer
     * @return the offer DTO
     * @throws OfferNotFoundException if the offer is not found
     */
    public OfferDTO getOfferById(UUID id) throws OfferNotFoundException {
        Optional<Offer> offer = offerRepository.findById(id);
        if(!offer.isPresent()) {
            LOGGER.error("Offer with id {} was not found in db", id);
            throw new OfferNotFoundException(Offer.class.getSimpleName() + " with id: " + id);
        }
        return OfferBuider.toOfferDTO(offer.get());
    }

    /**
     * Creates a new offer.
     *
     * @param offerDTO the offer DTO containing offer information
     * @return the created offer DTO
     * @throws PropertyNotFoundException if the property associated with the offer is not found
     */
    public OfferDTO createOffer(OfferDTO offerDTO) throws PropertyNotFoundException {
        Offer offer = OfferBuider.toEntity(offerDTO);

        Optional<Property> propertyOptional = propertyRepository.findById(offerDTO.getProperty_id());

        if(propertyOptional.isPresent()) {
            Property property = propertyOptional.get();
            float procent = offer.getOfferProcent();
            float initialPrice = property.getInitialPrice();
            float finalPrice = initialPrice - (initialPrice * (procent / 100.0f));

            property.setPriceAfterOffer(finalPrice);
            property.setOfferApplied(true);
            propertyRepository.save(property);
        } else {
            LOGGER.error("Property with ID {} not found", offerDTO.getProperty_id());
            throw new PropertyNotFoundException(Property.class.getSimpleName() + " with ID: " + offerDTO.getProperty_id());
        }

        offer = offerRepository.save(offer);

        LOGGER.debug("Offer with id {} was inserted in db", offer.getId());
        LOGGER.info("Offer created successfully");
        return OfferBuider.toOfferDTO(offer);
    }

    /**
     * Updates an offer by ID.
     *
     * @param id        the ID of the offer to update
     * @param offerDTO  the offer DTO containing updated offer information
     * @return the updated offer DTO
     * @throws OfferNotFoundException     if the offer is not found
     * @throws PropertyNotFoundException if the property associated with the offer is not found
     */
    public OfferDTO updateOffer(UUID id, OfferDTO offerDTO) throws OfferNotFoundException {
        Optional<Offer> offerOptional = offerRepository.findById(id);
        if (!offerOptional.isPresent()) {
            LOGGER.error("Offer with id {} was not found in db", id);
            throw new OfferNotFoundException("Offer with id " + id + " not found!");
        }

        Offer existingOffer = offerOptional.get();

        existingOffer.setOfferProcent(offerDTO.getOfferProcent());
        Offer updatedOffer = offerRepository.save(existingOffer);

        Optional<Property> propertyOptional = propertyRepository.findById(offerDTO.getProperty_id());
        if (propertyOptional.isPresent()) {
            Property property = propertyOptional.get();
            float procent = updatedOffer.getOfferProcent();
            float initialPrice = property.getInitialPrice();
            float finalPrice = initialPrice - (initialPrice * (procent / 100.0f));

            property.setPriceAfterOffer(finalPrice);
            propertyRepository.save(property);
        }

        LOGGER.debug("Offer with id {} was updated in db", updatedOffer.getId());
        LOGGER.info("Offer with id {} was updated successfully", id);
        return OfferBuider.toOfferDTO(updatedOffer);
    }

    /**
     * Deletes an offer by ID.
     *
     * @param id the ID of the offer to delete
     * @throws OfferNotFoundException     if the offer is not found
     * @throws PropertyNotFoundException if the property associated with the offer is not found
     */
    public void deleteOffer(UUID id) throws OfferNotFoundException {
        Optional<Offer> offerOptional = offerRepository.findById(id);
        if(!offerOptional.isPresent()) {
            LOGGER.error("Offer with id {} was not found in db", id);
            throw new OfferNotFoundException(Offer.class.getSimpleName() + " with id: " + id);
        }
        Offer offer = offerOptional.get();
        offerRepository.deleteById(id);

        LOGGER.info("Offer with id {} was deleted successfully", id);

        Optional<Property> propertyOptional = propertyRepository.findById(offer.getProperty().getId());
        if(propertyOptional.isPresent()) {
            Property property = propertyOptional.get();

            property.setPriceAfterOffer(property.getInitialPrice());
            property.setOfferApplied(false);
            propertyRepository.save(property);
        }
    }
}
