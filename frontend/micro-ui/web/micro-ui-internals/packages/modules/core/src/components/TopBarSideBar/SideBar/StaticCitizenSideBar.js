import React, { useState } from "react";
import { Link, useLocation, useHistory } from "react-router-dom";
import { useTranslation } from "react-i18next";
import LogoutDialog from "../../Dialog/LogoutDialog";
import ChangeCity from "../../ChangeCity";
import SideBarMenu from "../../../config/sidebar-menu";
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
} from "@demodigit/digit-ui-react-components";

const defaultImage =
  "https://randomuser.me/api/portraits/men/32.jpg"; // fallback

/* --- Profile Card --- */
const Profile = ({ info, stateName, t }) => (
  <div
    className="sidebar-profile d-flex align-items-center gap-3"
    style={{
      textAlign: "center",
      padding: "20px 15px",
      background: "linear-gradient(135deg, #cce0f0, #b3d6ff)",
      borderRadius: "12px",
      margin: 15,
      boxShadow: "0 4px 15px rgba(0,0,0,0.1)",
      display:"flex"
    }}
  >
    <img
      src={info?.photo || defaultImage}
      alt="Profile"
      style={{
        borderRadius: "50%",
        border: "3px solid #00599f",
        width: "70px",
        height: "70px",
      }}
    />
    <div style={{width:"100%"}}>
      <h6 style={{ marginTop: 10, color: "#00599f", fontSize: "16px", fontWeight: 600 }}>
        {info?.name}
      </h6>
      <p style={{ color: "#666", fontSize: "14px", margin: "2px 0" }}>{info?.mobileNumber}</p>
      {info?.emailId && (
        <p style={{ color: "#666", fontSize: "13px", margin: 0 }}>{info?.emailId}</p>
      )}
    </div>
  </div>
);

const IconsObject = {
  CommonPTIcon: <PTIcon className="icon" />,
  OBPSIcon: <OBPSIcon className="icon" />,
  propertyIcon: <PropertyHouse className="icon" />,
  TLIcon: <CaseIcon className="icon" />,
  PGRIcon: <PGRIcon className="icon" />,
  FSMIcon: <FSMIcon className="icon" />,
  WSIcon: <WSICon className="icon" />,
  MCollectIcon: <MCollectIcon className="icon" />,
  BillsIcon: <CollectionIcon className="icon" />,
  BirthIcon: <BirthIcon className="icon" />,
  DeathIcon: <DeathIcon className="icon" />,
  FirenocIcon: <FirenocIcon className="icon" />,
  HomeIcon: <HomeIcon className="icon" />,
  EditPencilIcon: <EditPencilIcon className="icon" />,
  LogoutIcon: <LogoutIcon className="icon" />,
  Phone: <Phone className="icon" />,
  LoginIcon: <LoginIcon className="icon" />,
};

const SIDEBAR_WIDTH = 260;
const COLLAPSED_WIDTH = 0;

