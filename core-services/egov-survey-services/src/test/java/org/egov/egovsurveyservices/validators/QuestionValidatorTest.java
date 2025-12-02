package org.egov.egovsurveyservices.validators;

import org.egov.egovsurveyservices.web.models.Question;
import org.egov.egovsurveyservices.web.models.QuestionRequest;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class QuestionValidatorTest {

    @InjectMocks
    QuestionValidator validator;

    @Test
    public void validForUpdateFail(){
        QuestionRequest request = QuestionRequest.builder().questions(Collections.singletonList(Question.builder().build())).build();
        RuntimeException thrown = Assertions.assertThrows(CustomException.class, () -> {
            validator.validateForUpdate(request);
        });
        Assertions.assertEquals("question uuid missing", thrown.getMessage());
    }

    @Test
    public void validForUpdatePass(){
        QuestionRequest request = QuestionRequest.builder().questions(Collections.singletonList(Question.builder().uuid("1").build())).build();
        validator.validateForUpdate(request);

    }

}