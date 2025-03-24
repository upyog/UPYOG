export const filterFunctions = {
    CND: (filtersArg) => {
        console.log("filtersArg",filtersArg);
        let { uuid } = Digit.UserService.getUser()?.info || {};
    
        const searchFilters = {};
        const workflowFilters = {};
    
        const { applicationNumber, mobileNumber,limit, offset, sortBy, sortOrder, total, applicationStatus, status, services } = filtersArg || {};
        if (filtersArg?.applicationNumber) {
          searchFilters.applicationNumber = filtersArg?.applicationNumber;
        }
        if (applicationStatus && applicationStatus?.[0]) {
          workflowFilters.applicationStatus = applicationStatus.map((status) => status.code).join(",");
        }
        if (status && status?.[0]) {
          workflowFilters.status = status.map((status) => status.code).join(",");
        }
        if (filtersArg?.locality?.length) {
          searchFilters.locality = filtersArg?.locality.map((item) => item.code.split("_").pop()).join(",");
        }
    
        if (filtersArg?.locality?.code) {
          searchFilters.locality = filtersArg?.locality?.code;
        }
    
        if (filtersArg?.uuid && filtersArg?.uuid.code === "ASSIGNED_TO_ME") {
          workflowFilters.assignee = uuid;
        }
        if (mobileNumber) {
          searchFilters.mobileNumber = mobileNumber;
        }
        if (applicationNumber) {
          searchFilters.applicationNumber = applicationNumber;
        }
        if (sortBy) {
          searchFilters.sortBy = sortBy;
        }
        if (sortOrder) {
          searchFilters.sortOrder = sortOrder;
        }
        if (services) {
          workflowFilters.businessServices = services.join();
        }
        if (limit) {
          searchFilters.limit = limit;
        }
        if (offset) {
          searchFilters.offset = offset;
        }
    
        return { searchFilters, workflowFilters };
      },
  
};
