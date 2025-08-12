import { useMutation } from "react-query";

import ApplicationUpdateActionsCHB from "../../services/molecules/CHB/ApplicationUpdateActionsCHB";

const useChbApplicationAction = (tenantId) => {
  
  return useMutation((applicationData) => ApplicationUpdateActionsCHB(applicationData, tenantId));
};

export default useChbApplicationAction;
