import React from "react";
import EmployeeSideBar from "./EmployeeSideBar";

const SideBar = ({ mobileView, userDetails, modules }) => {
  if (!mobileView && userDetails?.access_token) return <EmployeeSideBar {...{ mobileView, userDetails, modules }} />;
  return null;
};

export default SideBar;
