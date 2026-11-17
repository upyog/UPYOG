import React from "react";
import PropTypes from "prop-types";

const ToggleSwitch = ({ value, onChange, label, name, ref, style, ...props }) => {
  return (
    <div style={style}>
      <input
        checked={value}
        onChange={onChange}
        className="react-switch-checkbox"
        id={name}
        type="checkbox"
      />
      <label
        className={`react-switch-label ${value ? "react-switch-label-active" : ""}`}
        htmlFor={name}
      >
        <span className={`react-switch-button ${!value ? "react-switch-btn-inactive" : ""}`} />
      </label>
    </div>
  );

};

ToggleSwitch.propTypes = {
  value: PropTypes.bool, 
  name: PropTypes.string,
  onChange: PropTypes.func,
  ref: PropTypes.func,
};

ToggleSwitch.defaultProps = {};

export default ToggleSwitch;
