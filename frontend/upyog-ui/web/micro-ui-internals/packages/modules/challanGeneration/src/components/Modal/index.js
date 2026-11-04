import React, { useState, useEffect } from "react";
import ChallanActionModal from "./ChallanActionModal";

/**
 * ActionModal component:
 * - Wrapper for ChallanActionModal
 * - Passes all props directly
 */

const ActionModal = (props) => {
  return <ChallanActionModal {...props} />;
};

export default ActionModal;
