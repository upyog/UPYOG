import React from "react";
import InboxLinks from "./InboxLinks";
import FilterActionPlan from "./FilterActionPlan";
import SearchActionPlan from "./SearchActionPlan";
import WorksTable from "./WorksTable";

const WorksDesktopInbox = ({
  data = [],
  isLoading = false,
  totalCount = 0,
  pageSize = 10,
  pageOffset = 0,
  onPageSizeChange,
  onNextPage,
  onPrevPage,
  searchParams = {},
  onSearch,
  onClearSearch,
  filters = {},
  onFilterChange,
  statusCounts = {},
  parentRoute = "/works-ui/employee/works",
}) => {
  return (
    <div className="works-inbox-container">
      {/* Left Sidebar */}
      <div className="works-inbox-sidebar">
        <InboxLinks parentRoute={parentRoute} />
        <FilterActionPlan
          filters={filters}
          onFilterChange={onFilterChange}
          statusCounts={statusCounts}
        />
      </div>

      {/* Main Content */}
      <div className="works-inbox-main">
        <SearchActionPlan
          searchParams={searchParams}
          onSearch={onSearch}
          onClear={onClearSearch}
        />
        <WorksTable
          data={data}
          isLoading={isLoading}
          totalCount={totalCount}
          pageSize={pageSize}
          pageOffset={pageOffset}
          onPageSizeChange={onPageSizeChange}
          onNextPage={onNextPage}
          onPrevPage={onPrevPage}
          parentRoute={parentRoute}
        />
      </div>
    </div>
  );
};

export default WorksDesktopInbox;

