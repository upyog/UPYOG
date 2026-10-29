import React, { useEffect, useMemo, useRef, useState } from "react";
import { Link, useLocation } from "react-router-dom";
import {
  HomeIcon,
  EditPencilIcon,
  LogoutIcon,
  PropertyHouse,
  CaseIcon,
  CollectionIcon,
  OBPSIcon,
  PGRIcon,
  FSMIcon,
  WSICon,
  MCollectIcon,
  Phone,
  BirthIcon,
  DeathIcon,
  FirenocIcon,
  CHBIcon,
  ArrowForward,
  ArrowVectorDown,
  PersonIcon,
  ReceiptIcon,
  DocumentIconSolid,
  DropIcon,
  CollectionsBookmarIcons,
  FinanceChartIcon,
  SearchIcon,
} from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import LogoutDialog from "../../Dialog/LogoutDialog";
// import { DEFAULT_PROFILE_IMAGE } from "./defaultProfileImage";

const DEFAULT_PROFILE_IMAGE =
  "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAO4AAADUCAMAAACs0e/bAAAAM1BMVEXK0eL" +
  "/" +
  "/" +
  "/" +
  "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";

/* -------------------------------------------------------------------------- */
/*                                  Constants                                 */
/* -------------------------------------------------------------------------- */

const EMPLOYEE_HOME_URL = "/upyog-ui/employee";
const PROFILE_URL = `${EMPLOYEE_HOME_URL}/user/profile`;
const FINANCE_ROOT = "Finance";
const FINANCE_ICON_KEY = "finance:insert-chart";
const FINANCE_PATH_STORAGE_KEY = "upyog_employee_sidebar_finance_path";
const FINANCE_PATH_MARKERS = ["/finance", "/services/EGF", "/services/finance"];
const GUEST_USER_UUID = "asdf";
const APP_VERSION_LABEL = "v5.1";

const FINANCE_META_KEYS = [
  "id",
  "name",
  "url",
  "displayName",
  "orderNumber",
  "parentModule",
  "serviceCode",
  "code",
  "leftIcon",
  "path",
  "navigationURL",
  "enabled",
];

const EMPTY_ARRAY = [];
const MICRO_UI_MDMS_CONFIG = [{ name: "microUiModuleEnable" }];
const SKELETON_LABEL_WIDTHS = [72, 58, 80, 64, 76, 52, 68];

const EMPLOYEE_PREFIX_REGEX = /^\/?(upyog-ui\/employee\/|upyog-ui\/|employee\/)/;
const DSS_MARKERS = ["integration/dss/", "/dss/"];
const MONO_UI_PREFIXES = ["/upyog-ui/employee/", "/digit-ui/employee/", "/upyog-ui/", "/digit-ui/"];
const ACTION_KEY_SEPARATOR_REGEX = /[ -]/g;
// Separator set used for leaf-level finance labels (kept exactly as originally written).
const LEAF_LABEL_SEPARATOR_REGEX = /[.:-\s\/]/g;

/* -------------------------------------------------------------------------- */
/*                                    Icons                                   */
/* -------------------------------------------------------------------------- */

const renderIcon = (Icon) => <Icon className="sidebar-icon" />;

// Explicit icon names coming from MDMS / access-control (`leftIcon`).
const ICON_BY_KEY = {
  homeicon: HomeIcon,
  home: HomeIcon,
  obpsicon: OBPSIcon,
  bpahomeicon: OBPSIcon,
  propertyhouse: PropertyHouse,
  propertyicon: PropertyHouse,
  commonpticon: PropertyHouse,
  pticon: PropertyHouse,
  caseicon: CaseIcon,
  tlicon: CaseIcon,
  pgricon: PGRIcon,
  complainticon: PGRIcon,
  fsmicon: FSMIcon,
  wsicon: WSICon,
  mcollecticon: MCollectIcon,
  chbicon: CHBIcon,
  collectionicon: CollectionIcon,
  billsicon: CollectionIcon,
  birthicon: BirthIcon,
  deathicon: DeathIcon,
  firenocicon: FirenocIcon,
  phone: Phone,
  editpencilicon: EditPencilIcon,
  logouticon: LogoutIcon,
  personicon: PersonIcon,
  receipticon: ReceiptIcon,
  documenticonsolid: DocumentIconSolid,
  dropicon: DropIcon,
  financecharticon: FinanceChartIcon,
  insertchart: FinanceChartIcon,
  finance: FinanceChartIcon,
  collectionsbookmarkicons: CollectionsBookmarIcons,
};

// Fallback: pick an icon from the module name. ORDER MATTERS - first match wins.
const ICON_NAME_RULES = [
  [["home"], HomeIcon],
  [["bpa", "obps", "building", "stakeholder"], OBPSIcon],
  [["grievance", "pgr", "complaint"], PGRIcon],
  [["property", "pt", "house", "assessment"], PropertyHouse],
  [["fire"], FirenocIcon],
  [["engagement", "notification", "phone"], Phone],
  [["mcollect", "collect", "challan"], MCollectIcon],
  [["trade", "tl", "license"], CaseIcon],
  [["dashboard", "finance", "chart", "dss"], FinanceChartIcon],
  [["fsm", "sludge", "desilting"], FSMIcon],
  [["water", "sewerage", "ws"], WSICon],
  [["chb", "community", "hall"], CHBIcon],
  [["birth"], BirthIcon],
  [["death"], DeathIcon],
  [["hrms", "employee", "person", "user"], PersonIcon],
  [["bill", "receipt", "collection"], CollectionIcon],
];

