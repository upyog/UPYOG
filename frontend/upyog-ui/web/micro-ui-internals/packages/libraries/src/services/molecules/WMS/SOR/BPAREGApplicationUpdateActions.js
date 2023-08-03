import { WMSService } from "../../elements/WMS";

const BPAREGApplicationUpdateActions = async (applicationData, tenantId) => {
  try {
    const response = await WMSService.BPAREGupdate(applicationData, tenantId);
    return response;
  } catch (error) {
    throw new Error(error?.response?.data?.Errors[0].message);
  }
};

export default BPAREGApplicationUpdateActions;
