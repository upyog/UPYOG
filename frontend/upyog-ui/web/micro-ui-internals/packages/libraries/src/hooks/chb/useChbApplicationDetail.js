import { CHBSearch } from "../../services/molecules/CHB/Search";
import { queryTemplate } from "../../common/queryTemplate";


/**
 * useChbApplicationDetail Hook
 * 
 * This custom hook is responsible for fetching the details of a specific CHB (Community Hall Booking) application.
 * It uses the `CHBSearch` service to retrieve application data and processes it for use in components.
 * 
 * Parameters:
 * - `t`: Translation function for internationalization.
 * - `tenantId`: The tenant ID for which the application details are being fetched.
 * - `applicationNo`: The application number of the CHB application to be fetched.
 * - `config`: Optional configuration object for the `useQuery` hook (e.g., for caching, refetching).
 * - `userType`: The type of user (e.g., "citizen" or "employee").
 * - `args`: Additional arguments to be passed to the `CHBSearch.applicationDetails` service.
 * 
 * Logic:
 * - Uses the `useQuery` hook from `react-query` to fetch application details.
 * - The query key is an array containing identifiers such as "APPLICATION_SEARCH", "CHB_SEARCH", `applicationNo`, `userType`, and `args`.
 * - Calls the `CHBSearch.applicationDetails` service to fetch application data.
 * - Processes the fetched data using the `defaultSelect` function:
 *    - Maps over the `applicationDetails` array in the response to extract and return the details.
 *    - Returns an object containing:
 *        - `applicationData`: The raw application data from the API.
 *        - `applicationDetails`: The processed application details.
 * 
 * Returns:
 * - A query object from `react-query` that includes:
 *    - `data`: The fetched application details.
 *    - `isLoading`: Boolean indicating whether the query is in progress.
 *    - `isError`: Boolean indicating whether the query resulted in an error.
 *    - `refetch`: Function to manually refetch the data.
 * 
 * Usage:
 * - This hook can be used in components to fetch and display CHB application details.
 * - Example:
 *    const { data, isLoading, isError } = useChbApplicationDetail(t, tenantId, applicationNo, config, userType, args);
 */
const useChbApplicationDetail = (
  t,
  tenantId,
  applicationNo,
  config = {},
  userType,
  args
) => {
  const queryKey = [
    "CHB_APPLICATION_DETAIL",
    tenantId,
    applicationNo,
    userType,
    JSON.stringify(args),
  ];

  const queryFn = () =>
    CHBSearch.applicationDetails(t, tenantId, applicationNo, userType, args);

  const select = (data) => ({
    applicationData: data,
    applicationDetails: data?.applicationDetails?.map((obj) => obj),
  });

  return queryTemplate({
    queryKey,
    queryFn,
    select,
    config,
  });
};

export default useChbApplicationDetail;
