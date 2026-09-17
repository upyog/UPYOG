import { GCServices } from "../../elements/GC";

const ApplicationUpdateActionGC = async (applicationData, tenantId) => {
  try {
    const response = await GCServices.update(applicationData, tenantId);
    return response;
  } catch (error) {
    throw new Error(error?.response?.data?.Errors?.[0]?.message || error?.message);
  }
};

export default ApplicationUpdateActionGC;
