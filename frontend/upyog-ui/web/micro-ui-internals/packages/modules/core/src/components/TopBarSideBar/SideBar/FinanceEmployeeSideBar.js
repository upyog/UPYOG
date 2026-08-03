import React, { useRef, useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import FinanceSubMenu from "./FinanceSubMenu";
import FinanceSideBar from "./FinanceSideBar";
import { Loader, SearchIcon } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import NavItem from "./NavItem";
import _, { findIndex } from "lodash";

/**
 * FinanceEmployeeSideBar handles rendering the employee sidebar structure when
 * the Finance module is enabled. It parses Access Control actions, filters search queries,
 * maps path hierarchy levels, and renders FinanceSubMenu or FinanceSideBar depending on active path state.
 */
const FinanceEmployeeSideBar = ({ microUiModuleEnable, isFinanceEnabled }) => {
  const sidebarRef = useRef(null);
  const { isLoading, data } = Digit.Hooks.useAccessControl();
  const [search, setSearch] = useState("");
  const location = useLocation();
  const [activePath, setActivePath] = useState(
    location.pathname.includes("/finance") ? "Finance" : ""
  );
  const { t } = useTranslation();

  // added  !sidebarRef.current as a safety check ensure sidebarRef.current is not null.  Removed loader as useEffect now either need nothing in return or cleanup function
  useEffect(() => {
    if (isLoading || !sidebarRef.current) {
      return;
    }
    sidebarRef.current.style.cursor = "pointer";
    collapseNav();
  }, [isLoading]);

  /*
   * Helper to find and set the active sidebar path on page reload.
   * It maps the current browser URL to the correct menu category.
   */
  const getActivePathFromUrl = (actions, pathname) => {
    if (!pathname || !actions || actions.length === 0) return "Finance";
    
    // Regex matching any standard application path prefixes to extract standard service routes
    const prefixRegex = /^\/(upyog-ui\/employee\/finance|employee\/finance|upyog-ui\/employee|employee|finance)\//i;
    let cleanPath = pathname.replace(prefixRegex, "");
    if (cleanPath.startsWith("/")) cleanPath = cleanPath.substring(1);

    const matchedAction = actions.find(action => {
      if (!action.navigationURL) return false;
      let nav = action.navigationURL.replace(prefixRegex, "");
      if (nav.startsWith("/")) nav = nav.substring(1);
      
      return nav && cleanPath && (nav.toLowerCase() === cleanPath.toLowerCase() || cleanPath.toLowerCase().includes(nav.toLowerCase()));
    });

    if (matchedAction && matchedAction.path) {
      let itemPath = matchedAction.path;
      if (itemPath.startsWith("EGF")) {
        itemPath = itemPath.replace("EGF", "Finance");
      } else if (!itemPath.startsWith("Finance")) {
        itemPath = "Finance." + itemPath;
      }
      const parts = itemPath.split(".");
      if (parts.length > 1) {
        parts.pop();
        return parts.join(".");
      }
      return itemPath;
    }
    return "Finance";
  };

  useEffect(() => {
    if (isLoading || !data?.actions) return;
    
    if (location.pathname.includes("/finance")) {
      const resolvedPath = getActivePathFromUrl(data.actions, location.pathname);
      setActivePath(resolvedPath);
    } else {
      setActivePath("");
    }
  }, [isLoading, data, location.pathname]);

  const expandNav = () => {
    sidebarRef.current.style.width = "260px";
    sidebarRef.current.style.overflow = "auto";

    sidebarRef.current.querySelectorAll(".dropdown-link").forEach((element) => {
      element.style.display = "flex";
    });
  };
  const collapseNav = () => {
    sidebarRef.current.style.width = "55px";
    sidebarRef.current.style.overflow = "hidden";

    sidebarRef.current.querySelectorAll(".dropdown-link").forEach((element) => {
      element.style.display = "none";
    });
    sidebarRef.current.querySelectorAll(".actions").forEach((element) => {
      element.style.padding = "0";
    });
  };

  // MDMS configuration and flags are passed as props from parent EmployeeSideBar

  const configEmployeeSideBar = {};

  /*
   * Map over the user's allowed actions to check which feature modules are enabled.
   * - If a module is disabled in MDMS config, rewrite its URLs to redirect to the Mono-UI portal.
   * - For EGF / Finance, group them under the custom "Finance" path and normalize the names.
   */
  const actionsList = (data?.actions || [])
    .map(item => {
      const pathRoot = item.path?.split?.(".")[0]?.toLowerCase() || "";
      const parentModule = item.parentModule?.toLowerCase() || "";
      const navUrl = item.navigationURL?.toLowerCase() || "";

      // Find if this item belongs to any module specified in MDMS config
      const matchedConfig = microUiModuleEnable.find(config => {
        const codeLower = config.code?.toLowerCase();
        const pathMatches = pathRoot === codeLower;
        const parentModuleMatches = parentModule.includes(codeLower);
        const navUrlMatches = navUrl.includes(codeLower);
        let egfMatches = false;
        if (codeLower === "finance") {
          egfMatches = pathRoot === "egf" || parentModule.includes("egf") || navUrl.includes("services/egf") || navUrl.includes("services/");
        }
        return pathMatches || parentModuleMatches || navUrlMatches || egfMatches;
      });

      const isEnabled = matchedConfig ? matchedConfig.enabled : true;

      if (!isEnabled) {
        // If disabled, rewrite navigation URL to route to legacy mono-ui
        if (item.navigationURL) {
          let cleanUrl = item.navigationURL;
          if (!cleanUrl.startsWith("/employee/") && !cleanUrl.startsWith("/digit-ui/")) {
            cleanUrl = cleanUrl.replace("/upyog-ui/employee/", "");
            cleanUrl = cleanUrl.replace("/digit-ui/employee/", "");
            cleanUrl = cleanUrl.replace("/upyog-ui/", "");
            cleanUrl = cleanUrl.replace("/digit-ui/", "");
            if (cleanUrl.startsWith("/")) {
              cleanUrl = "/employee" + cleanUrl;
            } else {
              cleanUrl = "/employee/" + cleanUrl;
            }
          }
          return {
            ...item,
            navigationURL: cleanUrl
          };
        }
        return item;
      }

      // If enabled and it is EGF/Finance, apply prefix for nested iframe routing inside upyog-ui
      const isFinance = isFinanceEnabled && item.path && (
        item.path.startsWith("Finance") || 
        item.path.startsWith("EGF") || 
        (item.parentModule && item.parentModule.toLowerCase().includes("egf")) ||
        (item.parentModule && item.parentModule.toLowerCase().includes("finance")) ||
        (item.navigationURL && item.navigationURL.includes("services/"))
      );
      if (isFinance) {
        let newPath = item.path;
        if (newPath.startsWith("EGF")) {
          newPath = newPath.replace("EGF", "Finance");
        } else if (!newPath.startsWith("Finance")) {
          newPath = "Finance." + newPath;
        }
        if (item.displayName && newPath.includes(".")) {
          const parts = newPath.split(".");
          parts[parts.length - 1] = item.displayName;
          newPath = parts.join(".");
        }
        return {
          ...item,
          path: newPath
        };
      }
      return item;
    }) || [];

  const filterFn = (e) => e.url === "url" || (isFinanceEnabled && e.path && e.path.startsWith("Finance"));

  //creating the object structure from mdms value for easy iteration
  let configEmployeeSideBar1 = {};
  actionsList.filter(filterFn)?.forEach((item) => {
    if (isFinanceEnabled) {
      const parts = item.path.split(".");
      let current = configEmployeeSideBar1;
      for (let i = 0; i < parts.length; i++) {
        const part = parts[i];
        if (i === parts.length - 1) {
          if (current[part]) {
            current[part] = { ...item, ...current[part] };
          } else {
            current[part] = { ...item };
          }
        } else {
          if (!current[part]) {
            current[part] = {};
          }
          current = current[part];
        }
      }
    } else {
      _.set(configEmployeeSideBar1, item.path, { ...item });
    }
  });

  actionsList
    .filter(filterFn)
    .forEach((item) => {
      let index = item.path.split(".")[0];
      if (search == "" && item.path !== "") {
         index = item.path.split(".")[0];
        if (index === "TradeLicense") index = "Trade License";
        if (!configEmployeeSideBar[index]) {
          configEmployeeSideBar[index] = [item];
        } else {
          configEmployeeSideBar[index].push(item);
        }
      } else if (item.path !== "" && t(`ACTION_TEST_${index?.toUpperCase()?.replace(/[ -]/g, "_")}`)?.toLowerCase().includes(search.toLowerCase())) {
         index = item.path.split(".")[0];
        if (index === "TradeLicense") index = "Trade License";
        if (!configEmployeeSideBar[index]) {
          configEmployeeSideBar[index] = [item];
        } else {
          configEmployeeSideBar[index].push(item);
        }
      }
    });
  let res = [];

  const splitKeyValue = () => {
    const keys = Object.keys(configEmployeeSideBar);
    keys.sort((a, b) => a.orderNumber - b.orderNumber);
    for (let i = 0; i < keys.length; i++) {
      if (configEmployeeSideBar[keys[i]][0].path.indexOf(".") === -1) {
        if (configEmployeeSideBar[keys[i]][0].displayName === "Home") {
          const homeURL = "/upyog-ui/employee";
          res.unshift({
            moduleName: keys[i].toUpperCase(),
            icon: configEmployeeSideBar[keys[i]][0],
            navigationURL: homeURL,
            type: "single",
          });
        } else {
          res.push({
            moduleName: configEmployeeSideBar[keys[i]][0]?.displayName.toUpperCase(),
            type: "single",
            icon: configEmployeeSideBar[keys[i]][0],
            navigationURL: configEmployeeSideBar[keys[i]][0].navigationURL,
          });
        }
      } else {
        if (keys[i].toUpperCase() === "FINANCE" && isFinanceEnabled) {
          res.push({
            moduleName: keys[i].toUpperCase(),
            links: [configEmployeeSideBar[keys[i]]],
            icon: { leftIcon: "finance:insert-chart" },
            orderNumber: 0,
          });
        } else {
          let iconItem = configEmployeeSideBar[keys[i]].find(item => item.leftIcon) || configEmployeeSideBar[keys[i]][0];
          if (keys[i].toUpperCase() === "FINANCE" && !iconItem.leftIcon) {
            iconItem = { ...iconItem, leftIcon: "finance:insert-chart" };
          }
          res.push({
            moduleName: keys[i].toUpperCase(),
            links: configEmployeeSideBar[keys[i]],
            icon: iconItem,
            orderNumber: configEmployeeSideBar[keys[i]][0].orderNumber,
          });
        }
      }
    }
    if(res.find(a => a.moduleName === "HOME"))
    {
      const indx = res.findIndex(a => a.moduleName === "HOME");
      const home = res?.filter((ob) => ob?.moduleName === "HOME")
      let res1 = res?.filter((ob) => ob?.moduleName !== "HOME")
      res = res1.sort((a,b) => a.moduleName.localeCompare(b.moduleName));
      home?.[0] && res.unshift(home[0]);
    }
    else
    {
      res.sort((a,b) => a.moduleName.localeCompare(b.moduleName));
    }
    if (isFinanceEnabled) {
      return res?.map((item, index) => {
        return <FinanceSubMenu item={item} key={index + 1} onHeaderClick={(moduleName) => {
          if (moduleName === "FINANCE") {
            setActivePath("Finance");
          }
        }} />;
      });
    } else {
      return res?.map((item, index) => {
        return <FinanceSubMenu item={item} key={index + 1} />;
      });
    }
  };

  if (isLoading) {
    return <Loader />;
  }
  if (!res) {
    return "";
  }

  const renderSearch = () => {
    return (
      <div className="submenu-container">
        <div className="sidebar-link">
          <div className="actions search-icon-wrapper">
            <SearchIcon className="search-icon" />
            <input
              className="employee-search-input"
              type="text"
              placeholder={t(`ACTION_TEST_SEARCH`)}
              name="search"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>
        </div>
      </div>
    );
  };

  return (
    <div className="sidebar" ref={sidebarRef} onMouseOver={expandNav} onMouseLeave={collapseNav} style={{display:window.location.href.includes("main-dashboard-landing")?"none":""}}>
      {renderSearch()}
      {isFinanceEnabled && activePath ? (
        <FinanceSideBar
          activePath={activePath}
          setActivePath={setActivePath}
          configEmployeeSideBar1={configEmployeeSideBar1}
          t={t}
        />
      ) : (
        splitKeyValue()
      )}
    </div>
  );
};

export default FinanceEmployeeSideBar;
