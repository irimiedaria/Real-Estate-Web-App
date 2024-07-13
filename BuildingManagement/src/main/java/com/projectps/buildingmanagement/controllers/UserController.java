package com.projectps.buildingmanagement.controllers;

import com.projectps.buildingmanagement.dtos.UserDTO;
import com.projectps.buildingmanagement.exceptions.EmailSendingException;
import com.projectps.buildingmanagement.exceptions.UserNotFoundException;
import com.projectps.buildingmanagement.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

/**
 * This controller class handles HTTP requests related to user management.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Value("${authorization.token1}")
    private String authorizationToken1;

    @Value("${authorization.token2}")
    private String authorizationToken2;

    /**
     * Retrieves all users.
     *
     * @return a ModelAndView containing the view "usersList" and a list of user DTOs
     */
    @GetMapping("/usersList")
    public ModelAndView getAllUsers() {
        // Retrieves all users from the UserService
        List<UserDTO> dtos = userService.getAllUsers();
        ModelAndView modelAndView = new ModelAndView("usersList");
        modelAndView.addObject("user", dtos);
        return modelAndView;
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id the ID of the user
     * @return a ModelAndView containing the view "searchUser" and the searched user DTO
     */
    @GetMapping("/searchUser")
    public ModelAndView getUserById(@RequestParam(required = false) String id) {
        ModelAndView modelAndView = new ModelAndView("searchUser");
        if (id == null || id.isEmpty()) {
            return modelAndView;
        }
        try {
            UUID userId = UUID.fromString(id);
            UserDTO userDTO = userService.getUserById(userId);
            modelAndView.addObject("searched_user", userDTO);
        } catch (IllegalArgumentException e) {
            modelAndView.addObject("error", "Invalid UUID format for ID: " + id);
        } catch (UserNotFoundException e) {
            modelAndView.addObject("error", "User with ID " + id + " not found");
        } catch (Exception e) {
            modelAndView.addObject("error", "Failed to retrieve user");
        }
        return modelAndView;
    }

    /**
     * Displays the insert user page.
     *
     * @return a ModelAndView containing the view "insertUser"
     */
    @GetMapping("/insertUser")
    public ModelAndView showInsertUserPage() {
        ModelAndView modelAndView = new ModelAndView("insertUser");
        return modelAndView;
    }

    /**
     * Creates a new user.
     *
     * @param userDTO the user DTO containing user information
     * @return a ModelAndView containing the view "insertUser" and the created user DTO
     */
    @PostMapping("/insertUser")
    public ModelAndView createUser(@ModelAttribute UserDTO userDTO) {
        ModelAndView modelAndView = new ModelAndView("insertUser");
        try {
            UserDTO createdUser = userService.createUser(userDTO);

            userService.sendEmailToUser(createdUser, "Welcome to our platform", "Dear " + createdUser.getFirstName() + ",\n\nWelcome to our platform! We are excited to have you as a member.\n\nBest regards,\nThe Building Management Team", authorizationToken1, authorizationToken2);

            modelAndView.addObject("created_user", createdUser);
            modelAndView.addObject("success_message", "User created successfully!");
        } catch (EmailSendingException e) {
            modelAndView.addObject("error", "Failed to send email");
        } catch (IllegalArgumentException e) {
            modelAndView.addObject("error", e.getMessage());
        } catch (Exception e) {
            modelAndView.addObject("error", "Failed to create user");
        }
        return modelAndView;
    }

    /**
     * Displays the update user page.
     *
     * @param id the ID of the user to update
     * @return a ModelAndView containing the view "updateUser" and the user DTO to be updated
     */
    @GetMapping("/updateUser")
    public ModelAndView getUserForUpdate(@RequestParam(required = false) String id) {
        ModelAndView modelAndView = new ModelAndView("updateUser");
        if (id == null || id.isEmpty()) {
            return modelAndView;
        }
        try {
            UUID userId = UUID.fromString(id);
            UserDTO userDTO = userService.getUserById(userId);
            modelAndView.addObject("updated_user", userDTO);
        } catch (IllegalArgumentException e) {
            modelAndView.addObject("error", "Invalid UUID format for ID: " + id);
        } catch (UserNotFoundException e) {
            modelAndView.addObject("error", "User with ID " + id + " not found");
        } catch (Exception e) {
            modelAndView.addObject("error", "Failed to retrieve user");
        }
        return modelAndView;
    }

    /**
     * Updates a user.
     *
     * @param id      the ID of the user to update
     * @param userDTO the updated user DTO
     * @return a ModelAndView containing the view "updateUser" and the updated user DTO
     */
    @PostMapping("/updateUser")
    public ModelAndView updateUser(@RequestParam(required = false) String id, @ModelAttribute UserDTO userDTO) {
        ModelAndView modelAndView = new ModelAndView("updateUser");
        if (id == null || id.isEmpty()) {
            modelAndView.addObject("error", "ID is required.");
            return modelAndView;
        }
        try {
            UUID userId = UUID.fromString(id);
            UserDTO updatedUser = userService.updateUser(userId, userDTO);
            modelAndView.addObject("updated_user", updatedUser);
            modelAndView.addObject("success_message", "User with ID " + id + " was updated successfully!");
        } catch (IllegalArgumentException e) {
            modelAndView.addObject("error", e.getMessage());
        } catch (UserNotFoundException e) {
            modelAndView.addObject("error", "User with ID " + id + " not found!");
        } catch (Exception e) {
            modelAndView.addObject("error", "Failed to update user");
        }
        return modelAndView;
    }

    /**
     * Deletes a user by ID.
     *
     * @param id the ID of the user to delete
     * @return a ModelAndView redirecting to the usersList page
     */
    @PostMapping("/deleteUser")
    public ModelAndView deleteUser(@RequestParam UUID id) {
        try {
            userService.deleteUser(id);
            ModelAndView modelAndView = new ModelAndView("redirect:/users/usersList");
            return modelAndView;
        } catch (UserNotFoundException e) {
            ModelAndView modelAndView = new ModelAndView("redirect:/users/usersList");
            modelAndView.addObject("errorMessage", "User not found!");
            return modelAndView;
        } catch (Exception e) {
            ModelAndView modelAndView = new ModelAndView("redirect:/users/usersList");
            modelAndView.addObject("errorMessage", "Failed to delete user");
            return modelAndView;
        }
    }
}