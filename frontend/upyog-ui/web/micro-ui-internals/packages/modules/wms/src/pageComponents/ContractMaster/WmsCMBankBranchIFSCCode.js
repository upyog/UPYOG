import React, { useState, useEffect } from "react";
import { Dropdown, LabelFieldPair, CardLabel, CardLabelError, Loader } from "@egovernments/digit-ui-react-components";
import { useLocation } from "react-router-dom";

const WmsCMBankBranchIFSCCode = ({ t, config, onSelect, formData = {}, userType }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { pathname: url } = useLocation();
  const editScreen = url.includes("/modify-application/");
  // const { data: citizenTypes = [], isLoading } = Digit.Hooks.hrms.useHrmsMDMS(tenantId, "egov-hrms", "EmployeeType") || {};
  const { data: citizenTypes, isLoading } = Digit?.Hooks?.wms?.cm?.useWMSMaster(tenantId,"WMS_BANK_BRANCH_TYPE") || {};
  // const bankList = Digit?.Hooks?.wms?.cm?.useWMSMaster(tenantId,"WMS_BANK_BRANCH_TYPE");
  const [citizenTypeList, setcitizenTypeList] = useState();

console.log("citizenTypes bank ",citizenTypes)
  const [citizenType, setcitizenType] = useState(formData?.WmsCMBankBranchIFSCCode);
  const [isTrue, setisTrue] = useState(false);
  function SelectcitizenType(value) {
  if(!value?.name){setisTrue(true)}else{setisTrue(false);setcitizenType(value);}
  }

  useEffect(() => {
    onSelect(config.key, citizenType);
  }, [citizenType]);
  useEffect(()=>{
    let fData=[]
    if(citizenTypes?.WMSBankDetailsApplications?.length>0){
      const filterData = citizenTypes?.WMSBankDetailsApplications.filter((res)=> res.status=="Active");
      filterData.forEach(element => {
      fData.push({
  "id":element?.bank_id,
  "name": element?.bank_branch_ifsc_code,
  "status": element?.status
})
});
    setcitizenTypeList(fData)
    }
  },[citizenTypes])
//   const bank = [
//     {
//         "code": "CONTRACT_MASTER_SBI",
//         "name": "State Bank of India",
//         "module": "rainmaker-tl",
//         "locale": "en_IN"
//     },
//     {
//         "code": "CONTRACT_MASTER_HDFC",
//         "name": "HDFC",
//         "module": "rainmaker-tl",
//         "locale": "en_IN"
//     }
// ];
  const inputs = [
    {
      label: "Bank, Branch & IFSC Code",
      type: "text",
      name: "bank_branch_ifsc_code",
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
          // option={citizenTypes?.["egov-hrms"]?.citizenType}
          option={citizenTypeList!=undefined && citizenTypeList}
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

export default WmsCMBankBranchIFSCCode;





