import { mutationTemplate } from "../../common/mutationTemplate";
import BPAREGApplicationUpdateActions from "../../services/molecules/OBPS/BPAREGApplicationUpdateActions";

const useBPAREGApplicationActions = (tenantId) => {
  return mutationTemplate({
    mutationFn: (data) =>
      BPAREGApplicationUpdateActions(data, tenantId),
  });
};

export default useBPAREGApplicationActions;