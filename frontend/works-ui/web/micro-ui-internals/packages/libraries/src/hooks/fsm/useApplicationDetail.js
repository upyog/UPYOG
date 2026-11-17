import { queryTemplate } from "../../common/queryTemplate";
import { Search } from "../../services/molecules/FSM/Search";

const useApplicationDetail = (
  t,
  tenantId,
  applicationNos,
  config = {},
  userType
) => {
  const queryKey = [
    "FSM_APPLICATION_DETAIL",
    tenantId,
    JSON.stringify(applicationNos),
    userType,
  ];

  const queryFn = () =>
    Search.applicationDetails(t, tenantId, applicationNos, userType);

  return queryTemplate({
    queryKey,
    queryFn,
    config,
  });
};

export default useApplicationDetail;