import React, { useMemo } from "react";
import { ArrowRightInbox } from "./svgindex";
import { Link } from "react-router-dom";

const CARD_ACCENTS = [
  "#1d4ed8", // Royal Blue (PT)
  "#059669", // Emerald Green (TL)
  "#7c3aed", // Royal Purple (WS)
  "#d97706", // Amber Gold (MC)
  "#dc2626", // Crimson Red (PGR)
  "#db2777", // Hot Pink (PTR)
  "#0f766e", // Deep Teal (BND)
  "#86198f", // Plum (BPA)
  "#ea580c", // Burnt Orange (NOC)
  "#334155", // Charcoal Slate (FSM)
];

const KNOWN_MODULE_STYLES = [
  { match: /property\s*tax|\bpt\b/i, code: "PT", color: "#1d4ed8" }, // Blue
  { match: /trade\s*licen[cs]e|\btl\b/i, code: "TL", color: "#059669" }, // Green
  { match: /water\s*&\s*sewerage|water and sewerage|^water$|sewerage|\bws\b|\bsw\b/i, code: "WS", color: "#7c3aed" }, // Purple
  { match: /miscellaneous|mcollect|m.?collect|\bmc\b/i, code: "MC", color: "#d97706" }, // Amber Gold
  { match: /grievance|\bpgr\b|complaint/i, code: "PGR", color: "#dc2626" }, // Crimson Red
  { match: /pet\s*reg|\bptr\b/i, code: "PTR", color: "#db2777" }, // Pink
  { match: /birth|death|\bbnd\b/i, code: "BND", color: "#0f766e" }, // Teal
  { match: /building|obps|bpa/i, code: "BPA", color: "#86198f" }, // Plum
  { match: /\bnoc\b|objection|fire/i, code: "NOC", color: "#ea580c" }, // Orange
  { match: /faecal|fsm|desludg|vehicle\s*log/i, code: "FSM", color: "#334155" }, // Slate
  { match: /community\s*hall|\bchb\b|venue/i, code: "CHB", color: "#b45309" }, // Bronze
  { match: /hrms|hr management/i, code: "HR", color: "#475569" },
  { match: /advertisement|\bads\b/i, code: "ADS", color: "#9333ea" },
  { match: /e.?waste/i, code: "EW", color: "#9a3412" },
  { match: /vendor/i, code: "VM", color: "#0d9488" },
  { match: /receipt/i, code: "RC", color: "#2563eb" },
  { match: /bill\s*amend|bill\s*genie|\bbill\b/i, code: "BL", color: "#c2410c" },
  { match: /dashboard|dss/i, code: "DSS", color: "#4f46e5" },
  { match: /survey|engagement/i, code: "ENG", color: "#0284c7" },
  { match: /water\s*tanker|\bwt\b/i, code: "WT", color: "#0284c7" },
  { match: /mobile\s*toilet|\bmt\b/i, code: "MT", color: "#78350f" },
  { match: /tree\s*prun|\btp\b/i, code: "TP", color: "#16a34a" },
  { match: /\bgis\b/i, code: "GIS", color: "#64748b" },
  { match: /\btqm\b|quality/i, code: "TQM", color: "#6366f1" },
  { match: /no\s*dues|\bndc\b/i, code: "NDC", color: "#9f1239" },
];

const extractText = (value) => {
  if (value == null) return "";
  if (typeof value === "string" || typeof value === "number") return String(value);
  if (Array.isArray(value)) return value.map(extractText).join(" ");
  if (React.isValidElement(value)) return extractText(value.props?.children);
  return "";
};

const findKnownModuleStyle = (moduleName) => {
  const text = extractText(moduleName).trim();
  if (!text) return null;
  return KNOWN_MODULE_STYLES.find(({ match }) => match.test(text)) || null;
};

