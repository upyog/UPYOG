package org.egov.egovsurveyservices.repository.querybuilder;

import org.springframework.stereotype.Component;

@Component
public class SectionQueryBuilder {



    public String getSectionBySurveyUuid() {
        return "SELECT uuid, weightage FROM eg_ss_survey_section WHERE surveyuuid = ?";
    }

    public String getSectionBySectionUuid(){
        return "SELECT uuid, weightage FROM eg_ss_survey_section WHERE surveyuuid = ?";
    }

}
