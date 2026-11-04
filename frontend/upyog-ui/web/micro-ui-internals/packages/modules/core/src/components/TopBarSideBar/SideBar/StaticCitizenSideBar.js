import React, { useEffect, useState } from "react";
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
  CHBIcon
} from "@nudmcdgnpm/digit-ui-react-components";
import { Link, useLocation } from "react-router-dom";
import SideBarMenu from "../../../config/sidebar-menu";
import { useTranslation } from "react-i18next";

import LogoutDialog from "../../Dialog/LogoutDialog";
import ChangeCity from "../../ChangeCity";
import { APPLICATION_PATH } from "../../../pages/citizen/Home/EDCR/utils";

const defaultImage =
  "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAO4AAADUCAMAAACs0e/bAAAAM1BMVEXK0eL" +
  "/" +
  "/" +
  "/" +
  "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";
/* 
Feature :: Citizen Webview sidebar
*/
const Profile = ({ info, stateName, t, profilePhotoUrl, isSidebarCollapsed }) => {
  const username = isSidebarCollapsed
    ? info?.name?.split(' ').map(word => word[0]).join('').toUpperCase()
    : info?.name;

  return (
    <div className="profile-section">
      <div className="imageloader imageloader-loaded">
        <img className="img-responsive img-circle img-Profile" src={profilePhotoUrl ? profilePhotoUrl : defaultImage} />
      </div>
      <div id="profile-name" className="label-container name-Profile">
        <div className="label-text"> {username} </div>
      </div>
      {!isSidebarCollapsed && <div id="profile-location" className="label-container loc-Profile">
        <div className="label-text"> {info?.mobileNumber} </div>
      </div>}
      {!isSidebarCollapsed && info?.emailId && (
        <div id="profile-emailid" className="label-container loc-Profile">
          <div className="label-text"> {info.emailId} </div>
        </div>
      )}
      <div className="profile-divider"></div>
      {false && window.location.href.includes("/employee") &&
        !window.location.href.includes("/employee/user/login") &&
        !window.location.href.includes("employee/user/language-selection") && <ChangeCity t={t} mobileView={true} />}
    </div>
  )
};

const BadgeIcon = ({ bg = "#eef2ff", color = "#4f46e5", children }) => (
  <span
    className="icon citizen-sidebar-icon-badge"
    style={{ "--cs-icon-bg": bg, "--cs-icon-color": color }}
  >
    {children}
  </span>
);

