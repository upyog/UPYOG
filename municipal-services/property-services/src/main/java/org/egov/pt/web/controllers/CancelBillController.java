package org.egov.pt.web.controllers;

import org.egov.pt.service.PropertyService;
import org.egov.pt.web.contracts.CancelPropertyBillRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/property")
public class CancelBillController {

    @Autowired
    private PropertyService propertyService;

    @PostMapping("/_cancelbill")
    public ResponseEntity<?> cancelPropertyBill(
            @Valid @RequestBody CancelPropertyBillRequest request) {

        Map<String, String> response = new HashMap<>();

        if (propertyService.cancelPropertyBill(request)) {
            response.put("message", "Property Bill Successfully Cancelled");
            return ResponseEntity.ok(response);
        } else {
            throw new CustomException(
                    "INVALID_UPDATE",
                    "Property Bill could not be Cancelled"
            );
        }
    }
}
