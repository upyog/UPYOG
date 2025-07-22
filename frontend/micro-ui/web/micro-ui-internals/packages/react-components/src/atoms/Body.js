import React from "react";

const Body = (props) => {
  const bodyStyle = {
    width: "100%",
    height: "100%",
    position: "relative",
    backgroundColor: "#ebf1fb",
    backgroundImage: "url('https://jaljeevanmission.gov.in//themes/edutheme/images/header-bg.png')",
    backgroundColor: "rgb(235, 241, 251)",
    backgroundSize: "cover",
    backgroundRepeat: "no-repeat",
    backgroundPosition: "center"
  };

  return (
    <div className="body-container" style={{backgroundColor:"#ebf1fb"}}>
      <div style={bodyStyle}>
        {props.children}
      </div>
    </div>
  );
};


export default Body;
