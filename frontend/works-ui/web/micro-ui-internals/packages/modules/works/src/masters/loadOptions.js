import masters from "./worksMasters.json";

/**
 * Maps form master names (used in actionPlan.form.json) to live MDMS modules/masters
 * defined in upyog-mdms-data PR #622, with automatic fallback to worksMasters.json.
 */

const WORKS_MDMS_CACHE = {};

const fetchWorksMdms = async (stateId) => {
  if (WORKS_MDMS_CACHE[stateId]) {
    return WORKS_MDMS_CACHE[stateId];
  }

  const promise = Digit.MDMSService.call(stateId, {
    moduleDetails: [
      {
        moduleName: "WorksManagement",
        masterDetails: [
          { name: "Scheme" },
          { name: "RecommendedBy" },
          { name: "Purpose" },
          { name: "TypeOfWork" },
          { name: "WorkCategory" },
          { name: "ComponentCategory" },
          { name: "FundSource" },
          { name: "GovernmentGrant" },
          { name: "OwnSourceRevenue" },
          { name: "ApprovingDepartment" },
          { name: "DocumentType" },
          { name: "ActionPlanConfig" },
        ],
      },
      {
        moduleName: "FinanceService",
        masterDetails: [{ name: "Scheme" }],
      },
      {
        moduleName: "egf-master",
        masterDetails: [{ name: "FinancialYear" }],
      },
      {
        moduleName: "common-masters",
        masterDetails: [{ name: "StateInfo" }],
      },
      {
        moduleName: "tenant",
        masterDetails: [{ name: "district" }, { name: "District" }, { name: "tenants" }],
      },
    ],
  })
    .then((res) => res?.MdmsRes || {})
    .catch((err) => {
      delete WORKS_MDMS_CACHE[stateId];
      console.error("Failed to fetch Works MDMS data, falling back to local masters:", err);
      return null;
    });

  WORKS_MDMS_CACHE[stateId] = promise;
  return promise;
};

const fallbackOptions = (masterName) =>
  (masters[masterName] ?? [])
    .filter((r) => r.active !== false)
    .map((r) => ({ value: r.code, label: r.name, parent: r.parent }));

const toActiveOptions = (list = []) =>
  list
    .filter((r) => r && r.active !== false)
    .map((r) => ({ value: r.code, label: r.name || r.code, parent: r.parent }));

