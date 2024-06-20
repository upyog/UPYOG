import { MdmsService } from "../../services/elements/MDMS";
import { useQuery } from "react-query";
const useWmsMDMS = (tenantId, moduleCode, type, config = {}) => {
  const useWmsDepartments = () => {
    return useQuery(["WMS_DEPARTMENT", tenantId], () => MdmsService.WMSDepartment(tenantId, moduleCode, type), config);
  };
 const useWmsFunds = () => {
    return useQuery(["WMS_FUND", tenantId], () => MdmsService.WMSFund(tenantId, moduleCode, type), config);
  };
  const  useWmsChapters = () => {   
    return useQuery(["WMS_CHAPTER", tenantId],() =>  MdmsService.WMSChapter(tenantId, moduleCode, type) , config);
  };
  const  useWmsUnits = () => {   
    return useQuery(["WMS_UNIT", tenantId],() =>  MdmsService.WMSUnit(tenantId, moduleCode, type) , config);
  };
  const  useWmsProjects = () => {
    return useQuery(["WMS_PROJECT", tenantId],() =>  MdmsService.WMSProject(tenantId, moduleCode, type) , config);
  };
  switch (type) {
    case "Department":
      return useWmsDepartments();
       case "Fund":
      return useWmsFunds();
      case "Chapter":
      return useWmsChapters();
      case "Unit":
      return useWmsUnits();
      case "Project":
        return useWmsProjects();
  }
};
  

export default useWmsMDMS;
