import { useMutation } from "react-query";
import SchemeService from "../../services/elements/Scheme";

export const useSaveSchemes = (config = {}) => {
  return useMutation((data) => SchemeService.saveSchemeDetails(data), config);
};

export default useSaveSchemes;
