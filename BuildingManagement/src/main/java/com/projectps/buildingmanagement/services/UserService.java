package com.projectps.buildingmanagement.services;

import com.projectps.buildingmanagement.dtos.RequestDto;
import com.projectps.buildingmanagement.dtos.ResponseDto;
import com.projectps.buildingmanagement.dtos.UserDTO;
import com.projectps.buildingmanagement.dtos.builders.UserBuilder;
import com.projectps.buildingmanagement.entities.Contract;
import com.projectps.buildingmanagement.entities.Property;
import com.projectps.buildingmanagement.entities.User;
import com.projectps.buildingmanagement.exceptions.EmailSendingException;
import com.projectps.buildingmanagement.exceptions.UserNotFoundException;
import com.projectps.buildingmanagement.repositories.PropertyRepository;
import com.projectps.buildingmanagement.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This service class provides various functionalities related to user management.
 */
@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private static final String SESSION_USER_KEY = "logged_user";

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    private UserRepository userRepository;

    private final String authorizationToken1;
    private final String authorizationToken2;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private HttpSession session;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Constructs a new UserService with the specified UserRepository and authorization tokens.
     *
     * @param userRepository      the repository for accessing user data
     * @param authorizationToken1 the first part of the authorization token
     * @param authorizationToken2 the second part of the authorization token
     */
    @Autowired
    public UserService(UserRepository userRepository,
                       @Value("${authorization.token1}") String authorizationToken1,
                       @Value("${authorization.token2}") String authorizationToken2) {
        this.userRepository = userRepository;
        this.authorizationToken1 = authorizationToken1;
        this.authorizationToken2 = authorizationToken2;
    }

    /**
     * Sends an email to the user with the provided details.
     *
     * @param userDTO          the user DTO containing user information
     * @param emailSubject     the subject of the email
     * @param emailBody        the body of the email
     * @param authorizationToken1 the first part of the authorization token
     * @param authorizationToken2 the second part of the authorization token
     * @throws EmailSendingException if there is an error sending the email
     */
    public void sendEmailToUser(UserDTO userDTO, String emailSubject, String emailBody, String authorizationToken1, String authorizationToken2) {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(userDTO.getId());
        requestDto.setFirstName(userDTO.getFirstName());
        requestDto.setLastName(userDTO.getLastName());
        requestDto.setEmailReceiver(userDTO.getEmail());
        requestDto.setEmailSubject(emailSubject);
        requestDto.setEmailBody(emailBody);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.set("Authorization", authorizationToken1 + authorizationToken2);

        HttpEntity<RequestDto> requestEntity = new HttpEntity<>(requestDto, headers);

        try {
            ResponseEntity<ResponseDto> responseEntity = restTemplate.exchange(
                    "http://localhost:8081/send-email",
                    HttpMethod.POST,
                    requestEntity,
                    ResponseDto.class
            );
            HttpStatusCode statusCode = responseEntity.getStatusCode();
            if (statusCode != HttpStatus.OK) {
                LOGGER.error("Failed to send email with status code: " + statusCode);
                throw new EmailSendingException("Failed to send email with status code: " + statusCode);
            }
        } catch (Exception e){
            LOGGER.error("Unauthorized to send email!");
            throw new EmailSendingException("Failed to send email!");
        }
    }

    /**
     * Logs in a user with the provided username and password.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return the logged-in user DTO
     * @throws UserNotFoundException if the user is not found
     */
    public UserDTO login(String username, String password) throws UserNotFoundException {
        try {
            User user = userRepository.findByUsername(username);
            if (user == null) {
                throw new UserNotFoundException("User not found");
            }
            if (!user.getPassword().equals(password) || !user.getUsername().equals(username)) {
                throw new IllegalArgumentException("Incorrect password or username");
            }
            UserDTO userDTO = UserBuilder.toUserDTO(user);
            session.setAttribute(SESSION_USER_KEY, userDTO);
            return userDTO;
        } catch (UserNotFoundException e) {
            LOGGER.error("User not found: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            LOGGER.error("Incorrect password or username: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.error("An error occurred during login", e);
            throw e;
        }
    }

    /**
     * Logs out the currently logged-in user.
     */
    public void logout() {
        session.removeAttribute(SESSION_USER_KEY);
    }

    /**
     * Retrieves the currently logged-in user.
     *
     * @return the currently logged-in user DTO
     */
    public UserDTO getLoggedInUser() {
        return (UserDTO) session.getAttribute(SESSION_USER_KEY);
    }

    /**
     * Retrieves all users.
     *
     * @return a list of all user DTOs
     */
    public List<UserDTO> getAllUsers() {
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .map(UserBuilder::toUserDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id the ID of the user
     * @return the user DTO
     * @throws UserNotFoundException if the user is not found
     */
    public UserDTO getUserById(UUID id) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            LOGGER.error("User with id {} was not found in db", id);
            throw new UserNotFoundException("User with id " + id + " not found!");
        }
        return UserBuilder.toUserDTO(userOptional.get());
    }

    /**
     * Creates a new user.
     *
     * @param userDTO the user DTO containing user information
     * @return the created user DTO
     */
    public UserDTO createUser(UserDTO userDTO) {
        if (!isValidName(userDTO.getFirstName()) || !isValidName(userDTO.getLastName())) {
            LOGGER.error("Invalid first name or last name. First name: {}, Last name: {}", userDTO.getFirstName(), userDTO.getLastName());
            throw new IllegalArgumentException("First name and last name must be alphanumeric and less than 20 characters long.");
        }

        if (!isValidUsername(userDTO.getUsername()) || !isValidPassword(userDTO.getPassword())) {
            LOGGER.error("Invalid username or password. Username: {}, Password: {}", userDTO.getUsername(), userDTO.getPassword());
            throw new IllegalArgumentException("Username and password must be alphanumeric and less than 20 characters long.");
        }

        if (!isValidEmail(userDTO.getEmail())) {
            LOGGER.error("Invalid email format: {}", userDTO.getEmail());
            throw new IllegalArgumentException("Invalid email format");
        }

        if (!isValidPhoneNumber(userDTO.getPhoneNumber())) {
            LOGGER.error("Invalid phone number format: {}", userDTO.getPhoneNumber());
            throw new IllegalArgumentException("Invalid phone number format. Phone number must start with '+40' followed by 9 digits.");
        }

        User user = UserBuilder.toEntity(userDTO);
        user = userRepository.save(user);
        LOGGER.debug("User with id {} was inserted in db", user.getId());
        LOGGER.info("User created successfully");
        return UserBuilder.toUserDTO(user);
    }

    /**
    * Updates an existing user.
    *
    * @param id      the ID of the user to update
    * @param userDTO the user DTO containing updated information
    * @return the updated user DTO
    * @throws  UserNotFoundException if the user is not found
    **/
    public UserDTO updateUser(UUID id, UserDTO userDTO) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            LOGGER.error("User with id {} was not found in db", id);
            throw new UserNotFoundException("User with id " + id + " not found!");
        }

        User existingUser = userOptional.get();

        if (!isValidName(userDTO.getFirstName()) || !isValidName(userDTO.getLastName())) {
            LOGGER.error("Invalid first name or last name. First name: {}, Last name: {}", userDTO.getFirstName(), userDTO.getLastName());
            throw new IllegalArgumentException("First name and last name must be alphanumeric and less than 20 characters long.");
        }

        if (!isValidUsername(userDTO.getUsername()) || !isValidPassword(userDTO.getPassword())) {
            LOGGER.error("Invalid username or password. Username: {}, Password: {}", userDTO.getUsername(), userDTO.getPassword());
            throw new IllegalArgumentException("Username and password must be alphanumeric and less than 20 characters long.");
        }

        if (!isValidEmail(userDTO.getEmail())) {
            LOGGER.error("Invalid email format: {}", userDTO.getEmail());
            throw new IllegalArgumentException("Invalid email format");
        }

        if (!isValidPhoneNumber(userDTO.getPhoneNumber())) {
            LOGGER.error("Invalid phone number format: {}", userDTO.getPhoneNumber());
            throw new IllegalArgumentException("Invalid phone number format. Phone number must start with '+40' followed by 9 digits.");
        }

        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setUsername(userDTO.getUsername());
        existingUser.setPassword(userDTO.getPassword());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setPhoneNumber(userDTO.getPhoneNumber());
        existingUser.setUserRole(userDTO.getUserRole());

        User updatedUser = userRepository.save(existingUser);

        LOGGER.debug("User with id {} was updated successfully", id);
        LOGGER.info("User with id {} was updated successfully", id);

        return UserBuilder.toUserDTO(updatedUser);
    }

    /**
     * Deletes a user by ID.
     *
     * @param id the ID of the user to delete
     * @throws UserNotFoundException if the user is not found
     */
    public void deleteUser(UUID id) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            LOGGER.error("User with id {} was not found in db", id);
            throw new UserNotFoundException("User with id " + id + " not found!");
        }

        User user = userOptional.get();
        userRepository.deleteById(id);
        LOGGER.info("User with id {} was deleted successfully", id);

        List<Contract> userContracts = user.getContracts();

        for (Contract contract : userContracts) {
            Property property = contract.getProperty();
            property.setRented(false);
            propertyRepository.save(property);
        }
    }

    /**
     * Checks if a name is valid.
     *
     * @param name the name to validate
     * @return true if the name is valid, false otherwise
     */
    public boolean isValidName(String name) {
        return name != null && name.matches("[a-zA-Z]+") && name.length() <= 30;
    }

    /**
     * Checks if a username is valid.
     *
     * @param username the username to validate
     * @return true if the username is valid, false otherwise
     */
    public boolean isValidUsername(String username) {
        return username != null && username.length() <= 30;
    }

    /**
     * Checks if a password is valid.
     *
     * @param password the password to validate
     * @return true if the password is valid, false otherwise
     */
    public boolean isValidPassword(String password) {
        return password != null && password.length() <= 30;
    }

    /**
     * Checks if an email is valid.
     *
     * @param email the email to validate
     * @return true if the email is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches(EMAIL_REGEX);
    }

    /**
     * Checks if a phone number is valid.
     *
     * @param phoneNumber the phone number to validate
     * @return true if the phone number is valid, false otherwise
     */
    private boolean isValidPhoneNumber(String phoneNumber) {
        String PHONE_NUMBER_REGEX = "^\\+40\\d{9}$";
        return phoneNumber != null && phoneNumber.matches(PHONE_NUMBER_REGEX);
    }
}
