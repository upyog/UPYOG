import React from "react";
import { Table } from "@nudmcdgnpm/digit-ui-react-components";

/**
 * ApplicationTable Component
 * 
 * A thin wrapper around the shared `Table` component that passes through all
 * inbox-specific props including pagination, sorting, and column configurations.
 * Used in the desktop inbox view for displaying application records.
 * 
 * Props:
 * - `t`: i18n translation function
 * - `currentPage`, `columns`, `data`: Table display props
 * - `getCellProps`: Function to compute cell styling
 * - `disableSort`, `onSort`: Sorting configuration
 * - `onNextPage`, `onPrevPage`, `onPageSizeChange`: Pagination handlers
 * - `isPaginationRequired`, `pageSizeLimit`, `sortParams`, `totalRecords`: Pagination/sort state
 */
const ApplicationTable = ({
  t,
  currentPage,
  columns,
  data,
  getCellProps,
  disableSort,
  onSort,
  onNextPage,
  onPrevPage,
  onPageSizeChange,
  isPaginationRequired,
  pageSizeLimit,
  sortParams,
  totalRecords,
}) => {
  return (
    <Table
      t={t}
      data={data}
      currentPage={currentPage}
      columns={columns}
      getCellProps={getCellProps}
      onNextPage={onNextPage}
      onPrevPage={onPrevPage}
      pageSizeLimit={pageSizeLimit}
      disableSort={disableSort}
      isPaginationRequired={isPaginationRequired}
      onPageSizeChange={onPageSizeChange}
      onSort={onSort}
      sortParams={sortParams}
      totalRecords={totalRecords}
    />
  );
};

export default ApplicationTable;