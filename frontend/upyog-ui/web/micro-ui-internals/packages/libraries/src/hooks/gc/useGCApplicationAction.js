import { mutationTemplate } from "../../common/mutationTemplate";
import ApplicationUpdateActionGC from "../../services/molecules/GC/ApplicationUpdateActionGC";

const useGCApplicationAction = (tenantId) => {
  const mutationFn = (applicationData) => ApplicationUpdateActionGC(applicationData, tenantId);
  return mutationTemplate({ mutationFn });
};

export default useGCApplicationAction;
