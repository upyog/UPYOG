import { GCServices } from "../../elements/GC"


/**
 * ApplicationUpdateActionGC Function
 * 
 * This function is responsible for updating GC (Garbage Collection) application data.
 * 
 * Parameters:
 * - `applicationData`: The application data to be updated.
 * - `tenantId`: The tenant ID for which the update is being performed.
 * 
 * Logic:
 * - Calls `GCServices.update` to perform the update operation.
 * - Handles errors by throwing a new error with the message from the API response.
 * 
 * Returns:
 * - The response from the `GCServices.update` API call.
 * 
 * Throws:
 * - An error with the message from the API response if the update fails.
 */
const ApplicationUpdateActionGC = async (applicationData, tenantId) => {
  try {
    const response = await GCServices.update(applicationData, tenantId);
  } catch (error) {
    console.error("[GC] update error:", error);
    throw new Error(error?.response?.data?.Errors?.[0]?.message || error?.message);
  }
};

export default ApplicationUpdateActionGC;
