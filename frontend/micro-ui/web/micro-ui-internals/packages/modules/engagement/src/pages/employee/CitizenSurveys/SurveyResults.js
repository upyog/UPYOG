import React,{ useEffect,useState } from 'react'
import { useHistory, useParams } from "react-router-dom";
import CitizenSurveyForm from "../../../components/Surveys/CitizenSurveyForm";
import { useQueryClient } from "react-query";
import { ActionBar, Card, SubmitBar, Menu,Loader } from "@egovernments/digit-ui-react-components";
import { format } from "date-fns";
import SurveyResultsView from '../../../components/Surveys/ResultsView/SurveyResultsView';

const isActive = (startDate, endDate) => {
  const currentDate = new Date().getTime();
  if (startDate < currentDate && currentDate <= endDate) {
    return true;
  }
  return false;
};

const setSurveyQuestion = (surveyObj) =>{
  let questions = [];
  surveyObj.attributes.map((element)=>{
    questions.push({
      questionStatement : element.code,
      type : TypeAnswerEnum[element.additionalDetails.type],
      required : element.required,
      options : element?.additionalDetails?.options || ["NA"],
      uuid : element.code,
      surveyId : surveyObj.code
    })
  })
  return questions;
}

const TypeAnswerEnum = {
  SHORT_ANSWER_TYPE: "Short Answer",
  LONG_ANSWER_TYPE: "Paragraph",
  MULTIPLE_ANSWER_TYPE: "Multiple Choice",
  CHECKBOX_ANSWER_TYPE: "Check Boxes",
  DATE_ANSWER_TYPE: "Date",
  TIME_ANSWER_TYPE: "Time",
};

const SurveyResults = () => {
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const params = useParams();
    // const mutation = Digit.Hooks.survey.useShowResults();
    const queryClient = useQueryClient();
    const user = Digit.UserService.getUser();

    let ServiceDefinitionCriteria = {
      tenantId: Digit.ULBService.getCurrentTenantId(),
      code: [params.id],
      module: ["engagement"]
    }

    let ServiceCriteria = {
      tenantId: Digit.ULBService.getCurrentTenantId(),
      ids: [],
      serviceDefIds: [],
      referenceIds: [params.id],
    }
  
    const { data: selecedSurveyresults } = Digit.Hooks.survey.useSelectedSurveySearch({ServiceCriteria},{})
    if (selecedSurveyresults?.Service?.length){
      let questions = []
      selecedSurveyresults?.Service.map((element)=>{
        element.attributes.map((ele)=>{
          questions.push({
            answer: typeof(JSON.parse(ele.value)) === "string" ? [JSON.parse(ele.value)] : JSON.parse(ele.value),
            citizenId: element.accountId,
            questionId: ele.attributeCode,
            uuid: ele.id,
            auditDetails: element.auditDetails
          })
        })
        
      })
      selecedSurveyresults.answers = questions;

    }
  
    const { data: selecedSurveyData, isLoading } = Digit.Hooks.survey.useCfdefinitionsearchresult({ServiceDefinitionCriteria},{
      select: (data) => {
        const surveyObj = data?.ServiceDefinition?.[0];
        return{
          // uuid: surveyObj.code,
          title: surveyObj.code,
          description: surveyObj.additionalDetails.description,
          fromDate: format(new Date(surveyObj.additionalDetails.startDate), "yyyy-MM-dd"),
          toDate: format(new Date(surveyObj.additionalDetails.endDate), "yyyy-MM-dd"),
          fromTime: format(new Date(surveyObj.additionalDetails.startDate), "hh:mm"),
          toTime: format(new Date(surveyObj.additionalDetails.endDate), "hh:mm"),
          questions:setSurveyQuestion(surveyObj), 
          status: isActive(surveyObj.additionalDetails.startDate,surveyObj.additionalDetails.endDate)?"ACTIVE":"INACTIVE",
          answersCount:1,
        }
        
      }
    })
    

    useEffect(() => {
        // const onSuccess = () => {
        // queryClient.clear();
        // };
        // mutation.mutate({
        // surveyId:params.id
        // }, {
        // onSuccess,
        // });
    }, []);
    
  //   const { data: surveyData } = Digit.Hooks.survey.useSearch(
  //   { tenantIds: tenantId, uuid: params.id },
  //   {
  //     select: (data) => {
  //       const surveyObj = data?.Surveys?.[0];
  //       return {
  //         //tenantIds: { code: surveyObj.tenantId },
  //         // uuid: surveyObj.uuid,
  //         title: surveyObj.title,
  //         description: surveyObj.description,
  //         fromDate: format(new Date(surveyObj.startDate), "yyyy-MM-dd"),
  //         toDate: format(new Date(surveyObj.endDate), "yyyy-MM-dd"),
  //         fromTime: format(new Date(surveyObj.startDate), "hh:mm"),
  //         toTime: format(new Date(surveyObj.endDate), "hh:mm"),
  //         questions: surveyObj.questions.map(({ questionStatement, type, required, options, uuid, surveyId }) => ({
  //           questionStatement,
  //           type: TypeAnswerEnum[type],
  //           required,
  //           options,
  //           uuid,
  //           surveyId
  //         })),
  //         status: surveyObj.status,
  //         answersCount:surveyObj.answersCount,
  //       };
  //     },
  //   }
  // );


    if(isLoading) return <Loader />
    
    // else if(mutation.isLoading) return <Loader />
    // //if(mutation.isError) return <div>An error occured...</div>
    return <SurveyResultsView surveyInfo={selecedSurveyData} selecedSurveyresults={selecedSurveyresults} />
    
}

export default SurveyResults

