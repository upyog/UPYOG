import { useQuery, useQueryClient } from "react-query";
import { CustomService } from "../services/elements/CustomService";

const useCustomAPIHook = (url, params, body, plainAccessRequest, options = {}) => {
  const client = useQueryClient();
  //api name, querystr, reqbody
  const { isLoading, data } = useQuery(
    ["CUSTOM", { ...params, ...body, ...plainAccessRequest }].filter((e) => e),
    () => CustomService.getResponse({ url, params, ...body, plainAccessRequest }),
    options
  );
  return {
    isLoading,
    data,
    revalidate: () => {
      data && client.invalidateQueries({ queryKey: ["CUSTOM", { ...params, ...body, ...plainAccessRequest }] });
    },
  };
};

export default useCustomAPIHook;
