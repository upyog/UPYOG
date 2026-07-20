import React, { Fragment } from "react";
import { CardLabelError, SearchField, TextInput } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";

const SearchFormFieldsComponents = ({ registerRef, searchFormState, searchFieldComponents }) => {
  const { t } = useTranslation();
  const isMobile = window.Digit.Utils.browser.isMobile();


  if (!isMobile) {
    return (
      <React.Fragment>
        <div className="search-container ndc-search-container-wide">
          <div className="search-complaint-container">
            <div className="complaint-input-container ndc-text-start">
              <SearchField>
                <label>{t("NOC_HOME_SEARCH_RESULTS_APP_NO_LABEL")}</label>
                <TextInput name="applicationNo" inputRef={registerRef("applicationNo").ref} onChange={registerRef("applicationNo").onChange} onBlur={registerRef("applicationNo").onBlur}/>
              </SearchField>
              <SearchField>
                <label>{t("CORE_COMMON_PHONE_NUMBER")}</label>
                <TextInput name="mobileNumber" inputRef={registerRef("mobileNumber").ref} onChange={registerRef("mobileNumber").onChange} onBlur={registerRef("mobileNumber").onBlur}/>
              </SearchField>
              <div className="search-action-wrapper ndc-search-action-width">
                {searchFieldComponents}
              </div>
            </div>
          </div>
        </div>
      </React.Fragment>
    );
  }

  return (
    <>
      <SearchField>
        <label>{t("NOC_HOME_SEARCH_RESULTS_APP_NO_LABEL")}</label>
        <TextInput name="applicationNo" {...registerRef("applicationNo")} />
      </SearchField>
      <SearchField>
        <label>{t("CORE_COMMON_PHONE_NUMBER")}</label>
        <TextInput name="mobileNumber" inputRef={registerRef("mobileNumber")} />
      </SearchField>
    </>
  );
};

export default SearchFormFieldsComponents;
