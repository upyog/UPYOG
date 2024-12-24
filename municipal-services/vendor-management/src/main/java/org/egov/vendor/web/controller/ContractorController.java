
package org.egov.vendor.web.controller;

import org.egov.vendor.web.models.Contractor;
import org.egov.vendor.service.ContractorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendor/api/contractors")
public class ContractorController {

    private final ContractorService contractorService;

    public ContractorController(ContractorService contractorService) {
        this.contractorService = contractorService;
    }

    @GetMapping
    public List<Contractor> getAllContractors() {
        return contractorService.getAllContractors();
    }

    @PostMapping
    public Contractor createContractor(@RequestBody Contractor contractor) {
        return contractorService.saveContractor(contractor);
    }

    @PutMapping("/{id}")
    public Contractor updateContractor(@PathVariable Long id, @RequestBody Contractor contractor) {
        return contractorService.updateContractor(id, contractor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContractor(@PathVariable Long id) {
        contractorService.deleteContractor(id);
        return ResponseEntity.ok().build();
    }
}
