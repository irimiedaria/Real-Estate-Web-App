package com.projectps.buildingmanagement.files;

import com.projectps.buildingmanagement.dtos.ContractDTO;

public interface FileGeneratorStrategy {
    byte[] generateFile(ContractDTO contract);
}
