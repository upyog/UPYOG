package org.upyog.Automation.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.upyog.Automation.Service.ModuleTestService;
import org.upyog.Automation.model.ModuleExecutionResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/module")
public class ModuleTestController {

    @Autowired
    private ModuleTestService moduleTestService;

    @PostMapping("/run")
    public ResponseEntity<List<ModuleExecutionResult>> runModule(
            @RequestBody ModuleRequest request) {

        List<ModuleExecutionResult> result =
                moduleTestService.runModule(request);

        return ResponseEntity.ok(result);
    }

    public static class ModuleRequest {

        private String moduleName;

        private String baseUrl;

        public String getModuleName() {
            return moduleName;
        }

        public void setModuleName(String moduleName) {
            this.moduleName = moduleName;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
    }
}