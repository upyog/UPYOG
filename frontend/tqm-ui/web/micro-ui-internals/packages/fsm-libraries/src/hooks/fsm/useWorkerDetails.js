import { useQuery } from "react-query";
import WorkerDetails from "../../services/molecules/FSM/WorkerDetails";

const useWorkerDetails = (args) => {
  const { tenantId, params, details, config, t } = args;
  return useQuery(["DRIVER_SEARCH", details], () => WorkerDetails({ tenantId, params, details, t }), config);
};

export default useWorkerDetails;
