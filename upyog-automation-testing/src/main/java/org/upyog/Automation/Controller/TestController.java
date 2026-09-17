package org.upyog.Automation.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.upyog.Automation.Service.CitizenTestService;
import org.upyog.Automation.Service.EmployeeTestService;
import org.upyog.Automation.Service.VendorTestService;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private CitizenTestService citizenTestService;

    @Autowired
    private EmployeeTestService employeeTestService;

    @Autowired
    private VendorTestService vendorTestService;

    @Value("${selenium.grid.enabled:false}")
    private boolean gridEnabled;

    @Value("${novnc.url:}")
    private String novncUrl;

    @PostMapping("/citizen")
    public ResponseEntity<Map<String, String>> runCitizenTest(@RequestBody CitizenTestRequest request) {
        String result = citizenTestService.runCitizenSideTest(
                request.getBaseUrl(),
                request.getModuleName(),
                request.getMobileNumber(),
                request.getOtp(),
                request.getCityName(),
                request.getPermitNumber()
        );

        Map<String, String> response = new HashMap<>();

        if (gridEnabled && novncUrl != null && !novncUrl.isEmpty()) {
            response.put("message", result);
            response.put("viewerUrl", novncUrl);
        } else {
            response.put("message", result + " - Check your local Chrome browser window for live automation");
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/employee")
    public ResponseEntity<Map<String, String>> runEmployeeTest(@RequestBody EmployeeTestRequest request) {
        String result = employeeTestService.runEmployeeTest(
                request.getBaseUrl(),
                request.getModuleName(),
                request.getUsername(),
                request.getPassword(),
                request.getApplicationNumber()
        );

        Map<String, String> response = new HashMap<>();

        if (gridEnabled && novncUrl != null && !novncUrl.isEmpty()) {
            response.put("message", result);
            response.put("viewerUrl", novncUrl);
        } else {
            response.put("message", result + " - Check your local Chrome browser window for live automation");
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/vendor")
    public ResponseEntity<Map<String, String>> runVendorTest(@RequestBody VendorTestRequest request) {
        String result = vendorTestService.runVendorTest(
                request.getBaseUrl(),
                request.getModuleName(),
                request.getMobileNumber(),
                request.getOtp(),
                request.getCityName(),
                request.getApplicationNumber()
        );
        Map<String, String> response = new HashMap<>();

        if (gridEnabled && novncUrl != null && !novncUrl.isEmpty()) {
            response.put("message", result);
            response.put("viewerUrl", novncUrl);
        } else {
            response.put("message", result + " - Check your local Chrome browser window for live automation");
        }
        return ResponseEntity.ok(response);
    }

    // Request DTOs
    public static class CitizenTestRequest {
        private String baseUrl;
        private String moduleName;
        private String mobileNumber;
        private String otp;
        private String cityName;
        private String permitNumber;

        // Getters and Setters
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
        public String getModuleName() { return moduleName; }
        public void setModuleName(String moduleName) { this.moduleName = moduleName; }
        public String getMobileNumber() { return mobileNumber; }
        public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
        public String getOtp() { return otp; }
        public void setOtp(String otp) { this.otp = otp; }
        public String getCityName() { return cityName; }
        public void setCityName(String cityName) { this.cityName = cityName; }
        public String getPermitNumber() { return permitNumber; }
        public void setPermitNumber(String permitNumber) { this.permitNumber = permitNumber;}
    }

    public static class EmployeeTestRequest {
        private String baseUrl;
        private String username;
        private String password;
        private String applicationNumber;
        private String moduleName;
        private String permitNumber;

        // Getters and Setters
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getApplicationNumber() { return applicationNumber; }
        public void setApplicationNumber(String applicationNumber) { this.applicationNumber = applicationNumber; }
        public String getModuleName() { return moduleName; }
        public void setModuleName(String moduleName) { this.moduleName = moduleName; }
        public String getPermitNumber() { return permitNumber; }
        public void setPermitNumber(String permitNumber) { this.permitNumber = permitNumber;}
    }

        public static class VendorTestRequest {
            private String baseUrl;
            private String moduleName;
            private String mobileNumber;
            private String otp;
            private String cityName;
            private String applicationNumber;
            private String permitNumber;


            // Getters and Setters
            public String getBaseUrl() {return baseUrl;}
            public void setBaseUrl(String baseUrl) {this.baseUrl = baseUrl;}
            public String getModuleName() {return moduleName;}
            public void setModuleName(String moduleName) {this.moduleName = moduleName;}
            public String getMobileNumber() {return mobileNumber;}
            public void setMobileNumber(String mobileNumber) {this.mobileNumber = mobileNumber;}
            public String getOtp() {return otp;}
            public void setOtp(String otp) {this.otp = otp;}
            public String getCityName() {return cityName;}
            public void setCityName(String cityName) {this.cityName = cityName;}
            public String getApplicationNumber() {return applicationNumber;}
            public void setApplicationNumber(String applicationNumber) {this.applicationNumber = applicationNumber;}
            public String getPermitNumber() {return permitNumber;}
            public void setPermitNumber(String permitNumber) {this.permitNumber = permitNumber;}
        }}

