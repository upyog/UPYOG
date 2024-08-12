package digit.web.controllers;


import digit.service.SchemaDefinitionMigrationService;
import digit.web.models.ErrorRes;
import digit.web.models.SchemaMigrationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2023-06-20T09:54:35.237+05:30[Asia/Calcutta]")
@RestController
@RequestMapping(value = "/schema")
public class SchemaApiController {

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    private final SchemaDefinitionMigrationService schemaDefinitionMigrationService;

    @Autowired
    public SchemaApiController(ObjectMapper objectMapper, HttpServletRequest request, SchemaDefinitionMigrationService schemaDefinitionMigrationService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.schemaDefinitionMigrationService = schemaDefinitionMigrationService;
    }

    @PostMapping(value = "/v1/_generate")
    public ResponseEntity<Void> generateSchemaDefinition() {
        schemaDefinitionMigrationService.generateSchemaDefinition();
        return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }

    @PostMapping(value = "/v1/_migrate")
    public ResponseEntity<Void> migrateSchemaDefinition(@RequestBody SchemaMigrationRequest schemaMigrationRequest) {
        schemaDefinitionMigrationService.beginMigration(schemaMigrationRequest);
        return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }

}
