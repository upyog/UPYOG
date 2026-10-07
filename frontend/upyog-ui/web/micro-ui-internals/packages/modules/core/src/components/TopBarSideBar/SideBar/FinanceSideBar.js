import React from "react";
import { Link, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
const getRoutePath = (navUrl) => {
  if (!navUrl) return "";
  let formatted = navUrl.replace("/digit-ui/", "/upyog-ui/");
  if (formatted.startsWith("http://") || formatted.startsWith("https://")) {
    return formatted;
  }
  if (formatted.startsWith("/employee/")) {
    formatted = "/upyog-ui" + formatted;
  } else if (!formatted.startsWith("/upyog-ui/")) {
    if (formatted.startsWith("/")) {
      formatted = "/upyog-ui/employee" + formatted;
    } else {
      formatted = "/upyog-ui/employee/" + formatted;
    }
  }
  return formatted;
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
 * Helper function to recursively check if a menu node or any of its descendants
 * matches the current search query.
 * - This allows folders (parent nodes) to remain visible if they contain matching sub-menus.
 * - Leaf nodes are filtered out unless they match the search query directly.
 *
 * @param {object} node - The menu node configuration object.
 * @param {string} key - The lookup key for the menu node.
 * @param {string} search - The search term entered by the user.
 * @param {function} t - The translation hook function.
 * @param {object} i18n - The internationalization instance to verify translation existence.
 * @returns {boolean} True if the node or any of its descendants matches the search criteria, false otherwise.
 */
const hasMatchingDescendant = (node, key, search, t, i18n) => {
  if (!search) return true;

  const hasChildren = typeof node === "object" && !node.id;
  const translationKey = hasChildren 
    ? `ACTION_TEST_${key.toUpperCase().replace(/[ -]/g, "_")}`
    : `ACTION_TEST_${node.displayName ? node.displayName.toUpperCase().replace(/[.:-\s\/]/g, "_") : key.toUpperCase().replace(/[ -]/g, "_")}`;
  
  const displayLabel = i18n.exists(translationKey)
    ? t(translationKey)
    : (hasChildren ? key : (node.displayName || key));

  // If the current node's label matches the search, return true
  if (displayLabel.toLowerCase().includes(search.toLowerCase())) {
    return true;
  }

  // If it's a folder, search its child nodes recursively
  if (hasChildren) {
    for (const childKey in node) {
      if (["id", "name", "url", "displayName", "orderNumber", "parentModule", "serviceCode", "code", "leftIcon", "path", "navigationURL", "enabled"].includes(childKey)) {
        continue;
      }
      if (hasMatchingDescendant(node[childKey], childKey, search, t, i18n)) {
        return true;
      }
    }
  }

  return false;
};

/**
 * FinanceSideBar renders the custom inner sidebar layout when the user navigates into
 * the Finance section. It displays nested categories (like Transactions, Masters, Reports)
 * along with back navigation controls and layout padding aligned to core specs.
 */
const FinanceSideBar = ({ activePath, setActivePath, configEmployeeSideBar1, search }) => {
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
      <div className="submenu-container core-finance-side-bar-flex-row">
        <div onClick={handleBack} className="core-finance-side-bar-clickable">
          <svg xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24" fill="white">
            <path d="M0 0h24v24H0z" fill="none" />
            <path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z" />
          </svg>
        </div>
        <div className="actions">
          <FinanceChartIcon />
          <span className="core-finance-side-bar-text-style">
            {t(`ACTION_TEST_${menuTitle.toUpperCase().replace(/[ -]/g, "_")}`) || menuTitle}
          </span>
        </div>
      </div>

      <div className="submenu-links core-finance-side-bar-flex-container">
        {Object.keys(currentNode)
          .filter((key) => !["id", "name", "url", "displayName", "orderNumber", "parentModule", "serviceCode", "code", "leftIcon", "path", "navigationURL", "enabled"].includes(key))
          .filter((key) => hasMatchingDescendant(currentNode[key], key, search, t, i18n))
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
                className="sidebar-link core-finance-side-bar-clickable-2"
                title={displayLabel}
              >
                <div className="actions">
                  {leftIcon}
                  <span title={displayLabel} className="core-finance-side-bar-spacing">{displayLabel}</span>
                </div>
                <svg xmlns="http://www.w3.org/2000/svg" height="20" viewBox="0 0 24 24" width="20" fill="white" className="core-finance-side-bar-icon">
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
                  <span title={displayLabel} className="core-finance-side-bar-text-style-2">{displayLabel}</span>
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
