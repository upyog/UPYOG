import React, { useMemo } from "react";
import ESTDynamicFormStep from "./ESTDynamicFormStep";
import estateFormConfig from "../config/estateFormConfig";

const NewRegistration = ({ onSelect, config, persistedData, isEditMode, editData }) => (
  <ESTDynamicFormStep
    config={config}
    localOverrides={estateFormConfig}
    onSelect={onSelect}
    persistedData={persistedData}
    isEditMode={isEditMode}
    editData={editData}
  />
);

export default NewRegistration;
