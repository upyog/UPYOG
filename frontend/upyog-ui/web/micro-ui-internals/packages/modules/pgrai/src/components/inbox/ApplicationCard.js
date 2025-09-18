import React, { useState } from "react";
import { FilterAction, Card, DetailsCard, PopUp, SearchAction } from "@upyog/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import SearchComplaint from "./search";
import Filter from "./NewInboxFilter";

/**
 * ApplicationCard Component
 *
 * This component displays a list of complaint applications in card format. It provides mobile-friendly
 * actions for searching and filtering complaints using pop-ups. It conditionally renders results, no-data messages,
 * or error states based on the `data` prop. Integrates with `SearchComplaint` and `Filter` components.
 */

export const ApplicationCard = ({ data, onFilterChange, onSearch, serviceRequestIdKey, searchParams }) => {
  const { t } = useTranslation();
  const [popup, setPopup] = useState(false);
  const [selectedComponent, setSelectedComponent] = useState(null);
  const [filterCount, setFilterCount] = useState(Digit.inboxFilterCount || 1);

  const handlePopupAction = (type) => {
    if (type === "SEARCH") {
      setSelectedComponent(<SearchComplaint type="mobile" onClose={handlePopupClose} onSearch={onSearch} searchParams={searchParams} />);
    } else if (type === "FILTER") {
      setSelectedComponent(
        <Filter complaints={data} onFilterChange={onFilterChange} onClose={handlePopupClose} type="mobile" searchParams={searchParams} />
      );
    }
    setPopup(true);
  };

  const handlePopupClose = () => {
    setPopup(false);
    setSelectedComponent(null);
  };

  let result;
  if (data && data?.length === 0) {
    result = (
      <Card style={{ marginTop: 20 }}>
        {t("CS_MYCOMPLAINTS_NO_COMPLAINTS_EMPLOYEE")
          .split("\\n")
          .map((text, index) => (
            <p key={index} style={{ textAlign: "center" }}>
              {text}
            </p>
          ))}
      </Card>
    );
  } else if (data && data?.length > 0) {
    result = <DetailsCard data={data} serviceRequestIdKey={serviceRequestIdKey} linkPrefix={"/upyog-ui/employee/pgr/complaint/details/"} />;
  } else {
    result = (
      <Card style={{ marginTop: 20 }}>
        {t("CS_COMMON_ERROR_LOADING_RESULTS")
          .split("\\n")
          .map((text, index) => (
            <p key={index} style={{ textAlign: "center" }}>
              {text}
            </p>
          ))}
      </Card>
    );
  }

  return (
    <React.Fragment>
      <div className="searchBox">
        <SearchAction text="SEARCH" handleActionClick={() => handlePopupAction("SEARCH")} />
        <FilterAction filterCount={filterCount} text="FILTER" handleActionClick={() => handlePopupAction("FILTER")} />
        {/* <FilterAction text="SORT" handleActionClick={handlePopupAction} /> */}
      </div>
      {result}
      {popup && (
        <PopUp>
          <div className="popup-module">{selectedComponent}</div>
        </PopUp>
      )}
    </React.Fragment>
  );
};
