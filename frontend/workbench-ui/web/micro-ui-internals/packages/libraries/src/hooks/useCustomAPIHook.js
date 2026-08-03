import { useQuery, useQueryClient } from "@tanstack/react-query";
import { CustomService } from "../services/elements/CustomService";

// ISSUE: Including `RequestInfo` in the react-query `queryKey` caused an infinite loop.
// Because `RequestInfo.ts` = Date.now() — a new timestamp is generated on every render,
// making react-query treat each render as a new query and triggering a re-fetch each time.
// FIX: Exclude `RequestInfo` from `bodyKey` so the query key stays stable across renders.
// NOTE: Original `body` (with RequestInfo) is still passed to `queryFn` — API calls work correctly.

const useCustomAPIHook = ({
  url,
  params,
  body,
  config = {},
  plainAccessRequest,
  changeQueryName,
}) => {
  const client = useQueryClient();
  const { RequestInfo, ...stableBody } = body || {};
  const bodyKey = JSON.stringify(stableBody);

  const {
    isLoading,
    data,
    isFetching,
    refetch,
    error,
    isError,
  } = useQuery({
    queryKey: [url, changeQueryName, params, bodyKey].filter((e) => e),
    queryFn: () =>
      CustomService.getResponse({
        url,
        params,
        body,
        plainAccessRequest,
      }),
    staleTime: 0,
    gcTime: 5 * 60 * 1000,
    ...config,
  });

  return {
    isLoading,
    isFetching,
    data,
    error,
    isError,
    refetch,
    revalidate: () => {
      if (data) {
        client.invalidateQueries({
          queryKey: [url, changeQueryName, params, bodyKey].filter((e) => e),
        });
      }
    },
  };
};

export default useCustomAPIHook;
