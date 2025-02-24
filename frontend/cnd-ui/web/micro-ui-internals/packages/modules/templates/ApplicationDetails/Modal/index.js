import React from "react";
import SVActionModal from "./SVActionModal";

const ActionModal = (props) => {
  if (props?.businessService.includes("street-vending")) {
    return <SVActionModal {...props} />;
  }
};

export default ActionModal;
