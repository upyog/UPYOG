import React, { useRef, useEffect, useState } from "react";
import { Link, useLocation } from "react-router-dom";
import {
  HomeIcon,
  EditPencilIcon,
  LogoutIcon,
  Loader,
  AddressBookIcon,
  PropertyHouse,
  CaseIcon,
  CollectionIcon,
  PTIcon,
  OBPSIcon,
  PGRIcon,
  FSMIcon,
  WSICon,
  MCollectIcon,
  Phone,
  BirthIcon,
  DeathIcon,
  FirenocIcon,
  LoginIcon,
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

const Profile = ({ info, stateName, t, profilePhotoUrl, isSidebarCollapsed }) => {
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
        <img className="img-responsive img-circle img-Profile" src={profilePhotoUrl ? profilePhotoUrl : defaultImage} alt="Profile" />
      </div>
      <div id="profile-name" className="label-container name-Profile">
        <div className="label-text"> {username} </div>
      </div>
      {!isSidebarCollapsed && (
        <div id="profile-location" className="label-container loc-Profile">
          <div className="label-text"> {info?.mobileNumber} </div>
        </div>
      )}
      {!isSidebarCollapsed && info?.emailId && (
        <div id="profile-emailid" className="label-container loc-Profile">
          <div className="label-text"> {info.emailId} </div>
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
  collectionsbookmarkicons: <CollectionsBookmarIcons className="sidebar-icon" />,
};

const getModuleIcon = (item) => {
  let rawIcon = item?.leftIcon || item?.icon?.leftIcon || item?.icon;
  if (!rawIcon && item?.links?.length > 0) {
    const linkWithIcon = item.links.find((l) => l?.leftIcon || l?.icon?.leftIcon || l?.icon);
    rawIcon = linkWithIcon?.leftIcon || linkWithIcon?.icon?.leftIcon || linkWithIcon?.icon;
  }

  if (typeof rawIcon === "string") {
    let cleanKey = rawIcon;
    if (cleanKey.includes(":")) {
      cleanKey = cleanKey.split(":")[1];
    }
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
  if (nameToMatch.includes("fire"))
    return <FirenocIcon className="sidebar-icon" />;
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
  if (nameToMatch.includes("birth"))
    return <BirthIcon className="sidebar-icon" />;
  if (nameToMatch.includes("death"))
    return <DeathIcon className="sidebar-icon" />;
  if (nameToMatch.includes("hrms") || nameToMatch.includes("employee") || nameToMatch.includes("person") || nameToMatch.includes("user"))
    return <PersonIcon className="sidebar-icon" />;
  if (nameToMatch.includes("bill") || nameToMatch.includes("receipt") || nameToMatch.includes("collection"))
    return <CollectionIcon className="sidebar-icon" />;

  return <CollectionIcon className="sidebar-icon" />;
};

const resolveNavUrl = (navUrl) => {
  if (!navUrl) return "#";
  let formatted = navUrl.replace("/digit-ui/", "/upyog-ui/");
  if (!formatted.startsWith("/upyog-ui") && !formatted.startsWith("http") && !formatted.startsWith("/")) {
    formatted = "/upyog-ui/employee/" + formatted;
  }
  if (formatted.startsWith("/employee/")) {
    formatted = "/upyog-ui" + formatted;
  }
  return formatted;
};

const EmployeeSideBar = ({ isSidebarCollapsed = false, closeMobileSidebar }) => {
  const { isLoading, data } = Digit.Hooks.useAccessControl();
  const [search, setSearch] = useState("");
  const { t } = useTranslation();
  const location = useLocation();
  const { pathname } = location;
  const isMobile = window.Digit.Utils.browser.isMobile();

  const [openMenus, setOpenMenus] = useState({});
  const [showDialog, setShowDialog] = useState(false);
  const [profilePhotoUrl, setProfilePhotoUrl] = useState(null);

  const { data: storeData, isFetched } = Digit.Hooks.useStore.getInitData();
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

  const tenantId = Digit.ULBService.getCurrentTenantId?.() || "pg";

  useEffect(() => {
    const fetchPhoto = async () => {
      const usersResponse = await Digit.UserService.userSearch(
        user?.info?.tenantId || tenantId,
        { uuid: [user?.info?.uuid || user?.user?.[0]?.uuid] },
        {}
      );
      if (usersResponse?.user?.[0]?.photo) {
        try {
          const file = await Digit.UploadServices.Filefetch([usersResponse?.user?.[0]?.photo], "pg");
          if (file?.data?.fileStoreIds?.[0]?.url) {
            setProfilePhotoUrl(file?.data?.fileStoreIds?.[0]?.url.split(",")[0]);
          }
        } catch (err) {
          console.error("Error fetching profile photo:", err);
        }
      }
    };

    fetchPhoto();
  }, [user?.info?.photo, tenantId]);

  const toggleMenu = (key) => {
    setOpenMenus((prev) => ({ ...prev, [key]: !prev[key] }));
  };

  const handleLogout = () => {
    setShowDialog(true);
  };
  const handleOnSubmit = () => {
    Digit.UserService.logout();
    setShowDialog(false);
  };
  const handleOnCancel = () => {
    setShowDialog(false);
  };

  /* ==========================================================================
     MDMS ACCESS CONTROL GROUPING & LOGIC
     ========================================================================== */
  let configEmployeeSideBar = {};

  if (!isLoading && data?.actions?.length > 0) {
    let filteredActions = data?.actions?.filter((e) => e.url === "url");
    if (filteredActions?.length > 0) {
      filteredActions.forEach((item) => {
        let index = item.path.split(".")[0];
        if (index === "TradeLicense") index = "Trade License";
        if (index === "") return;
        if (!configEmployeeSideBar[index]) {
          configEmployeeSideBar[index] = [item];
        } else {
          configEmployeeSideBar[index].push(item);
        }
      });
    }
  }

  let filteredKeys = Object.keys(configEmployeeSideBar);
  if (search.trim()) {
    filteredKeys = filteredKeys.filter((key) => {
      const moduleKey = key.replace(/[ -]/g, "_");
      const translation = t(`ACTION_TEST_${Digit.Utils.locale.getTransformedLocale(moduleKey)}`) || key;
      const matchesParent =
        translation.toLowerCase().includes(search.trim().toLowerCase()) ||
        key.toLowerCase().includes(search.trim().toLowerCase());
      const matchesChild = configEmployeeSideBar[key]?.some((child) => {
        const childName = child?.displayName || "";
        const childTrans = t(`ACTION_TEST_${childName.toUpperCase().replace(/[ -]/g, "_")}`) || childName;
        return (
          childTrans.toLowerCase().includes(search.trim().toLowerCase()) ||
          childName.toLowerCase().includes(search.trim().toLowerCase())
        );
      });
      return matchesParent || matchesChild;
    });
  }

  let res = [];
  filteredKeys.sort((a, b) => {
    const orderA = configEmployeeSideBar[a]?.[0]?.orderNumber || 100;
    const orderB = configEmployeeSideBar[b]?.[0]?.orderNumber || 100;
    return orderA - orderB;
  });

  for (let i = 0; i < filteredKeys.length; i++) {
    const key = filteredKeys[i];
    const items = configEmployeeSideBar[key];
    if (items[0].path.indexOf(".") === -1) {
      if (items[0].displayName === "Home" || key.toUpperCase() === "HOME") {
        res.unshift({
          moduleName: key.toUpperCase(),
          icon: items[0]?.leftIcon || "HomeIcon",
          navigationURL: "/upyog-ui/employee",
          type: "single",
        });
      } else {
        res.push({
          moduleName: items[0]?.displayName?.toUpperCase() || key.toUpperCase(),
          type: "single",
          icon: items[0]?.leftIcon || items[0],
          navigationURL: items[0].navigationURL,
        });
      }
    } else {
      res.push({
        moduleName: key.toUpperCase(),
        links: items,
        icon: items[0]?.leftIcon || items[0],
        orderNumber: items[0].orderNumber,
      });
    }
  }

  // Ensure HOME is at the very top
  const homeIndex = res.findIndex((a) => a.moduleName === "HOME");
  if (homeIndex > -1) {
    const home = res.splice(homeIndex, 1)[0];
    res.unshift(home);
  } else if (!search.trim()) {
    res.unshift({
      moduleName: "HOME",
      icon: "HomeIcon",
      navigationURL: "/upyog-ui/employee",
      type: "single",
    });
  }

  // Auto-expand menu when active path matches child link
  useEffect(() => {
    if (res?.length > 0) {
      res.forEach((module) => {
        if (module?.links?.length > 0) {
          const hasActiveChild = module.links.some((linkItem) => {
            const childUrl = resolveNavUrl(linkItem?.navigationURL || linkItem?.link);
            return (
              pathname === childUrl ||
              (childUrl !== "/upyog-ui/employee" && childUrl && pathname.startsWith(childUrl))
            );
          });
          if (hasActiveChild) {
            setOpenMenus((prev) => ({ ...prev, [module.moduleName]: true }));
          }
        }
      });
    }
  }, [pathname, res?.length]);

  if (isLoading) {
    return <Loader />;
  }

  let profileItem;
  if (user && user.access_token) {
    profileItem = (
      <Profile
        info={user?.info}
        stateName={stateInfo?.name || "City A"}
        t={t}
        profilePhotoUrl={profilePhotoUrl}
        isSidebarCollapsed={isSidebarCollapsed}
      />
    );
  }

  return (
    <React.Fragment>
      <div className="employee-sidebar-inner">
        {/* Logo Section */}
        <div className="sidebar-logo-header">
          <div className="logo-design">
            {isSidebarCollapsed ? (
              <img src="/upyog-ui/images/logo-mobile.png" alt="UPYOG Logo" />
            ) : (
              <img src="/upyog-ui/images/Logo.png" alt="UPYOG Logo" />
            )}
          </div>
          {/* {closeMobileSidebar && (
              <button
                type="button"
                className="sidebar-mobile-close-btn"
                onClick={closeMobileSidebar}
                aria-label="Close Sidebar"
              >
                ✕
              </button>
            )} */}
        </div>

        {/* Profile Section */}
        {profileItem}

        {/* Search Box */}
        {!isSidebarCollapsed && (
          <div className="employee-sidebar-search-box">
            <SearchIcon className="search-icon" />
            <input
              type="text"
              placeholder={
                t("ACTION_TEST_SEARCH") !== "ACTION_TEST_SEARCH" ? t("ACTION_TEST_SEARCH") : "Search"
              }
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

        {/* Desktop Drawer with dynamic MDMS modules & Accordion submenus */}
        <div className="drawer-desktop">
          {res?.map((item, index) => {
            const icon = getModuleIcon(item);
            const getModuleName = item?.moduleName?.replace(/[ -]/g, "_");
            const appendTranslate = t(`ACTION_TEST_${getModuleName}`);
            const displayName =
              appendTranslate && appendTranslate !== `ACTION_TEST_${getModuleName}`
                ? appendTranslate
                : item?.moduleName;

            if (item.type === "single" || !item.links || item.links.length === 0) {
              const targetUrl = resolveNavUrl(item.navigationURL);
              const isSingleActive =
                pathname === targetUrl ||
                (targetUrl !== "/upyog-ui/employee" && targetUrl && pathname.startsWith(targetUrl));

              return (
                <div className={`sidebar-list ${isSingleActive ? "active" : ""}`} key={index}>
                  <Link
                    to={targetUrl}
                    className="menu-item"
                    title={displayName}
                    onClick={() => closeMobileSidebar && closeMobileSidebar()}
                  >
                    {icon}
                    {!isSidebarCollapsed && <div className="menu-label">{displayName}</div>}
                  </Link>
                </div>
              );
            }

            // Collapsible SubMenu Accordion
            const isOpen = search.trim() ? true : !!openMenus[item.moduleName];
            const validLinks = item.links
              ?.sort((a, b) => (a.orderNumber || 100) - (b.orderNumber || 100))
              ?.filter((l) => l.url === "url" || l.url !== "");

            const isChildActive = validLinks?.some((l) => {
              const cUrl = resolveNavUrl(l?.navigationURL || l?.link);
              return pathname === cUrl || (cUrl !== "/upyog-ui/employee" && cUrl && pathname.startsWith(cUrl));
            });

            return (
              <div className="employee-accordion-item" key={index}>
                <div
                  className={`sidebar-list sidebar-pointer-item ${isChildActive ? "active" : ""}`}
                  onClick={() => !isSidebarCollapsed && toggleMenu(item.moduleName)}
                >
                  <div className="menu-item" title={displayName}>
                    {icon}
                    {!isSidebarCollapsed && (
                      <div className="menu-label">{displayName}</div>
                    )}
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

                {/* Submenu links */}
                {isOpen && !isSidebarCollapsed && (
                  <div className="employee-submenu-wrapper">
                    {validLinks.map((linkItem, idx) => {
                      const childUrl = resolveNavUrl(linkItem?.navigationURL || linkItem?.link);
                      const isCurrentActive =
                        pathname === childUrl ||
                        (childUrl !== "/upyog-ui/employee" && childUrl && pathname.startsWith(childUrl));
                      const childRawName = linkItem?.displayName || "";
                      const childKey = childRawName.toUpperCase().replace(/[ -]/g, "_");
                      const childTranslate = t(`ACTION_TEST_${childKey}`);
                      const childDisplayName =
                        childTranslate && childTranslate !== `ACTION_TEST_${childKey}`
                          ? childTranslate
                          : childRawName;

                      return (
                        <div className={`sidebar-sublist ${isCurrentActive ? "active" : ""}`} key={idx}>
                          <Link
                            to={childUrl}
                            className="submenu-item"
                            title={childDisplayName}
                            onClick={() => closeMobileSidebar && closeMobileSidebar()}
                          >
                            <span className="sublink-dot">•</span>
                            <span className="submenu-label">{childDisplayName}</span>
                          </Link>
                        </div>
                      );
                    })}
                  </div>
                )}
              </div>
            );
          })}

          {/* Profile Link */}
          {user && user.access_token && (
            <div className={`sidebar-list ${pathname === "/upyog-ui/employee/user/profile" ? "active" : ""}`}>
              <Link
                to="/upyog-ui/employee/user/profile"
                className="menu-item"
                title={t("EDIT_PROFILE")}
                onClick={() => closeMobileSidebar && closeMobileSidebar()}
              >
                <EditPencilIcon className="sidebar-icon" />
                {!isSidebarCollapsed && <div className="menu-label">{t("EDIT_PROFILE")}</div>}
              </Link>
            </div>
          )}
        </div>

        {/* Logout Button */}
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
          ></LogoutDialog>
        )}
      </div>
    </React.Fragment>
  );
};

export default EmployeeSideBar;
