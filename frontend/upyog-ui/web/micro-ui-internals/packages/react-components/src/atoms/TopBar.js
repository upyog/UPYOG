import React, { useEffect, useState } from "react";
import { Dropdown } from "@nudmcdgnpm/digit-ui-react-components";
import PropTypes from "prop-types";
import Hamburger from "./Hamburger";
import { NotificationBell } from "./svgindex";
import { useLocation } from "react-router-dom";
import BackButton from "./BackButton";

const TopBar = ({
  img,
  isMobile,
  logoUrl,
  onLogout,
  toggleSidebar,
  ulb,
  userDetails,
  notificationCount,
  notificationCountLoaded,
  cityOfCitizenShownBesideLogo,
  onNotificationIconClick,
  hideNotificationIconOnSomeUrlsWhenNotLoggedIn,
  changeLanguage,
  changeCity,
  isSidebarCollapsed,
  userOptions,
  profilePic,
  TextToImg,
  handleUserDropdownSelection,
  isEmployee,
}) => {
  const { pathname } = useLocation();
  const cityName = Digit.SessionStorage.get("CITIZEN.COMMON.HOME.CITY")?.city.name;
  const isEmployeeUser =
    isEmployee ||
    Digit.UserService?.getType?.() === "employee" ||
    window.location.href.includes("/employee") ||
    pathname.includes("/employee");

  const userName = userDetails?.info?.name || userDetails?.info?.userInfo?.name || "Employee";
  const userInitials = userName
    .split(" ")
    .filter(Boolean)
    .map((word) => word[0])
    .join("")
    .slice(0, 2)
    .toUpperCase();

  return (
    <div className={`navbar ${isSidebarCollapsed ? "navbar-expanded" : ""}`}>
      <div className="center-container back-wrapper">
        <div className="hambuger-back-wrapper">
          <Hamburger handleClick={toggleSidebar} />
          {isEmployeeUser ? (
            changeCity ? (
              <div className="location-button nav-city-button-wrapper">
                <img src="/upyog-ui/images/location.svg" alt="location" />
                {changeCity}
              </div>
            ) : (
              <div className="location-button">
                <img src="/upyog-ui/images/location.svg" alt="location" />
                <span>{Digit.SessionStorage.get("Employee.tenantId") || cityName || "City"}</span>
              </div>
            )
          ) : (
            <div className="location-button">
              <img src="/upyog-ui/images/location.svg" alt="location" />
              <span>{cityName || "City A"}</span>
            </div>
          )}
        </div>


        <div className="RightMostTopBarOptions">
          {!hideNotificationIconOnSomeUrlsWhenNotLoggedIn ? changeLanguage : null}
          {!hideNotificationIconOnSomeUrlsWhenNotLoggedIn && !isEmployeeUser ? (
            <div className="EventNotificationWrapper" onClick={onNotificationIconClick}>
              {notificationCountLoaded && notificationCount ? (
                <span>
                  <p>{notificationCount}</p>
                </span>
              ) : null}
              <img src="/upyog-ui/images/bell.svg" alt="notification bell" />
            </div>
          ) : null}
          <div className="userDropdown">
            {userDetails?.access_token && (
              <Dropdown
                option={userOptions}
                optionKey={"name"}
                select={(option) => {
                  if (option?.func) {
                    option.func();
                  } else if (handleUserDropdownSelection) {
                    handleUserDropdownSelection(option);
                  } else if (onLogout) {
                    onLogout();
                  }
                }}
                showArrow={false}
                freeze={true}
                customSelector={
                  <div className="topbar-user-btn">
                    {profilePic ? (
                      <img src={profilePic} className="topbar-user-avatar" alt="Avatar" />
                    ) : (
                      <span className="topbar-user-badge">{userInitials || "E"}</span>
                    )}
                    <span className="topbar-user-name" title={userName}>
                      {userName}
                    </span>
                    <svg className="topbar-user-chevron" width="12" height="12" viewBox="0 0 24 24" fill="currentColor">
                      <path d="M7 10l5 5 5-5z" />
                    </svg>
                  </div>
                }
              />
            )}
          </div>
        </div>

      </div>
    </div>
  );
};

TopBar.propTypes = {
  img: PropTypes.string,
};

TopBar.defaultProps = {
  img: undefined,
};

export default TopBar;
