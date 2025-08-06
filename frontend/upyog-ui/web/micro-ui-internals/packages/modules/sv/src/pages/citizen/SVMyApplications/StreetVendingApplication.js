import { Toast, Card, KeyNote, SubmitBar } from "@upyog/digit-ui-react-components";
import React,{ useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Link,useHistory } from "react-router-dom";

const StreetVendingApplication = ({ application, buttonLabel,previousDraftId,onDiscard }) => {
  const { t } = useTranslation();
  const history = useHistory();
  const [showToast, setShowToast] = useState(null);
  //TODO: Need to remove all session storage from here and get the data from Search API call
  const handleEditClick = () => {
    sessionStorage.setItem("vendingApplicationID", application?.applicationNo);
    sessionStorage.setItem("ApplicationId",application?.applicationId);
    sessionStorage.setItem("applicationStatus",application?.applicationStatus);
    sessionStorage.setItem("addressIdOne",application?.addressDetails?.[0]?.addressId);
    sessionStorage.setItem("addressIdTwo",application?.addressDetails?.[1]?.addressId);
    sessionStorage.setItem("vendorIds",application?.addressDetails?.[0]?.vendorId);
    sessionStorage.setItem("bankIds",application?.bankDetail?.id);
    sessionStorage.setItem("venId",application?.vendorDetail?.[0]?.id);
    history.push(`/upyog-ui/citizen/sv/edit`);
  };

  useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => {
        setShowToast(null);
      }, 2000); // Close toast after 1.5 seconds

      return () => clearTimeout(timer); // Clear timer on cleanup
    }
  }, [showToast]);

  const handleDiscard = async () => {
    try {
      await Digit.SVService.deleteDraft({
        filters: {  
          draftId: previousDraftId
        }
      });
      setShowToast({ error: false, label: t("SV_DISCARD_MESSAGE") });
      if (onDiscard) {
        onDiscard(application);
      }
    } catch (error) {
      console.error("Error discarding application:", error);
      setShowToast({ error: true, label: t("SV_DISCARD_ERROR") });
    }
  };
  const isDraft = !application?.applicationNo?.length;


  return (
    <Card>
      <KeyNote keyValue={t("SV_APPLICATION_NUMBER")} note={application?.applicationNo} />
      <KeyNote keyValue={t("SV_VENDOR_NAME")} note={application?.vendorDetail?.[0]?.name} />
      <KeyNote keyValue={t("SV_VENDING_TYPE")} note={application?.vendingActivity} />
      <KeyNote keyValue={t("SV_VENDING_ZONES")} note={application?.vendingZone} />
      {application?.vendingActivity==="STATIONARY"&&(
      <KeyNote keyValue={t("SV_AREA_REQUIRED")} note={application?.vendingArea} />)}
      {(application?.applicationStatus == "CITIZENACTIONREQUIRED") && 
      <SubmitBar style={{ marginBottom: "5px" }} label={t("SV_EDIT")} onSubmit={handleEditClick} />}
      <div style={{ display: "flex", gap: "5px" }}>
        <Link to={isDraft ? `/upyog-ui/citizen/sv/apply/info` : `/upyog-ui/citizen/sv/application/${application?.applicationNo}/${application?.tenantId}`}>
          <SubmitBar label={isDraft ?t("SV_CONTINUE"):buttonLabel} />
        </Link>
        {isDraft && <SubmitBar label={t("SV_DISCARD")} onSubmit={handleDiscard} />}
      </div>
      <div>
      {showToast && (
        <Toast
          error={showToast.error}
          warning={showToast.warning}
          label={t(showToast.label)}
          onClose={() => {
            setShowToast(null);
          }}
        />
      )}
      </div>
    </Card>
  );
};

export default StreetVendingApplication;
