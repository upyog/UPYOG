import { TLSearch } from "../../services/molecules/TL/Search";
import { queryTemplate } from "../../common/queryTemplate";

const useApplicationDetail = (t, tenantId, applicationNumber, config = {}, userType) => {
  const EditRenewalApplastModifiedTime = Digit.SessionStorage.get("EditRenewalApplastModifiedTime");
  return queryTemplate({
    queryKey: ["APPLICATION_SEARCH", "TL_SEARCH", applicationNumber, userType, EditRenewalApplastModifiedTime],
    queryFn: () => TLSearch.applicationDetails(t, tenantId, applicationNumber, userType),
    config,
  });
};

export default useApplicationDetail;
