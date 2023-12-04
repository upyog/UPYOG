 import { PTRService } from "../../elements/PTR";


const ApplicationUpdateActionsPTR = async (applicationData, tenantId) => {
  
  
  try {
    const response = await PTRService.update(applicationData, tenantId);
    console.log("API Response:", response); // Log the API response
    
    return response;
  } catch (error) {
    console.error("API Error:", error); // Log the API error
    throw new Error(error?.response?.data?.Errors[0].message);
  }
};

export default ApplicationUpdateActionsPTR;
