import { Search } from "../../services/molecules/FSM/Search";
import { useQuery } from "react-query";

const useApplicationDetail = (t, tenantId, applicationNos, config = {}, userType, { getTripData } = false) => {
  return useQuery(["FSM_CITIZEN_SEARCH", applicationNos, userType], () => Search.applicationDetails(t, tenantId, applicationNos, userType, getTripData), config);
};

export default useApplicationDetail;
