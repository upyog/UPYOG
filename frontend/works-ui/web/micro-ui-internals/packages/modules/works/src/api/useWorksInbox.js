import { useQuery, useQueryClient } from "@tanstack/react-query";

const normalizeActionPlanRow = (ap) => {
  const projects = ap?.projectDetails || [];
  const componentsCount =
    ap?.totalComponents ??
    projects.reduce((sum, p) => sum + (p?.components?.length || 0), 0);

  return {
    ...ap,
    applicationNumber: ap?.applicationNumber,
    financialYear: ap?.financialYear,
    ulb: ap?.tenantId || ap?.ulb,
    scheme: ap?.schemeCode || ap?.scheme,
    subScheme: ap?.subSchemeCode || ap?.subScheme,
    approvedAmount: ap?.approvedAmount,
    approvedBy: ap?.approvedBy || ap?.approvingAuthority,
    status: ap?.status || ap?.applicationStatus || ap?.workflowStatus || "INITIATED",
    slaDays: ap?.slaDays ?? null,
    createdTime: ap?.auditDetails?.createdTime || ap?.createdTime || ap?.offlineApprovalDate,
    projectsCount: projects.length,
    componentsCount,
  };
};

export const useWorksInbox = ({
  tenantId,
  searchParams = {},
  filters = {},
  pagination = { limit: 10, offset: 0, sortOrder: "DESC" },
}) => {
  const queryClient = useQueryClient();

  const query = useQuery({
    queryKey: ["works", "inbox", tenantId, searchParams, filters, pagination],
    enabled: Boolean(tenantId),
    queryFn: async () => {
      const apiFilters = {
        sortBy: pagination.sortBy || "createdTime",
        sortOrder: pagination.sortOrder || "DESC",
        limit: Number(pagination.limit) || 10,
        offset: Number(pagination.offset) || 0,
      };

      if (searchParams.applicationNumber) {
        apiFilters.applicationNumbers = searchParams.applicationNumber.trim();
      }

      const fy = searchParams.financialYear || filters.financialYear;
      if (fy) {
        apiFilters.financialYear = fy;
      }

      const scheme = searchParams.scheme || filters.scheme;
      if (scheme) {
        apiFilters.schemeCode = scheme;
      }

      if (filters.status && filters.status.length > 0) {
        apiFilters.status = Array.isArray(filters.status)
          ? filters.status.join(",")
          : filters.status;
      }

      const res = await Digit.WorksService.searchActionPlan({
        tenantId,
        filters: apiFilters,
      });

      const rawList = res?.ActionPlans || res?.actionPlans || (res?.ActionPlan ? [res.ActionPlan] : []);
      const normalized = rawList.map(normalizeActionPlanRow);

      const statusCounts = normalized.reduce((acc, curr) => {
        if (curr.status) {
          acc[curr.status] = (acc[curr.status] || 0) + 1;
        }
        return acc;
      }, {});

      const totalCount =
        res?.TotalCount ??
        res?.totalCount ??
        res?.count ??
        normalized.length;

      return {
        ActionPlans: normalized,
        totalCount,
        statusCounts,
      };
    },
    keepPreviousData: true,
  });

  return {
    ...query,
    revalidate: () => queryClient.invalidateQueries({ queryKey: ["works", "inbox"] }),
  };
};
