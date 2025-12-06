import React from "react";

/**
 * Renders workflow reasons and comments for the E-Waste module.
 * Displays a primary comment as a heading followed by optional additional comments.
 *
 * @param {Object} props - Component properties
 * @param {string} props.headComment - Primary comment or reason to display as heading
 * @param {string} props.otherComment - Secondary or additional comments to display
 * @returns {JSX.Element} Container with formatted comments
 */
const EWASTEWFReason = ({ headComment, otherComment }) => (
  <div className="checkpoint-comments-wrap">
    <h4>{headComment}</h4>
    <p>{otherComment}</p>
  </div>
);

export default EWASTEWFReason;
