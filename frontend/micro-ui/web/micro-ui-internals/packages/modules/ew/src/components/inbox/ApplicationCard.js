import React, { useEffect, useState } from "react";
import { Card, DetailsCard, Loader, PopUp, SearchAction } from "@upyog/digit-ui-react-components";
import { FilterAction } from "@upyog/digit-ui-react-components";
import SearchApplication from "./search";
import SortBy from "./SortBy";

/**
 * Functional component for rendering an application card.
 * @param {Object} props - The props object containing the following properties:
 *   - t: Translation function for internationalization
 *   - data: Data object for the application card
 *   - onFilterChange: Function to handle filter change
 *   - onSearch: Function to handle search
 *   - onSort: Function to handle sorting
 *   - serviceRequestIdKey: Key for service request ID
 *   - isFstpOperator: Boolean flag indicating if the user is an FSTP operator
 *   - isLoading: Boolean flag indicating if data is loading
 *   - isSearch: Boolean flag indicating if search is active
 * @returns JSX element
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
  filterComponent,
}) => {
  const [type, setType] = useState(isSearch ? "SEARCH" : "");
  const [popup, setPopup] = useState(isSearch ? true : false);
  const [_sortparams, setSortParams] = useState(sortParams);
  const [FilterComp] = useState(() => Digit.ComponentRegistryService?.getComponent(filterComponent));

  /**
   * Handles the application of filters and closes the popup.
   *
   * @param {Object} params - The filter parameters to be applied.
   */
  const onSearchFilter = (params) => {
    onFilterChange(params);
    setPopup(false);
  };

  /**
   * Opens the popup whenever the type of action (SEARCH, FILTER, SORT) changes.
   */
  useEffect(() => {
    if (type) setPopup(true);
  }, [type]);

  /**
   * Closes the popup and resets the type and sorting parameters.
   */
  const handlePopupClose = () => {
    setPopup(false);
    setType("");
    setSortParams(sortParams);
  };

  /**
   * Displays a loader while the data is being fetched.
   *
   * @return {JSX.Element} A loader component.
   */
  if (isLoading) {
    return <Loader />;
  }

  let result;

  /**
   * Displays a message when no application data is available.
   */
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
  /**
   * Displays the application details when data is available.
   */
  else if (data && data?.length > 0) {
    result = <DetailsCard data={data} serviceRequestIdKey={serviceRequestIdKey} linkPrefix={linkPrefix ? linkPrefix : "/digit-ui/employee/ew/"} />;
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