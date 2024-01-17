import React, { Fragment, useState } from "react";
import { Controller, useWatch } from "react-hook-form";
import { TextInput, SubmitBar, DatePicker, SearchField, Dropdown, Loader, MobileNumber ,Localities} from "@egovernments/digit-ui-react-components";

const SearchFields = ({ register, control, reset, tenantId, t }) => {
  const [locality, setLocality] = useState("");
  const [city, setcity] = useState("pg.citya");
  const propsForMobileNumber = {
    maxlength: 10,
    pattern: "[6-9][0-9]{9}",
    title: t("ES_SEARCH_APPLICATION_MOBILE_INVALID"),
    componentInFront: "+91",
  };

  const propsForOldConnectionNumberNpropertyId = {
    pattern: "[A-Za-z]{2}\-[A-Za-z]{2}\-[0-9]{4}\-[0-9]{4}\-[0-9]{2}\-[0-9]{2}\-[0-9]{6}|[A-Za-z]{2}\-[A-Za-z]{2}\-[0-9]{4}\-[0-9]{2}\-[0-9]{2}\-[0-9]{6}",
    title: t("ERR_DEFAULT_INPUT_FIELD_MSG"),
  };
  function selectLocality(value) {
    setLocality(value);
    console.log("value",value)
  }
  function selectCity(value) {
    setcity(value);
    setLocality("");
  }
  let validation = {}
  return (
    <>
      
      <SearchField>
        <label>{t("WS_SEARCH_CONNNECTION_CITY")}</label>
        <TextInput name="oldConnectionNumber" disbale={true}/>
      </SearchField>
      <SearchField>
        <label>{t("WS_SEARCH_CONNNECTION_OLD_CONSUMER_LABEL")}</label>
        <Localities
                selectLocality={selectLocality}
                tenantId={"pg.citya"}
                boundaryType="revenue"
                keepNull={false}
                optionCardStyles={{ height: "600px", overflow: "auto", zIndex: "10" }}
                selected={locality}
                //disable={!city?.code}
                disableLoader={false}
              />
      </SearchField>
      <SearchField style={{marginTop:"16px !important"}}>
        <SubmitBar label={t("WS_SEARCH_CONNECTION_SEARCH_BUTTON")} submit />
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
