import React, { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { TypeSelectCard } from "@egovernments/digit-ui-react-components";
import { Dropdown } from "@egovernments/digit-ui-react-components";
import { useRouteMatch, useHistory } from "react-router-dom";
import { useQueryClient } from "react-query";
import { FormComposer } from "../../../../components/FormComposer";

//import { createComplaint } from "../../../../redux/actions";
//import { useDispatch } from "react-redux";
const SelectComplaintType = ({ t, config, onSelect, value }) => {
  const serviceDefinitions = Digit.GetServiceDefinitions;
  const [complaintType, setComplaintType] = useState(() => {
    const { complaintType } = value;
    return complaintType ? complaintType : {};
  });

  const goNext = () => {
    onSelect({ complaintType });
  };

  const textParams = config.texts;

  const menu = Digit.Hooks.pgr.useComplaintTypes({ stateCode: Digit.ULBService.getCurrentTenantId() });

  function selectedValue(value) {
    setComplaintType(value);
    window.Digit.SessionStorage.set("complaintType", value);
  }
  return (
    <TypeSelectCard
      {...textParams}
      {...{ menu: menu }}
      {...{ optionsKey: "name" }}
      {...{ selected: selectedValue }}
      {...{ selectedOption: complaintType }}
      {...{ onSave: goNext }}
      {...{ t }}
      disabled={Object.keys(complaintType).length === 0 || complaintType === null ? true : false}
    />
  );
};

export default SelectComplaintType;
