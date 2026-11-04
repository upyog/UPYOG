import { WTSearch } from "../../services/molecules/WT/Search";
import { queryTemplate } from "../../common/queryTemplate";

const useWTApplicationDetail = (t, tenantId, applicationNo, config = {}, userType, args) => {
  const defaultSelect = (data) => {
    let applicationDetails = data.applicationDetails.map((obj) => obj);
    return { applicationData: data, applicationDetails };
  };

  return queryTemplate({
    queryKey: ["APPLICATION_SEARCH", "WT_SEARCH", applicationNo, userType, args],
    queryFn: () => WTSearch.applicationDetails(t, tenantId, applicationNo, userType, args),
    select: defaultSelect,
    config,
  });
};

export default useWTApplicationDetail;
