import { MdmsServiceV2 } from "../../services/elements/MDMSV2";
import { useQuery } from "react-query";

const usePropertyMDMSV2 = (tenantId, moduleCode, type, config = {}) => {
  const usePropertyOwnerType = () => {
    return useQuery("PT_OWNERSHIP_CATEGORY", () => MdmsServiceV2.getPropertyOwnerType(tenantId, moduleCode, type), config);
  };
  const usePropertyOwnerShipCategory = () => {
    return useQuery("PT_OWNER_TYPE", () => MdmsServiceV2.getPropertyOwnerShipCategory(tenantId, moduleCode, type), config);
  };
  const useSubOwnerShipCategory = () => {
    return useQuery("PT_SUB_OWNERSHIP_CATEGORY", () => MdmsServiceV2.getPropertySubOwnerShipCategory(tenantId, moduleCode, type), config);
  };
  const useDocumentRequiredScreen = () => {
    return useQuery("PT_DOCUMENT_REQ_SCREEN", () => MdmsServiceV2.getDocumentRequiredScreen(tenantId, moduleCode), config);
  };
  const useUsageCategory = () => {
    return useQuery("PT_USAGE_CATEGORY", () => MdmsServiceV2.getUsageCategory(tenantId, moduleCode, type), config);
  };
  const usePTPropertyType = () => {
    return useQuery("PT_PROPERTY_TYPE", () => MdmsServiceV2.getPTPropertyType(tenantId, moduleCode, type), config);
  };
  const useRentalDetails = () => {
    return useQuery("PT_RENTAL_DETAILS", () => MdmsServiceV2.getRentalDetails(tenantId, moduleCode), config);
  };
  const useChargeSlabs = () => {
    return useQuery("PT_RENTAL_DETAILS", () => MdmsServiceV2.getChargeSlabs(tenantId, moduleCode), config);
  };
  const useFloorList = () => {
    return useQuery("PT_FLOOR_LIST", () => MdmsServiceV2.getFloorList(tenantId, moduleCode), config);
  };
  const useMapConfig = () => {
    return useQuery("PT_MAP_CONFIG", () => MdmsServiceV2.getMapConfig(tenantId, moduleCode), config);
  };

  const _default = () => {
    return useQuery([tenantId, moduleCode, type], () => MdmsServiceV2.getMultipleTypes(tenantId, moduleCode, type), config);
  };

  switch (type) {
    case "OwnerShipCategory":
      return usePropertyOwnerShipCategory();
    case "OwnerType":
      return usePropertyOwnerType();
    case "SubOwnerShipCategory":
      return useSubOwnerShipCategory();
    case "Documents":
      return useDocumentRequiredScreen();
    case "UsageCategory":
      return useUsageCategory();
    case "PTPropertyType":
      return usePTPropertyType();
    case "RentalDetails":
      return useRentalDetails();
    case "Floor":
      return useFloorList();
    case "MapConfig":
      return useMapConfig();
    case "ChargeSlabs":
      return useChargeSlabs();
    default:
      return _default();
  }
};

export default usePropertyMDMSV2;
