import React from "react";

const Body = (props) => {
  return <div className="body-container"><img src="https://i.postimg.cc/J7xPLnt2/Background-Image.png" style={{position:"absolute",height:"100%",width:"100%"}}/><div style={{width:"100%",height:"100%",position:"relative"}}>{props.children}</div></div>;
};

export default Body;
