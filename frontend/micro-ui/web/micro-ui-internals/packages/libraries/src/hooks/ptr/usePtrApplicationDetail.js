import { PTRSearch } from "../../services/molecules/PTR/Search";
import { useQuery } from "react-query";

// Custom hook to fetch details of a PTR application
const usePtrApplicationDetail = (t, tenantId, applicationNumber, config = {}, userType, args) => {
  
  // Function to format the application data received from the API
  const defaultSelect = (data) => {
     let applicationDetails = data.applicationDetails.map((obj) => {
      return obj;
    });

    return {
      applicationData : data,
      applicationDetails
    }
  };

  // hook to get the pet colors master data
  let { data: pet_color } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "PetService", [{ name: "PetColor" }],
  {
    select: (data) => {
      const formattedData = data?.["PetService"]?.["PetColor"].map((petone) => {
        return { i18nKey: `${petone.colourName}`, colourCode: `${petone.colourCode}`, code: `${petone.colourName}`, active: `${petone.active}` };
      })
      return formattedData;
    },
  });

  // Using the useQuery hook to fetch application details
  return useQuery(
    ["APPLICATION_SEARCH", "PT_SEARCH", applicationNumber, pet_color, userType, args],
    () => PTRSearch.applicationDetails(t, tenantId, applicationNumber, pet_color, userType, args),
    { select: defaultSelect, ...config }
 
  );
};

export default usePtrApplicationDetail;
