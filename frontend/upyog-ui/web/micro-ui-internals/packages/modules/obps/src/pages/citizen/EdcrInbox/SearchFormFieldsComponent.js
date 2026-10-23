import { SearchField, TextInput } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Controller } from "react-hook-form";

const SearchFormFieldsComponents = ({ registerRef, searchFormState, searchFieldComponents, controlSearchForm }) => {
  const { t } = useTranslation();
  const isMobile = window.Digit.Utils.browser.isMobile();

  const renderField = (name) => (
    <Controller
      name={name}
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

  return (
    <>
      <SearchField>
        <label>{t("BPA_APPLICATION_NUMBER_LABEL")}</label>
        {renderField("applicationNumber")}
      </SearchField>
      <SearchField>
        <label>{t("BPA_EDCR_NO_LABEL")}</label>
        {renderField("edcrNumber")}
      </SearchField>
      {searchFieldComponents ? (
        <div className="search-action-wrapper SubmitAndClearAllContainer">
          {searchFieldComponents}
        </div>
      ) : null}
    </>
  );
};

export default SearchFormFieldsComponents;
