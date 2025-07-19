import React from "react";
import { CheckPoint } from "@demodigit/digit-ui-react-components";

const PendingForAssignment = ({ isCompleted, text, complaintFiledDate, customChild }) => {
  return <CheckPoint isCompleted={isCompleted} label={text} customChild={customChild} />;
};

export default PendingForAssignment;
