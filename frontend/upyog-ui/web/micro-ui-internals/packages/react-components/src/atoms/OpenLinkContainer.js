import React, { useState } from "react";
import PropTypes from "prop-types";
import { NotificationBell } from "./svgindex";

const OpenLinkContainer = ({ img,}) => {
  return (
    <div className="navbar">
      <div className="center-container">
        <img
          className="city"
          id="topbar-logo" 
          crossOrigin="anonymous"
          src={"./../../logo_preview.png"}
          alt="Ey-Seva"
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