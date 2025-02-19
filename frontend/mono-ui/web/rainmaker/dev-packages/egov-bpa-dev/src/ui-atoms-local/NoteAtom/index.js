import React from "react";
import LabelContainer from "egov-ui-framework/ui-containers/LabelContainer";

// const styles = {
//   backgroundColor: "rgba(0, 0, 0, 0.6000000238418579)",
//   color: "rgba(255, 255, 255, 0.8700000047683716)",
//   marginLeft: "8px",
//   paddingLeft: "19px",
//   paddingRight: "19px",
//   textAlign: "center",
//   verticalAlign: "middle",
//   lineHeight: "35px",
//   fontSize: "16px"
// };

function NoteAtom(props) {
  const { number } = props;
  return <div><LabelContainer labelName="Permit valid up to" labelKey ={"BPA_LICENSE_VALID_LABEL"} dynamicArray={[number]}/></div>;
}

export default NoteAtom;