const toIconLookupKey = (value = "") => value.toLowerCase().replace(/[^a-z0-9]/g, "");

const getModuleIcon = (item) => {
  let rawIcon = item?.leftIcon || item?.icon?.leftIcon || item?.icon;

  if (!rawIcon && item?.links?.length > 0) {
    const linkWithIcon = item.links.find((l) => l?.leftIcon || l?.icon?.leftIcon || l?.icon);
    rawIcon = linkWithIcon?.leftIcon || linkWithIcon?.icon?.leftIcon || linkWithIcon?.icon;
  }

  if (typeof rawIcon === "string") {
    const iconName = rawIcon.includes(":") ? rawIcon.split(":")[1] : rawIcon;
    const Icon = ICON_BY_KEY[toIconLookupKey(iconName)];
    if (Icon) return renderIcon(Icon);
  }

  const nameToMatch = toIconLookupKey(item?.moduleName || item?.displayName || "");
  const rule = ICON_NAME_RULES.find(([keywords]) => keywords.some((keyword) => nameToMatch.includes(keyword)));
  return renderIcon(rule ? rule[1] : CollectionIcon);
};

/* -------------------------------------------------------------------------- */
/*                              Generic helpers                               */
/* -------------------------------------------------------------------------- */

const normalize = (s = "") => s.toString().trim().toLowerCase();

const toActionKey = (name = "") => `ACTION_TEST_${name.toUpperCase().replace(ACTION_KEY_SEPARATOR_REGEX, "_")}`;

/** Returns the translation of `key`, or `fallback` when no translation exists. */
const translateOrFallback = (t, key, fallback) => {
  const translated = t(key);
  return translated && translated !== key ? translated : fallback;
};

/** Translates an access-control module / action name, falling back to the raw name. */
const translateAction = (t, name = "") => translateOrFallback(t, toActionKey(name), name);

const byOrderNumber = (a, b) => (a?.orderNumber ?? 100) - (b?.orderNumber ?? 100);

/* -------------------------------------------------------------------------- */
/*                              URL helpers                                   */
/* -------------------------------------------------------------------------- */

const stripEmployeePrefix = (url) => url.replace(EMPLOYEE_PREFIX_REGEX, "");
const removeLeadingSlash = (url) => (url.startsWith("/") ? url.substring(1) : url);

const isExternalOrMono = (url) => {
  if (!url) return false;
  return url.startsWith("http://") || url.startsWith("https://") || url.startsWith("/employee/");
};

const resolveNavUrl = (navUrl) => {
  if (!navUrl) return "#";
  let formatted = navUrl.replace("/digit-ui/", "/upyog-ui/");

  if (formatted.startsWith("http://") || formatted.startsWith("https://")) return formatted;

  // MDMS screens are served by the mono-UI
  if (formatted.includes("mdms/")) {
    return "/employee/" + removeLeadingSlash(stripEmployeePrefix(formatted));
  }

  // DSS dashboards
  if (formatted.includes("integration/dss") || formatted.includes("/dss/")) {
    const marker = DSS_MARKERS.find((m) => formatted.includes(m));
    const subPath = marker ? formatted.split(marker)[1] || "home" : "home";
    const cleanSub = subPath.replace(/^\/+|\/+$/g, "");

    if (cleanSub === "home" || cleanSub === "NURT_DASHBOARD") return `${EMPLOYEE_HOME_URL}/dss/landing/${cleanSub}`;
    if (cleanSub.startsWith("landing/") || cleanSub.startsWith("dashboard/")) return `${EMPLOYEE_HOME_URL}/dss/${cleanSub}`;
    return `${EMPLOYEE_HOME_URL}/dss/dashboard/${cleanSub}`;
  }

  // Finance (EGF) screens
  if (formatted.includes("services/EGF") || formatted.includes("services/egf") || formatted.includes("/finance/")) {
    if (formatted.startsWith(`${EMPLOYEE_HOME_URL}/`)) return formatted;
    return `${EMPLOYEE_HOME_URL}/` + removeLeadingSlash(stripEmployeePrefix(formatted));
  }

  if (formatted.startsWith("/employee/")) {
    return "/upyog-ui" + formatted;
  }
  if (!formatted.startsWith("/upyog-ui/")) {
    return formatted.startsWith("/") ? EMPLOYEE_HOME_URL + formatted : `${EMPLOYEE_HOME_URL}/` + formatted;
  }
  return formatted;
};

/** Converts a micro-UI URL into its mono-UI equivalent (used when a module's micro-UI is disabled). */
const toMonoUiUrl = (url) => {
  if (url.startsWith("/employee/") || url.startsWith("/digit-ui/")) return url;
  const clean = MONO_UI_PREFIXES.reduce((acc, prefix) => acc.replace(prefix, ""), url);
  return clean.startsWith("/") ? `/employee${clean}` : `/employee/${clean}`;
};

const isPathActive = (pathname, url) =>
  pathname === url || Boolean(url && url !== EMPLOYEE_HOME_URL && pathname.startsWith(url));

