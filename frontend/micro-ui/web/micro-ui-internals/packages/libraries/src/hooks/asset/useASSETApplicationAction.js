import { useMutation } from "react-query";

import ApplicationUpdateActionsASSET from "../../services/molecules/ASSET/ApplicationUpdateActionsASSET";

const useASSETApplicationAction = (tenantId) => {
  
  return useMutation((applicationData) =>{
    console.log("applicationData in hook",applicationData);
    ApplicationUpdateActionsASSET(applicationData, tenantId)} );
};



export default useASSETApplicationAction;
