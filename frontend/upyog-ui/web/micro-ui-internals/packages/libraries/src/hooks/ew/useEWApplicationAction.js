import { useMutation } from "react-query";
import ApplicationUpdateActionsEW from "../../services/molecules/EW/ApplicationUpdateActionsEW";

/**
 * Custom hook for managing E-Waste application workflow actions.
 * Provides mutation capabilities for updating application status, approvals,
 * and other workflow-related actions.
 *
 * @param {string} tenantId Unique identifier for the tenant/city
 * @returns {Object} Mutation object containing isLoading, isError, data and mutation function
 *
 * @example
 * const { mutate, isLoading } = useEWApplicationAction("pb.amritsar");
 * 
 * // Update application status
 * mutate({
 *   applicationNumber: "EW-2023-01-29-000123",
 *   action: "APPROVE",
 *   comment: "Application approved"
 * });
 */
const useEWApplicationAction = (tenantId) => {
  return useMutation((applicationData) => ApplicationUpdateActionsEW(applicationData, tenantId));
};

export default useEWApplicationAction;
