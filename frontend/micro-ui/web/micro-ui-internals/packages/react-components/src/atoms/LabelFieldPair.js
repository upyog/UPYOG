import React from "react";

const LabelFieldPair = (props) => {
  console.log("props==",props)
  return (
    <div style={{ ...props.style }} className="label-field-pair">
      {props.children}
    </div>
  );
};

export default LabelFieldPair;
