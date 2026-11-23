import { useMutation, useQueryClient } from "@tanstack/react-query";

export const useUpdateActionPlan = (tenantId) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (payload) => {
      const effectiveTenantId =
        tenantId ||
        payload?.ActionPlan?.tenantId ||
        Digit.ULBService.getCurrentTenantId();

      const res = await Digit.WorksService.updateActionPlan(payload, effectiveTenantId);
      return (
        res?.ActionPlan ||
        res?.ActionPlans?.[0] ||
        res?.actionPlan ||
        res?.actionPlans?.[0] ||
        res
      );
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["works", "inbox"] });
      queryClient.invalidateQueries({ queryKey: ["works", "search"] });
      queryClient.invalidateQueries({ queryKey: ["works", "details"] });
      queryClient.invalidateQueries({ queryKey: ["workFlowDetails"] });
    },
  });
};

