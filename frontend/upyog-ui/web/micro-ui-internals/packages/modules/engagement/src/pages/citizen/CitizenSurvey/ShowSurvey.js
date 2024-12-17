import { Loader } from "@upyog/digit-ui-react-components";
import React, { useEffect } from "react";
import { useQueryClient } from "react-query";
import CitizenSurveyForm from "../../../components/Surveys/CitizenSurveyForm";
import CitizenSurveyDisplayForm from "../../../components/Surveys/CitizenSurveyDisplayForm";

const ShowSurvey = ({ location }) => {
  const surveyData = location?.state;
  const questions = surveyData?.questions;
  const tenantIds = Digit.ULBService.getCitizenCurrentTenant();
  const user = Digit.UserService.getUser();
  const mutation = Digit.Hooks.survey.useShowResults();

  let ServiceCriteria = {
    tenantId: tenantIds,
    ids: [],
    serviceDefIds: [],
    referenceIds: [surveyData.code],
    accountId: user?.info?.uuid || "dda30b0a-ae2d-4f87-8b52-d1fc73ed7643",
  }
  const { data: selecedSurveyData, isLoading } = Digit.Hooks.survey.useSelectedSurveySearch({ServiceCriteria},{})

  const queryClient = useQueryClient();
  useEffect(() => {
    // const onSuccess = () => {
    //   queryClient.clear();
    // };
    // mutation.mutate(
    //   {
    //     surveyId: surveyData.uuid,
    //   },
    //   {
    //     onSuccess,
    //   }
    // );
  }, []);

  if (isLoading) {
    return <Loader />;
  }

  // if (mutation.isError) return <div>An error occured...</div>;

  //questionid in answers uuid in surveys needs to be matched
  const answers = selecedSurveyData.Service[0].attributes;
  answers?.map((element)=>{
    element.uuid = element.id;
    element.questionId = element.attributeCode;
    element.answer = [JSON.parse(element.value)];
    element.citizenId = selecedSurveyData.Service.accountId;
  })
  const formDefaultValues = {};
  answers?.map((ans) => {
    if (ans?.answer.length === 1) formDefaultValues[ans?.questionId] = ans?.answer[0];
    else formDefaultValues[ans?.questionId] = ans?.answer;
  });

  //pass this formDefaultValues in this format.....questionUuid:answerText for all the questions that are in the particular survey
  //"44a54cdb-7e8a-4631-b261-600f1f2cf5a6":"answerText",
  // const formDefaultValues = {}
  // questions.map(ques => {
  //   if(ques.type==="SHORT_ANSWER_TYPE" || ques.type==="LONG_ANSWER_TYPE"){
  //     formDefaultValues[ques.uuid]="answerText"
  //   }
  // })

  return <CitizenSurveyDisplayForm surveyData={surveyData} submitDisabled={true} formDisabled={true} formDefaultValues={formDefaultValues} />;
};

export default ShowSurvey;
