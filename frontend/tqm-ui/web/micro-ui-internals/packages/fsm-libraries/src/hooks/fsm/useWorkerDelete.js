import { useMutation } from "react-query";
import { FSMService } from "../../services/elements/FSM";

const useWorkerDelete = (tenantId) => {
  return useMutation((workerData) => WorkerDeleteActions(workerData, tenantId));
};

const WorkerDeleteActions = async (workerData, tenantId) => {
  try {
    const response = await FSMService.deleteWorker(workerData, tenantId);
    return response;
  } catch (error) {
    throw new Error(error?.response?.data?.Errors[0].message);
  }
};

export default useWorkerDelete;
