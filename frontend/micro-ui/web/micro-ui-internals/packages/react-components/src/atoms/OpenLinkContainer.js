import React, { useState } from "react";
import PropTypes from "prop-types";
import { NotificationBell } from "./svgindex";

const OpenLinkContainer = ({ img,}) => {
  return (
    <div className="navbar" style={{backgroundColor:"white"}}>
      <div className="center-container" style={{backgroundColor:"white"}}>
        <img
          className="city"
          id="topbar-logo" 
          crossOrigin="anonymous"
          src={"https://www.mea.gov.in/images/black-emblem.png"}
          alt="mSeva"
        />
      </div>
    </div>
  );
};

OpenLinkContainer.propTypes = {
  img: PropTypes.string,
};

OpenLinkContainer.defaultProps = {
  img: undefined,
};

export default OpenLinkContainer;