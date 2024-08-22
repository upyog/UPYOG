import { useMutation } from "react-query";
import SchemeService from "../../services/elements/Scheme";



export const useVerifierSchemeDetail = (config = {}) => {
  return useMutation((data) => SchemeService.getVerifierSchemes(data), config);
};

export default useVerifierSchemeDetail;
