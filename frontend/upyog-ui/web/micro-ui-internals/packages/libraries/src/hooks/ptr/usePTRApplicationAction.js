import { mutationTemplate } from "../../common/mutationTemplate";
import ApplicationUpdateActionsPTR from "../../services/molecules/PTR/ApplicationUpdateActionsPTR";

const usePTRApplicationAction = (tenantId) => {
  const mutationFn = (applicationData) => ApplicationUpdateActionsPTR(applicationData, tenantId);
  return mutationTemplate({ mutationFn });
};

export default usePTRApplicationAction;
