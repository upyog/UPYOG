import { mutationTemplate } from "../../common/mutationTemplate";
import ApplicationUpdateActions from "../../services/molecules/TL/ApplicationUpdateActions";

const useApplicationActions = (tenantId) => {
  const mutationFn = (applicationData) => ApplicationUpdateActions(applicationData, tenantId);
  return mutationTemplate({ mutationFn });
};

export default useApplicationActions;
