package com.projectps.buildingmanagement.controllers.pages;

import com.projectps.buildingmanagement.dtos.UserDTO;
import com.projectps.buildingmanagement.entities.enums.UserType;
import com.projectps.buildingmanagement.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller class for managing admin-related pages.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    /**
     * Displays the home page for admins.
     *
     * @return The name of the home page view.
     */
    @GetMapping("/")
    public String homePage() {
        return "home";
    }

    /**
     * Redirects to the home page if the logged-in user is not an admin.
     *
     * @param loggedInUser The logged-in user DTO.
     * @return A redirection to the home page if the user is not an admin, otherwise null.
     */
    private String redirectToHomePageIfNotAdmin(UserDTO loggedInUser) {
        if (loggedInUser == null || loggedInUser.getUserRole() != UserType.ADMIN) {
            return "redirect:/customer";
        }
        return null; // Return null if user is admin
    }

    /**
     * Displays the users page for admins.
     *
     * @return The name of the users page view.
     */
    @GetMapping("/users")
    public String showUsersPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotAdmin(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "users_admin";
    }

    /**
     * Displays the properties page for admins.
     *
     * @return The name of the properties page view.
     */
    @GetMapping("/properties")
    public String showPropertiesPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotAdmin(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "properties_admin";
    }

    /**
     * Displays the contracts page for admins.
     *
     * @return The name of the contracts page view.
     */
    @GetMapping("/contracts")
    public String showContractsPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotAdmin(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "contracts_admin";
    }

    /**
     * Displays the offers page for admins.
     *
     * @return The name of the offers page view.
     */
    @GetMapping("/offers")
    public String showOffersPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotAdmin(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "offers_admin";
    }

    /**
     * Displays the reviews page for admins.
     *
     * @return The name of the reviews page view.
     */
    @GetMapping("/reviews")
    public String showReviewsPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotAdmin(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "reviews_admin";
    }

    /**
     * Displays the solicitations page for admins.
     *
     * @return The name of the solicitations page view.
     */
    @GetMapping("/solicitations")
    public String showSolicitationsPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotAdmin(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "requests_admin";
    }

    /**
     * Redirects to the users list page for admins.
     *
     * @return A redirection to the users list page.
     */
    @GetMapping("/users/usersList")
    public String showUsersList() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotAdmin(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/users/usersList";
    }

    /**
     * Redirects to the properties list page for admins.
     *
     * @return A redirection to the properties list page.
     */
    @GetMapping("/properties/propertiesList")
    public String showPropertiesList() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotAdmin(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/properties/propertiesList";
    }

    /**
     * Redirects to the contracts list page for admins.
     *
     * @return A redirection to the contracts list page.
     */
    @GetMapping("/contracts/contractsList")
    public String showContractsList() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotAdmin(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/contracts/contractsList";
    }

    /**
     * Redirects to the offers list page for admins.
     *
     * @return A redirection to the offers list page.
     */
    @GetMapping("/offers/offersList")
    public String showOffersList() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotAdmin(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/offers/offersList";
    }

    /**
     * Redirects to the reviews list page for admins.
     *
     * @return A redirection to the reviews list page.
     */
    @GetMapping("/reviews/reviewsList")
    public String showReviewsList() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotAdmin(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/reviews/reviewsList";
    }

    /**
     * Redirects to the search user page for admins.
     *
     * @return A redirection to the search user page.
     */
    @GetMapping("/users/searchUser")
    public String showSearchUserPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotAdmin(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/users/searchUser";
    }

    /**
     * Redirects to the search property page for admins.
     *
     * @return A redirection to the search property page.
     */
    @GetMapping("/properties/searchProperty")
    public String showSearchPropertyPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotAdmin(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/properties/searchProperty";
    }

    /**
     * Redirects to the search contract page for admins.
     *
     * @return A redirection to the search contract page.
     */
    @GetMapping("/contracts/searchContract")
    public String showSearchContractPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotAdmin(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/contracts/searchContract";
    }

    /**
     * Redirects to the search offer page for admins.
     *
     * @return A redirection to the search offer page.
     */
    @GetMapping("/offers/searchOffer")
    public String showSearchOfferPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotAdmin(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/offers/searchOffer";
    }

    /**
     * Redirects to the search review page for admins.
     *
     * @return A redirection to the search review page.
     */
    @GetMapping("/reviews/searchReview")
    public String showSearchReviewPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotAdmin(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/reviews/searchReview";
    }

    /**
     * Redirects to the insert user page for admins.
     *
     * @return A redirection to the insert user page.
     */
    @GetMapping("/users/insertUser")
    public String showInsertUserPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotAdmin(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/users/insertUser";
    }

    /**
     * Redirects to the insert property page for admins.
     *
     * @return A redirection to the insert property page.
     */
    @GetMapping("/properties/insertProperty")
    public String showInsertPropertyPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotAdmin(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/properties/insertProperty";
    }

    /**
     * Redirects to the insert contract page for admins.
     *
     * @return A redirection to the insert contract page.
     */
    @GetMapping("/contracts/insertContract")
    public String showInsertContractPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotAdmin(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/contracts/insertContract";
    }

    /**
     * Redirects to the insert offer page for admins.
     *
     * @return A redirection to the insert offer page.
     */
    @GetMapping("/offers/insertOffer")
    public String showInsertOfferPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotAdmin(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/offers/insertOffer";
    }

    /**
     * Redirects to the delete user page for admins.
     *
     * @return A redirection to the delete user page.
     */
    @GetMapping("/users/deleteUser")
    public String showDeleteUserPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotAdmin(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/users/deleteUser";
    }

    /**
     * Redirects to the delete property page for admins.
     *
     * @return A redirection to the delete property page.
     */
    @GetMapping("/properties/deleteProperty")
    public String showDeletePropertyPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotAdmin(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/properties/deleteProperty";
    }

    /**
     * Redirects to the delete contract page for admins.
     *
     * @return A redirection to the delete contract page.
     */
    @GetMapping("/contracts/deleteContract")
    public String showDeleteContractPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotAdmin(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/contracts/deleteContract";
    }

    /**
     * Redirects to the delete offer page for admins.
     *
     * @return A redirection to the delete offer page.
     */
    @GetMapping("/offers/deleteOffer")
    public String showDeleteOfferPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotAdmin(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/offers/deleteOffer";
    }

    /**
     * Redirects to the update user page for admins.
     *
     * @return A redirection to the update user page.
     */
    @GetMapping("/users/updateUser")
    public String showUpdateUserPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotAdmin(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/users/updateUser";
    }

    /**
     * Redirects to the update property page for admins.
     *
     * @return A redirection to the update property page.
     */
    @GetMapping("/properties/updateProperty")
    public String showUpdatePropertyPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotAdmin(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/properties/updateProperty";
    }

    /**
     * Redirects to the update contract page for admins.
     *
     * @return A redirection to the update contract page.
     */
    @GetMapping("/contracts/updateContract")
    public String showUpdateContractPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotAdmin(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/contracts/updateContract";
    }

    /**
     * Redirects to the update offer page for admins.
     *
     * @return A redirection to the update offer page.
     */
    @GetMapping("/offers/updateOffer")
    public String showUpdateOfferPage() {
        UserDTO loggedInUser = userService.getLoggedInUser();
        String redirectPage = redirectToHomePageIfNotAdmin(loggedInUser);
        if (redirectPage != null) {
            return redirectPage;
        }
        return "redirect:/offers/updateOffer";
    }

}
