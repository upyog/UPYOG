import { useQuery, useQueryClient } from "@tanstack/react-query";

const MS_PER_DAY = 24 * 60 * 60 * 1000;

const normalizeActionPlanRow = (ap, processInstance = null) => {
  const projects = ap?.projectDetails || [];
  const componentsCount =
    ap?.totalComponents ??
    projects.reduce((sum, p) => sum + (p?.components?.length || 0), 0);

  const slaMs = processInstance?.businesssServiceSla;
  const slaDays =
    typeof slaMs === "number"
      ? Math.round(slaMs / MS_PER_DAY)
      : ap?.slaDays ?? null;

  const status =
    processInstance?.state?.applicationStatus ||
    processInstance?.state?.state ||
    ap?.status ||
    ap?.applicationStatus ||
    ap?.workflowStatus ||
    "INITIATED";

  return {
    ...ap,
    applicationNumber: ap?.applicationNumber || processInstance?.businessId,
    financialYear: ap?.financialYear,
    ulb: ap?.tenantId || processInstance?.tenantId || ap?.ulb,
    scheme: ap?.schemeCode || ap?.scheme,
    subScheme: ap?.subSchemeCode || ap?.subScheme,
    approvedAmount: ap?.approvedAmount,
    approvedBy: ap?.approvedBy || ap?.approvingAuthority,
    status,
    slaDays,
    createdTime:
      ap?.auditDetails?.createdTime ||
      processInstance?.auditDetails?.createdTime ||
      ap?.createdTime ||
      ap?.offlineApprovalDate,
    projectsCount: projects.length,
    componentsCount,
    workflowData: processInstance,
  };
};

/**
 * Employee Inbox hook — calls POST /inbox/v1/_search (same UPYOG InboxGeneral.Search pattern as PT).
 */
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
      const processSearchCriteria = {
        businessService: ["WMS_ACTION_PLAN"],
        moduleName: "Works Management Service",
      };

      const moduleSearchCriteria = {
        isInboxSearch: true,
      };

      if (searchParams.applicationNumber) {
        moduleSearchCriteria.applicationNumber = searchParams.applicationNumber.trim();
      }

      const fy = searchParams.financialYear || filters.financialYear;
      if (fy) {
        moduleSearchCriteria.financialYear = fy;
      }

      const scheme = searchParams.scheme || filters.scheme;
      if (scheme) {
        moduleSearchCriteria.schemeCode = scheme;
      }

      if (filters.status && filters.status.length > 0) {
        processSearchCriteria.status = Array.isArray(filters.status)
          ? filters.status
          : [filters.status];
      }

      if (filters.assignee === "ASSIGNED_TO_ME") {
        const uuid = Digit.UserService.getUser()?.info?.uuid;
        if (uuid) processSearchCriteria.assignee = uuid;
      }

      const res = await Digit.InboxGeneral.Search({
        inbox: {
          tenantId,
          processSearchCriteria,
          moduleSearchCriteria,
          limit: Number(pagination.limit) || 10,
          offset: Number(pagination.offset) || 0,
        },
      });

      const items = Array.isArray(res?.items) ? res.items : [];
      const normalized = items.map((item) =>
        normalizeActionPlanRow(item?.businessObject || {}, item?.ProcessInstance || null)
      );

      const statusCounts = {};
      if (Array.isArray(res?.statusMap)) {
        res.statusMap.forEach((entry) => {
          const key = entry?.applicationstatus || entry?.state;
          if (key) {
            statusCounts[key] = (statusCounts[key] || 0) + (Number(entry?.count) || 0);
          }
        });
      } else {
        normalized.forEach((curr) => {
          if (curr.status) {
            statusCounts[curr.status] = (statusCounts[curr.status] || 0) + 1;
          }
        });
      }

      return {
        ActionPlans: normalized,
        totalCount: res?.totalCount ?? normalized.length,
        statusCounts,
        statusMap: res?.statusMap || [],
      };
    },
    keepPreviousData: true,
  });

  return {
    ...query,
    revalidate: () => queryClient.invalidateQueries({ queryKey: ["works", "inbox"] }),
  };
};

/**
 * Direct Action Plan search hook — calls POST /works-management-service/action-plan/v1/_search
 * (used by the Search Application screen).
 */
export const useWorksSearch = ({
  tenantId,
  searchParams = {},
  filters = {},
  pagination = { limit: 10, offset: 0, sortBy: "createdTime", sortOrder: "DESC" },
}) => {
  const queryClient = useQueryClient();

  const query = useQuery({
    queryKey: ["works", "search", tenantId, searchParams, filters, pagination],
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

      const rawList =
        res?.ActionPlans ||
        res?.actionPlans ||
        (res?.ActionPlan ? [res.ActionPlan] : []);
      const normalized = rawList.map((ap) => normalizeActionPlanRow(ap));

      const totalCount =
        res?.TotalCount ??
        res?.totalCount ??
        res?.count ??
        normalized.length;

      return {
        ActionPlans: normalized,
        totalCount,
      };
    },
    keepPreviousData: true,
  });

  return {
    ...query,
    revalidate: () => queryClient.invalidateQueries({ queryKey: ["works", "search"] }),
  };
};
