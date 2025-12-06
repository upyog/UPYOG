import { FSMService } from "../../elements/FSM";

const getVehicleDetails = (vehicles) => {
  return vehicles
    ? vehicles?.map((vehicle, index) => {
        return {
          name: index,
          values: [
            { title: "ES_FSM_REGISTRY_VEHICLE_NUMBER", value: vehicle?.registrationNumber },
            { title: "ES_FSM_REGISTRY_VEHICLE_TYPE", value: `COMMON_MASTER_VEHICLE_${vehicle?.type}` },
            { title: "ES_FSM_REGISTRY_VEHICLE_MODEL", value: vehicle?.model },
            { title: "ES_FSM_REGISTRY_VEHICLE_CAPACITY", value: vehicle?.tankCapacity },
            {
              title: "ES_FSM_REGISTRY_VEHICLE_POLLUTION_CERT",
              value: vehicle?.pollutionCertiValidTill && Digit.DateUtils.ConvertEpochToDate(vehicle?.pollutionCertiValidTill),
            },
            {
              title: "ES_FSM_REGISTRY_VEHICLE_ROAD_TAX",
              value: vehicle?.roadTaxPaidTill && Digit.DateUtils.ConvertEpochToDate(vehicle?.roadTaxPaidTill),
            },
            {
              title: "ES_FSM_REGISTRY_VEHICLE_INSURANCE",
              value: vehicle?.InsuranceCertValidTill && Digit.DateUtils.ConvertEpochToDate(vehicle?.InsuranceCertValidTill),
            },
            { title: "ES_FSM_REGISTRY_VEHICLE_STATUS", value: vehicle.status },
            { title: "ES_FSM_REGISTRY_VEHICLE_ADDITIONAL_DETAILS", value: vehicle?.additionalDetails?.description },
          ],
        };
      })
    : [];
};

const getDriverDetails = (drivers) => {
  return drivers
    ? drivers?.map((driver, index) => {
        return {
          name: index,
          id: driver?.id,
          values: [
            { title: "ES_FSM_REGISTRY_DRIVER_NAME", value: driver?.name },
            // { title: "ES_FSM_REGISTRY_DRIVER_PHONE", value: driver?.owner?.mobileNumber },
            { title: "ES_FSM_REGISTRY_DRIVER_LICENSE", value: driver?.licenseNumber },
            { title: "ES_FSM_REGISTRY_DRIVER_STATUS", value: driver?.status },
          ],
        };
      })
    : [];
};

const getWorkerDetails = (workers, t) => {
  return workers
    ? workers?.map((worker, index) => {
        const fn_list = worker?.additionalFields?.fields?.filter((item) => item.key.startsWith("FUNCTIONAL_ROLE_") && item.key !== "FUNCTIONAL_ROLE_COUNT");
        return {
          name: index,
          id: worker?.individualId,
          swid: worker?.id,
          values: [
            { title: "ES_FSM_REGISTRY_WORKER_NAME", value: worker?.name?.givenName || "N/A" },
            { title: "ES_FSM_REGISTRY_WORKER_ID", value: worker?.individualId || "N/A" },
            { title: "ES_FSM_REGISTRY_WORKER_GENDER", value: worker?.gender },
            { title: "ES_FSM_REGISTRY_WORKER_DOB", value: worker?.dateOfBirth },
            { title: "ES_FSM_REGISTRY_WORKER_SKILLS", value: worker?.skills?.length > 0 ? worker?.skills?.map((i) => t(`SW_SKILLS_${i.type}`))?.join(", ") : "N/A" },
            { title: "ES_FSM_REGISTRY_WORKER_FUNCTIONAL_ROLE", value: fn_list?.length > 0 ? fn_list?.map((i) => t(`SW_ROLE_${i.value}`))?.join(", ") : "N/A" },
            worker?.identifiers?.filter((i) => i.identifierType === "DRIVING_LICENSE_NUMBER").length > 0
              ? { title: "ES_FSM_REGISTRY_DRIVER_LICENSE", value: worker?.identifiers?.filter((i) => i.identifierType === "DRIVING_LICENSE_NUMBER")?.[0]?.identifierId || "N/A" }
              : null,
            worker?.photo
              ? {
                  // titlee: "ES_FSM_REGISTRY_PHOTO_DETAILS",
                  photo: worker?.photo ? [worker?.photo] : [],
                  isPhoto: worker?.photo,
                }
              : null,
            // { title: "ES_FSM_REGISTRY_DRIVER_LICENSE", value: driver?.licenseNumber },
          ],
        };
      })
    : [];
};

