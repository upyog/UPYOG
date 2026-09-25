import React from "react";
import PropTypes from "prop-types";

// A simple loader component that can be used as a full-page overlay or within a module. 
// It displays a spinning animation to indicate loading state. The `page` prop determines whether it should cover the entire page or just a section of it.
export const Loader = ({ page = false }) => {
  return (
    <div className={page ? "ndc-loader-page" : "ndc-loader-module"}>
      <div className="ndc-loader-spinner" />
    </div>
  );
};

Loader.propTypes = {
  /** Full page loader or module loader */
  page: PropTypes.bool,
};

Loader.defaultProps = {
  page: false,
};
