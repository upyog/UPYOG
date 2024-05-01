import React from "react";

const Header = (props) => {
  return <header className="h1" style={props.styles ? {...props.styles, fontSize:"24px", fontFamily:"Open Sans"} : {fontSize : "32px", fontFamily:"Open Sans"}}>{props.children}</header>;
};

export default Header;
