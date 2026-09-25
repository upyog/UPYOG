import { mutationTemplate } from "../../common/mutationTemplate";
import ApplicationUpdateActionsWT from "../../services/molecules/WT/ApplicationUpdateActionsWT";

const useWTApplicationAction = (tenantId) => {
  return mutationTemplate({ mutationFn: (applicationData) => ApplicationUpdateActionsWT(applicationData, tenantId) });
};

export default useWTApplicationAction;
