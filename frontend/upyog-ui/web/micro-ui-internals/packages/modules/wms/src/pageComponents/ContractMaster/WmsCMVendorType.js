import React, { useState, useEffect } from "react";
import { CardLabelError, Loader } from "@egovernments/digit-ui-react-components";
import { Dropdown, LabelFieldPair, CardLabel } from "@egovernments/digit-ui-react-components";
import { useLocation } from "react-router-dom";

const WmsCMVendorType = ({ t, config, onSelect, formData = {}, userType,setError, clearErrors, formState, onBlur }) => {
  console.log({formData, userType,setError, clearErrors, formState, onBlur})
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { pathname: url } = useLocation();
  const editScreen = url.includes("/modify-application/");
  const { data: citizenTypes, isLoading } = Digit?.Hooks?.wms?.cm?.useWMSMaster(tenantId, "WMS_V_TYPE_LIST") || {};
  // const citizenTypes = Digit?.Hooks?.wms?.cm?.useWMSMaster(tenantId, "WMS_V_TYPE") || {};
  const [citizenType, setcitizenType] = useState(formData?.WmsCMVendorType);
  const [citizenTypeList, setcitizenTypeList] = useState();
  const [isTrue, setisTrue] = useState(false);
console.log("citizenTypes vendor type ", citizenTypeList)
  function SelectcitizenType(value) {
  if(!value?.name){setisTrue(true)}else{setisTrue(false);setcitizenType(value);}
  }

  useEffect(() => {
    // setcitizenTypeList(citizenTypes)
    onSelect(config.key, citizenType);
  }, [citizenType]);

  useEffect(()=>{
    let fData=[]
    if(citizenTypes?.WMSVendorTypeApplications?.length>0){
      const filterData = citizenTypes?.WMSVendorTypeApplications.filter((res)=> res.vendor_type_status=="Active");
      filterData.forEach(element => {
        fData.push({
    "id":element?.vendor_id,
    "name": element?.vendor_type_name,
    "status": element?.vendor_type_status
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
      label: "Vendor Type",
      type: "text",
      name: "vendor_type",
      validation: {
        isRequired: true,
        title: "Vendor Type Require",

      },
      isMandatory: true,
    },
  ];

  // if (isLoading) {
  //   return <Loader />;
  // }

  return inputs?.map((input, index) => {
    return (
      <LabelFieldPair key={index}>
        <CardLabel className="card-label-smaller">
          {t(input.label)}
          {input.isMandatory ? <span style={{"color":"red"}}> * </span> : null}
        </CardLabel>
        <Dropdown
          className="form-field"
          selected={citizenType}
          option={citizenTypeList==="undefined" ? <span>Loading...</span> : citizenTypeList }
          // option={citizenTypeList!=undefined && citizenTypeList}
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
export default WmsCMVendorType;