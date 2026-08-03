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
} from "@nudmcdgnpm/upyog-ui-react-components-lts";
import { useTranslation } from "react-i18next";
import { Tooltip } from "react-tooltip";

const SubMenu = ({ item }) => {
  const [subnav, setSubnav] = useState(false);
  const location = useLocation();
  const { pathname } = location;
  const { t } = useTranslation();
  const showSubnav = () => setSubnav(!subnav);
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
    edcr: <CollectionIcon />,
    collections: <CollectionIcon />,
  };
  const leftIconArray = item?.icon?.leftIcon?.split?.(":")?.[1] || item?.leftIcon?.split?.(":")[1];
  const leftIcon = IconsObject[leftIconArray] || IconsObject.collections;
  const getModuleName = item?.moduleName?.replace(/[ -]/g, "_");
  const appendTranslate = t(`ACTION_TEST_${getModuleName}`);
  const trimModuleName = t(appendTranslate?.length > 20 ? appendTranslate.substring(0, 20) + "..." : appendTranslate);
  const tooltipId = `jk-side-${getModuleName}`;

  if (item.type === "single") {
    const getOrigin = window.location.origin;
    return (
      <div className="submenu-container">
        <div className={`sidebar-link  ${pathname === item?.navigationURL ? "active" : ""}`}>
          <div className="actions">
            {leftIcon}
            {item.navigationURL?.indexOf("/sv-ui") === -1? (
              <a
                data-tooltip-id={tooltipId}
                data-tooltip-content={t(`ACTION_TEST_${getModuleName}`)}
                className="custom-link"
                href={getOrigin + `${item.navigationURL.includes("/workbench-ui")?"":"/employee/"}`+ item.navigationURL}
              >
                <span> {trimModuleName} </span>

               {trimModuleName?.includes("...") && <Tooltip id={tooltipId} place="right" style={{backgroundColor:"grey",color:"white"}} />}
              </a>
            ) : (
              <Link className="custom-link" to={item.navigationURL}>
                <div data-tooltip-id={tooltipId} data-tooltip-content={t(`ACTION_TEST_${getModuleName}`)}>
                  <span> {trimModuleName} </span>

                 {trimModuleName?.includes("...") && <Tooltip id={tooltipId} place="right" style={{backgroundColor:"grey",color:"white"}} />}
                </div>
              </Link>
            )}
          </div>
        </div>
      </div>
    );
  } else {
    return (
      <React.Fragment>
        <div className="submenu-container">
          <div onClick={item.links && showSubnav} className={`sidebar-link`}>
            <div className="actions">
              {leftIcon}
              <div data-tooltip-id={tooltipId} data-tooltip-content={t(`ACTION_TEST_${getModuleName}`)}>
                <span> {trimModuleName} </span>

                {trimModuleName?.includes("...") && <Tooltip id={tooltipId} place="right" style={{backgroundColor:"grey",color:"white"}} />}
              </div>
            </div>
            <div> {item.links && subnav ? <ArrowVectorDown /> : item.links ? <ArrowForward /> : null} </div>
          </div>
        </div>

        {subnav &&
          item.links
          .sort((a, b) => a.orderNumber - b.orderNumber)
            .filter((item) => item.url === "url" || item.url !== "")
            .map((item, index) => {
              const getChildName = item?.displayName?.toUpperCase()?.replace(/[ -]/g, "_");
              const appendTranslate = t(`ACTION_TEST_${getChildName}`);
              const trimModuleName = t(appendTranslate?.length > 20 ? appendTranslate.substring(0, 20) + "..." : appendTranslate);
              const childTooltipId = `jk-side-${index}`;

              if (item.navigationURL.indexOf("/sv-ui") === -1) {
                const getOrigin = window.location.origin;
                return (
                  <a
                    key={index}
                    className={`dropdown-link ${pathname === item.link ? "active" : ""}`}
                    href={getOrigin + "/employee/" + item.navigationURL}
                  >
                    <div className="actions" data-tooltip-id={childTooltipId} data-tooltip-content={t(`ACTION_TEST_${getChildName}`)}>
                      <span> {trimModuleName} </span>
                    {trimModuleName?.includes("...") && <Tooltip id={childTooltipId} place="right" style={{backgroundColor:"grey",color:"white"}} />}
                    </div>
                  </a>
                );
              }
              return (
                <Link
                  to={item?.link || item.navigationURL}
                  key={index}
                  className={`dropdown-link ${pathname === item?.link || pathname === item?.navigationURL ? "active" : ""}`}
                >
                  <div className="actions" data-tooltip-id={childTooltipId} data-tooltip-content={t(`ACTION_TEST_${getChildName}`)}>
                    <span> {trimModuleName} </span>
                   {trimModuleName?.includes("...") && <Tooltip id={childTooltipId} place="right" style={{backgroundColor:"grey",color:"white"}} />}
                  </div>
                </Link>
              );
            })}
      </React.Fragment>
    );
  }
};

export default SubMenu;
