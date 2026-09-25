import { mutationTemplate } from "../../common/mutationTemplate";
import ApplicationUpdateActionsEW from "../../services/molecules/EW/ApplicationUpdateActionsEW";

const useEWApplicationAction = (tenantId) => {
  const mutationFn = (applicationData) =>
    ApplicationUpdateActionsEW(applicationData, tenantId);

  return mutationTemplate({
    mutationFn,
  });
};

export default useEWApplicationAction;