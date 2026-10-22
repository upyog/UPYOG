import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

/**
 * ============================================================================
 * UNIFIED EMPLOYEE DASHBOARD & WORKSPACE
 * ============================================================================
 * Replicates the executive municipal dashboard reference layout:
 * 1. Top greeting header with user shift status and quick "+ New" action
 * 2. "Act Now" Hero SLA Risk Triage Banner with active queue cards & shift stats
 * 3. 5 Unified KPI Metric Cards in a single clean row
 * 4. "Pinned Services" section integrating the action cards seamlessly
 * 5. "Ward Collections — Today" performance breakdown chart
 * 6. Standardized municipal portal footer
 */

const formatIndianCurrency = (amount) => {
  if (amount === null || amount === undefined) return "₹0";
  const num = Number(amount);
  if (isNaN(num)) return `₹${amount}`;
  if (num >= 10000000) {
    const crores = num / 10000000;
    return crores >= 100 ? `₹${Math.round(crores)} Cr` : `₹${crores.toFixed(2)} Cr`;
  } else if (num >= 100000) {
    const lakhs = num / 100000;
    return lakhs >= 100 ? `₹${Math.round(lakhs)}L` : `₹${lakhs.toFixed(2)}L`;
  } else if (num >= 1000) {
    return `₹${num.toLocaleString("en-IN")}`;
  }
  return `₹${num}`;
};

const formatNumbers = (num) => {
  if (num === null || num === undefined) return "0";
  const n = Number(num);
  if (isNaN(n)) return String(num);
  return n.toLocaleString("en-IN");
};

const getModuleStyle = (modName) => {
  const name = String(modName || "").toUpperCase();
  if (name.includes("PROPERTY") || name === "PT") return { code: "PT", color: "#1d4ed8", bg: "#eff6ff" }; // Blue
  if (name.includes("TRADE") || name === "TL") return { code: "TL", color: "#059669", bg: "#ecfdf5" }; // Green
  if (name.includes("WATER") || name === "WS") return { code: "WS", color: "#7c3aed", bg: "#f5f3ff" }; // Purple
  if (name.includes("MCOLLECT") || name === "MC") return { code: "MC", color: "#d97706", bg: "#fffbeb" }; // Amber Gold
  if (name.includes("GRIEVANCE") || name.includes("PGR") || name.includes("COMPLAINT")) return { code: "PGR", color: "#dc2626", bg: "#fef2f2" }; // Crimson Red
  if (name.includes("PET") || name === "PTR") return { code: "PTR", color: "#db2777", bg: "#fdf2f8" }; // Pink
  if (name.includes("BIRTH") || name.includes("DEATH") || name === "BND") return { code: "BND", color: "#0f766e", bg: "#f0fdfa" }; // Teal
  if (name.includes("COMMUNITY") || name.includes("CHB") || name.includes("HALL")) return { code: "CHB", color: "#b45309", bg: "#fefce8" }; // Bronze
  if (name.includes("BUILDING") || name.includes("BPA") || name.includes("OBPS")) return { code: "BPA", color: "#86198f", bg: "#fdf4ff" }; // Plum
  if (name.includes("NOC") || name.includes("FIRE")) return { code: "NOC", color: "#ea580c", bg: "#fff7ed" }; // Orange
  if (name.includes("FSM") || name.includes("FAECAL")) return { code: "FSM", color: "#334155", bg: "#f8fafc" }; // Slate
  return { code: name.slice(0, 3) || "MOD", color: "#475569", bg: "#f1f5f9" };
};

