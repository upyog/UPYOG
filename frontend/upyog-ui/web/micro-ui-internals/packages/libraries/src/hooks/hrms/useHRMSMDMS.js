import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService } from "../../services/elements/MDMS";

const useHrmsMDMS = (tenantId, moduleCode, type, config = {}) => {
  switch (type) {
    case "HRMSRolesandDesignation":
      return queryTemplate({
        queryKey: ["HRMS_EMP_RD", tenantId],
        queryFn: () =>
          MdmsService.getHrmsEmployeeRolesandDesignation(tenantId),
        config,
      });

    case "EmployeeType":
      return queryTemplate({
        queryKey: ["HRMS_EMP_TYPE", tenantId],
        queryFn: () =>
          MdmsService.getHrmsEmployeeTypes(tenantId, moduleCode, type),
        config,
      });

    case "DeactivationReason":
      return queryTemplate({
        queryKey: ["HRMS_EMP_REASON", tenantId],
        queryFn: () =>
          MdmsService.getHrmsEmployeeReason(tenantId, moduleCode, type),
        config,
      });

    default:
      return null;
  }
};

export default useHrmsMDMS;