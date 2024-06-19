import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useRouteMatch } from "react-router-dom";
import { Card, Header, Loader, TextInput, Button, ButtonSelector } from "@upyog/digit-ui-react-components";
import Complaint from "./Complaint";

export const ComplaintsList = (props) => {
  const [searchInput, setSearchInput] = useState("");
  const [filteredComplaints, setFilteredComplaints] = useState(null);
  const [enableSarch, setEnableSearch] = useState(false);
  const [serviceRequestId, setServiceRequestId] = useState("");
  const User = Digit.UserService.getUser();
  const mobileNumber = User.mobileNumber || User?.info?.mobileNumber || User?.info?.userInfo?.mobileNumber;
  const tenantId = Digit.SessionStorage.get("CITIZEN.COMMON.HOME.CITY")?.code || Digit.ULBService.getCurrentTenantId();
  const { t } = useTranslation();
  const { path, url } = useRouteMatch();
  let { isLoading, error, data, revalidate } = Digit.Hooks.pgr.useComplaintsListByServiceRequest(tenantId, serviceRequestId);
  const LOCALE = {
    MY_COMPLAINTS: "CS_HOME_MY_COMPLAINTS",
    NO_COMPLAINTS: "CS_MYCOMPLAINTS_NO_COMPLAINTS",
    NO_COMPLAINTS_EMPLOYEE: "CS_MYCOMPLAINTS_NO_COMPLAINTS_EMPLOYEE",
    ERROR_LOADING_RESULTS: "CS_COMMON_ERROR_LOADING_RESULTS",
  };

  useEffect(() => {
    revalidate();
  }, []);

  const handleSearch = (e) => {
    e.preventDefault()
    setServiceRequestId(searchInput)
    setEnableSearch(true)

  };

  if (isLoading) {
    return (
      <React.Fragment>
        <Header>{t(LOCALE.MY_COMPLAINTS)}</Header>
        <Loader />
      </React.Fragment>
    );
  }

  let complaints =  data?.ServiceWrappers;
  console.log("complaints",complaints)
  let complaintsList;
  if (error) {
    complaintsList = (
      <Card>
        {t(LOCALE.ERROR_LOADING_RESULTS)
          .split("\\n")
          .map((text, index) => (
            <p key={index} style={{ textAlign: "center" }}>
              {text}
            </p>
          ))}
      </Card>
    );
  } else if (!complaints || complaints.length === 0) {
    complaintsList = (
      <Card>
        {t(LOCALE.NO_COMPLAINTS)
          .split("\\n")
          .map((text, index) => (
            <p key={index} style={{ textAlign: "center" }}>
              {text}
            </p>
          ))}
      </Card>
    );
  } else {
    complaintsList = complaints.map(({ service }, index) => <Complaint key={index} data={service} path={path} />);
  }

  return (
    <React.Fragment>
      <div className="applications-list-container" style={{marginLeft:"10px"}}>
        <Header>{t(LOCALE.MY_COMPLAINTS)}</Header>
        <div style={{ display: "flex", marginBottom: "16px" }}>
          <TextInput
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
            placeholder={t("Search by service request ID")}
            style={{ marginRight: "8px" }}
          />
          <ButtonSelector label="Search" onSubmit={(e) =>handleSearch(e)} style={{marginLeft:"10px"}}/>
        </div>
        {complaintsList}
      </div>
    </React.Fragment>
  );
};
