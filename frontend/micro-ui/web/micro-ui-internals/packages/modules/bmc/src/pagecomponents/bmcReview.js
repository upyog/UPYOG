import React, { useCallback, useState } from "react";
import { useLocation, useHistory } from "react-router-dom";
import { useTranslation } from "react-i18next";
import AddressDetailCard from "../components/AddressDetails";
import BankDetails from "../components/BankDetails";
import DisabilityCard from "../components/DisabilityCard";
import PersonalDetailCard from "../components/PersonalDetails";
import QualificationCard from "../components/QualificationCard";
import WorkflowActions from "../components/Workflow";
import Timeline from "../components/bmcTimeline";
import Title from "../components/title";
import SchemeDetailsPage from "../components/schemeDetails";
import { Modal } from "@egovernments/digit-ui-react-components";

const BMCReviewPage = ({}) => {
  const location = useLocation();
  const history = useHistory();
  const { t } = useTranslation();
  const { scheme, schemeType, updateSchemeData, selectedScheme } = location.state || {};
  const userDetails = Digit.UserService.getUser();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [userDetail, setUserDetail] = useState({});
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [applicationNumber, setApplicationNumber] = useState(null);

  const userFunction = (data) => {
    if (data && data.UserDetails && data.UserDetails.length > 0) {
      setUserDetail(data.UserDetails[0]);
    }
  };

  const getUserDetails = { UserSearchCriteria: { Option: "full", TenantID: tenantId, UserID: userDetails?.info?.id } };
  Digit.Hooks.bmc.useUsersDetails(getUserDetails, { select: userFunction });

  const saveSchemeDetail = Digit.Hooks.bmc.useSaveSchemes();

  const handleSaveData = useCallback(() => {
    const data = { SchemeApplications: { scheme, schemeType, updateSchemeData } };
    saveSchemeDetail.mutate(data, {
      onSuccess: (response) => {
        const applicationNumber = response?.userSchemeApplication?.applicationNumber;
        if (applicationNumber) {
          setApplicationNumber(applicationNumber);
        }
        setIsModalOpen(true);
      },
      onError: (error) => {
        console.error("Failed to save user details:", error);
      },
    });
  }, [updateSchemeData, scheme, schemeType, saveSchemeDetail]);

  const handleCallback = useCallback((data) => {
    console.log(data);
  }, []);

  console.log("userDetail", userDetail);

  // const closeModal = () => {
  //   setIsModalOpen(false);
  //   history.push("/digit-ui/citizen/bmc/review");
  //   window.location.reload();
  // };

  return (
    <React.Fragment>
      <div className="bmc-card-full">
        {window.location.href.includes("/citizen") ? <Timeline currentStep={4} /> : null}
        <Title text={"Review Application"} />
        <PersonalDetailCard onUpdate={handleCallback} initialRows={userDetail} tenantId={tenantId} AllowEdit={false} />
        <AddressDetailCard onUpdate={handleCallback} initialRows={userDetail.address} tenantId={tenantId} AllowEdit={false} />
        <QualificationCard
          initialRows={userDetail.UserQualification}
          tenantId={tenantId}
          AddOption={false}
          AllowRemove={false}
          onUpdate={handleCallback}
        />
        <BankDetails onUpdate={handleCallback} initialRows={userDetail.bankDetail} tenantId={tenantId} AddOption={false} AllowRemove={false} />
        <DisabilityCard onUpdate={handleCallback} initialRows={userDetail.divyang} tenantId={tenantId} AllowEdit={false} />
        <SchemeDetailsPage
          onUpdate={handleCallback}
          initialRows={updateSchemeData || userDetail}
          tenantId={tenantId}
          AllowEdit={false}
          scheme={scheme}
          schemeType={schemeType}
          selectedScheme={selectedScheme}
        />
        <div className="bmc-card-row" style={{ textAlign: "end" }}>
          <button
            type="submit"
            className="bmc-card-button"
            style={{
              borderBottom: "3px solid black",
              outline: "none",
            }}
            onClick={handleSaveData}
          >
            {t("BMC_Save")}
          </button>
        </div>
        <WorkflowActions
          ActionBarStyle={{}}
          MenuStyle={{}}
          businessService={"bmc-schemes"}
          applicationNo={"MH-0003"}
          moduleCode={"BMC"}
          tenantId={tenantId}
        />
      </div>
      {isModalOpen && (
        <div className="bmc-modal">
          <Modal fullScreen hideSubmit={true}>
            <p style={{ fontSize: "15px" }}>
              <strong>Application Submitted Successfully</strong>
            </p>
            {applicationNumber && <p>Your Application Number is: {applicationNumber}</p>}
            <button>ok</button>
          </Modal>
        </div>
      )}
    </React.Fragment>
  );
};

export default BMCReviewPage;
