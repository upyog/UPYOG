import { useQuery } from "react-query";

import SchemeService from "../../services/elements/Scheme";

export const useCommonGetBank = (data, config = {}) => {
    return useQuery(["BankDetails", data], () => SchemeService.getBanks(data),config);
  };
  
  export default useCommonGetBank;