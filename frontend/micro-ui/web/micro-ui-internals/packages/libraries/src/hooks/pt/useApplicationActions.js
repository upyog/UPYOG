import { useMutation } from "react-query";
import ApplicationUpdateActions from "../../services/molecules/PT/ApplicationUpdateActions";
import AssessmentUpdateActions from "../../services/molecules/PT/AssessmentUpdateActions";

const useApplicationActions = (tenantId, action='') => {
  if(action && action=='ASMT') {
    return useMutation((applicationData) => AssessmentUpdateActions(applicationData, tenantId));
  } else {
    return useMutation((applicationData) => ApplicationUpdateActions(applicationData, tenantId));
  }
};

export default useApplicationActions;
