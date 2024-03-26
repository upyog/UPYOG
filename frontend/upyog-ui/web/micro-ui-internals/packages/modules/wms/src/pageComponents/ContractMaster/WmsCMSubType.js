import React, { useState, useEffect } from "react";
import { CardLabelError, Loader } from "@egovernments/digit-ui-react-components";
import { Dropdown, LabelFieldPair, CardLabel } from "@egovernments/digit-ui-react-components";
import { useLocation } from "react-router-dom";

const WmsCMSubType = ({ t, config, onSelect, formData = {}, userType }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { pathname: url } = useLocation();
  const { data: citizenTypes, isLoading } = Digit?.Hooks?.wms?.cm?.useWMSMaster(tenantId, "WMS_SUB_TYPE_VIEW") || {};
  console.log("citizenTypes SubTYpes ", citizenTypes)

  const [citizenType, setcitizenType] = useState(formData?.WmsCMSubType);
  const [citizenTypeList, setcitizenTypeList] = useState();
  console.log("citizenTypes SubTYpes citizenTypeList ", citizenTypeList)

  const [isTrue, setisTrue] = useState(false);
  function SelectcitizenType(value) {
  if(!value?.name){setisTrue(true)}else{setisTrue(false);setcitizenType(value);}
  }

  useEffect(() => {
    onSelect(config.key, citizenType);
  }, [citizenType]);

  useEffect(()=>{
    let fData=[]
    if(citizenTypes?.WMSContractorSubTypeApplications?.length>0){
      const filterData = citizenTypes?.WMSContractorSubTypeApplications.filter((res)=> res.contractor_stype_status=="Active");
      filterData.forEach(element => {
      fData.push({
  "id":element?.contractor_id,
  "name": element?.contractor_stype_name,
  "status": element?.contractor_stype_status
})
});
    setcitizenTypeList(fData)
    }
  },[citizenTypes])

//   const vendorStatus = [
//     {
//         "code": "CONTRACT_MASTER_ACTIVE",
//         "name": "Active",
//         "module": "rainmaker-tl",
//         "locale": "en_IN"
//     },
//     {
//         "code": "CONTRACT_MASTER_INACTIVE",
//         "name": "INactive",
//         "module": "rainmaker-tl",
//         "locale": "en_IN"
//     }
// ];

const option = [{"name":"Non-Specified Hindu Undivided Family"},{"name":"Individual"},{"name":"Registered Partnership (Business)"},{"name":"Association of Persons"},{"name":"Body of Individuals"},{"name":"A Domestic Company (Public subs. interested)"},{"name":"A company other than a Domestic Company"},{"name":"Registered Partnership (Professional)"},{"name":"A Domestic Company (Public non-subs. interested)"},{"name":"Specified Hindu Undivided Family"}
        // {
        //     "code": "CONTRACT_MASTER_CONTRACTOR",
        //     "name": "Contractor",
        //     "module": "rainmaker-tl",
        //     "locale": "en_IN"
        // }
    ];
  const inputs = [
    {
      label: "Sub Type",
      type: "text",
      name: "vendor_sub_type",
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
          // option={citizenTypes?.["egov-hrms"]?.EmployeeType}
          option={citizenTypeList!=undefined && citizenTypeList}
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

export default WmsCMSubType;





