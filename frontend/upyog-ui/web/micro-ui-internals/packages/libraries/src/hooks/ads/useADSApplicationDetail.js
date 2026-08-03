import { ADSSearch } from "../../services/molecules/ADS/Search";
import { queryTemplate } from "../../common/queryTemplate";

const useADSApplicationDetail = (
  t,
  tenantId,
  bookingNo,
  config = {},
  userType,
  args
) => {
  const queryKey = [
    "ADS_APPLICATION_DETAIL",
    tenantId,
    bookingNo,
    userType,
    JSON.stringify(args),
  ];

  const queryFn = () =>
    ADSSearch.applicationDetails(t, tenantId, bookingNo, userType, args);

  const select = (data) => ({
    applicationData: data,
    applicationDetails: data?.applicationDetails || {},
  });

  return queryTemplate({
    queryKey,
    queryFn,
    select,
    config,
  });
};

export default useADSApplicationDetail;