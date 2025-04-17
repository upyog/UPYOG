import { Card, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import ApplicationTable from "./inbox/ApplicationTable";
import SearchApplication from "./inbox/search";

/**
 * The CNDDesktopInbox component provides a structured layout for viewing application data in a table format, 
 * with support for search, filtering, sorting, and pagination.
 * 
 * Key features:
 * Dynamically loads filter components from a component registry service
 * Manages empty state handling through a configurable empty result component
 * Provides search functionality via SearchApplication component
 * Renders application data in a table with configurable columns
 * Supports pagination and sorting
 */

const CNDDesktopInbox = ({ tableConfig, filterComponent, ...props }) => {
  let isCitizen = window.location.href.includes("citizen");
  const { data, useNewInboxAPI } = props;
  const { t } = useTranslation();
  // Dynamically load components from registry service
  // This allows for flexible component injection based on configuration
  const [FilterComponent, setComp] = useState(() => Digit.ComponentRegistryService?.getComponent(filterComponent));
  const [EmptyInboxComp, setEmptyInboxComp] = useState(() => {
    const com = Digit.ComponentRegistryService?.getComponent(props.EmptyResultInboxComp);
    return com;
  });

  const [clearSearchCalled, setClearSearchCalled] = useState(false);


// Memoized column configuration that adapts based on whether we're in search mode
// This prevents unnecessary re-renders when props change but column config doesn't need to change
  const columns = React.useMemo(() => (props.isSearch ? tableConfig.searchColumns(props) : tableConfig.inboxColumns(props) || []), []);

  let result;
  if (props.isLoading) {
    result = <Loader />;
  } else if (clearSearchCalled) {
    result = null;
  } else if (!data || data?.length === 0 || (useNewInboxAPI && data?.[0].dataEmpty)) {
    result =
      (EmptyInboxComp && <EmptyInboxComp data={data} />) ||
      (data?.length === 0 || (useNewInboxAPI && data?.[0].dataEmpty) ? (
        <Card style={{ marginTop: 20 }}>
          {t("CS_MYAPPLICATIONS_NO_APPLICATION")
            .split("\\n")
            .map((text, index) => (
              <p key={index} style={{ textAlign: "center" }}>
                {text}
              </p>
            ))}
        </Card>
      ) : (
        <Loader />
      ));
  } else if (data?.length > 0) {
    result = (
      <ApplicationTable
        t={t}
        data={data}
        columns={columns}
        getCellProps={(cellInfo) => {
          return {
            style: {
              minWidth: cellInfo.column.Header === t("ES_INBOX_APPLICATION_NO") ? "240px" : "",
              padding: "20px 18px",
              fontSize: "16px",
            },
          };
        }}
        onPageSizeChange={props.onPageSizeChange}
        currentPage={props.currentPage}
        onNextPage={props.onNextPage}
        onPrevPage={props.onPrevPage}
        pageSizeLimit={props.pageSizeLimit}
        onSort={props.onSort}
        disableSort={props.disableSort}
        sortParams={props.sortParams}
        totalRecords={props.totalRecords}
      />
    );
  }

  return (
    <div className="inbox-container">
      {!props.isSearch && (
        <div className={isCitizen?"":"filters-container"}>
          <div>
            {
              <FilterComponent
                defaultSearchParams={props.defaultSearchParams}
                onFilterChange={props.onFilterChange}
                searchParams={props.searchParams}
                type="desktop"
                useNewInboxAPI={useNewInboxAPI}
                statusMap={useNewInboxAPI ? data?.[0].statusMap : null}
                moduleCode={props.moduleCode}
              />
            }
          </div>
        </div>
      )}
      <div style={{ width: isCitizen?"75%":"" }}>
        <SearchApplication
          defaultSearchParams={props.defaultSearchParams}
          onSearch={(d) => {
            props.onSearch(d);
            setClearSearchCalled(false);
          }}
          type="desktop"
          searchFields={props.searchFields}
          isInboxPage={!props?.isSearch}
          searchParams={props.searchParams}
          clearSearch={() => setClearSearchCalled(true)}
        />
        <div className="result" style={{ marginLeft: !props?.isSearch ? "24px" : "", flex: 1 }}>
          {result}
        </div>
      </div>
    </div>
  );
};

export default CNDDesktopInbox;
