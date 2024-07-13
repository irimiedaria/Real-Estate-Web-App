
package com.projectps.buildingmanagement.services;

import com.projectps.buildingmanagement.dtos.ContractDTO;
import com.projectps.buildingmanagement.dtos.UserDTO;
import com.projectps.buildingmanagement.dtos.builders.ContractBuilder;
import com.projectps.buildingmanagement.entities.Contract;
import com.projectps.buildingmanagement.entities.Property;
import com.projectps.buildingmanagement.entities.User;
import com.projectps.buildingmanagement.entities.enums.UserType;
import com.projectps.buildingmanagement.exceptions.ContractNotFoundException;
import com.projectps.buildingmanagement.exceptions.PropertyNotFoundException;
import com.projectps.buildingmanagement.exceptions.UserNotFoundException;
import com.projectps.buildingmanagement.repositories.ContractRepository;
import com.projectps.buildingmanagement.repositories.PropertyRepository;
import com.projectps.buildingmanagement.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This service class provides various functionalities related to property contracts.
 */
@Service
public class ContractService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContractService.class);

    private static final String SESSION_USER_KEY = "logged_user";
    private ContractRepository contractsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    private final HttpSession session;

    @Autowired
    public ContractService(ContractRepository contractsRepository, HttpSession session) {
        this.contractsRepository = contractsRepository;
        this.session = session;
    }

    /**
     * Retrieves all contracts.
     *
     * @return a list of all contract DTOs
     */
    public List<ContractDTO> getAllContracts() {
        List<Contract> contractList = contractsRepository.findAll();
        return contractList.stream()
                .map(ContractBuilder::toContractDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all contracts associated with the logged-in customer.
     *
     * @return a list of all contract DTOs associated with the logged-in customer
     * @throws IllegalStateException if the customer ID is not found in the session
     */
    public List<ContractDTO> getAllContractsCustomer() {
        UserDTO loggedUser = (UserDTO) session.getAttribute("logged_user");
        if (loggedUser.getId() == null) {
            LOGGER.error("Customer ID not found in session");
            throw new IllegalStateException("Customer ID not found in session");
        }
        List<Contract> contractList = contractsRepository.findByUserId(loggedUser.getId());
        return contractList.stream()
                .map(ContractBuilder::toContractDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a contract by ID.
     *
     * @param id the ID of the contract
     * @return the contract DTO
     * @throws ContractNotFoundException if the contract is not found
     */
    public ContractDTO getContractById(UUID id) throws ContractNotFoundException {
        Optional<Contract> contract = contractsRepository.findById(id);
        if(!contract.isPresent()) {
            LOGGER.error("Contract with id {} was not found", id);
            throw new ContractNotFoundException(Contract.class.getSimpleName() + " with id: " + id);
        }

        return ContractBuilder.toContractDTO(contract.get());
    }

    /**
     * Retrieves a contract by ID associated with the logged-in customer.
     *
     * @param id the ID of the contract
     * @return the contract DTO associated with the logged-in customer
     * @throws ContractNotFoundException if the contract is not found
     * @throws IllegalStateException     if the customer ID is not found in the session
     */
    public ContractDTO getContractByIdCustomer(UUID id) throws ContractNotFoundException {
        UserDTO loggedUser = (UserDTO) session.getAttribute("logged_user");
        if (loggedUser.getId() == null) {
            LOGGER.error("Customer ID not found in session");
            throw new IllegalStateException("Customer ID not found in session");
        }
        Contract contract = contractsRepository.findByIdAndUserId(id, loggedUser.getId())
                .orElseThrow(() -> new ContractNotFoundException("Contract not found with id: " + id));
        return ContractBuilder.toContractDTO(contract);
    }

    /**
     * Creates a new contract.
     *
     * @param contractDTO the contract DTO containing contract information
     * @return the created contract DTO
     * @throws PropertyNotFoundException if the property associated with the contract is not found
     * @throws UserNotFoundException     if the user associated with the contract is not found
     * @throws IllegalArgumentException  if the start date is not valid or is in the past
     */
    public ContractDTO createContract(ContractDTO contractDTO) {
        Contract contract = ContractBuilder.toEntity(contractDTO);

        LocalDateTime startDate = contractDTO.getStartDate();
        if (startDate == null) {
            LOGGER.error("Start date not valid");
            throw new IllegalArgumentException("Start date not valid");
        }

        LocalDateTime currentDate = LocalDateTime.now();
        if (startDate.isBefore(currentDate)) {
            LOGGER.error("Start date cannot be in the past");
            throw new IllegalArgumentException("Start date cannot be in the past");
        }

        Optional<User> userOptional = userRepository.findById(contractDTO.getUser_id());
        if (!userOptional.isPresent()) {
            LOGGER.error("User with ID {} not found", contractDTO.getUser_id());
            throw new UserNotFoundException(User.class.getSimpleName() + " with ID: " + contractDTO.getUser_id());
        }
        contract.setUser(userOptional.get());

        Optional<Property> propertyOptional = propertyRepository.findById(contractDTO.getProperty_id());
        if (propertyOptional.isPresent()) {
            Property property = propertyOptional.get();
            if (property.isRented()) {
                LOGGER.error("Property with ID {} is already rented", contractDTO.getProperty_id());
                throw new IllegalArgumentException("Property is already rented!");
            }
            property.setRented(true);
            propertyRepository.save(property);
        } else {
            LOGGER.error("Property with ID {} not found", contractDTO.getProperty_id());
            throw new PropertyNotFoundException(Property.class.getSimpleName() + " with ID: " + contractDTO.getProperty_id());
        }

        contract = contractsRepository.save(contract);
        LOGGER.debug("Contract with id {} was inserted in db", contract.getId());
        LOGGER.info("Contract created successfully");
        return ContractBuilder.toContractDTO(contract);
    }

    /**
     * Updates a contract by ID.
     *
     * @param id         the ID of the contract to update
     * @param contractDTO the contract DTO containing updated contract information
     * @return the updated contract DTO
     * @throws ContractNotFoundException if the contract is not found
     * @throws IllegalArgumentException  if the start date is not valid or is in the past
     */
    public ContractDTO updateContract(UUID id, ContractDTO contractDTO) throws ContractNotFoundException, IllegalArgumentException {
        LocalDateTime startDate = contractDTO.getStartDate();
        if (startDate == null) {
            LOGGER.error("Start date not valid");
            throw new IllegalArgumentException("Start date not valid");
        }

        LocalDateTime currentDate = LocalDateTime.now();
        if (startDate.isBefore(currentDate)) {
            LOGGER.error("Start date cannot be in the past");
            throw new IllegalArgumentException("Start date cannot be in the past");
        }

        Optional<Contract> contractOptional = contractsRepository.findById(id);
        if (!contractOptional.isPresent()) {
            LOGGER.error("Contract with id {} not found", id);
            throw new ContractNotFoundException(Contract.class.getSimpleName() + " with id: " + id);
        }

        Contract existingContract = contractOptional.get();

        existingContract.setStartDate(contractDTO.getStartDate());
        existingContract.setDuration(contractDTO.getDuration());
        existingContract.setDetails(contractDTO.getDetails());

        Contract updatedContract = contractsRepository.save(existingContract);

        LOGGER.debug("Contract with id {} was updated successfully", id);
        LOGGER.info("Contract with id {} was updated successfully", id);
        return ContractBuilder.toContractDTO(updatedContract);
    }

    /**
     * Deletes a contract by ID.
     *
     * @param id the ID of the contract to delete
     * @throws ContractNotFoundException if the contract is not found
     * @throws PropertyNotFoundException if the property associated with the contract is not found
     */
    public void deleteContract(UUID id) throws ContractNotFoundException {
        Optional<Contract> contractOptional = contractsRepository.findById(id);
        if(!contractOptional.isPresent()) {
            LOGGER.error("Contract with id {} was not found in db", id);
            throw new ContractNotFoundException(Contract.class.getSimpleName() + " with id: " + id);
        }
        Contract contract = contractOptional.get();
        contractsRepository.deleteById(id);

        LOGGER.info("Contract with id {} was deleted successfully", id);

        Optional<Property> propertyOptional = propertyRepository.findById(contract.getProperty().getId());
        if (propertyOptional.isPresent()) {
            Property property = propertyOptional.get();
            property.setRented(false);
            propertyRepository.save(property);
        }
    }
}
