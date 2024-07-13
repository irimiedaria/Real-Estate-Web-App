package com.projectps.buildingmanagement.dtos.builders;

import com.projectps.buildingmanagement.dtos.SolicitationDTO;
import com.projectps.buildingmanagement.entities.Property;
import com.projectps.buildingmanagement.entities.Solicitation;
import com.projectps.buildingmanagement.entities.User;

public class SolicitationBuilder {

    public static SolicitationDTO toSolicitationDTO(Solicitation solicitation) {
        return SolicitationDTO.builder()
                .id(solicitation.getId())
                .date(solicitation.getDate())
                .user_id(solicitation.getUser().getId())
                .property_id(solicitation.getProperty().getId())
                .build();
    }

    public static Solicitation toEntity(SolicitationDTO solicitationDTO) {
        return Solicitation.builder()
                .id(solicitationDTO.getId())
                .date(solicitationDTO.getDate())
                .user(User.builder()
                        .id(solicitationDTO.getUser_id())
                        .build())
                .property(Property.builder()
                        .id(solicitationDTO.getProperty_id())
                        .build())
                .build();
    }
}