export async function loadOptions(spec) {
  if (spec.source !== "mdms") return [];

  const stateId =
    Digit?.ULBService?.getStateId?.() ||
    Digit?.ULBService?.getCurrentTenantId?.()?.split(".")?.[0] ||
    "pg";

  const mdmsRes = await fetchWorksMdms(stateId);
  if (!mdmsRes) {
    return fallbackOptions(spec.master);
  }

  const wm = mdmsRes?.WorksManagement || {};
  const fs = mdmsRes?.FinanceService || {};
  const egf = mdmsRes?.["egf-master"] || {};
  const commonMasters = mdmsRes?.["common-masters"] || {};
  const tenantMdms = mdmsRes?.tenant || {};

  // Scheme.json lives under FinanceService/Scheme.json with moduleName: WorksManagement
  const schemesList =
    (Array.isArray(wm.Scheme) && wm.Scheme.length > 0 ? wm.Scheme : null) ||
    (Array.isArray(fs.Scheme) && fs.Scheme.length > 0 ? fs.Scheme : null) ||
    [];

  switch (spec.master) {
    case "FinancialYear": {
      const allFY = egf.FinancialYear || [];
      const worksFY = allFY.filter((r) => r.active !== false && r.module === "WorksManagement");
      const list = worksFY.length > 0 ? worksFY : allFY.filter((r) => r.active !== false);
      return list.length > 0
        ? list.map((r) => ({ value: r.code, label: r.name || r.finYearRange || r.code }))
        : fallbackOptions("FinancialYear");
    }

    case "State": {
      const states = commonMasters.StateInfo || [];
      const opts = states
        .filter((s) => s && (s.code || s.stateCode))
        .map((s) => ({
          value: (s.code || s.stateCode).toUpperCase(),
          label: s.name || s.code || s.stateCode,
        }));
      return opts.length > 0 ? opts : fallbackOptions("State");
    }

    case "District": {
      const defaultStateCode = (
        commonMasters.StateInfo?.[0]?.code ||
        stateId ||
        "PG"
      ).toUpperCase();

      const rawDistricts =
        (Array.isArray(tenantMdms.district) && tenantMdms.district.length > 0 && tenantMdms.district) ||
        (Array.isArray(tenantMdms.District) && tenantMdms.District.length > 0 && tenantMdms.District) ||
        [];

      if (rawDistricts.length > 0) {
        const opts = rawDistricts
          .filter((d) => d && d.active !== false && (d.code || d.districtCode || d.name))
          .map((d) => ({
            value: d.code || d.districtCode || d.name,
            label: d.name || d.districtName || d.code || d.districtCode,
            parent: (d.stateCode || d.parent || defaultStateCode).toUpperCase(),
          }));
        if (opts.length > 0) return opts;
      }

      // Fallback 1: derive unique districts from tenant.tenants (city.districtCode / districtName)
      const tenants = Array.isArray(tenantMdms.tenants) ? tenantMdms.tenants : [];
      const seen = new Set();
      const fromTenants = [];
      tenants.forEach((t) => {
        const code = t?.city?.districtCode || t?.city?.districtTenantCode;
        const name = t?.city?.districtName || code;
        if (code && !seen.has(code)) {
          seen.add(code);
          fromTenants.push({
            value: code,
            label: name,
            parent: defaultStateCode,
          });
        }
      });
      if (fromTenants.length > 0) return fromTenants;

      // Fallback 2: local worksMasters.json aligned with defaultStateCode
      return fallbackOptions("District").map((d) => ({
        ...d,
        parent: d.parent === "PB" ? defaultStateCode : d.parent,
      }));
    }

    case "Scheme": {
      const opts = toActiveOptions(schemesList);
      return opts.length > 0 ? opts : fallbackOptions("Scheme");
    }

    case "SubScheme": {
      const subSchemes = [];
      schemesList
        .filter((s) => s && s.active !== false)
        .forEach((scheme) => {
          (scheme.subSchemes || [])
            .filter((sub) => sub && sub.active !== false)
            .forEach((sub) => {
              subSchemes.push({
                value: sub.code,
                label: sub.name || sub.code,
                parent: scheme.code,
              });
            });
        });
      return subSchemes.length > 0 ? subSchemes : fallbackOptions("SubScheme");
    }

    case "RecommendedBy": {
      const opts = toActiveOptions(wm.RecommendedBy);
      return opts.length > 0 ? opts : fallbackOptions("RecommendedBy");
    }

    case "PurposeOfProject": {
      const opts = toActiveOptions(wm.Purpose);
      return opts.length > 0 ? opts : fallbackOptions("PurposeOfProject");
    }

    case "TypeOfWork": {
      const opts = toActiveOptions(wm.TypeOfWork);
      return opts.length > 0 ? opts : fallbackOptions("TypeOfWork");
    }

    case "WorkCategory": {
      const opts = toActiveOptions(wm.WorkCategory);
      return opts.length > 0 ? opts : fallbackOptions("WorkCategory");
    }

    case "ComponentCategory": {
      const opts = toActiveOptions(wm.ComponentCategory);
      return opts.length > 0 ? opts : fallbackOptions("ComponentCategory");
    }

    case "ComponentSubCategory": {
      const categories = wm.ComponentCategory || [];
      const subCategories = [];
      categories
        .filter((c) => c && c.active !== false)
        .forEach((cat) => {
          (cat.subCategories || [])
            .filter((sub) => sub && sub.active !== false)
            .forEach((sub) => {
              subCategories.push({
                value: sub.code,
                label: sub.name || sub.code,
                parent: cat.code,
              });
            });
        });
      return subCategories.length > 0 ? subCategories : fallbackOptions("ComponentSubCategory");
    }

    case "FundSource": {
      const opts = toActiveOptions(wm.FundSource);
      return opts.length > 0 ? opts : fallbackOptions("FundSource");
    }

    case "GrantType": {
      const opts = toActiveOptions(wm.GovernmentGrant);
      return opts.length > 0 ? opts : fallbackOptions("GrantType");
    }

    case "OsrSource": {
      const opts = toActiveOptions(wm.OwnSourceRevenue);
      return opts.length > 0 ? opts : fallbackOptions("OsrSource");
    }

    case "ApprovingDepartment": {
      const opts = toActiveOptions(wm.ApprovingDepartment);
      return opts.length > 0 ? opts : fallbackOptions("ApprovingDepartment");
    }

    default:
      return fallbackOptions(spec.master);
  }
}
