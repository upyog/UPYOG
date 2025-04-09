// Importing the useQuery and useQueryClient hooks from react-query for handling data fetching and cache management
import { useQuery, useQueryClient } from "react-query";

// Custom hook to perform a search for E-Waste applications
const useEWSearch = ({ tenantId, filters, auth, searchedFrom = "" }, config = {}) => {
  const client = useQueryClient(); // Initializing the query client for cache management

  // Arguments to be passed to the search API
  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };

  // Function to process and transform the fetched data
  const defaultSelect = (data) => {
    if (data.EwasteApplication.length > 0) data.EwasteApplication[0] = data.EwasteApplication[0] || []; // Ensure the first application exists
    return data; // Return the processed data
  };

  // Using the useQuery hook to fetch E-Waste application data
  const { isLoading, error, data, isSuccess } = useQuery(
    ["ewSearchList", tenantId, filters, auth, config], // Query key for caching
    () => Digit.EwService.search(args), // Function to fetch the search results
    {
      select: defaultSelect, // Data transformation function
      ...config, // Additional configuration options
    }
  );

  // Returning the query state and a function to revalidate the query
  return {
    isLoading, // Indicates if the query is loading
    error, // Contains any error encountered during the query
    data, // The fetched data
    isSuccess, // Indicates if the query was successful
    revalidate: () => client.invalidateQueries(["ewSearchList", tenantId, filters, auth]), // Function to invalidate and refetch the query
  };
};

export default useEWSearch; // Exporting the custom hook for use in other components