const MODULE_ROLE_CONFIG = [
  {
    moduleCode: "PT",
    titleKey: "PT_DASHBOARD",
    actionPrefix: "PT",
    workflowModuleName: "PT",
    inboxModuleName: "PT",
    businessService: ["PT.CREATE", "PT.MUTATION", "PT.UPDATE"],
    moduleSearchCriteria: {
      isInboxSearch: true,
      creationReason: ["CREATE", "MUTATION", "UPDATE"],
    },
    roles: [
      "PT_DOC_VERIFIER",
      "PT_COLLECTION_EMP",
      "PROPERTY_APPROVER",
      "PT_CEMP",
      "PROPERTY_VERIFIER",
      "PT_APPROVER",
      "PT_FIELD_INSPECTOR",
      "CR_PT",
      "PT_REPORT_VIEWER",
      "PT_DASHBOARD_VIEWER",
    ],
  },
  {
    moduleCode: "PTR",
    titleKey: "PET_DASHBOARD",
    actionPrefix: "PTR",
    workflowModuleName: "pet-services",
    inboxModuleName: "pet-services",
    businessService: ["ptr"],
    moduleSearchCriteria: {
      isInboxSearch: true,
    },
    roles: [
      "PTR_VERIFIER",
      "PTR_CEMP",
      "PTR_APPROVER",
      "PTR_DASHBOARD_VIEWER",
    ],
  },
  {
    moduleCode: "TL",
    titleKey: "TL_DASHBOARD",
    actionPrefix: "TL",
    workflowModuleName: "TL",
    inboxModuleName: "TL",
    businessService: ["TL", "DIRECTTL", "NewTL", "DIRECTRENEWAL", "EDITRENEWAL"],
    moduleSearchCriteria: {
      isInboxSearch: true,
    },
    roles: [
      "TL_CEMP",
      "TL_APPROVER",
      "TL_FIELD_INSPECTOR",
      "TL_DOC_VERIFIER",
      "TL_ADMIN",
      "TL_CREATOR",
    ],
  },
  {
    moduleCode: "WS",
    titleKey: "WS_DASHBOARD",
    actionPrefix: "WS",
    workflowModuleName: "WS",
    inboxModuleName: "WS",
    businessService: ["WS.ONE_TIME_FEE", "WS.NEW_CONNECTION", "NewWS1", "ModifyWSConnection", "DisconnectWSConnection"],
    moduleSearchCriteria: {
      isInboxSearch: true,
    },
    roles: [
      "WS_CEMP",
      "WS_APPROVER",
      "WS_FIELD_INSPECTOR",
      "WS_DOC_VERIFIER",
      "WS_CLERK",
    ],
  },
  {
    moduleCode: "SW",
    titleKey: "SW_DASHBOARD",
    actionPrefix: "SW",
    workflowModuleName: "SW",
    inboxModuleName: "SW",
    businessService: ["SW.ONE_TIME_FEE", "SW.NEW_CONNECTION", "NewSW1", "ModifySWConnection", "DisconnectSWConnection"],
    moduleSearchCriteria: {
      isInboxSearch: true,
    },
    roles: [
      "SW_CEMP",
      "SW_APPROVER",
      "SW_FIELD_INSPECTOR",
      "SW_DOC_VERIFIER",
      "SW_CLERK",
    ],
  },
  {
    moduleCode: "PGR",
    titleKey: "PGR_DASHBOARD",
    actionPrefix: "PGR",
    workflowModuleName: "PGR",
    inboxModuleName: "PGR",
    businessService: ["PGR"],
    moduleSearchCriteria: {
      isInboxSearch: true,
    },
    roles: [
      "PGR_LME",
      "GRO",
      "GO",
      "DGR",
      "CSR",
      "PGR_DASHBOARD_VIEWER",
      "PGR_REPORT_VIEWER",
    ],
  },
  {
    moduleCode: "NOC",
    titleKey: "NOC_DASHBOARD",
    actionPrefix: "NOC",
    workflowModuleName: "NOC",
    inboxModuleName: "NOC",
    businessService: ["FIRE_NOC"],
    moduleSearchCriteria: {
      isInboxSearch: true,
    },
    roles: [
      "NOC_DOC_VERIFIER",
      "NOC_APPROVER",
      "NOC_CEMP",
      "FIRE_NOC_APPROVER",
    ],
  },
  {
    moduleCode: "CHB",
    titleKey: "CHB_DASHBOARD",
    actionPrefix: "CHB",
    workflowModuleName: "CHB",
    inboxModuleName: "CHB",
    businessService: ["CHB"],
    moduleSearchCriteria: {
      isInboxSearch: true,
    },
    roles: [
      "CHB_APPROVER",
      "CHB_VERIFIER",
      "CHB_DASHBOARD_VIEWER",
    ],
  },
  {
    moduleCode: "BPA",
    titleKey: "BPA_DASHBOARD",
    actionPrefix: "BPA",
    workflowModuleName: "BPA",
    inboxModuleName: "BPA",
    businessService: ["BPA", "BPA_OC", "BPA_LOW", "BPA_STAKEHOLDER_REGISTRATION"],
    moduleSearchCriteria: {
      isInboxSearch: true,
    },
    roles: [
      "BPA_VERIFIER",
      "BPA_APPROVER",
      "BPA_FIELD_INSPECTOR",
      "BPA_NOC_VERIFIER",
      "BPA_DESIGNER",
      "BPA_ENGINEER",
      "BPA_BUILDER",
      "BPA_STRUCTURALENGINEER",
      "BPA_SUPERVISOR",
      "BPA_DOC_VERIFIER",
      "BPAREG_APPROVER",
      "BPAREG_DOC_VERIFIER",
      "BPAREG_EMPLOYEE",
    ],
  },
  {
    moduleCode: "FSM",
    titleKey: "FSM_DASHBOARD",
    actionPrefix: "FSM",
    workflowModuleName: "FSM",
    inboxModuleName: "FSM",
    businessService: ["FSM", "FSM_POST_PAY_SERVICE", "PAY_LATER_SERVICE", "FSM_ADVANCE_PAY_SERVICE", "FSM_ZERO_PAY_SERVICE"],
    moduleSearchCriteria: {
      isInboxSearch: true,
    },
    roles: [
      "FSM_CREATOR_EMP",
      "FSM_DSO",
      "FSM_EDITOR_EMP",
      "FSM_ADMIN",
      "FSM_COLLECTOR",
      "FSM_REPORT_VIEWER",
      "FSM_DASHBOARD_VIEWER",
    ],
  },
  {
    moduleCode: "MCOLLECT",
    titleKey: "MCOLLECT_DASHBOARD",
    actionPrefix: "MC",
    workflowModuleName: "mCollect",
    inboxModuleName: "mCollect",
    businessService: ["mcollect"],
    moduleSearchCriteria: {
      isInboxSearch: true,
    },
    roles: [
      "UC_EMP",
      "MCOLLECT_CEMP",
      "EGF_VOUCHER_CREATOR",
      "EGF_VOUCHER_APPROVER",
    ],
  },
  {
    moduleCode: "WT",
    titleKey: "WT_DASHBOARD",
    actionPrefix: "WT",
    workflowModuleName: "request-service",
    inboxModuleName: "request-service",
    businessService: ["watertanker"],
    moduleSearchCriteria: {
      isInboxSearch: true,
    },
    roles: ["WT_CEMP", "WT_VENDOR", "WT_DASHBOARD_VIEWER"],
  },
  {
    moduleCode: "MT",
    titleKey: "MT_DASHBOARD",
    actionPrefix: "MT",
    workflowModuleName: "request-service",
    inboxModuleName: "request-service",
    businessService: ["mobileToilet"],
    moduleSearchCriteria: {
      isInboxSearch: true,
    },
    roles: ["MT_CEMP", "MT_VENDOR"],
  },
  {
    moduleCode: "TP",
    titleKey: "TP_DASHBOARD",
    actionPrefix: "TP",
    workflowModuleName: "request-service",
    inboxModuleName: "request-service",
    businessService: ["treePruning"],
    moduleSearchCriteria: {
      isInboxSearch: true,
    },
    roles: ["TP_CEMP", "TP_VERIFIER", "TP_EXECUTION"],
  },
  {
    moduleCode: "ASSET",
    titleKey: "ASSET_DASHBOARD",
    actionPrefix: "ASSET",
    workflowModuleName: "asset-services",
    inboxModuleName: "asset-services",
    businessService: ["asset-create"],
    moduleSearchCriteria: {
      isInboxSearch: true,
    },
    roles: ["ASSET_INITIATOR", "ASSET_VERIFIER", "ASSET_APPROVER"],
  },
  {
    moduleCode: "EW",
    titleKey: "EW_DASHBOARD",
    actionPrefix: "EW",
    workflowModuleName: "ewaste-services",
    inboxModuleName: "ewaste-services",
    businessService: ["Ewaste"],
    moduleSearchCriteria: {
      isInboxSearch: true,
    },
    roles: ["EW_VENDOR", "EW_DASHBOARD_VIEWER"],
  },
  {
    moduleCode: "NDC",
    titleKey: "NDC_DASHBOARD",
    actionPrefix: "NDC",
    workflowModuleName: "ndc-services",
    inboxModuleName: "ndc-services",
    businessService: ["NDC"],
    moduleSearchCriteria: {
      isInboxSearch: true,
    },
    roles: ["NDC_ADMIN", "NDCADMIN", "NDCCEMP"],
  },
];

