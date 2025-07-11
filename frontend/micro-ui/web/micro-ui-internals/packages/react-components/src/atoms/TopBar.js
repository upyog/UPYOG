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
  return (
    <div className="navbar" style={{background:"white",color:"white"}}>
      <style>
        {
          `
          .navbar {
            padding:0px;
            background-color: white !important;
          }
          .navbar img {
            display: inline-block;
            height: auto;
            max-width: 40px;
            min-width: 40px;
        }
        .RightMostTopBarOptions .select-wrap svg{
          fill:#162f6a
        }
          `
        }
      </style>
      <div className="center-container back-wrapper" style={{display:"flex",paddingRight:"2rem",paddingLeft:"2rem",justifyContent:"space-between",backgroundColor:"white"}}>
        <div className="hambuger-back-wrapper" style={{display:"flex", alignItems:"center"}}>
          {window.innerWidth <= 660  && <Hamburger handleClick={toggleSidebar} />}
          <div style={{display:"flex",padding:"5px",justifyContent:"center"}}>
          <div>
      <img className="city" src="https://i.postimg.cc/3RK7wnrX/Screenshot-2025-07-10-at-11-17-17-AM.png" alt="City Logo"  style={{width:"40px"}}/>
      </div>
      <div>
    <span style={{ display: "flex", alignItems: "center", justifyContent: "space-between", width: "100%",letterSpacing:"1px" }}>
      <span style={{fontWeight:"bold", display:"flex", flexDirection:"column", marginLeft:"20px", color:"#0e338a", fontSize:isMobile?"16px":"20px"}} className="logoText">
        Panchayati Raj & Drinking Water Department
        <span style={{fontWeight:"normal", color:"black",  fontSize:isMobile?"16px":"20px",display:"flex",flexDirection:"column"}} className="logoTextSubline"> Government of Odisha</span>
      </span>
      </span>
      </div>
      </div>

          {/* <h3 style={{alignItems:"center",display:"flex",color:"white"}}>{cityOfCitizenShownBesideLogo}</h3> */}
        </div>

        <div className="RightMostTopBarOptions" style={{alignItems:"center",display:"flex",color:"white"}}>
          {!hideNotificationIconOnSomeUrlsWhenNotLoggedIn ? changeLanguage : changeLanguage}
          {!hideNotificationIconOnSomeUrlsWhenNotLoggedIn ? (
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
          {/* <img
          className="city"
          id="topbar-logo" 
          src={"https://i.postimg.cc/3RK7wnrX/Screenshot-2025-07-10-at-11-17-17-AM.png"}
          alt="mSeva"
          style={{marginLeft:"10px",minWidth:"40px", height:"60px"}}
        /> */}
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
