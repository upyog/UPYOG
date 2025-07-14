import React from "react";

const Body = (props) => {
  const bodyStyle = {
    width: "100%",
    height: "100%",
    position: "relative",
    backgroundColor: "#ebf1fb",
    backgroundImage: "url('https://jaljeevanmission.gov.in//themes/edutheme/images/header-bg.png')",
    backgroundSize: "cover",
    backgroundRepeat: "no-repeat",
    backgroundPosition: "center"
  };

  return (
    <div className="body-container">
      <div style={bodyStyle}>
        {props.children}
      </div>
    </div>
  );
};


export default Body;
