import React from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import EditForm from "./EditForm";
import { ComplaintDetails } from "../ComplaintDetails";

const EditApplication = () => {
    const { t } = useTranslation();
    let { id } = useParams();

    let url=window.location.href
    let applicationNumber=url.split("application/")[1];   
    const tenantId = Digit.ULBService.getCurrentTenantId();
    id=applicationNumber;
  const { isLoading, complaintDetails } = Digit.Hooks.pgr.useComplaintDetails({ tenantId, id });
  
  return complaintDetails && !isLoading ? <EditForm applicationData={complaintDetails?.service  } details={complaintDetails?.details} complaintDetails={complaintDetails} /> : null;
};
export default EditApplication;