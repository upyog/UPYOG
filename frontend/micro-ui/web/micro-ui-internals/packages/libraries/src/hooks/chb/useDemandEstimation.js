import { useQuery, useQueryClient } from "react-query";

const useDemandEstimation = ({ tenantId, filters, auth }, config = {}) => {
  const client = useQueryClient();

  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };

  const { isLoading, error, data, isSuccess, refetch } = useQuery(
    ["chbDemandEstimation", tenantId, filters, auth, config],
    () => Digit.CHBServices.estimate(args),
    {
      ...config,
      enabled: false, // Disable automatic query execution
    }
  );

  return { isLoading, error, data, isSuccess, refetch, revalidate: () => client.invalidateQueries(["chbDemandEstimation", tenantId, filters, auth]) };
};

export default useDemandEstimation;
