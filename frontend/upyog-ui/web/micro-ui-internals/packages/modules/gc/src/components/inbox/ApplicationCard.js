import React, { useCallback, useEffect, useState } from "react";

import { Card, DetailsCard, Loader, PopUp, SearchAction } from "@nudmcdgnpm/digit-ui-react-components";
import { FilterAction } from "@nudmcdgnpm/digit-ui-react-components";
import SearchApplication from "./search";
import SortBy from "./SortBy";

/**
 * ApplicationCard Component
 * 
 * Renders the mobile card view for applications with search, filter, and sort actions.
 * Manages popups for each action type (SEARCH, FILTER, SORT) and displays application
 * data using a DetailsCard layout.
 * 
 * Props:
 * - `data`: Array of application objects to display
 * - `onFilterChange`, `onSearch`, `onSort`: Callbacks for respective actions
 * - `serviceRequestIdKey`: Key to extract service request ID from data objects
 * - `isLoading`: Boolean for loading state
 * - `isSearch`: Boolean indicating if in search mode
 * - `searchParams`, `searchFields`, `sortParams`: Current state for search and sort
 * - `linkPrefix`: Base URL for navigation links in DetailsCard
 * - `filterComponent`: String identifier for the filter component
 */
export const ApplicationCard = ({
  t,
  data,
  onFilterChange,
  onSearch,
  onSort,
  serviceRequestIdKey,
  isFstpOperator,
  isLoading,
  isSearch,
  searchParams,
  searchFields,
  sortParams,
  linkPrefix,
  removeParam,
  filterComponent,
}) => {
  const [type, setType] = useState(isSearch ? "SEARCH" : "");
  const [popup, setPopup] = useState(isSearch ? true : false);
  const [_sortparams, setSortParams] = useState(sortParams);
  const [FilterComp] = useState(() => Digit.ComponentRegistryService?.getComponent(filterComponent));

  const onSearchFilter = (params) => {
    onFilterChange(params);
    setPopup(false);
  };

  useEffect(() => {
    if (type) setPopup(true);
  }, [type]);

  const handlePopupClose = () => {
    setPopup(false);
    setType("");
    setSortParams(sortParams);
  };

  if (isLoading) {
    return <Loader />;
  }

  let result;
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
  } else if (data && data?.length > 0) {
    result = <DetailsCard data={data} serviceRequestIdKey={serviceRequestIdKey} linkPrefix={linkPrefix ? linkPrefix : "/upyog-ui/employee/gc/"} />;
  }

  return (
    <React.Fragment>
      <div className="searchBox">
        {onSearch && (
          <SearchAction
            text="SEARCH"
            handleActionClick={() => {
              setType("SEARCH");
              setPopup(true);
            }}
          />
        )}
        {!isSearch && onFilterChange && (
          <FilterAction
            text="FILTER"
            handleActionClick={() => {
              setType("FILTER");
              setPopup(true);
            }}
          />
        )}
        <FilterAction
          text="SORT"
          handleActionClick={() => {
            setType("SORT");
            setPopup(true);
          }}
        />
      </div>
      {result}
      {popup && (
        <PopUp>
          {type === "FILTER" && (
            <div className="popup-module">
              {<FilterComp onFilterChange={onSearchFilter} Close={handlePopupClose} type="mobile" searchParams={searchParams} />}
            </div>
          )}
          {type === "SORT" && (
            <div className="popup-module">
              {<SortBy type="mobile" sortParams={sortParams} onClose={handlePopupClose} onSort={onSort} />}
            </div>
          )}
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