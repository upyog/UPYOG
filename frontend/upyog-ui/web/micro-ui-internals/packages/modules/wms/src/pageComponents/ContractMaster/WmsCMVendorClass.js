import React, { useState, useEffect } from "react";
import { CardLabelError, Loader } from "@egovernments/digit-ui-react-components";
import { Dropdown, LabelFieldPair, CardLabel } from "@egovernments/digit-ui-react-components";
import { useLocation } from "react-router-dom";

const WmsCMVendorClass = ({ t, config, onSelect, formData = {}, userType }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { pathname: url } = useLocation();
  const editScreen = url.includes("/modify-application/");
  const { data: citizenTypes, isLoading } = Digit?.Hooks?.wms?.cm?.useWMSMaster(tenantId, "WMS_V_CLASS_LIST") || {};
  const [citizenType, setcitizenType] = useState(formData?.WmsCMVendorClass);
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
    if(citizenTypes?.WMSVendorClassApplications?.length>0){
      const filterData = citizenTypes?.WMSVendorClassApplications.filter((res)=> res.vendor_class_status=="Active");
      filterData.forEach(element => {
        fData.push({
    "id":element?.vendor_class_id,
    "name": element?.vendor_class_name,
    "status": element?.vendor_class_status
  })
  });
      setcitizenTypeList(fData)
    }
  },[citizenTypes])
//   const vendorClass = [
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
//     }
// ];
  const inputs = [
    {
      label: "Vendor Class",
      type: "text",
      name: "vendor_class",
      validation: {
        isRequired: true,
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
          {input.isMandatory ? <span style={{"color":"red"}}> * </span> : null}
        </CardLabel>
        <Dropdown
          className="form-field"
          selected={citizenType}
          option={citizenTypeList!=undefined && citizenTypeList}
          // option={vendorClass}
          onBlur={SelectcitizenType}
          select={SelectcitizenType}
          optionKey="name"
          defaultValue={undefined}
          t={t}
        />
            {isTrue&&<CardLabelError style={{ width: "100%", marginTop: '-15px', fontSize: '16px', marginBottom: '12px'}}>{t("Require Field")}</CardLabelError>}

      </LabelFieldPair>
    );
  });
};

export default WmsCMVendorClass;





