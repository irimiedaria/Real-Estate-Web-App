package com.projectps.buildingmanagement.controllers;

import com.projectps.buildingmanagement.dtos.OfferDTO;
import com.projectps.buildingmanagement.exceptions.OfferNotFoundException;
import com.projectps.buildingmanagement.exceptions.PropertyNotFoundException;
import com.projectps.buildingmanagement.services.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

/**
 * This controller class handles HTTP requests related to offers.
 */
@RestController
@RequestMapping("/offers")
public class OfferController {

    private final OfferService offerService;

    /**
     * Constructs a new OfferController with the specified OfferService.
     *
     * @param offerService the offer service to use
     */
    @Autowired
    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    /**
     * Retrieves all offers.
     *
     * @return a ModelAndView containing the view "offersList" and a list of offer DTOs
     */
    @GetMapping("/offersList")
    public ModelAndView getAllOffers() {
        List<OfferDTO> dtos = offerService.getAllOffers();
        ModelAndView modelAndView = new ModelAndView("offersList");
        modelAndView.addObject("offer", dtos);
        return modelAndView;
    }

    /**
     * Retrieves an offer by its ID.
     *
     * @param id the ID of the offer to retrieve
     * @return a ModelAndView containing the view "searchOffer" and the retrieved offer DTO
     */
    @GetMapping("/searchOffer")
    public ModelAndView getOfferById(@RequestParam(required = false) String id) {
        ModelAndView modelAndView = new ModelAndView("searchOffer");
        if (id == null || id.isEmpty()) {
            return modelAndView;
        }
        try {
            UUID offerId = UUID.fromString(id);
            OfferDTO offerDTO = offerService.getOfferById(offerId);
            modelAndView.addObject("searched_offer", offerDTO);
        } catch (IllegalArgumentException e) {
            modelAndView.addObject("error", "Invalid UUID format for ID: " + id);
        } catch (OfferNotFoundException e) {
            modelAndView.addObject("error", "Offer with ID " + id + " not found");
        } catch (Exception e) {
            modelAndView.addObject("error", "Failed to retrieve offer");
        }
        return modelAndView;
    }

    /**
     * Displays the insert offer page.
     *
     * @return a ModelAndView containing the view "insertOffer"
     */
    @GetMapping("/insertOffer")
    public ModelAndView showInsertOfferPage() {
        ModelAndView modelAndView = new ModelAndView("insertOffer");
        return modelAndView;
    }

    /**
     * Creates a new offer.
     *
     * @param offerDTO the offer DTO containing offer details
     * @return a ModelAndView containing the view "insertOffer" and a success message or an error message
     */
    @PostMapping("/insertOffer")
    public ModelAndView createOffer(@ModelAttribute OfferDTO offerDTO) {
        ModelAndView modelAndView = new ModelAndView("insertOffer");

        try {
            OfferDTO createdOffer = offerService.createOffer(offerDTO);
            modelAndView.addObject("created_offer", createdOffer);
            modelAndView.addObject("success_message", "Offer created successfully!");
        } catch (PropertyNotFoundException e) {
            modelAndView.addObject("error", "Property with ID " + offerDTO.getProperty_id() + "not found!");
        } catch (Exception e) {
            modelAndView.addObject("error", "Failed to create offer");
        }

        return modelAndView;
    }

    /**
     * Retrieves an offer for updating by its ID.
     *
     * @param id the ID of the offer to update
     * @return a ModelAndView containing the view "updateOffer" and the offer DTO to update
     */
    @GetMapping("/updateOffer")
    public ModelAndView getOfferForUpdate(@RequestParam(required = false) String id) {
        ModelAndView modelAndView = new ModelAndView("updateOffer");
        if (id == null || id.isEmpty()) {
            return modelAndView;
        }
        try {
            UUID offerId = UUID.fromString(id);
            OfferDTO offerDTO = offerService.getOfferById(offerId);
            modelAndView.addObject("updated_offer", offerDTO);
        } catch (IllegalArgumentException e) {
            modelAndView.addObject("error", "Invalid UUID format for ID: " + id);
        } catch (OfferNotFoundException e) {
            modelAndView.addObject("error", "Offer with ID " + id + " not found");
        } catch (Exception e) {
            modelAndView.addObject("error", "Failed to retrieve offer");
        }
        return modelAndView;
    }

    /**
     * Updates an offer.
     *
     * @param id          the ID of the offer to update
     * @param offerDTO    the updated offer DTO containing offer details
     * @return a ModelAndView containing the view "updateOffer" and a success message or an error message
     */
    @PostMapping("/updateOffer")
    public ModelAndView updateOffer(@RequestParam(required = false) String id, @ModelAttribute OfferDTO offerDTO) {
        ModelAndView modelAndView = new ModelAndView("updateOffer");
        if (id == null || id.isEmpty()) {
            modelAndView.addObject("error", "Invalid offer ID");
            return modelAndView;
        }
        try {
            UUID offerId = UUID.fromString(id);
            OfferDTO updatedOffer = offerService.updateOffer(offerId, offerDTO);
            modelAndView.addObject("updated_offer", updatedOffer);
            modelAndView.addObject("success_message", "Offer with ID " + id + " was updated successfully!");
        } catch (IllegalArgumentException e) {
            modelAndView.addObject("error", "Invalid UUID format for ID: " + id);
        } catch (OfferNotFoundException e) {
            modelAndView.addObject("error", "Offer with ID " + id + " not found!");
        } catch (Exception e) {
            modelAndView.addObject("error", "Failed to update offer");
        }

        return modelAndView;
    }

    /**
     * Deletes an offer.
     *
     * @param id the ID of the offer to delete
     * @return a ModelAndView redirecting to the offersList page with an error message or a success message
     */
    @PostMapping("/deleteOffer")
    public ModelAndView deleteOffer(@RequestParam UUID id) {
        try {
            offerService.deleteOffer(id);
            ModelAndView modelAndView = new ModelAndView("redirect:/offers/offersList");
            return modelAndView;
        } catch (OfferNotFoundException e) {
            ModelAndView modelAndView = new ModelAndView("redirect:/offers/offersList");
            modelAndView.addObject("errorMessage", "Offer not found!");
            return modelAndView;
        } catch (Exception e) {
            ModelAndView modelAndView = new ModelAndView("redirect:/offers/offersList");
            modelAndView.addObject("errorMessage", "Failed to delete offer");
            return modelAndView;
        }
    }
}
