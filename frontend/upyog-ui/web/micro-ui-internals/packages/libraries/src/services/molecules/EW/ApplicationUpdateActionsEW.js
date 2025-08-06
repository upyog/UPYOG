import { EwService } from "../../elements/EW"


const ApplicationUpdateActionsEW = async (applicationData, tenantId) => {
  
  try {

    const response = await EwService.update(applicationData, tenantId);
    return response;   

  } catch (error) {
    throw new Error(error?.response?.data?.Errors[0].message);
  }
};

export default ApplicationUpdateActionsEW;