const getApplicationDetailsUrl = (modCode, applicationId, tenantId) => {
  const encId = encodeURIComponent(applicationId || "");
  const code = (modCode || "").toUpperCase();
  if (code.includes("PET") || code === "PTR") {
    return `/upyog-ui/employee/ptr/petservice/application-details/${encId}`;
  }
  if (code === "PT" || code.includes("PROPERTY")) {
    return `/upyog-ui/employee/pt/application-details/${encId}?from=INBOX`;
  }
  if (code === "TL" || code.includes("TRADE")) {
    return `/upyog-ui/employee/tl/application-details/${encId}`;
  }
  if (code === "WS" || code.includes("WATER")) {
    return `/upyog-ui/employee/ws/application-details?applicationNumber=${encId}&service=WATER&tenantId=${tenantId}`;
  }
  if (code === "SW" || code.includes("SEWERAGE")) {
    return `/upyog-ui/employee/ws/application-details?applicationNumber=${encId}&service=SEWERAGE&tenantId=${tenantId}`;
  }
  if (code === "NOC" || code.includes("FIRE")) {
    return `/upyog-ui/employee/noc/fire-noc-application-details/${encId}`;
  }
  if (code === "PGR" || code.includes("GRIEVANCE")) {
    return `/upyog-ui/employee/pgr/complaint/details/${encId}`;
  }
  if (code === "BPA" || code.includes("BUILDING") || code === "OBPS") {
    return `/upyog-ui/employee/obps/bpa/${encId}`;
  }
  if (code === "FSM") {
    return `/upyog-ui/employee/fsm/application-details/${encId}`;
  }
  if (code === "MC" || code === "MCOLLECT") {
    return `/upyog-ui/employee/mcollect/challan-search/${encId}`;
  }
  return `/upyog-ui/employee/${code.toLowerCase()}/inbox`;
};

