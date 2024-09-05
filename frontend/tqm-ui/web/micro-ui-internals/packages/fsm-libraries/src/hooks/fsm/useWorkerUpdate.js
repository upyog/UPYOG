import { useMutation } from "react-query";
import { FSMService } from "../../services/elements/FSM";

const useWorkerUpdate = (tenantId) => {
  return useMutation((workerData) => WorkerUpdateActions(workerData, tenantId));
};

const WorkerUpdateActions = async (workerData, tenantId) => {
  try {
    const response = await FSMService.updateWorker(workerData, tenantId);
    return response;
  } catch (error) {
    throw new Error(error?.response?.data?.Errors[0].message);
  }
};

export default useWorkerUpdate;
