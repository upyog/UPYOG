import { mutationTemplate } from "../../common/mutationTemplate";
import ApplicationUpdateActionsASSET from "../../services/molecules/ASSET/ApplicationUpdateActionsASSET";

const useASSETApplicationAction = (tenantId) => {
  const mutationFn = (applicationData) => ApplicationUpdateActionsASSET(applicationData, tenantId);

  return mutationTemplate({mutationFn});
  
  // return useMutation((applicationData) =>{
  //   ApplicationUpdateActionsASSET(applicationData, tenantId)} );
};



export default useASSETApplicationAction;
