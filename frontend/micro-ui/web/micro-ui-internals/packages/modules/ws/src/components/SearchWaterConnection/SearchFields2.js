import React, { Fragment } from "react";
import { Controller, useWatch } from "react-hook-form";
import { TextInput, SubmitBar, DatePicker, SearchField, Dropdown, Loader, MobileNumber } from "@nudmcdgnpm/digit-ui-react-components";

const SearchFields = ({ register, control, reset, tenantId, t }) => {
  const propsForMobileNumber = {
    maxlength: 10,
    pattern: "[6-9][0-9]{9}",
    title: t("ES_SEARCH_APPLICATION_MOBILE_INVALID"),
    componentInFront: "+91",
  };

  const propsForOldConnectionNumberNpropertyId = {
    pattern:  {
      value: "",
    },
    title: t("ERR_DEFAULT_INPUT_FIELD_MSG"),
  };

  return (
    <>
      <SearchField style={{width:"100%"}}>
        <label>{t("WS_PROPERTY_ID_LABEL")}</label>
        <TextInput name="propertyId" inputRef={register({})} />
      </SearchField>

      <SearchField className="submit">
        <SubmitBar label={t("WS_SEARCH_CONNECTION_SEARCH_INTEGRATED_BUTTON")} submit />
        <p
          onClick={() => {
            reset({
              searchType:"CONNECTION",
              mobileNumber: "",
              offset: 0,
              limit: 10,
              sortBy: "commencementDate",
              sortOrder: "DESC",
              propertyId: "",
              connectionNumber: "",
              oldConnectionNumber: "",
            });
          }}
        >
          {t("WS_SEARCH_CONNECTION_RESET_BUTTON")}
        </p>
      </SearchField>
    </>
  );
};
export default SearchFields;
