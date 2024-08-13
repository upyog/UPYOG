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
