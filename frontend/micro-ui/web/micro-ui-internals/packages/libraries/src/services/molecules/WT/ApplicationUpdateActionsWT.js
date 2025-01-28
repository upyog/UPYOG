import { WTService } from "../../elements/WT"

/**
 * Function to handle the application update action for Water Tanker (WT).
 * 
 * This function uses the `WTService.update` method to send the update request
 * with the provided `applicationData` and `tenantId`. It handles the response 
 * and any potential errors that may occur during the process.
 * 
 * @param {Object} applicationData - The data associated with the application to update.
 * @param {string} tenantId - The tenant ID to associate with the update request.
 * @returns {Promise} Resolves with the response from the `WTService.update` call.
 * @throws {Error} Throws an error if the update request fails, including error message details.
 */

const ApplicationUpdateActionsWT = async (applicationData, tenantId) => {
  
  
  try {
    const response = await WTService.update(applicationData, tenantId);
    return response;
  } catch (error) {
    throw new Error(error?.response?.data?.Errors[0].message);
  }
};

export default ApplicationUpdateActionsWT;