const isUrlMatching = (pathname, navUrl) => {
  if (!pathname || !navUrl) return false;
  const resolved = resolveNavUrl(navUrl);
  if (!resolved || resolved === "#") return false;

  const cleanPath = pathname.split("?")[0].replace(/\/+$/, "").toLowerCase();
  const cleanResolved = resolved.split("?")[0].replace(/\/+$/, "").toLowerCase();

  if (cleanPath === cleanResolved) return true;
  if (cleanPath.length > 15 && cleanResolved.length > 15 && cleanPath.startsWith(cleanResolved + "/")) return true;

  const toRelative = (url) => stripEmployeePrefix(url).replace(/^\/+/, "");
  const relativePath = toRelative(cleanPath);
  const relativeResolved = toRelative(cleanResolved);

  if (relativePath === relativeResolved) return true;
  return relativePath.length > 5 && relativeResolved.length > 5 && relativePath.startsWith(relativeResolved + "/");
};

/* -------------------------------------------------------------------------- */
/*                               Finance helpers                              */
/* -------------------------------------------------------------------------- */

const isFinancePathname = (pathname = "") => FINANCE_PATH_MARKERS.some((marker) => pathname.includes(marker));

/** Broad check - "does this action belong to the Finance module at all?" (used to detect availability). */
const isFinanceAction = (action) => {
  const parentModule = action.parentModule?.toLowerCase();
  const nav = action.navigationURL;
  return Boolean(
    action.path?.startsWith("Finance") ||
    action.path?.startsWith("EGF") ||
    nav?.includes("/finance") ||
    nav?.includes("services/EGF") ||
    nav?.includes("services/egf") ||
    nav?.includes("services/finance") ||
    parentModule?.includes("egf") ||
    parentModule?.includes("finance")
  );
};

/** Narrow check - "should this action's path be re-rooted under Finance?" */
const isFinanceMenuItem = (item) => {
  if (!item.path) return false;
  const parentModule = item.parentModule?.toLowerCase() || "";
  const nav = item.navigationURL?.toLowerCase() || "";
  return (
    item.path.startsWith("Finance") ||
    item.path.startsWith("EGF") ||
    parentModule.includes("egf") ||
    parentModule.includes("finance") ||
    nav.includes("services/egf") ||
    nav.includes("services/finance")
  );
};

const normalizeFinancePath = (path) => {
  if (path.startsWith("EGF")) return path.replace("EGF", FINANCE_ROOT);
  return path.startsWith(FINANCE_ROOT) ? path : `${FINANCE_ROOT}.${path}`;
};

const buildFinanceMenuPath = (item) => {
  const path = normalizeFinancePath(item.path);
  if (item.displayName && path.includes(".")) {
    const parts = path.split(".");
    parts[parts.length - 1] = item.displayName;
    return parts.join(".");
  }
  return path;
};

const getChildKeys = (node) => Object.keys(node).filter((key) => !FINANCE_META_KEYS.includes(key));

const hasSubMenuChildren = (node) => {
  if (!node || typeof node !== "object") return false;
  return getChildKeys(node).some((k) => node[k] && typeof node[k] === "object" && !Array.isArray(node[k]));
};

const getNodeByPath = (tree, path) => {
  if (!path) return tree;
  return path.split(".").reduce((node, part) => (node && node[part] ? node[part] : null), tree);
};

const getMinOrderNumber = (node) => {
  if (!node || typeof node !== "object") return 9999;
  if (typeof node.orderNumber === "number") return node.orderNumber;
  if (node.orderNumber !== undefined && !isNaN(Number(node.orderNumber))) return Number(node.orderNumber);

  return getChildKeys(node).reduce((min, key) => {
    const child = node[key];
    return child && typeof child === "object" ? Math.min(min, getMinOrderNumber(child)) : min;
  }, 9999);
};

/** Label shown for a node of the finance tree (parent folders and leaf pages). */
const getFinanceNodeLabel = (node, key, t, i18n) => {
  const hasChildren = hasSubMenuChildren(node);

  let translationKey;
  if (hasChildren || !node?.displayName) {
    translationKey = toActionKey(key);
  } else {
    translationKey = `ACTION_TEST_${node.displayName.toUpperCase().replace(LEAF_LABEL_SEPARATOR_REGEX, "_")}`;
  }

  if (i18n.exists(translationKey)) return t(translationKey);
  return hasChildren ? key : node?.displayName || key;
};

const hasMatchingDescendant = (node, key, search, t, i18n) => {
  if (!search) return true;
  if (getFinanceNodeLabel(node, key, t, i18n).toLowerCase().includes(search.toLowerCase())) return true;

  return (
    hasSubMenuChildren(node) &&
    getChildKeys(node).some((childKey) => hasMatchingDescendant(node[childKey], childKey, search, t, i18n))
  );
};

const nodeContainsActivePage = (node, pathname) =>
  getChildKeys(node).some((key) => {
    const child = node[key];
    return child && typeof child === "object" && child.navigationURL && isUrlMatching(pathname, child.navigationURL);
  });

