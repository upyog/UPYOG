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
  return useQuery({
    queryKey,
    queryFn,
    select,
    enabled,
    ...config,
  });
};