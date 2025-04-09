// Importing the useQuery hook from react-query for handling data fetching
import { useQuery } from "react-query";

// Custom hook to fetch product price data from MDMS (Master Data Management System)
const useProductPriceMDMS = (tenantId, moduleCode, type, config = {}) => {
  
  // Function to fetch product prices using the MDMS service
  const useProductPrices = () => {
    return useQuery(
      "PRODUCT_PRICES", // Query key for caching
      () => Digit.Hooks.useSelectedMDMS().getMasterData(tenantId, moduleCode, "ProductName"), // Function to fetch product price data
      config // Additional configuration options for the query
    );
  };

  // Switch case to handle different types of data fetching
  switch (type) {
    case "ProductName":
      return useProductPrices(); // Fetch product prices if the type is "ProductName"
    default:
      return null; // Return null for unsupported types
  }
};

export default useProductPriceMDMS; // Exporting the custom hook for use in other components
