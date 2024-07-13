package com.projectps.buildingmanagement.controllers;

import com.projectps.buildingmanagement.dtos.PropertyDTO;
import com.projectps.buildingmanagement.exceptions.PropertyNotFoundException;
import com.projectps.buildingmanagement.services.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

/**
 * This controller class handles HTTP requests related to properties.
 */
@RestController
@RequestMapping("/properties")
public class PropertyController {

    private final PropertyService propertyService;

    /**
     * Constructs a new PropertyController with the specified PropertyService.
     *
     * @param propertyService the property service to use
     */
    @Autowired
    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    /**
     * Retrieves all properties.
     *
     * @return a ModelAndView containing the view "propertiesList" and a list of property DTOs
     */
    @GetMapping("/propertiesList")
    public ModelAndView getAllProperties() {
        List<PropertyDTO> dtos = propertyService.getAllProperties();
        ModelAndView modelAndView = new ModelAndView("propertiesList");
        modelAndView.addObject("property", dtos);
        return modelAndView;
    }

    /**
     * Retrieves all properties for customers.
     *
     * @return a ModelAndView containing the view "propertiesListCustomer" and a list of property DTOs
     */
    @GetMapping("/propertiesListCustomer")
    public ModelAndView getAllPropertiesCustomer() {
        List<PropertyDTO> dtos = propertyService.getAllPropertiesCustomer();
        ModelAndView modelAndView = new ModelAndView("propertiesListCustomer");
        modelAndView.addObject("property", dtos);
        return modelAndView;
    }

    /**
     * Retrieves a property by its ID.
     *
     * @param id the ID of the property to retrieve
     * @return a ModelAndView containing the view "searchProperty" and the retrieved property DTO
     */
    @GetMapping("/searchProperty")
    public ModelAndView getPropertyById(@RequestParam(required = false) String id) {
        ModelAndView modelAndView = new ModelAndView("searchProperty");
        if (id == null || id.isEmpty()) {
            return modelAndView;
        }
        try {
            UUID propertyId = UUID.fromString(id);
            PropertyDTO propertyDTO = propertyService.getPropertyById(propertyId);
            modelAndView.addObject("searched_property", propertyDTO);
        } catch (IllegalArgumentException e) {
            modelAndView.addObject("error", "Invalid UUID format for ID: " + id);
        } catch (PropertyNotFoundException e) {
            modelAndView.addObject("error", "Property with ID " + id + " not found");
        } catch (Exception e) {
            modelAndView.addObject("error", "Failed to retrieve property");
        }
        return modelAndView;
    }

    @GetMapping("/insertProperty")
    public ModelAndView showInsertPropertyPage() {
        ModelAndView modelAndView = new ModelAndView("insertProperty");
        return modelAndView;
    }

    /**
     * Displays the insert property page.
     *
     * @return a ModelAndView containing the view "insertProperty"
     */
    @PostMapping("/insertProperty")
    public ModelAndView createProperty(@ModelAttribute PropertyDTO propertyDTO) {
        ModelAndView modelAndView = new ModelAndView("insertProperty");
        try {
            PropertyDTO createdProperty = propertyService.createProperty(propertyDTO);
            modelAndView.addObject("created_property", createdProperty);
            modelAndView.addObject("success_message", "Property created successfully!");
        } catch (IllegalArgumentException e) {
            modelAndView.addObject("error", e.getMessage());
        } catch (Exception e) {
            modelAndView.addObject("error", "Failed to create property due to an unexpected error.");
        }
        return modelAndView;
    }

    /**
     * Retrieves a property for updating by its ID.
     *
     * @param id the ID of the property to update
     * @return a ModelAndView containing the view "updateProperty" and the property DTO to update
     */
    @GetMapping("/updateProperty")
    public ModelAndView getPropertyForUpdate(@RequestParam(required = false) String id) {
        ModelAndView modelAndView = new ModelAndView("updateProperty");
        if (id == null || id.isEmpty()) {
            return modelAndView;
        }
        try {
            UUID propertyId = UUID.fromString(id);
            PropertyDTO propertyDTO = propertyService.getPropertyById(propertyId);
            modelAndView.addObject("updated_property", propertyDTO);
        } catch (IllegalArgumentException e) {
            modelAndView.addObject("error", "Invalid UUID format for ID: " + id);
        } catch (PropertyNotFoundException e) {
            modelAndView.addObject("error", "Property with ID " + id + " not found");
        } catch (Exception e) {
            modelAndView.addObject("error", "Failed to retrieve property");
        }
        return modelAndView;
    }

    /**
     * Updates a property.
     *
     * @param id          the ID of the property to update
     * @param propertyDTO the updated property DTO containing property details
     * @return a ModelAndView containing the view "updateProperty" and a success message or an error message
     */
    @PostMapping("/updateProperty")
    public ModelAndView updateProperty(@RequestParam String id, @ModelAttribute PropertyDTO propertyDTO) {
        ModelAndView modelAndView = new ModelAndView("updateProperty");
        try {
            UUID propertyId = UUID.fromString(id);
            PropertyDTO updatedProperty = propertyService.updateProperty(propertyId, propertyDTO);
            modelAndView.addObject("updated_property", updatedProperty);
            modelAndView.addObject("success_message", "Property updated successfully!");
        } catch (IllegalArgumentException e) {
            modelAndView.addObject("error", e.getMessage());
        } catch (PropertyNotFoundException e) {
            modelAndView.addObject("error", "Property with ID " + id + " not found!");
        } catch (Exception e) {
            modelAndView.addObject("error", "Failed to update property due to an unexpected error.");
        }
        return modelAndView;
    }

    /**
     * Deletes a property.
     *
     * @param id the ID of the property to delete
     * @return a ModelAndView redirecting to the propertiesList page with an error message or a success message
     */
    @PostMapping("/deleteProperty")
    public ModelAndView deleteProperty(@RequestParam UUID id) {
        try {
            propertyService.deleteProperty(id);
            ModelAndView modelAndView = new ModelAndView("redirect:/properties/propertiesList");
            return modelAndView;
        } catch (PropertyNotFoundException e) {
            ModelAndView modelAndView = new ModelAndView("redirect:/properties/propertiesList");
            modelAndView.addObject("errorMessage", "Property not found!");
            return modelAndView;
        } catch (Exception e) {
            ModelAndView modelAndView = new ModelAndView("redirect:/properties/propertiesList");
            modelAndView.addObject("errorMessage", "Failed to delete property");
            return modelAndView;
        }
    }
}
