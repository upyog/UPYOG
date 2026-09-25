import { queryTemplate } from "../../common/queryTemplate";
import { NOCSearch } from "../../services/molecules/NOC/Search";

const useNOCDetails = (
  t,
  tenantId,
  applicationNumber,
  config = {},
  userType
) => {
  const EditRenewalApplastModifiedTime =
    Digit.SessionStorage.get("EditRenewalApplastModifiedTime");

  const queryKey = [
    "NOC_APPLICATION_DETAIL",
    tenantId,
    applicationNumber,
    userType,
    EditRenewalApplastModifiedTime,
  ];

  const queryFn = () =>
    NOCSearch.applicationDetails(
      t,
      tenantId,
      applicationNumber,
      userType
    );

  return queryTemplate({
    queryKey,
    queryFn,
    config,
  });
};

export default useNOCDetails;