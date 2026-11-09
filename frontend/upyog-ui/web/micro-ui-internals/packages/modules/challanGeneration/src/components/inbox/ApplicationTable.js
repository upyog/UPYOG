import React from "react";
import { Table } from "@nudmcdgnpm/digit-ui-react-components";

/**
 * Reusable table component wrapper:
 * - Renders data using Digit UI Table
 * - Supports pagination, sorting, and custom cell props
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
  onFirstPage,
  onLastPage,
  onPageSizeChange,
  pageSizeLimit,
  sortParams,
  totalRecords,
  isPaginationRequired,
  className,
}) => {
  return (
    <div className={`challan-table-container ${className || ""}`}>
      <Table
        t={t}
        data={data}
        currentPage={currentPage}
        columns={columns}
        getCellProps={getCellProps}
        onNextPage={onNextPage}
        onPrevPage={onPrevPage}
        onLastPage={onLastPage}
        onFirstPage={onFirstPage}
        pageSizeLimit={pageSizeLimit}
        disableSort={disableSort}
        onPageSizeChange={onPageSizeChange}
        onSort={onSort}
        sortParams={sortParams}
        totalRecords={totalRecords}
        isPaginationRequired={isPaginationRequired}
        className={className}
      />
    </div>
  );
};

export default ApplicationTable;