const getRoleBasedCriteria = (roles = [], tenantId, modules = []) => {
  const roleCodes = roles.map((r) => (typeof r === "string" ? r : r?.code)).filter(Boolean);

  // Match configs based on user's active role codes
  let matchingConfigs = MODULE_ROLE_CONFIG.filter((cfg) =>
    cfg.roles.some((r) => roleCodes.includes(r))
  );

  // If SUPERUSER or many roles, refine to the modules present or PT and PTR
  if (matchingConfigs.length === 0) {
    matchingConfigs = [MODULE_ROLE_CONFIG[0], MODULE_ROLE_CONFIG[1]];
  } else if (matchingConfigs.length > 3 && roleCodes.includes("SUPERUSER")) {
    const enabledCodes = (modules || []).map((m) => (m.code || "").toUpperCase());
    const filtered = matchingConfigs.filter((cfg) =>
      enabledCodes.some((c) => c.includes(cfg.moduleCode) || cfg.moduleCode.includes(c))
    );
    if (filtered.length > 0) {
      matchingConfigs = filtered;
    } else {
      matchingConfigs = matchingConfigs.slice(0, 3);
    }
  }

  const primaryModule = matchingConfigs[0]?.workflowModuleName || "PT";

  const workflow = {
    tenantId,
    isViewAll: true,
    processSearchCriteria: {
      moduleName: primaryModule,
    },
  };

  const inbox = matchingConfigs.map((cfg) => ({
    tenantId,
    processSearchCriteria: {
      businessService: cfg.businessService,
      moduleName: cfg.inboxModuleName,
    },
    moduleSearchCriteria: {
      ...cfg.moduleSearchCriteria,
    },
  }));

  return { primaryModule, workflow, inbox, activeConfigs: matchingConfigs };
};

const getSlaDetails = (pi) => {
  const slaMs = pi?.businesssServiceSla ?? pi?.businessServiceSla ?? pi?.sla;
  const isEscalated =
    pi?.escalated === true ||
    pi?.priority === "HIGH" ||
    pi?.priority === "URGENT" ||
    pi?.additionalDetails?.priority === "HIGH";

  if (slaMs === null || slaMs === undefined) {
    return {
      tag: isEscalated ? "High Priority" : (pi?.state?.state || "Active"),
      tagType: isEscalated ? "danger" : "info",
      isBreached: isEscalated,
      sortScore: isEscalated ? -999999999999 : 999999999999,
      slaDesc: isEscalated ? "Escalated priority application" : "In active workflow",
    };
  }

  const hours = Math.round(slaMs / (1000 * 60 * 60));
  const days = Math.round(slaMs / (1000 * 60 * 60 * 24));

  if (slaMs < 0) {
    const overdueDays = Math.abs(days);
    return {
      tag: overdueDays > 0 ? `SLA Crossed (${overdueDays}d overdue)` : "SLA Crossed (Overdue)",
      tagType: "danger",
      isBreached: true,
      sortScore: slaMs, // Negative number: the larger the overdue time, the more negative, hence first
      slaDesc: overdueDays > 0 ? `${overdueDays} days past SLA deadline` : "SLA deadline crossed",
    };
  }

  if (hours <= 24) {
    return {
      tag: hours <= 0 ? "Due today" : `${hours}h left`,
      tagType: "danger",
      isBreached: false,
      sortScore: slaMs,
      slaDesc: `Urgent · Due in ${Math.max(1, hours)} hours`,
    };
  }

  if (days <= 3) {
    return {
      tag: `${days}d left`,
      tagType: "warning",
      isBreached: false,
      sortScore: slaMs,
      slaDesc: `${days} days left in SLA queue`,
    };
  }

  return {
    tag: `${days}d left`,
    tagType: "info",
    isBreached: false,
    sortScore: slaMs,
    slaDesc: `${days} days remaining`,
  };
};

const getServiceFriendlyName = (businessService, moduleName) => {
  const s = String(businessService || moduleName || "").toUpperCase();
  if (s.includes("PT.CREATE")) return "Property Tax Assessment";
  if (s.includes("PT.MUTATION")) return "Property Ownership Mutation";
  if (s.includes("PT.UPDATE")) return "Property Details Update";
  if (s.includes("PTR") || s.includes("PET")) return "Pet Registration Application";
  if (s.includes("TL") || s.includes("TRADE")) return "Trade License Application";
  if (s.includes("WS") || s.includes("WATER")) return "New Water Connection";
  if (s.includes("SW") || s.includes("SEWERAGE")) return "New Sewerage Connection";
  if (s.includes("NOC") || s.includes("FIRE")) return "Provisional Fire NOC";
  if (s.includes("PGR")) return "Grievance Redressal";
  if (s.includes("BPA") || s.includes("OBPS")) return "Building Permission";
  if (s.includes("FSM")) return "Desludging Service";
  if (s.includes("MCOLLECT")) return "Misc Collection";
  return businessService || moduleName || "Service Application";
};

