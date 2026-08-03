import { mutationTemplate } from "../../common/mutationTemplate";
import { CHBServices } from "../../services/elements/CHB";

/**
 * useChbCreateAPI Hook
 * 
 * This custom hook is responsible for handling the creation and updating of CHB (Community Hall Booking) applications.
 * 
 * Parameters:
 * - `tenantId`: The tenant ID for which the operation is being performed.
 * - `type`: Boolean indicating the operation type (true for creation, false for update).
 * 
 * Logic:
 * - Uses the `useMutation` hook from `react-query` to perform API calls.
 * - Calls `CHBServices.create` for creating applications when `type` is true.
 * - Calls `CHBServices.update` for updating applications when `type` is false.
 * 
 * Returns:
 * - A mutation object from `react-query` for performing the create or update operation.
 */
export const useChbCreateAPI = (tenantId, type = true) => {
  const mutationFn = (data) =>
    type
      ? CHBServices.create(data, tenantId)
      : CHBServices.update(data, tenantId);

  return mutationTemplate({ mutationFn });
};

export default useChbCreateAPI;