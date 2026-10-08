import React, { useEffect, useMemo, useState } from "react";
import { Link, useLocation } from "react-router-dom";
import {
  HomeIcon,
  EditPencilIcon,
  LogoutIcon,
  Loader,
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

const defaultImage =
  "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAO4AAADUCAMAAACs0e/bAAAAM1BMVEXK0eL" +
  "/" +
  "/" +
  "/" +
  "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";

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

const normalize = (s = "") => s.toString().trim().toLowerCase();

const FINANCE_PATH_STORAGE_KEY = "upyog_employee_sidebar_finance_path";

const Profile = ({ info, profilePhotoUrl, isSidebarCollapsed }) => {
  const username = isSidebarCollapsed
    ? info?.name
      ?.split(" ")
      .map((word) => word[0])
      .join("")
      .toUpperCase()
    : info?.name;

  return (
    <div className="profile-section">
      <div className="imageloader imageloader-loaded">
        <img className="img-responsive img-circle img-Profile" src={profilePhotoUrl || defaultImage} alt="Profile" />
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

const ICON_MAP = {
  homeicon: <HomeIcon className="sidebar-icon" />,
  home: <HomeIcon className="sidebar-icon" />,
  obpsicon: <OBPSIcon className="sidebar-icon" />,
  bpahomeicon: <OBPSIcon className="sidebar-icon" />,
  propertyhouse: <PropertyHouse className="sidebar-icon" />,
  propertyicon: <PropertyHouse className="sidebar-icon" />,
  commonpticon: <PropertyHouse className="sidebar-icon" />,
  pticon: <PropertyHouse className="sidebar-icon" />,
  caseicon: <CaseIcon className="sidebar-icon" />,
  tlicon: <CaseIcon className="sidebar-icon" />,
  pgricon: <PGRIcon className="sidebar-icon" />,
  complainticon: <PGRIcon className="sidebar-icon" />,
  fsmicon: <FSMIcon className="sidebar-icon" />,
  wsicon: <WSICon className="sidebar-icon" />,
  mcollecticon: <MCollectIcon className="sidebar-icon" />,
  chbicon: <CHBIcon className="sidebar-icon" />,
  collectionicon: <CollectionIcon className="sidebar-icon" />,
  billsicon: <CollectionIcon className="sidebar-icon" />,
  birthicon: <BirthIcon className="sidebar-icon" />,
  deathicon: <DeathIcon className="sidebar-icon" />,
  firenocicon: <FirenocIcon className="sidebar-icon" />,
  phone: <Phone className="sidebar-icon" />,
  editpencilicon: <EditPencilIcon className="sidebar-icon" />,
  logouticon: <LogoutIcon className="sidebar-icon" />,
  personicon: <PersonIcon className="sidebar-icon" />,
  receipticon: <ReceiptIcon className="sidebar-icon" />,
  documenticonsolid: <DocumentIconSolid className="sidebar-icon" />,
  dropicon: <DropIcon className="sidebar-icon" />,
  financecharticon: <FinanceChartIcon className="sidebar-icon" />,
  insertchart: <FinanceChartIcon className="sidebar-icon" />,
  finance: <FinanceChartIcon className="sidebar-icon" />,
  collectionsbookmarkicons: <CollectionsBookmarIcons className="sidebar-icon" />,
};

const getNodeByPath = (obj, pathString) => {
  if (!pathString) return obj;
  const parts = pathString.split(".");
  let current = obj;
  for (const part of parts) {
    if (current && current[part]) current = current[part];
    else return null;
  }
  return current;
};

const getMinOrderNumber = (node) => {
  if (!node) return 9999;
  if (typeof node === "object" && node.id !== undefined) return node.orderNumber || 0;
  let min = 9999;
  for (const key in node) {
    const order = getMinOrderNumber(node[key]);
    if (order < min) min = order;
  }
  return min;
};

const hasMatchingDescendant = (node, key, search, t, i18n) => {
  if (!search) return true;

  const hasChildren = typeof node === "object" && node && !node.id;
  const translationKey = hasChildren
    ? `ACTION_TEST_${key.toUpperCase().replace(/[ -]/g, "_")}`
    : `ACTION_TEST_${node.displayName ? node.displayName.toUpperCase().replace(/[.:-\s\/]/g, "_") : key.toUpperCase().replace(/[ -]/g, "_")}`;

  const displayLabel = i18n.exists(translationKey)
    ? t(translationKey)
    : hasChildren
      ? key
      : node.displayName || key;

  if (displayLabel.toLowerCase().includes(search.toLowerCase())) return true;

  if (hasChildren) {
    for (const childKey in node) {
      if (FINANCE_META_KEYS.includes(childKey)) continue;
      if (hasMatchingDescendant(node[childKey], childKey, search, t, i18n)) return true;
    }
  }

  return false;
};

const getModuleIcon = (item) => {
  let rawIcon = item?.leftIcon || item?.icon?.leftIcon || item?.icon;

  if (!rawIcon && item?.links?.length > 0) {
    const linkWithIcon = item.links.find((l) => l?.leftIcon || l?.icon?.leftIcon || l?.icon);
    rawIcon = linkWithIcon?.leftIcon || linkWithIcon?.icon?.leftIcon || linkWithIcon?.icon;
  }

  if (typeof rawIcon === "string") {
    let cleanKey = rawIcon;
    if (cleanKey.includes(":")) cleanKey = cleanKey.split(":")[1];
    const cleanLookup = cleanKey.toLowerCase().replace(/[^a-z0-9]/g, "");
    if (ICON_MAP[cleanLookup]) return ICON_MAP[cleanLookup];
  }

  const nameToMatch = (item?.moduleName || item?.displayName || "").toLowerCase().replace(/[^a-z0-9]/g, "");

  if (nameToMatch.includes("home")) return <HomeIcon className="sidebar-icon" />;
  if (nameToMatch.includes("bpa") || nameToMatch.includes("obps") || nameToMatch.includes("building") || nameToMatch.includes("stakeholder"))
    return <OBPSIcon className="sidebar-icon" />;
  if (nameToMatch.includes("grievance") || nameToMatch.includes("pgr") || nameToMatch.includes("complaint"))
    return <PGRIcon className="sidebar-icon" />;
  if (nameToMatch.includes("property") || nameToMatch.includes("pt") || nameToMatch.includes("house") || nameToMatch.includes("assessment"))
    return <PropertyHouse className="sidebar-icon" />;
  if (nameToMatch.includes("fire")) return <FirenocIcon className="sidebar-icon" />;
  if (nameToMatch.includes("engagement") || nameToMatch.includes("notification") || nameToMatch.includes("phone"))
    return <Phone className="sidebar-icon" />;
  if (nameToMatch.includes("mcollect") || nameToMatch.includes("collect") || nameToMatch.includes("challan"))
    return <MCollectIcon className="sidebar-icon" />;
  if (nameToMatch.includes("trade") || nameToMatch.includes("tl") || nameToMatch.includes("license"))
    return <CaseIcon className="sidebar-icon" />;
  if (nameToMatch.includes("dashboard") || nameToMatch.includes("finance") || nameToMatch.includes("chart") || nameToMatch.includes("dss"))
    return <FinanceChartIcon className="sidebar-icon" />;
  if (nameToMatch.includes("fsm") || nameToMatch.includes("sludge") || nameToMatch.includes("desilting"))
    return <FSMIcon className="sidebar-icon" />;
  if (nameToMatch.includes("water") || nameToMatch.includes("sewerage") || nameToMatch.includes("ws"))
    return <WSICon className="sidebar-icon" />;
  if (nameToMatch.includes("chb") || nameToMatch.includes("community") || nameToMatch.includes("hall"))
    return <CHBIcon className="sidebar-icon" />;
  if (nameToMatch.includes("birth")) return <BirthIcon className="sidebar-icon" />;
  if (nameToMatch.includes("death")) return <DeathIcon className="sidebar-icon" />;
  if (nameToMatch.includes("hrms") || nameToMatch.includes("employee") || nameToMatch.includes("person") || nameToMatch.includes("user"))
    return <PersonIcon className="sidebar-icon" />;
  if (nameToMatch.includes("bill") || nameToMatch.includes("receipt") || nameToMatch.includes("collection"))
    return <CollectionIcon className="sidebar-icon" />;

  return <CollectionIcon className="sidebar-icon" />;
};

const isExternalOrMono = (url) => {
  if (!url) return false;
  return url.startsWith("http://") || url.startsWith("https://") || url.startsWith("/employee/");
};

const resolveNavUrl = (navUrl) => {
  if (!navUrl) return "#";
  let formatted = navUrl.replace("/digit-ui/", "/upyog-ui/");

  if (formatted.startsWith("http://") || formatted.startsWith("https://")) return formatted;

  if (formatted.includes("mdms/") || formatted.includes("/mdms/")) {
    let clean = formatted.replace(/^\/?(upyog-ui\/employee\/|upyog-ui\/|employee\/)/, "");
    if (clean.startsWith("/")) clean = clean.substring(1);
    return "/employee/" + clean;
  }

  if (formatted.includes("integration/dss") || formatted.includes("/dss/")) {
    let subPath = "home";
    if (formatted.includes("integration/dss/")) subPath = formatted.split("integration/dss/")[1] || "home";
    else if (formatted.includes("/dss/")) subPath = formatted.split("/dss/")[1] || "home";

    const cleanSub = subPath.replace(/^\/+|\/+$/g, "");
    if (cleanSub === "home" || cleanSub === "NURT_DASHBOARD") return `/upyog-ui/employee/dss/landing/${cleanSub}`;
    if (cleanSub.startsWith("landing/") || cleanSub.startsWith("dashboard/")) return `/upyog-ui/employee/dss/${cleanSub}`;
    return `/upyog-ui/employee/dss/dashboard/${cleanSub}`;
  }

  if (formatted.includes("services/EGF") || formatted.includes("services/egf") || formatted.includes("/finance/")) {
    let clean = formatted;
    if (!clean.startsWith("/upyog-ui/employee/")) {
      clean = clean.replace(/^\/?(upyog-ui\/employee\/|upyog-ui\/|employee\/)/, "");
      if (clean.startsWith("/")) clean = clean.substring(1);
      clean = "/upyog-ui/employee/" + clean;
    }
    return clean;
  }

  if (formatted.startsWith("/employee/")) {
    formatted = "/upyog-ui" + formatted;
  } else if (!formatted.startsWith("/upyog-ui/")) {
    formatted = formatted.startsWith("/") ? "/upyog-ui/employee" + formatted : "/upyog-ui/employee/" + formatted;
  }

  return formatted;
};

const isUrlMatching = (pathname, navUrl) => {
  if (!pathname || !navUrl) return false;
  const resolved = resolveNavUrl(navUrl);
  if (!resolved || resolved === "#") return false;

  const cleanPath = pathname.split("?")[0].replace(/\/+$/, "").toLowerCase();
  const cleanResolved = resolved.split("?")[0].replace(/\/+$/, "").toLowerCase();

  if (cleanPath === cleanResolved) return true;

  if (cleanPath.length > 15 && cleanResolved.length > 15 && cleanPath.startsWith(cleanResolved + "/")) {
    return true;
  }

  const stripPrefix = (url) => url.replace(/^\/?(upyog-ui\/employee\/|upyog-ui\/|employee\/)/, "").replace(/^\/+/, "");
  const relativePath = stripPrefix(cleanPath);
  const relativeResolved = stripPrefix(cleanResolved);

  if (relativePath === relativeResolved) return true;
  if (relativePath.length > 5 && relativeResolved.length > 5) {
    if (relativePath.startsWith(relativeResolved + "/")) {
      return true;
    }
  }

  return false;
};

const getFinancePathFromCurrentUrl = (pathname, actions, isFinanceEnabled) => {
  if (!pathname || !actions?.length || !isFinanceEnabled || (!pathname.includes("/finance") && !pathname.includes("/services/EGF") && !pathname.includes("/services/finance"))) return "";

  const matchedAction = actions.find((action) => {
    if (!action?.navigationURL) return false;
    return isUrlMatching(pathname, action.navigationURL);
  });

  if (!matchedAction?.path) return "";

  let itemPath = matchedAction.path;

  if (itemPath.startsWith("EGF")) {
    itemPath = itemPath.replace("EGF", "Finance");
  } else if (!itemPath.startsWith("Finance")) {
    itemPath = `Finance.${itemPath}`;
  }

  const parts = itemPath.split(".");
  if (parts.length > 1) {
    parts.pop();
    return parts.join(".");
  }

  return itemPath || "Finance";
};

const EmployeeSideBar = ({ isSidebarCollapsed = false, closeMobileSidebar }) => {
  const { isLoading, data } = Digit.Hooks.useAccessControl();
  const { t, i18n } = useTranslation();
  const { pathname } = useLocation();

  const [search, setSearch] = useState("");
  const [openMenus, setOpenMenus] = useState({});
  const [showDialog, setShowDialog] = useState(false);
  const [profilePhotoUrl, setProfilePhotoUrl] = useState(null);
  const [activeFinancePath, setActiveFinancePath] = useState(() => {
    const isFinRoute = pathname.includes("/finance") || pathname.includes("/services/EGF") || pathname.includes("/services/finance");
    if (!isFinRoute) return "";
    const stored = sessionStorage.getItem(FINANCE_PATH_STORAGE_KEY);
    return stored !== null ? stored : "Finance";
  });

  useEffect(() => {
    sessionStorage.setItem(FINANCE_PATH_STORAGE_KEY, activeFinancePath || "");
  }, [activeFinancePath]);

  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { stateInfo } = storeData || {};

  const user =
    Digit.UserService.getUser()?.token !== null
      ? Digit.UserService.getUser()
      : {
        access_token: "asdf",
        info: {
          tenantId: "pg",
          uuid: "asdf",
          name: "Employee User",
          mobileNumber: 1231231231,
          emailId: "employee@upyog.niua.org",
        },
      };

  const tenantId = Digit.ULBService.getCurrentTenantId?.() || user?.info?.tenantId || "pg";
  const stateId = Digit.ULBService.getStateId();

  const { isLoading: isMdmsLoading, data: mdmsData } = Digit.Hooks.useCustomMDMS(
    stateId,
    "common-masters",
    [{ name: "microUiModuleEnable" }]
  );

  const microUiModuleEnable = mdmsData?.["common-masters"]?.["microUiModuleEnable"] || [];
  const isFinanceEnabled =
    microUiModuleEnable.find((elem) => elem?.code?.toLowerCase() === "finance")?.enabled === true;

  useEffect(() => {
    let mounted = true;

    const fetchPhoto = async () => {
      try {
        const uuid = user?.info?.uuid || user?.user?.[0]?.uuid;
        if (!uuid) return;

        const usersResponse = await Digit.UserService.userSearch(
          user?.info?.tenantId || tenantId,
          { uuid: [uuid] },
          {}
        );

        const photo = usersResponse?.user?.[0]?.photo;
        if (!photo) return;

        const file = await Digit.UploadServices.Filefetch([photo], tenantId);
        const url = file?.data?.fileStoreIds?.[0]?.url?.split(",")?.[0];

        if (mounted && url) setProfilePhotoUrl(url);
      } catch (err) {
        console.error("Error fetching profile photo:", err);
      }
    };

    fetchPhoto();
    return () => {
      mounted = false;
    };
  }, [tenantId, user]);

  const transformedActions = useMemo(() => {
    const sourceActions = data?.actions || [];

    return sourceActions.map((item) => {
      const pathRoot = item.path?.split?.(".")[0]?.toLowerCase() || "";
      const parentModule = item.parentModule?.toLowerCase() || "";
      const navUrl = item.navigationURL?.toLowerCase() || "";

      const matchedConfig = microUiModuleEnable.find((config) => {
        const codeLower = config.code?.toLowerCase();
        const pathMatches = pathRoot === codeLower;
        const parentMatches = parentModule.includes(codeLower);
        const navMatches = navUrl.includes(codeLower);

        let financeMatches = false;
        if (codeLower === "finance") {
          financeMatches =
            pathRoot === "egf" ||
            parentModule.includes("egf") ||
            navUrl.includes("services/egf") ||
            navUrl.includes("services/finance");
        }

        return pathMatches || parentMatches || navMatches || financeMatches;
      });

      const isEnabled = matchedConfig ? matchedConfig.enabled : true;

      if (!isEnabled) {
        if (item.navigationURL) {
          let cleanUrl = item.navigationURL;
          if (!cleanUrl.startsWith("/employee/") && !cleanUrl.startsWith("/digit-ui/")) {
            cleanUrl = cleanUrl.replace("/upyog-ui/employee/", "");
            cleanUrl = cleanUrl.replace("/digit-ui/employee/", "");
            cleanUrl = cleanUrl.replace("/upyog-ui/", "");
            cleanUrl = cleanUrl.replace("/digit-ui/", "");
            cleanUrl = cleanUrl.startsWith("/") ? `/employee${cleanUrl}` : `/employee/${cleanUrl}`;
          }
          return { ...item, navigationURL: cleanUrl };
        }
        return item;
      }

      const isFinanceItem =
        isFinanceEnabled &&
        item.path &&
        (item.path.startsWith("Finance") ||
          item.path.startsWith("EGF") ||
          item.parentModule?.toLowerCase()?.includes("egf") ||
          item.parentModule?.toLowerCase()?.includes("finance") ||
          item.navigationURL?.toLowerCase()?.includes("services/egf") ||
          item.navigationURL?.toLowerCase()?.includes("services/finance"));

      if (isFinanceItem) {
        let newPath = item.path;
        if (newPath.startsWith("EGF")) newPath = newPath.replace("EGF", "Finance");
        else if (!newPath.startsWith("Finance")) newPath = `Finance.${newPath}`;

        if (item.displayName && newPath.includes(".")) {
          const parts = newPath.split(".");
          parts[parts.length - 1] = item.displayName;
          newPath = parts.join(".");
        }

        return { ...item, path: newPath };
      }

      return item;
    });
  }, [data, microUiModuleEnable, isFinanceEnabled]);

  const financeTree = useMemo(() => {
    const tree = {};

    transformedActions
      .filter((item) => item?.url === "url" || (isFinanceEnabled && item?.path?.startsWith("Finance")))
      .forEach((item) => {
        if (!(isFinanceEnabled && item?.path?.startsWith("Finance"))) return;

        const parts = item.path.split(".");
        let current = tree;

        for (let i = 0; i < parts.length; i++) {
          const part = parts[i];
          if (i === parts.length - 1) {
            current[part] = current[part] ? { ...item, ...current[part] } : { ...item };
          } else {
            if (!current[part]) current[part] = {};
            current = current[part];
          }
        }
      });

    return tree;
  }, [transformedActions, isFinanceEnabled]);

  const financeHeaderPathFromUrl = useMemo(() => {
    return getFinancePathFromCurrentUrl(pathname, transformedActions, isFinanceEnabled);
  }, [pathname, transformedActions, isFinanceEnabled]);

  useEffect(() => {
    // Wait for access-control + MDMS data; isFinanceEnabled is false while loading
    if (isLoading || isMdmsLoading) return;

    const isFinanceRoute =
      isFinanceEnabled &&
      (pathname.includes("/finance") || pathname.includes("/services/EGF") || pathname.includes("/services/finance"));

    if (!isFinanceRoute) {
      setActiveFinancePath("");
      return;
    }

    setActiveFinancePath((prev) => {
      // User explicitly went back to the main menu while on a finance page
      if (prev === "") return financeHeaderPathFromUrl || "";

      // Keep the current level if the open page is a direct child of it
      const currentNode = getNodeByPath(financeTree, prev);
      if (currentNode && typeof currentNode === "object") {
        const containsActivePage = Object.keys(currentNode)
          .filter((k) => !FINANCE_META_KEYS.includes(k))
          .some((k) => {
            const child = currentNode[k];
            return child?.id !== undefined && child?.navigationURL && isUrlMatching(pathname, child.navigationURL);
          });
        if (containsActivePage) return prev;
      }

      // Otherwise derive the level from the URL (e.g. deep link / refresh), else keep where the user was
      return financeHeaderPathFromUrl || prev || "Finance";
    });
  }, [pathname, financeHeaderPathFromUrl, isFinanceEnabled, financeTree, isLoading, isMdmsLoading]);

  const menuItems = useMemo(() => {
    const grouped = {};
    const query = normalize(search);
    const filterFn = (item) => item?.url === "url" || (isFinanceEnabled && item?.path?.startsWith("Finance"));

    transformedActions.filter(filterFn).forEach((item) => {
      let index = item.path?.split(".")[0];
      if (!index) return;
      if (index === "TradeLicense") index = "Trade License";

      const moduleKey = index.toUpperCase().replace(/[ -]/g, "_");
      const moduleTranslation = t(`ACTION_TEST_${moduleKey}`);
      const moduleLabel =
        moduleTranslation && moduleTranslation !== `ACTION_TEST_${moduleKey}` ? moduleTranslation : index;

      const childName = item?.displayName || "";
      const childKey = childName.toUpperCase().replace(/[ -]/g, "_");
      const childTranslation = t(`ACTION_TEST_${childKey}`);
      const childLabel =
        childTranslation && childTranslation !== `ACTION_TEST_${childKey}` ? childTranslation : childName;

      const matches =
        !query ||
        normalize(index).includes(query) ||
        normalize(moduleLabel).includes(query) ||
        normalize(childName).includes(query) ||
        normalize(childLabel).includes(query);

      if (!matches) return;

      if (!grouped[index]) grouped[index] = [];
      grouped[index].push(item);
    });

    let result = [];

    Object.keys(grouped).forEach((key) => {
      const items = [...grouped[key]].sort((a, b) => (a?.orderNumber ?? 100) - (b?.orderNumber ?? 100));

      if (!items[0]?.path?.includes(".")) {
        if (items[0]?.displayName === "Home") {
          result.unshift({
            moduleName: key.toUpperCase(),
            icon: items[0],
            navigationURL: "/upyog-ui/employee",
            type: "single",
          });
        } else {
          result.push({
            moduleName: items[0]?.displayName?.toUpperCase() || key.toUpperCase(),
            type: "single",
            icon: items[0],
            navigationURL: items[0]?.navigationURL,
          });
        }
      } else {
        if (key.toUpperCase() === "FINANCE" && isFinanceEnabled) {
          result.push({
            moduleName: "FINANCE",
            links: grouped[key],
            icon: { leftIcon: "finance:insert-chart" },
            orderNumber: 0,
            isFinanceModule: true,
          });
        } else {
          const iconItem = items.find((item) => item?.leftIcon) || items[0];
          result.push({
            moduleName: key.toUpperCase(),
            links: items,
            icon:
              key.toUpperCase() === "FINANCE" && !iconItem?.leftIcon
                ? { ...iconItem, leftIcon: "finance:insert-chart" }
                : iconItem,
            orderNumber: items[0]?.orderNumber,
          });
        }
      }
    });

    const home = result.find((r) => r.moduleName === "HOME");
    const rest = result.filter((r) => r.moduleName !== "HOME").sort((a, b) => {
      const orderA = a?.orderNumber ?? 999;
      const orderB = b?.orderNumber ?? 999;
      if (orderA !== orderB) return orderA - orderB;
      return a.moduleName.localeCompare(b.moduleName);
    });

    result = home ? [home, ...rest] : rest;

    if (!home && !query) {
      result.unshift({
        moduleName: "HOME",
        icon: "HomeIcon",
        navigationURL: "/upyog-ui/employee",
        type: "single",
      });
    }

    return result;
  }, [transformedActions, search, isFinanceEnabled, t]);

  useEffect(() => {
    if (!menuItems?.length) return;

    const nextOpenMenus = {};

    menuItems.forEach((module) => {
      if (module?.links?.length > 0 && !module?.isFinanceModule) {
        const hasActiveChild = module.links.some((linkItem) => {
          return isUrlMatching(pathname, linkItem?.navigationURL || linkItem?.link);
        });

        if (hasActiveChild) {
          nextOpenMenus[module.moduleName] = true;
        }
      }
    });

    setOpenMenus((prev) => ({
      ...prev,
      ...nextOpenMenus,
    }));
  }, [pathname, menuItems]);

  const currentFinanceNode = useMemo(() => {
    if (!activeFinancePath || !financeTree) return null;
    return getNodeByPath(financeTree, activeFinancePath);
  }, [activeFinancePath, financeTree]);

  const toggleMenu = (key) => {
    setOpenMenus((prev) => ({ ...prev, [key]: !prev[key] }));
  };

  const handleLogout = () => setShowDialog(true);

  const handleOnSubmit = () => {
    Digit.UserService.logout();
    setShowDialog(false);
  };

  const handleOnCancel = () => setShowDialog(false);

  const renderFinanceInnerMenu = () => {
    if (!currentFinanceNode) return null;

    const menuTitle = activeFinancePath.split(".").pop();

    const handleFinanceBack = () => {
      const parts = activeFinancePath.split(".");
      parts.pop();
      setActiveFinancePath(parts.join("."));
    };

    const financeKeys = Object.keys(currentFinanceNode)
      .filter((key) => !FINANCE_META_KEYS.includes(key))
      .filter((key) => hasMatchingDescendant(currentFinanceNode[key], key, search, t, i18n))
      .sort((a, b) => getMinOrderNumber(currentFinanceNode[a]) - getMinOrderNumber(currentFinanceNode[b]));

    return (
      <>
        <div className="employee-accordion-item">
          <div className="sidebar-list sidebar-pointer-item active" onClick={handleFinanceBack}>
            <div className="menu-item" title={activeFinancePath === "Finance" ? (t("MAIN_MENU") !== "MAIN_MENU" ? t("MAIN_MENU") : "Main Menu") : menuTitle}>
              <ArrowForward className="arrow-icon" style={{ transform: "rotate(180deg)" }} />
              {!isSidebarCollapsed && (
                <div className="menu-label">
                  {activeFinancePath === "Finance"
                    ? (t("MAIN_MENU") !== "MAIN_MENU" ? t("MAIN_MENU") : "Main Menu")
                    : (t(`ACTION_TEST_${menuTitle.toUpperCase().replace(/[ -]/g, "_")}`) !==
                      `ACTION_TEST_${menuTitle.toUpperCase().replace(/[ -]/g, "_")}`
                      ? t(`ACTION_TEST_${menuTitle.toUpperCase().replace(/[ -]/g, "_")}`)
                      : menuTitle)}
                </div>
              )}
            </div>
          </div>
        </div>

        <div className="employee-submenu-wrapper" style={{ display: "block" }}>
          {financeKeys.map((key) => {
            const nodeValue = currentFinanceNode[key];
            const hasChildren = typeof nodeValue === "object" && nodeValue && !nodeValue.id;

            const translationKey = hasChildren
              ? `ACTION_TEST_${key.toUpperCase().replace(/[ -]/g, "_")}`
              : `ACTION_TEST_${nodeValue.displayName ? nodeValue.displayName.toUpperCase().replace(/[.:-\s\/]/g, "_") : key.toUpperCase().replace(/[ -]/g, "_")}`;

            const displayLabel = i18n.exists(translationKey)
              ? t(translationKey)
              : hasChildren
                ? key
                : nodeValue.displayName || key;

            const tempIconItem = hasChildren
              ? { moduleName: key, leftIcon: nodeValue?.leftIcon || "finance:insert-chart" }
              : { moduleName: nodeValue?.displayName || key, leftIcon: nodeValue?.leftIcon || "collections" };

            const leftIcon = getModuleIcon(tempIconItem);

            if (hasChildren) {
              return (
                <div
                  className="sidebar-sublist"
                  key={key}
                  onClick={() => setActiveFinancePath(`${activeFinancePath}.${key}`)}
                >
                  <div className="submenu-item" title={displayLabel}>
                    {leftIcon}
                    <span className="submenu-label">{displayLabel}</span>
                    <div className="accordion-chevron" style={{ marginLeft: "auto" }}>
                      <ArrowForward className="arrow-icon" />
                    </div>
                  </div>
                </div>
              );
            }

            const targetPath = resolveNavUrl(nodeValue.navigationURL);
            const isCurrentActive =
              pathname === targetPath ||
              (targetPath !== "/upyog-ui/employee" && targetPath && pathname.startsWith(targetPath));

            return (
              <div className={`sidebar-sublist ${isCurrentActive ? "active" : ""}`} key={key}>
                {isExternalOrMono(targetPath) ? (
                  <a
                    href={targetPath}
                    className="submenu-item"
                    title={displayLabel}
                    onClick={() => closeMobileSidebar?.()}
                  >
                    {leftIcon}
                    <span className="submenu-label">{displayLabel}</span>
                  </a>
                ) : (
                  <Link
                    to={targetPath}
                    className="submenu-item"
                    title={displayLabel}
                    onClick={() => closeMobileSidebar?.()}
                  >
                    {leftIcon}
                    <span className="submenu-label">{displayLabel}</span>
                  </Link>
                )}
              </div>
            );
          })}
        </div>
      </>
    );
  };

  if (isLoading || isMdmsLoading) return <Loader />;

  return (
    <>
      <div className="employee-sidebar-inner">
        <div className="sidebar-logo-header">
          <div className="logo-design">
            {isSidebarCollapsed ? (
              <img src="/upyog-ui/images/logo-mobile.png" alt="UPYOG Logo" />
            ) : (
              <img src="/upyog-ui/images/Logo.png" alt="UPYOG Logo" />
            )}
          </div>
        </div>

        {user && user.access_token && (
          <Profile
            info={user?.info}
            profilePhotoUrl={profilePhotoUrl}
            isSidebarCollapsed={isSidebarCollapsed}
          />
        )}

        {!isSidebarCollapsed && (
          <div className="employee-sidebar-search-box">
            <SearchIcon className="search-icon" />
            <input
              type="text"
              placeholder={t("ACTION_TEST_SEARCH") !== "ACTION_TEST_SEARCH" ? t("ACTION_TEST_SEARCH") : "Search"}
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
                <div className="sidebar-list active">
                  <div className="menu-item">
                    <FinanceChartIcon className="sidebar-icon" />
                    {!isSidebarCollapsed && (
                      <div className="menu-label">
                        {t("ACTION_TEST_FINANCE") !== "ACTION_TEST_FINANCE" ? t("ACTION_TEST_FINANCE") : "Finance"}
                      </div>
                    )}
                  </div>
                </div>
              </div>
              {!isSidebarCollapsed && renderFinanceInnerMenu()}
            </>
          ) : (
            <>
              {menuItems.map((item, index) => {
                const icon = getModuleIcon(item);
                const moduleKey = item?.moduleName?.replace(/[ -]/g, "_");
                const translatedModuleName = t(`ACTION_TEST_${moduleKey}`);
                const displayName =
                  translatedModuleName && translatedModuleName !== `ACTION_TEST_${moduleKey}`
                    ? translatedModuleName
                    : item?.moduleName;

                if (item.type === "single" || !item.links || item.links.length === 0) {
                  const targetUrl = resolveNavUrl(item.navigationURL);
                  const isSingleActive =
                    pathname === targetUrl ||
                    (targetUrl !== "/upyog-ui/employee" && targetUrl && pathname.startsWith(targetUrl));

                  return (
                    <div className={`sidebar-list ${isSingleActive ? "active" : ""}`} key={`${item.moduleName}-${index}`}>
                      {isExternalOrMono(targetUrl) ? (
                        <a
                          href={targetUrl}
                          className="menu-item"
                          title={displayName}
                          onClick={() => closeMobileSidebar?.()}
                        >
                          {icon}
                          {!isSidebarCollapsed && <div className="menu-label">{displayName}</div>}
                        </a>
                      ) : (
                        <Link
                          to={targetUrl}
                          className="menu-item"
                          title={displayName}
                          onClick={() => closeMobileSidebar?.()}
                        >
                          {icon}
                          {!isSidebarCollapsed && <div className="menu-label">{displayName}</div>}
                        </Link>
                      )}
                    </div>
                  );
                }

                if (item?.isFinanceModule) {
                  return (
                    <div className="employee-accordion-item" key={`${item.moduleName}-${index}`}>
                      <div
                        className={`sidebar-list sidebar-pointer-item ${pathname.includes("/finance") ? "active" : ""}`}
                        onClick={() => {
                          if (!isSidebarCollapsed) setActiveFinancePath("Finance");
                        }}
                      >
                        <div className="menu-item" title={displayName}>
                          {icon}
                          {!isSidebarCollapsed && <div className="menu-label">{displayName}</div>}
                          {!isSidebarCollapsed && (
                            <div className="accordion-chevron">
                              <ArrowForward className="arrow-icon" />
                            </div>
                          )}
                        </div>
                      </div>
                    </div>
                  );
                }

                const isOpen = search.trim() ? true : !!openMenus[item.moduleName];
                const validLinks = (item.links || []).filter((l) => l.url === "url" || l.url !== "");

                const isChildActive = validLinks.some((l) => {
                  const cUrl = resolveNavUrl(l?.navigationURL || l?.link);
                  return pathname === cUrl || (cUrl !== "/upyog-ui/employee" && cUrl && pathname.startsWith(cUrl));
                });

                return (
                  <div className="employee-accordion-item" key={`${item.moduleName}-${index}`}>
                    <div
                      className={`sidebar-list sidebar-pointer-item ${isChildActive ? "active" : ""}`}
                      onClick={() => !isSidebarCollapsed && toggleMenu(item.moduleName)}
                    >
                      <div className="menu-item" title={displayName}>
                        {icon}
                        {!isSidebarCollapsed && <div className="menu-label">{displayName}</div>}
                        {!isSidebarCollapsed && (
                          <div className="accordion-chevron">
                            {isOpen ? (
                              <ArrowVectorDown className="arrow-icon" />
                            ) : (
                              <ArrowForward className="arrow-icon" />
                            )}
                          </div>
                        )}
                      </div>
                    </div>

                    {isOpen && !isSidebarCollapsed && (
                      <div className="employee-submenu-wrapper">
                        {validLinks.map((linkItem, idx) => {
                          const childUrl = resolveNavUrl(linkItem?.navigationURL || linkItem?.link);
                          const isCurrentActive = isUrlMatching(pathname, linkItem?.navigationURL || linkItem?.link);

                          const childRawName = linkItem?.displayName || "";
                          const childKey = childRawName.toUpperCase().replace(/[ -]/g, "_");
                          const childTranslation = t(`ACTION_TEST_${childKey}`);
                          const childDisplayName =
                            childTranslation && childTranslation !== `ACTION_TEST_${childKey}`
                              ? childTranslation
                              : childRawName;

                          return (
                            <div className={`sidebar-sublist ${isCurrentActive ? "active" : ""}`} key={`${childRawName}-${idx}`}>
                              {isExternalOrMono(childUrl) ? (
                                <a
                                  href={childUrl}
                                  className="submenu-item"
                                  title={childDisplayName}
                                  onClick={() => closeMobileSidebar?.()}
                                >
                                  <span className="sublink-dot">•</span>
                                  <span className="submenu-label">{childDisplayName}</span>
                                </a>
                              ) : (
                                <Link
                                  to={childUrl}
                                  className="submenu-item"
                                  title={childDisplayName}
                                  onClick={() => closeMobileSidebar?.()}
                                >
                                  <span className="sublink-dot">•</span>
                                  <span className="submenu-label">{childDisplayName}</span>
                                </Link>
                              )}
                            </div>
                          );
                        })}
                      </div>
                    )}
                  </div>
                );
              })}

              {user && user.access_token && (
                <div className={`sidebar-list ${pathname === "/upyog-ui/employee/user/profile" ? "active" : ""}`}>
                  <Link
                    to="/upyog-ui/employee/user/profile"
                    className="menu-item"
                    title={t("EDIT_PROFILE")}
                    onClick={() => closeMobileSidebar?.()}
                  >
                    <EditPencilIcon className="sidebar-icon" />
                    {!isSidebarCollapsed && <div className="menu-label">{t("EDIT_PROFILE")}</div>}
                  </Link>
                </div>
              )}
            </>
          )}
        </div>

        <div className="citizen-logout">
          <button onClick={handleLogout}>
            <LogoutIcon className="sidebar-icon" />
            <span>{t("CORE_COMMON_LOGOUT")}</span>
          </button>
        </div>

        {showDialog && (
          <LogoutDialog
            onSelect={handleOnSubmit}
            onCancel={handleOnCancel}
            onDismiss={handleOnCancel}
          />
        )}
      </div>
    </>
  );
};

export default EmployeeSideBar;