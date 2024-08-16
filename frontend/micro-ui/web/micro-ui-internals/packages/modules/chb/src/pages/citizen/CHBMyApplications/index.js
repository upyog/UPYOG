import React, { useState, useEffect } from "react";
import { Header, Loader, TextInput, Dropdown, SubmitBar, CardLabel, Card } from "@nudmcdgnpm/digit-ui-react-components";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import ChbApplication from "./chb-application";

export const CHBMyApplications = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const user = Digit.UserService.getUser().info;

  const [searchTerm, setSearchTerm] = useState("");
  const [status, setStatus] = useState(null);
  const [filteredApplications, setFilteredApplications] = useState([]);
  const [applicationsList, setApplicationsList] = useState([]);

  let filter = window.location.href.split("/").pop();
  let t1;
  let off;
  if (!isNaN(parseInt(filter))) {
    off = filter;
    t1 = parseInt(filter) + 50;
  } else {
    t1 = 4;
  }
  let filter1 = !isNaN(parseInt(filter))
    ? { limit: "50", sortOrder: "ASC", sortBy: "createdTime", offset: off, tenantId }
    : { limit: "4", sortOrder: "ASC", sortBy: "createdTime", offset: "0", mobileNumber: user?.mobileNumber, tenantId };

  const { isLoading, isError, error, data } = Digit.Hooks.chb.useChbSearch({ filters: filter1 }, { filters: filter1 });

  const { hallsBookingApplication } = data || {};

  const handleSearch = () => {
    const lowerCaseSearchTerm = searchTerm.toLowerCase();

    const filtered = applicationsList?.filter(app => {
      const bookingNoMatch = searchTerm ? app.bookingNo.toLowerCase().includes(lowerCaseSearchTerm) : true;

      const statusMatch = status?.code
        ? (status.code === "BOOKED" ? app.bookingStatus === "BOOKED" :
           status.code === "BOOKING_CREATED" ? app.bookingStatus === "BOOKING_CREATED" : true)
        : true;

      return bookingNoMatch && statusMatch;
    }) || [];

    setFilteredApplications(filtered);
  };

  useEffect(() => {
    if (hallsBookingApplication) {
      // Filter applications based on status
      const filteredHallsBookingApplication = hallsBookingApplication.filter(app =>
        (status?.code === "BOOKED" && app.bookingStatus === "BOOKED") ||
        (status?.code === "BOOKING_CREATED" && app.bookingStatus === "BOOKING_CREATED") ||
        !status?.code
      );
  
      // Update filteredApplications and applicationsList
      setFilteredApplications(prevFilteredApplications => [
        ...prevFilteredApplications,
        ...filteredHallsBookingApplication
      ]);
  
      setApplicationsList(prevApplicationsList => [
        ...prevApplicationsList,
        ...filteredHallsBookingApplication
      ]);
    }
  }, [hallsBookingApplication, status]);

  if (isLoading) {
    return <Loader />;
  }

  const statusOptions = [
    { i18nKey: "Booked", code: "BOOKED", value: t("CHB_BOOKED") },
    { i18nKey: "Booking in Progres", code: "BOOKING_CREATED", value: t("CHB_BOOKING_IN_PROGRES") }
  ];

  return (
    <React.Fragment>
      <Header>{`${t("CHB_MY_APPLICATION_HEADER")} ${applicationsList ? `(${applicationsList.length})` : ""}`}</Header>
      <Card>
        <div style={{ marginLeft: "16px" }}>
          <div style={{ display: "flex", flexDirection: "row", alignItems: "center", gap: "16px" }}>
            <div style={{ flex: 1 }}>
              <div style={{ display: "flex", flexDirection: "column" }}>
                <CardLabel>{t("CHB_BOOKING_NO")}</CardLabel>
                <TextInput
                  placeholder={t("Enter Booking No.")}
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  style={{ width: "100%", padding: "8px", height: '150%' }}
                />
              </div>
            </div>
            <div style={{ flex: 1 }}>
              <div style={{ display: "flex", flexDirection: "column" }}>
                <CardLabel>{t("PT_COMMON_TABLE_COL_STATUS_LABEL")}</CardLabel>
                <Dropdown
                  className="form-field"
                  selected={status}
                  select={setStatus}
                  option={statusOptions}
                  placeholder={t("Select Status")}
                  optionKey="value"
                  style={{ width: "100%" }}
                  t={t}
                />
              </div>
            </div>
            <div>
              <SubmitBar label={t("ES_COMMON_SEARCH")} onSubmit={handleSearch} />
            </div>
          </div>
          <span className="link" style={{ display: "block" }}>
            <Link to="/digit-ui/citizen/chb/bookHall/searchhall">
              {t("CHB_NEW_BOOKING")}
            </Link>
          </span>
        </div>
      </Card>
      <div>
        {filteredApplications.length > 0 &&
          filteredApplications.map((application, index) => (
            <div key={index}>
              <ChbApplication
                application={application}
                tenantId={user?.permanentCity}
                buttonLabel={t("CHB_SUMMARY")}
              />
            </div>
          ))}
        {filteredApplications.length === 0 && !isLoading && (
          <p style={{ marginLeft: "16px", marginTop: "16px" }}>
            {t("CHB_NO_APPLICATION_FOUND_MSG")}
          </p>
        )}

        {filteredApplications.length !== 0 && (
          <div>
            <p style={{ marginLeft: "16px", marginTop: "16px" }}>
              <span className="link">
                <Link to={`/digit-ui/citizen/chb/myBookings/${t1}`}>
                  {t("CHB_LOAD_MORE_MSG")}
                </Link>
              </span>
            </p>
          </div>
        )}
      </div>
    </React.Fragment>
  );
};