const getResponse = (data, t, workers) => {
  let details = [
    {
      title: "",
      values: [
        { title: "ES_FSM_REGISTRY_DETAILS_VENDOR_NAME", value: data?.name },
        { title: "ES_FSM_REGISTRY_DETAILS_VENDOR_ADDRESS", value: data?.address?.locality?.name },
        { title: "ES_FSM_REGISTRY_DETAILS_VENDOR_PHONE", value: data?.owner?.mobileNumber },
        { title: "ES_FSM_REGISTRY_DETAILS_ADDITIONAL_DETAILS", value: data?.additionalDetails?.description },
        {
          title: "ES_FSM_REGISTRY_AGENCY_TYPE",
          value: data?.agencyType ? t(Digit.Utils.locale.getTransformedLocale(`FSM_VENDOR_AGENCY_TYPE_${data?.agencyType}`)) : t("ES_COMMON_NA"),
        },
      ],
    },
    {
      title: "ES_FSM_REGISTRY_DETAILS_VEHICLE_DETAILS",
      type: "ES_FSM_REGISTRY_DETAILS_TYPE_VEHICLE",
      child: getVehicleDetails(data.vehicles),
    },
    {
      title: "ES_FSM_REGISTRY_DETAILS_WORKER_DETAILS",
      type: "ES_FSM_REGISTRY_DETAILS_TYPE_WORKER",
      child: getWorkerDetails(workers, t),
    },
  ];
  return details;
};

const DsoDetails = async (tenantId, filters = {}, t) => {
  const dsoDetails = await FSMService.vendorSearch(tenantId, filters);

  const workers = window.location.pathname.includes("vendor-details")
    ? await Digit.FSMService.workerSearch({
        tenantId,
        params: {
          offset: 0,
          limit: 100,
        },
        details: {
          Individual: {
            id: dsoDetails?.vendor?.flatMap((dso) => dso?.workers?.map((i) => i.individualId)),
          },
        },
      })
    : null;

  const data = dsoDetails?.vendor?.map((dso) => ({
    workers: dso?.workers,
    displayName: dso.name,
    mobileNumber: dso.owner?.mobileNumber,
    name: dso.name,
    username: dso.owner?.userName,
    ownerId: dso.ownerId,
    id: dso.id,
    auditDetails: dso.auditDetails,
    drivers: dso.drivers,
    activeDrivers: dso.drivers?.filter((driver) => driver.status === "ACTIVE"),
    workers: dso.workers,
    // activeWorkers: dso.workers?.filter((worker) => worker.vendorWorkerStatus === "ACTIVE"),
    allVehicles: dso.vehicles,
    dsoDetails: dso,
    employeeResponse: getResponse(dso, t, workers?.Individual ? workers?.Individual : null),
    vehicles: dso.vehicles
      ?.filter((vehicle) => vehicle.status === "ACTIVE")
      ?.map((vehicle) => ({
        id: vehicle.id,
        registrationNumber: vehicle?.registrationNumber,
        type: vehicle.type,
        i18nKey: `FSM_VEHICLE_TYPE_${vehicle.type}`,
        capacity: vehicle.tankCapacity,
        suctionType: vehicle.suctionType,
        model: vehicle.model,
      })),
  }));

  return data;
};

export default DsoDetails;
