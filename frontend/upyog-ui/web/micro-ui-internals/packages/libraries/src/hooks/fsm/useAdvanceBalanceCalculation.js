import { queryTemplate } from "../../common/queryTemplate";
import { FSMService } from "../../services/elements/FSM";

const useAdvanceBalanceCalulation = ({ tenantId, filters }) => {
  const queryKey = [
    "FSM_ADVANCE_BALANCE",
    tenantId,
    JSON.stringify(filters),
  ];

  const queryFn = () =>
    FSMService.advanceBalanceCalculate({ tenantId, filters });

  return queryTemplate({
    queryKey,
    queryFn,
  });
};

export default useAdvanceBalanceCalulation;