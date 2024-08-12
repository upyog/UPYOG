import { useMutation } from "react-query";
import SchemeService from "../../services/elements/Scheme";

export const useSaveUserDetail = (config = {}) => {
  return useMutation((data) => SchemeService.saveUserDetails(data), config);
};

export default useSaveUserDetail;