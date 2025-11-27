import React from "react";
import { useTranslation } from "react-i18next";
import { Loader } from "@nudmcdgnpm/digit-ui-react-components";
import { ApplicationCard } from "./inbox/ApplicationCard";
import ApplicationLinks from "./inbox/ApplicationLinks";
import PropTypes from "prop-types";

/**
 * MobileInbox Component
 *
 * Renders a mobile-friendly view of complaints/applications inbox.
 * Handles loading state, localization, search and filter logic.
 *
 * Props:
 * - data: Array of complaint objects from search response
 * - onFilterChange: Callback to update filters
 * - onSearch: Callback to trigger search
 * - isLoading: Boolean indicating if data is being loaded
 * - searchParams: Current search/filter params
 */

const MobileInbox = ({ data, onFilterChange, onSearch, isLoading, searchParams }) => {
  const { t } = useTranslation();
  const localizedData = data?.map((items) => (
    {

    [t("CS_COMMON_COMPLAINT_NO")]: items?.searchData?.service?.serviceRequestId,
    [t("CS_ADDCOMPLAINT_COMPLAINT_SUB_TYPE")]: t(`SERVICEDEFS.${items?.searchData?.service?.serviceCode.toUpperCase()}`),
    [t("WF_INBOX_HEADER_LOCALITY")]: t(Digit.Utils.locale.getLocalityCode(items?.searchData?.service?.address?.locality, items?.searchData?.service?.tenantId)),
    [t("CS_COMPLAINT_DETAILS_CURRENT_STATUS")]: t(`CS_COMMON_${items?.searchData?.service?.applicationStatus}`),
  }));

  let result;
  if (isLoading) {
    result = <Loader />;
  } else {
    result = (
      <ApplicationCard
        data={localizedData}
        onFilterChange={onFilterChange}
        serviceRequestIdKey={t("CS_COMMON_COMPLAINT_NO")}
        onSearch={onSearch}
        searchParams={searchParams}
      />
    );
  }

  return (
    <div style={{ padding: 0 }}>
      <div className="inbox-container">
        <div className="filters-container">
          <ApplicationLinks isMobile={true} />
          {result}
        </div>
      </div>
    </div>
  );
};
MobileInbox.propTypes = {
  data: PropTypes.any,
  onFilterChange: PropTypes.func,
  onSearch: PropTypes.func,
  isLoading: PropTypes.bool,
  searchParams: PropTypes.any,
};

MobileInbox.defaultProps = {
  onFilterChange: () => {},
  searchParams: {},
};

export default MobileInbox;
