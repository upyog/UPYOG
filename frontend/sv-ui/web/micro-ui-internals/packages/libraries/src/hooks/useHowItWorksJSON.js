import { useQuery } from "@tanstack/react-query";

const useGetHowItWorksJSON = (tenantId) => {
    return useQuery({
      queryKey: ["HOW_IT_WORKS", tenantId],
      queryFn: () => Digit.MDMSService.getHowItWorksJSONData(tenantId)
    });
  };

export default useGetHowItWorksJSON;