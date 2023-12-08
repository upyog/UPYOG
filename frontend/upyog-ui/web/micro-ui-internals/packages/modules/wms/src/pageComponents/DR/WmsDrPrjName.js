import React, { useState, useEffect } from "react";
import { Loader } from "@egovernments/digit-ui-react-components";
import { Dropdown, LabelFieldPair, CardLabel } from "@egovernments/digit-ui-react-components";
import { useLocation } from "react-router-dom";

const WmsDrPrjName = ({ t, config, onSelect, formData = {}, userType }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { data: Projects , isLoading } = Digit.Hooks.wms.useWmsMDMS(tenantId, "common-masters", "Project") || {};
  const [project, setproject] = useState(formData?.SelectProject);
  function SelectProject(value) {
    setproject(value);
  }

  useEffect(() => {
    onSelect(config.key, project);
  }, [project]);
  const inputs = [
    {
      label: "WMS_DR_PROJECT_NAME_LABEL",
      type: "text",
      name: "project_name",
      validation: {
        isRequired: true,
      },
      isMandatory: true,
    },
  ];

  if (isLoading) {
    return <Loader />;
  }
  else{
    console.log("JK",Projects);
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
          selected={project}
          option={Projects?.["common-master"]?.Project}
          select={SelectProject}
          optionKey="code"
          defaultValue={undefined}
          t={t}
        />
      </LabelFieldPair>
    );
  });
};

export default WmsDrPrjName;