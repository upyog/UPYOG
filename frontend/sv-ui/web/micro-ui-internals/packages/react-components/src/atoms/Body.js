import React from "react";

const Body = (props) => {
  return <div className="body-container"><div style={{width:"100%",height:"100%",position:"relative"}}>{props.children}</div></div>;
};

export default Body;
