import { useMutation, useQueryClient } from "@tanstack/react-query";
import { saveMockActionPlan } from "./useWorksInbox";

const USE_MOCK = true; // TODO(integration): set to false once the endpoint below is real

export const useCreateActionPlan = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: async (values) => {
            if (USE_MOCK) {
                console.log("Action Plan payload", values);
                await new Promise((r) => setTimeout(r, 500));
                const serial = Math.floor(1000 + Math.random() * 9000);
                const appNo = `WORKS-AP-2026-${serial}`;
                const newRecord = {
                    applicationNumber: appNo,
                    financialYear: values.financialYear || "2025-26",
                    ulb: values.tenantId || values.ulb || "pg.citya",
                    state: values.state || "WORKS_STATE_PB",
                    district: values.district || "WORKS_DISTRICT_LDH",
                    scheme: values.scheme || "WORKS_SCHEME_FC15",
                    subScheme: values.subScheme || "WORKS_SUB_SCHEME_MUNICIPAL_ROAD_WORKS",
                    approvedAmount: Number(values.approvedAmount) || 2500000,
                    approvedBy: values.approvedBy || "Executive Engineer",
                    status: "PENDING_FOR_VERIFICATION",
                    businessService: "WORKS",
                    slaDays: 21,
                    createdTime: Date.now(),
                    projectsCount: (values.projects || []).length || 1,
                    componentsCount: (values.projects || []).reduce((sum, p) => sum + (p.components || []).length, 0) || 1,
                };
                saveMockActionPlan(newRecord);
                return { applicationNumber: appNo, ...newRecord };
            }

            // TODO(integration): 1) upload File objects (values.*.file) and swap them for fileStoreIds
            //                    2) map `values` to the API shape  3) confirm URL and workflow action code
            const res = await Digit.CustomService.getResponse({
                url: "/works-services/actionplan/_create", // placeholder
                method: "POST",
                body: { ActionPlan: values, workflow: { action: "SUBMIT" } },
            });
            return res?.ActionPlans?.[0] ?? res; // must contain applicationNumber
        },
        onSuccess: () => {
            // the mutation owns cache invalidation, so screens never refetch by hand
            queryClient.invalidateQueries({ queryKey: ["works", "inbox"] });
            queryClient.invalidateQueries({ queryKey: ["works", "search"] });
        },
    });
};