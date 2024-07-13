package com.projectps.buildingmanagement.dtos.builders;

import com.projectps.buildingmanagement.dtos.ContractDTO;
import com.projectps.buildingmanagement.entities.Contract;
import com.projectps.buildingmanagement.entities.Property;
import com.projectps.buildingmanagement.entities.User;

public class ContractBuilder {

    public static ContractDTO toContractDTO(Contract contract) {
        return ContractDTO.builder()
                .id(contract.getId())
                .startDate(contract.getStartDate())
                .duration(contract.getDuration())
                .details(contract.getDetails())
                .user_id(contract.getUser().getId())
                .property_id(contract.getProperty().getId())
                .build();
    }

    public static Contract toEntity(ContractDTO contractDTO) {
        return Contract.builder()
                .id(contractDTO.getId())
                .startDate(contractDTO.getStartDate())
                .duration(contractDTO.getDuration())
                .details(contractDTO.getDetails())
                .user(User.builder()
                        .id(contractDTO.getUser_id())
                        .build())
                .property(Property.builder()
                        .id(contractDTO.getProperty_id())
                                .build())
                .build();
    }
}
