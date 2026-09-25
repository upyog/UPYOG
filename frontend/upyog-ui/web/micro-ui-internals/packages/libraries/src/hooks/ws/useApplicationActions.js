import { mutationTemplate } from "../../common/mutationTemplate";
import ApplicationUpdateActions from "../../services/molecules/WS/ApplicationUpdateActions";

const useApplicationActions = (businessService) => {
  return mutationTemplate({ mutationFn: (applicationData) => ApplicationUpdateActions(applicationData, businessService) });
};

export default useApplicationActions;
