package com.projectps.buildingmanagement.services;


import com.projectps.buildingmanagement.dtos.ReviewDTO;
import com.projectps.buildingmanagement.dtos.UserDTO;
import com.projectps.buildingmanagement.dtos.builders.ReviewBuilder;
import com.projectps.buildingmanagement.entities.Review;
import com.projectps.buildingmanagement.entities.User;
import com.projectps.buildingmanagement.exceptions.ReviewNotFoundException;
import com.projectps.buildingmanagement.repositories.ReviewRepository;
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
 * This service class provides various functionalities related to review management.
 */
@Service
public class ReviewService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewService.class);
    private static final String SESSION_USER_KEY = "logged_user";

    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    private final HttpSession session;

    /**
     * Constructs a new ReviewService with the specified repository and session.
     *
     * @param reviewRepository the repository for accessing review data
     * @param session          the HTTP session
     */
    @Autowired
    public ReviewService(ReviewRepository reviewRepository, HttpSession session) {
        this.reviewRepository = reviewRepository;
        this.session = session;
    }

    /**
     * Retrieves all reviews.
     *
     * @return a list of all review DTOs
     */
    public List<ReviewDTO> getAllReviews() {
        List<Review> reviewList = reviewRepository.findAll();
        return reviewList.stream()
                .map(ReviewBuilder::toReviewDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all reviews for the logged-in customer.
     *
     * @return a list of all review DTOs for the logged-in customer
     */
    public List<ReviewDTO> getAllReviewsCustomer() {
        UserDTO loggedUser = (UserDTO) session.getAttribute("logged_user");
        if (loggedUser == null || loggedUser.getId() == null) {
            LOGGER.error("Customer ID not found in session");
            throw new IllegalStateException("Customer ID not found in session");
        }
        List<Review> reviewList = reviewRepository.findByUserId(loggedUser.getId());
        return reviewList.stream()
                .map(ReviewBuilder::toReviewDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a review by ID.
     *
     * @param id the ID of the review
     * @return the review DTO
     * @throws ReviewNotFoundException if the review is not found
     */
    public ReviewDTO getReviewById(UUID id) throws ReviewNotFoundException {
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        if (!reviewOptional.isPresent()) {
            LOGGER.error("Review with id {} was not found in db", id);
            throw new ReviewNotFoundException(Review.class.getSimpleName() + "with id: " + id);
        }
        return ReviewBuilder.toReviewDTO(reviewOptional.get());
    }

    /**
     * Retrieves a review by ID for the logged-in customer.
     *
     * @param id the ID of the review
     * @return the review DTO
     * @throws ReviewNotFoundException if the review is not found
     */
    public ReviewDTO getReviewByIdCustomer(UUID id) throws ReviewNotFoundException {
        UserDTO loggedUser = (UserDTO) session.getAttribute("logged_user");
        if (loggedUser.getId() == null) {
            LOGGER.error("Customer ID not found in session");
            throw new IllegalStateException("Customer ID not found in session");
        }

        Optional<Review> reviewOptional = reviewRepository.findByIdAndUserId(id, loggedUser.getId());
        if (!reviewOptional.isPresent()) {
            LOGGER.error("Review with id {} was not found for customer with id {}", id, loggedUser.getId());
            throw new ReviewNotFoundException("Review not found with id: " + id);
        }

        return ReviewBuilder.toReviewDTO(reviewOptional.get());
    }

    /**
     * Creates a new review.
     *
     * @param reviewDTO the review DTO containing review information
     * @return the created review DTO
     */
    public ReviewDTO createReview(ReviewDTO reviewDTO) {
        Review review = ReviewBuilder.toEntity(reviewDTO);

        Optional<User> userOptional = userRepository.findById(reviewDTO.getUser_id());
        review.setUser(userOptional.get());
        review = reviewRepository.save(review);

        LOGGER.debug("Review with id {} was inserted in db", review.getId());
        return ReviewBuilder.toReviewDTO(review);
    }

    /**
     * Creates a new review for the logged-in customer.
     *
     * @param reviewDTO the review DTO containing review information
     * @return the created review DTO
     */
    public ReviewDTO createReviewCustomer(ReviewDTO reviewDTO) {

        reviewDTO.setDate(LocalDateTime.now());

        UserDTO loggedUser = (UserDTO) session.getAttribute("logged_user");
        if (loggedUser == null || loggedUser.getId() == null) {
            LOGGER.error("User not logged in or user ID not found in session");
            throw new IllegalStateException("User not logged in or user ID not found in session");
        }

        reviewDTO.setUser_id(loggedUser.getId());

        Review review = ReviewBuilder.toEntity(reviewDTO);
        review = reviewRepository.save(review);

        LOGGER.debug("Review with id {} was inserted in db", review.getId());

        return ReviewBuilder.toReviewDTO(review);
    }

    /**
     * Updates a review by ID.
     *
     * @param id        the ID of the review to update
     * @param reviewDTO the review DTO containing updated review information
     * @return the updated review DTO
     * @throws ReviewNotFoundException if the review is not found
     */
    public ReviewDTO updateReview(UUID id, ReviewDTO reviewDTO) throws ReviewNotFoundException {
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        if (!reviewOptional.isPresent()) {
            LOGGER.error("Review with id {} was not found in db", id);
            throw new ReviewNotFoundException(Review.class.getSimpleName() + "with id: " + id);
        }

        Review existingReview = reviewOptional.get();

        existingReview.setMessage(reviewDTO.getMessage());
        existingReview.setDate(reviewDTO.getDate());

        Optional<User> userOptional = userRepository.findById(reviewDTO.getUser_id());
        userOptional.ifPresent(existingReview::setUser);

        Review updatedReview = reviewRepository.save(existingReview);

        LOGGER.debug("Review with id {} was updated in db", updatedReview.getId());
        return ReviewBuilder.toReviewDTO(updatedReview);
    }

    /**
     * Updates a review by ID for the logged-in customer.
     *
     * @param id        the ID of the review to update
     * @param reviewDTO the review DTO containing updated review information
     * @return the updated review DTO
     * @throws ReviewNotFoundException if the review is not found
     */
    public ReviewDTO updateReviewCustomer(UUID id, ReviewDTO reviewDTO) throws ReviewNotFoundException {
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        if (!reviewOptional.isPresent()) {
            LOGGER.error("Review with id {} was not found in db", id);
            throw new ReviewNotFoundException(Review.class.getSimpleName() + "with id: " + id);
        }

        Review existingReview = reviewOptional.get();
        
        LocalDateTime reviewDate = reviewDTO.getDate();
        if (reviewDate == null) {
            LOGGER.error("Review date not valid");
            throw new IllegalArgumentException("Review date not valid");
        }

        LocalDateTime currentDate = LocalDateTime.now();
        if (reviewDate.isBefore(currentDate)) {
            LOGGER.error("Review date cannot be in the past");
            throw new IllegalArgumentException("Review date cannot be in the past");
        }

        existingReview.setDate(reviewDTO.getDate());
        existingReview.setMessage(reviewDTO.getMessage());

        Review updatedReview = reviewRepository.save(existingReview);

        LOGGER.debug("Review with id {} was updated in db", updatedReview.getId());
        return ReviewBuilder.toReviewDTO(updatedReview);
    }

    /**
     * Deletes a review by ID.
     *
     * @param id the ID of the review to delete
     * @throws ReviewNotFoundException if the review is not found
     */
    public void deleteReview(UUID id) throws ReviewNotFoundException {
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        if (!reviewOptional.isPresent()) {
            LOGGER.error("Review with id {} was not found in db", id);
            throw new ReviewNotFoundException(Review.class.getSimpleName() + "with id: " + id);
        }
        reviewRepository.deleteById(id);
    }

    /**
     * Deletes a review by ID for the logged-in customer.
     *
     * @param id the ID of the review to delete
     * @throws ReviewNotFoundException if the review is not found
     */
    public void deleteReviewCustomer(UUID id) throws ReviewNotFoundException {
        UserDTO loggedUser = (UserDTO) session.getAttribute("logged_user");
        if (loggedUser == null) {
            LOGGER.error("User not logged in");
            throw new IllegalStateException("User not logged in");
        }

        Optional<Review> reviewOptional = reviewRepository.findByIdAndUserId(id, loggedUser.getId());
        if (!reviewOptional.isPresent()) {
            LOGGER.error("Review with id {} was not found for customer with id {}", id, loggedUser.getId());
            throw new ReviewNotFoundException("Review not found with id: " + id);
        }

        reviewRepository.deleteById(id);
    }
}
