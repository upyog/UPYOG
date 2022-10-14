import React from "react";


 
const LabelFieldPair = (props) => {
  return (
    <div className="padding">
      <div style={{ ...props.style }} className="label-field-pair">
        {props.children}
      </div>
    </div>
  );
};

export default LabelFieldPair;
