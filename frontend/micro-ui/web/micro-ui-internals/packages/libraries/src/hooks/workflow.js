import { useQuery, useQueryClient } from "react-query";
import { WorkflowService } from "../services/elements/WorkFlow";
import React, { useState, useEffect } from "react";
const useWorkflowDetails = ({ tenantId, id, moduleCode, role = "CITIZEN", serviceData = {}, getStaleData,  getTripData = false,config }) => {
  const [trigger, setTrigger] = useState(Date.now());
  const queryClient = useQueryClient();
  useEffect(()=>{
    setTrigger(Date.now()); 
   },[])

  const staleDataConfig = { staleTime: Infinity };

  const { isLoading, error, isError, data } = useQuery(
    ["workFlowDetails", tenantId, id, moduleCode, role, config, trigger],
    () => WorkflowService.getDetailsById({ tenantId, id, moduleCode, role, getTripData }),
    getStaleData ? { ...staleDataConfig, ...config } : config
  );

  if (getStaleData) return { isLoading, error, isError, data };

  return { isLoading, error, isError, data, revalidate: () => queryClient.invalidateQueries(["workFlowDetails", tenantId, id, moduleCode, role]) };
};

export default useWorkflowDetails;
