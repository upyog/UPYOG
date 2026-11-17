import { mutationTemplate } from "../../common/mutationTemplate";
import ApplicationUpdateActions from "../../services/molecules/NOC/ApplicationUpdateActions";

const useNOCApplicationActions = (tenantId) => {
  return mutationTemplate({
    mutationFn: (applicationData) =>
      ApplicationUpdateActions(applicationData, tenantId),
  });
};

export default useNOCApplicationActions;