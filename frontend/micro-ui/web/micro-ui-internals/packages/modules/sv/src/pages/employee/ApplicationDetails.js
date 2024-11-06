import { Header, MultiLink, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import _ from "lodash";
import React, { useContext, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams, Link, useHistory } from "react-router-dom";
import ApplicationDetailsTemplate from "../../../../templates/ApplicationDetails";
import getSVAcknowledgementData from "../../utils/getSVAcknowledgementData";

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
  const [businessService, setBusinessService] = useState("street-vending");

  const history = useHistory();

  // isAction is added to enable or disable the actionbar
  let isAction = false;
  if (window.location.href.includes("applicationsearch")) {
    isAction = true;
  }

  const { isLoading, isError, data: applicationDetails, error } = Digit.Hooks.sv.useSVApplicationDetail(t, tenantId, applicationNumber);

  // hook used to work with the mutation function
  const {
    isLoading: updatingApplication,
    isError: updateApplicationError,
    data: updateResponse,
    error: updateError,
    mutate,
  } = Digit.Hooks.sv.useSVApplicationAction(tenantId);

  // fetches workflowDetails from the hook useworkflowdetails
  let workflowDetails = Digit.Hooks.useWorkflowDetails({
    tenantId: applicationDetails?.applicationData?.tenantId || tenantId,
    id: applicationDetails?.applicationData?.applicationData?.applicationNo,
    moduleCode: businessService,
    role: "SV_CEMP",
  });

  console.log("data of workflow in applciation dateadslis :: ", workflowDetails)


  // const { isLoading: auditDataLoading, isError: isAuditError, data: auditData } = Digit.Hooks.sv.useSVSearch(
  //   {
  //     tenantId,
  //     filters: { applicationNumber: applicationNumber, audit: true },
  //   },
  // );

  const closeToast = () => {
    setShowToast(null);
  };

  console.log("data of applicationDetails in application details page :: ", applicationDetails)
  useEffect(() => {
    if (applicationDetails) {
      setAppDetailsToShow(_.cloneDeep(applicationDetails));
    }
  }, [applicationDetails]);


  useEffect(() => {

    if (workflowDetails?.data?.applicationBusinessService && !(workflowDetails?.data?.applicationBusinessService === "street-vending" && businessService === "street-vending")) {
      setBusinessService(workflowDetails?.data?.applicationBusinessService);
    }
  }, [workflowDetails.data]);


  // const SV_CEMP = Digit.UserService.hasAccess(["SV_CEMP"]) || false;
  // if (
  //   SV_CEMP &&
  //   workflowDetails?.data?.applicationBusinessService === "sv" &&
  //   workflowDetails?.data?.actionState?.nextActions?.find((act) => act.action === "PAY")
  // ) {
  //   workflowDetails.data.actionState.nextActions = workflowDetails?.data?.actionState?.nextActions.map((act) => {
  //     if (act.action === "PAY") {
  //       return {
  //         action: "PAY",
  //         forcedName: "WF_PAY_APPLICATION",
  //         redirectionUrl: { pathname: `/digit-ui/employee/payment/collect/sv-services/${appDetailsToShow?.applicationData?.applicationData?.applicationNumber}` },
  //       };
  //     }
  //     return act;
  //   });
  // }

  // Handling the download of acknowledgement page
  const handleDownloadPdf = async () => {
    const SVApplication = appDetailsToShow?.applicationData;
    const tenantInfo = tenants.find((tenant) => tenant.code === SVApplication.tenantId);
    const data = await getSVAcknowledgementData(SVApplication.applicationData, tenantInfo, t);
    Digit.Utils.pdf.generate(data);
  };

  const svDetailsPDF = {
    order: 1,
    label: t("SV_APPLICATION"),
    onClick: () => handleDownloadPdf(),
  };

  let dowloadOptions = [svDetailsPDF];


  // ------------------------------ The commented code maybe needed to be used later ------------------------
  // const { data: reciept_data, isLoading: recieptDataLoading } = Digit.Hooks.useRecieptSearch(
  //   {
  //     tenantId: tenantId,
  //     businessService: "pet-services",
  //     consumerCodes: appDetailsToShow?.applicationData?.applicationData?.applicationNumber,
  //     isEmployee: false,
  //   },
  //   { enabled: appDetailsToShow?.applicationData?.applicationData?.applicationNumber ? true : false }
  // );

  // async function getRecieptSearch({ tenantId, payments, ...params }) {
  //   let response = { filestoreIds: [payments?.fileStoreId] };
  //   response = await Digit.PaymentService.generatePdf(tenantId, { Payments: [{ ...payments }] }, "petservice-receipt");
  //   const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
  //   window.open(fileStore[response?.filestoreIds[0]], "_blank");
  // };

  // if (reciept_data && reciept_data?.Payments.length > 0 && recieptDataLoading == false)
  //   dowloadOptions.push({
  //     label: t("SV_FEE_RECIEPT"),
  //     onClick: () => getRecieptSearch({ tenantId: reciept_data?.Payments[0]?.tenantId, payments: reciept_data?.Payments[0] }),
  //   });

  // const printCertificate = async () => {
  //   let response = await Digit.PaymentService.generatePdf(tenantId, { PetRegistrationApplications: [applicationDetails?.applicationData?.applicationData] }, "petservicecertificate");
  //   const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
  //   window.open(fileStore[response?.filestoreIds[0]], "_blank");
  // };

  // if (reciept_data?.Payments[0]?.instrumentStatus === "APPROVED")
  //   dowloadOptions.push({
  //     label: t("SV_CERTIFICATE"),
  //     onClick: () => printCertificate(),
  //   });


  return (
    <div>
      <div className={"employee-application-details"} style={{ marginBottom: "15px" }}>
        <Header styles={{ marginLeft: "0px", paddingTop: "10px", fontSize: "32px" }}>{t("SV_APPLICATION_DETAILS")}</Header>
        {dowloadOptions && dowloadOptions.length > 0 && (
          <MultiLink
            className="multilinkWrapper employee-mulitlink-main-div"
            onHeadClick={() => setShowOptions(!showOptions)}
            displayOptions={showOptions}
            options={dowloadOptions}
            downloadBtnClassName={"employee-download-btn-className"}
            optionsClassName={"employee-options-btn-className"}
          // ref={menuRef}
          />
        )}
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
        moduleCode="sv-services"
        showToast={showToast}
        setShowToast={setShowToast}
        closeToast={closeToast}
        timelineStatusPrefix={"SV_COMMON_STATUS_"}
        forcedActionPrefix={"EMPLOYEE_SV"}
        statusAttribute={"state"}
        MenuStyle={{ color: "#FFFFFF", fontSize: "18px" }}
      />

    </div>
  );
};

export default React.memo(ApplicationDetails);
