import React from "react";
import { FormStep } from "@nudmcdgnpm/digit-ui-react-components";

const SelectName = ({ config, onSelect, onSkip, t }) => {
  return <FormStep config={config} onSelect={onSelect} t={t}></FormStep>;
};

export default SelectName;
