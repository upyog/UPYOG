import { FSMService } from "../../elements/FSM";

const getResponse = (data, vendorDetails) => {
  let details = [
    {
      title: "ES_FSM_REGISTRY_DETAILS_VEHICLE_DETAILS",
      values: [
        { title: "ES_FSM_REGISTRY_VEHICLE_NUMBER", value: data?.registrationNumber },
        { title: "ES_FSM_REGISTRY_VEHICLE_TYPE", value: `COMMON_MASTER_VEHICLE_${data?.type}` },
        { title: "ES_FSM_REGISTRY_VEHICLE_MODEL", value: data?.model },
        { title: "ES_FSM_REGISTRY_VEHICLE_CAPACITY", value: data?.tankCapacity },
        {
          title: "ES_FSM_REGISTRY_VEHICLE_POLLUTION_CERT",
          value: data?.pollutionCertiValidTill > 0 ? Digit.DateUtils.ConvertEpochToDate(data?.pollutionCertiValidTill) : "N/A",
        },
        {
          title: "ES_FSM_REGISTRY_VEHICLE_ROAD_TAX",
          value: data?.roadTaxPaidTill > 0 ? Digit.DateUtils.ConvertEpochToDate(data?.roadTaxPaidTill) : "N/A",
        },
        {
          title: "ES_FSM_REGISTRY_VEHICLE_INSURANCE",
          value: data?.InsuranceCertValidTill > 0 ? Digit.DateUtils.ConvertEpochToDate(data?.InsuranceCertValidTill) : "N/A",
        },
        {
          title: "ES_FSM_REGISTRY_VEHICLE_FITNESS",
          value: data?.fitnessValidTill > 0 ? Digit.DateUtils.ConvertEpochToDate(data?.fitnessValidTill) : "N/A",
        },
        { title: "ES_FSM_REGISTRY_VEHICLE_STATUS", value: data.status },
        { title: "ES_FSM_REGISTRY_VEHICLE_ADDITIONAL_DETAILS", value: data?.additionalDetails?.description },
        {
          title: "ES_FSM_REGISTRY_DETAILS_VENDOR_NAME",
          value: vendorDetails?.vendor?.[0]?.name || "ES_FSM_REGISTRY_DETAILS_ADD_VENDOR",
          type: "custom",
        },
      ],
    },
  ];
  return details;
};

const VehicleDetails = async (tenantId, filters = {}) => {
  const vehicleDetails = await FSMService.vehiclesSearch(tenantId, filters);
  const id = vehicleDetails?.vehicle?.[0]?.id;
  const vendorDetails = await FSMService.vendorSearch(tenantId, { vehicleIds: id });

  const data = vehicleDetails.vehicle.map((data) => ({
    vehicleData: data,
    employeeResponse: getResponse(data, vendorDetails),
    vendorDetails: vendorDetails,
  }));

  return data;
};

export default VehicleDetails;
