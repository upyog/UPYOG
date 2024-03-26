// import { MdmsService } from "../../services/elements/MDMS";
import WMSService from "../../../services/elements/WMS";

import { useQuery,useMutation } from "react-query";

const useWMSTEMaster = ( tenantId, type,endppoints, moduleCode, config = {}) => {
  console.log("tenantId, type ",{tenantId, type})
  // Department
  const DepartmentGetAllRecord  = () => {
    return useQuery(["WMS_DEPARTMENT_BRANCH", tenantId], () => WMSService.TenderEntry.getDepartmentMasterData(tenantId, moduleCode, type), config);
  };
  const DepartmentGetSingleRecord  = () => {
    return useQuery(["WMS_DEPARTMENT_SINGLE_RECORD", tenantId], () => WMSService.TenderEntry.getDepartmentSingleResordsMasterData(tenantId), config);
  };
  const DepartmentUpdateRecord = () => {
    return useMutation((data) => WMSService.TenderEntry.updateDepartmentMasterData(data), config);
  };
  const DepartmentCreateRecord = () => {
    return useMutation((data) => WMSService.TenderEntry.createDepartmentMasterData(data), config);
  };

  // Tender Category
  const TenderCategoryGetAllRecord  = () => {
    return useQuery(["WMS_TenderCategoryGetAllRecord_BRANCH", tenantId], () => WMSService.TenderEntry.getTenderCategoryMasterData(tenantId, moduleCode, type), config);
  };
  const TenderCategoryGetSingleRecord  = () => {
    return useQuery(["WMS_TenderCategoryGetAllRecord_SINGLE_RECORD", tenantId], () => WMSService.TenderEntry.getTenderCategorySingleResordsMasterData(tenantId), config);
  };
  const TenderCategoryUpdateRecord = () => {
    return useMutation((data) => WMSService.TenderEntry.updateTenderCategoryMasterData(data), config);
  };
  const TenderCategoryCreateRecord = () => {
    return useMutation((data) => WMSService.TenderEntry.createTenderCategoryMasterData(data), config);
  };

    // Tender Category
    const getProjectNameGetAllRecord  = () => {
      return useQuery(["WMS_PROJECT_NAME_BRANCH", tenantId], () => WMSService.TenderEntry.getProjectName(), config);
    };
  

  switch (type) {
    case "WMS_DEPARTMENT_ALL_RECORD":
      return DepartmentGetAllRecord();
    case "WMS_DEPARTMENT_SINGLE_RECORD":
      return DepartmentGetSingleRecord();
    case "WMS_DEPARTMENT_UPDATE":
      return DepartmentUpdateRecord();
    case "WMS_DEPARTMENT_CREATE":
      return DepartmentCreateRecord();

      case "WMS_TENDER_CATEGORY_ALL_RECORD":
        return TenderCategoryGetAllRecord();
      case "WMS_TENDER_CATEGORY_SINGLE_RECORD":
        return TenderCategoryGetSingleRecord();
      case "WMS_TENDER_CATEGORY_UPDATE":
        return TenderCategoryUpdateRecord();
      case "WMS_TENDER_CATEGORY_CREATE":
        return TenderCategoryCreateRecord();

        case "WMS_PROJECT_NAME_ALL_RECORD":
        return getProjectNameGetAllRecord()
  }
};

export default useWMSTEMaster;
