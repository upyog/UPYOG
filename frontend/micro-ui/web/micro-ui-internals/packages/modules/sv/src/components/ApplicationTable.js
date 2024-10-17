import React from "react";
import { Table } from "@nudmcdgnpm/digit-ui-react-components";

import { useTranslation } from "react-i18next";

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
