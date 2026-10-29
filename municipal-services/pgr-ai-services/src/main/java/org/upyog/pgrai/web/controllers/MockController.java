package org.upyog.pgrai.web.controllers;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.upyog.pgrai.util.HRMSUtil;
import org.upyog.pgrai.web.models.RequestInfoWrapper;
import org.upyog.pgrai.web.models.RequestSearchCriteria;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import jakarta.validation.Valid;

/**
 * Controller for handling mock API requests.
 * Provides endpoints for creating, searching, updating, and testing mock data.
 */
@Controller
@RequestMapping("/mock")
@Slf4j
public class MockController {

    private static final String MOCK_DATA_CLASSPATH = "classpath:mockData.json";

    private static final String MOCK_FILE_LOG_PREFIX = "mock file: ";

    private ResourceLoader resourceLoader;

    private HRMSUtil hrmsUtil;

    /**
     * Constructor for MockController.
     *
     * @param resourceLoader ResourceLoader for loading resources.
     * @param hrmsUtil Utility for HRMS-related operations.
     */
    @Autowired
    public MockController(ResourceLoader resourceLoader, HRMSUtil hrmsUtil) {
        this.resourceLoader = resourceLoader;
        this.hrmsUtil = hrmsUtil;
    }

    /**
     * Endpoint to create mock requests.
     *
     * @return A ResponseEntity containing the mock data as a string.
     * @throws IOException If an error occurs while reading the mock data file.
     */
    @PostMapping("/requests/_create")
    public ResponseEntity<String> requestsCreatePost() throws IOException {
        return readMockDataResponse();
    }

    /**
     * Endpoint to search mock requests.
     *
     * @param requestInfoWrapper Wrapper containing request information.
     * @param criteria Search criteria for the mock requests.
     * @return A ResponseEntity containing the mock data as a string.
     */
    @PostMapping("/requests/_search")
    public ResponseEntity<String> requestsSearchPost(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                                     @Valid @ModelAttribute RequestSearchCriteria criteria) {
        InputStream mockDataFile = null;
        try {
            Resource resource = resourceLoader.getResource(MOCK_DATA_CLASSPATH);
            mockDataFile = resource.getInputStream();
            log.info(MOCK_FILE_LOG_PREFIX + mockDataFile.toString());
            String res = IOUtils.toString(mockDataFile, StandardCharsets.UTF_8.name());
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            throw new CustomException("FILEPATH_ERROR", "Failed to read file for mock data");
        } finally {
            try {
                mockDataFile.close();
            } catch (IOException e) {
                log.error("Error while closing mock data file");
            }
        }
    }

    /**
     * Endpoint to update mock requests.
     *
     * @return A ResponseEntity containing the mock data as a string.
     * @throws IOException If an error occurs while reading the mock data file.
     */
    @PostMapping("/requests/_update")
    public ResponseEntity<String> requestsUpdatePost() throws IOException {
        return readMockDataResponse();
    }

    /**
     * Reads the classpath mock data file and returns it as a response payload.
     *
     * @return A ResponseEntity containing the mock data as a string.
     * @throws IOException If an error occurs while closing the mock data file.
     */
    private ResponseEntity<String> readMockDataResponse() throws IOException {
        InputStream mockDataFile = null;
        try {
            Resource resource = resourceLoader.getResource(MOCK_DATA_CLASSPATH);
            mockDataFile = resource.getInputStream();
            log.info(MOCK_FILE_LOG_PREFIX + mockDataFile.toString());
            String res = IOUtils.toString(mockDataFile, StandardCharsets.UTF_8.name());
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            throw new CustomException("FILEPATH_ERROR", "Failed to read file for mock data");
        } finally {
            mockDataFile.close();
        }
    }

    /**
     * Endpoint to test HRMS-related operations.
     *
     * @param requestInfoWrapper Wrapper containing request information.
     * @param tenantId The tenant ID for the request.
     * @param uuids List of UUIDs for which departments are to be fetched.
     * @return A ResponseEntity containing the list of departments.
     */
    @PostMapping("/requests/_test")
    public ResponseEntity<List<String>> requestsTest(@RequestBody RequestInfoWrapper requestInfoWrapper,
                                                     @RequestParam String tenantId, @RequestParam List<String> uuids) {

        List<String> department = hrmsUtil.getDepartment(uuids, requestInfoWrapper.getRequestInfo());

        return new ResponseEntity<>(department, HttpStatus.OK);
    }
}