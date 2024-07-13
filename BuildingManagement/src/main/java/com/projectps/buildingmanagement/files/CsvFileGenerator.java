package com.projectps.buildingmanagement.files;


import com.projectps.buildingmanagement.dtos.ContractDTO;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

@Component
public class CsvFileGenerator implements FileGeneratorStrategy {

    @Override
    public byte[] generateFile(ContractDTO contract) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);

            writer.write("Contract ID,Start Date,Duration,Details,User ID,Property ID\n");
            writer.write(contract.getId() + "," +
                    contract.getStartDate() + "," +
                    contract.getDuration() + "," +
                    contract.getDetails() + "," +
                    contract.getUser_id() + "," +
                    contract.getProperty_id() + "\n");

            writer.flush();
            writer.close();

            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
