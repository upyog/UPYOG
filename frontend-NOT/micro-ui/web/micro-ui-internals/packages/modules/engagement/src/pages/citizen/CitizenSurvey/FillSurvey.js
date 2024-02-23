import React from "react";
import { useHistory } from "react-router-dom";
import  { useState, useEffect } from "react";
import CitizenSurveyDisplayForm from "../../../components/Surveys/CitizenSurveyDisplayForm";
import NoSurveyFoundPage from "../../../components/Surveys/NoSurveyFoundPage";
import { useTranslation } from "react-i18next";

const transformSurveyResponseData = (data,surveyData) => {
    /**
     * TODO : handle checkbox
     */
  if (!data) return;
  const questions = [];
  // for (const key in data) {
  //   questions.push({
  //     questionId: key,
  //     answer: [data[key]],
  //   });
  // }
  for (const key in data) {
    questions.push({
      attributeCode: key,
      value:data[key]===""? "\"NA\"":JSON.stringify(data[key]) || "\"\"" ,
      additionalDetails: {
        questionId: getQuestionID(key,surveyData)
      }
    });
  }
  return questions;
};

const getQuestionID = (key,surveyData) =>{
  let questionId = "";
  surveyData.attributes.map((ele)=>{
    if (ele.code === key){
      questionId = ele.id
    }
  })
  return questionId;
}

const FillSurvey = ({ location }) => {
  //const surveyData = location?.state;
  const user = Digit.UserService.getUser();
  const { applicationNumber: surveyId, tenantId } = Digit.Hooks.useQueryParams();
  const { data, isLoading } = Digit.Hooks.survey.useSearch({uuid:surveyId,tenantId},{})
  const tenantIds = Digit.ULBService.getCitizenCurrentTenant();
  let ServiceDefinitionCriteria =  {
    "tenantId": tenantId,
    "code": [],
    "module": ["engagement"],
  }
  
  const { data: surveyQuestionData, isLoading: isLoadingSurveys } = Digit.Hooks.survey.useCfdefinitionsearch({ServiceDefinitionCriteria},{})
  const surveyData = location?.state?location.state:surveyQuestionData?.ServiceDefinition?.[0] ? surveyQuestionData?.ServiceDefinition?.[0] : {}
  let ServiceCriteria = {
    tenantId: tenantIds,
    ids: [],
    serviceDefIds: [],
    referenceIds: [surveyData.code],
    accountId: user?.info?.uuid || "dda30b0a-ae2d-4f87-8b52-d1fc73ed7643",
  }
  const { data: selecedSurveyData, } = Digit.Hooks.survey.useSelectedSurveySearch({ServiceCriteria},{})
  const surveyDataOld = data?.Surveys?.[0] ? data?.Surveys?.[0] : {}
  const {t} = useTranslation();
  let initialData = data;
  const [showToast, setShowToast] = useState(null);
  
  //sort survey questions based on qorder field, in surveyData.questions array, here and then render
  surveyData?.attributes?.sort((a,b)=>a.order-b.order)
  const history = useHistory();

  // useEffect(() => {
  //   if(data && initialData?.Surveys?.[0]?.hasResponded == true || initialData?.Surveys?.[0]?.hasResponded === "true")
  //     setShowToast({ key: true, label: "SURVEY_FORM_IS_ALREADY_SUBMITTED" });
  //   else if(data && initialData?.Surveys?.[0]?.status == "INACTIVE")
  //     setShowToast({ key: true, label: "SURVEY_FORM_IS_ALREADY_INACTIVE" });
  // },[data?.Surveys?.[0]?.hasResponded,initialData?.Surveys?.[0]?.hasResponded])

  const onSubmit = (data) => {
    const user = Digit.UserService.getUser();
    if (selecedSurveyData.Service[0]){
      setShowToast({ key: true, label: "SURVEY_FORM_IS_ALREADY_SUBMITTED" });
    }else{
      const details = {
        // AnswerEntity: {
        //   surveyId: surveyData.uuid,
        //   answers: transformSurveyResponseData(data),
        //   surveyTitle:surveyData.title,
        //   hasResponded:surveyData.hasResponded,
        // },
        "Service": {
          "tenantId": tenantId,
          "serviceDefId": location.state.id,
          "isActive": true,
          "referenceId": location.state.code,
          "attributes":transformSurveyResponseData(data,surveyData),
          "additionalDetails": {},
          "accountId" : user?.info?.uuid || "dda30b0a-ae2d-4f87-8b52-d1fc73ed7643",
        }
      };
  
      history.push("/digit-ui/citizen/engagement/surveys/submit-response", details);
    }

    
  };

  if(Object.keys(surveyData)?.length > 0 || isLoadingSurveys)
  return <CitizenSurveyDisplayForm surveyData={surveyData} isSubmitDisabled={showToast? true : false} isLoading={isLoading} onFormSubmit={onSubmit} formDisabled={showToast? true : false} showToast={showToast} />;
  else return <NoSurveyFoundPage t={t}/>
};

export default FillSurvey;
