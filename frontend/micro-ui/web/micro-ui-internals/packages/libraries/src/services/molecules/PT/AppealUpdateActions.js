import { PTService } from "../../elements/PT";

const AppealUpdateActions = async (applicationData, tenantId) => {
  try {
    const response = await PTService.appealUpdate(applicationData, tenantId);
    return response;
  } catch (error) {
    throw new Error(error?.response?.data?.Errors[0].message);
  }
};

export default AppealUpdateActions;
