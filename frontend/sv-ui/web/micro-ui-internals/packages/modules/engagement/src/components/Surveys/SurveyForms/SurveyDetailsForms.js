import { CardLabelError, Dropdown, RemoveableTag, TextInput, MultiSelectDropdown } from "@nudmcdgnpm/upyog-ui-react-components-lts";
import React, { Fragment, useMemo } from "react";
import { Controller } from "react-hook-form";

import { alphabeticalSortFunctionForTenantsBasedOnName } from "../../../utils/index";
const SurveyDetailsForms = ({ t, registerRef, controlSurveyForm, surveyFormState, surveyFormData, disableInputs, enableDescriptionOnly }) => {
  const ulbs = Digit.SessionStorage.get("ENGAGEMENT_TENANTS");
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const userInfo = Digit.UserService.getUser().info;
  const userUlbs = ulbs
    .filter((ulb) => userInfo?.roles?.some((role) => role?.tenantId === ulb?.code))
    .sort(alphabeticalSortFunctionForTenantsBasedOnName);
  const selectedTenat = useMemo(() => {
    const filtered = ulbs.filter((item) => item.code === tenantId);
    return filtered;
  }, [ulbs]);
  const checkRemovableTagDisabled = (isFormDisabled, isActiveSurveyEdit) => {

    //survey details page 
    if (!isActiveSurveyEdit && isFormDisabled){
      return true
    }
    //active survey editing
    else if(isActiveSurveyEdit){
      return true
    }
    //inactive survey editing
    return false
  }

  return (
    <div className="surveydetailsform-wrapper">
      <span className="surveyformfield">
        <label>{`${t("LABEL_FOR_ULB")} * `}</label>
        <Controller
          name="tenantIds"
          control={controlSurveyForm}
          defaultValue={selectedTenat}
          rules={{ required: true }}
          render={({ field }) => {
            const renderRemovableTokens = useMemo(
              () =>
                field?.value?.map((ulb, index) => {
                  return (
                    <RemoveableTag
                      key={index}
                      text={ulb.name}
                      disabled = {checkRemovableTagDisabled(disableInputs,enableDescriptionOnly)}
                      onClick={() => {
                        field.onChange(field?.value?.filter((loc) => loc.code !== ulb.code));
                      }}
                    />
                  );
                }),
              [field?.value]
            );
            return (
              <div style={{ display: "grid", gridAutoFlow: "row" }}>
                 {/* <Dropdown
                  allowMultiselect={true}
                  optionKey={"i18nKey"}
                  option={userUlbs}
                  placeholder={t("ES_COMMON_USER_ULBS")}
                  select={(e) => {
                    field.onChange([...(surveyFormData("tenantIds")?.filter?.((f) => e.code !== f?.code) || []), e]);
                  }}
                  selected={field?.value}
                  keepNull={true}
                  disable={disableInputs}
                  t={t}
                />  */}
                <MultiSelectDropdown
                  options={userUlbs}
                  isSurvey={true}
                  optionsKey="i18nKey"
                  props={field}
                  isPropsNeeded={true}
                  onSelect={(e) => {
                    field.onChange([...(surveyFormData("tenantIds")?.filter?.((f) => e.code !== f?.code) || []), e]);
                  }}
                  selected={field?.value}
                  defaultLabel={t("ES_COMMON_USER_ULBS")}
                  defaultUnit={t("CS_SELECTED_TEXT")}
                />
                {/* <div className="tag-container">{renderRemovableTokens}</div> */}
              </div>
            );
          }}
        />
        {surveyFormState?.errors?.tenantIds && <CardLabelError>{t("ES_ERROR_REQUIRED")}</CardLabelError>}
      </span>

      <span className="surveyformfield">
        <label>{`${t("CS_SURVEY_NAME")} * `}</label>
        <TextInput
          name="title"
          type="text"
          {...registerRef("title",{
            required: t("ES_ERROR_REQUIRED"),
            maxLength: {
              value: 60,
              message: t("EXCEEDS_60_CHAR_LIMIT"),
            },
            pattern:{
              value: /^[A-Za-z_-][A-Za-z0-9_\ -]*$/,
              message: t("ES_SURVEY_DONT_START_WITH_NUMBER")
            }
          })}
          disable={disableInputs}
        />
        {surveyFormState?.errors?.title && <CardLabelError>{surveyFormState?.errors?.["title"]?.message}</CardLabelError>}
      </span>

      <span className="surveyformfield">
        <label>{`${t("CS_SURVEY_DESCRIPTION")} `}</label>
        <TextInput
          name="description"
          type="text"
          {...registerRef("description",{
            //required: t("ES_ERROR_REQUIRED"),
            maxLength: {
              value: 140,
              message: t("EXCEEDS_140_CHAR_LIMIT"),
            },
            pattern:{
              value: /^[A-Za-z_-][A-Za-z0-9_\ -]*$/,
              message: t("ES_SURVEY_DONT_START_WITH_NUMBER")
            }
          })}
          disable={enableDescriptionOnly ?  !enableDescriptionOnly : disableInputs}
        />
        {surveyFormState?.errors?.description && <CardLabelError>{surveyFormState?.errors?.["description"]?.message}</CardLabelError>}
      </span>
    </div>
  );
};

export default SurveyDetailsForms;
