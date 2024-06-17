import React, { useState } from "react";
import {CloseSvg, Loader, DateRange, Localities, ApplyFilterBar, SubmitBar, Dropdown, RefreshIcon,CardLabel,TextInput } from "@upyog/digit-ui-react-components";
import { useTranslation } from "react-i18next";
// import Status from "./Status";

const SearchCHB = ({onFilterChange, searchParams }) => {
  const { t } = useTranslation();
  const [localSearchParams, setLocalSearchParams] = useState(() => ({ ...searchParams }));
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const state = tenantId?.split('.')[0];
  const stateId = Digit.ULBService.getStateId();
  const { isLoading, data } = Digit.Hooks.useCommonMDMS(state, "mseva", ["EventCategories"]);

  // const { data1: Resident } = Digit.Hooks.chb.useChbCommunityHall(stateId, "CHB", "ChbCommunityHalls");
  // console.log("data is--->",...Resident);
  const [applicantName, setName] = useState("");
  

//   const clearAll = () => {
//     setLocalSearchParams({ eventCategory: null, eventStatus: [], range: { startDate: null, endDate: new Date(""), title: "" } })
//     onFilterChange({ eventCategory: null, eventStatus: [], range: { startDate: null, endDate: new Date(""), title: "" } });
//     onClose?.();
//   };
let validation = {};
function setOwnerName(e) {
  setName(e.target.value);
}
  const applyLocalFilters = () => {
    onFilterChange(localSearchParams);
    onClose?.();
  };
  const handleChange = (data) => {
    setLocalSearchParams({ ...localSearchParams, ...data });
  
  };
//   const onStatusChange = (e, type) => {
//     if (e.target.checked) handleChange({ eventStatus: [...(localSearchParams?.eventStatus || []), type] })
//     else handleChange({ eventStatus: localSearchParams?.eventStatus?.filter(status => status !== type) })
//   }
function goNext() {
    onSelect();
  }
  if (isLoading) {
    return (
      <Loader />
    );
  }
  
  return (
    <div>
      <div style={{ width: '50%' }}>
      <CardLabel>{`${t("SEARCH_COMMUNITY_HALL")}`}</CardLabel>
        <Dropdown option={data?.mseva?.EventCategories} optionKey="code" t={t} selected={localSearchParams?.eventCategory} select={val => handleChange({ eventCategory: val })} />
        <DateRange t={t} values={localSearchParams?.range} onFilterChange={handleChange} labelClass="filter-label" />
        <CardLabel>{`${t("ENTER_HALL_CODE")}`}</CardLabel>
        <Dropdown option={data?.mseva?.EventCategories} optionKey="code" t={t} selected={localSearchParams?.eventCategory} select={val => handleChange({ eventCategory: val })} />
        </div>
        
        {/* <div>
          <SubmitBar onSubmit={() => applyLocalFilters()} label={t("ES_COMMON_SEARCH")} />
          <SubmitBar label={t("Book")} style={{margin:"20px"}}/>
        </div> */}
        {/* <TextInput
          t={t}
          type={"text"}
          isMandatory={false}
          optionKey="i18nKey"
          name="applicantName"
          value={localSearchParams?.range?.title}
          // onChange={setOwnerName}
         
        /> */}
    </div>
  )
}

export default SearchCHB;