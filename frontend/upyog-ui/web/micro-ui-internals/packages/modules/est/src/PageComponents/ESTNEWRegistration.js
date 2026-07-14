import React, { useCallback } from "react";
import { DynamicFormStep } from "@nudmcdgnpm/digit-ui-react-components";
import estateFormConfig from "../config/estateFormConfig";
import { findAssetsByApplicationPrefix } from "../config/Create/assetReference";
import { mapAssetReferenceToPrefill } from "../utils";

const NewRegistration = ({
  onSelect,
  config,
  persistedData,
  isEditMode,
  editData,
  t,
}) => {
  const handleFieldSearch = useCallback(async (fieldName, formData) => {
    if (fieldName !== "searchEstateNo") return null;

    const applicationNo = String(formData?.searchEstateNo || "").trim();
    if (!applicationNo) return { error: "EST_ASSET_NUMBER_REQUIRED" };

    const assets = findAssetsByApplicationPrefix(applicationNo);
    if (!assets.length) {
      return { notFound: true, estateNo: applicationNo };
    }

    return {
      matches: assets.map((asset) => ({
        estateNo: asset.applicationNo,
        label: asset.applicationNo,
        subtitle: asset.assetName || asset.description || "",
        prefill: mapAssetReferenceToPrefill(asset),
      })),
    };
  }, []);

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
      onFieldSearch={handleFieldSearch}
    />
  );
};

export default NewRegistration;
