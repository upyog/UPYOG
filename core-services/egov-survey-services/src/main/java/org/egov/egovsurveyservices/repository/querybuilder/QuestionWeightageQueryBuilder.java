package org.egov.egovsurveyservices.repository.querybuilder;

import org.springframework.stereotype.Component;

@Component
public class QuestionWeightageQueryBuilder {

    public static final String QUESTION_WEIGHTAGE_QUERY = "SELECT weightage FROM eg_ss_question_weightage WHERE questionuuid = ? AND sectionuuid = ?";

}
