package com.projectps.buildingmanagement.controllers.pages;

import com.projectps.buildingmanagement.dtos.UserDTO;
import com.projectps.buildingmanagement.entities.enums.UserType;
import com.projectps.buildingmanagement.exceptions.UserNotFoundException;
import com.projectps.buildingmanagement.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller class for managing pages related to user authentication and navigation.
 */
@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpSession session;

    /**
     * Displays the home page.
     *
     * @return The name of the home page view.
     */
    @GetMapping("/")
    public String homePage() {
        return "home";
    }

    /**
     * Displays the admin page if the logged-in user is an admin.
     * Otherwise, redirects to the customer page.
     *
     * @return The name of the admin page view or a redirection to the customer page.
     */
    @GetMapping("/admin")
    public String adminPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        if (loggedInUser != null && loggedInUser.getUserRole() == UserType.ADMIN) {
            return "admin";
        } else {
            return "redirect:/customer"; // Redirect to customer page if not authorized
        }
    }

    /**
     * Displays the customer page if the logged-in user is a customer.
     * Otherwise, redirects to the admin page.
     *
     * @return The name of the customer page view or a redirection to the admin page.
     */
    @GetMapping("/customer")
    public String customerPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        if (loggedInUser != null && loggedInUser.getUserRole() == UserType.CUSTOMER) {
            return "customer";
        } else {
            return "redirect:/admin"; // Redirect to admin page if not authorized
        }
    }

    /**
     * Handles user login and redirects to the appropriate page based on user role.
     *
     * @param username The username.
     * @param password The password.
     * @return A ModelAndView indicating the result of the login attempt.
     */
    @PostMapping("/")
    public ModelAndView login(@RequestParam String username, @RequestParam String password) {
        ModelAndView modelAndView = new ModelAndView("home");
        try {
            UserDTO user = userService.login(username, password);
            session.setAttribute("user_id", user);
            if (user.getUserRole() == UserType.ADMIN) {
                modelAndView.setViewName("redirect:/admin");
            } else {
                modelAndView.setViewName("redirect:/customer");
            }
        } catch (UserNotFoundException e) {
            modelAndView.addObject("error", "Invalid username or password");
        } catch (Exception e) {
            modelAndView.addObject("error", "Invalid username or password, please try again!");
            modelAndView.addObject("unexpectedError", true);
        }
        return modelAndView;
    }

    /**
     * Logs out the user and redirects to the home page.
     *
     * @return A ModelAndView for redirecting to the home page.
     */
    @GetMapping("/logout")
    public ModelAndView logout() {
        userService.logout();
        session.removeAttribute("user_id");
        return new ModelAndView("redirect:/");
    }

    /**
     * Retrieves the logged-in user.
     *
     * @return The logged-in user.
     */
    @GetMapping("/loggedin")
    public UserDTO getLoggedInUser() {
        return userService.getLoggedInUser();
    }

    /**
     * Displays the sign-up page.
     *
     * @return A ModelAndView for displaying the sign-up page.
     */
    @GetMapping("/signup")
    public ModelAndView showSignUpPage() {
        ModelAndView modelAndView = new ModelAndView("signup");
        return modelAndView;
    }

    /**
     * Handles user sign-up.
     *
     * @param userDTO The user DTO containing user information.
     * @return A ModelAndView indicating the result of the sign-up attempt.
     */
    @PostMapping("/signup")
    public ModelAndView signUp(@ModelAttribute UserDTO userDTO) {
        ModelAndView modelAndView = new ModelAndView("signup");
        try {
            userDTO.setUserRole(UserType.CUSTOMER);
            UserDTO createdUser = userService.createUser(userDTO);
            modelAndView.addObject("created_user", createdUser);
            modelAndView.addObject("success_message", "User created successfully!");
        } catch (IllegalArgumentException e) {
            modelAndView.addObject("error", e.getMessage());
        } catch (Exception e) {
            modelAndView.addObject("error", "Failed to create user");
        }
        return modelAndView;
    }
}