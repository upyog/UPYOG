import { Header, Loader } from "@upyog/digit-ui-react-components";
import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import SurveyListCard from "../../../components/Surveys/SurveyListCard";
const isActive = (startDate, endDate) => {
  const currentDate = new Date().getTime();
  if (startDate < currentDate && currentDate <= endDate) {
    return true;
  }
  return false;
};

const SurveyList = () => {
  const { t } = useTranslation();
  const history = useHistory();
  const tenantIds = Digit.ULBService.getCitizenCurrentTenant();
  const user = Digit.UserService.getUser();
  
  let ServiceDefinitionCriteria =  {
    "tenantId": tenantIds,
    "code": [],
    "module": ["engagement"],
  }

  let Pagination = {
    "limit": 100,
    "offSet": 0,
    "sortBy": "string",
    "order": "asc"
  }

  let ServiceCriteria = {
    tenantId: tenantIds,
    ids: [],
    serviceDefIds: [],
    referenceIds: [],
    accountId: user?.info?.uuid || "dda30b0a-ae2d-4f87-8b52-d1fc73ed7643",
  }
  const { data: selecedSurveyData } = Digit.Hooks.survey.useSelectedSurveySearch({ServiceCriteria},{})
  const { data, isLoading: isLoadingSurveys } = Digit.Hooks.survey.useCfdefinitionsearch(
    { ServiceDefinitionCriteria, Pagination },
    {
      select: ({ ServiceDefinition }) => {
        // const allSurveys = Surveys.map((survey) => ({ hasResponded: false, responseStatus: "CS_SURVEY_YT_TO_RESPOND", ...survey }));
        // const allSurveys = Surveys.map((survey) => {
        //   const isSurveyActive = isActive(survey.startDate, survey.endDate);
        //   let resStatus = "";
        //   if (isSurveyActive && survey.hasResponded) resStatus = "CS_SURVEY_RESPONDED";
        //   else if (isSurveyActive) resStatus = "CS_SURVEY_YT_TO_RESPOND";
        //   else resStatus = "CANNOT_RESPOND_MSG";
        //   return { hasResponded: false, responseStatus: resStatus, ...survey };
        // });
        const activeSurveysList = [];
        const inactiveSurveysList = [];
        let surveyListId = [];
        ServiceDefinition?.map((element, index) => {
          surveyListId.push(element.code)
          if (element.isActive && isActive(element.additionalDetails.startDate, element.additionalDetails.endDate)) {
            element.hasResponded = false;
            element.responseStatus = "CS_SURVEY_YT_TO_RESPOND";
            activeSurveysList.push(element);
          } else if (element.additionalDetails.endDate) {
            element.hasResponded = false;
            element.responseStatus = "CANNOT_RESPOND_MSG";
            inactiveSurveysList.push(element)
          }
        })

        activeSurveysList.reverse();
        inactiveSurveysList.reverse();
        
        return {
          activeSurveysList,
          inactiveSurveysList,
          surveyListId
        };
      },
    }
  );

  ( async() => {
    if (data?.surveyListId && selecedSurveyData){
      data?.activeSurveysList.map((element,index)=>{
        selecedSurveyData.Service.map((ele)=>{
          if (element.code === ele.referenceId ){
            data.activeSurveysList[index].hasResponded = true;
            data.activeSurveysList[index].responseStatus = "CS_SURVEY_RESPONDED";
          }
        })
      })

      // data?.inactiveSurveysList.map((element,index)=>{
      //   selecedSurveyData.Service.map((ele)=>{
      //     if (element.code === ele.referenceId ){
      //       data.inactiveSurveysList[index].hasResponded = true;
      //       data.inactiveSurveysList[index].responseStatus = "CS_SURVEY_RESPONDED";
      //     }
      //   })
      // })
    }
  })();

  // useEffect(()=>{
  //   if (data?.surveyListId && selecedSurveyData){
  //     data?.surveyListId.map((element,index)=>{
  //       selecedSurveyData.Service.map((ele)=>{
  //         if (element === ele.referenceId && data?.activeSurveysList[index]?.responseStatus){
  //           data.activeSurveysList[index].hasResponded = true;
  //           data.activeSurveysList[index].responseStatus = "CS_SURVEY_RESPONDED";
  //         }
  
  //       })
  
  //     })
  //   }
  // },[data,selecedSurveyData])
  


  // const { data, isLoading: isLoadingSurveys } = Digit.Hooks.survey.useSearch(
  //   { tenantIds },
  //   {
  //     select: ({ Surveys }) => {
  //       // const allSurveys = Surveys.map((survey) => ({ hasResponded: false, responseStatus: "CS_SURVEY_YT_TO_RESPOND", ...survey }));

  //       const allSurveys = Surveys.map((survey) => {
  //         const isSurveyActive = isActive(survey.startDate, survey.endDate);
  //         let resStatus = "";
  //         if (isSurveyActive && survey.hasResponded) resStatus = "CS_SURVEY_RESPONDED";
  //         else if (isSurveyActive) resStatus = "CS_SURVEY_YT_TO_RESPOND";
  //         else resStatus = "CANNOT_RESPOND_MSG";
  //         return { hasResponded: false, responseStatus: resStatus, ...survey };
  //       });
  //       //why hasResoponded always set to false here
  //       const activeSurveysList = [];
  //       const inactiveSurveysList = [];
  //       for (let survey of allSurveys) {
  //         if (survey.status === "ACTIVE" && isActive(survey.startDate, survey.endDate)) {
  //           activeSurveysList.push(survey);
  //         } else {
  //           inactiveSurveysList.push(survey);
  //         }
  //       }
  //       return {
  //         activeSurveysList,
  //         inactiveSurveysList,
  //       };
  //     },
  //   }
  // );



  // const handleCardClick = (details) => {
  //     history.push("/digit-ui/citizen/engagement/surveys/fill-survey", details);
  // };

  //trying to implement like this-> If user already responded then open ShowSurvey
  const handleCardClick = (details) => {
    
    if (!details.hasResponded) {
      history.push(`/digit-ui/citizen/engagement/surveys/fill-survey?applicationNumber=${details?.code}&tenantId=${details?.tenantId}`, details);
    } else {
      history.push("/digit-ui/citizen/engagement/surveys/show-survey", details);
    }
  };

  if (isLoadingSurveys) {
    return <Loader />;
  }

  return (
    <div className="survey-list-container">
      <Header>{`${t("CS_COMMON_SURVEYS")} (${data?.activeSurveysList?.length})`}</Header>

      {data?.activeSurveysList && data.activeSurveysList.length ? (
        data.activeSurveysList.map((data, index) => {
          return (
            <div className="surveyListCardMargin">
              <SurveyListCard
                header={data.code}
                about={data.additionalDetails.description}
                activeTime={data.additionalDetails.endDate}
                postedAt={data.auditDetails.createdTime}
                responseStatus={data.responseStatus}
                hasResponsed={false}
                key={index}
                onCardClick={() => handleCardClick(data)}
              />
            </div>
          );
        })
      ) : (
        <div className="centered-message">
          <p>{t("CS_NO_ACTIVE_SURVEYS")}</p>
        </div>
      )}

      <Header>{`${t("CS_COMMON_INACTIVE_SURVEYS")} (${data?.inactiveSurveysList?.length})`}</Header>

      {data?.inactiveSurveysList && data.inactiveSurveysList.length ? (
        data.inactiveSurveysList.map((data, index) => {
          return (
            <div className="surveyListCardMargin">
              <SurveyListCard
                header={data.code}
                about={data.additionalDetails.description}
                activeTime={data.additionalDetails.endDate}
                postedAt={data.auditDetails.createdTime}
                responseStatus={data.responseStatus}
                hasResponsed={false}
                key={index}
              />
            </div>
          );
        })
      ) : (
        <div className="centered-message">
          <p>{t(`CS_NO_INACTIVE_SURVEYS`)}</p>
        </div>
      )}
    </div>
  );
};

export default SurveyList;
