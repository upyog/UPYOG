// Importing necessary components and hooks from external libraries
import React, { useEffect, useState } from "react";
import { Card, DetailsCard, Loader, PopUp, SearchAction } from "@nudmcdgnpm/digit-ui-react-components"; // UI components for cards, loaders, and popups
import { FilterAction } from "@nudmcdgnpm/digit-ui-react-components"; // Component for filter actions
import SearchApplication from "./search"; // Component for searching applications
import SortBy from "./SortBy"; // Component for sorting functionality

// Component to render application cards for the E-Waste inbox
export const ApplicationCard = ({
  t, // Translation function
  data, // Data to display in the application card
  onFilterChange, // Handler for filter changes
  onSearch, // Handler for search actions
  onSort, // Handler for sorting actions
  serviceRequestIdKey, // Key for service request IDs
  isFstpOperator, // Flag to indicate if the user is an FSTP operator
  isLoading, // Flag to indicate if data is loading
  isSearch, // Flag to indicate if it is in search mode
  searchParams, // Current search parameters
  searchFields, // Fields to display in the search form
  sortParams, // Current sorting parameters
  linkPrefix, // Prefix for links
  filterComponent, // Component to render filters
}) => {
  const [type, setType] = useState(isSearch ? "SEARCH" : ""); // State to manage the type of popup (SEARCH, FILTER, SORT)
  const [popup, setPopup] = useState(isSearch ? true : false); // State to manage the visibility of the popup
  const [_sortparams, setSortParams] = useState(sortParams); // State to manage sorting parameters
  const [FilterComp] = useState(() => Digit.ComponentRegistryService?.getComponent(filterComponent)); // Dynamically load the filter component

  // Function to handle filter changes and close the popup
  const onSearchFilter = (params) => {
    onFilterChange(params); // Notify parent component of the filter changes
    setPopup(false); // Close the popup
  };

  // Effect to open the popup when the type changes
  useEffect(() => {
    if (type) setPopup(true);
  }, [type]);

  // Function to handle closing the popup
  const handlePopupClose = () => {
    setPopup(false); // Close the popup
    setType(""); // Reset the type
    setSortParams(sortParams); // Reset sorting parameters
  };

  // Render a loader if data is still loading
  if (isLoading) {
    return <Loader />;
  }

  let result;
  // Render a message if no data is available
  if (!data || data?.length === 0) {
    result = (
      <Card style={{ marginTop: 20 }}>
        {t("CS_MYAPPLICATIONS_NO_APPLICATION")
          .split("\\n")
          .map((text, index) => (
            <p key={index} style={{ textAlign: "center" }}>
              {text}
            </p>
          ))}
      </Card>
    );
  } 
  // Render the details card if data is available
  else if (data && data?.length > 0) {
    result = <DetailsCard data={data} serviceRequestIdKey={serviceRequestIdKey} linkPrefix={linkPrefix ? linkPrefix : "/digit-ui/employee/ew/"} />;
  }

  return (
    <React.Fragment>
      <div className="searchBox">
        {/* Render the search action button */}
        {onSearch && (
          <SearchAction
            text="SEARCH"
            handleActionClick={() => {
              setType("SEARCH"); // Set the type to SEARCH
              setPopup(true); // Open the popup
            }}
          />
        )}
        {/* Render the filter action button if not in search mode */}
        {!isSearch && onFilterChange && (
          <FilterAction
            text="FILTER"
            handleActionClick={() => {
              setType("FILTER"); // Set the type to FILTER
              setPopup(true); // Open the popup
            }}
          />
        )}
        {/* Render the sort action button */}
        <FilterAction
          text="SORT"
          handleActionClick={() => {
            setType("SORT"); // Set the type to SORT
            setPopup(true); // Open the popup
          }}
        />
      </div>
      {result} {/* Render the result (message or details card) */}
      {popup && (
        <PopUp>
          {/* Render the filter popup */}
          {type === "FILTER" && (
            <div className="popup-module">
              {<FilterComp onFilterChange={onSearchFilter} Close={handlePopupClose} type="mobile" searchParams={searchParams} />}
            </div>
          )}
          {/* Render the sort popup */}
          {type === "SORT" && (
            <div className="popup-module">
              {<SortBy type="mobile" sortParams={sortParams} onClose={handlePopupClose} onSort={onSort} />}
            </div>
          )}
          {/* Render the search popup */}
          {type === "SEARCH" && (
            <div className="popup-module">
              <SearchApplication
                type="mobile"
                onClose={handlePopupClose}
                onSearch={onSearch}
                isFstpOperator={isFstpOperator}
                searchParams={searchParams}
                searchFields={searchFields}
              />
            </div>
          )}
        </PopUp>
      )}
    </React.Fragment>
  );
};