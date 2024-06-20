import React, { useState, useEffect } from "react";
import { Loader } from "@egovernments/digit-ui-react-components";
import { Dropdown, LabelFieldPair, CardLabel } from "@egovernments/digit-ui-react-components";
import { useLocation } from "react-router-dom";

const WmsSchSourceOfFund = ({ t, config, onSelect, formData = {}, userType }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { pathname: url } = useLocation();
  const editScreen = url.includes("/modify-application/");
  const { data: SourceOfFunds = [{SourceOfFund:"SourceOfFund1"},{SourceOfFund:"SourceOfFund2"}], isLoading } = Digit.Hooks.wms.useWmsMDMS(tenantId, "wms", "SourceOfFund") || {};
  const [sourceOfFund, setSourceOfFund] = useState(formData?.WmsSchSourceOfFund);
  function WmsSchSourceOfFund(value) {
    setSourceOfFund(value);
  }

  useEffect(() => {
   // alert(SourceOfFunds)
    onSelect(config.key, sourceOfFund);
  }, [sourceOfFund]);
  const inputs = [
    {
      label: "WMS_SCH_SOURCE_OF_FUND_LABEL",
      type: "text",
      name: "source_of_fund",
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
          selected={sourceOfFund}
          option={[{code:"SourceOfFund1",SourceOfFund:"SourceOfFund1"},{code:"SourceOfFund2",SourceOfFund:"SourceOfFund2"}]}
          select={WmsSchSourceOfFund}
          optionKey="code"
          defaultValue={undefined}
          t={t}
        />
      </LabelFieldPair>
    );
  });
};

export default WmsSchSourceOfFund;
