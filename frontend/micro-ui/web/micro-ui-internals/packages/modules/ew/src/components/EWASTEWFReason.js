// Importing React library for building UI components
import React from "react";

// Component to render workflow reasons in the E-Waste module
const EWASTEWFReason = ({ headComment, otherComment }) => (
  <div className="checkpoint-comments-wrap">
    {/* Render the main comment or heading */}
    <h4>{headComment}</h4>
    {/* Render additional comments if available */}
    <p>{otherComment}</p>
  </div>
);

export default EWASTEWFReason; // Exporting the component
