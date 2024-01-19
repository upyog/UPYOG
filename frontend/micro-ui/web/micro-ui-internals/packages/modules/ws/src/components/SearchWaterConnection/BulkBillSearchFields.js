import React, { Fragment, useState, useEffect } from "react";
import { Controller, useWatch } from "react-hook-form";
import { TextInput, SubmitBar, DatePicker, SearchField, Dropdown, Loader, MobileNumber ,Localities} from "@egovernments/digit-ui-react-components";

const BulkBillSearchFields = ({ register, control, reset, tenantId, t ,setValue}) => {
  const [locality, setLocality] = useState("");
  const tenant = Digit.ULBService.getCurrentTenantId();
  const [city, setcity] = useState(tenant);


  // const [localities, setLocalities] = useState();
  // const [selectedLocality, setSelectedLocality] = useState();
  // useEffect(() => {
  //     const _locality = localities?.filter((e) => e.code === code)[0];
  //     setValue("locality", _locality);
    
  // }, [localities]);
console.log("tenant",tenant)
  function selectLocality(value) {
    console.log("register, control",register, control.fieldsRef)
    setValue('locality', value);
    setValue( "tenantId",tenantId)
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
        <TextInput name="city" disable={true} value={t(tenant)}  inputRef={register({})} />
      </SearchField>
      <SearchField>
        <label>{t("WS_SEARCH_LOCALITY_LABEL")}</label>
        <Controller
            name="locality"
            defaultValue={null}
            control={control}
            inputRef={register({})}
            render={(props) => (
              <Localities
                selectLocality={selectLocality}
                tenantId={tenant}
                boundaryType="revenue"
                keepNull={false}
                optionCardStyles={{ height: "600px", overflow: "auto", zIndex: "10" }}
                selected={locality}
           
                //disable={!city?.code}
                disableLoader={false}
              />
            )}
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
