import React from "react";

const ApplyFilterBar = (props) => {
  return (
    <div className="rc-advertisement-module-card-flex-row">
      <button type={props.submit ? "submit" : "button"} style={props?.style} className="button-clear" onClick={props.onClear}>
        <header>{props.labelLink}</header>
      </button>
      <button className="submit-bar" type={props.submit ? "submit" : "button"} style={props?.style} onClick={props.onSubmit}>
        <header>{props.buttonLink}</header>
      </button>
    </div>
  );
};

export default ApplyFilterBar;
