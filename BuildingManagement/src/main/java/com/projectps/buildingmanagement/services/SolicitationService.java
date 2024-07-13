package com.projectps.buildingmanagement.services;

import com.projectps.buildingmanagement.dtos.SolicitationDTO;
import com.projectps.buildingmanagement.dtos.UserDTO;
import com.projectps.buildingmanagement.dtos.builders.SolicitationBuilder;
import com.projectps.buildingmanagement.entities.Property;
import com.projectps.buildingmanagement.entities.Solicitation;
import com.projectps.buildingmanagement.entities.User;
import com.projectps.buildingmanagement.exceptions.PropertyNotFoundException;
import com.projectps.buildingmanagement.exceptions.SolicitationNotFoundException;
import com.projectps.buildingmanagement.repositories.PropertyRepository;
import com.projectps.buildingmanagement.repositories.SolicitationRepository;
import com.projectps.buildingmanagement.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This service class provides various functionalities related to solicitation management.
 */
@Service
public class SolicitationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewService.class);
    private static final String SESSION_USER_KEY = "logged_user";

    private SolicitationRepository solicitationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    private final HttpSession session;

    /**
     * Constructs a new SolicitationService with the specified repository and session.
     *
     * @param solicitationRepository the repository for accessing solicitation data
     * @param session                the HTTP session
     */
    @Autowired
    public SolicitationService(SolicitationRepository solicitationRepository, HttpSession session) {
        this.solicitationRepository = solicitationRepository;
        this.session = session;
    }

    /**
     * Retrieves all solicitations.
     *
     * @return a list of all solicitation DTOs
     */
    public List<SolicitationDTO> getAllSolicitations() {
        List<Solicitation> solicitationList = solicitationRepository.findAll();
        return solicitationList.stream()
                .map(SolicitationBuilder::toSolicitationDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all solicitations for the logged-in customer.
     *
     * @return a list of all solicitation DTOs for the logged-in customer
     */
    public List<SolicitationDTO> getAllSolicitationsCustomer() {
        UserDTO loggedUser = (UserDTO) session.getAttribute("logged_user");
        if (loggedUser == null || loggedUser.getId() == null) {
            LOGGER.error("Customer ID not found in session");
            throw new IllegalStateException("Customer ID not found in session");
        }
        List<Solicitation> solicitationList = solicitationRepository.findByUserId(loggedUser.getId());
        return solicitationList.stream()
                .map(SolicitationBuilder::toSolicitationDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a solicitation by ID.
     *
     * @param id the ID of the solicitation
     * @return the solicitation DTO
     * @throws SolicitationNotFoundException if the solicitation is not found
     */
    public SolicitationDTO getSolicitationById(UUID id) throws SolicitationNotFoundException {
        Optional<Solicitation> solicitationOptional = solicitationRepository.findById(id);
        if(!solicitationOptional.isPresent()) {
            LOGGER.error("Solicitation with id {} was not found in db", id);
            throw new SolicitationNotFoundException(Solicitation.class.getSimpleName() + "with id:" + id);
        }
        return SolicitationBuilder.toSolicitationDTO(solicitationOptional.get());
    }

    /**
     * Retrieves a solicitation by ID for the logged-in customer.
     *
     * @param id the ID of the solicitation
     * @return the solicitation DTO
     * @throws SolicitationNotFoundException if the solicitation is not found
     */
    public SolicitationDTO getSolicitationByIdCustomer(UUID id) throws SolicitationNotFoundException {
        UserDTO loggedUser = (UserDTO) session.getAttribute("logged_user");
        if (loggedUser.getId() == null) {
            LOGGER.error("Customer ID not found in session");
            throw new IllegalStateException("Customer ID not found in session");
        }

        Optional<Solicitation> solicitationOptional = solicitationRepository.findByIdAndUserId(id, loggedUser.getId());
        if(!solicitationOptional.isPresent()) {
            LOGGER.error("Solicitation with id {} was not found for customer with id {}", id, loggedUser.getId());
            throw new SolicitationNotFoundException("Solicitation not found with id: " + id);
        }
        return SolicitationBuilder.toSolicitationDTO(solicitationOptional.get());
    }

    /**
     * Checks if a solicitation already exists for the given user and property.
     *
     * @param userId    the ID of the user
     * @param propertyId the ID of the property
     * @return true if a solicitation exists, false otherwise
     */
    private boolean doesSolicitationExist(UUID userId, UUID propertyId) {
        List<Solicitation> existingSolicitations = solicitationRepository.findByUserIdAndPropertyId(userId, propertyId);
        return !existingSolicitations.isEmpty();
    }

    /**
     * Creates a new solicitation for the logged-in customer.
     *
     * @param solicitationDTO the solicitation DTO containing solicitation information
     * @return the created solicitation DTO
     */
    public SolicitationDTO createSolicitationCustomer(SolicitationDTO solicitationDTO) {
        LocalDateTime currentDate = LocalDateTime.now();
        solicitationDTO.setDate(currentDate);

        UserDTO loggedUser = (UserDTO) session.getAttribute("logged_user");
        if (loggedUser == null || loggedUser.getId() == null) {
            LOGGER.error("User not logged in or user ID not found in session");
            throw new IllegalStateException("User not logged in or user ID not found in session");
        }

        solicitationDTO.setUser_id(loggedUser.getId());

        if (doesSolicitationExist(loggedUser.getId(), solicitationDTO.getProperty_id())) {
            LOGGER.error("User {} already has a solicitation for property {}", loggedUser.getId(), solicitationDTO.getProperty_id());
            throw new IllegalStateException("You have already requested this property.");
        }

        Optional<Property> propertyOptional = propertyRepository.findById(solicitationDTO.getProperty_id());
        if (!propertyOptional.isPresent()) {
            LOGGER.error("Property with ID {} not found", solicitationDTO.getProperty_id());
            throw new PropertyNotFoundException(Property.class.getSimpleName() + " with ID: " + solicitationDTO.getProperty_id());
        }

        Property property = propertyOptional.get();
        if (property.isRented()) {
            LOGGER.error("Property with ID {} is not available", solicitationDTO.getProperty_id());
            throw new IllegalArgumentException("Property is not available!");
        }

        Solicitation solicitation = SolicitationBuilder.toEntity(solicitationDTO);
        solicitation.setUser(User.builder().id(solicitationDTO.getUser_id()).build());
        solicitation.setProperty(property);

        solicitation = solicitationRepository.save(solicitation);

        LOGGER.debug("Solicitation with id {} was inserted in db", solicitation.getId());
        LOGGER.info("Solicitation created successfully");

        return SolicitationBuilder.toSolicitationDTO(solicitation);
    }

    /**
     * Deletes a solicitation by ID for the administrator.
     *
     * @param id the ID of the solicitation to delete
     * @throws SolicitationNotFoundException if the solicitation is not found
     */
    public void deleteSolicitationAdmin(UUID id) throws SolicitationNotFoundException {
        Optional<Solicitation> solicitationOptional = solicitationRepository.findById(id);
        if(!solicitationOptional.isPresent()) {
            LOGGER.error("Solicitation with id {} was not found in db", id);
            throw new SolicitationNotFoundException(Solicitation.class.getSimpleName() + "with id: " + id);
        }
        solicitationRepository.deleteById(id);
        LOGGER.info("Solicitation with id {} was deleted successfully", id);
    }

    /**
     * Deletes a solicitation by ID for the logged-in customer.
     *
     * @param id the ID of the solicitation to delete
     * @throws SolicitationNotFoundException if the solicitation is not found
     */
    public void deleteSolicitationCustomer(UUID id) throws SolicitationNotFoundException {
        UserDTO loggedUser = (UserDTO) session.getAttribute("logged_user");
        if (loggedUser == null) {
            LOGGER.error("User not logged in");
            throw new IllegalStateException("User not logged in");
        }

        Optional<Solicitation> solicitationOptional = solicitationRepository.findByIdAndUserId(id, loggedUser.getId());
        if(!solicitationOptional.isPresent()) {
            LOGGER.error("Solicitation with id {} was not found for customer with id {}", id, loggedUser.getId());
            throw new SolicitationNotFoundException("Solicitation not found with id: " + id);
        }
        solicitationRepository.deleteById(id);
        LOGGER.info("Solicitation with id {} was deleted successfully", id);
    }
}
