import React from "react";
import { FormStep } from "@nudmcdgnpm/digit-ui-react-components";

const SelectName = ({ config, onSelect, t, isDisabled }) => {
  return <FormStep config={config} onSelect={onSelect} t={t} isDisabled={isDisabled}></FormStep>;
};

export default SelectName;
