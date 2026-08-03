import { mutationTemplate } from "../../common/mutationTemplate";
import { FSMService } from "../../services/elements/FSM";

const useApplicationUpdate = (tenantId) => {
  const mutationFn = (details) =>
    FSMService.update(details, tenantId);

  return mutationTemplate({
    mutationFn,
  });
};

export default useApplicationUpdate;