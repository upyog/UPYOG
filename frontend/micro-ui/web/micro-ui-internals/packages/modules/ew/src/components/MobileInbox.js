// Importing necessary components and hooks from external libraries and local files
import React from "react";
import { useTranslation } from "react-i18next"; // Hook for translations
import { ApplicationCard } from "./inbox/ApplicationCard"; // Component for rendering application cards
import ApplicationLinks from "./inbox/ApplicationLinks"; // Component for rendering application links

// Component to render the mobile inbox for the E-Waste module
const MobileInbox = ({
  data, // Data to display in the inbox
  isLoading, // Flag to indicate if data is loading
  isSearch, // Flag to indicate if it is in search mode
  searchFields, // Fields to display in the search form
  onFilterChange, // Handler for filter changes
  onSearch, // Handler for search actions
  onSort, // Handler for sorting actions
  parentRoute, // Parent route for navigation
  searchParams, // Current search parameters
  sortParams, // Current sorting parameters
  linkPrefix, // Prefix for links
  tableConfig, // Configuration for the table
  filterComponent, // Component to render filters
}) => {
  const { t } = useTranslation(); // Translation hook

  // Function to transform data for mobile view
  const getData = () => {
    return data?.map((dataObj) => {
      const obj = {};
      const columns = isSearch ? tableConfig.searchColumns() : tableConfig.inboxColumns(); // Get columns based on search or inbox mode
      columns.forEach((el) => {
        if (el.mobileCell) obj[el.Header] = el.mobileCell(dataObj); // Map mobile-specific cells
      });
      return obj;
    });
  };

  return (
    <div style={{ padding: 0 }}>
      <div className="inbox-container">
        <div className="filters-container">
          {/* Render application links if not in search mode */}
          {!isSearch && <ApplicationLinks classNameForMobileView="linksWrapperForMobileInbox" linkPrefix={parentRoute} isMobile={true} />}
          {/* Render the application card with the transformed data */}
          <ApplicationCard
            t={t} // Translation function
            data={getData()} // Transformed data for mobile view
            onFilterChange={onFilterChange} // Handler for filter changes
            isLoading={isLoading} // Flag to indicate if data is loading
            isSearch={isSearch} // Flag to indicate if it is in search mode
            onSearch={onSearch} // Handler for search actions
            onSort={onSort} // Handler for sorting actions
            searchParams={searchParams} // Current search parameters
            searchFields={searchFields} // Fields to display in the search form
            linkPrefix={linkPrefix} // Prefix for links
            sortParams={sortParams} // Current sorting parameters
            serviceRequestIdKey={tableConfig?.serviceRequestIdKey} // Key for service request IDs
            filterComponent={filterComponent} // Component to render filters
          />
        </div>
      </div>
    </div>
  );
};

export default MobileInbox; // Exporting the component
