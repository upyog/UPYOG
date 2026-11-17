import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Header } from "@nudmcdgnpm/digit-ui-react-components";
import { useWorksInbox } from "../../api/useWorksInbox";
import WorksDesktopInbox from "../../components/inbox/WorksDesktopInbox";
import WorksMobileInbox from "../../components/inbox/WorksMobileInbox";
import "../../components/inbox/inbox.css";

const Inbox = ({
  parentRoute = "/works-ui/employee/works",
  initialStates = {},
}) => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const [pageOffset, setPageOffset] = useState(initialStates.pageOffset || 0);
  const [pageSize, setPageSize] = useState(initialStates.pageSize || 10);
  const [searchParams, setSearchParams] = useState(initialStates.searchParams || {});
  const [filters, setFilters] = useState(initialStates.filters || {});

  const isMobile = window.Digit?.Utils?.browser?.isMobile();

  // Reset pagination when search params or filters change
  useEffect(() => {
    setPageOffset(0);
  }, [searchParams, filters]);

  const { isLoading, isError, data, revalidate } = useWorksInbox({
    tenantId,
    searchParams,
    filters,
    pagination: {
      limit: pageSize,
      offset: pageOffset,
      sortOrder: "DESC",
    },
  });

  const handleSearch = (newParams) => {
    setSearchParams(newParams);
  };

  const handleClearSearch = () => {
    setSearchParams({});
  };

  const handleFilterChange = (newFilters) => {
    setFilters(newFilters);
  };

  const handlePageSizeChange = (newSize) => {
    setPageSize(newSize);
    setPageOffset(0);
  };

  const fetchNextPage = () => {
    setPageOffset((prev) => prev + pageSize);
  };

  const fetchPrevPage = () => {
    setPageOffset((prev) => Math.max(0, prev - pageSize));
  };

  const actionPlans = data?.ActionPlans || [];
  const totalCount = data?.totalCount || 0;
  const statusCounts = data?.statusCounts || {};

  return (
    <div className="works-inbox-wrapper">
      <div className="works-inbox-header">
        <Header>{t("ES_COMMON_INBOX", "Works Inbox")}</Header>
      </div>

      {isMobile ? (
        <WorksMobileInbox
          data={actionPlans}
          isLoading={isLoading}
          totalCount={totalCount}
          pageSize={pageSize}
          pageOffset={pageOffset}
          onNextPage={fetchNextPage}
          onPrevPage={fetchPrevPage}
          searchParams={searchParams}
          onSearch={handleSearch}
          filters={filters}
          onFilterChange={handleFilterChange}
          statusCounts={statusCounts}
          parentRoute={parentRoute}
        />
      ) : (
        <WorksDesktopInbox
          data={actionPlans}
          isLoading={isLoading}
          totalCount={totalCount}
          pageSize={pageSize}
          pageOffset={pageOffset}
          onPageSizeChange={handlePageSizeChange}
          onNextPage={fetchNextPage}
          onPrevPage={fetchPrevPage}
          searchParams={searchParams}
          onSearch={handleSearch}
          onClearSearch={handleClearSearch}
          filters={filters}
          onFilterChange={handleFilterChange}
          statusCounts={statusCounts}
          parentRoute={parentRoute}
        />
      )}
    </div>
  );
};

export default Inbox;

