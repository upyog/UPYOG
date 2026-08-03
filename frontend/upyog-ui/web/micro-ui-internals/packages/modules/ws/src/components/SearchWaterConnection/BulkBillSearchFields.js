import React, { Fragment, useState, useEffect } from "react";
import { Controller, useWatch } from "react-hook-form";
import { TextInput, SubmitBar, SearchField, Localities } from "@nudmcdgnpm/digit-ui-react-components";

const BulkBillSearchFields = ({ register, control, reset, tenantId, t, setValue }) => {
  const [locality, setLocality] = useState("");
  const tenant = Digit.ULBService.getCurrentTenantId();
  function selectLocality(value) {
    console.log("register, control", register, tenant);
    setValue('locality', value);
    setValue('tenantId', tenant);
    setLocality(value);
  }
  return <>
      <SearchField>
        <label>{t("WS_SEARCH_CONNNECTION_CITY")}</label>
        <TextInput name="city" disable={true} value={t(tenant)} {...register("city")} />
      </SearchField>
      <SearchField>
        <label>{t("WS_SEARCH_LOCALITY_LABEL")}</label>
        <Controller
          name="locality"
          defaultValue={null}
          control={control}
          render={({ field }) => (
            <Localities
              selectLocality={(value) => {
                field.onChange(value);
                selectLocality(value);
              }}
              tenantId={tenant}
              boundaryType="revenue"
              keepNull={false}
              optionCardStyles={{ height: "600px", overflow: "auto", zIndex: "10" }}
              selected={field.value || locality}

      //disable={!city?.code}
      disableLoader={false} />)} />

      </SearchField>
      <SearchField className="ws-auto-23">
        <SubmitBar label={t("WS_SEARCH_CONNECTION_SEARCH_BUTTON")} submit />
        {/* <p
          onClick={() => {
            reset({
              searchType: "CONNECTION",
              mobileNumber: "",
              offset: 0,
              limit: 10,
              sortBy: "commencementDate",
              sortOrder: "DESC",
              propertyId: "",
              connectionNumber: "",
              oldConnectionNumber: "",
              locality:""
            });
          }}
         >
          {t("WS_SEARCH_CONNECTION_RESET_BUTTON")}
         </p> */}
      </SearchField>
    </>;
};
export default BulkBillSearchFields;
