import React from "react";
import { SearchIconSvg,FilterSvg,FilterIcon } from "../atoms/svgindex";

const SearchAction = ({ text, handleActionClick }) => (
  <div className="searchAction" onClick={handleActionClick}>
    <FilterIcon fill={"#f47738"}/> <span className="searchText">{text}</span>
  </div>
);

export default SearchAction;
