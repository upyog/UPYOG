import { useMutation } from "react-query";
import { FSMService } from "../../services/elements/FSM";

const useWorkerCreate = (tenantId) => {
  return useMutation((workerData) => WorkerCreateActions(workerData, tenantId));
};

const WorkerCreateActions = async (workerData, tenantId) => {
  try {
    const response = await FSMService.createWorker({ details: workerData, tenantId });
    return response;
  } catch (error) {
    throw new Error(error?.response?.data?.Errors[0].message);
  }
};

export default useWorkerCreate;
