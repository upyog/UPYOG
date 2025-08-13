import { Card, Loader } from "@upyog/digit-ui-react-components";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import ApplicationTable from "./inbox/ApplicationTable";
import InboxLinks from "./inbox/InboxLink";
import SearchApplication from "./inbox/search";

/**
 * CHBDesktopInbox Component
 * 
 * This component is responsible for rendering the desktop version of the inbox for the CHB (Community Hall Booking) module.
 * It displays a table of applications or tasks, along with filtering and search functionality.
 * 
 * Props:
 * - `tableConfig`: Configuration object for the inbox table, including columns for search and inbox views.
 * - `filterComponent`: Name of the filter component to be dynamically loaded.
 * - `data`: The data to be displayed in the inbox table.
 * - `useNewInboxAPI`: Boolean indicating whether to use the new inbox API.
 * - `isSearch`: Boolean indicating whether the component is in search mode.
 * - `isLoading`: Boolean indicating whether the data is being loaded.
 * - `EmptyResultInboxComp`: Name of the component to display when the inbox has no results.
 * 
 * State Variables:
 * - `FilterComponent`: Dynamically loaded filter component for the inbox.
 * - `EmptyInboxComp`: Dynamically loaded component to display when the inbox has no results.
 * - `clearSearchCalled`: Boolean state to track whether the search has been cleared.
 * 
 * Hooks:
 * - `useTranslation`: Provides the `t` function for internationalization.
 * - `React.useMemo`: Memoizes the columns for the inbox table based on the mode (search or inbox).
 * 
 * Logic:
 * - Dynamically loads the filter and empty result components using the `Digit.ComponentRegistryService`.
 * - Determines the columns for the inbox table based on the mode (search or inbox) using the `tableConfig` prop.
 * - Handles different states of the inbox:
 *    - Displays a loader when `isLoading` is true.
 *    - Displays nothing when `clearSearchCalled` is true.
 *    - Displays the empty result component or a default message when there is no data.
 * 
 * Returns:
 * - A desktop inbox component with a table of applications or tasks, filtering, and search functionality.
 * - Displays a loader, empty result component, or the inbox table based on the current state.
 */
const CHBDesktopInbox = ({ tableConfig, filterComponent, ...props }) => {
  
  const { data, useNewInboxAPI } = props;
  const { t } = useTranslation();
  const [FilterComponent, setComp] = useState(() => Digit.ComponentRegistryService?.getComponent(filterComponent));
  const [EmptyInboxComp, setEmptyInboxComp] = useState(() => {
    const com = Digit.ComponentRegistryService?.getComponent(props.EmptyResultInboxComp);
    return com;
  });

  const [clearSearchCalled, setClearSearchCalled] = useState(false);

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
        <div className="filters-container">
          <InboxLinks parentRoute={props.parentRoute} businessService={props.moduleCode} />
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
      <div style={{ flex: 1 }}>
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

export default CHBDesktopInbox;
