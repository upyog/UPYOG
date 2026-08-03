import React from "react";
import { Table } from "@nudmcdgnpm/upyog-ui-react-components-lts";

/**
 * Component developed to show the Details inside the Table in Inbox as well as My Request Components 
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
  pageSizeLimit,
  sortParams,
  totalRecords,
  styles,
  isPaginationRequired
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
      onPageSizeChange={onPageSizeChange}
      onSort={onSort}
      sortParams={sortParams}
      totalRecords={totalRecords}
      styles={styles}
      isPaginationRequired={isPaginationRequired}
    />
  );
};

export default ApplicationTable;
