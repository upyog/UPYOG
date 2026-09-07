import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { ApplicationCard } from "./inbox/ApplicationCard";
import ApplicationLinks from "./inbox/ApplicationLinks";

/**
 * MobileInbox Component
 * 
 * Renders the mobile-optimized version of the inbox for the GC module.
 * Maps application data to mobile-friendly columns using the table configuration,
 * and wraps it in an ApplicationCard with filter, search, and sort actions.
 * 
 * Props:
 * - `data`: Array of application objects
 * - `isLoading`: Boolean indicating if data is being fetched
 * - `isSearch`: Boolean indicating if the inbox is in search mode
 * - `searchFields`, `onFilterChange`, `onSearch`, `onSort`: Handlers for search/filter/sort
 * - `parentRoute`, `linkPrefix`: Routing helpers
 * - `searchParams`, `sortParams`: Current search and sort state
 * - `tableConfig`: Configuration with inboxColumns/searchColumns and mobileCell renderers
 * - `filterComponent`: String identifier of the filter component
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
      const columns = (isSearch ? tableConfig?.searchColumns?.() : tableConfig?.inboxColumns?.()) || [];
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