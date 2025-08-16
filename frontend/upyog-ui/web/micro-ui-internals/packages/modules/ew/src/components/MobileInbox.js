import React from "react";
import { useTranslation } from "react-i18next";
import { ApplicationCard } from "./inbox/ApplicationCard";
import ApplicationLinks from "./inbox/ApplicationLinks";

/**
 * Renders a mobile-optimized inbox interface for the E-Waste module.
 * Provides application listing, filtering, and search capabilities for mobile devices.
 *
 * @param {Object} props - Component properties
 * @param {Array} props.data - Application data to be displayed in the inbox
 * @param {boolean} props.isLoading - Flag indicating data loading state
 * @param {boolean} props.isSearch - Flag indicating search mode
 * @param {Array} props.searchFields - Configuration for search form fields
 * @param {Function} props.onFilterChange - Handler for filter changes
 * @param {Function} props.onSearch - Handler for search operations
 * @param {Function} props.onSort - Handler for sorting operations
 * @param {string} props.parentRoute - Base route for navigation
 * @param {Object} props.searchParams - Current search parameters
 * @param {Object} props.sortParams - Current sorting parameters
 * @param {string} props.linkPrefix - Prefix for application links
 * @param {Object} props.tableConfig - Table display configuration
 * @param {string} props.filterComponent - Name of filter component to load
 * @returns {JSX.Element} Mobile inbox interface
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

  /**
   * Transforms application data for mobile view rendering.
   * Maps table columns to their mobile-specific cell representations.
   *
   * @returns {Array} Transformed data for mobile display
   */
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
