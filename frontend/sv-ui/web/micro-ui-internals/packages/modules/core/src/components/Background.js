import React from "react";

const Background = ({ children }) => {
  return <div className="banner banner-container" style={{"zIndex":"2"}}>{children}</div>;
};

export default Background;
