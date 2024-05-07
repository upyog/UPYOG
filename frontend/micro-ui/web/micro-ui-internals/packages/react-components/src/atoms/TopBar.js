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
  //   if (pathname === "/digit-ui/citizen" || pathname === "/digit-ui/citizen/" || pathname === "/digit-ui/citizen/select-language") {
  //     return <Hamburger handleClick={toggleSidebar} />;
  //   } else {
  //     return <BackButton className="top-back-btn" />;
  //   }
  // };
  console.log("hambuger-back-wrapper==",cityOfCitizenShownBesideLogo,hideNotificationIconOnSomeUrlsWhenNotLoggedIn)
  return (
    <div className="navbar">
      <div className="center-container back-wrapper" style={{display:"flex",justifyContent:"space-between"}}>
        {/* marginRight:"2rem",marginLeft:"2rem", */}
        <div className="hambuger-back-wrapper" style={{display:"flex"}}>
          {window.innerWidth <= 660  && <Hamburger handleClick={toggleSidebar} />}
          <a style={{display: 'inline-flex'}} href={window.location.href.includes("citizen")?"/digit-ui/citizen":"/digit-ui/employee"}><img
            className="city"
            id="topbar-logo"
            src={"https://mnptapp-terraform.s3.ap-south-1.amazonaws.com/images/trlogo2.png" || "https://cdn.jsdelivr.net/npm/@egovernments/digit-ui-css@1.0.7/img/m_seva_white_logo.png"}
            // {"https://in-egov-assets.s3.ap-south-1.amazonaws.com/images/Upyog-logo.png" || "https://cdn.jsdelivr.net/npm/@egovernments/digit-ui-css@1.0.7/img/m_seva_white_logo.png"}
            alt="UPYOG"
          />
           <div className='header-txt' style={{marginLeft: 15, color: "#0f4f9e"}}>
              <span style={{fontSize: 16, fontWeight: 600}}>MUNICIPAL ADMINISTRATION,</span>
              <div style={{fontSize: 16, fontWeight: 600}}>HOUSING AND URBAN DEVELOPMENT</div>
              <div style={{fontSize: 14}}>GOVERNMENT OF MANIPUR</div>
            </div>
          {/* <span>Property Tax - Manipur</span> */}
          </a>
          <h3>{cityOfCitizenShownBesideLogo}</h3>
        </div>

        <div className="RightMostTopBarOptions">
          {/* {!hideNotificationIconOnSomeUrlsWhenNotLoggedIn ? changeLanguage : null} */}
          {!hideNotificationIconOnSomeUrlsWhenNotLoggedIn && window.location.href.includes("citizen") ? (
            <div className="EventNotificationWrapper" onClick={onNotificationIconClick}>
              {notificationCountLoaded && notificationCount ? (
                <span>
                  <p>{notificationCount}</p>
                </span>
              ) : null}
              <NotificationBell />
            </div>
          ) : null}
          <h3></h3>
          <img
          className="city"
          id="topbar-logo" 
          src={"https://mnptapp-terraform.s3.ap-south-1.amazonaws.com/images/logo2.png" || "https://cdn.jsdelivr.net/npm/@egovernments/digit-ui-css@1.0.7/img/m_seva_white_logo.png"}
          alt="mSeva"
          style={{marginLeft:"10px"}}
        />
        <div className='header-txt' style={{marginLeft: 15,color: "#0f4f9e"}}>
            <span style={{fontSize: 16, fontWeight: 600}}>MANIPUR MUNICIPALITY</span>
            <div style={{fontSize: 16, fontWeight: 600}}>PROPERTY TAX BOARD</div>
          <div style={{fontSize: 14}}>GOVERNMENT OF MANIPUR</div>
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
