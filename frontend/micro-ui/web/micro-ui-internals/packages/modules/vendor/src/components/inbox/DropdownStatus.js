import React from "react";
import { Dropdown, Loader } from "@nudmcdgnpm/digit-ui-react-components";

const DropdownStatus = ({ onAssignmentChange, value, applicationStatuses, areApplicationStatus }) => {
  return areApplicationStatus ? (
    <Dropdown option={applicationStatuses} optionKey="name" selected={value} select={onAssignmentChange}></Dropdown>
  ) : (
    <Loader />
  );
};

export default DropdownStatus;
