import { TextInput, CardLabel, LabelFieldPair, Dropdown, Loader, LocationSearch, CardLabelError } from "@upyog/digit-ui-react-components";
import React, { Fragment, useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { Controller } from "react-hook-form";
import { alphabeticalSortFunctionForTenantsBasedOnName } from "../../utils";
import { useLocation } from "react-router-dom";

const EventForm = ({ onSelect, config, formData, register, control, errors }) => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const serviceDefinitions = Digit.GetServiceDefinitions;
  const state = tenantId?.split('.')[0];
  const [subTypeMenu, setSubTypeMenu] = useState([]);
  const [subType, setSubType] = useState({});
  const ulbs = Digit.SessionStorage.get("ENGAGEMENT_TENANTS");
  const userInfo = Digit.UserService.getUser().info;
  const [complaintType, setComplaintType] = useState({}) 
  const [selectedLocality, setSelectedLocality] = useState({});
  const { data: fetchedLocalities } = Digit.Hooks.useBoundaryLocalities(
    "pg.citya",
    "admin",
    {},
    t
  );

  const [localities, setLocalities] = useState(fetchedLocalities);
  const userUlbs = ulbs.filter(ulb => userInfo?.roles?.some(role => role?.tenantId === ulb?.code)).sort(alphabeticalSortFunctionForTenantsBasedOnName)
  const getDefaultUlb = () => {
    if (formData?.defaultTenantId) {
      return ulbs?.find(ulb => ulb?.code === formData?.defaultTenantId);
    }
    if(tenantId){
      return ulbs?.find(ulb => ulb?.code === tenantId)
    }
    return userUlbs?.length === 1 ? userUlbs?.[0] : null
  }
 
  const { isLoading, data } = Digit.Hooks.useCommonMDMS(state, "mseva", ["EventCategories"]);
  const menu = Digit.Hooks.pgr.useComplaintTypes({ stateCode: Digit.ULBService.getCurrentTenantId() });
  console.log("menumenu",menu,data,errors)
  const location = useLocation();
  const isInEditFormMode = useMemo(()=>{
    if(location.pathname.includes('/engagement/event/edit-event')) return true;
    return false;
  },[location.pathname])
  async function selectedValue(value) {
    if (value.key !== complaintType.key) {

      if (value.key === "Others") {
        setSubType({ name: "" });
        setSubTypeMenu([{ key: "Others", name: t("SERVICEDEFS.OTHERS") }]);
      } else {
        setSubType({ name: "" });
        setComplaintType(value);
        Digit.SessionStorage.set("event",value)
        setSubTypeMenu(await serviceDefinitions.getSubMenu("pg.citya", value, t));
      }
    }
  }
  const findEventByCode = (input, obj2) => {
    const { key } = input;

    return obj2.mseva.EventCategories.find(event => event.code === key);
};
function selectLocality(locality) {
  Digit.SessionStorage.set("locality",locality)
  setSelectedLocality(locality);
}
  function selectedSubType(value) {
   
    
    const result = findEventByCode(value, data);
    console.log("selectedSubType",value,data,result)
    Digit.SessionStorage.set("selectedSubType",value)
    Digit.SessionStorage.set("eventType",result)
    setSubType(value);
  }
  useEffect(async ()=>{
    if (fetchedLocalities) {
      let __localityList =   fetchedLocalities;
      await setLocalities(__localityList);
      //setLocalities(__localityList)
     
    }
  },[fetchedLocalities])
  if (isLoading) {
    return (
      <Loader />
    );
  }

  
  return (
    <Fragment>
      <LabelFieldPair>
        <CardLabel className="card-label-smaller">{`${t(`EVENTS_ULB_LABEL`)} *`}</CardLabel>
        <div className="field">
          <Controller
            control={control}
            defaultValue={getDefaultUlb()}
            name="tenantId"
            rules={{ required: true }}
            render={({ onChange, value }) => <Dropdown option={userUlbs} selected={value} disable={isInEditFormMode ? true : userUlbs?.length === 1} optionKey="code" t={t} select={onChange} />}
          />
          {errors && errors['tenantId'] && <CardLabelError>{t(`EVENTS_TENANT_ERROR_REQUIRED`)}</CardLabelError>}
        </div>
      </LabelFieldPair>
      <LabelFieldPair>
        <CardLabel className="card-label-smaller">{`${t(`EVENTS_CITY_LABEL`)} *`}</CardLabel>
        <div className="field">
          <Controller
            control={control}
            defaultValue={getDefaultUlb()}
            name="city"
            rules={{ required: true }}
            render={({ onChange, value }) =>  <Dropdown isMandatory selected={selectedLocality} optionKey="i18nkey" option={localities} select={selectLocality} t={t} />}
          />
          {errors && errors['tenantId'] && <CardLabelError>{t(`EVENTS_TENANT_ERROR_REQUIRED`)}</CardLabelError>}
        </div>
      </LabelFieldPair>
      {/* */}
      <LabelFieldPair>
        <CardLabel className="card-label-smaller">{`${t(`EVENTS_CATEGORY_LABEL`)} *`}</CardLabel>
        <div className="field">
          <Controller
            name="eventCategory"
            control={control}
            defaultValue={formData?.category ? data?.mseva?.EventCategories.filter(category => category.code === formData?.category)?.[0] : null}
            render={({ onChange, ref, value }) => <Dropdown inputRef={ref} option={menu} optionKey="name" id="complaintType" selected={complaintType} select={selectedValue}  />}
          />
         
        </div>
        </LabelFieldPair>
        <LabelFieldPair>
        <CardLabel className="card-label-smaller">{`${t(`EVENTS_SUB_CATEGORY_LABEL`)} *`}</CardLabel>
        <div className="field">
          <Controller
            name="eventSubCategory"
            control={control}
            render={({ onChange, ref, value }) => <Dropdown inputRef={ref} option={subTypeMenu}optionKey="name" id="complaintSubType" selected={subType} select={selectedSubType} />}
          />
        </div>
      </LabelFieldPair>
      <LabelFieldPair>
        <CardLabel className="card-label-smaller">{`${t(`EVENTS_NAME_LABEL`)} *`}</CardLabel>
        <div className="field">
          <Controller
            defaultValue={formData?.name}
            render={({ onChange, ref, value }) => <TextInput value={value} type="text" name="name" onChange={onChange} inputRef={ref} />}
            name="name"
            rules={{ required: true , maxLength:66}}
            control={control}
          />
           {errors && errors?.name && errors?.name?.type==="required" && <CardLabelError>{t(`EVENTS_COMMENTS_ERROR_REQUIRED`)}</CardLabelError>}
          {errors && errors?.name && errors?.name?.type==="maxLength" && <CardLabelError>{t(`EVENTS_MAXLENGTH_66_CHARS_REACHED`)}</CardLabelError>}
        </div>
      </LabelFieldPair> 
    </Fragment>
  )
};

export default EventForm;