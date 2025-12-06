/*
  Custom hook to fetch pet registration data using react-query.

  Parameters:
  - tenantId: ID of the tenant.
  - filters: Filtering criteria for the search.
  - auth: Authentication details.
  - searchedFrom: Optional string indicating the source of the search.
  - config: Optional configuration for react-query.

  Returns:
  - isLoading: Boolean indicating if the data is being fetched.
  - error: Error object if the query fails.
  - data: Pet registration data.
  - isSuccess: Boolean indicating if the query was successful.
  - revalidate: Function to refetch the data.

  Description:
  - Uses `react-query` to fetch pet registration data from the PTR service.
  - Applies a default selection function to ensure the `owners` field is initialized.
*/
import { useQuery, useQueryClient } from "react-query";

const usePTRSearch = ({ tenantId, filters, auth,searchedFrom="" }, config = {}) => {
  const client = useQueryClient();

  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };

  const defaultSelect = (data) => {
    // console.log("ptrhook", data)
    if(data.PetRegistrationApplications.length > 0)  data.PetRegistrationApplications[0].owners = data.PetRegistrationApplications[0].owners || [];
      
    return data;
  };

  const { isLoading, error, data, isSuccess } = useQuery(["ptrSearchList", tenantId, filters, auth, config], () => Digit.PTRService.search(args), {
    select: defaultSelect,
    ...config,
  });

  return { isLoading, error, data, isSuccess, revalidate: () => client.invalidateQueries(["ptrSearchList", tenantId, filters, auth]) };
};

export default usePTRSearch;
