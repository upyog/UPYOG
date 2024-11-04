import { SVService } from "../../elements/SV";

const ApplicationUpdateActionsSV = async (applicationData, tenantId) => {
  
  try {
    const response = await SVService.update(applicationData, tenantId);
    return response;
  } catch (error) {
    throw new Error(error?.response?.data?.Errors[0].message);
  }
};

export default ApplicationUpdateActionsSV;
