import { PTRSearch } from "../../services/molecules/PTR/Search";
import { useQuery } from "react-query";
import { usePetColors } from "./usePetColors";

const usePtrApplicationDetail = (t, tenantId, applicationNumber, config = {}, userType, args) => {
  
  const defaultSelect = (data) => {
    let applicationDetails = data.applicationDetails
    return {
      applicationData : data,
      applicationDetails
    }
  };

  const pet_color = usePetColors().data;
  return useQuery(
    ["APPLICATION_SEARCH", "PT_SEARCH", applicationNumber,pet_color, userType, args],
    () => PTRSearch.applicationDetails(t, tenantId, applicationNumber,pet_color, userType, args),
    { select: defaultSelect, ...config }
 
  );
};

export default usePtrApplicationDetail;
