import { CNDService } from "../../elements/CND";

/**
* Updates a CND application with the provided data
* 
* @param {object} applicationData - The application data to be updated
* @param {string} tenantId - The tenant identifier for the application
* @returns {object} - The response from the update operation
* @throws {Error} - Throws an error with the message from the backend if the update fails
*/
const ApplicationUpdateActionsCND = async (applicationData, tenantId) => {
  try {
    const response = await CNDService.update(applicationData, tenantId);
    return response;
  } catch (error) {
    throw new Error(error?.response?.data?.Errors[0].message);
  }
};

export default ApplicationUpdateActionsCND;
