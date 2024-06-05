import React from "react";
import { useForm } from "react-hook-form";
import { ActionBar, Header, Loader, SubmitBar, Toast } from "@upyog/digit-ui-react-components";

import CitizenSurveyQuestion from "./CitizenSurveyQuestion";
import { useTranslation } from "react-i18next";


const CitizenSurveyDisplayForm = ({ surveyData, onFormSubmit,submitDisabled,formDisabled,formDefaultValues,isLoading, showToast, isSubmitDisabled }) => {
  //need to disable this form and fill with default values if formDisabled is true
  const {
    register: registerRef,
    control: controlSurveyForm,
    handleSubmit: handleSurveyFormSubmit,
    setValue: setSurveyFormValue,
    getValues: getSurveyFormValues,
    reset: resetSurveyForm,
    formState: surveyFormState,
    clearErrors: clearSurveyFormsErrors,
  } = useForm({
    defaultValues:formDefaultValues
  });

  let surveyQuestion = {...surveyData};
  surveyQuestion.title = surveyData.code;
  surveyQuestion.questions = [];
  surveyQuestion?.attributes?.map((element, index) => {
    surveyQuestion.questions.push(
      {
        "uuid": element.code,
        // "surveyId": element.code,
        "questionStatement": element.code,
        "options": [
          ...element.additionalDetails.options
        ],
        "auditDetails": element.auditDetails,
        "status": element.isActive?"ACTIVE":"INACTIVE",
        "type": element.additionalDetails.type,
        "required": element.required,
        "qorder": element.order++,
        "value": "12345"
      }
    ); 
    
  })

//   surveyQuestion.questions = [
//     {
//         "uuid": "7d2aee5b-7ff5-4bda-9128-d3bd59e7a388",
//         "surveyId": "SY-2023-07-21-000034",
//         "questionStatement": "short",
//         "options": [
//             "NA"
//         ],
//         "auditDetails": {
//             "createdBy": "1fd992e1-7d43-43bb-9666-29d32af6697f",
//             "lastModifiedBy": "1fd992e1-7d43-43bb-9666-29d32af6697f",
//             "createdTime": 1689934066437,
//             "lastModifiedTime": 1689934066437
//         },
//         "status": "ACTIVE",
//         "type": "SHORT_ANSWER_TYPE",
//         "required": false,
//         "qorder": 1
//     },
//     {
//         "uuid": "22b8763a-3cb0-4c57-b794-ad1f88eafe49",
//         "surveyId": "SY-2023-07-21-000034",
//         "questionStatement": "mul",
//         "options": [
//             "1",
//             "2",
//             "3",
//             "4"
//         ],
//         "auditDetails": {
//             "createdBy": "1fd992e1-7d43-43bb-9666-29d32af6697f",
//             "lastModifiedBy": "1fd992e1-7d43-43bb-9666-29d32af6697f",
//             "createdTime": 1689934066437,
//             "lastModifiedTime": 1689934066437
//         },
//         "status": "ACTIVE",
//         "type": "MULTIPLE_ANSWER_TYPE",
//         "required": false,
//         "qorder": 2
//     },
//     {
//         "uuid": "d6641081-2c08-49e7-b850-2efb13043b17",
//         "surveyId": "SY-2023-07-21-000034",
//         "questionStatement": "check",
//         "options": [
//             "1",
//             "2",
//             "3",
//             "4"
//         ],
//         "auditDetails": {
//             "createdBy": "1fd992e1-7d43-43bb-9666-29d32af6697f",
//             "lastModifiedBy": "1fd992e1-7d43-43bb-9666-29d32af6697f",
//             "createdTime": 1689934066438,
//             "lastModifiedTime": 1689934066438
//         },
//         "status": "ACTIVE",
//         "type": "CHECKBOX_ANSWER_TYPE",
//         "required": false,
//         "qorder": 3
//     },
//     {
//         "uuid": "03cf1483-d2ca-4e97-a58c-a3a730de16a0",
//         "surveyId": "SY-2023-07-21-000034",
//         "questionStatement": "para",
//         "options": [
//             "NA"
//         ],
//         "auditDetails": {
//             "createdBy": "1fd992e1-7d43-43bb-9666-29d32af6697f",
//             "lastModifiedBy": "1fd992e1-7d43-43bb-9666-29d32af6697f",
//             "createdTime": 1689934066438,
//             "lastModifiedTime": 1689934066438
//         },
//         "status": "ACTIVE",
//         "type": "LONG_ANSWER_TYPE",
//         "required": false,
//         "qorder": 4
//     },
//     {
//         "uuid": "acc77cf9-fcab-4198-be66-da23e5227627",
//         "surveyId": "SY-2023-07-21-000034",
//         "questionStatement": "date",
//         "options": [
//             "NA"
//         ],
//         "auditDetails": {
//             "createdBy": "1fd992e1-7d43-43bb-9666-29d32af6697f",
//             "lastModifiedBy": "1fd992e1-7d43-43bb-9666-29d32af6697f",
//             "createdTime": 1689934066438,
//             "lastModifiedTime": 1689934066438
//         },
//         "status": "ACTIVE",
//         "type": "DATE_ANSWER_TYPE",
//         "required": false,
//         "qorder": 5
//     },
//     {
//         "uuid": "b2461bff-e893-499d-9b24-e32eef51865b",
//         "surveyId": "SY-2023-07-21-000034",
//         "questionStatement": "time",
//         "options": [
//             "NA"
//         ],
//         "auditDetails": {
//             "createdBy": "1fd992e1-7d43-43bb-9666-29d32af6697f",
//             "lastModifiedBy": "1fd992e1-7d43-43bb-9666-29d32af6697f",
//             "createdTime": 1689934066438,
//             "lastModifiedTime": 1689934066438
//         },
//         "status": "ACTIVE",
//         "type": "TIME_ANSWER_TYPE",
//         "required": false,
//         "qorder": 6
//     }
// ]

  const {t} = useTranslation()
  if(isLoading){
    return <Loader/>
  }
  return (
    <div className="citizenSurvey-wrapper">
      <Header>{surveyData?.title?.toUpperCase()}</Header>
      <form onSubmit={handleSurveyFormSubmit(onFormSubmit)}>
        {surveyQuestion?.questions?.length ? surveyQuestion.questions.map((config, index) => <CitizenSurveyQuestion key={index} t={t} question={config} control={controlSurveyForm} register={registerRef} values={getSurveyFormValues} formState={surveyFormState} formDisabled={formDisabled} index={index}/>) : null}
        {showToast && <Toast error={showToast.key} label={t(showToast.label)} />}
        <ActionBar>
          {!submitDisabled && <SubmitBar disabled={isSubmitDisabled} label={t("CS_SUBMIT_SURVEY")} submit="submit" />}
        </ActionBar>
      </form>
    </div>
  );
};

export default CitizenSurveyDisplayForm;
