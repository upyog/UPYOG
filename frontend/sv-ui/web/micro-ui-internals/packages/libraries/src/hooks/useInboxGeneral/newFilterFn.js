export const filterFunctions = {
 SV: (filtersArg) => {
        let { uuid } = Digit.UserService.getUser()?.info || {};
    
        const searchFilters = {};
        const workflowFilters = {};
    
        const { applicationNumber, services, mobileNumber, limit, offset, sortBy, sortOrder, vendingType, applicationStatus, status } = filtersArg || {};

        if (applicationStatus && applicationStatus?.[0]?.applicationStatus) {
          workflowFilters.status = applicationStatus.map((status) => status.uuid);
          if (applicationStatus?.some((e) => e.nonActionableRole)) {
            searchFilters.fetchNonActionableRecords = true;
          }
        }
        if (status && status?.[0]?.status) {
          workflowFilters.status = status.map((status) => status.uuid);
          if (status?.some((e) => e.nonActionableRole)) {
            searchFilters.fetchNonActionableRecords = true;
          }
        }

        if (filtersArg?.uuid && filtersArg?.uuid.code === "ASSIGNED_TO_ME") {
          workflowFilters.assignee = uuid;
        }
        if (mobileNumber) {
          searchFilters.mobileNumber = mobileNumber;
        }
        if (vendingType) {
          searchFilters.vendingType = vendingType;
        }
        if (status) {
          searchFilters.applicationStatus = status;
        }
        if (applicationNumber) {
          searchFilters.applicationNumber = applicationNumber;
        }
        if (services) {
          workflowFilters.businessService = services;
        }
        searchFilters["isInboxSearch"] = true;
        searchFilters["creationReason"] = [""];
        workflowFilters["moduleName"] = "sv-services";
        
        return { searchFilters, workflowFilters, limit, offset, sortBy, sortOrder, isDraftApplication:false };
  },
};
