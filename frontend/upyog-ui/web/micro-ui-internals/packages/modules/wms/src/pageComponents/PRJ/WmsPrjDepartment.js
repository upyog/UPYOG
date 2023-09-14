import React, { useState, useEffect } from "react";
import { Loader } from "@egovernments/digit-ui-react-components";
import { Dropdown, LabelFieldPair, CardLabel } from "@egovernments/digit-ui-react-components";
import { useLocation } from "react-router-dom";

const WmsPrjDepartment = ({ t, config, onSelect, formData = {}, userType }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { pathname: url } = useLocation();
  const editScreen = url.includes("/modify-application/");
  const { data: Departments = [{Department:"Dept1"},{Department:"Dept2"}], isLoading } = Digit.Hooks.wms.useWmsMDMS(tenantId, "common-masters", "Department") || {};
  const [department, setdepartment] = useState(formData?.WmsPrjDepartment);
  function WmsPrjDepartment(value) {
    setdepartment(value);
  }

  useEffect(() => {
   // alert(Departments)
    onSelect(config.key, department);
  }, [department]);
  const inputs = [
    {
      label: "WMS_PRJ_DEPARTMENT_LABEL",
      type: "text",
      name: "department",
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
          selected={department}
          option={[{code:"Department1",Department:"Dept1"},{code:"Department2",Department:"Dept2"}]}
          select={WmsPrjDepartment}
          optionKey="code"
          defaultValue={undefined}
          t={t}
        />
      </LabelFieldPair>
    );
  });
};

export default WmsPrjDepartment;
