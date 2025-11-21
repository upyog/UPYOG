import { useMutation } from "react-query";

import ApplicationUpdateActionsASSET from "../../services/molecules/ASSET/ApplicationUpdateActionsASSET";

const useASSETApplicationAction = (tenantId) => {
  
  return useMutation((applicationData) =>{
    ApplicationUpdateActionsASSET(applicationData, tenantId)} );
};



export default useASSETApplicationAction;
