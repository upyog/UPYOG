import { PTRSearch } from "../../services/molecules/PTR/Search";
import { useQuery } from "react-query";
<<<<<<< HEAD
=======
import { usePetColors } from "./usePetColors";
>>>>>>> master-LTS

const usePtrApplicationDetail = (t, tenantId, applicationNumber, config = {}, userType, args) => {
  
  const defaultSelect = (data) => {
<<<<<<< HEAD
    // console.log("####",data)
     let applicationDetails = data.applicationDetails.map((obj) => {
    
      return obj;
    });
    

=======
    let applicationDetails = data.applicationDetails
>>>>>>> master-LTS
    return {
      applicationData : data,
      applicationDetails
    }
  };

<<<<<<< HEAD
  return useQuery(
    ["APPLICATION_SEARCH", "PT_SEARCH", applicationNumber, userType, args],
    () => PTRSearch.applicationDetails(t, tenantId, applicationNumber, userType, args),
=======
  const pet_color = usePetColors().data;
  return useQuery(
    ["APPLICATION_SEARCH", "PT_SEARCH", applicationNumber,pet_color, userType, args],
    () => PTRSearch.applicationDetails(t, tenantId, applicationNumber,pet_color, userType, args),
>>>>>>> master-LTS
    { select: defaultSelect, ...config }
 
  );
};

export default usePtrApplicationDetail;
