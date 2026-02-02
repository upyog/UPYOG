import { useMutation } from "react-query";
import ApplicationUpdateActions from "../../services/molecules/PT/ApplicationUpdateActions";
import AssessmentUpdateActions from "../../services/molecules/PT/AssessmentUpdateActions";
import AppealUpdateActions from "../../services/molecules/PT/AppealUpdateActions";

const useApplicationActions = (tenantId, action='') => {
  if(action && action=='ASMT') {
    return useMutation((applicationData) => AssessmentUpdateActions(applicationData, tenantId));
  } else if(action && action=='PT.APPEAL') {
    return useMutation((applicationData) => AppealUpdateActions(applicationData, tenantId));
  } else {
    return useMutation((applicationData) => ApplicationUpdateActions(applicationData, tenantId));
  }
};

export default useApplicationActions;
