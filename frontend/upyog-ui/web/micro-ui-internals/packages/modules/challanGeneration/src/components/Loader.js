import React from "react";
import PropTypes from "prop-types";

/**
 * Loader component:
 * - Displays a spinner loader
 * - Supports full-page and module-level loading states
 */

export const Loader = ({ page = false }) => {
  const className = page ? "cg-loader-page" : "cg-loader-module";
  return (
    <div className={className}>
      <div className="cg-loader-spinner" />
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
