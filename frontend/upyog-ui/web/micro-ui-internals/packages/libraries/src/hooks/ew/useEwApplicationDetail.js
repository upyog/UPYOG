import { EWSearch } from "../../services/molecules/EW/Search";
import { queryTemplate } from "../../common/queryTemplate";

const useEwApplicationDetail = (
  t,
  tenantId,
  requestId,
  config = {},
  userType,
  args
) => {
  // stable queryKey
  const queryKey = [
    "EW_APPLICATION_DETAIL",
    tenantId,
    requestId,
    userType,
    JSON.stringify(args),
  ];

  // queryFn
  const queryFn = () =>
    EWSearch.applicationDetails(t, tenantId, requestId, userType, args);

  // select
  const select = (data) => {
    const applicationDetails =
      data?.applicationDetails?.map((obj) => obj) || [];

    return {
      applicationData: data,
      applicationDetails,
    };
  };

  return queryTemplate({
    queryKey,
    queryFn,
    select,
    config,
  });
};

export default useEwApplicationDetail;