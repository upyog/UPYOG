// Importing the useMutation hook from react-query for handling mutations
import { useMutation } from "react-query";
// Importing the function to update E-Waste application actions
import ApplicationUpdateActionsEW from "../../services/molecules/EW/ApplicationUpdateActionsEW";

// Custom hook to handle E-Waste application actions
const useEWApplicationAction = (tenantId) => {
  // Using the useMutation hook to call the ApplicationUpdateActionsEW function
  return useMutation((applicationData) => ApplicationUpdateActionsEW(applicationData, tenantId));
};

// Exporting the custom hook for use in other components
export default useEWApplicationAction;
