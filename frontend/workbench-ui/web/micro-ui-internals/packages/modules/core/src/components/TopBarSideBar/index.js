import React, { useState } from "react";
import { EditPencilIcon, LogoutIcon } from "@upyog/workbench-ui-react-components";
import TopBar from "./TopBar";
import SideBar from "./SideBar";
import LogoutDialog from "../Dialog/LogoutDialog";
const TopBarSideBar = ({
  t,
  stateInfo,
  userDetails,
  cityDetails,
  mobileView,
  handleUserDropdownSelection,
  logoUrl,
  showSidebar = true,
  showLanguageChange,
}) => {
  const [isSidebarOpen, toggleSidebar] = useState(false);
  const navigate = Digit.Hooks.useCustomNavigate();
  const [showDialog, setShowDialog] = useState(false);
  const handleLogout = () => {
    toggleSidebar(false);
    setShowDialog(true);
  };
  const handleOnSubmit = () => {
    Digit.UserService.logout();
    setShowDialog(false);
  }
  const handleOnCancel = () => {
    setShowDialog(false);
  }
  const userProfile = () => {
    navigate(`/${window?.contextPath}/employee/user/profile`);
  };
  const userOptions = [
    { name: t("EDIT_PROFILE"), icon: <EditPencilIcon className="icon" />, func: userProfile },
    { name: t("CORE_COMMON_LOGOUT"), icon: <LogoutIcon className="icon" />, func: handleLogout },
  ];
  return (
    <>
      <TopBar
        t={t}
        stateInfo={stateInfo}
        toggleSidebar={toggleSidebar}
        isSidebarOpen={isSidebarOpen}
        handleLogout={handleLogout}
        userDetails={userDetails}
        cityDetails={cityDetails}
        mobileView={mobileView}
        userOptions={userOptions}
        handleUserDropdownSelection={handleUserDropdownSelection}
        logoUrl={logoUrl}
        showLanguageChange={showLanguageChange}
      />
      {showDialog && (
        <LogoutDialog onSelect={handleOnSubmit} onCancel={handleOnCancel} onDismiss={handleOnCancel}></LogoutDialog>
      )}
      {showSidebar && (
        <SideBar
          mobileView={mobileView}
          userDetails={userDetails}
        />
      )}
    </>
  );
};
export default TopBarSideBar;