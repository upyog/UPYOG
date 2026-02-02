import React from "react";

const Header = (props) => {
  return <header className="h1" style={props.styles ? {...props.styles, fontSize:"20px", fontFamily:"Open Sans"} : {fontSize : "20px", fontFamily:"Open Sans"}}>{props.children}</header>;
};

export default Header;
