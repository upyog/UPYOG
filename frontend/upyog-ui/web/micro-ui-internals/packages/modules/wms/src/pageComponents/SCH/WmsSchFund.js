import React, { useState, useEffect } from "react";
import { Loader } from "@egovernments/digit-ui-react-components";
import { Dropdown, LabelFieldPair, CardLabel } from "@egovernments/digit-ui-react-components";
import { useLocation } from "react-router-dom";

const WmsSchFund = ({ t, config, onSelect, formData = {}, userType }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { pathname: url } = useLocation();
  const editScreen = url.includes("/modify-application/");
  const { data: Funds = [{Fund:"Fund1"},{Fund:"Fund2"}], isLoading } = Digit.Hooks.wms.useWmsMDMS(tenantId, "common-masters", "Fund") || {};
  const [fund, setfund] = useState(formData?.WmsSchFund);
  function WmsSchFund(value) {
    setfund(value);
  }

  useEffect(() => {
   // alert(Funds)
    onSelect(config.key, fund);
  }, [fund]);
  const inputs = [
    {
      label: "WMS_SCH_FUND_LABEL",
      type: "text",
      name: "fund",
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
          selected={fund}
          option={EmployeeTypes?.["egov-hrms"]?.EmployeeType[{code:"Fund1",Fund:"Fund1"},{code:"Fund2",Fund:"Fund2"}]}
          select={WmsSchFund}
          optionKey="code"
          defaultValue={undefined}
          t={t}
        />
      </LabelFieldPair>
    );
  });
};

export default WmsSchFund;
