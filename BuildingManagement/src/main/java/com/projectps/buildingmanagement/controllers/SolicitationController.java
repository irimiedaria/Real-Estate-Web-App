package com.projectps.buildingmanagement.controllers;

import com.projectps.buildingmanagement.dtos.SolicitationDTO;
import com.projectps.buildingmanagement.dtos.UserDTO;
import com.projectps.buildingmanagement.exceptions.SolicitationNotFoundException;
import com.projectps.buildingmanagement.services.SolicitationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * This controller class handles HTTP requests related to property solicitation management.
 */
@RestController
@RequestMapping("/solicitations")
public class SolicitationController {

    private final SolicitationService solicitationService;

    @Autowired
    private HttpSession session;

    /**
     * Constructs a new SolicitationController with the specified SolicitationService and HttpSession.
     *
     * @param solicitationService the solicitation service to use
     * @param session             the HTTP session
     */
    @Autowired
    public SolicitationController(SolicitationService solicitationService, HttpSession session) {

        this.solicitationService = solicitationService;
        this.session = session;
    }

    /**
     * Retrieves all property solicitations for administrators.
     *
     * @return a ModelAndView containing the view "requests_admin" and a list of solicitation DTOs
     */
    @GetMapping("/solicitationsAdmin")
    public ModelAndView getAllSolicitationsAdmin() {
        List<SolicitationDTO> dtos = solicitationService.getAllSolicitations();
        ModelAndView modelAndView = new ModelAndView("requests_admin");
        modelAndView.addObject("request", dtos);
        return modelAndView;
    }

    /**
     * Retrieves all property solicitations for customers.
     *
     * @return a ModelAndView containing the view "requests_customer" and a list of solicitation DTOs
     */
    @GetMapping("/solicitationsCustomer")
    public ModelAndView getAllSolicitationsCustomer() {
        List<SolicitationDTO> dtos = solicitationService.getAllSolicitationsCustomer();
        ModelAndView modelAndView = new ModelAndView("requests_customer");
        modelAndView.addObject("request", dtos);
        return modelAndView;
    }

    /**
     * Creates a new solicitation for a property by a customer.
     *
     * @param propertyId the ID of the property being solicited
     * @return a ResponseEntity with a success message or an error message
     */
    @PostMapping("/requestProperty")
    public ResponseEntity<String> requestProperty(@RequestParam UUID propertyId) {
        try {
            UserDTO loggedUser = (UserDTO) session.getAttribute("logged_user");
            if (loggedUser == null || loggedUser.getId() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
            }

            SolicitationDTO solicitationDTO = new SolicitationDTO();
            solicitationDTO.setProperty_id(propertyId);
            solicitationDTO.setUser_id(loggedUser.getId());
            solicitationDTO.setDate(LocalDateTime.now());

            solicitationService.createSolicitationCustomer(solicitationDTO);
            return ResponseEntity.ok("Solicitation created successfully!");
        } catch (IllegalStateException e) {
            if (e.getMessage().contains("You have already requested this property")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create solicitation");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create solicitation");
        }
    }

    /**
     * Deletes a solicitation by ID for administrators.
     *
     * @param id the ID of the solicitation to delete
     * @return a ModelAndView redirecting to the solicitationsAdmin page
     */
    @PostMapping("/deleteSolicitationAdmin")
    public ModelAndView deleteSolicitationAdmin(@RequestParam UUID id) {
        try {
            solicitationService.deleteSolicitationAdmin(id);
            ModelAndView modelAndView = new ModelAndView("redirect:/solicitations/solicitationsAdmin");
            return modelAndView;
        } catch (SolicitationNotFoundException e) {
            ModelAndView modelAndView = new ModelAndView("redirect:/solicitations/solicitationsAdmin");
            modelAndView.addObject("errorMessage", "Solicitation not found!");
            return modelAndView;
        } catch (Exception e) {
            ModelAndView modelAndView = new ModelAndView("redirect:/solicitations/solicitationsAdmin");
            modelAndView.addObject("errorMessage", "Failed to delete solicitation");
            return modelAndView;
        }
    }

    /**
     * Deletes a solicitation by ID for customers.
     *
     * @param id the ID of the solicitation to delete
     * @return a ModelAndView redirecting to the solicitationsCustomer page
     */
    @PostMapping("/deleteSolicitationCustomer")
    public ModelAndView deleteSolicitationCustomer(@RequestParam UUID id) {
        try {
            solicitationService.deleteSolicitationCustomer(id);
            ModelAndView modelAndView = new ModelAndView("redirect:/solicitations/solicitationsCustomer");
            return modelAndView;
        } catch (SolicitationNotFoundException e) {
            ModelAndView modelAndView = new ModelAndView("redirect:/solicitations/solicitationsCustomer");
            modelAndView.addObject("errorMessage", "Solicitation not found!");
            return modelAndView;
        } catch (Exception e) {
            ModelAndView modelAndView = new ModelAndView("redirect:/solicitations/solicitationsCustomer");
            modelAndView.addObject("errorMessage", "Failed to delete solicitation");
            return modelAndView;
        }
    }

}
