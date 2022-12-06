import { CRsearch } from "../../services/molecules/CR/Search";
import { useQuery } from "react-query";

const useApplicationDetail = (t, tenantId, applicationNumber, config = {}, userType) => {
  let EditRenewalApplastModifiedTime = Digit.SessionStorage.get("EditRenewalApplastModifiedTime");
  return useQuery(
    ["APPLICATION_SEARCH", "CRsearch", applicationNumber, userType, EditRenewalApplastModifiedTime],
    () => CRsearch.applicationDetails(t, tenantId, applicationNumber, userType),
    config
  );
};

export default useApplicationDetail;
