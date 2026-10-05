import React from "react";

const Body = (props) => {
  return <div className="body-container"><div className="rc-body-fullwidth">{props.children}</div></div>;
};

export default Body;
