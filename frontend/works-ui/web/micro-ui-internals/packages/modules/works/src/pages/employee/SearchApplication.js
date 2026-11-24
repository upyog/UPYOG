import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Header, BackButton } from "@nudmcdgnpm/digit-ui-react-components";
import { useWorksSearch } from "../../api/useWorksInbox";
import SearchActionPlan from "../../components/inbox/SearchActionPlan";
import WorksTable from "../../components/inbox/WorksTable";
import "../../components/inbox/inbox.css";

const SearchApplication = ({ parentRoute = "/works-ui/employee/works" }) => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const [pageOffset, setPageOffset] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [searchParams, setSearchParams] = useState({});

  useEffect(() => {
    setPageOffset(0);
  }, [searchParams]);

  const { isLoading, data } = useWorksSearch({
    tenantId,
    searchParams,
    filters: {},
    pagination: {
      limit: pageSize,
      offset: pageOffset,
      sortBy: "createdTime",
      sortOrder: "DESC",
    },
  });

  const actionPlans = data?.ActionPlans || [];
  const totalCount = data?.totalCount || 0;

  return (
    <div className="works-inbox-wrapper">
      <div style={{ marginBottom: 12 }}>
        <BackButton>{t("CS_COMMON_BACK", "Back")}</BackButton>
      </div>

      <div className="works-inbox-header">
        <Header>{t("WORKS_SEARCH_APPLICATION", "Search Action Plans")}</Header>
      </div>

      <div style={{ display: "flex", flexDirection: "column", gap: 16 }}>
        <SearchActionPlan
          searchParams={searchParams}
          onSearch={setSearchParams}
          onClear={() => setSearchParams({})}
        />

        <WorksTable
          data={actionPlans}
          isLoading={isLoading}
          totalCount={totalCount}
          pageSize={pageSize}
          pageOffset={pageOffset}
          onPageSizeChange={(newSize) => {
            setPageSize(newSize);
            setPageOffset(0);
          }}
          onNextPage={() => setPageOffset((prev) => prev + pageSize)}
          onPrevPage={() => setPageOffset((prev) => Math.max(0, prev - pageSize))}
          parentRoute={parentRoute}
        />
      </div>
    </div>
  );
};

export default SearchApplication;