const CitizenSidebarIcons = {
  // 1. Home -> House with roof & door
  home: (
    <BadgeIcon bg="#EEF2FF" color="#4F46E5">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M3 10.5L12 3l9 7.5" />
        <path d="M5 9.5V19a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V9.5" />
        <path d="M9 21v-6a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v6" />
      </svg>
    </BadgeIcon>
  ),
  // 2. Advertisement -> Megaphone / Bullhorn with sound waves
  ads: (
    <BadgeIcon bg="#FFF1F2" color="#E11D48">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M3 11l14-5v12L3 13v-2z" />
        <path d="M7 14l1.5 5a1.5 1.5 0 0 0 2.9-.8L10 13.5" />
        <path d="M20 8c1.3 1.3 1.3 4.7 0 6" />
      </svg>
    </BadgeIcon>
  ),
  // 3. Bills accounting -> Calculator with buttons & display
  bills: (
    <BadgeIcon bg="#ECFDF5" color="#059669">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <rect x="4" y="2" width="16" height="20" rx="2" />
        <line x1="8" y1="6" x2="16" y2="6" />
        <circle cx="8.5" cy="11" r="1" fill="currentColor" />
        <circle cx="12" cy="11" r="1" fill="currentColor" />
        <circle cx="15.5" cy="11" r="1" fill="currentColor" />
        <circle cx="8.5" cy="15" r="1" fill="currentColor" />
        <circle cx="12" cy="15" r="1" fill="currentColor" />
        <line x1="15.5" y1="14" x2="15.5" y2="18" />
        <circle cx="8.5" cy="18.5" r="1" fill="currentColor" />
        <circle cx="12" cy="18.5" r="1" fill="currentColor" />
      </svg>
    </BadgeIcon>
  ),
  // 4. Birth -> Baby stroller / carriage
  birth: (
    <BadgeIcon bg="#FDF2F8" color="#DB2777">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M4 12h15a7 7 0 0 1-14 0z" />
        <path d="M19 12V5a7 7 0 0 0-7 7" />
        <path d="M2 7h3l2 5" />
        <circle cx="8" cy="20" r="2" />
        <circle cx="16" cy="20" r="2" />
      </svg>
    </BadgeIcon>
  ),
  // 5. Community Hall Booking -> Calendar with star event
  chb: (
    <BadgeIcon bg="#FAF5FF" color="#9333EA">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <rect x="3" y="4" width="18" height="17" rx="2" />
        <line x1="16" y1="2" x2="16" y2="6" />
        <line x1="8" y1="2" x2="8" y2="6" />
        <line x1="3" y1="9" x2="21" y2="9" />
        <polygon points="12 12 13.3 14.6 16 15 14 16.9 14.5 19.5 12 18.2 9.5 19.5 10 16.9 8 15 10.7 14.6 12 12" />
      </svg>
    </BadgeIcon>
  ),
  // 6. Construction and Demolition -> Tower crane & hook lifting block
  cnd: (
    <BadgeIcon bg="#FFFBEB" color="#D97706">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M3 21h10" />
        <path d="M6 21V4l14 3h-14" />
        <path d="M6 4L3 7" />
        <line x1="17" y1="7" x2="17" y2="13" />
        <rect x="14" y="13" width="6" height="5" rx="1" />
      </svg>
    </BadgeIcon>
  ),
  // 7. Death -> Memorial candle with flame
  death: (
    <BadgeIcon bg="#F1F5F9" color="#475569">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M12 2c1.8 2 2.8 3.6 2.8 5.2a2.8 2.8 0 1 1-5.6 0C9.2 5.6 10.2 4 12 2z" />
        <rect x="9" y="10" width="6" height="11" rx="1" />
        <line x1="5" y1="21" x2="19" y2="21" />
      </svg>
    </BadgeIcon>
  ),
  // 8. Estate -> Property Key
  est: (
    <BadgeIcon bg="#EFF6FF" color="#1D4ED8">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <circle cx="7.5" cy="15.5" r="4.5" />
        <path d="M10.7 12.3L21 2" />
        <path d="M17 6l3 3" />
        <path d="M14 9l2.5 2.5" />
      </svg>
    </BadgeIcon>
  ),
  // 9. Ewaste -> Electric plug with cord
  ew: (
    <BadgeIcon bg="#F0FDF4" color="#16A34A">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M9 2v5" />
        <path d="M15 2v5" />
        <path d="M6 7h12v4a6 6 0 0 1-12 0V7z" />
        <path d="M12 17v5" />
      </svg>
    </BadgeIcon>
  ),
  // 10. Fire NOC -> Fire Flame
  firenoc: (
    <BadgeIcon bg="#FEF2F2" color="#DC2626">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M8.5 14.5A2.5 2.5 0 0 0 11 12c0-1.38-.5-2-1-3-1.072-2.143-.224-4.054 2-6 .5 2.5 2 4.9 4 6.5 2 1.6 3 3.5 3 5.5a7 7 0 1 1-14 0c0-1.153.433-2.294 1-3a2.5 2.5 0 0 0 2.5 2.5z" />
      </svg>
    </BadgeIcon>
  ),
  // 11. Desludging Service -> Tanker truck
  fsm: (
    <BadgeIcon bg="#ECFEFF" color="#0891B2">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <rect x="1" y="7" width="13" height="8" rx="4" />
        <path d="M14 10h4l3 3v3h-7v-6z" />
        <circle cx="5.5" cy="18" r="2" />
        <circle cx="17.5" cy="18" r="2" />
      </svg>
    </BadgeIcon>
  ),
  // 12. Garbage Collection -> Trash bin with lid
  gc: (
    <BadgeIcon bg="#F0FDFA" color="#0D9488">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <polyline points="3 6 5 6 21 6" />
        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
        <line x1="10" y1="11" x2="10" y2="17" />
        <line x1="14" y1="11" x2="14" y2="17" />
      </svg>
    </BadgeIcon>
  ),
  // 13. GIS -> Globe with latitude/longitude grid
  gis: (
    <BadgeIcon bg="#EFF6FF" color="#2563EB">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <circle cx="12" cy="12" r="9" />
        <line x1="3" y1="12" x2="21" y2="12" />
        <path d="M12 3a14.5 14.5 0 0 1 0 18 14.5 14.5 0 0 1 0-18" />
      </svg>
    </BadgeIcon>
  ),
  // 14. Miscellaneous Collections -> Stack of Coins
  mcollect: (
    <BadgeIcon bg="#FEFCE8" color="#CA8A04">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <ellipse cx="12" cy="6" rx="7" ry="3" />
        <path d="M5 6v6c0 1.66 3.13 3 7 3s7-1.34 7-3V6" />
        <path d="M5 12v6c0 1.66 3.13 3 7 3s7-1.34 7-3v-6" />
      </svg>
    </BadgeIcon>
  ),
  // 15. No Due Certificate -> Award Rosette Ribbon with Checkmark
  ndc: (
    <BadgeIcon bg="#ECFDF5" color="#10B981">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <circle cx="12" cy="9" r="6" />
        <polyline points="9.5 9 11.2 10.8 14.8 7.2" />
        <path d="M8.5 14L7 22l5-2.5L17 22l-1.5-8" />
      </svg>
    </BadgeIcon>
  ),
  // 16. Building Plan Approval -> Architect drafting compass & ruler
  obps: (
    <BadgeIcon bg="#FFF7ED" color="#EA580C">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <circle cx="12" cy="5" r="2" />
        <path d="M10.5 6.8L5 21" />
        <path d="M13.5 6.8L19 21" />
        <path d="M7 15h10" />
      </svg>
    </BadgeIcon>
  ),
  // 17. Public Grievance Redressal -> Support Headset with microphone
  pgr: (
    <BadgeIcon bg="#FFF1F2" color="#F43F5E">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M4 13v-1a8 8 0 0 1 16 0v1" />
        <rect x="2" y="12" width="4" height="6" rx="2" />
        <rect x="18" y="12" width="4" height="6" rx="2" />
        <path d="M20 18v1a3 3 0 0 1-3 3h-4" />
      </svg>
    </BadgeIcon>
  ),
  // 18. PGR-AI -> AI Robot with antenna
  pgrai: (
    <BadgeIcon bg="#FDF4FF" color="#C026D3">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <rect x="4" y="8" width="16" height="12" rx="3" />
        <line x1="12" y1="3" x2="12" y2="8" />
        <circle cx="12" cy="3" r="1" />
        <circle cx="9" cy="13" r="1.2" fill="currentColor" />
        <circle cx="15" cy="13" r="1.2" fill="currentColor" />
        <line x1="9" y1="17" x2="15" y2="17" />
      </svg>
    </BadgeIcon>
  ),
  // 19. Property Tax Assessment and Payment -> Indian Rupee (₹) tax badge
  pt: (
    <BadgeIcon bg="#F5F3FF" color="#7C3AED">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M7 5h10" />
        <path d="M7 9.5h10" />
        <path d="M9 5c3 0 5 1.8 5 4.5S12 14 9 14H7l7.5 6" />
      </svg>
    </BadgeIcon>
  ),
  // 20. Pet Registration -> Animal Paw Print
  ptr: (
    <BadgeIcon bg="#FEF3C7" color="#B45309">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <circle cx="7.5" cy="7.5" r="1.8" />
        <circle cx="12" cy="5.5" r="1.8" />
        <circle cx="16.5" cy="7.5" r="1.8" />
        <circle cx="19" cy="12" r="1.6" />
        <path d="M12 11.5c-3 0-5.5 2.5-5.5 5.2 0 1.6 1.2 2.8 2.8 2.8 1 0 1.8-.4 2.7-.4s1.7.4 2.7.4c1.6 0 2.8-1.2 2.8-2.8 0-2.7-2.5-5.2-5.5-5.2z" />
      </svg>
    </BadgeIcon>
  ),
  // 21. Street Vending -> Street Vendor Cart with Umbrella
  sv: (
    <BadgeIcon bg="#CCFBF1" color="#0F766E">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M4 9a8 8 0 0 1 16 0Z" />
        <line x1="12" y1="9" x2="12" y2="14" />
        <rect x="5" y="14" width="14" height="4" rx="1" />
        <circle cx="8" cy="20" r="1.8" />
        <circle cx="16" cy="20" r="1.8" />
      </svg>
    </BadgeIcon>
  ),
  // 22. Trade License Issuance and Payment -> Storefront Shop Awning
  tl: (
    <BadgeIcon bg="#EDE9FE" color="#6D28D9">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M3 9l2-5h14l2 5" />
        <path d="M3 9c0 1.7 1.3 3 3 3s3-1.3 3-3c0 1.7 1.3 3 3 3s3-1.3 3-3c0 1.7 1.3 3 3 3s3-1.3 3-3" />
        <path d="M5 12v8h14v-8" />
        <path d="M10 16h4v4h-4z" />
      </svg>
    </BadgeIcon>
  ),
  // 23. Verification Search -> Magnifying Glass
  verification: (
    <BadgeIcon bg="#E0F2FE" color="#0284C7">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <circle cx="10.5" cy="10.5" r="6.5" />
        <line x1="21" y1="21" x2="15.2" y2="15.2" />
        <polyline points="8 10.5 10 12.5 13.5 8.8" />
      </svg>
    </BadgeIcon>
  ),
  // 24. Water and Sewerage Connection Management -> Water Drop with wave
  ws: (
    <BadgeIcon bg="#E0F2FE" color="#0369A1">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M12 2.69l5.66 5.66a8 8 0 1 1-11.31 0z" />
        <path d="M8.5 15.5c1.2 1 2.8 1 4 0s2.8-1 3.5 0" />
      </svg>
    </BadgeIcon>
  ),
  // 25. Request Service -> Service Bell (Concierge Bell)
  wt: (
    <BadgeIcon bg="#E0E7FF" color="#4338CA">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M10 4h4" />
        <line x1="12" y1="4" x2="12" y2="7" />
        <path d="M4 17a8 8 0 0 1 16 0Z" />
        <line x1="2" y1="20" x2="22" y2="20" />
      </svg>
    </BadgeIcon>
  ),
  // 26. Edit Profile -> Pencil writing
  profile: (
    <BadgeIcon bg="#F3E8FF" color="#7E22CE">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M12 20h9" />
        <path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z" />
      </svg>
    </BadgeIcon>
  ),
  // 27. HELPLINE -> Ringing Phone
  helpline: (
    <BadgeIcon bg="#FFE4E6" color="#BE123C">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z" />
      </svg>
    </BadgeIcon>
  ),
};

