import React from "react";
import { Table } from "@upyog/digit-ui-react-components";


/**
 * CHBApplicationTable Component
 * 
 * This component is responsible for rendering a table to display CHB (Community Hall Booking) applications.
 * It provides features such as pagination, global search, and customizable columns for displaying application data.
 * 
 * Props:
 * - `t`: Translation function for internationalization.
 * - `data`: Array of application data to be displayed in the table.
 * - `columns`: Array of column configurations for the table.
 * - `globalSearch`: Boolean indicating whether global search functionality is enabled.
 * - `onSearch`: Callback function triggered when a search is performed.
 * - `getCellProps`: Function to customize the properties of individual table cells.
 * - `pageSizeLimit`: Number of records to display per page.
 * - `totalRecords`: Total number of records available for pagination.
 * - `currentPage`: Current page number in the pagination.
 * - `onNextPage`: Callback function triggered when the "Next" button is clicked in pagination.
 * - `onPrevPage`: Callback function triggered when the "Previous" button is clicked in pagination.
 * - `onPageSizeChange`: Callback function triggered when the page size is changed.
 * 
 * Features:
 * - Displays application data in a tabular format with customizable columns.
 * - Supports global search functionality for filtering data.
 * - Provides manual pagination controls, including next/previous page navigation and page size selection.
 * - Allows customization of table cell properties using the `getCellProps` function.
 * 
 * Returns:
 * - A `Table` component from the `@nudmcdgnpm/digit-ui-react-components` library with the provided data, columns, and pagination controls.
 */
const CHBApplicationTable = ({
  t,
  data,
  columns,
  globalSearch,
  onSearch,
  getCellProps,
  pageSizeLimit,
  totalRecords,
  currentPage,
  onNextPage,
  onPrevPage,
  onPageSizeChange,

}) => {
  return (
    <Table
      t={t}
      data={data}
      columns={columns}
      onSearch={onSearch}
      globalSearch={globalSearch}
      manualGlobalFilter={true}
      manualPagination={true}
      pageSizeLimit={pageSizeLimit}
      getCellProps={getCellProps}
      totalRecords={totalRecords}
      currentPage={currentPage}
      onNextPage={onNextPage}
      onPrevPage={onPrevPage}
      onPageSizeChange={onPageSizeChange}

    />
  )
}

export default CHBApplicationTable;