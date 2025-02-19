import React from "react";
import SvgIcon from "material-ui/SvgIcon";

const Circle = (props) => {
  return (
    <SvgIcon viewBox="0 0 24 24" className="custom-icon" {...props}>
      <path d="M12,20A8,8 0 0,1 4,12A8,8 0 0,1 12,4A8,8 0 0,1 20,12A8,8 0 0,1 12,20M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2Z" />
    </SvgIcon>
  );
};

export default Circle;
