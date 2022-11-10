import React from "react";

const DFMRadioButtons = (props) => {
  return (
    <div className="applicationWrapper">
      <div className="application-label">
        {" "}
        <label>{props.title}</label>
      </div>
      <div className="C-radio-btn application-label">
        {props.options?.map((item, ind) => (
          <React.Fragment>
            <input class="form-check-input" type="radio" name="inlineRadioOptions" id="inlineRadio1" value="" checked="" />
            <label>{item.label}</label>
          </React.Fragment>
        ))}{" "}
      </div>
    </div>
  );
};

export default DFMRadioButtons;
