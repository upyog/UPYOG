export const filterFunctions = {
  CND: (filtersArg) => {

      // Extracting user UUID from the Digit user service
      let { uuid } = Digit.UserService.getUser()?.info || {};

      // Initializing empty filter objects
      const searchFilters = {};   // Used for general search filters
      const workflowFilters = {}; // Used for workflow-related filters

      // Destructuring filter arguments from the function parameter
      const { applicationNumber, services, mobileNumber, limit, offset, sortBy, sortOrder, applicationStatus, status } = filtersArg || {};

      // Handling applicationStatus filter (workflow-based filtering)
      if (applicationStatus && applicationStatus?.[0]?.applicationStatus) {
          // Extracting status UUIDs for filtering
          workflowFilters.status = applicationStatus.map((status) => status.uuid);

          // If any applicationStatus has a "nonActionableRole", fetch non-actionable records
          if (applicationStatus?.some((e) => e.nonActionableRole)) {
              searchFilters.fetchNonActionableRecords = true;
          }
      }

      // Handling status filter (workflow-based filtering)
      if (status && status?.[0]?.status) {
          // Extracting status UUIDs for filtering
          workflowFilters.status = status.map((status) => status.uuid);

          // If any status has a "nonActionableRole", fetch non-actionable records
          if (status?.some((e) => e.nonActionableRole)) {
              searchFilters.fetchNonActionableRecords = true;
          }
      }

      // If filter argument contains "ASSIGNED_TO_ME", assign the current user UUID
      if (filtersArg?.uuid && filtersArg?.uuid.code === "ASSIGNED_TO_ME") {
          workflowFilters.assignee = uuid;
      }

      // Adding mobile number to search filters if provided
      if (mobileNumber) {
          searchFilters.mobileNumber = mobileNumber;
      }

      // Adding status filter if provided
      if (status) {
          searchFilters.applicationStatus = status;
      }

      // Adding application number filter if provided
      if (applicationNumber) {
          searchFilters.applicationNumber = applicationNumber;
      }

      // Adding business service filter if provided
      if (services) {
          workflowFilters.businessService = services;
      }

      // Enforcing additional default filters for the search
      searchFilters["isInboxSearch"] = true;
      searchFilters["creationReason"] = [""]; // Empty reason, can be modified if needed
      workflowFilters["moduleName"] = "cnd-service"; // Defining the module for workflow filters

      // Returning the final filter objects along with pagination & sorting options
      return { searchFilters, workflowFilters, limit, offset, sortBy, sortOrder, isUserDetailRequired:true };
  }
};
