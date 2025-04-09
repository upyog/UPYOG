// Importing the EWSearch service for fetching application details
import { EWSearch } from "../../services/molecules/EW/Search";
// Importing the useQuery hook from react-query for handling data fetching
import { useQuery } from "react-query";

// Custom hook to fetch and manage E-Waste application details
const useEwApplicationDetail = (t, tenantId, requestId, config = {}, userType, args) => {
  
  // Function to process and transform the fetched data
  const defaultSelect = (data) => {
    // Map through the application details and return them as-is
    let applicationDetails = data.applicationDetails.map((obj) => {
      return obj;
    });

    // Return the transformed data
    return {
      applicationData: data, // Original application data
      applicationDetails, // Processed application details
    };
  };

  // Using the useQuery hook to fetch application details
  return useQuery(
    ["APP_SEARCH", "EW_SEARCH", requestId, userType, args], // Query key for caching
    () => EWSearch.applicationDetails(t, tenantId, requestId, userType, args), // Function to fetch application details
    { select: defaultSelect, ...config } // Configuration options and data transformation
  );
};

export default useEwApplicationDetail; // Exporting the custom hook for use in other components
