import { PTSearch } from "../../services/molecules/PT/Search";
import { queryTemplate } from "../../common/queryTemplate";

const useApplicationDetail = (t, tenantId, propertyIds, config = {}, userType, args) => {
  const defaultSelect = (data) => {
    let applicationDetails = data.applicationDetails.map((obj) => {
      const { additionalDetails, title } = obj;
      if (title === "PT_OWNERSHIP_INFO_SUB_HEADER") {
        additionalDetails.owners = additionalDetails.owners.filter((e) => e.status === "ACTIVE");
        const values = additionalDetails.documents[0]?.values?.filter((e) => e.status === "ACTIVE");
        additionalDetails.documents[0] = { ...additionalDetails.documents[0], values };
        return { ...obj, additionalDetails };
      }
      return obj;
    });
    data.applicationData.units=data?.applicationData?.units?.filter(unit=>unit?.active)||[];
    return { ...data, applicationDetails };
  };

  const queryKey = ["APPLICATION_SEARCH", "PT_SEARCH", propertyIds, userType, args];

  const queryFn = () => PTSearch.applicationDetails(t, tenantId, propertyIds, userType, args);

  const select = defaultSelect;

  return queryTemplate({
    queryKey,
    queryFn,
    select,
    config,
  });
  // config
};

export default useApplicationDetail;
