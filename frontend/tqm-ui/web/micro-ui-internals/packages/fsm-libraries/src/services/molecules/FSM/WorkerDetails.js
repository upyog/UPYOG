import { FSMService } from "../../elements/FSM";

const getResponse = (data, vendorDetails = {}, tenantId, t) => {
  const rolesData = data?.additionalFields?.fields;
  const sysRole = data?.userDetails?.roles;
  const countIndex = rolesData.findIndex((obj) => obj.key === "FUNCTIONAL_ROLE_COUNT");
  const count = parseInt(rolesData[countIndex]?.value) || null;
  const groupedObjects = [];
  const licenseNumber = data?.identifiers?.[0]?.identifierType === "DRIVING_LICENSE_NUMBER" ? data?.identifiers?.[0]?.identifierId : null;
  if (count) {
    for (let i = 1; i <= count; i++) {
      const group =
        countIndex !== -1
          ? rolesData
              .filter((obj) => obj.key.includes(`_${i}`))
              .reduce((acc, obj) => {
                acc[obj.key.replace(`_${i}`, "")] = obj.value;
                if (obj.key.replace(`_${i}`, "") === "FUNCTIONAL_ROLE" && obj.value === "DRIVER") {
                  // "FSM_DRIVER"
                  acc["LICENSE_NUMBER"] = licenseNumber;
                  // acc["SYSTEM_ROLE"] = sysRole
                }
                return acc;
              }, {})
          : rolesData.reduce((acc, obj) => {
              acc[obj.key] = obj.value;
              return acc;
            });
      groupedObjects.push(group);
    }
  }
  let details = [
    {
      title: "ES_FSM_REGISTRY_PERSONAL_DETAILS",
      values: [
        { title: "ES_FSM_REGISTRY_WORKER_ID", value: data?.individualId },
        { title: "ES_FSM_REGISTRY_WORKER_NAME", value: data?.name?.givenName },
        { title: "ES_FSM_REGISTRY_DETAILS_DOB", value: data?.dateOfBirth },
        { title: "ES_FSM_REGISTRY_DETAILS_GENDER", value: data?.gender },
      ],
    },
    {
      title: "ES_FSM_REGISTRY_CONTACT_DETAILS",
      values: [
        { title: "FSM_REGISTRY_APPLICANT_MOBILE_NO", value: data?.mobileNumber },
        { title: "ES_APPLICATION_DETAILS_LOCATION_PINCODE", value: data?.address?.[0]?.pincode },
        { title: "ES_APPLICATION_DETAILS_LOCATION_CITY", value: data?.address?.[0]?.city },
        { title: "CS_APPLICATION_DETAILS_MOHALLA", value: data?.address?.[0]?.locality?.code },
        { title: "CS_APPLICATION_DETAILS_STREET", value: data?.address?.[0]?.street },
        { title: "ES_APPLICATION_DETAILS_LOCATION_DOOR", value: data?.address?.[0]?.doorNo },
        { title: "ES_APPLICATION_DETAILS_LOCATION_LANDMARK", value: data?.address?.[0]?.landmark },
      ],
    },
    {
      title: "ES_FSM_REGISTRY_SKILLS_DETAILS",
      values: [{ title: "ES_FSM_REGISTRY_SKILLS_LABEL", value: data?.skills?.map((i) => t(`SW_SKILL_${i.type}`)).join(", ") }],
    },
    {
      title: "ES_FSM_REGISTRY_EMPLOYMENT_DETAILS",
      values: [
        { title: "ES_FSM_REGISTRY_EMPLOYER_LABEL", value: rolesData?.find((i) => i.key === "EMPLOYER") ? rolesData?.find((i) => i.key === "EMPLOYER").value : "N/A" },
        { title: "ES_FSM_REGISTRY_DETAILS_VENDOR_NAME", value: vendorDetails?.name || "ES_FSM_REGISTRY_DETAILS_ADD_VENDOR", type: "custom" },
      ],
    },
    {
      titlee: "ES_FSM_REGISTRY_PHOTO_DETAILS",
      photo: data?.photo ? [data?.photo] : [],
      isPhoto: data?.photo,
    },
    groupedObjects.length > 0
      ? {
          title: "ES_FSM_REGISTRY_ROLE_DETAILS",
          type: "role",
          child: groupedObjects,
        }
      : {},
  ];

  return details;
};

const WorkerDetails = async ({ tenantId, params, details, t }) => {
  const workerDetails = await FSMService.workerSearch({ tenantId, details, params });
  const ids = workerDetails?.Individual?.map((i) => i.id).join(",");
  const vendorDetails = await FSMService.vendorSearch(tenantId, { individualIds: ids, status: "ACTIVE" });

  const data = workerDetails?.Individual?.map((data) => ({
    workerData: data,
    employeeResponse: getResponse(data, vendorDetails?.vendor?.[0], tenantId, t),
    vendorDetails: vendorDetails?.vendor?.[0],
    agencyType: data?.additionalFields?.fields?.find((i) => i.key === "EMPLOYER") ? data?.additionalFields?.fields?.find((i) => i.key === "EMPLOYER").value : null,
  }));

  return data;
};

export default WorkerDetails;