package com.projectps.buildingmanagement.files;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.projectps.buildingmanagement.dtos.ContractDTO;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class PdfFileGenerator implements FileGeneratorStrategy {

    @Override
    public byte[] generateFile(ContractDTO contract) {
        try {
            Document document = new Document();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            document.add(new Paragraph("Contract ID: " + contract.getId()));
            document.add(new Paragraph("Start Date: " + contract.getStartDate()));
            document.add(new Paragraph("Duration: " + contract.getDuration()));
            document.add(new Paragraph("Details: " + contract.getDetails()));
            document.add(new Paragraph("User ID: " + contract.getUser_id()));
            document.add(new Paragraph("Property ID: " + contract.getProperty_id()));

            document.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
