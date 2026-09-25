import { MdmsService } from "../../services/elements/MDMS";
import { queryTemplate } from "../../common/queryTemplate";

const useMDMS = (tenantId, moduleCode, type, config = {}, payload = []) => {
  const queryConfig = { staleTime: Infinity, ...config };

  const useSanitationType = () =>
    queryTemplate({
      queryKey: ["FSM_SANITATION_TYPE"],
      queryFn: () => MdmsService.getSanitationType(tenantId, moduleCode),
      config: queryConfig,
    });

  const usePitType = () =>
    queryTemplate({
      queryKey: ["FSM_PIT_TYPE"],
      queryFn: () => MdmsService.getPitType(tenantId, moduleCode),
      config: queryConfig,
    });

  const useApplicationChannel = () =>
    queryTemplate({
      queryKey: ["FSM_APPLICATION_NEW_APPLICATION_CHANNEL"],
      queryFn: () => MdmsService.getApplicationChannel(tenantId, moduleCode, type),
      config: queryConfig,
    });

  const useEmployeeApplicationChannel = () => {
    async function onlyEmployeeChannels() {
      const allApplicationChannels = await MdmsService.getApplicationChannel(tenantId, moduleCode, type);
      return allApplicationChannels.filter((t) => !t.citizenOnly);
    }
    return queryTemplate({
      queryKey: ["FSM_APPLICATION_EDIT_APPLICATION_CHANNEL"],
      queryFn: () => onlyEmployeeChannels(),
      config: queryConfig,
    });
  };

  const useUrcConfig = () =>
    queryTemplate({
      queryKey: ["FSM_APPLICATION_NEW_URC_CONFIG"],
      queryFn: () => MdmsService.getUrcConfig(tenantId, moduleCode, type),
      config: queryConfig,
    });

  const usePropertyType = () =>
    queryTemplate({
      queryKey: ["FSM_PROPERTY_TYPE"],
      queryFn: () => MdmsService.getPropertyType(tenantId, moduleCode, type),
      config: queryConfig,
    });

  const usePropertySubType = () =>
    queryTemplate({
      queryKey: ["FSM_PROPERTY_SUBTYPE"],
      queryFn: () => MdmsService.getPropertyType(tenantId, moduleCode, type),
      config: queryConfig,
    });

  const useChecklist = () =>
    queryTemplate({
      queryKey: ["FSM_CHECKLIST"],
      queryFn: () => MdmsService.getChecklist(tenantId, moduleCode),
      config: queryConfig,
    });

  const useVehicleType = () =>
    queryTemplate({
      queryKey: ["FSM_VEHICLE_TYPE"],
      queryFn: () => MdmsService.getVehicleType(tenantId, moduleCode, type),
      config: queryConfig,
    });

  const useSlumLocality = () =>
    queryTemplate({
      queryKey: ["SLUM_LOCALITY_MAPPING", tenantId, moduleCode],
      queryFn: () => MdmsService.getSlumLocalityMapping(tenantId, moduleCode, type),
      config: queryConfig,
    });

  const useReason = () =>
    queryTemplate({
      queryKey: ["CANCELLATION_REASON"],
      queryFn: () => MdmsService.getReason(tenantId, moduleCode, type, payload),
      config: queryConfig,
    });

  const useRoleStatusMapping = () =>
    queryTemplate({
      queryKey: ["ROLE_STATUS_MAPPING"],
      queryFn: () => MdmsService.getRoleStatus(tenantId, moduleCode, type),
    });

  const useCommonFieldsConfig = () =>
    queryTemplate({
      queryKey: ["COMMON_FIELDS"],
      queryFn: () => MdmsService.getCommonFieldsConfig(tenantId, moduleCode, type, payload),
    });

  const usePreFieldsConfig = () =>
    queryTemplate({
      queryKey: ["PRE_FIELDS"],
      queryFn: () => MdmsService.getPreFieldsConfig(tenantId, moduleCode, type, payload),
    });

  const usePostFieldsConfig = () =>
    queryTemplate({
      queryKey: ["POST_FIELDS"],
      queryFn: () => MdmsService.getPostFieldsConfig(tenantId, moduleCode, type, payload),
    });

  const useGenderDetails = () =>
    queryTemplate({
      queryKey: ["FSM_GENDER_DETAILS"],
      queryFn: () => MdmsService.getFSMGenderType(tenantId, moduleCode, type),
      config,  // intentionally uses raw config, not queryConfig (matches original)
    });

  const useFSTPORejectionReason = () =>
    queryTemplate({
      queryKey: ["FSM_FSTPO_REJECTION"],
      queryFn: () => MdmsService.getFSTPORejectionReason(tenantId, moduleCode, type),
      config: queryConfig,
    });

  const usePaymentType = () =>
    queryTemplate({
      queryKey: ["FSM_PAYMENT_TYPE"],
      queryFn: () => MdmsService.getFSMPaymentType(tenantId, moduleCode, type),
      config: queryConfig,
    });

  const useTripNumber = () =>
    queryTemplate({
      queryKey: ["FSM_TRIP_NUMBER"],
      queryFn: () => MdmsService.getFSMTripNumber(tenantId, moduleCode, type),
      config: queryConfig,
    });

  const useReceivedPaymentType = () =>
    queryTemplate({
      queryKey: ["FSM_RECEIVED_PAYMENT_TYPE"],
      queryFn: () => MdmsService.getFSMReceivedPaymentType(tenantId, moduleCode, type),
      config: queryConfig,
    });

  const useWSTaxHeadMaster = () =>
    queryTemplate({
      queryKey: ["FSM_WS_TAX_HEAD_MASTER"], // fixed: was duplicate "FSM_RECEIVED_PAYMENT_TYPE"
      queryFn: () => MdmsService.getWSTaxHeadMaster(tenantId, moduleCode, type),
      config: queryConfig,
    });

  switch (type) {
    case "SanitationType":         return useSanitationType();
    case "ApplicationChannel":     return useApplicationChannel();
    case "EmployeeApplicationChannel": return useEmployeeApplicationChannel();
    case "PropertyType":           return usePropertyType();
    case "PropertySubtype":        return usePropertySubType();
    case "PitType":                return usePitType();
    case "VehicleType":            return useVehicleType();
    case "VehicleMakeModel":       return useVehicleType();
    case "Checklist":              return useChecklist();
    case "Slum":                   return useSlumLocality();
    case "Reason":                 return useReason();
    case "RoleStatusMapping":      return useRoleStatusMapping();
    case "CommonFieldsConfig":     return useCommonFieldsConfig();
    case "PreFieldsConfig":        return usePreFieldsConfig();
    case "PostFieldsConfig":       return usePostFieldsConfig();
    case "FSMGenderType":          return useGenderDetails();
    case "FSTPORejectionReason":   return useFSTPORejectionReason();
    case "PaymentType":            return usePaymentType();
    case "TripNumber":             return useTripNumber();
    case "ReceivedPaymentType":    return useReceivedPaymentType();
    case "WSTaxHeadMaster":        return useWSTaxHeadMaster();
    case "UrcConfig":              return useUrcConfig();
    default:                       return null;
  }
};

export default useMDMS;