import React, { useState, useEffect } from "react";
import { Loader } from "@egovernments/digit-ui-react-components";
import { Dropdown, LabelFieldPair, CardLabel } from "@egovernments/digit-ui-react-components";
import { useLocation } from "react-router-dom";

const WmsSorChapter = ({ t, config, onSelect, formData = {}, userType }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { pathname: url } = useLocation();
  const editScreen = url.includes("/sor-edit/");
  const { data: Chapters , isLoading } = Digit.Hooks.wms.useWmsMDMS(tenantId, "common-masters", "Chapter") || {};
  const [chapter, setchapter] = useState(formData?.SelectChapter);
  function SelectChapter(value) {
    setchapter(value);
  }

  useEffect(() => {
    onSelect(config.key, chapter);
  }, [chapter]);
  const inputs = [
    {
      label: "WMS_SOR_CHAPTER_LABEL",
      type: "text",
      name: "chapter",
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
          selected={chapter}
          option={Chapters?.["common-master"]?.Chapter}
          select={SelectChapter}
          optionKey="code"
          defaultValue={undefined}
          t={t}
        />
      </LabelFieldPair>
    );
  });
};

export default WmsSorChapter;