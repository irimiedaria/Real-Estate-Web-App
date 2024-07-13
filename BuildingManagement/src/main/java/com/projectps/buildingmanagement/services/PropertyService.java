package com.projectps.buildingmanagement.services;

import com.projectps.buildingmanagement.dtos.PropertyDTO;
import com.projectps.buildingmanagement.dtos.builders.PropertyBuilder;
import com.projectps.buildingmanagement.entities.Property;
import com.projectps.buildingmanagement.exceptions.PropertyNotFoundException;
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
 * This service class provides various functionalities related to property management.
 */
@Service
public class PropertyService {
  private static final Logger LOGGER = LoggerFactory.getLogger(PropertyService.class);
  private PropertyRepository propertyRepository;

  @Autowired
  public PropertyService(PropertyRepository propertyRepository) {
    this.propertyRepository = propertyRepository;
  }

  /**
   * Retrieves all properties.
   *
   * @return a list of all property DTOs
   */
  public List<PropertyDTO> getAllProperties() {
    List<Property> propertyList = propertyRepository.findAll();
    return propertyList.stream()
            .map(PropertyBuilder::toPropertyDTO)
            .collect(Collectors.toList());
  }

  /**
   * Retrieves all properties available for customers (not rented).
   *
   * @return a list of all available property DTOs
   */
  public List<PropertyDTO> getAllPropertiesCustomer() {
    List<Property> propertyList = propertyRepository.findAll();
    return propertyList.stream()
            .filter(property -> !property.isRented()) // Filter out rented properties
            .map(PropertyBuilder::toPropertyDTO)
            .collect(Collectors.toList());
  }

  /**
   * Retrieves a property by ID.
   *
   * @param id the ID of the property
   * @return the property DTO
   * @throws PropertyNotFoundException if the property is not found
   */
  public PropertyDTO getPropertyById(UUID id) throws PropertyNotFoundException {
    Optional<Property> propertyOptional = propertyRepository.findById(id);
    if (!propertyOptional.isPresent()) {
      LOGGER.error("Property with id {} was not found in db", id);
      throw new PropertyNotFoundException("Property with id " + id + " not found!");
    }
    return PropertyBuilder.toPropertyDTO(propertyOptional.get());
  }

  /**
   * Creates a new property.
   *
   * @param propertyDTO the property DTO containing property information
   * @return the created property DTO
   */
  public PropertyDTO createProperty(PropertyDTO propertyDTO) {

    if (propertyDTO.getLocation().length() > 50) {
      LOGGER.error("Location is too long: {}", propertyDTO.getLocation());
      throw new IllegalArgumentException("Location must be 50 characters or less.");
    }

    if (propertyDTO.getRoomsNumber() <= 0) {
      LOGGER.error("Invalid rooms number: {}", propertyDTO.getRoomsNumber());
      throw new IllegalArgumentException("Rooms number must be greater than 0.");
    }

    if (propertyDTO.getInitialPrice() <= 0) {
      LOGGER.error("Invalid initial price: {}", propertyDTO.getInitialPrice());
      throw new IllegalArgumentException("Initial price must be greater than 0.");
    }

    if (!isValidLatitude(propertyDTO.getLatitude()) || !isValidLongitude(propertyDTO.getLongitude())) {
      LOGGER.error("Invalid latitude or longitude: Latitude={}, Longitude={}", propertyDTO.getLatitude(), propertyDTO.getLongitude());
      throw new IllegalArgumentException("Invalid latitude or longitude values.");
    }

    Property property = PropertyBuilder.toEntity(propertyDTO);

    if (propertyDTO.getRentalContract() == null) {
      property.setRentalContract(null);
      property.setRented(false);
    }
    if (propertyDTO.getPriceOffer() == null) {
      property.setPriceOffer(null);
      property.setPriceAfterOffer(property.getInitialPrice());
      property.setOfferApplied(false);
    }

    property = propertyRepository.save(property);
    LOGGER.debug("Property with id {} was inserted in db", property.getId());
    LOGGER.info("Property created successfully");
    return PropertyBuilder.toPropertyDTO(property);
  }

