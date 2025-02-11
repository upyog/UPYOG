import React from "react";
import PropTypes from "prop-types";

const LinkButton = (props) => {
  return (
    <div className={`card-link cp ${props.className}`} onClick={props.onClick} style={props.style}>
      {props.label}
    </div>
  );
};

LinkButton.propTypes = {
  /**
   * LinkButton contents
   */
  label: PropTypes.any,
  /**
   * Optional click handler
   */
  onClick: PropTypes.func,
};

LinkButton.defaultProps = {};

export default LinkButton;