const getFinancePathFromCurrentUrl = (pathname, actions, isFinanceEnabled) => {
  if (!pathname || !actions?.length || !isFinanceEnabled || !isFinancePathname(pathname)) return "";

  const matchedAction = actions
    .filter((action) => action?.navigationURL && action?.path && isUrlMatching(pathname, action.navigationURL))
    .sort((a, b) => resolveNavUrl(b.navigationURL).length - resolveNavUrl(a.navigationURL).length)[0];

  if (!matchedAction?.path) return "";

  const itemPath = normalizeFinancePath(matchedAction.path);
  const lastDot = itemPath.lastIndexOf(".");
  return lastDot > -1 ? itemPath.slice(0, lastDot) : itemPath;
};

/* -------------------------------------------------------------------------- */
/*                          micro-UI module toggles                           */
/* -------------------------------------------------------------------------- */

const findMicroUiConfig = (item, configs) => {
  const pathRoot = item.path?.split?.(".")[0]?.toLowerCase() || "";
  const parentModule = item.parentModule?.toLowerCase() || "";
  const navUrl = item.navigationURL?.toLowerCase() || "";

  return configs.find((config) => {
    const code = config.code?.toLowerCase();
    const isFinanceCode =
      code === "finance" &&
      (pathRoot === "egf" ||
        parentModule.includes("egf") ||
        navUrl.includes("services/egf") ||
        navUrl.includes("services/finance"));

    return pathRoot === code || parentModule.includes(code) || navUrl.includes(code) || isFinanceCode;
  });
};

/* -------------------------------------------------------------------------- */
/*                          Photo helpers (profile)                           */
/* -------------------------------------------------------------------------- */

const isInlineImage = (photo) => photo.startsWith("http") || photo.startsWith("data:");

const fetchFileUrl = async (fileStoreId, tenantId) => {
  const file = await Digit.UploadServices.Filefetch([fileStoreId], tenantId);
  return file?.data?.fileStoreIds?.[0]?.url?.split(",")?.[0];
};

/* -------------------------------------------------------------------------- */
/*                        Small presentational components                     */
/* -------------------------------------------------------------------------- */

const getInitials = (name = "") =>
  name
    .split(" ")
    .map((word) => word[0])
    .join("")
    .toUpperCase();

const Profile = ({ info, profilePhotoUrl, isSidebarCollapsed }) => {
  const username = isSidebarCollapsed ? getInitials(info?.name) : info?.name;

  return (
    <div className={`profile-section ${isSidebarCollapsed ? "profile-section--collapsed" : ""}`}>
      <div className="imageloader imageloader-loaded">
        <img
          className="img-responsive img-circle img-Profile"
          src={profilePhotoUrl || DEFAULT_PROFILE_IMAGE}
          alt="Profile"
          onError={(e) => {
            e.target.onerror = null;
            e.target.src = DEFAULT_PROFILE_IMAGE;
          }}
        />
      </div>
      <div id="profile-name" className="label-container name-Profile">
        <div className="label-text">{username}</div>
      </div>
      {!isSidebarCollapsed && (
        <div id="profile-location" className="label-container loc-Profile">
          <div className="label-text">{info?.mobileNumber}</div>
        </div>
      )}
      {!isSidebarCollapsed && info?.emailId && (
        <div id="profile-emailid" className="label-container loc-Profile">
          <div className="label-text">{info?.emailId}</div>
        </div>
      )}
      <div className="profile-divider"></div>
    </div>
  );
};

/** Renders an in-app <Link>, or a plain <a> for external / mono-UI targets. */
const SidebarLink = ({ url, className, title, onClick, children }) =>
  isExternalOrMono(url) ? (
    <a href={url} className={className} title={title} onClick={onClick}>
      {children}
    </a>
  ) : (
    <Link to={url} className={className} title={title} onClick={onClick}>
      {children}
    </Link>
  );

/** Top-level clickable row (module header, back button, finance header). */
const MenuHeader = ({ title, label, icon, active, collapsed, chevron, onClick }) => (
  <div className={`sidebar-list sidebar-pointer-item ${active ? "active" : ""}`} onClick={onClick}>
    <div className="menu-item" title={title}>
      {icon}
      {!collapsed && <div className="menu-label">{label}</div>}
      {!collapsed && chevron && <div className="accordion-chevron">{chevron}</div>}
    </div>
  </div>
);

/** Second-level navigable row. `leading` is either a bullet or a module icon. */
const SubMenuLink = ({ url, active, title, label, leading, onNavigate }) => (
  <div className={`sidebar-sublist ${active ? "active" : ""}`}>
    <SidebarLink url={url} className="submenu-item" title={title} onClick={onNavigate}>
      {leading}
      <span className="submenu-label">{label}</span>
    </SidebarLink>
  </div>
);

