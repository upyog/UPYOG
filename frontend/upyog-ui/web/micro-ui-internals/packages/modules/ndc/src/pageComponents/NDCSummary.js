import React, { useEffect, useState, useRef } from "react";
import {
  CardLabel,
  ActionBar,
  SubmitBar,
  Menu,
} from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import NDCDocument from "../components/NDCDocument";
import Timeline from "../components/NDCTimeline";

// This component is the summary page for the NDC application. 
// It displays all the details entered by the user in the previous steps and allows them to review before submission.
//  It also provides an option to go back and edit the details if needed. The user can also take actions based on the workflow state of the application.
const NDCSummary = ({ formData, goNext, onGoBack }) => {
  const { t } = useTranslation();
  const menuRef = useRef();
  let user = Digit.UserService.getUser();

  let docs = formData?.DocummentDetails?.documents?.documents;
  const isCitizen = !user?.info?.type || user?.info?.type === "CITIZEN";


  const appId = formData?.apiData?.Applications?.[0]?.applicationNo || formData?.responseData?.[0]?.applicationNo;

  const propertyDet = formData?.apiData?.Applications?.[0]?.NdcDetails || formData?.responseData?.[0]?.NdcDetails;

  const filterType = propertyDet?.filter((item) => item?.businessService == "PT");


  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();


  const [getData, setData] = useState();
  const [displayMenu, setDisplayMenu] = useState(false);

  const closeMenu = () => {
    setDisplayMenu(false);
  };

  Digit.Hooks.useClickOutside(menuRef, closeMenu, displayMenu);

  const fetchCalculations = async () => {
    const payload = {
      CalculationCriteria: [
        {
          applicationNumber: appId,
          tenantId: tenantId,
          propertyType: filterType?.[0]?.additionalDetails?.propertyType,
        },
      ],
    };
    const response = await Digit.NDCService.NDCCalculator({ tenantId, filters: { getCalculationOnly: true }, details: payload });
    setData(response?.Calculation?.[0]);
  };

  useEffect(() => {
    fetchCalculations();
  }, []);

  const workflowDetails = Digit.Hooks.useWorkflowDetails({
    tenantId: tenantId,
    id: appId,
    moduleCode: "ndc-services",
  });

  const userRoles = user?.info?.roles?.map((e) => e.code);
  let actions =
    workflowDetails?.data?.actionState?.nextActions?.filter((e) => {
      return userRoles?.some((role) => e.roles?.includes(role)) || !e.roles;
    }) ||
    workflowDetails?.data?.nextActions?.filter((e) => {
      return userRoles?.some((role) => e.roles?.includes(role)) || !e.roles;
    });

  function onActionSelect(action) {
    goNext(action);
  }

  const documentCardStyle = {
    flex: isCitizen ? "1 1 18%" : "1 1 22%", // around 4 per row
    minWidth: "200px", // keeps it from shrinking too small
    maxWidth: "250px", // prevents oversized stretching on big screens
    backgroundColor: "#fdfdfd",
    padding: "0.75rem",
    border: "1px solid #e0e0e0",
    borderRadius: "6px",
    boxShadow: "0 1px 3px rgba(0,0,0,0.05)",
  };

  const boldLabelStyle = { fontWeight: "bold", color: "#555" };

  const renderLabel = (label, value) => (
    <div className="bpa-summary-label-field-pair">
      <CardLabel className="bpa-summary-bold-label" style={{ width: "auto" }}>
        {label}
      </CardLabel>

      <div>{value || "NA"}</div>
    </div>
  );

  return (
    <div>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={3} /> : null}
    <div className="bpa-summary-page">
      <h2 className="bpa-summary-heading">{t("Application Summary")}</h2>

      {/* Property Details Section */}
      <div className="bpa-summary-section">
        {renderLabel(t("Full Name"), formData?.NDCDetails?.PropertyDetails?.firstName)}
        {/* {renderLabel(t("Last Name"), formData?.NDCDetails?.PropertyDetails?.lastName)} */}
        {renderLabel(t("Mobile Number"), formData?.NDCDetails?.PropertyDetails?.mobileNumber)}
        {renderLabel(t("Email ID"), formData?.NDCDetails?.PropertyDetails?.email)}
        {renderLabel(t("Address"), formData?.NDCDetails?.PropertyDetails?.address)}
        {/* {renderLabel(t("NDC Reason"), t(formData?.NDCDetails?.NDCReason?.i18nKey))} */}
        {formData?.NDCDetails?.NDCReason?.i18nKey === "OTHERS"
          ? renderLabel(t("NDC Reason"), t(formData?.NDCDetails?.NDCReason?.reason))
          : renderLabel(t("NDC Reason"), t(formData?.NDCDetails?.NDCReason?.i18nKey))}
        {renderLabel(t("Remarks"), formData?.NDCDetails?.PropertyDetails?.remarks)}

        {renderLabel(
          t("Water Connection"),
          formData?.NDCDetails?.PropertyDetails?.waterConnection?.length > 0
            ? formData?.NDCDetails?.PropertyDetails?.waterConnection?.map((item, index) => <div key={index}>{item?.connectionNo}</div>)
            : "NA"
        )}

        {renderLabel(
          t("Sewerage Connection"),
          formData?.NDCDetails?.PropertyDetails?.sewerageConnection?.length > 0
            ? formData?.NDCDetails?.PropertyDetails?.sewerageConnection?.map((item, index) => <div key={index}>{item?.connectionNo}</div>)
            : "NA"
        )}

        {renderLabel(t("Property ID"), formData?.NDCDetails?.cpt?.id)}
        {renderLabel(
          t("Application Fees"),
          getData?.totalAmount ? new Intl.NumberFormat("en-IN", { style: "currency", currency: "INR" }).format(getData?.totalAmount) : "NA"
        )}
      </div>

      {/* Documents Section */}
      {/* Documents Section */}
      <h2 className="bpa-summary-heading">{t("Documents Uploaded")}</h2>
      <div className="bpa-summary-section">
        {docs?.length > 0 ? (
          <div className="ndc-doc-view-comp">
            {docs?.map((doc, index) => (
              <div key={index} style={documentCardStyle}>
                <NDCDocument value={docs} Code={doc?.documentType} index={index} formData={formData} />
              </div>
            ))}
          </div>
        ) : (
          <div>{t("TL_NO_DOCUMENTS_MSG")}</div>
        )}
      </div>

      {/* Action Section */}
      <ActionBar>
        <SubmitBar className="submit-bar-back" label="Back" onSubmit={onGoBack} />
        {displayMenu && (workflowDetails?.data?.actionState?.nextActions || workflowDetails?.data?.nextActions) ? (
          <Menu localeKeyPrefix={`WF_EMPLOYEE_${"NDC"}`} options={actions} optionKey={"action"} t={t} onSelect={onActionSelect} />
        ) : null}
        <SubmitBar ref={menuRef} label={t("WF_TAKE_ACTION")} onSubmit={() => setDisplayMenu(!displayMenu)} />
      </ActionBar>
    </div>
    </div>
  );
};

export default NDCSummary;
