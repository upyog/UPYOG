import { mutationTemplate } from "../../common/mutationTemplate";
import ApplicationUpdateActions from "../../services/molecules/OBPS/ApplicationUpdateActions";

const useApplicationActions = (tenantId) => {
  return mutationTemplate({
    mutationFn: (data) =>
      ApplicationUpdateActions(data, tenantId),
  });
};

export default useApplicationActions;