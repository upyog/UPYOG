import { useQuery } from "@tanstack/react-query";

/**
 * Standard Query Template (v5)
 */
export const queryTemplate = ({
  queryKey,
  queryFn,
  select,
  enabled = true,
  config = {},
}) => {

  // React Query v5 does not allow queryFn to return undefined.
  // If a query function accidentally returns undefined, React Query treats it as an invalid response
  // and can cause unexpected cache/state behavior. This wrapper ensures that such cases are caught
  // immediately and converted into an error state instead of silently storing an invalid result.
  const safeQueryFn = async (...args) => {
    const result = await queryFn(...args);

    if (result === undefined) {
      throw new Error("Query function returned undefined.");
    }

    return result;
  };

  return useQuery({
    queryKey,
    queryFn: safeQueryFn,
    select,
    enabled,
    ...config,
  });
};