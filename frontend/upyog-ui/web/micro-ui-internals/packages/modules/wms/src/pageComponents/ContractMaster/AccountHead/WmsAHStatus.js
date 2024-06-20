import React, { useState, useEffect } from "react";
import { CardLabelError, Loader } from "@egovernments/digit-ui-react-components";
import { Dropdown, LabelFieldPair, CardLabel } from "@egovernments/digit-ui-react-components";
import { useLocation } from "react-router-dom";

const WmsAHStatus = ({ t, config, onSelect, formData = {}, userType }) => {
  console.log({config, onSelect, formData})
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { pathname: url } = useLocation();
  const editScreen = url.includes("/modify-application/");
  const [isTrue, setisTrue] = useState(false);

  const vendorStatus = [
    {
        "code": "CONTRACT_MASTER_ACTIVE",
        "name": "Active",
        "module": "rainmaker-tl",
        "locale": "en_IN"
    },
    {
        "code": "CONTRACT_MASTER_INACTIVE",
        "name": "Inactive",
        "module": "rainmaker-tl",
        "locale": "en_IN"
    }
];
  // const { data: EmployeeTypes = [], isLoading } = Digit.Hooks.hrms.useHrmsMDMS(tenantId, "egov-hrms", "EmployeeType") || {};
  const [ venderStatusTypes,setVenderStatusTypes ] = useState(vendorStatus)
  const [ isLoading,setisLoading ] = useState(false)
  const [venderStatusType, setvenderStatusType] = useState(formData?.WmsAHStatus);
  function SelectvenderStatusType(value) {
  if(!value?.name){setisTrue(true)}else{setisTrue(false);setvenderStatusType(value);}
  }

  useEffect(() => {
    onSelect(config.key, venderStatusType);
  }, [venderStatusType]);

  
  const inputs = [
    {
      label: "Status",
      type: "text",
      name: "status",
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
          selected={venderStatusType}
          // option={EmployeeTypes?.["egov-hrms"]?.EmployeeType}
          option={venderStatusTypes}
          select={SelectvenderStatusType}
          onBlur={SelectvenderStatusType}
          optionKey="name"
          defaultValue={undefined}
          t={t}
        />
            {isTrue&&<CardLabelError style={{ width: "100%", marginTop: '-15px', fontSize: '16px', marginBottom: '12px'}}>{t("Require Field")}</CardLabelError>}

      </LabelFieldPair>
    );
  });
};

export default WmsAHStatus;





