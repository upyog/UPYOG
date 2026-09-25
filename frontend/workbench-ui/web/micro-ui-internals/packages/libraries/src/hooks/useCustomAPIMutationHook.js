import { useQueryClient, useMutation } from "@tanstack/react-query";
import { CustomService } from "../services/elements/CustomService";

/**
 * Custom hook which can make api call and format response
 *
 * @author NUDM Team
 *
 *
 * @example

 *
 * @returns {Object} Returns the object which contains data and isLoading flag
 */

const useCustomAPIMutationHook = ({ url, params, body, config = {}, plainAccessRequest, changeQueryName = "Random" }) => {
  const client = useQueryClient();

  const { isPending, data, ...rest } = useMutation({
    mutationFn: (data) => CustomService.getResponse({ url, params: { ...params, ...data?.params }, body: { ...body, ...data?.body }, plainAccessRequest }),
    gcTime: 0,
    ...config,
  });
  return {
    ...rest,
    isLoading: isPending,
    data,
    revalidate: () => {
      data && client.invalidateQueries({ queryKey: [url].filter((e) => e) });
    },
  };
};

export default useCustomAPIMutationHook;
