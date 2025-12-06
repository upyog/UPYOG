import { useMutation } from "react-query";

/**
 * useChbApplicationAction Hook
 * 
 * This custom hook is responsible for handling application actions in the CHB (Community Hall Booking) module.
 * It provides a mutation function to update CHB application data using the `ApplicationUpdateActionsCHB` service.
 * 
 * Parameters:
 * - `tenantId`: The tenant ID for which the application action is being performed.
 * 
 * Logic:
 * - Uses the `useMutation` hook from `react-query` to create a mutation for updating CHB application data.
 * - The mutation function calls the `ApplicationUpdateActionsCHB` service with the provided `applicationData` and `tenantId`.
 * 
 * Returns:
 * - A mutation object from `react-query` that includes:
 *    - `mutate`: Function to trigger the mutation.
 *    - `isLoading`: Boolean indicating whether the mutation is in progress.
 *    - `isError`: Boolean indicating whether the mutation resulted in an error.
 *    - `data`: The response data from the mutation.
 *    - `error`: The error object if the mutation fails.
 * 
 * Usage:
 * - This hook can be used in components to perform application update actions in the CHB module.
 * - Example:
 *    const { mutate, isLoading, isError } = useChbApplicationAction(tenantId);
 *    mutate(applicationData);
 */
import ApplicationUpdateActionsCHB from "../../services/molecules/CHB/ApplicationUpdateActionsCHB";

const useChbApplicationAction = (tenantId) => {
  
  return useMutation((applicationData) => ApplicationUpdateActionsCHB(applicationData, tenantId));
};

export default useChbApplicationAction;