const ModuleBreakdownSkeleton = () => (
  <div className="employee-module-breakdown-section is-skeleton">
    <div className="module-breakdown-header">
      <div className="skeleton-line skeleton-header-title" />
      <div className="skeleton-line skeleton-header-count" />
    </div>
    <div className="module-breakdown-grid count-2">
      {[1, 2].map((idx) => (
        <div key={idx} className="module-breakdown-card skeleton-card">
          <div className="module-breakdown-card-top">
            <div className="module-top-left">
              <div className="skeleton-box skeleton-badge" />
              <div className="skeleton-text-group">
                <div className="skeleton-line skeleton-title" />
                <div className="skeleton-line skeleton-sub" />
              </div>
            </div>
            <div className="skeleton-box skeleton-btn" />
          </div>

          <div className="module-breakdown-kpis">
            {[1, 2, 3, 4].map((kpiIdx) => (
              <div key={kpiIdx} className="breakdown-kpi skeleton-kpi">
                <div className="skeleton-line skeleton-kpi-label" />
                <div className="skeleton-line skeleton-kpi-val" />
              </div>
            ))}
          </div>
        </div>
      ))}
    </div>
  </div>
);

const ActNowSkeleton = () => (
  <div className="hero-act-now-banner is-skeleton">
    <div className="act-now-header">
      <div className="act-now-title-group">
        <div className="skeleton-box skeleton-dot" />
        <div className="skeleton-line skeleton-act-title" />
        <div className="skeleton-line skeleton-act-desc" />
      </div>
      <div className="skeleton-line skeleton-act-count" />
    </div>

    <div className="act-now-queue-grid count-4">
      {[1, 2, 3, 4].map((idx) => (
        <div key={idx} className="act-now-queue-card skeleton-act-card">
          <div className="queue-card-top">
            <div className="skeleton-line skeleton-badge-tag" />
            <div className="skeleton-line skeleton-sla-pill" />
          </div>
          <div className="skeleton-line skeleton-card-h3" />
          <div className="skeleton-line skeleton-card-meta-line" />
          <div className="skeleton-box skeleton-action-btn" />
        </div>
      ))}
    </div>
  </div>
);

