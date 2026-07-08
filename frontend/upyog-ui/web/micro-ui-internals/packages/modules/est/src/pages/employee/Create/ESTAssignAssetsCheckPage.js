import React from "react";
import  {DynamicCheckPage}  from "@nudmcdgnpm/digit-ui-react-components"; // adjust if you export it differently
import { useTranslation } from "react-i18next";
import { checkForNA, ESTDocumnetPreview, formatEpochDate } from "../../../utils";

// The same config sources ESTAssignAssets merges at runtime:
import { Config } from "../../../config/Create/AssignAssetConfig";                   
//import estateAllotmentFormConfig from "../../../config/Create/AssignAssetConfig"; 
import estateAllotmentFormConfig from "../../../config/Create/estateAllotmentFormConfig"; // ← its own file

/* =========================================================
   ESTAssignAssetsCheckPage
   ---------------------------------------------------------
   Thin wrapper: all rendering is driven by the SAME config
   DynamicForm uses (Config body entry + estateAllotmentFormConfig),
   so adding/removing a field in the form config automatically
   updates this summary page too.
   ========================================================= */

// Same merge ESTAssignAssets.js does: estateAllotmentFormConfig's `form` wins.
const baseRoute = Config[0].body.find((b) => b.key === "Allotments");
const routeConfig = { ...baseRoute, ...estateAllotmentFormConfig };

const ESTAssignAssetsCheckPage = ({ onSubmit, value = {} }) => {
  const { t } = useTranslation();

  // Display-only asset fields (excludeFromPayload) live in router state
  // under value.assetData with different key names — map them once here
  // to the field `name`s declared in the form config.
  const assetData = value?.assetData || {};
  const extraData = {
    assetNo: assetData.estateNo,
    assetRefNumber: assetData.assetRefNumber,
    buildingName: assetData.buildingName,
    localityDisplay: assetData.locality,
    totalFloorArea: assetData.totalFloorArea,
    buildingFloor: assetData.buildingFloor || assetData.floor,
    assetRate: assetData.rate,
  };

  const formatAnyDate = (v) => {
  if (!v) return "N/A";
  if (typeof v === "number" || /^\d+$/.test(String(v))) return formatEpochDate(Number(v));
  const d = new Date(v);
  return isNaN(d.getTime()) ? "N/A" : d.toLocaleDateString("en-IN"); // dd/mm/yyyy
};

  return (
    <DynamicCheckPage
      routeConfig={routeConfig}
      config={{ key: "Allotments" }}
      value={value}
      extraData={extraData}
      editRoute="/upyog-ui/employee/est/assignassets/assign-assets"
      onSubmit={onSubmit}
      summaryHeaderCode="EST_ASSIGN_ASSETS_SUMMARY"
      defaultSectionHeaderCode="EST_ASSET_DETAILS"
      t={t}
      formatDate={formatAnyDate}
      checkNA={checkForNA}
      DocumentPreview={ESTDocumnetPreview}
    />
  );
};

export default ESTAssignAssetsCheckPage;
