// Importing the useQuery and useMutation hooks from react-query for handling API calls
import { useQuery, useMutation } from "react-query";
// Importing the EwService for creating or updating E-Waste data
import { EwService } from "../../services/elements/EW";

// Custom hook to handle the creation or update of E-Waste data
export const useEwCreateAPI = (tenantId, type = true) => {
  // If type is true, use the create API; otherwise, use the update API
  if (type) {
    return useMutation((data) => EwService.create(data, tenantId)); // Mutation for creating E-Waste data
  } else {
    return useMutation((data) => EwService.update(data, tenantId)); // Mutation for updating E-Waste data
  }
};

export default useEwCreateAPI; // Exporting the custom hook for use in other components