const StaticCitizenSideBar = ({ linkData, islinkDataLoading }) => {
  const { t } = useTranslation();
  const history = useHistory();
  const location = useLocation();
  const { pathname } = location;
  const { data: storeData, isFetched } = Digit.Hooks.useStore.getInitData();
  const { stateInfo } = storeData || {};
  const user = Digit.UserService.getUser();

  const [collapsed, setCollapsed] = useState(false);
  const [showDialog, setShowDialog] = useState(false);

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

  if (islinkDataLoading || !isFetched) {
    return <Loader />;
  }

  const redirectToLoginPage = () => history.push("/digit-ui/citizen/login");
  const showProfilePage = () => history.push("/digit-ui/citizen/user/profile");

  const tenantId = Digit.ULBService.getCitizenCurrentTenant();
  const filteredTenantContact =
    storeData?.tenants.find((e) => e.code === tenantId)?.contactNumber ||
    storeData?.tenants[0]?.contactNumber;

  let menuItems = [...SideBarMenu(t, showProfilePage, redirectToLoginPage, false, storeData, tenantId)];
  menuItems = menuItems.filter((item) => item.element !== "LANGUAGE");

  const MenuItem = ({ item }) => {
    const leftIconArray = item?.icon || item.icon?.type?.name;
    const leftIcon = leftIconArray ? IconsObject[leftIconArray] : IconsObject.BillsIcon;

    const Item = () => (
      <span
        className="menu-item"
        {...item.populators}
        style={{
          display: "flex",
          alignItems: "center",
          gap: "12px",
          padding: "10px 16px",
          borderRadius: "8px",
          margin: "4px 8px",
          cursor: "pointer",
          transition: "all 0.3s",
          color: "#000",
        }}
      >
        <div
          className="icon-wrapper"
          style={{
            width: "36px",
            height: "36px",
            borderRadius: "50%",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            background: "linear-gradient(135deg, #00599f, #00aaff)",
            color: "#fff",
          }}
        >
          {leftIcon}
        </div>
        <div className="menu-label" style={{ fontSize: "14px", fontWeight: 500 }}>
          {item.type === "component" ? item.action : item.text}
        </div>
      </span>
    );

    if (item.type === "external-link") {
      return <a href={item.link}><Item /></a>;
    }
    if (item.type === "link") {
      return <Link to={item.link}><Item /></Link>;
    }
    return <Item />;
  };

  // Profile + Extra Menu
  if (isFetched && user && user.access_token) {
    menuItems = menuItems.filter((item) => item.id !== "login-btn" && item.id !== "help-line");
    menuItems = [
      ...menuItems,
      {
        text: t("EDIT_PROFILE"),
        element: "PROFILE",
        icon: "EditPencilIcon",
        populators: { onClick: showProfilePage },
      },
      {
        text: t("CORE_COMMON_LOGOUT"),
        element: "LOGOUT",
        icon: "LogoutIcon",
        populators: { onClick: handleLogout },
      },
      {
        text: (
          <React.Fragment>
            {t("CS_COMMON_HELPLINE")}
            <div style={{ fontSize: "13px" }}>
              <a href={`tel:${filteredTenantContact}`}>{filteredTenantContact}</a>
            </div>
          </React.Fragment>
        ),
        element: "Helpline",
        icon: "Phone",
      },
    ];
  }

  /* Sidebar container style */
  const sidebarStyle = {
    width: collapsed ? COLLAPSED_WIDTH : SIDEBAR_WIDTH,
    transition: "width 0.3s ease",
    position: "fixed",
    top: "100px",
    bottom: "20px",
    left: 0,
    backgroundColor: "#fff",
    borderRadius: "0 12px 12px 0",
    boxShadow: "2px 0 8px rgba(0,0,0,0.1)",
    overflowY: "auto",
    zIndex: 1050,
    paddingBottom: "20px",
  };

  return (
    <React.Fragment>
      {/* Toggle button */}
      {/* <button
        onClick={() => setCollapsed(!collapsed)}
        style={{
          position: "fixed",
          top: "110px",
          left: collapsed ? "10px" : SIDEBAR_WIDTH + 10,
          background: "#fff",
          border: "none",
          fontSize: "1.5rem",
          cursor: "pointer",
          padding: "6px 10px",
          borderRadius: "8px",
          boxShadow: "0 2px 6px rgba(0,0,0,0.2)",
          transition: "left 0.3s ease",
          zIndex: 1100,
        }}
      >
        <i className="fas fa-bars"></i>
      </button> */}

      {/* Sidebar */}
      <div style={sidebarStyle}>
        {user?.info && <Profile info={user.info} stateName={stateInfo?.name} t={t} />}
        <div>
          {menuItems.map((item, idx) => (
            <div
              key={idx}
              className={`sidebar-list ${pathname === item.link ? "active" : ""}`}
            >
              <MenuItem item={item} />
            </div>
          ))}
        </div>
      </div>

      {showDialog && <LogoutDialog onSelect={handleOnSubmit} onCancel={handleOnCancel} />}
    </React.Fragment>
  );
};

export default StaticCitizenSideBar;
