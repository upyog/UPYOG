import { CHBServices } from "../../elements/CHB"



const ApplicationUpdateActionsCHB = async (applicationData, tenantId) => {
  
  
  try {
    const response = await CHBServices.update(applicationData, tenantId);
    return response;
  } catch (error) {
    throw new Error(error?.response?.data?.Errors[0].message);
  }
};

export default ApplicationUpdateActionsCHB;
