import React, { useState, useEffect } from "react";
import { CardLabelError, Loader } from "@egovernments/digit-ui-react-components";
import { Dropdown, LabelFieldPair, CardLabel } from "@egovernments/digit-ui-react-components";
import { useLocation } from "react-router-dom";

const WmsCMFunction = ({ t, config, onSelect, formData = {}, userType }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { pathname: url } = useLocation();
  const editScreen = url.includes("/modify-application/");
  const { data: citizenTypes, isLoading } = Digit?.Hooks?.wms?.cm?.useWMSMaster(tenantId, "WMS_FUNCTION_APP_LIST") || {};
  const [citizenType, setcitizenType] = useState(formData?.WmsCMFunction);
  
  const [citizenTypeList, setcitizenTypeList] = useState();
  console.log("citizenTypes vendor type ", citizenTypes)

  const [isTrue, setisTrue] = useState(false);
  function SelectcitizenType(value) {
  if(!value?.name){setisTrue(true)}else{setisTrue(false);setcitizenType(value);}
  }

  useEffect(() => {
    onSelect(config.key, citizenType);
  }, [citizenType]);

  useEffect(()=>{
    let fData=[]
    if(citizenTypes?.WMSFunctionApplications?.length>0){
      const filterData = citizenTypes?.WMSFunctionApplications.filter((res)=> res.status=="Active");
      filterData.forEach(element => {
        fData.push({
    "id":element?.function_id,
    "name": element?.function_name,
    "status": element?.status
  })
  });
      setcitizenTypeList(fData)
    }
  },[citizenTypes])


//   const vendorFunction = [
//     {
//         "code": "CONTRACT_MASTER_FUNCTIONA",
//         "name": "Function A",
//         "module": "rainmaker-tl",
//         "locale": "en_IN"
//     },
//     {
//         "code": "CONTRACT_MASTER_FUNCTIONB",
//         "name": "Function B",
//         "module": "rainmaker-tl",
//         "locale": "en_IN"
//     }
// ];
  const inputs = [
    {
      label: "Function",
      type: "text",
      name: "function",
      // validation: {
      //   isRequired: true,
      // },
      // isMandatory: true,
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
          {/* {input.isMandatory ? " * " : null} */}
        </CardLabel>
        <Dropdown
          className="form-field"
          selected={citizenType}
          // option={citizenTypes?.["egov-hrms"]?.EmployeeType}
          option={citizenTypeList!=undefined && citizenTypeList}
          // option={vendorFunction}
          select={SelectcitizenType}
          onBlur={SelectcitizenType}
          optionKey="name"
          defaultValue={undefined}
          t={t}
        />
            {/* {isTrue&&<CardLabelError style={{ width: "100%", marginTop: '-15px', fontSize: '16px', marginBottom: '12px'}}>{t("Require Field")}</CardLabelError>} */}

      </LabelFieldPair>
    );
  });
};

export default WmsCMFunction;





