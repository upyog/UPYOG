/**
 * Renders search input fields for employee NOC Inbox.
 * Captures NOC Application Number and Source Reference (BPA) ID for inbox filtering.
 */
import React, {Fragment} from "react"
import { CardLabelError, SearchField, TextInput } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";

const SearchFormFieldsComponents = ({ registerRef, searchFieldComponents }) => {
  const { t } = useTranslation();

  return (
    <>
      <SearchField>
        <label>{t("NOC_APPLICATION_NUMBER_LABEL")}</label>
        <TextInput name="applicationNo" inputRef={registerRef("applicationNo").ref} {...registerRef("applicationNo")} />
      </SearchField>
      <SearchField>
        <label>{t("NOC_BPA_APPLICATION_NUMBER_LABEL")}</label>
        <TextInput name="sourceRefId" inputRef={registerRef("sourceRefId").ref} {...registerRef("sourceRefId")} />
      </SearchField>
      {searchFieldComponents ? (
        <div className="search-action-wrapper SubmitAndClearAllContainer">
          {searchFieldComponents}
        </div>
      ) : null}
    </>
  );
};

export default SearchFormFieldsComponents