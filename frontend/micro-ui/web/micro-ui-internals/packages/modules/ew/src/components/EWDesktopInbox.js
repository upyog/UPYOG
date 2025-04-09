// Importing necessary components and hooks from external libraries and local files
import { Card, Loader } from "@nudmcdgnpm/digit-ui-react-components"; // Components for displaying cards and loaders
import React, { useState } from "react"; // React library and hooks for state management
import { useTranslation } from "react-i18next"; // Hook for translations
import ApplicationTable from "./inbox/ApplicationTable"; // Component for rendering the application table
import InboxLinks from "./inbox/InboxLink"; // Component for rendering inbox links
import SearchApplication from "./inbox/search"; // Component for searching applications

// Component to render the desktop inbox for the E-Waste module
const EWDesktopInbox = ({ tableConfig, filterComponent, ...props }) => {
  const { data, useNewInboxAPI } = props; // Extracting data and API usage flag from props
  const { t } = useTranslation(); // Translation hook

  // State to dynamically load the filter component
  const [FilterComponent, setComp] = useState(() => Digit.ComponentRegistryService?.getComponent(filterComponent));

  // State to dynamically load the empty inbox component
  const [EmptyInboxComp, setEmptyInboxComp] = useState(() => {
    const com = Digit.ComponentRegistryService?.getComponent(props.EmptyResultInboxComp);
    return com;
  });

  const [clearSearchCalled, setClearSearchCalled] = useState(false); // State to track if the search has been cleared

  // Memoized columns for the table based on whether it is a search or inbox view
  const columns = React.useMemo(() => (props.isSearch ? tableConfig.searchColumns(props) : tableConfig.inboxColumns(props) || []), []);

  let result; // Variable to store the rendered result

  // Render a loader if data is still loading
  if (props.isLoading) {
    result = <Loader />;
  } 
  // Render nothing if the search has been cleared
  else if (clearSearchCalled) {
    result = null;
  } 
  // Render the empty inbox component or a "No Applications" message if no data is available
  else if (!data || data?.length === 0 || (useNewInboxAPI && data?.[0].dataEmpty)) {
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
  } 
  // Render the application table if data is available
  else if (data?.length > 0) {
    result = (
      <ApplicationTable
        t={t} // Translation function
        data={data} // Data to display in the table
        columns={columns} // Columns configuration
        getCellProps={(cellInfo) => {
          return {
            style: {
              minWidth: cellInfo.column.Header === t("ES_INBOX_APPLICATION_NO") ? "240px" : "", // Set minimum width for specific columns
              padding: "20px 18px", // Cell padding
              fontSize: "16px", // Font size
            },
          };
        }}
        onPageSizeChange={props.onPageSizeChange} // Handler for page size change
        currentPage={props.currentPage} // Current page number
        onNextPage={props.onNextPage} // Handler for navigating to the next page
        onPrevPage={props.onPrevPage} // Handler for navigating to the previous page
        pageSizeLimit={props.pageSizeLimit} // Page size limit
        onSort={props.onSort} // Handler for sorting
        disableSort={props.disableSort} // Flag to disable sorting
        sortParams={props.sortParams} // Sorting parameters
        totalRecords={props.totalRecords} // Total number of records
      />
    );
  }

  return (
    <div className="inbox-container">
      {/* Render the filters container if not in search mode */}
      {!props.isSearch && (
        <div className="filters-container">
          <InboxLinks parentRoute={props.parentRoute} businessService={props.moduleCode} /> {/* Render inbox links */}
          <div>
            {
              <FilterComponent
                defaultSearchParams={props.defaultSearchParams} // Default search parameters
                onFilterChange={props.onFilterChange} // Handler for filter changes
                searchParams={props.searchParams} // Current search parameters
                type="desktop" // Type of view (desktop)
                useNewInboxAPI={useNewInboxAPI} // Flag to use the new inbox API
                statusMap={useNewInboxAPI ? data?.[0].statusMap : null} // Status map for the new inbox API
                moduleCode={props.moduleCode} // Module code
              />
            }
          </div>
        </div>
      )}
      <div style={{ flex: 1 }}>
        {/* Render the search application component */}
        <SearchApplication
          defaultSearchParams={props.defaultSearchParams} // Default search parameters
          onSearch={(d) => {
            props.onSearch(d); // Handler for search
            setClearSearchCalled(false); // Reset the clear search flag
          }}
          type="desktop" // Type of view (desktop)
          searchFields={props.searchFields} // Search fields configuration
          isInboxPage={!props?.isSearch} // Flag to indicate if it is an inbox page
          searchParams={props.searchParams} // Current search parameters
          clearSearch={() => setClearSearchCalled(true)} // Handler for clearing the search
        />
        <div className="result" style={{ marginLeft: !props?.isSearch ? "24px" : "", flex: 1 }}>
          {result} {/* Render the result (table, loader, or empty message) */}
        </div>
      </div>
    </div>
  );
};

export default EWDesktopInbox; // Exporting the component