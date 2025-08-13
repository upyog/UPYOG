import { EwService } from "../../elements/EW";

/**
 * Updates E-Waste application data through the EwService.
 * Handles API communication and error processing for application updates.
 *
 * @param {Object} applicationData Updated application data to be saved
 * @param {string} tenantId Tenant/city identifier
 * @returns {Promise<Object>} Response from the update operation
 * @throws {Error} When update operation fails with service error
 *
 * @example
 * try {
 *   const response = await ApplicationUpdateActionsEW(
 *     { requestId: "EW-2023-01", status: "APPROVED" },
 *     "pb.amritsar"
 *   );
 * } catch (error) {
 *   console.error("Update failed:", error.message);
 * }
 */
const ApplicationUpdateActionsEW = async (applicationData, tenantId) => {
  try {
    const response = await EwService.update(applicationData, tenantId);
    return response;
  } catch (error) {
    throw new Error(error?.response?.data?.Errors[0].message);
  }
};

export default ApplicationUpdateActionsEW;