import { mutationTemplate } from "../../common/mutationTemplate";
import ApplicationUpdateActions from "../../services/molecules/FSM/ApplicationUpdateActions";

const useApplicationActions = (tenantId) => {
  const mutationFn = (applicationData) =>
    ApplicationUpdateActions(applicationData, tenantId);

  return mutationTemplate({
    mutationFn,
  });
};

export default useApplicationActions;