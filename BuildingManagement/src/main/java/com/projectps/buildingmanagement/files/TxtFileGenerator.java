package com.projectps.buildingmanagement.files;

import com.projectps.buildingmanagement.dtos.ContractDTO;
import org.springframework.stereotype.Component;

@Component
public class TxtFileGenerator implements FileGeneratorStrategy {
    @Override
    public byte[] generateFile(ContractDTO contract) {

        StringBuilder content = new StringBuilder();
        content.append("Contract ID: ").append(contract.getId()).append("\n");
        content.append("Start Date: ").append(contract.getStartDate()).append("\n");
        content.append("Duration: ").append(contract.getDuration()).append("\n");
        content.append("Details: ").append(contract.getDetails()).append("\n");
        content.append("User ID: ").append(contract.getUser_id()).append("\n");
        content.append("Property ID: ").append(contract.getProperty_id()).append("\n");
        return content.toString().getBytes();
    }
}