const detectModuleFromLinks = (links = [], kpis = []) => {
  const allUrls = [
    ...(links || []).map((l) => String(l?.link || "")),
    ...(kpis || []).map((k) => String(k?.link || "")),
  ].join(" ").toLowerCase();

  if (allUrls.includes("/pt/") || allUrls.includes("property")) return { code: "PT", color: "#1d4ed8" };
  if (allUrls.includes("/tl/") || allUrls.includes("trade")) return { code: "TL", color: "#059669" };
  if (allUrls.includes("/ws/") || allUrls.includes("water") || allUrls.includes("/sw/")) return { code: "WS", color: "#7c3aed" };
  if (allUrls.includes("/mcollect/") || allUrls.includes("challan") || allUrls.includes("uc")) return { code: "MC", color: "#d97706" };
  if (allUrls.includes("/pgr/") || allUrls.includes("complaint")) return { code: "PGR", color: "#dc2626" };
  if (allUrls.includes("/ptr/") || allUrls.includes("pet")) return { code: "PTR", color: "#db2777" };
  if (allUrls.includes("/bnd/") || allUrls.includes("birth") || allUrls.includes("death")) return { code: "BND", color: "#0f766e" };
  if (allUrls.includes("/obps/") || allUrls.includes("/bpa/") || allUrls.includes("building")) return { code: "BPA", color: "#86198f" };
  if (allUrls.includes("/noc/") || allUrls.includes("fire")) return { code: "NOC", color: "#ea580c" };
  if (allUrls.includes("/fsm/") || allUrls.includes("faecal") || allUrls.includes("desludg")) return { code: "FSM", color: "#334155" };
  if (allUrls.includes("/hrms/")) return { code: "HR", color: "#475569" };
  if (allUrls.includes("/chb/")) return { code: "CHB", color: "#b45309" };
  if (allUrls.includes("/receipts/")) return { code: "RC", color: "#2563eb" };
  if (allUrls.includes("/bills/")) return { code: "BL", color: "#c2410c" };
  if (allUrls.includes("/dss/") || allUrls.includes("dashboard")) return { code: "DSS", color: "#4f46e5" };
  return null;
};

const getModuleAbbreviation = (moduleCode, moduleName, links = [], kpis = []) => {
  if (moduleCode) return String(moduleCode).replace(/[^a-zA-Z0-9]/g, "").slice(0, 3).toUpperCase();

  const detected = detectModuleFromLinks(links, kpis);
  if (detected?.code) return detected.code;

  const known = findKnownModuleStyle(moduleName);
  if (known) return known.code;

  const text = extractText(moduleName).trim();
  if (!text) return "MD";

  const words = text.replace(/[^a-zA-Z0-9\s&]/g, " ").split(/\s+/).filter(Boolean);
  if (words.length === 1) return words[0].slice(0, 3).toUpperCase();
  return words
    .slice(0, 3)
    .map((word) => word[0])
    .join("")
    .toUpperCase();
};

const getAccentColor = (moduleCode, moduleName, abbreviation, links = [], kpis = []) => {
  const code = String(moduleCode || abbreviation || "").toUpperCase();
  const knownByCode = KNOWN_MODULE_STYLES.find((k) => k.code === code);
  if (knownByCode?.color) return knownByCode.color;

  const detected = detectModuleFromLinks(links, kpis);
  if (detected?.color) return detected.color;

  const known = findKnownModuleStyle(moduleName);
  if (known?.color) return known.color;

  const allUrls = [
    ...(links || []).map((l) => String(l?.link || "")),
    ...(kpis || []).map((k) => String(k?.link || "")),
  ].join(" ");

  const text = String(moduleCode || extractText(moduleName) || abbreviation || allUrls || "");
  let hash = 0;
  for (let i = 0; i < text.length; i += 1) {
    hash = (hash << 5) - hash + text.charCodeAt(i);
    hash |= 0;
  }
  return CARD_ACCENTS[Math.abs(hash) % CARD_ACCENTS.length];
};

const formatCount = (count) => {
  if (count === 0) return "0";
  if (count === undefined || count === null || count === "") return "-";
  return count;
};

