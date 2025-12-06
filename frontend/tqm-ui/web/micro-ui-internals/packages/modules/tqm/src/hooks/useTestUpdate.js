import { useMutation } from "react-query";
import testUpdateService from "./services/testUpdateService";

const useTestUpdate = (tenantId) => {
  return useMutation((testData) => testUpdateService(testData, tenantId));
};

export default useTestUpdate;
