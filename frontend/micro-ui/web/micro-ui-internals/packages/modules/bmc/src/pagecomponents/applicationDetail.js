import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useLocation, useHistory } from "react-router-dom";
import SchemeDetailsPage from "../components/schemeDetails";
import Timeline from "../components/bmcTimeline";
import Title from "../components/title";

const ApplicationDetailFull = (_props) => {
  const { t } = useTranslation();
  const location = useLocation();
  const history = useHistory();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const userDetails = Digit.UserService.getUser();
  const { scheme, schemeType,selectedScheme } = location.state || {};

  const [userDetail, setUserDetail] = useState({});
  const [updateSchemeData, setUpdateSchemeData] = useState({});

  const userFunction = (data) => {
    if (data && data.UserDetails && data.UserDetails.length > 0) {
      setUserDetail(data.UserDetails[0]);
    }
  };

  const getUserDetails = { UserSearchCriteria: { Option: "full", TenantID: tenantId, UserID: userDetails?.info?.id } };
  Digit.Hooks.bmc.useUsersDetails(getUserDetails, { select: userFunction });

  const handleApplicationUpdate = (updatedData) => {
    setUpdateSchemeData(updatedData);
  };

  const handleConfirmClick = () => {
    history.push({
      pathname: "/digit-ui/citizen/bmc/review",
      state: {
        scheme,
        schemeType,
        selectedScheme,
        updateSchemeData,
      },
    });
  };

  return (
    <React.Fragment>
      <div className="bmc-card-full">
        {window.location.href.includes("/citizen") && <Timeline currentStep={4} />}
        <Title text={`Application for ${scheme?.label}`} />

        <SchemeDetailsPage
          onUpdate={handleApplicationUpdate}
          initialRows={userDetail}
          AllowEdit={true}
          tenantId={tenantId}
          scheme={scheme}
          selectedScheme={selectedScheme}
          schemeType={schemeType}
        />
        <div className="bmc-card-row" style={{ textAlign: "end" }}>
          <button
            type="submit"
            className="bmc-card-button"
            style={{
              borderBottom: "3px solid black",
              outline: "none",
            }}
            onClick={handleConfirmClick}
          >
            {t("BMC_Confirm")}
          </button>
        </div>
      </div>
    </React.Fragment>
  );
};

export default ApplicationDetailFull;
