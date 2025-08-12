import { Header, MultiLink, SubmitBar } from "@upyog/digit-ui-react-components";
import _ from "lodash";
import React, { useContext, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import ApplicationDetailsTemplate from "../../../../templates/ApplicationDetails";
import getSVAcknowledgementData from "../../utils/getSVAcknowledgementData";
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
  const businessService = "street-vending"
  const isDraftApplication=false


  // isAction is added to enable or disable the actionbar
  let isAction = false;
  if (window.location.href.includes("applicationsearch")) {
    isAction = true;
  }

  const { isLoading, data: applicationDetails } = Digit.Hooks.sv.useSVApplicationDetail(t, tenantId, applicationNumber,isDraftApplication);

  // hook used to work with the mutation function
  const {mutate} = Digit.Hooks.sv.useSVApplicationAction(tenantId);

  // fetches workflowDetails from the hook useworkflowdetails
  let workflowDetails = Digit.Hooks.useWorkflowDetails({
    tenantId: applicationDetails?.applicationData?.tenantId || tenantId,
    id: applicationDetails?.applicationData?.applicationData?.applicationNo,
    moduleCode: businessService,
    role: "SV_CEMP",
  });

  const closeToast = () => {
    setShowToast(null);
  };

  useEffect(() => {
    if (applicationDetails) {
      setAppDetailsToShow(_.cloneDeep(applicationDetails));
    }
  }, [applicationDetails]);

  const { data: fetchedVendingZones } = Digit.Hooks.useBoundaryLocalities(
    appDetailsToShow?.applicationData?.applicationData?.locality,
    "vendingzones",
    {
      enabled: !!appDetailsToShow?.applicationData?.applicationData?.locality,
    },
    t
  );

  let vending_Zone = [];
  fetchedVendingZones && fetchedVendingZones.map((vendingData) => {
    vending_Zone.push({ i18nKey: vendingData?.i18nkey, code: vendingData?.code, value: vendingData?.name })
  })

  const vz = vending_Zone?.filter((zone) => zone?.code === appDetailsToShow?.applicationData?.applicationData?.vendingZone || zone?.value === appDetailsToShow?.applicationData?.applicationData?.vendingZone);
  const UserVendingZone = vz[0]?.value;
  const UserVendingZoneCode = vz[0]?.code;

  // This code wil check if the the employee has access && businessService is streetvending and nextAction is Pay then it will redirect in the Payment page
  const SV_CEMP = Digit.UserService.hasAccess(["SVCEMP", "TVCEMPLOYEE"]) || false;
  if (
    SV_CEMP &&
    workflowDetails?.data?.applicationBusinessService === "street-vending" &&
    workflowDetails?.data?.actionState?.nextActions?.find((act) => act.action === "PAY")
  ) {
    workflowDetails.data.actionState.nextActions = workflowDetails?.data?.actionState?.nextActions.map((act) => {
      if (act.action === "PAY") {
        return {
          action: "PAY",
          forcedName: "SV_PAY",
          redirectionUrl: { pathname: `/digit-ui/employee/payment/collect/sv-services/${appDetailsToShow?.applicationData?.applicationData?.applicationNo}` },
        };
      }
      return act;
    });
  }

  // Handling the download of acknowledgement page
  const handleDownloadPdf = async () => {
    const SVApplication = appDetailsToShow?.applicationData;
    const tenantInfo = tenants.find((tenant) => tenant.code === SVApplication.tenantId);
    const data = await getSVAcknowledgementData(SVApplication.applicationData, tenantInfo, t);
    Digit.Utils.pdf.generate(data);
  };

  const svDetailsPDF = {
    order: 1,
    label: t("SV_ACKNOWLEDGEMENT"),
    onClick: () => handleDownloadPdf(),
  };

  let dowloadOptions = [svDetailsPDF];


  const { data: reciept_data, isLoading: recieptDataLoading } = Digit.Hooks.useRecieptSearch(
    {
      tenantId: tenantId,
      businessService: "sv-services",
      consumerCodes: appDetailsToShow?.applicationData?.applicationData?.applicationNo,
      isEmployee: false,
    },
    { enabled: appDetailsToShow?.applicationData?.applicationData?.applicationNo ? true : false }
  );

  async function getRecieptSearch({ tenantId, payments, ...params }) {
    let response = { filestoreIds: [payments?.fileStoreId] };
    response = await Digit.PaymentService.generatePdf(tenantId, { Payments: [{ ...payments }] }, "svservice-receipt");
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  };

  if (reciept_data && reciept_data?.Payments.length > 0 && recieptDataLoading == false)
    dowloadOptions.push({
      label: t("SV_FEE_RECIEPT"),
      onClick: () => getRecieptSearch({ tenantId: reciept_data?.Payments[0]?.tenantId, payments: reciept_data?.Payments[0] }),
    });

  const printCertificate = async () => {
    let response = await Digit.PaymentService.generatePdf(tenantId, { SVDetail: [applicationDetails?.applicationData?.applicationData] }, "svcertificate");
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  };
  const printId = async () => {
    let response = await Digit.PaymentService.generatePdf(tenantId, { SVDetail: [applicationDetails?.applicationData?.applicationData] }, "svidentitycard");
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  };

  if (reciept_data && reciept_data?.Payments.length > 0 && recieptDataLoading == false)
    dowloadOptions.push({
      label: t("SV_CERTIFICATE"),
      onClick: () => printCertificate(),
    });
    
  if (reciept_data && reciept_data?.Payments.length > 0 && recieptDataLoading == false)
    dowloadOptions.push({
      label: t("SV_ID_CARD"),
      onClick: () => printId(),
    });
  
  return (
    <div>
      <div className={"employee-application-details"} style={{ marginBottom: "15px" }}>
        <Header styles={{ marginLeft: "0px", paddingTop: "10px", fontSize: "32px" }}>{t("SV_APPLICATION_DETAILS")}</Header>
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
        vending_Zone={vending_Zone}
        UserVendingZone={UserVendingZone}
        UserVendingZoneCode={UserVendingZoneCode}
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
        timelineStatusPrefix={"SV_"}
        forcedActionPrefix={"SV"}
        statusAttribute={"state"}
        MenuStyle={{ color: "#FFFFFF", fontSize: "18px" }}
      />

    </div>
  );
};

export default React.memo(ApplicationDetails);
