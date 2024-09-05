import { useMutation } from "react-query";
import createService from "./services/createService";
const useCreateTest = (tenantId) => {
  return useMutation((testData) => createService(testData, tenantId));
};

export default useCreateTest;
