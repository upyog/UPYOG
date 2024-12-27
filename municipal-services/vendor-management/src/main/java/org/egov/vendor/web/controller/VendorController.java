
package org.egov.vendor.web.controller;

import org.egov.vendor.web.models.Vendor;
import org.egov.vendor.service.ContractorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendor/api/contractors")
public class VendorController {

    private final ContractorService contractorService;

    public VendorController(ContractorService contractorService) {
        this.contractorService = contractorService;
    }

    @GetMapping
    public List<Vendor> getAllContractors() {
        return contractorService.getAllContractors();
    }

    @PostMapping
    public Vendor createContractor(@RequestBody Vendor contractor) {
        return contractorService.saveContractor(contractor);
    }

    @PutMapping("/{id}")
    public Vendor updateContractor(@PathVariable Long id, @RequestBody Vendor contractor) {
        return contractorService.updateContractor(id, contractor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContractor(@PathVariable Long id) {
        contractorService.deleteContractor(id);
        return ResponseEntity.ok().build();
    }
}
