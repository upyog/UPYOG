/**
 * @file MobileInbox.js
 * @description Renders the mobile inbox with filtering, search, and sorting.
 * 
 * @components
 * - `ApplicationLinks`: Navigation links.
 * - `ApplicationCard`: Displays application data.
 * 
 * @props
 * - `data`: Application records.
 * - `isLoading`: Loading state.
 * - `isSearch`: Search mode flag.
 * - `onFilterChange`, `onSearch`, `onSort`: Callbacks.
 * - `parentRoute`, `linkPrefix`: Navigation routes.
 * - `tableConfig`: Table configuration.
 * 
 * @methods
 * - `getData`: Formats data for display.
 */
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { ApplicationCard } from "./inbox/ApplicationCard";
import ApplicationLinks from "./inbox/ApplicationLinks";

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
