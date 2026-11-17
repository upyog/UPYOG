import { queryTemplate } from "../common/queryTemplate";
import { useQueryClient } from "../common/queryClientTemplate";
import { CustomService } from "../services/elements/CustomService";

/**
 * Custom hook which can gives the privacy functions to access
 *
 * @author jagankumar-egov
 *
 * Feature :: Privacy
 *
 * @example
 *         const { privacy , updatePrivacy } = Digit.Hooks.usePrivacyContext()
 *
 * @returns {Object} Returns the object which contains privacy value and updatePrivacy method
 */
const useCustomAPIHook = (url, params, body, plainAccessRequest, options = {}) => {
  const client = useQueryClient();
  //api name, querystr, reqbody
  const { isLoading, data } = queryTemplate({
    queryKey: ["CUSTOM", { ...params, ...body, ...plainAccessRequest }].filter((e) => e),
    queryFn: () => CustomService.getResponse({ url, params, ...body, plainAccessRequest }),
    config: options,
  });
  return {
    isLoading,
    data,
    revalidate: () => {
      data && client.invalidateQueries(["CUSTOM", { ...params, ...body, ...plainAccessRequest }]);
    },
  };
};

export default useCustomAPIHook;
