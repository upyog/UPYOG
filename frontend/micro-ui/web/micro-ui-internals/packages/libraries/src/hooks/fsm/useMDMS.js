import { MdmsService } from "../../services/elements/MDMS";
import { MdmsServiceV2 } from "../../services/elements/MDMSV2";
import { useQuery } from "react-query";

const useMDMS = (tenantId, moduleCode, type, config = {}, payload = []) => {
  const queryConfig = { staleTime: Infinity, ...config };

  const useSanitationType = () => {
    return useQuery("FSM_SANITATION_TYPE", () => MdmsServiceV2.getSanitationType(tenantId, moduleCode), queryConfig);
  };

  const usePitType = () => {
    return useQuery("FSM_PIT_TYPE", () => MdmsServiceV2.getPitType(tenantId, moduleCode, queryConfig));
  };

  const useApplicationChannel = () => {
    return useQuery("FSM_APPLICATION_NEW_APPLICATION_CHANNEL", () => MdmsServiceV2.getApplicationChannel(tenantId, moduleCode, type), queryConfig);
  };

  const useEmployeeApplicationChannel = () => {
    async function onlyEmployeeChannels() {
      const allApplicationChannels = await MdmsServiceV2.getApplicationChannel(tenantId, moduleCode, type);
      return allApplicationChannels.filter((type) => !type.citizenOnly);
    }
    return useQuery("FSM_APPLICATION_EDIT_APPLICATION_CHANNEL", () => onlyEmployeeChannels(), queryConfig);
  };

  const useUrcConfig = () => {
    return useQuery("FSM_APPLICATION_NEW_URC_CONFIG", () => MdmsService.getUrcConfig(tenantId, moduleCode, type), queryConfig);
  };

  const usePropertyType = () => {
    return useQuery("FSM_PROPERTY_TYPE", () => MdmsServiceV2.getPropertyType(tenantId, moduleCode, type), queryConfig);
  };

  const usePropertySubType = () => {
    return useQuery("FSM_PROPERTY_SUBTYPE", () => MdmsServiceV2.getPropertyType(tenantId, moduleCode, type), queryConfig);
  };

  const useChecklist = () => {
    return useQuery("FSM_CHECKLIST", () => MdmsServiceV2.getChecklist(tenantId, moduleCode), queryConfig);
  };

  const useVehicleType = () => {
    return useQuery("FSM_VEHICLE_TYPE", () => MdmsServiceV2.getVehicleType(tenantId, moduleCode, type), queryConfig);
  };

  const useSlumLocality = () => {
    return useQuery(
      ["SLUM_LOCALITY_MAPPING", tenantId, moduleCode],
      () => MdmsServiceV2.getSlumLocalityMapping(tenantId, moduleCode, type),
      queryConfig
    );
  };

  const useReason = () => {
    return useQuery("CANCELLATION_REASON", () => MdmsServiceV2.getReason(tenantId, moduleCode, type, payload), queryConfig);
  };

  const useRoleStatusMapping = () => {
    return useQuery("ROLE_STATUS_MAPPING", () => MdmsServiceV2.getRoleStatus(tenantId, moduleCode, type));
  };
  const useCommonFieldsConfig = () => {
    return useQuery("COMMON_FIELDS", () => MdmsService.getCommonFieldsConfig(tenantId, moduleCode, type, payload));
  };

  const usePreFieldsConfig = () => {
    return useQuery("PRE_FIELDS", () => MdmsService.getPreFieldsConfig(tenantId, moduleCode, type, payload));
  };

  const usePostFieldsConfig = () => {
    return useQuery("POST_FIELDS", () => MdmsService.getPostFieldsConfig(tenantId, moduleCode, type, payload));
  };

  const useGenderDetails = () => {
    return useQuery("FSM_GENDER_DETAILS", () => MdmsServiceV2.getFSMGenderType(tenantId, moduleCode, type), config);
  };

  const useFSTPORejectionReason = () => {
    return useQuery("FSM_FSTPO_REJECTION", () => MdmsServiceV2.getFSTPORejectionReason(tenantId, moduleCode, type), queryConfig);
  };

  const usePaymentType = () => {
    return useQuery("FSM_PAYMENT_TYPE", () => MdmsServiceV2.getFSMPaymentType(tenantId, moduleCode, type), queryConfig);
  };

  const useTripNumber = () => {
    return useQuery("FSM_TRIP_NUMBER", () => MdmsServiceV2.getFSMTripNumber(tenantId, moduleCode, type), queryConfig);
  };

  const useReceivedPaymentType = () => {
    return useQuery("FSM_RECEIVED_PAYMENT_TYPE", () => MdmsServiceV2.getFSMReceivedPaymentType(tenantId, moduleCode, type), queryConfig);
  };

  const useWSTaxHeadMaster = () => {
    return useQuery("FSM_RECEIVED_PAYMENT_TYPE", () => MdmsServiceV2.getWSTaxHeadMaster(tenantId, moduleCode, type), queryConfig);
  };

  switch (type) {
    case "SanitationType":
      return useSanitationType();

    case "ApplicationChannel":
      return useApplicationChannel();

    case "EmployeeApplicationChannel":
      return useEmployeeApplicationChannel();

    case "PropertyType":
      return usePropertyType();

    case "PropertySubtype":
      return usePropertySubType();

    case "PitType":
      return usePitType();

    case "VehicleType":
      return useVehicleType();

    case "VehicleMakeModel":
      return useVehicleType();

    case "Checklist":
      return useChecklist();

    case "Slum":
      return useSlumLocality();

    case "Reason":
      return useReason();

    case "RoleStatusMapping":
      return useRoleStatusMapping();

    case "CommonFieldsConfig":
      return useCommonFieldsConfig();
    case "PreFieldsConfig":
      return usePreFieldsConfig();
    case "PostFieldsConfig":
      return usePostFieldsConfig();
    case "FSMGenderType":
      return useGenderDetails();
    case "FSTPORejectionReason":
      return useFSTPORejectionReason();
    case "PaymentType":
      return usePaymentType();
    case "TripNumber":
      return useTripNumber();
    case "ReceivedPaymentType":
      return useReceivedPaymentType();
    case "WSTaxHeadMaster":
      return useWSTaxHeadMaster();
    case "UrcConfig":
      return useUrcConfig();
    default:
      return null;
  }
};

export default useMDMS;
