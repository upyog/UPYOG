import { NOCService } from "../../elements/NOC";

// To hit the update call while taking actions in FireNoc Applications
const FireNocApplicationActions = async (applicationData, tenantId) => {
  try {
    const response = await NOCService.update(applicationData, tenantId);
    return response;
  } catch (error) {
    throw new Error(error?.response?.data?.Errors[0].message);
  }
};

export default FireNocApplicationActions;
