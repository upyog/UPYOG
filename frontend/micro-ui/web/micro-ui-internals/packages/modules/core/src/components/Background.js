import React from "react";

const Background = ({ children, backgroundClass }) => {
  return <div className="banner banner-container" style={{"zIndex":"2"}}>{children}</div>;
};

export default Background;
