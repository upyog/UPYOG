import React from "react";

const Reason = ({ headComment, otherComment }) => (
  <div className="checkpoint-comments-wrap">
    <h2>{headComment}</h2>
    <p>{otherComment}</p>
  </div>
);

export default Reason;
