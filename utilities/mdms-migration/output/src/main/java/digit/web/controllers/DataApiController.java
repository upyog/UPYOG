package digit.web.controllers;


import digit.service.MasterDataMigrationService;
import digit.web.models.ErrorRes;
import digit.web.models.MasterDataMigrationRequest;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.*;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.stream.Collectors;

@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2023-06-20T09:54:35.237+05:30[Asia/Calcutta]")
@Controller
@RequestMapping(value = "/data")
public class DataApiController {

    private final ObjectMapper objectMapper;

    private final MasterDataMigrationService masterDataMigrationService;

    @Autowired
    public DataApiController(ObjectMapper objectMapper, MasterDataMigrationService masterDataMigrationService) {
        this.objectMapper = objectMapper;
        this.masterDataMigrationService = masterDataMigrationService;
    }

    @RequestMapping(value = "/v1/_migrate", method = RequestMethod.POST)
    public ResponseEntity<Object> migrateMasterData(@Valid @RequestBody MasterDataMigrationRequest body) {
        masterDataMigrationService.migrateMasterData(body);
        return new ResponseEntity<Object>(HttpStatus.ACCEPTED);
    }

}
