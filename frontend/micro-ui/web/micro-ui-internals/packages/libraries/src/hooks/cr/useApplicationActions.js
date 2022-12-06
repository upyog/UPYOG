import { useMutation } from "react-query";
import ApplicationUpdateActions from "../../services/molecules/CR/ApplicationUpdateActions";

const useApplicationActions = (tenantId) => {
  return useMutation((applicationData) => ApplicationUpdateActions(applicationData, tenantId));
};

export default useApplicationActions;
