import { useQuery, useQueryClient } from "react-query";

const useChbSearch = ({ tenantId, filters, auth,searchedFrom="" }, config = {}) => {
  const client = useQueryClient();

  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };

  const defaultSelect = (data) => {
    console.log("chbhook", data)
    if(data.hallsBookingApplication.length > 0)  data.hallsBookingApplication[0].applicationNo = data.hallsBookingApplication[0].applicationNo || [];
      
    return data;
  };

  const { isLoading, error, data, isSuccess } = useQuery(["chbSearchList", tenantId, filters, auth, config], () => Digit.CHBServices.search(args), {
    
    select: defaultSelect,
    ...config,
  });

  return { isLoading, error, data, isSuccess, revalidate: () => client.invalidateQueries(["chbSearchList", tenantId, filters, auth]) };
};

export default useChbSearch;
