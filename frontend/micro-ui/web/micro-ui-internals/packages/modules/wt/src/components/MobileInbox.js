import React from "react";
import { useTranslation } from "react-i18next";
import { ApplicationCard } from "./inbox/ApplicationCard";
import ApplicationLinks from "./inbox/ApplicationLinks";

/**
 * `MobileInbox` component displays a mobile-friendly inbox interface, which is used 
 * to show application data in card format. It supports search, filtering, sorting, 
 * and customizable columns for different use cases (inbox or search). It leverages 
 * the `ApplicationCard` to display data and `ApplicationLinks` to show mobile-friendly 
 * navigation links. The component dynamically fetches and formats data based on the 
 * search state and columns configuration.
 * 
 * @param {Object} props - The component's props.
 * @param {Array} props.data - Data to be displayed in the inbox.
 * @param {boolean} props.isLoading - Indicates if data is loading.
 * @param {boolean} props.isSearch - Whether the view is in search mode or not.
 * @param {Array} props.searchFields - Fields to be used for the search functionality.
 * @param {function} props.onFilterChange - Callback for handling filter changes.
 * @param {function} props.onSearch - Callback for handling search functionality.
 * @param {function} props.onSort - Callback for handling sorting.
 * @param {string} props.parentRoute - Parent route for navigation.
 * @param {Object} props.searchParams - Parameters for search.
 * @param {Object} props.sortParams - Parameters for sorting.
 * @param {string} props.linkPrefix - Prefix for links to be displayed.
 * @param {Object} props.tableConfig - Configuration object for table columns.
 * @param {React.Component} props.filterComponent - Custom filter component.
 */

const MobileInbox = ({
  data,
  isLoading,
  isSearch,
  searchFields,
  onFilterChange,
  onSearch,
  onSort,
  parentRoute,
  searchParams,
  sortParams,
  linkPrefix,
  tableConfig,
  filterComponent,
}) => {
  const { t } = useTranslation();
  const getData = () => {
    return data?.map((dataObj) => {
      const obj = {};
      const columns = isSearch ? tableConfig.searchColumns() : tableConfig.inboxColumns();
      columns.forEach((el) => {
        if (el.mobileCell) obj[el.Header] = el.mobileCell(dataObj);
      });
      return obj;
    });
  };

  return (
    <div style={{ padding: 0 }}>
      <div className="inbox-container">
        <div className="filters-container">
          {!isSearch && <ApplicationLinks classNameForMobileView="linksWrapperForMobileInbox" linkPrefix={parentRoute} isMobile={true} />}
          <ApplicationCard
            t={t}
            data={getData()}
            onFilterChange={onFilterChange}
            isLoading={isLoading}
            isSearch={isSearch}
            onSearch={onSearch}
            onSort={onSort}
            searchParams={searchParams}
            searchFields={searchFields}
            linkPrefix={linkPrefix}
            sortParams={sortParams}
            serviceRequestIdKey={tableConfig?.serviceRequestIdKey}
            filterComponent={filterComponent}
          />
        </div>
      </div>
    </div>
  );
};

export default MobileInbox;
