import React from 'react';
import { NavLink } from 'react-router-dom';
import NavItemHeader from './NavItemHeader';
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
  CollectionIcon,
} from "@nudmcdgnpm/upyog-ui-react-components-lts";
import { Tooltip } from 'react-tooltip';
import { useTranslation } from 'react-i18next';

const NavItem = props => {
  let { label, icon, to, children } = props.item;
  const { t } = useTranslation();
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
  const leftIconArray = icon?.split?.(":")?.[1];
  let leftIcon = IconsObject[leftIconArray] || IconsObject.collections;
  const iconArr=icon?.leftIcon?.split?.(":")|| leftIcon?.split?.(":");
  if(iconArr?.[0]=='dynamic'){
    var IconComp = require("@nudmcdgnpm/upyog-ui-react-components-lts")?.[iconArr?.[1]];
    leftIcon=IconComp?<IconComp/>:leftIcon;
  }
  const getModuleName = label?.replace(/[ -]/g, "_").toUpperCase();
  const appendTranslate = t(`ACTION_TEST_${getModuleName.toUpperCase()}`);
  const trimModuleName = t(appendTranslate?.length > 20 ? appendTranslate.substring(0, 20) + "..." : appendTranslate);
  const tooltipId = `jk-side-${getModuleName}`;

  if (children) {
    return <NavItemHeader item={props.item} />;
  }

  return (
    <div className={`${"submenu-container"}`} style={{marginLeft:"19px", marginBottom:"15px",marginTop:"15px"}}>
    <NavLink
      to={to}
      className={({ isActive }) => isActive ? "submenu-container active" : "submenu-container"}
    >
      <div className="sidebar-link">
      <div className='actions' style={{padding:"0px"}}>
      {leftIcon}
      <div data-tooltip-id={tooltipId} data-tooltip-content={t(`ACTION_TEST_${getModuleName}`)}>
      <span style={{fontSize:"14px"}}>{trimModuleName}</span>
      {trimModuleName?.includes("...") && <Tooltip id={tooltipId} place="right" style={{backgroundColor:"grey",color:"white"}} />}
                </div>
      </div>
      </div>
      
    </NavLink>
    </div>
  );
};

export default NavItem;