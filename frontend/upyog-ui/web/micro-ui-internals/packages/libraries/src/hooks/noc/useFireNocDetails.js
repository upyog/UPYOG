import { FireNocSearch } from "../../services/molecules/NOC/FireNocSearch";
import { queryTemplate } from "../../common/queryTemplate";

// Custom hook to search the specific applications as per applicationNumber to show the detaisl in the Employee side of the My Application
const useFireNocDetails = (t, tenantId, applicationNumber, config = {}, userType, args) => {
  const defaultSelect = (data) => {
    let applicationDetails = data.applicationDetails;
    return {
      applicationData: data,
      applicationDetails,
    };
  };

  // passing the data in FireNocSearch component
  return queryTemplate({
    queryKey: ["APPLICATION_SEARCH", "FIRENOC_SEARCH", applicationNumber, userType, args],
    queryFn: () => FireNocSearch.applicationDetails(t, tenantId, applicationNumber, userType, args),
    select: defaultSelect,
    config,
  });
};

export default useFireNocDetails;
