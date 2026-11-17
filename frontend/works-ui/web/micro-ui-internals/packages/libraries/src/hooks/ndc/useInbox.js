import useInbox from "../useInbox";

// useNDCInbox is a custom hook that fetches the inbox data for the NDC module.
//  It takes in tenantId, filters, and config as parameters and returns the inbox data based on the provided filters and configuration.
const useNDCInbox = ({ tenantId, filters, config = {} }) => {
  const { filterForm, searchForm, tableForm, getFilter } = filters;
  let { assignee } = filterForm;
  const { applicationNo, mobileNumber } = searchForm;
  const { limit, offset } = tableForm;
  const user = Digit.UserService.getUser();
  const status = filters?.filterForm?.applicationStatus;


  const _filters = {
    tenantId,
    processSearchCriteria: {
      assignee: assignee === "ASSIGNED_TO_ME" ? user?.info?.uuid : "",
      moduleName: "NDC",
      businessService: ["ndc-services"]
    },

    moduleSearchCriteria:
      status.length > 0
        ? {
            applicationStatus: status,
            ...(applicationNo ? { applicationNo } : {}),
            ...(mobileNumber ? { mobileNumber } : {}),
            sortOrder: "DESC",
          }
        : {
            ...(applicationNo ? { applicationNo } : {}),
            ...(mobileNumber ? { mobileNumber } : {}),
            sortOrder: "DESC",
          },

    limit,
    offset,
  };

  return useInbox({
    tenantId,
    filters: _filters,
    config: {
      select: (data) => {
        const tableData = data?.items?.map((application) => {
          return {
            applicationId: application.businessObject?.applicationNo,
            date: parseInt(application.businessObject?.auditDetails?.createdTime),
            businessService: application?.ProcessInstance?.businessService,
            locality: `${application.businessObject?.tenantId?.toUpperCase()?.split(".")?.join("_")}`,
            status: `${application.businessObject.applicationStatus}`,
            owner: application?.ProcessInstance?.assigner?.[0]?.name || "-",
          };
        });

        return {
          statuses: data.statusMap,
          table: tableData,
          totalCount: data.totalCount,
          nearingSlaCount: data.nearingSlaCount,
        };
      },
      ...config,
    },
  });
};

export default useNDCInbox;
