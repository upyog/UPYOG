import React from "react";

const CardSubHeader = (props) => {
  const user_type = Digit.SessionStorage.get("user_type") === "employee" ? true : false;
  return (
    <header
      className={`${user_type ? "employee-card-sub-header" : "card-sub-header"} ${props?.className ? props?.className : ""}`}
      style={props.style}
    >
      {props?.headingLogo ? <img style={{height: "100px", marginLeft: "70px", marginBottom: "12px"}} className="state" src="https://mnptapp-terraform.s3.ap-south-1.amazonaws.com/images/trlogo2.png" />
: ''}
{props.children}
    </header>
  );
};

export default CardSubHeader;
