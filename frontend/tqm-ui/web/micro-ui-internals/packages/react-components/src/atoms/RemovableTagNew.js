import React from "react";
import { Close } from "./svgindex";

const RemoveableTagNew = ({ text={}, onClick, extraStyles,disabled = false }) => (
  <div className="tag inbox-tag" style={extraStyles?extraStyles?.tagStyles:{}} >
    <span className="text inbox-tag-label" style={extraStyles?extraStyles?.textStyles:{}}>{`${text?.label} :`}</span>
    <span className="text inbox-tag-values" style={extraStyles?extraStyles?.textStyles:{}}>{text?.value}</span>
    <span onClick={disabled?null:onClick}>
      <Close className="close" style={extraStyles?extraStyles?.closeIconStyles:{}} />
    </span>
  </div>
);

export default RemoveableTagNew;
