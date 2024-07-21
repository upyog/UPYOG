import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import AddressDetailCard from "../components/AddressDetails";
import BankDetails from "../components/BankDetails";
import Timeline from "../components/bmcTimeline";
import DisabilityCard from "../components/DisabilityCard";
import PersonalDetailCard from "../components/PersonalDetails";
import QualificationCard from "../components/QualificationCard";
import Title from "../components/title";

const AadhaarFullFormPage = (_props) => {
  const { formData, config } = _props;
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const userDetails = Digit.UserService.getUser();
  const { t } = useTranslation();
  const history = useHistory();

  const [userDetail, setUserDetail] = useState({});


  const userFunction = (data) => {
    if (data && data.UserDetails && data.UserDetails.length > 0) {
      setUserDetail(data.UserDetails[0]);
    }
  };

  const getUserDetails = { UserSearchCriteria: { Option: "full", TenantID: tenantId, UserID: userDetails?.info?.id } };
  Digit.Hooks.bmc.useUsersDetails(getUserDetails, { select: userFunction });


  const handleQualificationsUpdate = (updatedQualifications) => {
    console.log(updatedQualifications);
  };

  const handlePersonalDetailUpdate = (updatedPersonalDetails) => {
    console.log(updatedPersonalDetails);
  };

  const handleDisabilityUpdate = (updatedDisability) => {
    console.log(updatedDisability);
  };

  const handleAddressUpdate = (updatedAddress) => {
    console.log(updatedAddress);
  };

  const goNext = () => {
    history.push("/digit-ui/citizen/bmc/selectScheme");
  };

  return (
    <React.Fragment>
      <div className="bmc-card-full">
        {window.location.href.includes("/citizen") ? <Timeline currentStep={2} /> : null}
        <Title text={"Applicant Details"} />
        <PersonalDetailCard
          onUpdate={handlePersonalDetailUpdate}
          initialRows={userDetail}
          tenantId={tenantId}
          AllowEdit={true}
        />
        <AddressDetailCard
          onUpdate={handleAddressUpdate}
          initialRows={userDetail.address}
          tenantId={tenantId}
          AllowEdit={true}
        />
        <QualificationCard
          onUpdate={handleQualificationsUpdate}
          initialRows={userDetail.qualificationDetails}
          tenantId={tenantId}
          AddOption={true}
          AllowRemove={true}
        />
        <BankDetails
          initialRows={userDetail.bankDetail}
          tenantId={tenantId}
          AddOption={true}
          AllowRemove={true}
        />
        <DisabilityCard
          onUpdate={handleDisabilityUpdate}
          initialRows={userDetail.divyang}
          tenantId={tenantId}
          AllowEdit={true}
        />
        <div className="bmc-card-row" style={{ textAlign: "end" }}>
          <button
            className="bmc-card-button"
            onClick={goNext}
            style={{ borderBottom: "3px solid black", marginRight: "1rem" }}
          >
            {t("BMC_Confirm")}
          </button>
          <button
            className="bmc-card-button-cancel"
            onClick={() => history.push("/bmc/dashboard()")}
            style={{ borderBottom: "3px solid black", outline: "none", marginRight: "5rem" }}
          >
            {t("BMC_Cancel")}
          </button>
        </div>
      </div>
    </React.Fragment>
  );
};

export default AadhaarFullFormPage;