const EmployeeDashboard = ({ modules = [], isStandalone = false }) => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const user = Digit.UserService.getUser();
  const [showQuickMenu, setShowQuickMenu] = useState(false);
  const [showAllQueue, setShowAllQueue] = useState(false);
  const [showAllModules, setShowAllModules] = useState(false);
  const [isDashboardLoading, setIsDashboardLoading] = useState(true);
  const [modulesData, setModulesData] = useState({});
  const [liveQueueData, setLiveQueueData] = useState([]);
  const [renderedCardCount, setRenderedCardCount] = useState(0);
  const cardsContainerRef = React.useRef(null);
  const tenantId =
    Digit.ULBService.getCurrentTenantId?.() ||
    Digit.SessionStorage.get("Employee.tenantId") ||
    user?.info?.tenantId ||
    "pg.citya";

  const userName = user?.info?.name || "Employee";
  const userRole = user?.info?.roles?.[0]?.name || "Revenue clerk · counter · राजस्व लिपिक";

  const today = new Date();
  const dateStr = today.toLocaleDateString("en-GB", {
    weekday: "long",
    day: "numeric",
    month: "long",
    year: "numeric",
  });

  useEffect(() => {
    let isMounted = true;
    const fetchDashboardData = async () => {
      try {
        setIsDashboardLoading(true);
        const userRoles = user?.info?.roles || [];
        const { workflow, inbox } = getRoleBasedCriteria(userRoles, tenantId, modules);
        const requestId = `${new Date().getTime()}`;

        // Call both aggregate and role-based search in parallel
        const [aggregateRes, roleBaseRes] = await Promise.allSettled([
          Digit.EmployeeDashboardService.aggregate({
            tenantId,
            requestId,
            workflow,
            inbox,
          }),
          Digit.EmployeeDashboardService.roleBaseSearch({ tenantId }),
        ]);

        if (!isMounted) return;

        // 1. ROLE-BASED SERVICE METRICS: strictly from employee dashboard API only
        if (roleBaseRes.status === "fulfilled" && roleBaseRes.value?.dashboardData) {
          setModulesData(roleBaseRes.value.dashboardData);
        }

        // 2. ACT NOW SLA RISK QUEUE: from workflow.ProcessInstances in aggregate API response
        if (aggregateRes.status === "fulfilled" && aggregateRes.value) {
          const aggData = aggregateRes.value;
          const allRawInstances = [];

          // Primary source: workflow.ProcessInstances
          if (Array.isArray(aggData.workflow?.ProcessInstances)) {
            aggData.workflow.ProcessInstances.forEach((pi) => {
              allRawInstances.push(pi);
            });
          }

          // Secondary enrichment: inbox items (if any additional process instances)
          if (aggData.inbox && typeof aggData.inbox === "object") {
            Object.entries(aggData.inbox).forEach(([modKey, modInfo]) => {
              if (Array.isArray(modInfo?.items)) {
                modInfo.items.forEach((it) => {
                  if (it.ProcessInstance) {
                    allRawInstances.push({
                      ...it.ProcessInstance,
                      businessObject: it.businessObject || it.serviceObject,
                      inboxModuleName: modKey,
                    });
                  }
                });
              }
            });
          }

          // Deduplicate by businessId
          const seenBusinessIds = new Set();
          const uniqueInstances = [];
          for (const pi of allRawInstances) {
            const bId = pi.businessId || pi.businessObject?.applicationNumber || pi.id;
            if (bId && !seenBusinessIds.has(bId)) {
              seenBusinessIds.add(bId);
              uniqueInstances.push(pi);
            }
          }

          if (uniqueInstances.length > 0) {
            const queueItems = uniqueInstances
              .map((pi, idx) => {
                const rawModule = (pi.moduleName || pi.inboxModuleName || pi.businessService?.split(".")[0] || "PT").toUpperCase();
                let modCode = "PT";
                if (rawModule.includes("PET") || rawModule.includes("PTR")) modCode = "PET";
                else if (rawModule.includes("TRADE") || rawModule === "TL") modCode = "TL";
                else if (rawModule.includes("WATER") || rawModule === "WS") modCode = "WS";
                else if (rawModule.includes("SEWERAGE") || rawModule === "SW") modCode = "SW";
                else if (rawModule.includes("NOC") || rawModule.includes("FIRE")) modCode = "NOC";
                else if (rawModule.includes("GRIEVANCE") || rawModule === "PGR") modCode = "PGR";
                else if (rawModule.includes("MCOLLECT") || rawModule === "MC") modCode = "MC";
                else if (rawModule.includes("BUILDING") || rawModule === "BPA") modCode = "BPA";
                else if (rawModule.includes("FSM")) modCode = "FSM";

                const slaInfo = getSlaDetails(pi);
                const applicationId = pi.businessId || pi.businessObject?.applicationNumber || pi.id || `${modCode}-2026-${1000 + idx * 237}`;
                const serviceName = getServiceFriendlyName(pi.businessService, pi.moduleName);
                const stateName = pi.state?.state || "INWORKFLOW";

                const applicantName = pi.businessObject?.applicantName || pi.assigner?.name;
                const metaText = applicantName
                  ? `${applicantName} · ${slaInfo.slaDesc}`
                  : slaInfo.slaDesc;

                const appDetailsUrl = getApplicationDetailsUrl(modCode, applicationId, tenantId);

                return {
                  code: modCode,
                  id: applicationId,
                  tag: slaInfo.tag,
                  tagType: slaInfo.tagType,
                  sortScore: slaInfo.sortScore,
                  isBreached: slaInfo.isBreached,
                  title: `${serviceName} — ${stateName}`,
                  meta: metaText,
                  link: appDetailsUrl,
                };
              })
              // Sort by SLA priority: Crossed SLA (< 0) & High Priority first, followed by urgent deadlines
              .sort((a, b) => a.sortScore - b.sortScore);

            setLiveQueueData(queueItems);
          }
        }
      } catch (error) {
        console.error("Error fetching dashboard stats:", error);
      } finally {
        if (isMounted) {
          setIsDashboardLoading(false);
        }
      }
    };

    fetchDashboardData();
    return () => {
      isMounted = false;
    };
  }, [tenantId]);

  useEffect(() => {
    const updateCount = () => {
      if (cardsContainerRef.current) {
        const count = cardsContainerRef.current.children.length;
        if (count > 0) setRenderedCardCount(count);
      }
    };
    updateCount();
    const timer = setTimeout(updateCount, 500);
    return () => clearTimeout(timer);
  }, [modules, modulesData]);

  // Dynamically compute real aggregates from the original dashboard API response
  const moduleEntries = Object.entries(modulesData).filter(
    ([, data]) => data && (data.applicationReceived != null || data.totalAmount != null || data.applicationPending != null || data.applicationApproved != null)
  );

  const hasApiData = moduleEntries.length > 0;

  const totalAmount = hasApiData
    ? moduleEntries.reduce((acc, [, curr]) => acc + (Number(curr?.totalAmount) || 0), 0)
    : 482000;

  const totalReceived = hasApiData
    ? moduleEntries.reduce((acc, [, curr]) => acc + (Number(curr?.applicationReceived) || 0), 0)
    : 38;

  const totalPending = hasApiData
    ? moduleEntries.reduce((acc, [, curr]) => acc + (Number(curr?.applicationPending) || 0), 0)
    : 7;

  const totalApproved = hasApiData
    ? moduleEntries.reduce((acc, [, curr]) => acc + (Number(curr?.applicationApproved) || 0), 0)
    : 11;

  const quickActions = [
    { label: "Counter receipt", key: "R", icon: "₹", link: "/upyog-ui/employee/mcollect/new-receipt" },
    { label: "New application (any module)", key: "A", icon: "⊞", link: "/upyog-ui/employee/pt/new-application" },
    { label: "Log a grievance", key: "G", icon: "!", link: "/upyog-ui/employee/pgr/complaint/create" },
    { label: "Schedule site visit", key: "V", icon: "⦿", link: "/upyog-ui/employee" },
    { label: "Bulk upload — demand register", key: "U", icon: "⬆", link: "/upyog-ui/employee" },
  ];

  const allQueueItems = liveQueueData;
  const displayedQueueItems = showAllQueue ? allQueueItems : allQueueItems.slice(0, 4);
  const gridColClass = `count-${Math.min(displayedQueueItems.length, 4)}`;

  // 1. Collect all valid registered modules, guaranteeing at least 4 are available
  const registeredModules = (modules || []).filter(({ code }) => {
    return !!Digit.ComponentRegistryService.getComponent(`${code}Card`);
  });

  const standardModuleFallbacks = [
    { code: "PT" },
    { code: "TL" },
    { code: "WS" },
    { code: "PTR" },
    { code: "PGR" },
    { code: "MCollect" },
    { code: "NOC" },
    { code: "OBPS" },
  ].filter(({ code }) => !!Digit.ComponentRegistryService.getComponent(`${code}Card`));

  const effectiveModulesMap = new Map();
  registeredModules.forEach((m) => effectiveModulesMap.set(m.code, m));
  if (effectiveModulesMap.size < 4) {
    standardModuleFallbacks.forEach((m) => {
      if (!effectiveModulesMap.has(m.code)) {
        effectiveModulesMap.set(m.code, m);
      }
    });
  }

  const allAvailableModules = Array.from(effectiveModulesMap.values());
  // Show all if expanded, otherwise keep at least 4 visible
  const displayedModules = showAllModules
    ? allAvailableModules
    : allAvailableModules.slice(0, Math.max(4, Math.min(4, allAvailableModules.length)));

  const wardCollections = [
    { ward: "Ward 12 · Dharampeth", amount: 142000, width: "92%" },
    { ward: "Ward 4 · Sitabuldi", amount: 106000, width: "70%" },
    { ward: "Ward 19 · Hingna", amount: 78400, width: "52%" },
    { ward: "Ward 7 · Mankapur", amount: 61200, width: "40%" },
    { ward: "Ward 3 · Itwari", amount: 49900, width: "32%" },
  ];

  return (
    <div className="employee-workspace-layout">
      {/* 1. TOP GREETING HEADER */}
      <div className="employee-workspace-topbar">
        <div className="employee-workspace-greeting">
          <h1 className="employee-greeting-title">Welcome, {userName}</h1>
          <p className="employee-greeting-sub">
            {userRole} — {dateStr} · shift 09:30–17:30
          </p>
        </div>

        <div className="employee-workspace-actions">
          <button
            className="employee-btn-new"
            onClick={() => setShowQuickMenu((prev) => !prev)}
            aria-expanded={showQuickMenu}
          >
            <span className="plus-symbol">+</span> New
            <span className="dropdown-arrow">▾</span>
          </button>

          {showQuickMenu && (
            <div className="quick-action-dropdown-card">
              {quickActions.map((action, idx) => (
                <div
                  key={idx}
                  className="quick-action-dropdown-item"
                  onClick={() => {
                    setShowQuickMenu(false);
                    navigate(action.link);
                  }}
                >
                  <span className="quick-action-icon">{action.icon}</span>
                  <span className="quick-action-label">{action.label}</span>
                  <span className="quick-action-shortcut">{action.key}</span>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* 2. "ACT NOW" HERO SLA RISK BANNER */}
      {isDashboardLoading ? (
        <ActNowSkeleton />
      ) : allQueueItems.length > 0 ? (
        <div className="hero-act-now-banner">
          <div className="act-now-header">
            <div className="act-now-title-group">
              <span className="act-now-pulse-dot" aria-hidden="true" />
              <span className="act-now-title">Act now</span>
              <span className="act-now-desc">
                Highest SLA risk first — open each item to review and give consent before approval
              </span>
            </div>
            {allQueueItems.length > 4 ? (
              <button
                className="act-now-view-all-btn"
                onClick={() => setShowAllQueue((prev) => !prev)}
                aria-label="Toggle view all applications in SLA queue"
              >
                {showAllQueue ? "Show Less ↑" : `View all (${allQueueItems.length}) →`}
              </button>
            ) : (
              <div className="act-now-hint">Open to review · consent before approval</div>
            )}
          </div>

          <div className={`act-now-queue-grid ${gridColClass}`}>
            {displayedQueueItems.map((item, index) => (
              <div key={index} className="act-now-queue-card">
                <div className="queue-card-top">
                  <span className="queue-module-badge">{item.code} · {item.id}</span>
                  <span className={`queue-sla-tag queue-sla-tag--${item.tagType}`}>{item.tag}</span>
                </div>
                <h3 className="queue-card-title">{item.title}</h3>
                <p className="queue-card-meta">{item.meta}</p>
                <button
                  className="queue-card-action-btn"
                  onClick={() => navigate(item.link)}
                >
                  Open application
                </button>
              </div>
            ))}
          </div>
        </div>
      ) : null}

      {/* 3. 5 KPI METRIC CARDS ROW */}


      {/* 4. ROLE-BASED MODULE METRICS FROM ORIGINAL API */}
      {isDashboardLoading ? (
        <ModuleBreakdownSkeleton />
      ) : hasApiData ? (
        <div className={`employee-module-breakdown-section ${moduleEntries.length === 1 ? "is-single-module" : "is-multi-module"}`}>
          <div className="module-breakdown-header">
            <span className="module-breakdown-title">
              {moduleEntries.length === 1 ? "SERVICE PERFORMANCE OVERVIEW" : "ROLE-BASED SERVICE METRICS"}
            </span>
            <span className="module-breakdown-count">
              {moduleEntries.length === 1 ? "Live Role Stats" : `${moduleEntries.length} Services Monitored`}
            </span>
          </div>

          <div className={`module-breakdown-grid count-${moduleEntries.length} ${moduleEntries.length === 1 ? "single-card-grid" : ""}`}>
            {moduleEntries.map(([modName, modData], idx) => {
              const modStyle = getModuleStyle(modName);
              const title = t(`${modName}_DASHBOARD`) !== `${modName}_DASHBOARD`
                ? t(`${modName}_DASHBOARD`)
                : (t(`ACTION_TEST_${modName}`) || `${modName} Service`);
              const inboxUrl = `/upyog-ui/employee/${String(modName).toLowerCase()}/inbox`;

              return (
                <div
                  key={idx}
                  className={`module-breakdown-card ${moduleEntries.length === 1 ? "is-fullwidth-card" : ""}`}
                >
                  <div className="module-breakdown-card-top">
                    <div className="module-top-left">
                      <span
                        className={`module-breakdown-badge module-breakdown-badge--${modStyle.code.toLowerCase()}`}
                      >
                        {modStyle.code}
                      </span>
                      <div>
                        <h4 className="module-breakdown-name">{title}</h4>
                        <span className="module-breakdown-sub">Active Operational Metrics</span>
                      </div>
                    </div>
                    <button
                      className="module-inbox-link-btn"
                      onClick={() => navigate(inboxUrl)}
                    >
                      View Inbox →
                    </button>
                  </div>

                  <div className="module-breakdown-kpis">
                    <div className="breakdown-kpi kpi-received">
                      <div className="kpi-head">
                        <span className="breakdown-kpi-label">{t("ES_APPLICATION_RECEIVED") || "Received"}</span>
                      </div>
                      <span className="breakdown-kpi-val">{formatNumbers(modData.applicationReceived || 0)}</span>
                    </div>

                    <div className="breakdown-kpi kpi-amount">
                      <div className="kpi-head">
                        <span className="breakdown-kpi-label">{t("ES_TOTAL_AMOUNT") || "Collections"}</span>
                      </div>
                      <span className="breakdown-kpi-val">{formatIndianCurrency(modData.totalAmount || 0)}</span>
                    </div>

                    <div className="breakdown-kpi kpi-pending">
                      <div className="kpi-head">
                        <span className="breakdown-kpi-label">{t("ES_APPLICATION_PENDING") || "Pending"}</span>
                      </div>
                      <span className="breakdown-kpi-val text-amber">{formatNumbers(modData.applicationPending || 0)}</span>
                    </div>

                    <div className="breakdown-kpi kpi-approved">
                      <div className="kpi-head">
                        <span className="breakdown-kpi-label">{t("ES_APPLICATION_APPROVED") || "Approved"}</span>
                      </div>
                      <span className="breakdown-kpi-val text-green">{formatNumbers(modData.applicationApproved || 0)}</span>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      ) : null}

      {/* 5. PINNED SERVICES SECTION */}
      {allAvailableModules.length > 0 && (
        <div className={`employee-pinned-services-section ${showAllModules ? "is-expanded" : ""}`}>
          <div className="pinned-services-header">
            <div className="pinned-services-title-group">
              <span className="pinned-services-title">PINNED SERVICES & WORKSPACES</span>
              <span className="pinned-services-count-badge">
                {showAllModules
                  ? `All ${renderedCardCount || allAvailableModules.length} active services`
                  : `Showing 4 of ${renderedCardCount || allAvailableModules.length} services`}
              </span>
            </div>
            {(renderedCardCount > 4 || allAvailableModules.length > 4) && (
              <button
                className="pinned-services-toggle-btn"
                onClick={() => setShowAllModules((prev) => !prev)}
                aria-label="Toggle view all modules"
              >
                {showAllModules
                  ? "Show less (4 primary) ↑"
                  : `View all (${renderedCardCount || allAvailableModules.length}) services →`}
              </button>
            )}
          </div>

          <div ref={cardsContainerRef} className="ground-container moduleCardWrapper gridModuleWrapper">
            {allAvailableModules.map(({ code }) => {
              const Card = Digit.ComponentRegistryService.getComponent(`${code}Card`) || (() => null);
              return <Card key={code} />;
            })}
          </div>

          {/* {(renderedCardCount > 4 || allAvailableModules.length > 4) && (
            <div className="pinned-services-bottom-action">
              <button
                className="pinned-services-expand-btn"
                onClick={() => setShowAllModules((prev) => !prev)}
              >
                <span className="expand-btn-text">
                  {showAllModules
                    ? "Collapse to 4 primary municipal services"
                    : `View all ${renderedCardCount || allAvailableModules.length} municipal services & modules`}
                </span>
                <span className="expand-btn-icon">{showAllModules ? "▲" : "▼"}</span>
              </button>
            </div>
          )} */}
        </div>
      )}
    </div>
  );
};

export default EmployeeDashboard;
