import { MdmsService } from "../../services/elements/MDMS";
import { MdmsServiceV2 } from "../../services/elements/MDMSV2";
import { queryTemplate } from "../../common/queryTemplate";

const usePropertyMDMS = (tenantId, moduleCode, type, config = {}) => {
  const usePropertyOwnerType = () => {
    return queryTemplate({ queryKey: ["PT_OWNERSHIP_CATEGORY"], queryFn: () => MdmsServiceV2.getPropertyOwnerType(tenantId, moduleCode, type), config });
  };
  const usePropertyOwnerShipCategory = () => {
    return queryTemplate({ queryKey: ["PT_OWNER_TYPE"], queryFn: () => MdmsServiceV2.getPropertyOwnerShipCategory(tenantId, moduleCode, type), config });
  };
  const useSubOwnerShipCategory = () => {
    return queryTemplate({ queryKey: ["PT_SUB_OWNERSHIP_CATEGORY"], queryFn: () => MdmsServiceV2.getPropertySubOwnerShipCategory(tenantId, moduleCode, type), config });
  };
  const useDocumentRequiredScreen = () => {
    return queryTemplate({ queryKey: ["PT_DOCUMENT_REQ_SCREEN"], queryFn: () => MdmsServiceV2.getDocumentRequiredScreen(tenantId, moduleCode), config });
  };
  const useUsageCategory = () => {
    return queryTemplate({ queryKey: ["PT_USAGE_CATEGORY"], queryFn: () => MdmsServiceV2.getUsageCategory(tenantId, moduleCode, type), config });
  };
  const usePTPropertyType = () => {
    return queryTemplate({ queryKey: ["PT_PROPERTY_TYPE"], queryFn: () => MdmsServiceV2.getPTPropertyType(tenantId, moduleCode, type), config });
  };
  const useRentalDetails = () => {
    return queryTemplate({ queryKey: ["PT_RENTAL_DETAILS"], queryFn: () => MdmsServiceV2.getRentalDetails(tenantId, moduleCode), config });
  };
  const useChargeSlabs = () => {
    return queryTemplate({ queryKey: ["PT_RENTAL_DETAILS"], queryFn: () => MdmsServiceV2.getChargeSlabs(tenantId, moduleCode), config });
  };
  const useFloorList = () => {
    return queryTemplate({ queryKey: ["PT_FLOOR_LIST"], queryFn: () => MdmsServiceV2.getFloorList(tenantId, moduleCode), config });
  };
  const useMapConfig = () => {
    return queryTemplate({ queryKey: ["PT_MAP_CONFIG"], queryFn: () => MdmsServiceV2.getMapConfig(tenantId, moduleCode), config });
  };

  const _default = () => {
    return queryTemplate({ queryKey: [tenantId, moduleCode, type], queryFn: () => MdmsServiceV2.getMultipleTypes(tenantId, moduleCode, type), config });
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

export default usePropertyMDMS;
