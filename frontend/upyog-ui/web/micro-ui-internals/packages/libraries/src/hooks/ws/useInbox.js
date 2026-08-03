import useInbox from "../useInbox";
import { useEffect } from "react";

const useWSInbox = ({ tenantId, filters, config = {} }) => {
  const { filterForm, searchForm, tableForm } = filters;
  const user = Digit.UserService.getUser();
  let { mobileNumber, applicationNumber, consumerNo } = searchForm;
  let { sortBy, limit, offset, sortOrder } = tableForm;
  let { moduleName, businessService, applicationStatus, locality, assignee, businessServiceArray, applicationType } = filterForm;

  useEffect(() => {
    if (mobileNumber || applicationNumber || consumerNo) {
      offset = 0;
    }
  }, [filters?.searchForm?.applicationNumber, filters?.searchForm?.consumerNo, filters?.searchForm?.mobileNumber]);

  if (!window.location.href.includes("upyog-ui/employee/")) {
    moduleName = moduleName;
  } else {
    if (window.location.href.includes("water/inbox")) moduleName = "ws-services";
    if (window.location.href.includes("sewerage/inbox")) moduleName = "sw-services";
  }

  if (moduleName === "ws-services") {
    if (applicationType && applicationType.length > 0) {
      businessService = applicationType;
    } else {
      businessService = ["NewWS1", "ModifyWSConnection", "DisconnectWSConnection"];
    }
  }
  if (window.location.href.includes("sewerage/inbox")) {
    if (applicationType && applicationType.length > 0) {
      businessService = applicationType;
    } else {
      businessService = ["NewSW1", "ModifySWConnection", "DisconnectSWConnection"];
    }
  }

  let _filters = {
    tenantId,
    processSearchCriteria: {
      moduleName: moduleName,
      businessService: businessService,
      ...(applicationStatus?.length > 0 ? { status: applicationStatus } : {}),
    },
    moduleSearchCriteria: {
      businessService: businessService?.join(","),
      ...(mobileNumber ? { mobileNumber } : {}),
      ...(applicationNumber ? { applicationNumber } : {}),
      ...(consumerNo ? { consumerNo } : {}),
      ...(sortOrder ? { sortOrder } : {}),
      sortBy: "additionalDetails.appCreatedDate",
      ...(locality?.length > 0 ? { locality: locality.map((item) => item.code.split("_").pop()).join(",") } : {}),
    },
    limit,
    offset,
  };

  if (assignee === "ASSIGNED_TO_ME") {
    _filters.moduleSearchCriteria.assignee = user?.info?.uuid;
  }

  return useInbox({
    tenantId,
    filters: _filters,
    config: {
      select: (data) => ({
        statuses: data.statusMap,
        table: data?.items?.map((application) => {
          const appData = application?.businessObject?.Data;
          const latestHistory = appData?.history?.[0];
          return {
            applicationNo: appData?.applicationNo || "NA",
            applicationType: latestHistory?.businessService || "NA",
            status: appData?.applicationStatus || latestHistory?.state?.applicationStatus || "NA",
            owner: appData?.connectionHolders?.[0]?.name || appData?.additionalDetails?.ownerName || "NA",
            sla: latestHistory?.businesssServiceSla
              ? Math.round(latestHistory.businesssServiceSla / (24 * 60 * 60 * 1000))
              : "NA",
            connectionNo: appData?.connectionNo || "NA",
          };
        }) || [],
        slaCount: data?.nearingSlaCount || 0,
        totalCount: data?.totalCount || data?.items?.length || 0,
      }),
      ...config,
    },
  });
};

export default useWSInbox;