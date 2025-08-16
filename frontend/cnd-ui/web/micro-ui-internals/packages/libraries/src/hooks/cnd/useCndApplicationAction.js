import { useMutation } from "react-query";
import ApplicationUpdateActionsCND from "../../services/molecules/CND/ApplicationUpdateActionsCND";

/**
 * Custom hook that provides mutation functionality for CND application actions
 * Uses react-query's useMutation hook to handle API calls
 * 
 * @param {string} tenantId - The tenant ID to be used in the application update action
 * @returns {Object} - The mutation object from react-query with methods like mutate, isLoading, etc.
 */

const useCndApplicationAction = (tenantId) => {
  // Create and return a mutation function that will call ApplicationUpdateActionsCND
  // when invoked with application data
  return useMutation((applicationData) => ApplicationUpdateActionsCND(applicationData, tenantId));
};

export default useCndApplicationAction;
