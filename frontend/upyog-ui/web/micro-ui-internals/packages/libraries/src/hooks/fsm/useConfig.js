import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService } from "../../services/elements/MDMS";

const useConfig = (tenantId) => {
  const queryKey = ["FSM_CONFIG", tenantId];

  const queryFn = async () => {
    const res = await MdmsService.getCustomizationConfig(tenantId, "FSM");

    return res["FSM"].Config
      .filter((i) => i.active)
      .reduce((acc, item) => {
        acc[item.code] = {
          override: item.override,
          default: item.default,
          state: item.WFState,
        };
        return acc;
      }, {});
  };

  return queryTemplate({
    queryKey,
    queryFn,
  });
};

export default useConfig;