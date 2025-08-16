import { PTRSearch } from "../../services/molecules/PTR/Search";
import { useQuery } from "react-query";

const usePtrApplicationDetail = (t, tenantId, applicationNumber, config = {}, userType, args) => {
  
  const defaultSelect = (data) => {
    // console.log("####",data)
     let applicationDetails = data.applicationDetails.map((obj) => {
    
      return obj;
    });
    

    return {
      applicationData : data,
      applicationDetails
    }
  };
  // hook to get the pet colors master data
  let { data: pet_color } = Digit.Hooks.useCustomMDMSV2(Digit.ULBService.getStateId(), "PetService", [{ name: "PetColor" }],
  {
    select: (data) => {
      const formattedData = data?.["PetService"]?.["PetColor"].map((petone) => {
        return { i18nKey: `${petone.colourName}`, colourCode: `${petone.colourCode}`, code: `${petone.colourName}`, active: `${petone.active}` };
      })
      return formattedData;
    },
  });
  return useQuery(
    ["APPLICATION_SEARCH", "PT_SEARCH", applicationNumber,pet_color, userType, args],
    () => PTRSearch.applicationDetails(t, tenantId, applicationNumber,pet_color, userType, args),
    { select: defaultSelect, ...config }
 
  );
};

export default usePtrApplicationDetail;
