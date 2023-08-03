import React from "react";
import { FormStep } from "@egovernments/digit-ui-react-components";

const WmsDescriptionOfItem = ({ config, onSelect, onSkip, t }) => {
  return <FormStep config={config} onSelect={onSelect} t={t}></FormStep>;
};

export default WmsDescriptionOfItem;
