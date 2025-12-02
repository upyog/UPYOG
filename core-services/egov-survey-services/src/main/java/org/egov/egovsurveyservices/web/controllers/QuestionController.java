package org.egov.egovsurveyservices.web.controllers;

import lombok.extern.slf4j.Slf4j;
import org.egov.egovsurveyservices.service.QuestionService;
import org.egov.egovsurveyservices.web.models.*;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/egov-ss")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @RequestMapping(value="/question/_create", method = RequestMethod.POST)
    public ResponseEntity<QuestionResponse> create(@Valid @RequestBody QuestionRequest questionRequest) {
        QuestionResponse questionResponse = questionService.createQuestion(questionRequest);
        return new ResponseEntity<>(questionResponse, HttpStatus.CREATED);
    }

    @RequestMapping(value="/question/_update", method = RequestMethod.PUT)
    public ResponseEntity<QuestionResponse> update(@RequestBody QuestionRequest questionRequest) {
        QuestionResponse questionResponse = questionService.updateQuestion(questionRequest);
        return new ResponseEntity<>(questionResponse, HttpStatus.OK);
    }


    @PostMapping("/question/_search")
    public ResponseEntity<QuestionResponse> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                                   @ModelAttribute QuestionSearchCriteria criteria) {

        QuestionResponse questionResponse = questionService.searchQuestion(criteria);
        return new ResponseEntity<>(questionResponse,HttpStatus.OK);
    }

    @PostMapping("/question/_plainsearch")
    public ResponseEntity<QuestionResponse> plainSearch(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                                   @ModelAttribute QuestionSearchCriteria criteria) {

        QuestionResponse questionResponse = questionService.searchQuestionPlainSearch(criteria);
        return new ResponseEntity<>(questionResponse,HttpStatus.OK);
    }


//    @RequestMapping(value = "/question/_upload",
//            produces = {MediaType.APPLICATION_JSON_VALUE},
//            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
//            method = RequestMethod.POST
//    )
//    public ResponseEntity<String> uploadQuestions(@Valid @RequestPart(name="requestInfo") RequestInfoWrapper requestInfoWrapper
//            ,@RequestPart MultipartFile file) throws IOException {
//        questionService.uploadQuestions(requestInfoWrapper, file);
//        return ResponseEntity.status(HttpStatus.CREATED).body("Questions uploaded successfully!");
//    }
//
//    @GetMapping("/question/download-template")
//    public ResponseEntity<byte[]> downloadExcelTemplate(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) throws IOException {
//        byte[] templateBytes = questionService.downloadTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//        headers.setContentDispositionFormData("attachment", "question_template.xlsx");
//        return new ResponseEntity<>(templateBytes, headers, HttpStatus.OK);
//    }

}
