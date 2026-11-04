import { queryTemplate } from "../../common/queryTemplate";
import { OBPSService } from "../../services/elements/OBPS";

const convertMillisecondsToDays = (ms) =>
  Math.round(ms / (1000 * 60 * 60 * 24));

const mapWfBybusinessId = (workflowData) =>
  workflowData?.reduce((acc, item) => {
    acc[item?.businessId] = item;
    return acc;
  }, {});

const combineResponse = (applications, workflowData) => {
  const wfMap = mapWfBybusinessId(workflowData);

  return applications.map((application) => {
    const wf = wfMap?.[application?.applicationNo];
    const isApproved = application?.status === "APPROVED";

    return {
      ...application,
      assignee: wf?.assignes?.[0]?.name || "-",
      sla: isApproved
        ? "CS_NA"
        : wf?.businesssServiceSla != null
        ? convertMillisecondsToDays(wf.businesssServiceSla)
        : "CS_NA",
      state: wf?.state?.state || application?.status,
      action: wf?.action || "-",
    };
  });
};

const useBPASearch = (tenantId, filters = {}, config = {}) => {
  // ⚠️ keep your existing logic untouched
  if (window.location.href.includes("search/application")) {
    if (!filters?.limit) filters.limit = 10;
    if (!filters?.offset) filters.offset = 0;
  }

  const userInfos = sessionStorage.getItem("Digit.citizen.userRequestObject");
  const userInfo = userInfos ? JSON.parse(userInfos) : {};
  const userInformation = userInfo?.value?.info;

  if (window.location.href.includes("/citizen") && window.location.href.includes("/search")) {
    if (!filters?.createdBy && !window.location.href.includes("obps-application"))
      filters.createdBy = userInformation?.uuid;
    if (!filters?.applicationType)
      filters.applicationType = "BUILDING_PLAN_SCRUTINY";
    if (!filters?.serviceType)
      filters.serviceType = "NEW_CONSTRUCTION";
  }

  if (window.location.href.includes("/search/obps-application"))
    filters.mobileNumber = userInformation?.mobileNumber;

  return queryTemplate({
    queryKey: ["BPA_SEARCH", tenantId, filters],

    queryFn: async () => {
      const response = await OBPSService.BPASearch(tenantId, { ...filters });

      let tenantMap = {},
        processInstanceArray = [],
        appNumbers = [];

      response?.BPA?.forEach((item) => {
        const appNums = tenantMap[item.tenantId] || [];
        appNumbers = appNums;
        appNums.push(item.applicationNo);
        tenantMap[item.tenantId] = appNums;
        item["Count"] = response?.Count;
      });

      for (let key in tenantMap) {
        for (let i = 0; i < appNumbers.length / 100; i++) {
          try {
            const payload = await Digit.WorkflowService.getAllApplication(
              key,
              {
                businessIds: tenantMap[key]
                  ?.slice(i * 100, i * 100 + 100)
                  ?.join(),
              }
            );
            processInstanceArray = processInstanceArray.concat(
              payload.ProcessInstances
            );
          } catch {
            return [];
          }
        }

        processInstanceArray = processInstanceArray.filter((r) =>
          r.moduleName.includes("bpa-services")
        );
      }

      return combineResponse(response?.BPA, processInstanceArray);
    },

    config,
  });
};

export default useBPASearch;