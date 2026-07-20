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
  const [subnav, setSubnav] = useState(false);
  const location = useLocation();
  const { pathname } = location;
  const { t } = useTranslation();

  const showSubnav = () => {
    if (item?.moduleName === "FINANCE" && onHeaderClick) {
      onHeaderClick("FINANCE");
    } else {
      setSubnav(!subnav);
    }
  };

  const getHref = (navUrl) => {
    if (!navUrl) return "";
    const isUpyog = window.location.href.includes("/upyog-ui");
    if (navUrl.startsWith("/employee/") || (navUrl.startsWith("/digit-ui/") && !navUrl.includes("upyog-ui"))) {
      return window.location.origin + navUrl;
    }
    const prefix = isUpyog ? "/upyog-ui/employee/finance/" : "/employee/finance/";
    const url = isUpyog ? navUrl.replace("digit-ui", "upyog-ui") : navUrl;
    return window.location.origin + (url.includes("upyog-ui") || url.includes("digit-ui") ? url : prefix + url);
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
        item.links.map((item, index) => {
          const getModuleName = item?.displayName?.replace(/[ -]/g, "_");
          const trimModuleName = t(`ACTION_TEST_${getModuleName}`);
          return (
            <div key={index}>
              {item.navigationURL ? (
                <a
                  key={index}
                  className={`dropdown-link ${pathname === item.link ? "active" : ""}`}
                  href={getHref(item.navigationURL)}
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
