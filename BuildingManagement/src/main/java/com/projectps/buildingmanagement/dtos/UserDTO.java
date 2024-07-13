package com.projectps.buildingmanagement.dtos;

import com.projectps.buildingmanagement.entities.Solicitation;
import com.projectps.buildingmanagement.entities.enums.UserType;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class UserDTO {

    private UUID id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private UserType userRole;
    private List<ContractDTO> contracts;
    private List<ReviewDTO> reviews;
    private List<SolicitationDTO> solicitations;

    public UserDTO() {
        this.contracts = new ArrayList<>();
        this.reviews = new ArrayList<>();
        this.solicitations = new ArrayList<>();
    }
}
