package com.projectps.buildingmanagement.controllers.pages;

import com.projectps.buildingmanagement.dtos.UserDTO;
import com.projectps.buildingmanagement.entities.enums.UserType;
import com.projectps.buildingmanagement.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller class for managing customer-related pages.
 */
@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private UserService userService;

    /**
     * Displays the home page for customers.
     *
     * @return The name of the home page view.
     */
    @GetMapping("/")
    public String homePage() {
        return "home";
    }

    /**
     * Redirects to the home page if the logged-in user is not a customer.
     *
     * @param loggedInUser The logged-in user DTO.
     * @return A redirection to the home page if the user is not a customer, otherwise null.
     */
    private String redirectToHomePageIfNotCustomer(UserDTO loggedInUser) {
        if (loggedInUser == null || loggedInUser.getUserRole() != UserType.CUSTOMER) {
            return "redirect:/admin";
        }
        return null; // Return null if user is customer
    }

    /**
     * Displays the properties page for customers.
     *
     * @return The name of the properties page view.
     */
    @GetMapping("/properties")
    public String showPropertiesPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotCustomer(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "properties_customer";
    }

    /**
     * Displays the contracts page for customers.
     *
     * @return The name of the contracts page view.
     */
    @GetMapping("/contracts")
    public String showContractsPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotCustomer(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "contracts_customer";
    }

    /**
     * Displays the reviews page for customers.
     *
     * @return The name of the reviews page view.
     */
    @GetMapping("/reviews")
    public String showReviewsPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotCustomer(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "reviews_customer";
    }

    /**
     * Displays the solicitations page for customers.
     *
     * @return The name of the solicitations page view.
     */
    @GetMapping("/solicitations")
    public String showSolicitationsPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotCustomer(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "requests_customer";
    }

    /**
     * Redirects to the properties list page for customers.
     *
     * @return A redirection to the properties list page.
     */
    @GetMapping("/properties/propertiesListCustomer")
    public String showPropertiesList() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotCustomer(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/properties/propertiesListCustomer";
    }

    /**
     * Redirects to the contracts list page for customers.
     *
     * @return A redirection to the contracts list page.
     */
    @GetMapping("/contracts/contractsListCustomer")
    public String showContractsList() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotCustomer(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/contracts/contractsListCustomer";
    }

    /**
     * Redirects to the search contract page for customers.
     *
     * @return A redirection to the search contract page.
     */
    @GetMapping("/contracts/searchContractCustomer")
    public String showSearchContractPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotCustomer(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/contracts/searchContractCustomer";
    }

    /**
     * Redirects to the reviews list page for customers.
     *
     * @return A redirection to the reviews list page.
     */
    @GetMapping("/reviews/reviewsListCustomer")
    public String showReviewsList() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotCustomer(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/reviews/reviewsListCustomer";
    }

    /**
     * Redirects to the search review page for customers.
     *
     * @return A redirection to the search review page.
     */
    @GetMapping("/reviews/searchReviewCustomer")
    public String showSearchReviewPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotCustomer(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/reviews/searchReviewCustomer";
    }

    /**
     * Redirects to the insert review page for customers.
     *
     * @return A redirection to the insert review page.
     */
    @GetMapping("/reviews/insertReviewCustomer")
    public String showInsertReviewPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotCustomer(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/reviews/insertReviewCustomer";
    }

    /**
     * Redirects to the delete review page for customers.
     *
     * @return A redirection to the delete review page.
     */
    @GetMapping("/reviews/deleteReviewCustomer")
    public String showDeleteReviewPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotCustomer(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/reviews/deleteReviewCustomer";
    }

    /**
     * Redirects to the update review page for customers.
     *
     * @return A redirection to the update review page.
     */
    @GetMapping("/reviews/updateReviewCustomer")
    public String showUpdateReviewPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotCustomer(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/reviews/updateReviewCustomer";
    }
}
