import { useQuery, useQueryClient } from "@tanstack/react-query";

const formatCurrency = (amt) => {
  if (amt == null || amt === "" || Number.isNaN(Number(amt))) return "NA";
  return Number(amt).toLocaleString("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 2,
  });
};

const formatDate = (val) => {
  if (!val) return "NA";
  if (typeof val === "string" && /^\d{2}\/\d{2}\/\d{4}$/.test(val)) return val;
  const d = new Date(val);
  if (Number.isNaN(d.getTime())) return String(val);
  return d.toLocaleDateString("en-IN", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
};

const transformActionPlanToDetails = (ap) => {
  if (!ap) return null;

  const loc = ap?.locations?.[0] || {};
  const rec = ap?.recommendations?.[0] || {};
  const projects = ap?.projectDetails || [];

  const allDocuments = [];
  (ap?.documents || []).forEach((doc) => {
    if (doc?.fileStoreId) {
      allDocuments.push({
        title: doc.documentType || "ACTION_PLAN_DOCUMENT",
        documentType: doc.documentType || "ACTION_PLAN",
        documentUid: doc.id || doc.fileStoreId,
        fileStoreId: doc.fileStoreId,
      });
    }
  });
  (ap?.recommendations || []).forEach((r) => {
    (r?.documents || []).forEach((doc) => {
      if (doc?.fileStoreId) {
        allDocuments.push({
          title: doc.documentType || "RECOMMENDATION",
          documentType: doc.documentType || "RECOMMENDATION",
          documentUid: doc.id || doc.fileStoreId,
          fileStoreId: doc.fileStoreId,
        });
      }
    });
  });
  projects.forEach((p) => {
    (p?.components || []).forEach((c) => {
      (c?.documents || []).forEach((doc) => {
        if (doc?.fileStoreId) {
          allDocuments.push({
            title: `${c.componentName || "Component"} - ${doc.documentType || "SUPPORTING"}`,
            documentType: doc.documentType || "COMPONENT_SUPPORTING",
            documentUid: doc.id || doc.fileStoreId,
            fileStoreId: doc.fileStoreId,
          });
        }
      });
    });
  });

  const sections = [
    {
      title: "WORKS_AP_SEC_PLAN_DETAILS",
      asSectionHeader: true,
      values: [
        { title: "WORKS_AP_APPLICATION_NO", value: ap.applicationNumber || "NA" },
        { title: "WORKS_AP_FINANCIAL_YEAR", value: ap.financialYear || "NA" },
        { title: "WORKS_AP_ULB", value: ap.tenantId || "NA" },
        { title: "WORKS_INBOX_STATUS", value: ap.status || ap.applicationStatus || "NA" },
        { title: "WORKS_AP_ADMIN_SANCTION", value: ap.adminSanction ? "CS_COMMON_YES" : "CS_COMMON_NO" },
        { title: "WORKS_AP_TECH_SANCTION", value: ap.technicalSanction ? "CS_COMMON_YES" : "CS_COMMON_NO" },
        { title: "WORKS_AP_FUND_SOURCE", value: ap.fundSource || "NA" },
        { title: "WORKS_AP_TOTAL_COMPONENTS", value: String(ap.totalComponents ?? projects.reduce((s, p) => s + (p.components?.length || 0), 0)) },
      ],
    },
    {
      title: "WORKS_AP_SEC_APPROVAL",
      asSectionHeader: true,
      values: [
        { title: "WORKS_AP_APPROVAL_DATE", value: ap.approvalDate || formatDate(ap.offlineApprovalDate) },
        { title: "WORKS_AP_APPROVED_BY", value: ap.approvingAuthority || ap.approvedBy || "NA" },
        { title: "WORKS_AP_SCHEME", value: ap.schemeCode || "NA" },
        { title: "WORKS_AP_SUB_SCHEME", value: ap.subSchemeCode || "NA" },
        { title: "WORKS_AP_APPROVED_AMOUNT", value: formatCurrency(ap.approvedAmount) },
      ],
    },
    {
      title: "WORKS_AP_LOCATION_AND_RECOMMENDATION",
      asSectionHeader: true,
      values: [
        { title: "WORKS_AP_STATE", value: loc.stateCode || "NA" },
        { title: "WORKS_AP_DISTRICT", value: loc.districtCode || "NA" },
        { title: "WORKS_AP_MLA_CONSTITUENCY", value: loc.mlaConstituency || "NA" },
        { title: "WORKS_AP_MP_CONSTITUENCY", value: loc.mpConstituency || "NA" },
        { title: "WORKS_AP_RECOMMENDED_BY", value: rec.recommendedBy || "NA" },
      ],
    },
  ];

  projects.forEach((project, pIdx) => {
    const projectValues = [
      { title: "WORKS_AP_PROJECT_ID", value: project.projectId || "NA" },
      { title: "WORKS_AP_PROJECT_NAME", value: project.projectName || "NA" },
      { title: "WORKS_AP_PURPOSE", value: project.purposeCode || "NA" },
      { title: "WORKS_AP_TYPE_OF_WORK", value: project.typeOfWork || "NA" },
      { title: "WORKS_AP_WORK_CATEGORY", value: project.workCategory || "NA" },
      { title: "WORKS_AP_START_DATE", value: formatDate(project.startDate) },
      { title: "WORKS_AP_END_DATE", value: formatDate(project.endDate) },
    ];

    (project.components || []).forEach((comp, cIdx) => {
      const fundSummary = (comp.fundDetails || [])
        .map((fd) => `${fd.fundType}: ${fd.fundName || fd.sourceCode} (${formatCurrency(fd.amount)})`)
        .join(", ");
      const beneficiarySummary = (comp.beneficiaries || [])
        .map((b) => (b.remark ? `${b.name} (${b.remark})` : b.name))
        .join(", ");

      projectValues.push(
        { title: `Component ${cIdx + 1}`, value: comp.componentName || "NA" },
        { title: "WORKS_AP_CATEGORY", value: [comp.category, comp.subCategory].filter(Boolean).join(" / ") || "NA" },
        { title: "WORKS_AP_COMPONENT_AMOUNT", value: formatCurrency(comp.approvedAmount) },
        { title: "WORKS_AP_FUND_SOURCE", value: fundSummary || comp.fundSource || "NA" },
        ...(beneficiarySummary ? [{ title: "WORKS_AP_BENEFICIARY", value: beneficiarySummary }] : [])
      );
    });

    sections.push({
      title: `Project ${pIdx + 1}: ${project.projectName || project.projectId || ""}`,
      asSectionHeader: true,
      values: projectValues,
    });
  });

  if (allDocuments.length > 0) {
    sections.push({
      title: "WORKS_AP_SEC_DOCUMENTS",
      additionalDetails: {
        documents: [
          {
            title: "WORKS_AP_DOCUMENTS",
            BS: "WORKS",
            values: allDocuments,
          },
        ],
      },
    });
  }

  return {
    applicationData: ap,
    applicationDetails: sections,
    tenantId: ap.tenantId,
  };
};

export const useActionPlanDetails = ({ tenantId, applicationNumber, config = {} }) => {
  const queryClient = useQueryClient();

  const query = useQuery({
    queryKey: ["works", "details", tenantId, applicationNumber],
    enabled: Boolean(tenantId && applicationNumber),
    queryFn: async () => {
      const res = await Digit.WorksService.searchActionPlan({
        tenantId,
        filters: {
          applicationNumbers: applicationNumber,
        },
      });
      const ap =
        res?.ActionPlans?.[0] ||
        res?.actionPlans?.[0] ||
        res?.ActionPlan ||
        res?.actionPlan ||
        null;
      return transformActionPlanToDetails(ap);
    },
    ...config,
  });

  return {
    ...query,
    revalidate: () =>
      queryClient.invalidateQueries({
        queryKey: ["works", "details", tenantId, applicationNumber],
      }),
  };
};

