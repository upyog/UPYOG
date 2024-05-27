// import { ASSETService } from "../../elements/PTR";
import { ASSETService } from "../../elements/ASSET"



const ApplicationUpdateActionsASSET = async (applicationData, tenantId) => {
  
  
  try {
    const response = await ASSETService.update(applicationData, tenantId);
    return response;
  } catch (error) {
    throw new Error(error?.response?.data?.Errors[0].message);
  }
};

export default ApplicationUpdateActionsASSET;
