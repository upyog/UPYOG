import { mutationTemplate } from "../../common/mutationTemplate";
import FireNocApplicationActions from "../../services/molecules/NOC/FireNocApplicationActions";

// To get the actions employee can take in the application
const useFireNocApplicationAction = (tenantId) => {
  const mutationFn = (applicationData) => FireNocApplicationActions(applicationData, tenantId);
  return mutationTemplate({ mutationFn });
};

export default useFireNocApplicationAction;
