package org.egov.egovsurveyservices.validators;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.egov.egovsurveyservices.web.models.QuestionRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class QuestionValidator {

    public void validateForUpdate(QuestionRequest questionRequest) {
        if(StringUtils.isBlank(questionRequest.getQuestions().get(0).getUuid())){
            throw new CustomException("EG_SS_UPDATE_QUESTION_MISSING_UUID", "question uuid missing");
        }
    }
}
