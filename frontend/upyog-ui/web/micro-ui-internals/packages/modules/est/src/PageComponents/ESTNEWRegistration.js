import React from "react";
import { DynamicFormStep } from "@nudmcdgnpm/digit-ui-react-components";
import estateFormConfig from "../config/estateFormConfig";

const NewRegistration = ({
  onSelect,
  config,
  persistedData,
  isEditMode,
  editData,
  t,
}) => (
  <DynamicFormStep
    config={config}
    localOverrides={estateFormConfig}
    onSelect={onSelect}
    persistedData={persistedData}
    isEditMode={isEditMode}
    editData={editData}
    t={t}
    defaultHeaderCode="EST_COMMON_NEW_REGISTRATION"
  />
);

export default NewRegistration;
