import React from "react";
import { getQueryArg } from "egov-ui-framework/ui-utils/commons";

const styles = {
  backgroundColor: "rgba(0, 0, 0, 0.6000000238418579)",
  color: "rgba(255, 255, 255, 0.8700000047683716)",
  marginLeft: "8px",
  paddingLeft: "19px",
  paddingRight: "19px",
  textAlign: "center",
  verticalAlign: "middle",
  lineHeight: "35px",
  fontSize: "16px"
};

function ApplicationNoContainer(props) {
  const { number } = props;
  const applicationNumber = getQueryArg(window.location.href, "applicationNumber")
  const applicationNo = (number === applicationNumber) ? number : applicationNumber;
  return <div style={styles}>Application No. {applicationNo}</div>;
}

export default ApplicationNoContainer;
