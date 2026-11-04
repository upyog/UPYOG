import { GCSearch } from "../../services/molecules/GC/Search";
import { queryTemplate } from "../../common/queryTemplate";

const useGCApplicationDetail = (t, tenantId, applicationNo, config = {}, userType, args) => {
  return queryTemplate({
    queryKey: [tenantId, applicationNo, userType, JSON.stringify(args)],
    queryFn: () => GCSearch.applicationDetails(t, tenantId, applicationNo, userType, args),
    select: (data) => ({
      applicationData: data,
    }),
    config,
  });
};

export default useGCApplicationDetail;
