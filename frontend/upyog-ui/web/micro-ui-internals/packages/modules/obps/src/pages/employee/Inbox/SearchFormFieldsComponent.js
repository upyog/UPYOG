import React from "react";
import { CardLabelError, SearchField, TextInput, MobileNumber } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { Controller } from "react-hook-form";

const SearchFormFieldsComponents = ({ registerRef, searchFormState, searchFieldComponents, controlSearchForm }) => {
  const { t } = useTranslation();
  const isMobile = window.Digit.Utils.browser.isMobile();
  const isCitizen = window.location.href.includes("/citizen");

  const mobileValidation = {
    minLength: { value: 10, message: t("CORE_COMMON_MOBILE_ERROR") },
    maxLength: { value: 10, message: t("CORE_COMMON_MOBILE_ERROR") },
    pattern: { value: /[6789][0-9]{9}/, message: t("CORE_COMMON_MOBILE_ERROR") },
  };

  const applicationNoField = (
    <Controller
      name="applicationNo"
      control={controlSearchForm}
      defaultValue=""
      render={({ field }) => (
        <TextInput
          inputRef={field.ref}
          value={field.value || ""}
          onChange={(e) => field.onChange(e.target.value)}
          onBlur={field.onBlur}
        />
      )}
    />
  );

  const mobileNumberField = (
    <Controller
      name="mobileNumber"
      control={controlSearchForm}
      defaultValue=""
      rules={mobileValidation}
      render={({ field }) => (
        <MobileNumber
          type="number"
          inputRef={field.ref}
          value={field.value || ""}
          onChange={(val) => field.onChange(val)}
          onBlur={field.onBlur}
        />
      )}
    />
  );

  return (
    <React.Fragment>
      <SearchField>
        <label>{t("BPA_APPLICATION_NUMBER_LABEL")}</label>
        {applicationNoField}
      </SearchField>
      {!isCitizen && (
        <SearchField>
          <label>{t("CORE_COMMON_MOBILE_NUMBER")}</label>
          {mobileNumberField}
          {searchFormState?.errors?.["mobileNumber"]?.message && (
            <CardLabelError>{searchFormState.errors["mobileNumber"].message}</CardLabelError>
          )}
        </SearchField>
      )}
      {searchFieldComponents ? (
        <div className="search-action-wrapper SubmitAndClearAllContainer">
          {searchFieldComponents}
        </div>
      ) : null}
    </React.Fragment>
  );
};

export default SearchFormFieldsComponents;
