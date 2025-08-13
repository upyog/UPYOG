import React from "react";

/**
 * CHBWFReason Component
 * 
 * This component is responsible for rendering the comments associated with a specific checkpoint in the workflow timeline of the CHB module.
 * It displays a main comment (headComment) and an additional comment (otherComment) in a structured format.
 * 
 * Props:
 * - `headComment`: The main comment or heading for the checkpoint.
 * - `otherComment`: Additional details or comments related to the checkpoint.
 * 
 * Logic:
 * - Renders the `headComment` inside an `<h4>` tag to emphasize it as the main comment.
 * - Renders the `otherComment` inside a `<p>` tag to display it as supplementary information.
 * 
 * Returns:
 * - A `div` containing the main comment and additional comment, styled with the `checkpoint-comments-wrap` class.
 */
const CHBWFReason = ({ headComment, otherComment }) => (
  <div className="checkpoint-comments-wrap">
    <h4>{headComment}</h4>
    <p>{otherComment}</p>
  </div>
);

export default CHBWFReason;
