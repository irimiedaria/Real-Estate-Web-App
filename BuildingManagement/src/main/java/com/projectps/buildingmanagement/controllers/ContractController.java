
package com.projectps.buildingmanagement.controllers;

import com.projectps.buildingmanagement.dtos.ContractDTO;
import com.projectps.buildingmanagement.dtos.UserDTO;
import com.projectps.buildingmanagement.exceptions.ContractNotFoundException;
import com.projectps.buildingmanagement.exceptions.PropertyNotFoundException;
import com.projectps.buildingmanagement.exceptions.UserNotFoundException;
import com.projectps.buildingmanagement.files.CsvFileGenerator;
import com.projectps.buildingmanagement.files.TxtFileGenerator;
import com.projectps.buildingmanagement.services.ContractService;
import com.projectps.buildingmanagement.files.PdfFileGenerator;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.http.HttpHeaders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Controller class for managing contracts in the building management system.
 */
@RestController
@RequestMapping("/contracts")
public class ContractController {

    private final ContractService contractService;
    private final PdfFileGenerator pdfFileGenerator;

    private final TxtFileGenerator txtFileGenerator;

    private final CsvFileGenerator csvFileGenerator;
    private final HttpSession session;

    /**
     * Constructs a new ContractController with the specified services and session.
     *
     * @param contractService   The contract service to use.
     * @param pdfFileGenerator The PDF file generator.
     * @param txtFileGenerator The TXT file generator.
     * @param csvFileGenerator The CSV file generator.
     * @param session           The HTTP session.
     */
    @Autowired
    public ContractController(ContractService contractService, PdfFileGenerator pdfFileGenerator, TxtFileGenerator txtFileGenerator, CsvFileGenerator csvFileGenerator, HttpSession session) {
        this.contractService = contractService;
        this.pdfFileGenerator = pdfFileGenerator;
        this.txtFileGenerator = txtFileGenerator;
        this.csvFileGenerator = csvFileGenerator;
        this.session = session;
    }

