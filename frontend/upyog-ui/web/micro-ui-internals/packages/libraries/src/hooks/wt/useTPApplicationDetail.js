import { TPSearch } from "../../services/molecules/WT/TPSearch";
import { queryTemplate } from "../../common/queryTemplate";

/*Custom React hook for fetching Tree Pruning (TP) application details.
  - Calls `TPSearch.applicationDetails` to retrieve application details based on provided parameters.
  - Uses `react-query`'s `useQuery` to manage the fetching, caching, and updating of the data.
  - Transforms the response structure before returning it..*/
const useTPApplicationDetail = (t, tenantId, applicationNo, config = {}, userType, args) => {
  const defaultSelect = (data) => {
    let applicationDetails = data.applicationDetails.map((obj) => obj);
    return { applicationData: data, applicationDetails };
  };

  return queryTemplate({
    queryKey: [applicationNo, userType, args],
    queryFn: () => TPSearch.applicationDetails(t, tenantId, applicationNo, userType, args),
    select: defaultSelect,
    config,
  });
};

export default useTPApplicationDetail;
