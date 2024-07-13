package com.projectps.buildingmanagement.dtos.builders;

import com.projectps.buildingmanagement.dtos.ContractDTO;
import com.projectps.buildingmanagement.dtos.ReviewDTO;
import com.projectps.buildingmanagement.dtos.SolicitationDTO;
import com.projectps.buildingmanagement.dtos.UserDTO;
import com.projectps.buildingmanagement.entities.Contract;
import com.projectps.buildingmanagement.entities.Review;
import com.projectps.buildingmanagement.entities.Solicitation;
import com.projectps.buildingmanagement.entities.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserBuilder {

    public static UserDTO toUserDTO(User user) {
        List<ContractDTO> contractDTOs = user.getContracts().stream()
                .map(ContractBuilder::toContractDTO)
                .collect(Collectors.toList());
        List<ReviewDTO> reviewDTOS = user.getReviews().stream()
                .map(ReviewBuilder::toReviewDTO)
                .collect(Collectors.toList());
        List<SolicitationDTO> solicitationDTOs = user.getSolicitations().stream()
                .map(SolicitationBuilder::toSolicitationDTO)
                .collect(Collectors.toList());
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .userRole(user.getUserRole())
                .contracts(contractDTOs)
                .reviews(reviewDTOS)
                .solicitations(solicitationDTOs)
                .build();
    }

    public static User toEntity(UserDTO userDTO) {
        List<Contract> contractsEntity = userDTO.getContracts().stream()
                .map(ContractBuilder::toEntity)
                .collect(Collectors.toList());
        List<Review> reviewsEntity = userDTO.getReviews().stream()
                .map(ReviewBuilder::toEntity)
                .collect(Collectors.toList());
        List<Solicitation> solicitationsEntity = userDTO.getSolicitations().stream()
                .map(SolicitationBuilder::toEntity)
                .collect(Collectors.toList());
        return User.builder()
                .id(userDTO.getId())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .email(userDTO.getEmail())
                .phoneNumber(userDTO.getPhoneNumber())
                .userRole(userDTO.getUserRole())
                .contracts(contractsEntity)
                .reviews(reviewsEntity)
                .solicitations(solicitationsEntity)
                .build();
    }
}
