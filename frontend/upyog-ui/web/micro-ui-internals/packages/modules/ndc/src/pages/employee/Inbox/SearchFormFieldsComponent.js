import "../../../../css/ndc.css";
import React from "react";
import { SearchField, TextInput } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";

const SearchFormFieldsComponents = ({ registerRef, searchFormState, searchFieldComponents }) => {
  const { t } = useTranslation();

  const isMobile = window.Digit.Utils.browser.isMobile();

  /*
   * Desktop Search Form
   */
  if (!isMobile) {
    return (
      <React.Fragment>
        <div className="ndc-search-container-wide">
              {/* Application Number */}
              <SearchField>
                <label>{t("NOC_HOME_SEARCH_RESULTS_APP_NO_LABEL")}</label>

                <TextInput
                  name="applicationNo"
                  inputRef={registerRef("applicationNo").ref}
                  onChange={registerRef("applicationNo").onChange}
                  onBlur={registerRef("applicationNo").onBlur}
                />
              </SearchField>

              {/* Phone Number */}
              <SearchField>
                <label>{t("CORE_COMMON_PHONE_NUMBER")}</label>

                <TextInput
                  name="mobileNumber"
                  inputRef={registerRef("mobileNumber").ref}
                  onChange={registerRef("mobileNumber").onChange}
                  onBlur={registerRef("mobileNumber").onBlur}
                />
              </SearchField>

              {/* Search / Clear Buttons */}
              <div className="search-action-wrapper ndc-search-action-width">{searchFieldComponents}</div>
        </div>
      </React.Fragment>
    );
  }

  /*
   * Mobile Search Form
   */
  return (
    <React.Fragment>
      {/* Application Number */}
      <SearchField>
        <label>{t("NOC_HOME_SEARCH_RESULTS_APP_NO_LABEL")}</label>

        <TextInput name="applicationNo" {...registerRef("applicationNo")} />
      </SearchField>

      {/* Phone Number */}
      <SearchField>
        <label>{t("CORE_COMMON_PHONE_NUMBER")}</label>

        <TextInput name="mobileNumber" {...registerRef("mobileNumber")} />
      </SearchField>
    </React.Fragment>
  );
};

export default SearchFormFieldsComponents;