const SidebarSkeleton = ({ isSidebarCollapsed }) => (
  <div className={`employee-sidebar-inner sidebar-skeleton-container ${isSidebarCollapsed ? "sidebar-skeleton--collapsed" : ""}`}>
    <div className="sidebar-logo-header">
      <div className="sidebar-skeleton-pulse sidebar-skeleton-logo" />
    </div>

    <div className="profile-section sidebar-skeleton-profile">
      <div className="sidebar-skeleton-pulse sidebar-skeleton-avatar" />
      {!isSidebarCollapsed && (
        <div className="sidebar-skeleton-profile-info">
          <div className="sidebar-skeleton-pulse sidebar-skeleton-text-name" />
          <div className="sidebar-skeleton-pulse sidebar-skeleton-text-sub" />
        </div>
      )}
      <div className="profile-divider" />
    </div>

    {!isSidebarCollapsed && (
      <div className="sidebar-skeleton-search-wrap">
        <div className="sidebar-skeleton-pulse sidebar-skeleton-search" />
      </div>
    )}

    <div className="drawer-desktop sidebar-skeleton-menu-list">
      {SKELETON_LABEL_WIDTHS.map((width, i) => (
        <div className="sidebar-skeleton-item" key={i}>
          <div className="sidebar-skeleton-pulse sidebar-skeleton-icon" />
          {!isSidebarCollapsed && (
            <>
              <div className="sidebar-skeleton-pulse sidebar-skeleton-label" style={{ width: `${width}%` }} />
              <div className="sidebar-skeleton-pulse sidebar-skeleton-chevron" />
            </>
          )}
        </div>
      ))}
    </div>
    <div className="citizen-logout sidebar-skeleton-logout">
      <div className="sidebar-skeleton-pulse sidebar-skeleton-logout-btn" />
    </div>
  </div>
);

/* -------------------------------------------------------------------------- */
/*                                  Component                                 */
/* -------------------------------------------------------------------------- */

