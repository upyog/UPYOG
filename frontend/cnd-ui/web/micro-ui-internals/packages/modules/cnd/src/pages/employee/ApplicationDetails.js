import { Header, MultiLink, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import _ from "lodash";
import React, { useContext, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import ApplicationDetailsTemplate from "../../../../templates/ApplicationDetails";
import cndAcknowledgementData from "../../utils/cndAcknowledgementData";
import { cndStyles } from "../../utils/cndStyles";

/**
* ApplicationDetails component displays the details of a CND application
* It handles loading application data, workflow details, and provides necessary actions (Update Application)
* 
* @returns {JSX.Element} - Rendered component with application details
*/
const ApplicationDetails = () => {
  const { t } = useTranslation();
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { tenants } = storeData || {};
  const { id: applicationNumber } = useParams();
  const [showToast, setShowToast] = useState(null);
  const [appDetailsToShow, setAppDetailsToShow] = useState({});
  const [showOptions, setShowOptions] = useState(false);
  const businessService = "cnd"
  const isUserDetailRequired=true;


  // isAction is added to enable or disable the actionbar
  let isAction = false;
  if (window.location.href.includes("applicationsearch")) {
    isAction = true;
  }

  const { isLoading, data: applicationDetails } = Digit.Hooks.cnd.useCndApplicationDetails(t, tenantId, applicationNumber, isUserDetailRequired);

  // hook used to work with the mutation function
  const {mutate} = Digit.Hooks.cnd.useCndApplicationAction(tenantId);


  // fetches workflowDetails from the hook useworkflowdetails
  let workflowDetails = Digit.Hooks.useWorkflowDetails({
    tenantId: applicationDetails?.applicationData?.tenantId || tenantId,
    id: applicationDetails?.applicationData?.applicationData?.applicationNumber,
    moduleCode: businessService,
    role: "CND_CEMP",
  });

  const closeToast = () => {
    setShowToast(null);
  };

  useEffect(() => {
    if (applicationDetails) {
      setAppDetailsToShow(_.cloneDeep(applicationDetails));
    }
  }, [applicationDetails]);


  // This code wil check if the the employee has access && businessService is cnd-service and nextAction is Pay then it will redirect in the Payment page
  const CND_CEMP = Digit.UserService.hasAccess(["CND_CEMP"]) || false;
  if (
    CND_CEMP &&
    workflowDetails?.data?.applicationBusinessService === "cnd" &&
    workflowDetails?.data?.actionState?.nextActions?.find((act) => act.action === "PAY")
  ) {
    workflowDetails.data.actionState.nextActions = workflowDetails?.data?.actionState?.nextActions.map((act) => {
      if (act.action === "PAY") {
        return {
          action: "PAY",
          forcedName: "CND_PAY",
          redirectionUrl: { pathname: `/cnd-ui/employee/payment/collect/cnd-service/${appDetailsToShow?.applicationData?.applicationData?.applicationNumber}`},
        };
      }
      return act;
    });
  }

  // // Handling the download of acknowledgement page
  const handleDownloadPdf = async () => {
    const cndApplicationDetails = appDetailsToShow?.applicationData;
    const tenantInfo = tenants.find((tenant) => tenant.code === cndApplicationDetails.tenantId);
    const data = await cndAcknowledgementData(cndApplicationDetails.applicationData, tenantInfo, t);
    Digit.Utils.pdf.generateTable(data);
  };

  const cndDetailsPDF = {
    order: 1,
    label: t("CND_ACKNOWLEDGEMENT"),
    onClick: () => handleDownloadPdf(),
  };

  let dowloadOptions = [cndDetailsPDF];


  const { data: reciept_data, isLoading: recieptDataLoading } = Digit.Hooks.useRecieptSearch(
    {
      tenantId: tenantId,
      businessService: "cnd-service",
      consumerCodes: appDetailsToShow?.applicationData?.applicationData?.applicationNumber,
      isEmployee: false,
    },
    { enabled: appDetailsToShow?.applicationData?.applicationData?.applicationNumber ? true : false }
  );

  async function getRecieptSearch({ tenantId, payments, ...params }) {
    let response = { filestoreIds: [payments?.fileStoreId] };
    response = await Digit.PaymentService.generatePdf(tenantId, { Payments: [{ ...payments }] }, "cnd-service");
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  };

  if (reciept_data && reciept_data?.Payments.length > 0 && recieptDataLoading == false)
    dowloadOptions.push({
      label: t("CND_FEE_RECIEPT"),
      onClick: () => getRecieptSearch({ tenantId: reciept_data?.Payments[0]?.tenantId, payments: reciept_data?.Payments[0] }),
    });

  
  return (
    <div>
      <div className={"employee-application-details"} style={cndStyles.siteMediaPhotoEmployee}>
        <Header styles={cndStyles.applicationDetailHeader}>{t("CND_APPLICATION_DETAILS")}</Header>
        <div style={cndStyles.applicationDetailCard}>
         
      <div style={cndStyles.downloadButton}>
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

     {/* ApplicationDetailsTemplate renders the application details with workflow actions */}
      <ApplicationDetailsTemplate
        isAction={isAction}
        applicationDetails={appDetailsToShow?.applicationData}
        isLoading={isLoading}
        isDataLoading={isLoading}
        applicationData={appDetailsToShow?.applicationData?.applicationData}
        mutate={mutate}
        workflowDetails={workflowDetails}
        businessService={businessService}
        moduleCode="cnd-service"
        showToast={showToast}
        setShowToast={setShowToast}
        closeToast={closeToast}
        timelineStatusPrefix={"CND_"}
        forcedActionPrefix={"C&D"}
        statusAttribute={"state"}
        MenuStyle={cndStyles.menuStyle}
      />

    </div>
  );
};

export default React.memo(ApplicationDetails);
