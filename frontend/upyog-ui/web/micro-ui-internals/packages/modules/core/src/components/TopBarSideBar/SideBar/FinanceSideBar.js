import React from "react";
import { Link, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
const getRoutePath = (navUrl) => {
  if (!navUrl) return "";
  const isUpyog = window.location.href.includes("/upyog-ui");
  const prefix = isUpyog ? "/upyog-ui/employee/finance/" : "/employee/finance/";
  const url = isUpyog ? navUrl.replace("digit-ui", "upyog-ui") : navUrl;
  return url.includes("upyog-ui") || url.includes("digit-ui") ? url : prefix + url;
};
import {
  FinanceChartIcon,
  CollectionIcon,
} from "@nudmcdgnpm/digit-ui-react-components";

const IconsObject = {
  "insert-chart": <FinanceChartIcon />,
  finance: <FinanceChartIcon />,
  collections: <CollectionIcon />,
};

const getIcon = (node) => {
  if (!node) return null;
  let iconStr = node.leftIcon || node.icon?.leftIcon;
  if (!iconStr && typeof node === "object") {
    for (const k in node) {
      if (typeof node[k] === "object") {
        const childIcon = getIcon(node[k]);
        if (childIcon) {
          iconStr = childIcon;
          break;
        }
      }
    }
  }
  return iconStr;
};



const getNodeByPath = (obj, pathString) => {
  if (!pathString) return obj;
  const parts = pathString.split(".");
  let current = obj;
  for (const part of parts) {
    if (current && current[part]) {
      current = current[part];
    } else {
      return null;
    }
  }
  return current;
};

const getMinOrderNumber = (node) => {
  if (!node) return 9999;
  if (typeof node === "object" && node.id !== undefined) {
    return node.orderNumber || 0;
  }
  let min = 9999;
  for (const key in node) {
    const val = node[key];
    const order = getMinOrderNumber(val);
    if (order < min) {
      min = order;
    }
  }
  return min;
};

/**
 * FinanceSideBar renders the custom inner sidebar layout when the user navigates into
 * the Finance section. It displays nested categories (like Transactions, Masters, Reports)
 * along with back navigation controls and layout padding aligned to core specs.
 */
const FinanceSideBar = ({ activePath, setActivePath, configEmployeeSideBar1 }) => {
  const { t, i18n } = useTranslation();
  const { pathname } = useLocation();
  const currentNode = getNodeByPath(configEmployeeSideBar1, activePath);
  if (!currentNode) return null;

  const menuTitle = activePath.split(".").pop();
  const handleBack = () => {
    const parts = activePath.split(".");
    parts.pop();
    setActivePath(parts.join("."));
  };

  return (
    <React.Fragment>
      <div className="submenu-container" style={{ display: "flex", alignItems: "center", padding: "20px", color: "white", borderBottom: "1px solid rgba(255,255,255,0.1)", marginBottom: "10px" }}>
        <div onClick={handleBack} style={{ cursor: "pointer", marginRight: "20px", display: "flex", alignItems: "center" }}>
          <svg xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24" fill="white">
            <path d="M0 0h24v24H0z" fill="none" />
            <path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z" />
          </svg>
        </div>
        <div className="actions">
          <FinanceChartIcon />
          <span style={{ fontSize: "16px", fontWeight: "bold" }}>
            {t(`ACTION_TEST_${menuTitle.toUpperCase().replace(/[ -]/g, "_")}`) || menuTitle}
          </span>
        </div>
      </div>

      <div className="submenu-links" style={{ overflowX: "auto", display: "flex", flexDirection: "column" }}>
        {Object.keys(currentNode)
          .filter((key) => !["id", "name", "url", "displayName", "orderNumber", "parentModule", "serviceCode", "code", "leftIcon", "path", "navigationURL", "enabled"].includes(key))
          .sort((a, b) => {
            const valA = currentNode[a];
            const valB = currentNode[b];
            return getMinOrderNumber(valA) - getMinOrderNumber(valB);
          }).map((key) => {
          const nodeValue = currentNode[key];
          const hasChildren = typeof nodeValue === "object" && !nodeValue.id;
          
          const translationKey = hasChildren 
            ? `ACTION_TEST_${key.toUpperCase().replace(/[ -]/g, "_")}`
            : `ACTION_TEST_${nodeValue.displayName ? nodeValue.displayName.toUpperCase().replace(/[.:-\s\/]/g, "_") : key.toUpperCase().replace(/[ -]/g, "_")}`;
          
          const displayLabel = i18n.exists(translationKey)
            ? t(translationKey)
            : (hasChildren ? key : (nodeValue.displayName || key));

          const iconStr = nodeValue?.leftIcon || getIcon(nodeValue);
          const iconKey = iconStr?.split?.(":")?.[1];
          const leftIcon = IconsObject[iconKey] || IconsObject.collections;

          if (hasChildren) {
            return (
              <div
                key={key}
                onClick={() => setActivePath(`${activePath}.${key}`)}
                className="sidebar-link"
                title={displayLabel}
                style={{ cursor: "pointer", padding: "20px", color: "white", display: "flex", justifyContent: "space-between", alignItems: "center", width: "max-content", minWidth: "100%" }}
              >
                <div className="actions">
                  {leftIcon}
                  <span title={displayLabel} style={{ fontSize: "14px", whiteSpace: "nowrap", paddingRight: "10px" }}>{displayLabel}</span>
                </div>
                <svg xmlns="http://www.w3.org/2000/svg" height="20" viewBox="0 0 24 24" width="20" fill="white" style={{ flexShrink: 0 }}>
                  <path d="M0 0h24v24H0z" fill="none" />
                  <path d="M10 6L8.59 7.41 13.17 12l-4.58 4.59L10 18l6-6z" />
                </svg>
              </div>
            );
          } else {
            const targetPath = getRoutePath(nodeValue.navigationURL);
            const isActive = pathname === targetPath;
            return (
              <Link
                key={key}
                to={targetPath}
                className={`sidebar-link ${isActive ? "active" : ""}`}
                title={displayLabel}
              >
                <div className="actions">
                  {leftIcon}
                  <span title={displayLabel} style={{ fontSize: "14px", whiteSpace: "nowrap" }}>{displayLabel}</span>
                </div>
              </Link>
            );
          }
        })}
      </div>
    </React.Fragment>
  );
};

export default FinanceSideBar;
