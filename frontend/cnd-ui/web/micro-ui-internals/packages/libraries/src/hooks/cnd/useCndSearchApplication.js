/**
 * Following hook is used to get data of applications from backend in CND module and returns the data in an object "SVDetail"
 */
import { useQueryClient } from "@tanstack/react-query";
import { queryTemplate } from "../../common/queryTemplate";


const useCndSearchApplication = ({ tenantId, filters, auth, searchedFrom = "" }, config = {}) => {
  const client = useQueryClient();
  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };

  // Selects the data to be returned by the hook
  const queryKey = [
    "cndSearchList",
    tenantId,
    JSON.stringify(filters),
    JSON.stringify(auth)
  ];

  const queryFn = () => Digit.CNDService.search(args);

  const select = (data) => {
    if (data.cndApplicationDetail.length > 0) {
      data.cndApplicationDetail[0].cndApplicationDetail = data.cndApplicationDetail[0].cndApplicationDetail || [];
    }
    return data;
  };

  const query = queryTemplate({
    queryKey,
    queryFn,
    select,
    config
  });

  return {
    ...query,
    revalidate: () => client.invalidateQueries({ queryKey })
  };
};

export default useCndSearchApplication;
