// Importing the EwService for updating E-Waste application data
import { EwService } from "../../elements/EW";

// Function to handle the update actions for E-Waste applications
const ApplicationUpdateActionsEW = async (applicationData, tenantId) => {
  try {
    // Call the EwService to update the application data
    const response = await EwService.update(applicationData, tenantId);
    return response; // Return the response from the service
  } catch (error) {
    // Throw an error with the message from the response if the update fails
    throw new Error(error?.response?.data?.Errors[0].message);
  }
};

export default ApplicationUpdateActionsEW; // Exporting the function for use in other components
