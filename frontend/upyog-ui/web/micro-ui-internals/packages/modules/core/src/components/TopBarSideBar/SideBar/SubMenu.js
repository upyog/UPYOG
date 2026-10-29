import React, { useState, useContext } from "react";
import { Link, useLocation } from "react-router-dom";
import {
  ArrowForward,
  ArrowVectorDown,
  ArrowDirection,
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
  CollectionIcon,
} from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import ReactTooltip from "react-tooltip";

const SubMenu = ({ item }) => {
  const [subnav, setSubnav] = useState(false);
  const location = useLocation();
  const { pathname } = location;
  const { t } = useTranslation();
  const showSubnav = () => setSubnav(!subnav);

  const IconsObject = {
    home: <HomeIcon />,
    Home: <HomeIcon />,
    HomeIcon: <HomeIcon />,
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
    edcr: <CollectionIcon />,
    collections: <CollectionIcon />,
    CommonPTIcon: <PropertyHouse />,
    propertyIcon: <PropertyHouse />,
    PTIcon: <PropertyHouse />,
    TLIcon: <CaseIcon />,
    CaseIcon: <CaseIcon />,
    PGRIcon: <ComplaintIcon />,
    ComplaintIcon: <ComplaintIcon />,
    WSIcon: <DropIcon />,
    MCollectIcon: <ReceiptIcon />,
    BillsIcon: <CollectionIcon />,
    CHBIcon: <BPAHomeIcon />,
    OBPSIcon: <BPAHomeIcon />,
    PersonIcon: <PersonIcon />,
    ReceiptIcon: <ReceiptIcon />,
    DropIcon: <DropIcon />,
  };

  const rawLeftIcon = item?.icon?.leftIcon || item?.leftIcon || item?.icon || "";
  const leftIconKey = typeof rawLeftIcon === "string" && rawLeftIcon.includes(":") ? rawLeftIcon.split(":")[1] : rawLeftIcon;
  const leftIcon = IconsObject[leftIconKey] || IconsObject[item?.moduleName] || IconsObject.collections;

  const getModuleName = item?.moduleName?.replace(/[ -]/g, "_");
  const appendTranslate = t(`ACTION_TEST_${getModuleName}`);
  const trimModuleName = t(appendTranslate?.length > 20 ? appendTranslate.substring(0, 20) + "..." : appendTranslate);

  const resolveNavUrl = (navUrl) => {
    if (!navUrl) return "#";
    let formatted = navUrl.replace("/digit-ui/", "/upyog-ui/");
    if (!formatted.startsWith("/upyog-ui") && !formatted.startsWith("http") && !formatted.startsWith("/")) {
      formatted = "/upyog-ui/employee/" + formatted;
    }
    return formatted;
  };

  if (item.type === "single") {
    const targetUrl = resolveNavUrl(item.navigationURL);
    const isSingleActive = pathname === targetUrl || (targetUrl !== "/upyog-ui/employee" && targetUrl && pathname.startsWith(targetUrl));

    return (
      <div className="submenu-container">
        <div className={`sidebar-link ${isSingleActive ? "active" : ""}`}>
          <div className="actions">
            {leftIcon}
            <Link className="custom-link" to={targetUrl}>
              <div data-tip="React-tooltip" data-for={`jk-side-${getModuleName}`}>
                <span> {trimModuleName} </span>

                {trimModuleName?.includes("...") && (
                  <ReactTooltip textColor="white" backgroundColor="grey" place="right" type="info" effect="solid" id={`jk-side-${getModuleName}`}>
                    {t(`ACTION_TEST_${getModuleName}`)}
                  </ReactTooltip>
                )}
              </div>
            </Link>
          </div>
        </div>
      </div>
    );
  } else {
    return (
      <React.Fragment>
        <div className="submenu-container">
          <div onClick={item.links && showSubnav} className="sidebar-link">
            <div className="actions">
              {leftIcon}
              <div data-tip="React-tooltip" data-for={`jk-side-${getModuleName}`}>
                <span> {trimModuleName} </span>

                {trimModuleName?.includes("...") && (
                  <ReactTooltip textColor="white" backgroundColor="grey" place="right" type="info" effect="solid" id={`jk-side-${getModuleName}`}>
                    {t(`ACTION_TEST_${getModuleName}`)}
                  </ReactTooltip>
                )}
              </div>
            </div>
            <div> {item.links && subnav ? <ArrowVectorDown /> : item.links ? <ArrowForward /> : null} </div>
          </div>
        </div>

        {subnav &&
          item.links
            ?.sort((a, b) => (a.orderNumber || 100) - (b.orderNumber || 100))
            ?.filter((linkItem) => linkItem.url === "url" || linkItem.url !== "")
            ?.map((linkItem, index) => {
              const getChildName = linkItem?.displayName?.toUpperCase()?.replace(/[ -]/g, "_");
              const childTranslate = t(`ACTION_TEST_${getChildName}`);
              const trimChildName = t(childTranslate?.length > 20 ? childTranslate.substring(0, 20) + "..." : childTranslate);
              const childUrl = resolveNavUrl(linkItem?.link || linkItem?.navigationURL);
              const isChildActive = pathname === childUrl || (childUrl !== "/upyog-ui/employee" && childUrl && pathname.startsWith(childUrl));

              return (
                <Link
                  to={childUrl}
                  key={index}
                  className={`dropdown-link ${isChildActive ? "active" : ""}`}
                >
                  <div className="actions" data-tip="React-tooltip" data-for={`jk-side-${index}`}>
                    <span> {trimChildName} </span>
                    {trimChildName?.includes("...") && (
                      <ReactTooltip textColor="white" backgroundColor="grey" place="right" type="info" effect="solid" id={`jk-side-${index}`}>
                        {t(`ACTION_TEST_${getChildName}`)}
                      </ReactTooltip>
                    )}
                  </div>
                </Link>
              );
            })}
      </React.Fragment>
    );
  }
};

export default SubMenu;