    /**
     * Handles the download of contract files based on the given contract ID and file type.
     *
     * @param contractId The ID of the contract.
     * @param fileType   The type of file to download (pdf, txt, csv).
     * @param response   The HTTP response.
     */
    @PostMapping("/downloadFile")
    public void downloadContractFile(@RequestParam("contractId") String contractId,
                                     @RequestParam("fileType") String fileType,
                                     HttpServletResponse response) {
        try {
            UUID uuid = UUID.fromString(contractId);
            ContractDTO contractDTO = contractService.getContractById(uuid);

            byte[] fileContent;
            String contentType;
            String filename;
            if ("pdf".equals(fileType)) {
                fileContent = pdfFileGenerator.generateFile(contractDTO);
                contentType = "application/pdf";
                filename = "contract_" + contractId + ".pdf";
            } else if ("txt".equals(fileType)) {
                fileContent = txtFileGenerator.generateFile(contractDTO);
                contentType = "text/plain";
                filename = "contract_" + contractId + ".txt";
            } else if ("csv".equals(fileType)) {
                fileContent = csvFileGenerator.generateFile(contractDTO);
                contentType = "text/csv";
                filename = "contract_" + contractId + ".csv";
            } else {
                throw new IllegalArgumentException("Invalid file type");
            }

            response.setContentType(contentType);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
            response.getOutputStream().write(fileContent);
            response.getOutputStream().flush();
        } catch (ContractNotFoundException | IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves all contracts and returns a ModelAndView containing the contracts list view.
     *
     * @return A ModelAndView for displaying all contracts.
     */
    @GetMapping("/contractsList")
    public ModelAndView getAllContracts() {
        List<ContractDTO> dtos = contractService.getAllContracts();
        ModelAndView modelAndView = new ModelAndView("contractsList");
        modelAndView.addObject("contract", dtos);
        return modelAndView;
    }

    /**
     * Retrieves all contracts for a customer and returns a ModelAndView containing the customer's contracts list view.
     *
     * @return A ModelAndView for displaying all contracts for a customer.
     */
    @GetMapping("/contractsListCustomer")
    public ModelAndView getAllContractsCustomer() {
        List<ContractDTO> dtos = contractService.getAllContractsCustomer();
        ModelAndView modelAndView = new ModelAndView("contractsListCustomer");
        modelAndView.addObject("contract", dtos);
        return modelAndView;
    }

    /**
     * Retrieves a contract by its ID and returns a ModelAndView containing details of the contract.
     *
     * @param id The ID of the contract to retrieve.
     * @return A ModelAndView for displaying the contract details.
     */
    @GetMapping("/searchContract")
    public ModelAndView getContractById(@RequestParam(required = false) String id) {
        ModelAndView modelAndView = new ModelAndView("searchContract");
        if (id == null || id.isEmpty()) {
            return modelAndView;
        }
        try {
            UUID contractId = UUID.fromString(id);
            ContractDTO contractDTO = contractService.getContractById(contractId);
            modelAndView.addObject("searched_contract", contractDTO);
        } catch (IllegalArgumentException e) {
            modelAndView.addObject("error", "Invalid UUID format for ID: " + id);
        } catch (ContractNotFoundException e) {
            modelAndView.addObject("error", "Contract with ID " + id + " was not found");
        } catch (Exception e) {
            modelAndView.addObject("error", "Failed to retrieve contract");
        }
        return modelAndView;
    }

    /**
     * Retrieves a contract by its ID for a customer and returns a ModelAndView containing details of the contract.
     *
     * @param id The ID of the contract to retrieve.
     * @return A ModelAndView for displaying the contract details for a customer.
     */
    @GetMapping("/searchContractCustomer")
    public ModelAndView getContractByIdCustomer(@RequestParam(required = false) String id) {
        ModelAndView modelAndView = new ModelAndView("searchContractCustomer");
        if (id == null || id.isEmpty()) {
            return modelAndView;
        }
        try {
            UUID contractId = UUID.fromString(id);
            ContractDTO contractDTO = contractService.getContractByIdCustomer(contractId);
            modelAndView.addObject("searched_contract", contractDTO);
        } catch (IllegalArgumentException e) {
            modelAndView.addObject("error", "Invalid UUID format for ID: " + id);
        } catch (ContractNotFoundException e) {
            modelAndView.addObject("error", "Contract with ID " + id + " was not found");
        } catch (Exception e) {
            modelAndView.addObject("error", "Failed to retrieve contract");
        }
        return modelAndView;
    }

    /**
     * Displays the page for inserting a new contract.
     *
     * @return A ModelAndView for displaying the insert contract page.
     */
    @GetMapping("/insertContract")
    public ModelAndView showInsertContractPage() {
        ModelAndView modelAndView = new ModelAndView("insertContract");
        return modelAndView;
    }

    /**
     * Handles the creation of a new contract.
     *
     * @param contractDTO    The contract DTO containing contract information.
     * @param bindingResult  The result of binding contract DTO.
     * @return A ModelAndView indicating the result of the contract creation attempt.
     */
    @PostMapping("/insertContract")
    public ModelAndView createContract(@ModelAttribute ContractDTO contractDTO, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView("insertContract");

        try {
            // Attempt to create contract
            contractService.createContract(contractDTO);
            modelAndView.addObject("success_message", "Contract created successfully!");

        } catch (PropertyNotFoundException e) {
            modelAndView.addObject("error", "Property with ID " + contractDTO.getProperty_id() + " not found");
        } catch (UserNotFoundException e) {
            modelAndView.addObject("error", "User with ID " + contractDTO.getUser_id() + " not found");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().startsWith("Property is already rented")) {
                modelAndView.addObject("error", e.getMessage());
            } else if (e.getMessage().startsWith("Start date")) {
                modelAndView.addObject("error", e.getMessage());
            } else {
                modelAndView.addObject("error", "Failed to create contract");
            }
        } catch (Exception e) {
            modelAndView.addObject("error", "Failed to create contract");
        }
        return modelAndView;
    }

    /**
     * Retrieves a contract by its ID for update and returns a ModelAndView containing details of the contract.
     *
     * @param id The ID of the contract to update.
     * @return A ModelAndView for displaying the contract update form.
     */
    @GetMapping("/updateContract")
    public ModelAndView getContractForUpdate(@RequestParam(required = false) String id) {
        ModelAndView modelAndView = new ModelAndView("updateContract");
        if (id == null || id.isEmpty()) {
            return modelAndView;
        }
        try {
            UUID contractId = UUID.fromString(id);
            ContractDTO contractDTO = contractService.getContractById(contractId);
            modelAndView.addObject("updated_contract", contractDTO);
        } catch (IllegalArgumentException e) {
            modelAndView.addObject("error", "Invalid UUID format for ID: " + id);
        } catch (ContractNotFoundException e) {
            modelAndView.addObject("error", "Contract with ID " + id + " was not found");
        } catch (Exception e) {
            modelAndView.addObject("error", "Failed to retrieve contract");
        }
        return modelAndView;
    }

    /**
     * Handles the update of an existing contract.
     *
     * @param id          The ID of the contract to update.
     * @param contractDTO The updated contract DTO.
     * @return A ModelAndView indicating the result of the contract update attempt.
     */
    @PostMapping("/updateContract")
    public ModelAndView updateContract(@RequestParam(required = false) String id, @ModelAttribute ContractDTO contractDTO) {
        ModelAndView modelAndView = new ModelAndView("updateContract");
        if (id == null || id.isEmpty()) {
            modelAndView.addObject("error", "Invalid contract ID");
            return modelAndView;
        }
        try {
            UUID contractId = UUID.fromString(id);

            ContractDTO updatedContract = contractService.updateContract(contractId, contractDTO);
            modelAndView.addObject("updated_contract", updatedContract);
            modelAndView.addObject("success_message", "Contract with ID " + id + " was updated successfully!");

            LocalDateTime startDate = contractDTO.getStartDate();
            if (startDate == null) {
                throw new IllegalArgumentException("Start date not valid");
            }
            LocalDateTime currentDate = LocalDateTime.now();
            if (startDate.isBefore(currentDate)) {
                throw new IllegalArgumentException("Start date cannot be in the past");
            }
        } catch (IllegalArgumentException | ContractNotFoundException e) {
            modelAndView.addObject("error", e.getMessage());
        } catch (Exception e) {
            modelAndView.addObject("error", "Failed to update contract");
        }
        return modelAndView;
    }

    /**
     * Handles the deletion of a contract.
     *
     * @param id The ID of the contract to delete.
     * @return A ModelAndView indicating the result of the contract deletion attempt.
     */
    @PostMapping("/deleteContract")
    public ModelAndView deleteContract(@RequestParam UUID id) {
        try {
            contractService.deleteContract(id);
            ModelAndView modelAndView = new ModelAndView("redirect:/contracts/contractsList");
            return modelAndView;
        } catch (ContractNotFoundException e) {
            ModelAndView modelAndView = new ModelAndView("redirect:/contracts/contractsList");
            modelAndView.addObject("errorMessage", "Contract not found!");
            return modelAndView;
        } catch (Exception e) {
            ModelAndView modelAndView = new ModelAndView("redirect:/contracts/contractsList");
            modelAndView.addObject("errorMessage", "Failed to delete contract");
            return modelAndView;
        }
    }
}
