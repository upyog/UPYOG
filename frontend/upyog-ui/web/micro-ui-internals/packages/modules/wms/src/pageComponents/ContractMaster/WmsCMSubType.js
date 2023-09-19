import React, { useState, useEffect } from "react";
import { CardLabelError, Loader } from "@egovernments/digit-ui-react-components";
import { Dropdown, LabelFieldPair, CardLabel } from "@egovernments/digit-ui-react-components";
import { useLocation } from "react-router-dom";

const WmsCMSubType = ({ t, config, onSelect, formData = {}, userType }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { pathname: url } = useLocation();
  const editScreen = url.includes("/modify-application/");
  const { data: citizenTypes = [], isLoading } = Digit.Hooks.hrms.useHrmsMDMS(tenantId, "egov-hrms", "EmployeeType") || {};
  const [citizenType, setcitizenType] = useState(formData?.WmsCMSubType);
  const [isTrue, setisTrue] = useState(false);
  function SelectcitizenType(value) {
  if(!value?.name){setisTrue(true)}else{setisTrue(false);setcitizenType(value);}
  }

  useEffect(() => {
    onSelect(config.key, citizenType);
  }, [citizenType]);

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
          {input.isMandatory ? " * " : null}
        </CardLabel>
        <Dropdown
          className="form-field"
          selected={citizenType}
          // option={citizenTypes?.["egov-hrms"]?.EmployeeType}
          option={option}
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





