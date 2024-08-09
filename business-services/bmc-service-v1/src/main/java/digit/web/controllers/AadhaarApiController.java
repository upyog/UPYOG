package digit.web.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-06-05T23:24:36.608+05:30")

@Controller
@RequestMapping("")
public class AadhaarApiController {

        private final ObjectMapper objectMapper;

        private final HttpServletRequest request;

        public AadhaarApiController(ObjectMapper objectMapper, HttpServletRequest request) {
                this.objectMapper = objectMapper;
                this.request = request;
        }

        @PostMapping("/aadhaar/authenticate")
        public ResponseEntity<String> aadhaarAuthenticatePost() {
                String accept = request.getHeader("Accept");
                String jsonResponse = "{'name':'sundeep'}";

                return ResponseEntity.accepted().body(jsonResponse);
        }

        @PostMapping("/aadhaar/verify")
        public ResponseEntity<Void> aadhaarVerifyPost() {
                String accept = request.getHeader("Accept");
                return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
        }
}
