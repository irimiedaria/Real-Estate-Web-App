package com.projectps.buildingmanagement.controllers;

import com.projectps.buildingmanagement.dtos.ReviewDTO;
import com.projectps.buildingmanagement.exceptions.ReviewNotFoundException;
import com.projectps.buildingmanagement.services.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * This controller class handles HTTP requests related to property reviews.
 */
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    private HttpSession session;

    /**
     * Constructs a new ReviewController with the specified ReviewService and HttpSession.
     *
     * @param reviewService the review service to use
     * @param session       the HTTP session
     */
    @Autowired
    public ReviewController(ReviewService reviewService, HttpSession session) {

        this.reviewService = reviewService;
        this.session = session;
    }

    /**
     * Retrieves all property reviews.
     *
     * @return a ModelAndView containing the view "reviewsList" and a list of review DTOs
     */
    @GetMapping("/reviewsList")
    public ModelAndView getAllReviews() {
        List<ReviewDTO> dtos = reviewService.getAllReviews();
        ModelAndView modelAndView = new ModelAndView("reviewsList");
        modelAndView.addObject("review", dtos);
        return modelAndView;
    }

    /**
     * Retrieves all property reviews for customers.
     *
     * @return a ModelAndView containing the view "reviewsListCustomer" and a list of review DTOs
     */
    @GetMapping("/reviewsListCustomer")
    public ModelAndView getAllReviewsCustomer() {
        List<ReviewDTO> dtos = reviewService.getAllReviewsCustomer();
        ModelAndView modelAndView = new ModelAndView("reviewsListCustomer");
        modelAndView.addObject("review", dtos);
        return modelAndView;
    }

    /**
     * Retrieves a review by its ID.
     *
     * @param id the ID of the review to retrieve
     * @return a ModelAndView containing the view "searchReview" and the retrieved review DTO
     */
    @GetMapping("/searchReview")
    public ModelAndView getReviewById(@RequestParam(required = false) String id) {
        ModelAndView modelAndView = new ModelAndView("searchReview");
        if (id == null || id.isEmpty()) {
            return modelAndView;
        }
        try {
            UUID reviewId = UUID.fromString(id);
            ReviewDTO reviewDTO = reviewService.getReviewById(reviewId);
            modelAndView.addObject("searched_review", reviewDTO);
        } catch (IllegalArgumentException e) {
            modelAndView.addObject("error", "Invalid UUID format for ID: " + id);
        } catch (ReviewNotFoundException e) {
            modelAndView.addObject("error", "Review with ID " + id + " not found");
        } catch (Exception e) {
            modelAndView.addObject("error", "Failed to retrieve review");
        }
        return modelAndView;
    }

    /**
     * Retrieves a review by its ID for customers.
     *
     * @param id the ID of the review to retrieve
     * @return a ModelAndView containing the view "searchReviewCustomer" and the retrieved review DTO
     */
    @GetMapping("/searchReviewCustomer")
    public ModelAndView getReviewByIdCustomer(@RequestParam(required = false) String id) {
        ModelAndView modelAndView = new ModelAndView("searchReviewCustomer");
        if (id == null || id.isEmpty()) {
            return modelAndView;
        }
        try {
            UUID reviewId = UUID.fromString(id);
            ReviewDTO reviewDTO = reviewService.getReviewByIdCustomer(reviewId);
            modelAndView.addObject("searched_review", reviewDTO);
        } catch (IllegalArgumentException e) {
            modelAndView.addObject("error", "Invalid UUID format for ID: " + id);
        } catch (ReviewNotFoundException e) {
            modelAndView.addObject("error", "Review with ID " + id + " not found");
        } catch (Exception e) {
            modelAndView.addObject("error", "Failed to retrieve review");
        }
        return modelAndView;
    }

    /**
     * Displays the insert review page for customers.
     *
     * @return a ModelAndView containing the view "insertReviewCustomer"
     */
    @GetMapping("/insertReviewCustomer")
    public ModelAndView showInsertReviewPage() {
        ModelAndView modelAndView = new ModelAndView("insertReviewCustomer");
        return modelAndView;
    }

    /**
     * Creates a new review for a property by a customer.
     *
     * @param reviewDTO the review DTO containing review details
     * @return a ModelAndView containing the view "insertReviewCustomer" and a success message or an error message
     */
    @PostMapping("/insertReviewCustomer")
    public ModelAndView createReview(@ModelAttribute ReviewDTO reviewDTO) {
        ModelAndView modelAndView = new ModelAndView("insertReviewCustomer");
        try {
            ReviewDTO createdReview = reviewService.createReviewCustomer(reviewDTO);
            modelAndView.addObject("created_review", createdReview);
            modelAndView.addObject("success_message", "Review created successfully!");
        } catch (IllegalArgumentException e) {
            modelAndView.addObject("error", e.getMessage());
        } catch (Exception e) {
            modelAndView.addObject("error", "Failed to create review");
        }
        return modelAndView;
    }

    /**
     * Retrieves a review for updating by its ID for customers.
     *
     * @param id the ID of the review to update
     * @return a ModelAndView containing the view "updateReviewCustomer" and the review DTO to update
     */
    @GetMapping("/updateReviewCustomer")
    public ModelAndView getReviewForUpdate(@RequestParam(required = false) String id) {
        ModelAndView modelAndView = new ModelAndView("updateReviewCustomer");
        if(id == null || id.isEmpty()) {
            return modelAndView;
        }
        try {
            UUID reviewId = UUID.fromString(id);
            ReviewDTO reviewDTO = reviewService.getReviewByIdCustomer(reviewId);
            modelAndView.addObject("updated_review", reviewDTO);
        } catch (IllegalArgumentException e) {
            modelAndView.addObject("error", "Invalid UUID format for ID: " + id);
        } catch (ReviewNotFoundException e) {
            modelAndView.addObject("error", "Review with ID " + id + " not found");
        } catch (Exception e) {
            modelAndView.addObject("error", "Failed to retrieve review");
        }
        return modelAndView;
    }

    /**
     * Updates a review for customers.
     *
     * @param id       the ID of the review to update
     * @param reviewDTO the updated review DTO containing review details
     * @return a ModelAndView containing the view "updateReviewCustomer" and a success message or an error message
     */
    @PostMapping("/updateReviewCustomer")
    public ModelAndView updateReviewCustomer(@RequestParam(required = false) String id, @ModelAttribute ReviewDTO reviewDTO) {
        ModelAndView modelAndView = new ModelAndView("updateReviewCustomer");
        try {
            UUID reviewId = UUID.fromString(id);

            LocalDateTime reviewDate = reviewDTO.getDate();
            if (reviewDate != null && reviewDate.isBefore(LocalDateTime.now())) {
                modelAndView.addObject("error", "Review date cannot be in the past");
                return modelAndView;
            }

            ReviewDTO updatedReview = reviewService.updateReviewCustomer(reviewId, reviewDTO);
            modelAndView.addObject("updated_review", updatedReview);
            modelAndView.addObject("success_message", "Review updated successfully!");
        } catch (IllegalArgumentException e) {
            modelAndView.addObject("error", "Invalid UUID format for ID: " + id);
        } catch (ReviewNotFoundException e) {
            modelAndView.addObject("error", "Review with ID " + id + " not found!");
        } catch (Exception e) {
            modelAndView.addObject("error", "Failed to update review");
        }
        return modelAndView;
    }

    /**
     * Deletes a review for customers.
     *
     * @param id the ID of the review to delete
     * @return a ModelAndView redirecting to the reviewsListCustomer page with an error message or a success message
     */
    @PostMapping("/deleteReviewCustomer")
    public ModelAndView deleteReviewCustomer(@RequestParam(name = "id") UUID id) {
        try {
            reviewService.deleteReviewCustomer(id);
            ModelAndView modelAndView = new ModelAndView("redirect:/reviews/reviewsListCustomer");
            return modelAndView;
        } catch (ReviewNotFoundException e) {
            ModelAndView modelAndView = new ModelAndView("redirect:/reviews/reviewsListCustomer");
            modelAndView.addObject("errorMessage", "Review not found!");
            return modelAndView;
        } catch (Exception e) {
            ModelAndView modelAndView = new ModelAndView("redirect:/reviews/reviewsListCustomer");
            modelAndView.addObject("errorMessage", "Failed to delete review");
            return modelAndView;
        }
    }
}
