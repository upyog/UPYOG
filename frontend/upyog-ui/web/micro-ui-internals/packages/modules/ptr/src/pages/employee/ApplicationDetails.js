/**
 * @file ApplicationDetails.js
 * 
 * @description
 * This component displays the details of a pet registration application. It allows employees to view, download, 
 * and print various documents related to the application, such as the application details, fee receipt, 
 * and pet registration certificate. 
 * 
 * @features
 * - Displays pet application details using the `ApplicationDetailsTemplate` component.
 * - Retrieves workflow details and handles business service changes dynamically.
 * - Provides download options for PDF documents (application details, receipt, certificate).
 * - Supports audit data retrieval and error handling.
 * - Displays a renewal link for expired applications.
 * 
 * @dependencies
 * - React: Component rendering and state management.
 * - Digit: For hooks, PDF generation, and payment services.
 * - Lodash: For deep cloning application data.
 * - React Router: For navigation and parameter handling.
 * 
 * @hooks used:
 * - `useEffect`: Handles side effects such as setting application details and updating business service.
 * - `useState`: Manages local state variables (showToast, appDetailsToShow, etc.).
 * - `useHistory`: For navigation.
 * 
 * @author Kunal Gaurav
 * @date 2025-03-31
 */

import { Header, MultiLink, SubmitBar } from "@upyog/digit-ui-react-components";
import _ from "lodash";
import React, { useContext, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams, Link, useHistory } from "react-router-dom";
import ApplicationDetailsTemplate from "../../../../templates/ApplicationDetails";
import getPetAcknowledgementData from "../../getPetAcknowledgementData";


