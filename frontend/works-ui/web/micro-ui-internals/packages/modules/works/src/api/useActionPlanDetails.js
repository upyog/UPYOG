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
        tenantId: doc.tenantId || ap.tenantId,
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
          tenantId: doc.tenantId || ap.tenantId,
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
            tenantId: doc.tenantId || ap.tenantId,
          });
        }
      });
    });
  });

  const status = ap.status || ap.applicationStatus || "";
  const isApproved = String(status).toUpperCase() === "APPROVED";

  // Compute financial summary totals matching CreateActionPlan
  let totalAllocated = 0;
  let grantTotal = 0;
  let osrTotal = 0;
  projects.forEach((p) => {
    (p?.components || []).forEach((c) => {
      totalAllocated += Number(c?.approvedAmount) || 0;
      (c?.fundDetails || []).forEach((fd) => {
        const ft = String(fd?.fundType || "").toUpperCase();
        const amt = Number(fd?.amount) || 0;
        if (ft === "GOVT_GRANT" || ft === "GRANT") grantTotal += amt;
        else if (ft === "OSR") osrTotal += amt;
      });
    });
  });
  const approvedAmt = Number(ap.approvedAmount) || 0;
  const unallocated = approvedAmt - totalAllocated;

  const sections = [
    // 1. Plan Details (matches CreateActionPlan Section 1)
    {
      title: "WORKS_AP_SEC_PLAN_DETAILS",
      asSectionHeader: true,
      values: [
        { title: "WORKS_AP_APPLICATION_NO", value: ap.applicationNumber || "NA" },
        ...(isApproved
          ? [{ title: "WORKS_AP_ACTION_ID", value: ap.actionPlanNumber || "NA" }]
          : []),
        { title: "WORKS_AP_FINANCIAL_YEAR", value: ap.financialYear || "NA" },
        { title: "WORKS_AP_ULB", value: ap.tenantId || "NA" },
        { title: "WORKS_INBOX_STATUS", value: status ? `WF_WORKS_${status}` : "NA" },
        ...(ap.submittedDate
          ? [{ title: "WORKS_AP_SUBMITTED_DATE", value: formatDate(ap.submittedDate) }]
          : []),
        { title: "WORKS_AP_STATE", value: loc.stateCode || "NA" },
        { title: "WORKS_AP_DISTRICT", value: loc.districtCode || "NA" },
        { title: "WORKS_AP_MLA_CONSTITUENCY", value: loc.mlaConstituency?.trim() || "NA" },
        { title: "WORKS_AP_MP_CONSTITUENCY", value: loc.mpConstituency?.trim() || "NA" },
        { title: "WORKS_AP_ADMIN_SANCTION", value: ap.adminSanction ? "CS_COMMON_YES" : "CS_COMMON_NO" },
        { title: "WORKS_AP_TECH_SANCTION", value: ap.technicalSanction ? "CS_COMMON_YES" : "CS_COMMON_NO" },
        { title: "WORKS_AP_RECOMMENDED_BY", value: rec.recommendedBy || "NA" },
        ...(rec.recommendedByName
          ? [{ title: "WORKS_AP_RECOMMENDED_BY_NAME", value: rec.recommendedByName }]
          : []),
      ],
    },
    // 2. Approval & Fund Details (matches CreateActionPlan Section 2)
    {
      title: "WORKS_AP_SEC_APPROVAL",
      asSectionHeader: true,
      values: [
        { title: "WORKS_AP_APPROVAL_DATE", value: ap.approvalDate || formatDate(ap.offlineApprovalDate) },
        { title: "WORKS_AP_APPROVED_BY", value: ap.approvingAuthority || ap.approvedBy || "NA" },
        { title: "WORKS_AP_SCHEME", value: ap.schemeCode || "NA" },
        { title: "WORKS_AP_SUB_SCHEME", value: ap.subSchemeCode || "NA" },
        { title: "WORKS_AP_APPROVED_AMOUNT", value: formatCurrency(ap.approvedAmount) },
        { title: "WORKS_AP_FUND_SOURCE", value: ap.fundSource || "NA" },
        {
          title: "WORKS_AP_TOTAL_COMPONENTS",
          value: String(
            ap.totalComponents ??
            projects.reduce((s, p) => s + (p.components?.length || 0), 0)
          ),
        },
        {
          title: "WORKS_AP_DECLARATION_ACCEPTED",
          value: ap.declarationAccepted ? "CS_COMMON_YES" : "CS_COMMON_NO",
        },
      ],
    },
  ];

  // 3. Project Details (Single Table Structure)
  if (projects.length > 0) {
    const projectRows = projects.map((project) => [
      project.projectId || "NA",
      project.projectName || "NA",
      project.purposeCode === "OTHERS" && project.specifyPurpose
        ? `${project.purposeCode} (${project.specifyPurpose})`
        : project.purposeCode || "NA",
      project.typeOfWork || "NA",
      project.workCategory || "NA",
      formatDate(project.startDate),
      formatDate(project.endDate),
      String(project.components?.length || 0),
    ]);

    sections.push({
      title: "WORKS_AP_SEC_PROJECT_DETAILS",
      asSectionHeader: true,
      isTable: true,
      headers: [
        "WORKS_AP_PROJECT_ID",
        "WORKS_AP_PROJECT_NAME",
        "WORKS_AP_PURPOSE",
        "WORKS_AP_TYPE_OF_WORK",
        "WORKS_AP_WORK_CATEGORY",
        "WORKS_AP_START_DATE",
        "WORKS_AP_END_DATE",
        "WORKS_AP_TOTAL_COMPONENTS",
      ],
      tableRows: projectRows,
    });

    // 4. Component Details (Single Table Structure right below Project Details)
    const componentRows = [];
    projects.forEach((project) => {
      (project.components || []).forEach((comp) => {
        const fundDetailsText =
          (comp.fundDetails || [])
            .map((fd) => {
              const parts = [fd.fundType, fd.sourceCode].filter(Boolean).join(" / ");
              const namePart = fd.fundName ? ` (${fd.fundName})` : "";
              return `${parts}${namePart}: ${formatCurrency(fd.amount)}`;
            })
            .join("; ") || "NA";

        const beneficiariesText =
          (comp.beneficiaries || [])
            .map((b) => (b.remark ? `${b.name} (${b.remark})` : b.name))
            .join("; ") || "NA";

        const categoryText = [comp.category, comp.subCategory].filter(Boolean).join(" / ") || "NA";

        componentRows.push([
          project.projectId || project.projectName || "NA",
          comp.componentNumber || "NA",
          comp.componentName || "NA",
          categoryText,
          formatCurrency(comp.approvedAmount),
          comp.fundSource || "NA",
          fundDetailsText,
          beneficiariesText,
          comp.status || "NA",
        ]);
      });
    });

    if (componentRows.length > 0) {
      sections.push({
        title: "WORKS_AP_SEC_COMPONENT_DETAILS",
        asSectionHeader: true,
        isTable: true,
        headers: [
          "WORKS_AP_PROJECT_ID",
          "WORKS_AP_COMPONENT_NUMBER",
          "WORKS_AP_COMPONENT_NAME",
          "WORKS_AP_CATEGORY",
          "WORKS_AP_COMPONENT_AMOUNT",
          "WORKS_AP_FUND_SOURCE",
          "WORKS_AP_FUND_DETAILS",
          "WORKS_AP_BENEFICIARY",
          "WORKS_AP_COMPONENT_STATUS",
        ],
        tableRows: componentRows,
      });
    }
  }

  // 4. Financial Summary (matches CreateActionPlan Section 5)
  sections.push({
    title: "WORKS_AP_SEC_SUMMARY",
    asSectionHeader: true,
    values: [
      { title: "WORKS_AP_TOTAL_ALLOCATED", value: formatCurrency(totalAllocated) },
      { title: "WORKS_AP_UNALLOCATED", value: formatCurrency(unallocated) },
      { title: "WORKS_AP_GRANT_TOTAL", value: formatCurrency(grantTotal) },
      { title: "WORKS_AP_OSR_TOTAL", value: formatCurrency(osrTotal) },
    ],
  });

  // 5. Documents (Action Plan, Admin/Technical Sanction, Recommendation, Component Supporting)
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
        (Array.isArray(res) ? res[0] : null);
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
