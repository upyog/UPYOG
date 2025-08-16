import React, { useState, useEffect } from "react";
import CNDActionModal from "../Modal/CNDActionModal"

//Action Modal which is responsible to show the Actions on the Actionbar which is fetched as per the Business service.

const ActionModal = (props) => {
    if (props?.businessService.includes("cnd")) {
      return <CNDActionModal {...props} />;
    }
  };
  
  export default ActionModal; 