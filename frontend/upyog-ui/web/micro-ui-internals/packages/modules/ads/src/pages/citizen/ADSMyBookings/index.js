import React, { useState, useEffect } from "react";
import { Header, Loader, TextInput, Dropdown, SubmitBar, CardLabel, Card } from "@upyog/digit-ui-react-components";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import AdsApplication from "./ads-application";

/*
 * ADSMyApplications component manages and displays a user's advertisement applications.
 * It allows users to search applications by booking number and filter by status. The component fetches application data and handles
 * pagination for displaying multiple applications. Users can also clear filters and see
 * the number of applications they have.
 */
import "../../../css/ads-inline-auto.css";
export const ADSMyApplications = () => {
  const {
    t
  } = useTranslation();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const user = Digit.UserService.getUser().info;
  const [searchTerm, setSearchTerm] = useState("");
  const [status, setStatus] = useState(null);
  const [filters, setFilters] = useState(null);
  let filter = window.location.href.split("/").pop();
  let t1;
  let off;
  if (!isNaN(parseInt(filter))) {
    off = filter;
    t1 = parseInt(filter) + 50;
  } else {
    t1 = 4;
  }
  let initialFilters = !isNaN(parseInt(filter)) ? {
    limit: "50",
    sortOrder: "ASC",
    sortBy: "createdTime",
    offset: off,
    tenantId
  } : {
    limit: "4",
    sortOrder: "ASC",
    sortBy: "createdTime",
    offset: "0",
    tenantId,
    mobileNumber: user?.mobileNumber
  };
  useEffect(() => {
    setFilters(initialFilters);
  }, [filter]);

  // Use the search hook with dynamic filters
  const {
    isLoading,
    data
  } = Digit.Hooks.ads.useADSSearch({
    filters
  });
  const handleSearch = () => {
    const trimmedSearchTerm = searchTerm.trim();
    const searchFilters = {
      ...initialFilters,
      bookingNo: trimmedSearchTerm || undefined,
      status: status?.code || undefined
    };

    // Update the filters state to trigger refetch
    setFilters(searchFilters);
  };
  if (isLoading) {
    return <Loader />;
  }
  const statusOptions = [{
    i18nKey: "Booked",
    code: "BOOKED",
    value: t("ADS_BOOKED")
  }, {
    i18nKey: "Booking in Progres",
    code: "BOOKING_CREATED",
    value: t("ADS_BOOKING_IN_PROGRES")
  }, {
    i18nKey: "Pending For Payment",
    code: "PENDING_FOR_PAYMENT",
    value: t("PENDING_FOR_PAYMENT")
  }, {
    i18nKey: "Booking Expired",
    code: "BOOKING_EXPIRED",
    value: t("BOOKING_EXPIRED")
  }, {
    i18nKey: "Cancelled",
    code: "CANCELLED",
    value: t("CANCELLED")
  }];
  const filteredApplications = data?.bookingApplication || [];
  return <React.Fragment>
      <Header>{`${t("ADS_MY_BOOKINGS_HEADER")} (${filteredApplications.length})`}</Header>
      <Card>
        <div className="ads-auto-80">
          <div className="ads-auto-81">
            <div className="ads-auto-82">
              <div className="ads-auto-83">
                <CardLabel>{t("ADS_BOOKING_NO")}</CardLabel>
                <TextInput placeholder={t("Enter Booking No.")} value={searchTerm} onChange={e => setSearchTerm(e.target.value)} className="ads-auto-84" />
              </div>
            </div>
            <div className="ads-auto-85">
              <div className="ads-auto-86">
                <CardLabel>{t("PT_COMMON_TABLE_COL_STATUS_LABEL")}</CardLabel>
                <Dropdown className="form-field ads-auto-87" selected={status} select={setStatus} option={statusOptions} placeholder={t("Select Status")} optionKey="value" t={t} />
              </div>
            </div>
            <div>
              <div className="ads-auto-88">
                <SubmitBar label={t("ES_COMMON_SEARCH")} onSubmit={handleSearch} />
                <p className="link ads-auto-89" onClick={() => {
                setSearchTerm(""), setStatus("");
              }}>
                  {t(`ES_COMMON_CLEAR_ALL`)}
                </p>
              </div>
            </div>
          </div>
          <Link to="/upyog-ui/citizen/ads/bookad/searchads">
              <SubmitBar label={t("ADS_NEW_BOOKING") + " +"} className="ads-auto-90" />
            </Link>
        </div>
      </Card>
      <div>
        {filteredApplications.length > 0 && filteredApplications.map((application, index) => <div key={index}>
              <AdsApplication application={application} tenantId={tenantId} buttonLabel={t("ADS_SUMMARY")} />
            </div>)}
        {filteredApplications.length === 0 && !isLoading && <p className="ads-auto-91">{t("ADS_NO_APPLICATION_FOUND_MSG")}</p>}

        {filteredApplications.length !== 0 && data?.count > t1 && <div>
            <p className="ads-auto-92">
              <span className="link">
                <Link to={`/upyog-ui/citizen/ads/myBookings/${t1}`}>{t("ADS_LOAD_MORE_MSG")}</Link>
              </span>
            </p>
          </div>}
      </div>
    </React.Fragment>;
};
