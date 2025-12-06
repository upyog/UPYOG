import { CHBServices } from "../../elements/CHB"


/**
 * ApplicationUpdateActionsCHB Function
 * 
 * This function is responsible for updating CHB (Community Hall Booking) application data.
 * 
 * Parameters:
 * - `applicationData`: The application data to be updated.
 * - `tenantId`: The tenant ID for which the update is being performed.
 * 
 * Logic:
 * - Calls `CHBServices.update` to perform the update operation.
 * - Handles errors by throwing a new error with the message from the API response.
 * 
 * Returns:
 * - The response from the `CHBServices.update` API call.
 * 
 * Throws:
 * - An error with the message from the API response if the update fails.
 */
const ApplicationUpdateActionsCHB = async (applicationData, tenantId) => {
  
  
  try {
    const response = await CHBServices.update(applicationData, tenantId);
    return response;
  } catch (error) {
    throw new Error(error?.response?.data?.Errors[0].message);
  }
};

export default ApplicationUpdateActionsCHB;
