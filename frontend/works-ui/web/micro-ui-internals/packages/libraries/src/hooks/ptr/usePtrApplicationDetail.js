import { PTRSearch } from "../../services/molecules/PTR/Search";
import { queryTemplate } from "../../common/queryTemplate";
import { usePetColors } from "./usePetColors";

const usePtrApplicationDetail = (t, tenantId, applicationNumber, config = {}, userType, args) => {
  const defaultSelect = (data) => {
    let applicationDetails = data.applicationDetails;
    return {
      applicationData: data,
      applicationDetails,
    };
  };

  const pet_color = usePetColors().data;

  return queryTemplate({
    queryKey: ["APPLICATION_SEARCH", "PT_SEARCH", applicationNumber, pet_color, userType, args],
    queryFn: () => PTRSearch.applicationDetails(t, tenantId, applicationNumber, pet_color, userType, args),
    select: defaultSelect,
    config,
  });
};

export default usePtrApplicationDetail;
