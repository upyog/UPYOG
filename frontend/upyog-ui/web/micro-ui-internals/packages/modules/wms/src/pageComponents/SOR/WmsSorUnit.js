import React, { useState, useEffect } from "react";
import { Loader } from "@egovernments/digit-ui-react-components";
import { Dropdown, LabelFieldPair, CardLabel } from "@egovernments/digit-ui-react-components";
import { useLocation } from "react-router-dom";

const WmsSorUnit = ({ t, config, onSelect, formData = {}, userType }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { data: Units , isLoading } = Digit.Hooks.wms.useWmsMDMS(tenantId, "common-masters", "Unit") || {};
  const [unit, setUnit] = useState(formData?.SelectUnit);
  function SelectUnit(value) {
    setUnit(value);
  }

  useEffect(() => {
    onSelect(config.key, unit);
  }, [unit]);
  const inputs = [
    {
      label: "WMS_SOR_UNIT_LABEL",
      type: "text",
      name: "unit",
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
          key={input.name}
          className="form-field"
          selected={unit}
          option={Units?.["common-masters"]?.Unit}
          select={SelectUnit}
          optionKey="name"
          defaultValue={undefined}
          t={t}
        />
      </LabelFieldPair>
    );
  });
};

export default WmsSorUnit;