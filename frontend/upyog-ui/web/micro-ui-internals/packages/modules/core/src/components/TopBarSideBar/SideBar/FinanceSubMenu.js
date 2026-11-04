import React, { useState } from "react";
import { Link, useLocation } from "react-router-dom";
import {
  HomeIcon,
  ComplaintIcon,
  BPAHomeIcon,
  PropertyHouse,
  CaseIcon,
  ReceiptIcon,
  PersonIcon,
  DocumentIconSolid,
  DropIcon,
  CollectionsBookmarIcons,
  FinanceChartIcon,
  CollectionIcon
} from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import ReactTooltip from "react-tooltip";

/**
 * FinanceSubMenu handles rendering individual dropdown menu items inside the main sidebar.
 * It maps icons (including custom rupee and finance icons) and appends the "/finance/" prefix
 * to EGF navigation links so they match the Finance module routes.
 */
const FinanceSubMenu = ({ item, onHeaderClick }) => {
  const location = useLocation();
  const { pathname } = location;
  const { t } = useTranslation();

  const getHref = (navUrl) => {
    if (!navUrl) return "";
    const isUpyog = window.location.href.includes("/upyog-ui");
    const url = isUpyog ? navUrl.replace("digit-ui", "upyog-ui") : navUrl;
    if (url.startsWith("/employee/") || (url.startsWith("/digit-ui/") && !url.includes("upyog-ui")) || url.startsWith("/upyog-ui/")) {
      return window.location.origin + url;
    }
    const isFinance = item?.moduleName === "FINANCE";
    const prefix = isFinance
      ? (isUpyog ? "/upyog-ui/employee/finance/" : "/employee/finance/")
      : "/employee/";
    return window.location.origin + (url.includes("upyog-ui") || url.includes("digit-ui") ? url : prefix + url);
  };

  const hasActiveChild = React.useMemo(() => {
    if (!item?.links) return false;
    return item.links.some((linkItem) => {
      const nav = linkItem?.navigationURL || linkItem?.link;
      if (!nav) return false;
      const href = getHref(nav);
      const cleanPath = pathname?.split("?")[0]?.replace(/\/+$/, "");
      const cleanHref = href?.replace(window.location.origin, "")?.split("?")[0]?.replace(/\/+$/, "");
      return cleanPath && cleanHref && (cleanPath === cleanHref || cleanPath.startsWith(cleanHref + "/"));
    });
  }, [item, pathname]);

  const [subnav, setSubnav] = useState(hasActiveChild);

  React.useEffect(() => {
    if (hasActiveChild) {
      setSubnav(true);
    }
  }, [hasActiveChild]);

  const showSubnav = () => {
    if (item?.moduleName === "FINANCE" && onHeaderClick) {
      onHeaderClick("FINANCE");
    } else {
      setSubnav(!subnav);
    }
  };

  const IconsObject = {
    home: <HomeIcon />,
    announcement: <ComplaintIcon />,
    business: <BPAHomeIcon />,
    store: <PropertyHouse />,
    assignment: <CaseIcon />,
    receipt: <ReceiptIcon />,
    "business-center": <PersonIcon />,
    description: <DocumentIconSolid />,
    "water-tap": <DropIcon />,
    "collections-bookmark": <CollectionsBookmarIcons />,
    "insert-chart": <FinanceChartIcon />,
    finance: <FinanceChartIcon />,
    edcr: <CollectionIcon />,
    collections: <CollectionIcon />,
  };

  const leftIconArray = item?.icon?.leftIcon?.split?.(":")?.[1] || item?.leftIcon?.split?.(":")[1];
  const leftIcon = IconsObject[leftIconArray] || IconsObject.collections;
  const getOrigin = window.location.origin;

  const getModuleName = item?.moduleName?.replace(/[ -]/g, "_");
  const trimModuleName = t(`ACTION_TEST_${getModuleName}`);

  return (
    <React.Fragment>
      <div className="submenu-container">
        <div className="sidebar-link" onClick={item.links && showSubnav}>
          <div className="actions">
            {leftIcon}
            {item.links ? (
              <span className="name-trigger">{trimModuleName}</span>
            ) : (
              <a
                data-tip="React-tooltip"
                data-for={`jk-side-${getModuleName}`}
                className="custom-link"
                href={getHref(item.navigationURL)}
              >
                <span> {trimModuleName} </span>
              </a>
            )}
          </div>
          <div>
            {item.links && subnav
              ? item.iconNavOpen
              : item.links
                ? item.iconNavClose
                : null}
          </div>
        </div>
      </div>
      {subnav &&
        item.links &&
        item.links.map((childItem, index) => {
          const getModuleName = childItem?.displayName?.replace(/[ -]/g, "_");
          const trimModuleName = t(`ACTION_TEST_${getModuleName}`);
          const targetHref = getHref(childItem.navigationURL || childItem.link);
          const cleanTarget = targetHref.replace(window.location.origin, "").split("?")[0].replace(/\/+$/, "");
          const cleanCurrent = pathname?.split("?")[0]?.replace(/\/+$/, "");
          const isActive = cleanCurrent && cleanTarget && (cleanCurrent === cleanTarget || cleanCurrent.startsWith(cleanTarget + "/"));

          return (
            <div key={index}>
              {childItem.navigationURL || childItem.link ? (
                <a
                  key={index}
                  className={`dropdown-link ${isActive ? "active" : ""}`}
                  href={targetHref}
                >
                  <div className="actions" data-tip="React-tooltip" data-for={`jk-side-${index}`}>
                    <span> {trimModuleName} </span>
                  </div>
                </a>
              ) : (
                <div className="dropdown-link-no-url">
                  <span> {trimModuleName} </span>
                </div>
              )}
            </div>
          );
        })}
    </React.Fragment>
  );
};

export default FinanceSubMenu;