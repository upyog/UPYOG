import React, { useCallback } from "react";
import { DynamicFormStep } from "@nudmcdgnpm/digit-ui-react-components";
import estateFormConfig from "../config/estateFormConfig";
import layoutStyles from "../styles/estEmployeeLayout.module.scss";
import {
  mapAssetSearchToRegistrationMatch,
  searchExistingEstateAssets,
} from "../utils";

const NewRegistration = ({
  onSelect,
  config,
  persistedData,
  isEditMode,
  editData,
  t,
}) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const handleFieldSearch = useCallback(
    async (fieldName, formData) => {
      if (fieldName !== "searchEstateNo") return null;

      const assetNumber = String(formData?.searchEstateNo || "").trim();
      if (!assetNumber) return { error: "EST_ASSET_NUMBER_REQUIRED" };

      try {
        const assets = await searchExistingEstateAssets(assetNumber, tenantId);
        if (!assets.length) {
          return { notFound: true, estateNo: assetNumber };
        }

        if (assets.length === 1) {
          const match = mapAssetSearchToRegistrationMatch(assets[0]);
          return {
            found: true,
            estateNo: match.estateNo || assetNumber,
            prefill: match.prefill,
          };
        }

        return {
          matches: assets.map(mapAssetSearchToRegistrationMatch),
        };
      } catch (err) {
        console.error("Existing asset search failed:", err);
        return { error: "CS_SOMETHING_WENT_WRONG" };
      }
    },
    [tenantId]
  );

  return (
    <DynamicFormStep
      config={config}
      localOverrides={estateFormConfig}
      onSelect={onSelect}
      persistedData={persistedData}
      isEditMode={isEditMode}
      editData={editData}
      t={t}
      defaultHeaderCode="EST_COMMON_NEW_REGISTRATION"
      wrapperClassName={`employeeCard ${layoutStyles.estFormStep}`}
      onFieldSearch={handleFieldSearch}
      confirmCancel
    />
  );
};

export default NewRegistration;