const EmployeeModuleCard = ({
  Icon,
  moduleName,
  moduleCode,
  kpis = [],
  links = [],
  isCitizen = false,
  className,
  styles,
  FsmHideCount,
}) => {
  const abbreviation = useMemo(
    () => getModuleAbbreviation(moduleCode, moduleName, links, kpis),
    [moduleCode, moduleName, links, kpis]
  );
  const accentColor = useMemo(
    () => getAccentColor(moduleCode, moduleName, abbreviation, links, kpis),
    [moduleCode, moduleName, abbreviation, links, kpis]
  );

  const cardClassName = ["employeeCard", "card-home", "customEmployeeCard", className]
    .filter(Boolean)
    .join(" ");

  return (
    <div
      className={cardClassName}
      style={styles || {}}
    >
      <div className="employeeCustomCard">
        <div className="employee-card-header">
          <div className="employee-card-top-row">
            <div className="employee-card-brand">
              <span className="employee-card-abbr" aria-hidden="true">
                {abbreviation}
              </span>
              <div className="employee-card-title-group">
                <h3 className="employee-card-title text-employee-card">{moduleName}</h3>
                <span className="employee-card-subtext">Authorized Desk</span>
              </div>
            </div>
            <span className="employee-card-live-dot" title="Active Municipal Service" />
          </div>

          {kpis.length > 0 && (
            <div className={`employee-card-kpis flex-fit${isCitizen ? " is-citizen" : ""}`}>
              {kpis.map(({ count, label, link }, index) => {
                const kpiContent = (
                  <>
                    <span className="employee-card-kpi-count">{formatCount(count)}</span>
                    <span className="employee-card-kpi-label employeeTotalLink">{label}</span>
                  </>
                );

                return (
                  <div className="employee-card-kpi card-count" key={index}>
                    {link ? (
                      <Link to={link} className="employee-card-kpi-link">
                        {kpiContent}
                      </Link>
                    ) : (
                      <div className="employee-card-kpi-link">{kpiContent}</div>
                    )}
                  </div>
                );
              })}
            </div>
          )}
        </div>

        <div className="employee-card-banner employee-card-actions">
          <div className="links-wrapper body">
            {links.map(({ label, link }, index) => (
              <React.Fragment key={index}>
                {index > 0 && <span className="employee-card-link-sep" aria-hidden="true">|</span>}
                <span className="link">
                  {link ? <Link to={link}>{label}</Link> : <span>{label}</span>}
                </span>
              </React.Fragment>
            ))}
          </div>
        </div>
      </div>
      {/* Keep Icon prop accepted for backwards compatibility with module cards */}
      {Icon ? <span className="employee-card-icon-legacy" hidden>{Icon}</span> : null}
    </div>
  );
};

const ModuleCardFullWidth = ({
  moduleName,
  links = [],
  isCitizen = false,
  className,
  styles,
  headerStyle,
  subHeader,
  subHeaderLink,
}) => {
  return (
    <div
      className={className ? className : "employeeCard card-home customEmployeeCard home-action-cards"}
      style={styles ? styles : {}}
    >
      <div className="complaint-links-container employee-module-card-links">
        <div className={`header ${isCitizen ? "header--citizen" : ""}`} style={!isCitizen && headerStyle ? headerStyle : {}}>
          <span className="text removeHeight">{moduleName}</span>
          <span className="link">
            <a href={subHeaderLink}>
              <span className="inbox-total inbox-total-flex">
                {subHeader || "-"}
                <span className="inbox-arrow-wrap">
                  <ArrowRightInbox />
                </span>
              </span>
            </a>
          </span>
        </div>
        <div className="body body--compact">
          <div className="links-wrapper links-wrapper--flex">
            {links.map(({ count, label, link }, index) => (
              <span className="link full-employee-card-link" key={index}>
                {link ? (
                  link?.includes("upyog-ui/") ? (
                    <Link to={link}>{label}</Link>
                  ) : (
                    <a href={link}>{label}</a>
                  )
                ) : null}
              </span>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export { EmployeeModuleCard, ModuleCardFullWidth };