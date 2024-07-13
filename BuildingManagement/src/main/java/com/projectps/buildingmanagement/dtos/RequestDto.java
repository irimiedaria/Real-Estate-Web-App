package com.projectps.buildingmanagement.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RequestDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String emailReceiver;
    private String emailSubject;
    private String emailBody;
}