const IconsObject = {
  CommonPTIcon: CitizenSidebarIcons.pt,
  OBPSIcon: CitizenSidebarIcons.obps,
  propertyIcon: CitizenSidebarIcons.pt,
  TLIcon: CitizenSidebarIcons.tl,
  PGRIcon: CitizenSidebarIcons.pgr,
  FSMIcon: CitizenSidebarIcons.fsm,
  WSIcon: CitizenSidebarIcons.ws,
  MCollectIcon: CitizenSidebarIcons.mcollect,
  CHBIcon: CitizenSidebarIcons.chb,
  BillsIcon: CitizenSidebarIcons.bills,
  BirthIcon: CitizenSidebarIcons.birth,
  DeathIcon: CitizenSidebarIcons.death,
  FirenocIcon: CitizenSidebarIcons.firenoc,
  HomeIcon: CitizenSidebarIcons.home,
  EditPencilIcon: CitizenSidebarIcons.profile,
  LogoutIcon: <LogoutIcon className="icon" />,
  Phone: CitizenSidebarIcons.helpline,
  LoginIcon: <LoginIcon className="icon" />,
};

const resolveSidebarItemIcon = (item) => {
  const link = (item?.link || item?.sidebarURL || "").toLowerCase();
  const element = (item?.element || "").toUpperCase();
  const moduleKey = (item?.moduleKey || "").toLowerCase();
  const iconKey = typeof item?.icon === "string" ? item.icon : item?.icon?.type?.name || "";
  const labelText = typeof item?.text === "string" ? item.text.toLowerCase() : "";

  if (element === "HOME" || iconKey === "HomeIcon" || link.endsWith("/citizen") || link.endsWith("/citizen/")) return CitizenSidebarIcons.home;
  if (element === "PROFILE" || iconKey === "EditPencilIcon" || labelText.includes("edit profile")) return CitizenSidebarIcons.profile;
  if (element === "HELPLINE" || iconKey === "Phone" || item?.id === "help-line") return CitizenSidebarIcons.helpline;

  if (link.includes("ads-home") || moduleKey.includes("ads") || labelText.includes("advertisement")) return CitizenSidebarIcons.ads;
  if (link.includes("bills-home") || moduleKey.includes("bill") || labelText.includes("bills accounting")) return CitizenSidebarIcons.bills;
  if (link.includes("birth-citizen") || moduleKey.includes("birth") || iconKey === "BirthIcon" || labelText === "birth") return CitizenSidebarIcons.birth;
  if (link.includes("chb-home") || moduleKey.includes("chb") || iconKey === "CHBIcon" || labelText.includes("community hall")) return CitizenSidebarIcons.chb;
  if (link.includes("cnd-home") || moduleKey.includes("cnd") || labelText.includes("construction and demolition")) return CitizenSidebarIcons.cnd;
  if (link.includes("death-citizen") || moduleKey.includes("death") || iconKey === "DeathIcon" || labelText === "death") return CitizenSidebarIcons.death;
  if (link.includes("est-home") || moduleKey.includes("est") || labelText.includes("estate")) return CitizenSidebarIcons.est;
  if (link.includes("ew-home") || moduleKey.includes("ew") || labelText.includes("ewaste") || labelText.includes("e-waste")) return CitizenSidebarIcons.ew;
  if (link.includes("firenoc-home") || moduleKey.includes("firenoc") || iconKey === "FirenocIcon" || labelText.includes("fire noc")) return CitizenSidebarIcons.firenoc;
  if (link.includes("fsm-home") || moduleKey.includes("fsm") || iconKey === "FSMIcon" || labelText.includes("desludging")) return CitizenSidebarIcons.fsm;
  if (link.includes("gc-home") || moduleKey.includes("gc") || labelText.includes("garbage collection")) return CitizenSidebarIcons.gc;
  if (link.includes("gis-home") || moduleKey.includes("gis") || labelText === "gis") return CitizenSidebarIcons.gis;
  if (link.includes("mcollect-home") || moduleKey.includes("mcollect") || iconKey === "MCollectIcon" || labelText.includes("miscellaneous")) return CitizenSidebarIcons.mcollect;
  if (link.includes("ndc-home") || moduleKey.includes("ndc") || labelText.includes("no due certificate")) return CitizenSidebarIcons.ndc;
  if (link.includes("obps-home") || moduleKey.includes("obps") || iconKey === "OBPSIcon" || labelText.includes("building plan")) return CitizenSidebarIcons.obps;
  if (link.includes("pgrai-home") || moduleKey.includes("pgrai") || labelText.includes("pgr-ai")) return CitizenSidebarIcons.pgrai;
  if (link.includes("pgr-home") || moduleKey === "pgr" || iconKey === "PGRIcon" || labelText.includes("grievance")) return CitizenSidebarIcons.pgr;
  if (link.includes("pt-home") || moduleKey === "pt" || iconKey === "CommonPTIcon" || labelText.includes("property tax")) return CitizenSidebarIcons.pt;
  if (link.includes("ptr-home") || moduleKey.includes("ptr") || labelText.includes("pet registration")) return CitizenSidebarIcons.ptr;
  if (link.includes("sv-home") || moduleKey.includes("sv") || labelText.includes("street vending")) return CitizenSidebarIcons.sv;
  if (link.includes("tl-home") || moduleKey === "tl" || iconKey === "TLIcon" || labelText.includes("trade license")) return CitizenSidebarIcons.tl;
  if (link.includes("verificationsearch-home") || moduleKey.includes("verification") || labelText.includes("verification search")) return CitizenSidebarIcons.verification;
  if (link.includes("ws-home") || moduleKey === "ws" || iconKey === "WSIcon" || labelText.includes("water and sewerage")) return CitizenSidebarIcons.ws;
  if (link.includes("wt-home") || moduleKey === "wt" || labelText.includes("request service")) return CitizenSidebarIcons.wt;

  if (iconKey && IconsObject[iconKey]) return IconsObject[iconKey];
  return CitizenSidebarIcons.bills;
};
const StaticCitizenSideBar = ({ linkData, islinkDataLoading, isSidebarCollapsed, closeMobileSidebar }) => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const location = useLocation();
  const { pathname } = location;
  const { data: storeData, isFetched } = Digit.Hooks.useStore.getInitData();
  const { stateInfo } = storeData || {};
  const user = Digit.UserService.getUser().token !== null ? Digit.UserService.getUser() : {
    access_token: "asdf", info: {
      tanentID: "pg",
      uuid: "asdf",
      name: "Shubham Singh",
      "mobileNumber": 1231231231,
      emailId: "ss@gmail.com"
    }
  };
  let isMobile = window.Digit.Utils.browser.isMobile();

  const [isEmployee, setisEmployee] = useState(false);
  const [isSidebarOpen, toggleSidebar] = useState(false);
  const [showDialog, setShowDialog] = useState(false);
  const tenantId = Digit.ULBService.getCitizenCurrentTenant();
  const [profilePhotoUrl, setProfilePhotoUrl] = useState(null);
  useEffect(() => {
    const fetchPhoto = async () => {
      const usersResponse = await Digit.UserService.userSearch(user?.info?.tenantId, { uuid: [user?.info?.uuid || user?.user?.[0]?.uuid] }, {});
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
  const handleLogout = () => {
    toggleSidebar(false);
    if (closeMobileSidebar) closeMobileSidebar();
    setShowDialog(true);
  };
  const handleOnSubmit = () => {
    Digit.UserService.logout();
    setShowDialog(false);
  };
  const handleOnCancel = () => {
    setShowDialog(false);
  };

  if (islinkDataLoading || !isFetched) {
    return <Loader />;
  }

  const handleNavClick = () => {
    if (closeMobileSidebar) closeMobileSidebar();
  };

  const redirectToLoginPage = () => {
    if (closeMobileSidebar) closeMobileSidebar();
    navigate(`${APPLICATION_PATH}/citizen/login`);
  };
  // Function to redirect the user to the EDCR scrutiny page
  const redirectToScrutinyPage = () => {
    if (closeMobileSidebar) closeMobileSidebar();
    navigate(`${APPLICATION_PATH}/citizen/core/edcr/scrutiny`);
  };
  const showProfilePage = () => {
    if (closeMobileSidebar) closeMobileSidebar();
    navigate(`${APPLICATION_PATH}/citizen/user/profile`);
  };
  //const tenantId = Digit.ULBService.getCitizenCurrentTenant();
  const filteredTenantContact = storeData?.tenants.filter((e) => e.code === tenantId)[0]?.contactNumber || storeData?.tenants[0]?.contactNumber;

  let menuItems = [...SideBarMenu(t, showProfilePage, redirectToLoginPage, redirectToScrutinyPage, isEmployee, storeData, tenantId)];

  menuItems = menuItems.filter((item) => item.element !== "LANGUAGE");

  const MenuItem = ({ item }) => {
    const leftIcon = resolveSidebarItemIcon(item);
    let itemComponent;
    if (item.type === "component") {
      itemComponent = item.action;
    } else {
      itemComponent = item.text;
    }
    const Item = () => (
      <span className="menu-item" {...item.populators} onClick={(e) => {
        if (item.populators?.onClick) item.populators.onClick(e);
        handleNavClick();
      }}>
        {leftIcon}
        <div className="menu-label">{itemComponent}</div>
      </span>
    );
    if (item.type === "external-link") {
      return (
        <a href={item.link} onClick={handleNavClick}>
          <Item />
        </a>
      );
    }
    if (item.type === "link") {
      return (
        <Link to={item?.link.replace("/digit-ui/", "/upyog-ui/")} onClick={handleNavClick}>
          <Item />
        </Link>
      );
    }

    return <Item />;
  };
  let profileItem;

  if (true && user && user.access_token) {
    profileItem = <Profile info={user?.info} stateName={stateInfo?.name || "City A"} t={t} profilePhotoUrl={profilePhotoUrl} isSidebarCollapsed={isSidebarCollapsed} />;
    menuItems = menuItems.filter((item) => item?.id !== "login-btn" && item?.id !== "help-line");
    menuItems = [
      ...menuItems,
      {
        text: t("EDIT_PROFILE"),
        element: "PROFILE",
        icon: "EditPencilIcon",
        populators: {
          onClick: showProfilePage,
        },
      },
      {
        text: (
          <React.Fragment>
            <span className="sidebar-helpline-title">{t("CS_COMMON_HELPLINE")}</span>
            <div className="telephone sidebar-helpline-telephone">
              <div className="link">
                <a href={`tel:${filteredTenantContact}`}>{filteredTenantContact}</a>
              </div>
            </div>
          </React.Fragment>
        ),
        element: "Helpline",
        icon: "Phone",
      },
    ];
  }
  const dynamicModuleItems = [];
  Object.keys(linkData || {}).forEach((key) => {
    if (linkData[key]?.[0]?.sidebar === "digit-ui-links" || linkData[key]?.[0]?.sidebar === "upyog-ui-links") {
      dynamicModuleItems.push({
        type: linkData[key][0]?.sidebarURL?.includes("digit-ui") || linkData[key][0]?.sidebarURL?.includes("upyog-ui") ? "link" : "external-link",
        text: t(`ACTION_TEST_${Digit.Utils.locale.getTransformedLocale(key)}`),
        links: linkData[key],
        icon: linkData[key][0]?.leftIcon,
        link: linkData[key][0]?.sidebarURL,
        moduleKey: key,
      });
    }
  });

  dynamicModuleItems.sort((a, b) =>
    String(a?.text || "").localeCompare(String(b?.text || ""), undefined, { sensitivity: "base" })
  );

  if (dynamicModuleItems.length > 0) {
    menuItems.splice(1, 0, ...dynamicModuleItems);
  }

  return (
    <React.Fragment>
      <div className="static-citizen-sidebar-inner">
        <div className="sidebar-logo-header">
          <div className="logo-design">
            {isSidebarCollapsed ? <img src="/upyog-ui/images/logo-mobile.png" alt="UPYOG Logo" /> : <img src="/upyog-ui/images/logo-secondary.png" alt="UPYOG Logo" />}
          </div>
          {isMobile && (
            <button
              type="button"
              className="sidebar-mobile-close-btn"
              onClick={closeMobileSidebar}
              aria-label="Close menu"
            >
              ✕
            </button>
          )}
        </div>
        {profileItem}
        <div className="drawer-desktop">
          {menuItems?.map((item, index) => (
            <div className={`sidebar-list ${pathname === item?.link || pathname === item?.sidebarURL ? "active" : ""} ${item?.element === "Helpline" || item?.id === "help-line" ? "sidebar-list-helpline" : ""}`} key={index}>
              <MenuItem item={item} />
            </div>
          ))}
        </div>
        <div className="version-badge">
          <span>v5.1</span>
        </div>
        <div className="citizen-logout">
          <button onClick={handleLogout}>
            <span>{IconsObject.LogoutIcon}</span><span>{t("CORE_COMMON_LOGOUT")}</span>
          </button>
        </div>
        {showDialog && <LogoutDialog onSelect={handleOnSubmit} onCancel={handleOnCancel} onDismiss={handleOnCancel}></LogoutDialog>}
      </div>
    </React.Fragment>
  );
};

export default StaticCitizenSideBar;
