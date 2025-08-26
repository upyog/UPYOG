import { useQuery, useMutation } from "react-query";
import { EwService } from "../../services/elements/EW";

/**
 * Custom hook for creating and updating E-Waste applications.
 * Provides mutation capabilities for submitting new applications
 * or modifying existing ones.
 *
 * @param {string} tenantId Tenant/city identifier
 * @param {boolean} type If true, creates new application; if false, updates existing one
 * @returns {Object} Mutation object with create/update capabilities
 *
 * @example
 * // Creating a new application
 * const { mutate: createEW } = useEwCreateAPI("pb.amritsar");
 * createEW(applicationData);
 *
 * // Updating an existing application
 * const { mutate: updateEW } = useEwCreateAPI("pb.amritsar", false);
 * updateEW(modifiedData);
 */
export const useEwCreateAPI = (tenantId, type = true) => {
  if (type) {
    return useMutation((data) => EwService.create(data, tenantId));
  } else {
    return useMutation((data) => EwService.update(data, tenantId));
  }
};

export default useEwCreateAPI;
