<<<<<<< HEAD
import { useQuery } from "react-query";
import SchemeService from "../../services/elements/Scheme";

// export const useVerifierSchemeDetail = (config = {}) => {
//   return useMutation((data) => SchemeService.getVerifierSchemes(data), config);
// };

// export default useVerifierSchemeDetail;

export const useVerifierSchemeDetail = (data, config = {}) => {
  return useQuery(["UserDetails", data], () => SchemeService.getVerifierSchemes(data),config);
};
=======
import { useMutation } from "react-query";
import SchemeService from "../../services/elements/Scheme";

export const useVerifierSchemeDetail = (config = {}) => {
  return useMutation((data) => SchemeService.getVerifierSchemes(data), config);
};

>>>>>>> refs/remotes/origin/DEV
export default useVerifierSchemeDetail;
