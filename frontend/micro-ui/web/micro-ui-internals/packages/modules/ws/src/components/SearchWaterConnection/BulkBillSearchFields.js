import React, { Fragment, useState } from "react";
import { Controller, useWatch } from "react-hook-form";
import { TextInput, SubmitBar, DatePicker, SearchField, Dropdown, Loader, MobileNumber ,Localities} from "@egovernments/digit-ui-react-components";

const BulkBillSearchFields = ({ register, control, reset, tenantId, t }) => {
  const [locality, setLocality] = useState("");
  const tenant = Digit.ULBService.getCurrentTenantId();
  const [city, setcity] = useState("pg.citya");
console.log("tenant",tenant)
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
        <TextInput name="city" disable={true} value={tenant}/>
      </SearchField>
      <SearchField>
        <label>{t("WS_SEARCH_LOCALITY_LABEL")}</label>
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
export default BulkBillSearchFields;
