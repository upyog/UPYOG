import React from "react";
import { Table } from "@nudmcdgnpm/digit-ui-react-components";
import { assetTableWrapperStyle, getAssetTableCellProps } from "./assetTableUtils";

/**
 * Shared EST asset table wrapper with consistent styling.
 */
const AssetTable = ({
  t,
  data,
  columns,
  totalRecords,
  isMobile,
  pagination,
  pageSizeLimit = 10,
  disableSort = false,
}) => {
  const cellProps = getAssetTableCellProps(isMobile);
  const wrapperStyle = assetTableWrapperStyle(isMobile);

  const tableProps = {
    t,
    data,
    columns,
    totalRecords: totalRecords ?? data?.length ?? 0,
    getCellProps: () => cellProps,
    disableSort,
  };

  if (pagination) {
    Object.assign(tableProps, {
      onPageSizeChange: pagination.onPageSizeChange,
      currentPage: pagination.currentPage,
      onNextPage: pagination.onNextPage,
      onPrevPage: pagination.onPrevPage,
      pageSizeLimit: pagination.pageSizeLimit ?? pageSizeLimit,
      onSort: pagination.onSort,
      sortParams: pagination.sortParams,
    });
  } else {
    Object.assign(tableProps, {
      manualPagination: false,
      globalSearch: false,
      pageSizeLimit,
    });
  }

  return (
    <div style={wrapperStyle}>
      <Table {...tableProps} />
    </div>
  );
};

export default AssetTable;