  /**
   * Updates a property by ID.
   *
   * @param id          the ID of the property to update
   * @param propertyDTO the property DTO containing updated property information
   * @return the updated property DTO
   * @throws PropertyNotFoundException if the property is not found
   */
  public PropertyDTO updateProperty(UUID id, PropertyDTO propertyDTO) throws PropertyNotFoundException {
    Optional<Property> propertyOptional = propertyRepository.findById(id);
    if (!propertyOptional.isPresent()) {
      LOGGER.error("Property with id {} was not found in db", id);
      throw new PropertyNotFoundException("Property with id " + id + " not found!");
    }
    Property existingProperty = propertyOptional.get();

    if (propertyDTO.getLocation() != null && propertyDTO.getLocation().length() > 50) {
      LOGGER.error("Invalid location length: {}", propertyDTO.getLocation());
      throw new IllegalArgumentException("Location must not exceed 50 characters.");
    }

    if (!String.valueOf(propertyDTO.getRoomsNumber()).matches("\\d+")) {
      LOGGER.error("Invalid rooms number format: {}", propertyDTO.getRoomsNumber());
      throw new IllegalArgumentException("Rooms number must be numeric and greater than 0.");
    }
    if (propertyDTO.getRoomsNumber() <= 0) {
      LOGGER.error("Invalid rooms number: {}", propertyDTO.getRoomsNumber());
      throw new IllegalArgumentException("Rooms number must be greater than 0.");
    }

    if (!String.valueOf(propertyDTO.getInitialPrice()).matches("\\d+(\\.\\d+)?")) {
      LOGGER.error("Invalid initial price format: {}", propertyDTO.getInitialPrice());
      throw new IllegalArgumentException("Initial price must be numeric and greater than 0.");
    }
    if (propertyDTO.getInitialPrice() <= 0) {
      LOGGER.error("Invalid initial price: {}", propertyDTO.getInitialPrice());
      throw new IllegalArgumentException("Initial price must be greater than 0.");
    }

    if (!isValidLatitude(propertyDTO.getLatitude()) || !isValidLongitude(propertyDTO.getLongitude())) {
      LOGGER.error("Invalid latitude or longitude: Latitude={}, Longitude={}", propertyDTO.getLatitude(), propertyDTO.getLongitude());
      throw new IllegalArgumentException("Invalid latitude or longitude values.");
    }

    existingProperty.setLocation(propertyDTO.getLocation());
    existingProperty.setLatitude(propertyDTO.getLatitude());
    existingProperty.setLongitude(propertyDTO.getLongitude());
    existingProperty.setRoomsNumber(propertyDTO.getRoomsNumber());
    existingProperty.setInitialPrice(propertyDTO.getInitialPrice());
    existingProperty.setRented(propertyDTO.isRented());
    existingProperty.setOfferApplied(propertyDTO.isOfferApplied());
    existingProperty.setPropertyType(propertyDTO.getPropertyType());
    existingProperty.setPropertyStatus(propertyDTO.getPropertyStatus());

    if (propertyDTO.getImageUrl() != null && !propertyDTO.getImageUrl().isEmpty()) {
      existingProperty.setImageUrl(propertyDTO.getImageUrl());
    }

    Property updatedProperty = propertyRepository.save(existingProperty);

    LOGGER.debug("Property with id {} was updated successfully", id);
    LOGGER.info("Property with id {} was updated successfully", id);

    return PropertyBuilder.toPropertyDTO(updatedProperty);
  }

  /**
   * Deletes a property by ID.
   *
   * @param id the ID of the property to delete
   * @throws PropertyNotFoundException if the property is not found
   */
  public void deleteProperty(UUID id) throws PropertyNotFoundException {
    Optional<Property> propertyOptional = propertyRepository.findById(id);
    if (!propertyOptional.isPresent()) {
      LOGGER.error("Property with id {} was not found in db", id);
      throw new PropertyNotFoundException("Property with id " + id + " not found!");
    }
    propertyRepository.deleteById(id);
    LOGGER.info("Property with id {} was deleted successfully", id);
  }

  /**
   * Validates the latitude value.
   *
   * @param latitude the latitude value to validate
   * @return true if the latitude value is valid, false otherwise
   */
  private boolean isValidLatitude(Double latitude) {
    return latitude != null && latitude >= -90 && latitude <= 90;
  }

  /**
   * Validates the longitude value.
   *
   * @param longitude the longitude value to validate
   * @return true if the longitude value is valid, false otherwise
   */
  private boolean isValidLongitude(Double longitude) {
    return longitude != null && longitude >= -180 && longitude <= 180;
  }
}
