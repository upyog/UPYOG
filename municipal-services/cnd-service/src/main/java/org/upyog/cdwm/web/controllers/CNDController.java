package org.upyog.cdwm.web.controllers;


import org.springframework.web.bind.annotation.*;
import org.upyog.cdwm.web.models.CNDApplicationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.*;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;

@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2025-02-12T16:11:18.767+05:30")

@Controller
@RequestMapping("/sv")
public class CNDController {

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    public CNDController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }


    @PostMapping(value = "/_create")
    public ResponseEntity<CNDApplicationResponse> createConstructionDemolitionRequest() {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<CNDApplicationResponse>(objectMapper.readValue("{  \"ResponseInfo\" : {    \"ver\" : \"ver\",    \"resMsgId\" : \"resMsgId\",    \"msgId\" : \"msgId\",    \"apiId\" : \"apiId\",    \"ts\" : 0,    \"status\" : \"SUCCESSFUL\"  },  \"CDWasteApplicationDetail\" : \"{}\"}", CNDApplicationResponse.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                return new ResponseEntity<CNDApplicationResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<CNDApplicationResponse>(HttpStatus.NOT_IMPLEMENTED);
    }
}
