import React, { useState, useEffect } from "react";
import SVActionModal from "./SVActionModal";

//Action Modal which is responsible to show the Actions on the Actionbar which is fetched as per the Business service.

const ActionModal = (props) => {
    if (props?.businessService.includes("street-vending")) {
    return <SVActionModal {...props} />;
  }
  };
  
  export default ActionModal; 