const EmployeeSideBar = ({ isSidebarCollapsed = false, closeMobileSidebar, toggleSidebarWidth }) => {
  const { isLoading, data } = Digit.Hooks.useAccessControl();
  const { t, i18n } = useTranslation();
  const { pathname } = useLocation();

  const [search, setSearch] = useState("");
  const [openMenus, setOpenMenus] = useState({});
  const [showDialog, setShowDialog] = useState(false);
  const [profilePhotoUrl, setProfilePhotoUrl] = useState(null);
  const [activeFinancePath, setActiveFinancePath] = useState(() => {
    if (!isFinancePathname(pathname)) return "";
    const stored = sessionStorage.getItem(FINANCE_PATH_STORAGE_KEY);
    return stored !== null ? stored : FINANCE_ROOT;
  });

  const lastPathnameRef = useRef(pathname);
  const userClearedFinanceRef = useRef(false);
  const lastOpenMenuPathRef = useRef("");

  useEffect(() => {
    sessionStorage.setItem(FINANCE_PATH_STORAGE_KEY, activeFinancePath || "");
  }, [activeFinancePath]);

  // Result is intentionally unused here; the call keeps the store-init query warm for the sidebar.
  Digit.Hooks.useStore.getInitData();

  const user = Digit.UserService.getUser();
  const userUuid = user?.info?.uuid || user?.user?.[0]?.uuid;
  const userTenantId = user?.info?.tenantId || Digit.ULBService.getCurrentTenantId?.() || "pg";
  const stateId = Digit.ULBService.getStateId();

  const { isLoading: isMdmsLoading, data: mdmsData } = Digit.Hooks.useCustomMDMS(
    stateId,
    "common-masters",
    MICRO_UI_MDMS_CONFIG
  );

  const microUiModuleEnable = useMemo(
    () => mdmsData?.["common-masters"]?.["microUiModuleEnable"] || EMPTY_ARRAY,
    [mdmsData]
  );

  const isFinanceEnabled = useMemo(() => {
    const financeConfig = Array.isArray(microUiModuleEnable)
      ? microUiModuleEnable.find((elem) => ["finance", "egf"].includes(elem?.code?.toLowerCase()))
      : undefined;

    if (financeConfig) return financeConfig.enabled === true;
    return data?.actions?.some(isFinanceAction) ?? false;
  }, [microUiModuleEnable, data?.actions]);

  /* ------------------------------ Profile photo ----------------------------- */

  useEffect(() => {
    if (!userUuid || userUuid === GUEST_USER_UUID) return undefined;

    let mounted = true;
    const applyPhoto = (url) => {
      if (mounted && url) setProfilePhotoUrl(url);
    };

    const loadPhoto = async () => {
      const stateTenant = stateId || "pg";

      // 1. Photo already available on the logged-in user object
      const directPhoto = user?.info?.photo || user?.user?.[0]?.photo;
      if (directPhoto) {
        if (isInlineImage(directPhoto)) {
          applyPhoto(directPhoto);
          return;
        }
        try {
          const url = await fetchFileUrl(directPhoto, stateTenant);
          if (url) {
            applyPhoto(url);
            return;
          }
        } catch (e) {
          // fall through to userSearch
        }
      }

      // 2. Fetch through userSearch (same approach as TopBar / UserProfile)
      try {
        const tenant = Digit.ULBService.getCurrentTenantId?.() || userTenantId;
        const usersResponse = await Digit.UserService.userSearch(tenant, { uuid: [userUuid] }, {});
        const photo = usersResponse?.user?.[0]?.photo;
        if (!photo) return;

        if (isInlineImage(photo)) {
          applyPhoto(photo);
          return;
        }

        let url = await fetchFileUrl(photo, stateTenant);
        if (!url && tenant !== stateTenant) url = await fetchFileUrl(photo, tenant);
        applyPhoto(url);
      } catch (err) {
        console.warn("Could not load employee photo in sidebar:", err?.message || err);
      }
    };

    loadPhoto();
    return () => {
      mounted = false;
    };
  }, [userTenantId, userUuid, stateId, user?.info?.photo]);

  /* ------------------------------ Menu data --------------------------------- */

  const transformedActions = useMemo(() => {
    const sourceActions = data?.actions || EMPTY_ARRAY;
    if (!sourceActions.length) return EMPTY_ARRAY;

    return sourceActions.map((item) => {
      const matchedConfig = findMicroUiConfig(item, microUiModuleEnable);
      const isEnabled = matchedConfig ? matchedConfig.enabled : true;

      if (!isEnabled) {
        return item.navigationURL ? { ...item, navigationURL: toMonoUiUrl(item.navigationURL) } : item;
      }

      if (isFinanceEnabled && isFinanceMenuItem(item)) {
        return { ...item, path: buildFinanceMenuPath(item) };
      }

      return item;
    });
  }, [data?.actions, microUiModuleEnable, isFinanceEnabled]);

  const financeTree = useMemo(() => {
    const tree = {};
    if (!transformedActions.length || !isFinanceEnabled) return tree;

    transformedActions
      .filter((item) => item?.path?.startsWith(FINANCE_ROOT))
      .forEach((item) => {
        const parts = item.path.split(".");
        const leaf = parts.pop();
        const parent = parts.reduce((node, part) => {
          node[part] = node[part] || {};
          return node[part];
        }, tree);

        parent[leaf] = parent[leaf] ? { ...item, ...parent[leaf] } : { ...item };
      });

    return tree;
  }, [transformedActions, isFinanceEnabled]);

  /* ----------------------- Keep finance drawer in sync ---------------------- */

  useEffect(() => {
    // Wait for access-control + MDMS data; isFinanceEnabled is false while loading
    if (isLoading || isMdmsLoading) return;

    const isFinanceRoute = isFinanceEnabled && isFinancePathname(pathname);
    const getTargetPath = () => getFinancePathFromCurrentUrl(pathname, transformedActions, isFinanceEnabled);

    if (lastPathnameRef.current !== pathname) {
      lastPathnameRef.current = pathname;
      userClearedFinanceRef.current = false;

      if (!isFinanceRoute) {
        setActiveFinancePath("");
        return;
      }

      const targetPath = getTargetPath();
      setActiveFinancePath((prev) => {
        const currentNode = prev ? getNodeByPath(financeTree, prev) : null;
        if (currentNode && typeof currentNode === "object" && nodeContainsActivePage(currentNode, pathname)) {
          return prev;
        }
        return targetPath || prev || FINANCE_ROOT;
      });
      return;
    }

    if (isFinanceRoute && !activeFinancePath && !userClearedFinanceRef.current) {
      setActiveFinancePath(getTargetPath() || FINANCE_ROOT);
    }
    // activeFinancePath / financeTree are intentionally omitted: this effect must only react to navigation and data loading.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pathname, isFinanceEnabled, isLoading, isMdmsLoading, transformedActions]);

  const menuItems = useMemo(() => {
    const query = normalize(search);
    const grouped = {};

    transformedActions
      .filter((item) => item?.url === "url" || (isFinanceEnabled && item?.path?.startsWith(FINANCE_ROOT)))
      .forEach((item) => {
        let moduleName = item.path?.split(".")[0];
        if (!moduleName) return;
        if (moduleName === "TradeLicense") moduleName = "Trade License";

        const childName = item?.displayName || "";
        const matches =
          !query ||
          [moduleName, translateAction(t, moduleName), childName, translateAction(t, childName)].some((label) =>
            normalize(label).includes(query)
          );

        if (!matches) return;
        (grouped[moduleName] = grouped[moduleName] || []).push(item);
      });

    const result = [];

    Object.keys(grouped).forEach((key) => {
      const items = [...grouped[key]].sort(byOrderNumber);
      const [first] = items;
      const upperKey = key.toUpperCase();

      // Modules without sub-menus (Home and other single links)
      if (!first?.path?.includes(".")) {
        if (first?.displayName === "Home") {
          result.unshift({ moduleName: upperKey, icon: first, navigationURL: EMPLOYEE_HOME_URL, type: "single" });
        } else {
          result.push({
            moduleName: first?.displayName?.toUpperCase() || upperKey,
            type: "single",
            icon: first,
            navigationURL: first?.navigationURL,
          });
        }
        return;
      }

      if (upperKey === "FINANCE" && isFinanceEnabled) {
        result.push({
          moduleName: "FINANCE",
          links: grouped[key],
          icon: { leftIcon: FINANCE_ICON_KEY },
          orderNumber: 0,
          isFinanceModule: true,
        });
        return;
      }

      const iconItem = items.find((item) => item?.leftIcon) || first;
      result.push({
        moduleName: upperKey,
        links: items,
        icon: upperKey === "FINANCE" && !iconItem?.leftIcon ? { ...iconItem, leftIcon: FINANCE_ICON_KEY } : iconItem,
        orderNumber: first?.orderNumber,
      });
    });

    const home = result.find((r) => r.moduleName === "HOME");
    const rest = result
      .filter((r) => r.moduleName !== "HOME")
      .sort((a, b) => {
        const orderA = a?.orderNumber ?? 999;
        const orderB = b?.orderNumber ?? 999;
        return orderA !== orderB ? orderA - orderB : a.moduleName.localeCompare(b.moduleName);
      });

    const ordered = home ? [home, ...rest] : rest;

    if (!home && !query) {
      ordered.unshift({ moduleName: "HOME", icon: "HomeIcon", navigationURL: EMPLOYEE_HOME_URL, type: "single" });
    }

    return ordered;
  }, [transformedActions, search, isFinanceEnabled, t]);

  // Auto-open the accordion that contains the current route
  useEffect(() => {
    if (!menuItems?.length) return;
    if (lastOpenMenuPathRef.current === pathname) return;

    const modulesToOpen = menuItems
      .filter(
        (module) =>
          module?.links?.length > 0 &&
          !module?.isFinanceModule &&
          module.links.some((link) => isUrlMatching(pathname, link?.navigationURL || link?.link))
      )
      .map((module) => module.moduleName);

    lastOpenMenuPathRef.current = pathname;
    if (!modulesToOpen.length) return;

    setOpenMenus((prev) => {
      if (modulesToOpen.every((name) => prev[name])) return prev;
      return { ...prev, ...Object.fromEntries(modulesToOpen.map((name) => [name, true])) };
    });
  }, [pathname, menuItems]);

  const currentFinanceNode = useMemo(() => {
    if (!activeFinancePath || !financeTree) return null;
    return getNodeByPath(financeTree, activeFinancePath);
  }, [activeFinancePath, financeTree]);

  /* -------------------------------- Handlers -------------------------------- */

  const toggleMenu = (key) => setOpenMenus((prev) => ({ ...prev, [key]: !prev[key] }));
  const handleNavigate = () => closeMobileSidebar?.();
  const handleLogout = () => setShowDialog(true);
  const handleOnCancel = () => setShowDialog(false);

  const handleOnSubmit = () => {
    Digit.UserService.logout();
    setShowDialog(false);
  };

  const expandIfCollapsed = () => {
    if (isSidebarCollapsed && typeof toggleSidebarWidth === "function") toggleSidebarWidth();
  };

  const openFinanceMenu = () => {
    userClearedFinanceRef.current = false;
    expandIfCollapsed();
    setActiveFinancePath(FINANCE_ROOT);
  };

  const handleFinanceBack = () => {
    const parts = activeFinancePath.split(".");
    parts.pop();
    const newPath = parts.join(".");
    if (!newPath) userClearedFinanceRef.current = true;
    setActiveFinancePath(newPath);
  };

  /* -------------------------------- Rendering ------------------------------- */

  if (isLoading || isMdmsLoading) return <SidebarSkeleton isSidebarCollapsed={isSidebarCollapsed} />;

  const financeLabel = translateOrFallback(t, "ACTION_TEST_FINANCE", "Finance");

  const renderFinanceMenu = () => {
    if (!currentFinanceNode) return null;

    const isRoot = activeFinancePath === FINANCE_ROOT;
    const menuTitle = activeFinancePath.split(".").pop();
    const mainMenuLabel = translateOrFallback(t, "MAIN_MENU", "Main Menu");

    const financeKeys = getChildKeys(currentFinanceNode)
      .filter((key) => hasMatchingDescendant(currentFinanceNode[key], key, search, t, i18n))
      .sort((a, b) => getMinOrderNumber(currentFinanceNode[a]) - getMinOrderNumber(currentFinanceNode[b]));

    return (
      <>
        <div className="employee-accordion-item">
          <MenuHeader
            active
            title={isRoot ? mainMenuLabel : menuTitle}
            label={isRoot ? mainMenuLabel : translateAction(t, menuTitle)}
            icon={<ArrowForward className="arrow-icon" style={{ transform: "rotate(180deg)" }} />}
            collapsed={isSidebarCollapsed}
            onClick={handleFinanceBack}
          />
        </div>

        <div className="employee-submenu-wrapper" style={{ display: "block" }}>
          {financeKeys.map((key) => {
            const nodeValue = currentFinanceNode[key];
            if (!nodeValue || typeof nodeValue !== "object") return null;

            const hasChildren = hasSubMenuChildren(nodeValue);
            const label = getFinanceNodeLabel(nodeValue, key, t, i18n);
            const leftIcon = getModuleIcon(
              hasChildren
                ? { moduleName: key, leftIcon: nodeValue?.leftIcon || FINANCE_ICON_KEY }
                : { moduleName: nodeValue?.displayName || key, leftIcon: nodeValue?.leftIcon || "collections" }
            );

            if (hasChildren) {
              return (
                <div
                  className="sidebar-sublist"
                  key={key}
                  onClick={() => setActiveFinancePath(`${activeFinancePath}.${key}`)}
                >
                  <div className="submenu-item" title={label}>
                    {leftIcon}
                    <span className="submenu-label">{label}</span>
                    <div className="accordion-chevron" style={{ marginLeft: "auto" }}>
                      <ArrowForward className="arrow-icon" />
                    </div>
                  </div>
                </div>
              );
            }

            const targetPath = resolveNavUrl(nodeValue?.navigationURL);
            return (
              <SubMenuLink
                key={key}
                url={targetPath}
                active={isPathActive(pathname, targetPath)}
                title={label}
                label={label}
                leading={leftIcon}
                onNavigate={handleNavigate}
              />
            );
          })}
        </div>
      </>
    );
  };

  const renderMenuItem = (item, index) => {
    const key = `${item.moduleName}-${index}`;
    const icon = getModuleIcon(item);
    const displayName = translateAction(t, item?.moduleName);

    // Single link (Home, standalone modules)
    if (item.type === "single" || !item.links || item.links.length === 0) {
      const targetUrl = resolveNavUrl(item.navigationURL);
      return (
        <div className={`sidebar-list ${isPathActive(pathname, targetUrl) ? "active" : ""}`} key={key}>
          <SidebarLink url={targetUrl} className="menu-item" title={displayName} onClick={handleNavigate}>
            {icon}
            {!isSidebarCollapsed && <div className="menu-label">{displayName}</div>}
          </SidebarLink>
        </div>
      );
    }

    // Finance entry - opens the dedicated finance drawer
    if (item.isFinanceModule) {
      return (
        <div className="employee-accordion-item" key={key}>
          <MenuHeader
            title={displayName}
            label={displayName}
            icon={icon}
            active={isFinancePathname(pathname)}
            collapsed={isSidebarCollapsed}
            chevron={<ArrowForward className="arrow-icon" />}
            onClick={openFinanceMenu}
          />
        </div>
      );
    }

    // Regular accordion module
    const validLinks = (item.links || []).filter((link) => link.url !== "");
    const isOpen = search.trim() ? true : !!openMenus[item.moduleName];
    const isChildActive = validLinks.some((link) =>
      isPathActive(pathname, resolveNavUrl(link?.navigationURL || link?.link))
    );

    return (
      <div className="employee-accordion-item" key={key}>
        <MenuHeader
          title={displayName}
          label={displayName}
          icon={icon}
          active={isChildActive}
          collapsed={isSidebarCollapsed}
          chevron={isOpen ? <ArrowVectorDown className="arrow-icon" /> : <ArrowForward className="arrow-icon" />}
          onClick={() => !isSidebarCollapsed && toggleMenu(item.moduleName)}
        />

        {isOpen && !isSidebarCollapsed && (
          <div className="employee-submenu-wrapper">
            {validLinks.map((linkItem, idx) => {
              const rawUrl = linkItem?.navigationURL || linkItem?.link;
              const rawName = linkItem?.displayName || "";
              const label = translateAction(t, rawName);

              return (
                <SubMenuLink
                  key={`${rawName}-${idx}`}
                  url={resolveNavUrl(rawUrl)}
                  active={isUrlMatching(pathname, rawUrl)}
                  title={label}
                  label={label}
                  leading={<span className="sublink-dot">•</span>}
                  onNavigate={handleNavigate}
                />
              );
            })}
          </div>
        )}
      </div>
    );
  };

  const isLoggedIn = Boolean(user && user.access_token);

  return (
    <div className="employee-sidebar-inner">
      <div className="sidebar-logo-header">
        <div className="logo-design">
          <img
            src={isSidebarCollapsed ? "/upyog-ui/images/logo-mobile.png" : "/upyog-ui/images/logo-secondary.png"}
            alt="UPYOG Logo"
          />
        </div>
      </div>

      {isLoggedIn && (
        <Profile info={user?.info} profilePhotoUrl={profilePhotoUrl} isSidebarCollapsed={isSidebarCollapsed} />
      )}

      {!isSidebarCollapsed && (
        <div className="employee-sidebar-search-box">
          <SearchIcon className="search-icon" />
          <input
            type="text"
            placeholder={translateOrFallback(t, "ACTION_TEST_SEARCH", "Search")}
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
          {search && (
            <button className="search-clear-btn" onClick={() => setSearch("")} type="button">
              ×
            </button>
          )}
        </div>
      )}

      <div className="drawer-desktop">
        {activeFinancePath ? (
          <>
            <div className="employee-accordion-item">
              <MenuHeader
                active
                title={financeLabel}
                label={financeLabel}
                icon={<FinanceChartIcon className="sidebar-icon" />}
                collapsed={isSidebarCollapsed}
                onClick={expandIfCollapsed}
              />
            </div>
            {!isSidebarCollapsed && renderFinanceMenu()}
          </>
        ) : (
          <>
            {menuItems.map(renderMenuItem)}

            {isLoggedIn && (
              <div className={`sidebar-list ${pathname === PROFILE_URL ? "active" : ""}`}>
                <Link to={PROFILE_URL} className="menu-item" title={t("EDIT_PROFILE")} onClick={handleNavigate}>
                  <EditPencilIcon className="sidebar-icon" />
                  {!isSidebarCollapsed && <div className="menu-label">{t("EDIT_PROFILE")}</div>}
                </Link>
              </div>
            )}
          </>
        )}
      </div>

      <div className="version-badge">
        <span>{APP_VERSION_LABEL}</span>
      </div>

      <div className="citizen-logout">
        <button onClick={handleLogout}>
          <LogoutIcon className="sidebar-icon" />
          <span>{t("CORE_COMMON_LOGOUT")}</span>
        </button>
      </div>

      {showDialog && <LogoutDialog onSelect={handleOnSubmit} onCancel={handleOnCancel} onDismiss={handleOnCancel} />}
    </div>
  );
};

export default EmployeeSideBar;