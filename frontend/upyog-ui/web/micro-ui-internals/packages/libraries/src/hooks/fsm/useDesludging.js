import { mutationTemplate } from "../../common/mutationTemplate";
import { FileDesludging } from "../../services/molecules/FSM/FileDesludging";

const useDesludging = (tenantId) => {
  const mutationFn = (data) =>
    FileDesludging.create(tenantId, data);

  return mutationTemplate({
    mutationFn,
  });
};

export default useDesludging;