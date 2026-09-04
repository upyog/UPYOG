/**
 * @file ApplicationDetails.js
 * 
 * @description
 * This component displays the details of a firenoc application. It allows employees to view data
 * 
 * @features
 * - Displays firenoc application details using the `ApplicationDetailsTemplate` component.
 * - Retrieves workflow details and handles business service changes dynamically.
 * - Supports audit data retrieval and error handling.
 * - Displays a renewal link for expired applications.

 */

import { Header, MultiLink, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import _ from "lodash";
import React, { useContext, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams, Link,  } from "react-router-dom";
import ApplicationDetailsTemplate from "../../../../templates/ApplicationDetails";



const ApplicationDetails = () => {
  const { t } = useTranslation();
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { tenants } = storeData || {};
  const { id: applicationNumber } = useParams();
  const [showToast, setShowToast] = useState(null);
  const [showOptions, setShowOptions] = useState(false);
  const [enableAudit, setEnableAudit] = useState(false);
  const [businessService, setBusinessService] = useState("FIRENOC");

  const navigate = Digit.Hooks.useCustomNavigate();

  const { isLoading, isError, data: applicationDetails, error } = Digit.Hooks.noc.useFireNocDetails(t, tenantId, applicationNumber);

  const {mutate} = Digit.Hooks.noc.useFireNocApplicationAction(tenantId);

  let workflowDetails = Digit.Hooks.useWorkflowDetails({
    tenantId: tenantId,
    id: applicationDetails?.applicationData?.applicationData?.applicationNumber,
    moduleCode: businessService,
    role: "NOC_CEMP",
  });

  const { data: reciept_data, isLoading: recieptDataLoading } = Digit.Hooks.useRecieptSearch(
    {
      tenantId: tenantId,
      businessService: "FIRENOC",
      consumerCodes: applicationDetails?.applicationData?.applicationData?.applicationNumber,
      isEmployee: false,
    },
    { enabled: applicationDetails?.applicationData?.applicationData?.applicationNumber ? true : false }
  );


  const closeToast = () => {
    setShowToast(null);
  };

  const NOC_CEMP = Digit.UserService.hasAccess(["NOC_CEMP"]) || false;
  if (
    NOC_CEMP &&
    workflowDetails?.data?.applicationBusinessService === "FIRENOC" &&
    workflowDetails?.data?.actionState?.nextActions?.find((act) => act.action === "PAY")
  ) {
    workflowDetails.data.actionState.nextActions = workflowDetails?.data?.actionState?.nextActions.map((act) => {
      if (act.action === "PAY") {
        return {
          action: "PAY",
          forcedName: "WF_PAY_APPLICATION",
          redirectionUrl: { pathname: `/upyog-ui/employee/payment/collect/FIRENOC/${applicationDetails?.applicationData?.applicationData?.applicationNumber}` },
        };
      }
      return act;
    });
  }

  async function getRecieptSearch({ tenantId, payments, ...params }) {
    let response = { filestoreIds: [payments?.fileStoreId] };
    response = await Digit.PaymentService.generatePdf(tenantId, { Payments: [{ ...payments }] }, "consolidatedreceipt");
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  };

  let dowloadOptions = [];

  if (reciept_data && reciept_data?.Payments.length > 0 && recieptDataLoading == false)
    dowloadOptions.push({
      label: t("FN_FEE_RECIEPT"),
      onClick: () => getRecieptSearch({ tenantId: reciept_data?.Payments[0]?.tenantId, payments: reciept_data?.Payments[0] }),
    });

    // function to generate certificate when Application status is Approved
    const printCertificate = async () => {
    let response = await Digit.PaymentService.generatePdf(tenantId, { FireNOCs: [applicationDetails?.applicationData?.applicationData] }, "firenoccertificate");
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  };

    if (applicationDetails?.applicationData?.applicationData?.fireNOCDetails?.status==="APPROVED")
    dowloadOptions.push({
      label: t("FN_CERTIFICATE"),
      onClick: () => printCertificate(),
    });

    return (
    <div>
      <div className={"employee-application-details"} style={{ marginBottom: "15px" }}>
        <Header styles={{ marginLeft: "0px", paddingTop: "10px", fontSize: "32px" }}>{t("FN_APPLICATION_DETAILS")}</Header>
        <div style={{zIndex: "10",display:"flex",flexDirection:"row-reverse",alignItems:"center",marginTop:"-25px"}}> 
        <div style={{zIndex: "10",  position: "relative"}}>
        {dowloadOptions && dowloadOptions.length > 0 && (
          <MultiLink
            className="multilinkWrapper"
            onHeadClick={() => setShowOptions(!showOptions)}
            displayOptions={showOptions}
            options={dowloadOptions}
            downloadBtnClassName={"employee-download-btn-className"}
            optionsClassName={"employee-options-btn-className"}
          />
        )}
      </div>
      </div>
      </div>

      <ApplicationDetailsTemplate
        isAction={false}
        applicationDetails={applicationDetails?.applicationData}
        isLoading={isLoading}
        isDataLoading={isLoading}
        applicationData={applicationDetails?.applicationData?.applicationData}
        mutate={mutate}
        workflowDetails={workflowDetails}
        businessService={businessService}
        moduleCode="fireNoc"
        showToast={showToast}
        setShowToast={setShowToast}
        closeToast={closeToast}
        timelineStatusPrefix={"FN_COMMON_STATUS_"}
        forcedActionPrefix={"FN"}
        statusAttribute={"state"}
        MenuStyle={{ color: "#FFFFFF", fontSize: "18px" }}
      />
    </div>
  );
};

export default React.memo(ApplicationDetails);
