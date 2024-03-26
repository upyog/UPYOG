import React, { useState, useEffect } from "react";
import { CardLabelError, Loader } from "@egovernments/digit-ui-react-components";
import { Dropdown, LabelFieldPair, CardLabel } from "@egovernments/digit-ui-react-components";
import { useLocation } from "react-router-dom";

const WmsTMProjectName = ({ t, config, onSelect, formData = {}, userType,setError, clearErrors, formState, onBlur }) => {
  console.log({formData, userType,setError, clearErrors, formState, onBlur})
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { pathname: url } = useLocation();
  const editScreen = url.includes("/modify-application/");
  const { data: citizenTypes, isLoading } = Digit?.Hooks?.wms?.te?.useWMSTEMaster(tenantId, "WMS_PROJECT_NAME_ALL_RECORD") || {};
  // const citizenTypes = Digit?.Hooks?.wms?.cm?.useWMSMaster(tenantId, "WMS_V_TYPE") || {};
  const [citizenType, setcitizenType] = useState(formData?.WmsTMProjectName);
  const [citizenTypeList, setcitizenTypeList] = useState();
  const [isTrue, setisTrue] = useState(false);
console.log("citizenTypes project Name type ", citizenTypes)
  function SelectcitizenType(value) {
  if(!value?.name){setisTrue(true)}else{setisTrue(false);setcitizenType(value);}
  }

  useEffect(() => {
    // setcitizenTypeList(citizenTypes)
    onSelect(config.key, citizenType);
  }, [citizenType]);

  useEffect(()=>{
    let fData=[]
    if(citizenTypes?.ProjectApplications?.length>0){
      const filterData = citizenTypes?.ProjectApplications.filter((res)=> res.status=="Active");
      filterData.forEach(element => {
        fData.push({
    "id":element?.project_id,
    "name": element?.project_name_en,
    "status": element?.status
  })
  });
      setcitizenTypeList(fData)
    }
  },[citizenTypes])

//   const vendorType = [
//     {
//         "code": "CONTRACT_MASTER_CLASSA",
//         "name": "CLASS A",
//         "module": "rainmaker-tl",
//         "locale": "en_IN"
//     },
//     {
//         "code": "CONTRACT_MASTER_CLASSB",
//         "name": "CLASS B",
//         "module": "rainmaker-tl",
//         "locale": "en_IN"
//     },
//     {
//         "code": "CONTRACT_MASTER_CLASSC",
//         "name": "CLASS C",
//         "module": "rainmaker-tl",
//         "locale": "en_IN"
//     },
// ];
  const inputs = [
    {
      label: "WMS_TE_COMMON_PROJECT_NAME",
      type: "text",
      name: "project_name",
      validation: {
        isRequired: true,
        title: "Vendor Type Require",

      },
      isMandatory: true,
    },
  ];

  if (isLoading) {
    return <Loader />;
  }

  return inputs?.map((input, index) => {
    return (
      <LabelFieldPair key={index}>
        <CardLabel className="card-label-smaller">
          {t(input.label)}
          {input.isMandatory ? " * " : null}
        </CardLabel>
        <Dropdown
          className="form-field"
          selected={citizenType}
          // option={citizenTypeList?.["egov-hrms"]?.citizenType}
          option={citizenTypeList!=undefined && citizenTypeList}
          // option={vendorType}
          select={SelectcitizenType}
          onBlur={SelectcitizenType}
          optionKey="name"
          defaultValue={undefined}
          {...input.validation}
          t={t}
        />
            {isTrue&&<CardLabelError style={{ width: "100%", marginTop: '-15px', fontSize: '16px', marginBottom: '12px'}}>{t("Require Field")}</CardLabelError>}

      </LabelFieldPair>
    );
  });
};
export default WmsTMProjectName;