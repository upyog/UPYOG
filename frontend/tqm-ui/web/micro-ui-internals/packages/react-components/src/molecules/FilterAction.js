import React from "react";
import { FilterSvg,FilterIcon,SortSvg } from "../atoms/svgindex";
import RoundedLabel from "../atoms/RoundedLabel";

const FilterAction = ({ text, handleActionClick, ...props }) => (
  <div className="searchAction" onClick={handleActionClick}>
    <RoundedLabel count={props.filterCount}></RoundedLabel>
    <SortSvg fill={"#f47738"}/> <span className="searchText">{text}</span>
  </div>
);

export default FilterAction;
