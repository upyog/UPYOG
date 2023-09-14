import React, { useState, useEffect } from "react";
import { Loader } from "@egovernments/digit-ui-react-components";
import { Dropdown, LabelFieldPair, CardLabel } from "@egovernments/digit-ui-react-components";
import { useLocation } from "react-router-dom";

const WmsPrjStatus = ({ t, config, onSelect, formData = {}, userType }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { pathname: url } = useLocation();
  const editScreen = url.includes("/modify-application/");
  const { data: Statuss = [{Status:"Status1"},{Status:"Status2"}], isLoading } = Digit.Hooks.wms.useWmsMDMS(tenantId, "wms", "Status") || {};
  const [status, setstatus] = useState(formData?.WmsPrjStatus);
  function WmsPrjStatus(value) {
    setstatus(value);
  }

  useEffect(() => {
   // alert(Statuss)
    onSelect(config.key, status);
  }, [status]);
  const inputs = [
    {
      label: "WMS_PRJ_STATUS_LABEL",
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
          {input.isMandatory ? " * " : null}
        </CardLabel>
        <Dropdown
          className="form-field"
          selected={status}
          option={[{code:"Active",Status:"Status1"},{code:"Inactive",Status:"Status2"}]}
          select={WmsPrjStatus}
          optionKey="code"
          defaultValue={undefined}
          t={t}
        />
      </LabelFieldPair>
    );
  });
};

export default WmsPrjStatus;
