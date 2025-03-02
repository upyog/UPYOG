import { useMutation } from "react-query";
// import ApplicationUpdateActionsPTR from "../../services/molecules/PTR/ApplicationUpdateActionsPTR";
import ApplicationUpdateActionsEW from "../../services/molecules/EW/ApplicationUpdateActionsEW"

const useEWApplicationAction = (tenantId) => {

  return useMutation((applicationData) => ApplicationUpdateActionsEW(applicationData, tenantId));

};
// console.log("Mutation called with applicationData:", applicationData);

export default useEWApplicationAction;
