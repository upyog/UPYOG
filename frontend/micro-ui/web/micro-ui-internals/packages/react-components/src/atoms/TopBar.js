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
    <div className="navbar" style={{background:"#fef5e7",color:"white"}}>
      <style>
        {
          `
          .navbar {
            background-color: #fef5e7;
          }
          .navbar img {
            display: inline-block;
            height: auto;
            max-width: 40px;
            min-width: 40px;
        }
        .RightMostTopBarOptions .select-wrap svg{
          fill:white
        }
          `
        }
      </style>
      <div className="center-container back-wrapper" style={{display:"flex",paddingRight:"2rem",paddingLeft:"2rem",justifyContent:"space-between",backgroundColor:"#fef5e7"}}>
        <div className="hambuger-back-wrapper" style={{display:"flex", alignItems:"center"}}>
          {window.innerWidth <= 660  && <Hamburger handleClick={toggleSidebar} />}
          <div style={{display:"flex",padding:"5px",justifyContent:"center"}}>
          <div>
      <img className="city" src="https://i.postimg.cc/gc4FYkqX/977a9096-3548-4980-aae8-45a6e4d61263-removalai-preview.png" alt="City Logo"  style={{width:"40px"}}/>
      </div>
      <div>
    <span style={{ display: "flex", alignItems: "center", justifyContent: "space-between", width: "100%",letterSpacing:"1px" }}>
      <span style={{fontWeight:"bold", display:"flex", flexDirection:"column", marginLeft:"20px", color:"#2394d4", fontSize:isMobile?"16px":"20px"}} className="logoText">
        State Wide Attention on Grievances by Application of Technology
        <span style={{fontWeight:"bold", color:"black",  fontSize:isMobile?"16px":"20px",display:"flex",flexDirection:"column"}} className="logoTextSubline"> Government of Gujrat</span>
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
          src={"https://i.postimg.cc/gc4FYkqX/977a9096-3548-4980-aae8-45a6e4d61263-removalai-preview.png"}
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