const ApplicationDetails = () => {
  const { t } = useTranslation();
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { tenants } = storeData || {};
  const { id: applicationNumber } = useParams();
  const [showToast, setShowToast] = useState(null);
  const [appDetailsToShow, setAppDetailsToShow] = useState({});
  const [showOptions, setShowOptions] = useState(false);
  const [enableAudit, setEnableAudit] = useState(false);
  const [businessService, setBusinessService] = useState("ptr");

  const history = useHistory();

  // isAction is added to enable or disable the actionbar
  let isAction = false;
  if (window.location.href.includes("applicationsearch")) {
    isAction = true;
  }


  sessionStorage.setItem("applicationNoinAppDetails", applicationNumber);
  const { isLoading, isError, data: applicationDetails, error } = Digit.Hooks.ptr.usePtrApplicationDetail(t, tenantId, applicationNumber);

  const {
    isLoading: updatingApplication,
    isError: updateApplicationError,
    data: updateResponse,
    error: updateError,
    mutate,
  } = Digit.Hooks.ptr.usePTRApplicationAction(tenantId);

  let workflowDetails = Digit.Hooks.useWorkflowDetails({
    tenantId: applicationDetails?.applicationData?.tenantId || tenantId,
    id: applicationDetails?.applicationData?.applicationData?.applicationNumber,
    moduleCode: businessService,
    role: "PTR_CEMP",
  });


  const { isLoading: auditDataLoading, isError: isAuditError, data: auditData } = Digit.Hooks.ptr.usePTRSearch(
    {
      tenantId,
      filters: { applicationNumber: applicationNumber, audit: true },
    },
  );

  const closeToast = () => {
    setShowToast(null);
  };

  useEffect(() => {
    if (applicationDetails) {
      setAppDetailsToShow(_.cloneDeep(applicationDetails));

    }
  }, [applicationDetails]);


  useEffect(() => {

    if (workflowDetails?.data?.applicationBusinessService && !(workflowDetails?.data?.applicationBusinessService === "ptr" && businessService === "ptr")) {
      setBusinessService(workflowDetails?.data?.applicationBusinessService);
    }
  }, [workflowDetails.data]);


  const PTR_CEMP = Digit.UserService.hasAccess(["PTR_CEMP"]) || false;
  if (
    PTR_CEMP &&
    workflowDetails?.data?.applicationBusinessService === "ptr" &&
    workflowDetails?.data?.actionState?.nextActions?.find((act) => act.action === "PAY")
  ) {
    workflowDetails.data.actionState.nextActions = workflowDetails?.data?.actionState?.nextActions.map((act) => {
      if (act.action === "PAY") {
        return {
          action: "PAY",
          forcedName: "WF_PAY_APPLICATION",
          redirectionUrl: { pathname: `/upyog-ui/employee/payment/collect/pet-services/${appDetailsToShow?.applicationData?.applicationData?.applicationNumber}` },
        };
      }
      return act;
    });
  }

  const handleDownloadPdf = async () => {
    const PetRegistrationApplications = appDetailsToShow?.applicationData;
    const tenantInfo = tenants.find((tenant) => tenant.code === PetRegistrationApplications.tenantId);
    const data = await getPetAcknowledgementData(PetRegistrationApplications.applicationData, tenantInfo, t);
    Digit.Utils.pdf.generate(data);
  };

  const petDetailsPDF = {
    order: 1,
    label: t("PTR_APPLICATION"),
    onClick: () => handleDownloadPdf(),
  };
  let dowloadOptions = [petDetailsPDF];

  const { data: reciept_data, isLoading: recieptDataLoading } = Digit.Hooks.useRecieptSearch(
    {
      tenantId: tenantId,
      businessService: "pet-services",
      consumerCodes: appDetailsToShow?.applicationData?.applicationData?.applicationNumber,
      isEmployee: false,
    },
    { enabled: appDetailsToShow?.applicationData?.applicationData?.applicationNumber ? true : false }
  );
  

  async function getRecieptSearch({ tenantId, payments, ...params }) {
    let response = { filestoreIds: [payments?.fileStoreId] };
    response = await Digit.PaymentService.generatePdf(tenantId, { Payments: [{ ...payments }] }, "petservice-receipt");
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  };

  if (reciept_data && reciept_data?.Payments.length > 0 && recieptDataLoading == false)
  dowloadOptions.push({
    label: t("PTR_FEE_RECIEPT"),
    onClick: () => getRecieptSearch({ tenantId: reciept_data?.Payments[0]?.tenantId, payments: reciept_data?.Payments[0] }),
  });

  const printCertificate = async () => {
    let response = await Digit.PaymentService.generatePdf(tenantId, { PetRegistrationApplications: [applicationDetails?.applicationData?.applicationData] }, "petservicecertificate");
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  };

  if (reciept_data && reciept_data?.Payments.length > 0 && recieptDataLoading == false)
  dowloadOptions.push({
    label: t("PTR_CERTIFICATE"),
    onClick: () => printCertificate(),
  });

    return (
    <div>
      <div className={"employee-application-details"} style={{ marginBottom: "15px" }}>
        <Header styles={{ marginLeft: "0px", paddingTop: "10px", fontSize: "32px" }}>{t("PTR_PET_APPLICATION_DETAILS")}</Header>
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
          // ref={menuRef}
          />
        )}
      </div>
      </div>
      </div>

      <ApplicationDetailsTemplate
        isAction={isAction}
        applicationDetails={appDetailsToShow?.applicationData}
        isLoading={isLoading}
        isDataLoading={isLoading}
        applicationData={appDetailsToShow?.applicationData?.applicationData}
        mutate={mutate}
        workflowDetails={workflowDetails}
        businessService={businessService}
        moduleCode="pet-services"
        showToast={showToast}
        setShowToast={setShowToast}
        closeToast={closeToast}
        timelineStatusPrefix={"PTR_COMMON_STATUS_"}
        forcedActionPrefix={"EMPLOYEE_PTR"}
        statusAttribute={"state"}
        MenuStyle={{ color: "#FFFFFF", fontSize: "18px" }}
      />

      {/* link added for the renewal application */}
      {(appDetailsToShow?.applicationData?.applicationData?.status == "Expired") && <Link to={`/upyog-ui/employee/ptr/petservice/revised-application`}  >
        <SubmitBar style={{ marginBottom: "5px" }} label={"Renewal"} />
      </Link>}

    </div>
  );
};

export default React.memo(ApplicationDetails);
