import React, { Fragment } from "react";
import SearchWaterConnectionIntegrated from "../SearchWaterConnectionIntegrated.js";

const SearchWaterConnectionViewIntegrated = ({ data, onSubmit, count, resultOk }) => {
  return (
    <>
      <SearchWaterConnectionIntegrated onSubmit={onSubmit} data={data} count={count} resultOk={resultOk} />
    </>
  );
};

export default SearchWaterConnectionViewIntegrated;
