export const filterFunctions = {
  PT: (filtersArg) => {
    let { uuid } = Digit.UserService.getUser()?.info || {};

    const searchFilters = {};
    const workflowFilters = {};

    const { propertyIds, mobileNumber, limit, offset, sortBy, sortOrder, total, applicationStatus, services } = filtersArg || {};

    if (filtersArg?.acknowledgementIds) {
      searchFilters.acknowledgementIds = filtersArg?.acknowledgementIds;
    }
    if (filtersArg?.propertyIds) {
      searchFilters.propertyIds = propertyIds;
    }
    if (filtersArg?.oldpropertyids) {
      searchFilters.oldpropertyids = filtersArg?.oldpropertyids;
    }
    if (applicationStatus && applicationStatus?.[0]) {
      workflowFilters.applicationStatus = applicationStatus.map((status) => status.code).join(",");
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
    if (propertyIds) {
      searchFilters.propertyIds = propertyIds;
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
  PTR: (filtersArg) => {
    console.log("filer",filtersArg )
    let { uuid } = Digit.UserService.getUser()?.info || {};

    const searchFilters = {};
    const workflowFilters = {};

    const { applicationNumber, mobileNumber, limit, offset, sortBy, sortOrder, total, applicationStatus, services } = filtersArg || {};

    if (filtersArg?.applicationNumber) {
      searchFilters.applicationNumber = filtersArg?.applicationNumber;
    }
    
    if (applicationStatus && applicationStatus?.[0]) {
      workflowFilters.applicationStatus = applicationStatus.map((status) => status.code).join(",");
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
  TL: (filtersArg) => {
    let { uuid } = Digit.UserService.getUser()?.info || {};

    const searchFilters = {};
    const workflowFilters = {};

    const { propertyIds, mobileNumber, limit, offset, sortBy, sortOrder, total, applicationStatus, services } = filtersArg || {};

    if (filtersArg?.acknowledgementIds) {
      searchFilters.acknowledgementIds = filtersArg?.acknowledgementIds;
    }
    if (filtersArg?.propertyIds) {
      searchFilters.propertyIds = propertyIds;
    }
    if (filtersArg?.oldpropertyids) {
      searchFilters.oldpropertyids = filtersArg?.oldpropertyids;
    }
    if (applicationStatus && applicationStatus?.[0]) {
      workflowFilters.applicationStatus = applicationStatus.map((status) => status.code).join(",");
    }
    if (filtersArg?.locality?.length) {
      searchFilters.locality = filtersArg?.locality.map((item) => item.code.split("_").pop()).join(",");
    }

    if (filtersArg?.uuid && filtersArg?.uuid.code === "ASSIGNED_TO_ME") {
      workflowFilters.assignee = uuid;
    }
    if (mobileNumber) {
      searchFilters.mobileNumber = mobileNumber;
    }
    if (propertyIds) {
      searchFilters.propertyIds = propertyIds;
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

  ASSET: (filtersArg) => {
    console.log("filtersArg",filtersArg);
    let { uuid } = Digit.UserService.getUser()?.info || {};

    const searchFilters = {};
    const workflowFilters = {};

    const { applicationNo, mobileNumber,assetclassification, assetParentCategory, limit, offset, sortBy, sortOrder, total, applicationStatus, services } = filtersArg || {};
    console.log("filtersArg",applicationNo,assetclassification,assetParentCategory);
    if (filtersArg?.applicationNo) {
      searchFilters.applicationNo = filtersArg?.applicationNo;
    }
    if (filtersArg?.assetclassification) {
      searchFilters.assetclassification = filtersArg?.assetclassification;
    }if (filtersArg?.assetParentCategory) {
      searchFilters.assetParentCategory = filtersArg?.assetParentCategory;
    }
    
    if (applicationStatus && applicationStatus?.[0]) {
      workflowFilters.applicationStatus = applicationStatus.map((status) => status.code).join(",");
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
    if (applicationNo) {
      searchFilters.applicationNo = applicationNo;
    }
    if (assetclassification) {
      searchFilters.assetclassification = assetclassification;
    }
    if (assetParentCategory) {
      searchFilters.assetParentCategory = assetParentCategory;
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

  SV: (filtersArg) => {
    let { uuid } = Digit.UserService.getUser()?.info || {};

    const searchFilters = {};
    const workflowFilters = {};

    const { applicationNumber, mobileNumber,limit, offset, sortBy, sortOrder, total, applicationStatus, vendingType, vendingZone, status, services } = filtersArg || {};
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
    if (vendingType) {
      searchFilters.vendingType = vendingType;
    }    
    if (vendingZone) {
      searchFilters.vendingZone = vendingZone;
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
  EW: (filtersArg) => {
    console.log("filer",filtersArg )
    let { uuid } = Digit.UserService.getUser()?.info || {};

    const searchFilters = {};
    const workflowFilters = {};

    const { requestId, mobileNumber, limit, offset, sortBy, sortOrder, total, applicationStatus, services } = filtersArg || {};

    if (filtersArg?.requestId) {
      searchFilters.requestId = filtersArg?.requestId;
    }
    
    if (applicationStatus && applicationStatus?.[0]) {
      workflowFilters.applicationStatus = applicationStatus.map((status) => status.code).join(",");
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
    if (requestId) {
      searchFilters.requestId = requestId;
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

    return { requestId,searchFilters, workflowFilters };
  },

  /**
 * PGRAI Filter Builder
 *
 * This function prepares the search and workflow filters used to fetch complaints in PGRAI.
 * It extracts filter arguments like serviceRequestId, application status, locality, etc.,
 * and formats them into two filter objects:
 *  - `searchFilters` for PGRAIService search
 *  - `workflowFilters` for workflow status & assignment filtering
 *
 * @param {Object} filtersArg - Filters input from UI or API (includes things like status, serviceRequestId, etc.)
 * @returns {Object} { searchFilters, workflowFilters } - Filter objects for backend query
 */
  PGRAI: (filtersArg) => {
    let { uuid } = Digit.UserService.getUser()?.info || {};

    const searchFilters = {};
    const workflowFilters = {};

    const { serviceRequestId, mobileNumber,limit, offset, sortBy, sortOrder, total, applicationStatus, status, services } = filtersArg || {};
    if (filtersArg?.serviceRequestId) {
      searchFilters.serviceRequestId = filtersArg?.serviceRequestId;
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
    if (serviceRequestId) {
      searchFilters.serviceRequestId = serviceRequestId;
    }
    // if (sortBy) {
    //   searchFilters.sortBy = sortBy;
    // }
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
