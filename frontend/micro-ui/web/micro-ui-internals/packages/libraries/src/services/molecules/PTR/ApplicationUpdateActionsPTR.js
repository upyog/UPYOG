 // Importing the PTRService which contains methods for interacting with the PTR API
 import { PTRService } from "../../elements/PTR";

// Function to handle the update action for a PTR application
const ApplicationUpdateActionsPTR = async (applicationData, tenantId) => {
  
  
  try {
    // Calling the update method from PTRService to update the application data
    const response = await PTRService.update(applicationData, tenantId);
    return response;
  } catch (error) {
    throw new Error(error?.response?.data?.Errors[0].message);
  }
};

export default ApplicationUpdateActionsPTR;
