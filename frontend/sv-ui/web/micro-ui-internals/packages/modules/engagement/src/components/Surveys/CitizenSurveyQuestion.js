import { Card, CardLabelError, CheckBox, RadioButtons, TextArea, TextInput } from "@nudmcdgnpm/upyog-ui-react-components-lts";
import React, { Fragment } from "react";
import { Controller } from "react-hook-form";

const CitizenSurveyQuestion = ({t, question, control, register, values, formState,formDisabled, index }) => {
  const formErrors = formState?.errors;
  
  if (!question) return;
  const displayAnswerField = (answerType) => {
    switch (answerType) {
      case "SHORT_ANSWER_TYPE":
        return (
          <Fragment>
            <TextInput
              name={question.uuid}
              disabled={formDisabled}
              type="text"
              inputRef={register({
                maxLength: {
                  value: 200,
                 message: t("EXCEEDS_200_CHAR_LIMIT"),
                },
                required:question.required,
              })}
            />
            {formErrors && formErrors?.[question.uuid] && formErrors?.[question.uuid]?.type === "required" && (
              <CardLabelError>{t(`CS_COMMON_REQUIRED`)}</CardLabelError>)}
            {formErrors && formErrors?.[question.uuid] && formErrors?.[question.uuid]?.type === "maxLength" && (
              <CardLabelError>{t(`EXCEEDS_200_CHAR_LIMIT`)}</CardLabelError>)} 
          </Fragment>
        );
      case "LONG_ANSWER_TYPE":
        return (
          <Fragment>
            <TextArea
              name={question.uuid}
              disabled={formDisabled}
              inputRef={register({
                maxLength: {

                  value: 500,
                  message: t("EXCEEDS_500_CHAR_LIMIT"),
                },
                required:question.required
              })}
            />
            {formErrors && formErrors?.[question.uuid] && formErrors?.[question.uuid]?.type === "required" && (
              <CardLabelError>{t(`CS_COMMON_REQUIRED`)}</CardLabelError>)} 
            {formErrors && formErrors?.[question.uuid] && formErrors?.[question.uuid]?.type === "maxLength" && (
              <CardLabelError>{t(`EXCEEDS_500_CHAR_LIMIT`)}</CardLabelError>)}
          </Fragment>
        );
      case "MULTIPLE_ANSWER_TYPE":
        return (
          <Fragment>
            <Controller
              control={control}
              name={question.uuid}
              //defaultValue={surveyFormState?.collectCitizenInfo}
              rules={{ required: question.required }}
              render={({ field }) => (
                <RadioButtons
                  disabled={formDisabled}
                  onSelect={field.onChange}
                  selectedOption={field.value}
                  optionsKey=""
                  options={[...question.options]}
                  //disabled={disableInputs}
                />
              )}
            />
            {formErrors && formErrors?.[question.uuid] && formErrors?.[question.uuid]?.type === "required" && (
              <CardLabelError>{t(`EVENTS_TO_DATE_ERROR_REQUIRED`)}</CardLabelError>
            )}
          </Fragment>
        );
      case "CHECKBOX_ANSWER_TYPE":
        return (
          <Fragment>
            <Controller
              control={control}
              name={question.uuid}
              //defaultValue={surveyFormState?.collectCitizenInfo}
              //rules={{required:true}}
              rules={{ required:question.required }}
              render={({ field}) => {
                return (
                <div className="align-columns">
                  {question.options.map((option) => {
                    return (
                      <CheckBox
                        disable={formDisabled}
                        key={option}
                        onChange={(e) => {
                          if (e.target.checked) {
                            onChange([option,...field.value?field.value:[]]);             
                          } else {
                            field.value && field.onChange(field.value?.filter((item) => item !== option));
                          }
                        }}
                        checked={typeof field.value === "string" ? !!([field.value]?.find(e => e === option)) : !!field.value?.find(e => e === option)}
                        label={option}
                        checkboxWidth = {{width:"34px",height:"34px"}}
                        style={{marginTop:"5px", overflowWrap:"break-word"}}
                      />
                    );
                  })}
                </div>
              )}}
            />
            {formErrors && formErrors?.[question.uuid] && formErrors?.[question.uuid]?.type ==="required" && (
              <CardLabelError style={{marginTop:"20px"}}>{t(`CS_COMMON_REQUIRED`)}</CardLabelError>
            )}
          </Fragment>
        );
      // case "CHECKBOX_ANSWER_TYPE":
      //   return (
      //     <Fragment>
      //     {question.options.map((option,index) => (
      //     <div>
      //       <label for="checkbox">
      //         <input
      //         control={control}
      //         id={option}
      //         type="checkbox"
      //         name={option}
      //         value={option}
      //         ref={register({
      //           required:false,
      //         })}
      //       />
      //         {option}</label>
            
      //     </div>
      //     ))}
          
      //       {formErrors && formErrors?.[question.uuid] && formErrors?.[question.uuid]?.type ==="required" && (
      //         <CardLabelError>{t(`CS_COMMON_REQUIRED`)}</CardLabelError>
      //       )}
      //     </Fragment>
      //   );
      case "DATE_ANSWER_TYPE":
        return (
          <Fragment>
            <Controller
              control={control}
              name={question.uuid}
              //defaultValue=
              rules={{
                required: question.required,
                // validate: { isValidToDate }
              }}

              render={({ onChange, value }) => <TextInput disabled={formDisabled} type="date"  onChange={onChange} defaultValue={value} />}
            />
            {formErrors && formErrors?.[question.uuid] && formErrors?.[question.uuid]?.type === "required" && (
              <CardLabelError>{t(`EVENTS_TO_DATE_ERROR_REQUIRED`)}</CardLabelError>
            )}
          </Fragment>
        );
      case "TIME_ANSWER_TYPE":
        return (
          <Fragment>
            <Controller
              control={control}
              name={question.uuid}
              //defaultValue={surveyFormState?.toTime}
              rules={{
                required: question.required,
                // validate: { isValidToTime }
              }}
              render={({ onChange, value }) => <TextInput type="time" disabled={formDisabled}  onChange={onChange} defaultValue={value} />}
            />
            {formErrors && formErrors?.[question.uuid] && formErrors?.[question.uuid]?.type === "required" && (
              <CardLabelError>{t(`EVENTS_TO_DATE_ERROR_REQUIRED`)}</CardLabelError>
            )}
          </Fragment>
        );

      default:
        return (
          <TextInput
            name={question.uuid}
            disabled={formDisabled}
            type="text"
            inputRef={register({
              maxLength: {
                value: 60,
                message: t("EXCEEDS_60_CHAR_LIMIT"),
              },
              required:question.required
            })}
          />
        );
    }
  };
  return (
    <Card>
      <div className="surveyQuestion-wrapper">
        <span className="question-title">{index+1}. {question.questionStatement} {question?.required? "*":""}</span>
        <span>{displayAnswerField(question.type)}</span>
      </div>
    </Card>
  );
};

export default CitizenSurveyQuestion;
