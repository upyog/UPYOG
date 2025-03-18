export const filterFunctions = {
  PT: (filtersArg) => {
    let { uuid } = Digit.UserService.getUser()?.info || {};

    const searchFilters = {};
    const workflowFilters = {};

    const { propertyIds, mobileNumber, limit, offset, sortBy, sortOrder, total, applicationStatus, services } = filtersArg || {};

    if (filtersArg?.acknowledgementIds) {
      searchFilters.applicationNumber = filtersArg?.acknowledgementIds;
    }
    if (filtersArg?.propertyIds) {
      searchFilters.propertyId = propertyIds;
    }
    if (filtersArg?.oldpropertyids) {
      searchFilters.oldpropertyids = filtersArg?.oldpropertyids;
    }
    if (applicationStatus && applicationStatus?.[0]) {
      workflowFilters.status = applicationStatus.map((status) => status.uuid);
      if (applicationStatus?.some((e) => e.nonActionableRole)) {
        searchFilters.fetchNonActionableRecords = true;
      }
    }
    if (filtersArg?.locality?.length) {
      searchFilters.locality = filtersArg?.locality.map((item) => item.code.split("_").pop());
    }
    if (filtersArg?.uuid && filtersArg?.uuid.code === "ASSIGNED_TO_ME") {
      workflowFilters.assignee = uuid;
    }
    if (mobileNumber) {
      searchFilters.mobileNumber = mobileNumber;
    }
    if (services) {
      workflowFilters.businessService = services;
    }
    searchFilters["isInboxSearch"] = true;
    searchFilters["creationReason"] = ["CREATE", "MUTATION", "UPDATE"];
    workflowFilters["moduleName"] = "PT";

    // if (limit) {
    //   searchFilters.limit = limit;
    // }
    // if (offset) {
    //   searchFilters.offset = offset;
    // }

    // workflowFilters.businessService = "PT.CREATE";
    // searchFilters.mobileNumber = "9898568989";
    return { searchFilters, workflowFilters, limit, offset, sortBy, sortOrder };
  },
  PTR: (filtersArg) => {
    

    let { uuid } = Digit.UserService.getUser()?.info || {};

    const searchFilters = {};
    const workflowFilters = {};

    const { applicationNumbers, mobileNumber, limit, offset, sortBy, sortOrder, total, applicationStatus, services } = filtersArg || {};

    if (filtersArg?.applicationNumber) {
      searchFilters.applicationNumber = filtersArg?.applicationNumber;
    }
    if (filtersArg?.applicationNumbers) {
      searchFilters.applicationNumber = applicationNumbers;
    }
    
    if (applicationStatus && applicationStatus?.[0]?.applicationStatus) {
      workflowFilters.status = applicationStatus.map((status) => status.uuid);
      if (applicationStatus?.some((e) => e.nonActionableRole)) {
        searchFilters.fetchNonActionableRecords = true;
      }
    }
    if (filtersArg?.locality?.length) {
      searchFilters.locality = filtersArg?.locality.map((item) => item.code.split("_").pop());
    }
    if (filtersArg?.uuid && filtersArg?.uuid.code === "ASSIGNED_TO_ME") {
      workflowFilters.assignee = uuid;
    }
    if (mobileNumber) {
      searchFilters.mobileNumber = mobileNumber;
    }


    if (services) {
      workflowFilters.businessService = services;
    }
    searchFilters["isInboxSearch"] = true;
    searchFilters["creationReason"] = ["CREATE"];
    workflowFilters["moduleName"] = "pet-services";


   
    return { searchFilters, workflowFilters, limit, offset, sortBy, sortOrder };
  },
  ASSET: (filtersArg) => {
    
    console.log("filtersArgss",filtersArg);
        let { uuid } = Digit.UserService.getUser()?.info || {};
    
        const searchFilters = {};
        const workflowFilters = {};
    
        const { applicationNo, assetParentCategory,assetclassification, limit, offset, sortBy, sortOrder, total, applicationStatus, services } = filtersArg || {};
    
        if (filtersArg?.applicationNo) {
          searchFilters.applicationNo = filtersArg?.applicationNo;
        }
        if (filtersArg?.assetParentCategory) {
          searchFilters.assetParentCategory = filtersArg?.assetParentCategory;
        }
        if (filtersArg?.assetclassification) {
          searchFilters.assetclassification = filtersArg?.assetclassification;
        }
        
        
        if (applicationStatus && applicationStatus?.[0]?.applicationStatus) {
          workflowFilters.status = applicationStatus.map((status) => status.uuid);
          if (applicationStatus?.some((e) => e.nonActionableRole)) {
            searchFilters.fetchNonActionableRecords = true;
          }
        }
        if (filtersArg?.locality?.length) {
          searchFilters.locality = filtersArg?.locality.map((item) => item.code.split("_").pop());
        }
        if (filtersArg?.uuid && filtersArg?.uuid.code === "ASSIGNED_TO_ME") {
          workflowFilters.assignee = uuid;
        }
        if (applicationNo) {
          searchFilters.applicationNo = applicationNo;
        }
        if (assetclassification) {
          searchFilters.assetclassification = assetclassification;
        }
        if (assetParentCategory) {
          searchFilters.assetParentCategory = assetParentCategory;
        }
    
    
        if (services) {
          workflowFilters.businessService = services;
        }
        searchFilters["isInboxSearch"] = true;
        searchFilters["creationReason"] = ["asset-create"];
        workflowFilters["moduleName"] = "asset-services";
    
    
       
        return { searchFilters, workflowFilters, limit, offset, sortBy, sortOrder };
  },

  EW: (filtersArg) => {


    let { uuid } = Digit.UserService.getUser()?.info || {};

    const searchFilters = {};
    const workflowFilters = {};

    const { requestId, mobileNumber, limit, offset, sortBy, sortOrder, total, applicationStatus, services } = filtersArg || {};

    if (filtersArg?.requestId) {
      searchFilters.requestId = filtersArg?.requestId;
    }
    if (filtersArg?.requestId) {
      searchFilters.requestId = requestId;
    }
    
    if (applicationStatus && applicationStatus?.[0]?.applicationStatus) {
      workflowFilters.status = applicationStatus.map((status) => status.uuid);
      if (applicationStatus?.some((e) => e.nonActionableRole)) {
        searchFilters.fetchNonActionableRecords = true;
      }
    }
    if (filtersArg?.locality?.length) {
      searchFilters.locality = filtersArg?.locality.map((item) => item.code.split("_").pop());
    }
    if (filtersArg?.uuid && filtersArg?.uuid.code === "ASSIGNED_TO_ME") {
      workflowFilters.assignee = uuid;
    }
    if (mobileNumber) {
      searchFilters.mobileNumber = mobileNumber;
    }


    if (services) {
      workflowFilters.businessService = services;
    }
    searchFilters["isInboxSearch"] = true;
    searchFilters["creationReason"] = ["ewst"];
    workflowFilters["moduleName"] = "ewaste-services";


    
    return { requestId,searchFilters, workflowFilters, limit, offset, sortBy, sortOrder };
  },
  CHB: (filtersArg) => {
    /*
  This function generates filters for querying Community Hall Booking (CHB) applications.

  - `searchFilters` is used to filter the application data based on various criteria such as:
    - `mobileNumber`: Filters by the user's mobile number.
    - `bookingNo`: Filters by the booking number.
    - `communityHallCode`: Filters by the code of the community hall.
    - `creationReason`: A fixed filter set to an empty array.
  
  The function also accepts parameters for pagination and sorting:
    - `limit`: The number of results to return.
    - `offset`: The starting point for pagination.
    - `sortBy`: The field to sort by.
    - `sortOrder`: The direction of the sort (ascending or descending).
*/

    let { uuid } = Digit.UserService.getUser()?.info || {};

    const searchFilters = {};
    const workflowFilters = {};


    const { bookingNo, mobileNumber,communityHallCode, limit, offset, sortBy, sortOrder, total, services } = filtersArg || {};

    if (filtersArg?.uuid && filtersArg?.uuid.code === "ASSIGNED_TO_ME") {
      workflowFilters.assignee = uuid;
    }
    if (mobileNumber) {
      searchFilters.mobileNumber = mobileNumber;
    }
    if(bookingNo) {   
      searchFilters.bookingNo = bookingNo;
    }
    if(communityHallCode){
      searchFilters.communityHallCode = communityHallCode.code;
    }

    if (services) {
      workflowFilters.businessService = services;
    }
    searchFilters["isInboxSearch"] = true;
    searchFilters["creationReason"] = [""];
    workflowFilters["moduleName"] = "chb-services";

    
    
    return { searchFilters, workflowFilters, limit, offset, sortBy, sortOrder };
  },

  WT: (filtersArg) => {
    /*
  This function generates filters for querying Water Tanker applications.

  - `searchFilters` is used to filter the application data based on various criteria such as:
    - `mobileNumber`: Filters by the user's mobile number.
    - `bookingNo`: Filters by the booking number.
    - `creationReason`: A fixed filter set to an empty array.
  
  The function also accepts parameters for pagination and sorting:
    - `limit`: The number of results to return.
    - `offset`: The starting point for pagination.
    - `sortBy`: The field to sort by.
    - `sortOrder`: The direction of the sort (ascending or descending).
*/

    let { uuid } = Digit.UserService.getUser()?.info || {};

    const searchFilters = {};
    const workflowFilters = {};


    const { bookingNo, mobileNumber,limit, offset, sortBy, sortOrder, total, services } = filtersArg || {};

    if (filtersArg?.uuid && filtersArg?.uuid.code === "ASSIGNED_TO_ME") {
      workflowFilters.assignee = uuid;
    }
    if (mobileNumber) {
      searchFilters.mobileNumber = mobileNumber;
    }
    if(bookingNo) {   
      searchFilters.bookingNo = bookingNo;
    }
    if (services) {
      workflowFilters.businessService = services;
    }
    searchFilters["isInboxSearch"] = true;
    searchFilters["creationReason"] = [""];
    workflowFilters["moduleName"] = "request-service.water_tanker";    
    return { searchFilters, workflowFilters, limit, offset, sortBy, sortOrder };
  },
  MT: (filtersArg) => {
    /*
  This function generates filters for querying Mobile Toilet applications.

  - `searchFilters` is used to filter the application data based on various criteria such as:
    - `mobileNumber`: Filters by the user's mobile number.
    - `bookingNo`: Filters by the booking number.
    - `creationReason`: A fixed filter set to an empty array.
  
  The function also accepts parameters for pagination and sorting:
    - `limit`: The number of results to return.
    - `offset`: The starting point for pagination.
    - `sortBy`: The field to sort by.
    - `sortOrder`: The direction of the sort (ascending or descending).
*/

    let { uuid } = Digit.UserService.getUser()?.info || {};

    const searchFilters = {};
    const workflowFilters = {};


    const { bookingNo, mobileNumber,limit, offset, sortBy, sortOrder, total, services } = filtersArg || {};

    if (filtersArg?.uuid && filtersArg?.uuid.code === "ASSIGNED_TO_ME") {
      workflowFilters.assignee = uuid;
    }
    if (mobileNumber) {
      searchFilters.mobileNumber = mobileNumber;
    }
    if(bookingNo) {   
      searchFilters.bookingNo = bookingNo;
    }
    if (services) {
      workflowFilters.businessService = services;
    }

    searchFilters["isInboxSearch"] = true;
    searchFilters["creationReason"] = [""];
    workflowFilters["moduleName"] = "request-service.mobile_toilet";
    
    return { searchFilters, workflowFilters, limit, offset, sortBy, sortOrder };
  },

  SV: (filtersArg) => {
    
    console.log("filtersArgssIN NEWFILTERFN",filtersArg);
        let { uuid } = Digit.UserService.getUser()?.info || {};
    
        const searchFilters = {};
        const workflowFilters = {};
    
        const { applicationNumber, services, mobileNumber, limit, offset, sortBy, sortOrder, vendingType, vendingZone, applicationStatus, status } = filtersArg || {};

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
        if (vendingZone) {
          searchFilters.vendingZone = vendingZone;
        }
        if (status) {
          searchFilters.status = status;
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
