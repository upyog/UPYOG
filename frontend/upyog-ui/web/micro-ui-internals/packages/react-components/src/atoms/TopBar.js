import React, { useEffect, useState } from "react";
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
}) => {
  const { pathname } = useLocation();

  // const showHaburgerorBackButton = () => {
  //   if (pathname === "/mycity-ui/citizen" || pathname === "/mycity-ui/citizen/" || pathname === "/mycity-ui/citizen/select-language") {
  //     return <Hamburger handleClick={toggleSidebar} />;
  //   } else {
  //     return <BackButton className="top-back-btn" />;
  //   }
  // };
  return (
    <React.Fragment>
      <style>{`
    .navbar {
      background: #FFFFFF !important;
      padding: 10px !important;
      color: #192771
    }
  `}
      </style>
      <div className="navbar" style={{ background: "#FFFFFF", boxShadow: "0px 2px 6px rgba(0, 0, 0, 0.2)" }}>
        <div className="center-container back-wrapper" style={{ display: "flex", marginRight: "2rem", marginLeft: "2rem", justifyContent: "space-between", alignItems: "center" }}>
          <div className="hambuger-back-wrapper" style={{ display: "flex", alignItems: "center" }}>
            {window.innerWidth <= 660 && <Hamburger handleClick={toggleSidebar} />}
            <a href={window.location.href.includes("citizen") ? "/mycity-ui/citizen" : "/mycity-ui/employee"}><img
              className="city"
              id="topbar-logo"
              src={"https://upload.wikimedia.org/wikipedia/commons/9/99/Seal_of_Uttarakhand.svg"}
              alt="UPYOG"
              style={{ minWidth: "40px", height: "40px" }}
            />
            </a>
            <strong><h3 style={{ color: "#000000", fontSize: "20px", marginLeft: "12px" }}>NagarSewa Portal<br /><p style={{ fontSize: "14px" }}>Government of Uttarakhand</p></h3></strong>
          </div>

          <div className="RightMostTopBarOptions">
            {!hideNotificationIconOnSomeUrlsWhenNotLoggedIn ? changeLanguage : null}
            {!hideNotificationIconOnSomeUrlsWhenNotLoggedIn ? (
              <div className="EventNotificationWrapper" onClick={onNotificationIconClick}>
                {notificationCountLoaded && notificationCount ? (
                  <span>
                    <p style={{color: "#FFFFFF"}}>{notificationCount}</p>
                  </span>
                ) : null}
                <NotificationBell />
              </div>
            ) : null}
            <h3></h3>
            {/* <img
            className="city"
            id="topbar-logo"
            src={"https://in-egov-assets.s3.ap-south-1.amazonaws.com/images/Upyog-logo.png" || "https://cdn.jsdelivr.net/npm/@egovernments/digit-ui-css@1.0.7/img/m_seva_white_logo.png"}
            alt="mSeva"
            style={{ marginLeft: "10px" }}
          /> */}
          </div>
        </div>
      </div>
    </React.Fragment>
  );
};

TopBar.propTypes = {
  img: PropTypes.string,
};

TopBar.defaultProps = {
  img: undefined,
};

export default TopBar;
