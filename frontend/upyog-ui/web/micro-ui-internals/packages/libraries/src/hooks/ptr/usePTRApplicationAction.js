import { useMutation } from "react-query";
<<<<<<< HEAD
// import ApplicationUpdateActionsPTR from "../../services/molecules/PTR/ApplicationUpdateActionsPTR";
import ApplicationUpdateActionsPTR from "../../services/molecules/PTR/ApplicationUpdateActionsPTR"

const usePTRApplicationAction = (tenantId) => {
  
  return useMutation((applicationData) => ApplicationUpdateActionsPTR(applicationData, tenantId));
};

// console.log("hjdfhj", applicationData)

=======
import ApplicationUpdateActionsPTR from "../../services/molecules/PTR/ApplicationUpdateActionsPTR"

const usePTRApplicationAction = (tenantId) => {
  return useMutation((applicationData) => ApplicationUpdateActionsPTR(applicationData, tenantId));
};

>>>>>>> master-LTS
export default usePTRApplicationAction;
