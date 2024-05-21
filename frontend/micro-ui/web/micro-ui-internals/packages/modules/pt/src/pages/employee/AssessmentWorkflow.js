import { Header, MultiLink } from "@egovernments/digit-ui-react-components";
import _ from "lodash";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import ApplicationDetailsTemplate from "../../../../templates/ApplicationDetails";
import { newConfigMutate } from "../../config/Mutate/config";
import TransfererDetails from "../../pageComponents/Mutate/TransfererDetails";
import MutationApplicationDetails from "./MutationApplicatinDetails";
import getPTAcknowledgementData from "../../getPTAcknowledgementData";

// const assessmentDataSearch =async (tenantId)=>{
//     const assData = await Digit.PTService.assessmentSearch({ tenantId, filters: { assessmentNumbers:'MN-AS-2024-04-14-000289' } });
//     console.log("assData===",assData)
//     return assData?.Assessments;
// } 

const setBillData = async (tenantId, assessmentId, updatefetchBillData, updateCanFetchBillData, updateassmentSearchData) => {
    const assessmentData = await Digit.PTService.assessmentSearch({ tenantId, filters: { assessmentNumbers:assessmentId } });
    let billData = {};
    if (assessmentData?.Assessments?.length > 0) {
        updateassmentSearchData(assessmentData?.Assessments)
      billData = await Digit.PaymentService.fetchBill(tenantId, {
        businessService: "PT",
        consumerCode: assessmentData?.Assessments[0].propertyId,
      });
    }
    updatefetchBillData(billData);
    updateCanFetchBillData({
      loading: false,
      loaded: false,
      canLoad: false,
    });
  };


const AssessmentWorkflow = () => {
  const { t } = useTranslation();
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { tenants } = storeData || {};
  const { id: assessmentId } = useParams();
  const [showToast, setShowToast] = useState(null);
  const [appDetailsToShow, setAppDetailsToShow] = useState({});
  const [showOptions, setShowOptions] = useState(false);
  const [enableAudit, setEnableAudit] = useState(false);
  const [businessService, setBusinessService] = useState("ASMT");
  let propertyId;
  const [billData, updateCanFetchBillData] = useState({
    loading: false,
    loaded: false,
    canLoad: true,
  });

  const [fetchBillData, updatefetchBillData] = useState({});
  const [assmentSearchData, updateassmentSearchData] = useState({});
if(billData?.canLoad) {
    setBillData(tenantId, assessmentId, updatefetchBillData, updateCanFetchBillData,updateassmentSearchData);

}
propertyId = assmentSearchData[0]?.propertyId || ''
  console.log("updateassmentSearchData===",assmentSearchData);
  console.log("updatefetchBillData===",fetchBillData)
  console.log("updateCanFetchBillData===",billData)

  sessionStorage.setItem("applicationNoinAppDetails",propertyId);
//   const assessmentSerachResult = Digit.PTService.assessmentSearch({ tenantId, filters: { assessmentNumbers:'MN-AS-2024-04-14-000289' } });
//   console.log("assessmentSerachResult===",assessmentSerachResult);
//   let assessmentData = assessmentDataSearch(tenantId);
//     console.log("assessmentData===",assessmentData)
  const { isLoading, isError, data: applicationDetails, error } = Digit.Hooks.pt.useApplicationDetail(t, tenantId, propertyId);
console.log("applicationDetails===",applicationDetails)
  const {
    isLoading: updatingApplication,
    isError: updateApplicationError,
    data: updateResponse,
    error: updateError,
    mutate,
  } = Digit.Hooks.pt.useApplicationActions(tenantId, 'ASMT');

  let workflowDetails = Digit.Hooks.useWorkflowDetails({
    tenantId: applicationDetails?.tenantId || tenantId,
    id: assessmentId,
    // applicationDetails?.applicationData?.acknowldgementNumber,
    moduleCode: businessService,
    role: "PT_CEMP",
  });
  console.log("workflowDetails--Assessment==",workflowDetails);
  const { isLoading: auditDataLoading, isError: isAuditError, data: auditData } = Digit.Hooks.pt.usePropertySearch(
    {
      tenantId,
      filters: { propertyIds: propertyId, audit: true },
    },
    { enabled: enableAudit, select: (data) => data.Properties?.filter((e) => e.status === "ACTIVE") }
  );
//   const assessmentData = Digit.Hooks.pt.usePropertyAssessmentSearch(
//     {
//       tenantId,
//       filters: { assessmentNumbers:'MN-AS-2024-04-14-000289' },
//     },
//     { enabled: enableAudit, select: (data) => data.Properties?.filter((e) => e.status === "ACTIVE") }
//   );

//   console.log("assessmentData===",assessmentData.Assessments)

  const closeToast = () => {
    setShowToast(null);
  };

  useEffect(() => {
    if (applicationDetails) {
      setAppDetailsToShow(_.cloneDeep(applicationDetails));
      if (applicationDetails?.applicationData?.status !== "ACTIVE" && applicationDetails?.applicationData?.creationReason === "MUTATION") {
        setEnableAudit(true);
      }
    }
  }, [applicationDetails]);


  useEffect(() => {
    if (workflowDetails?.data?.applicationBusinessService && !(workflowDetails?.data?.applicationBusinessService === "ASMT" && businessService === "PT.UPDATE")) {
      setBusinessService(workflowDetails?.data?.applicationBusinessService);
    }
  }, [workflowDetails.data]);

  const PT_CEMP = Digit.UserService.hasAccess(["PT_CEMP"]) || false;

  if (appDetailsToShow?.applicationData?.status === "ACTIVE" && PT_CEMP) {
    workflowDetails = {
      ...workflowDetails,
      data: {
        ...workflowDetails?.data,
        actionState: {
          nextActions: [
            {
              action: "VIEW_DETAILS",
              redirectionUrl: {
                pathname: `/digit-ui/employee/pt/property-details/${propertyId}`,
              },
              tenantId: Digit.ULBService.getStateId(),
            },
          ],
        },
      },
    };
  }

  if (!(appDetailsToShow?.applicationDetails?.[0]?.values?.[0].title === "PT_PROPERTY_APPLICATION_NO")) {
    appDetailsToShow?.applicationDetails?.unshift({
      values: [
        { title: "PT_PROPERTY_APPLICATION_NO", value: appDetailsToShow?.applicationData?.acknowldgementNumber },
        { title: "PT_SEARCHPROPERTY_TABEL_PTUID", value: appDetailsToShow?.applicationData?.propertyId },
        { title: "ES_APPLICATION_CHANNEL", value: `ES_APPLICATION_DETAILS_APPLICATION_CHANNEL_${appDetailsToShow?.applicationData?.channel}` },
      ],
    });
  }

  if (
    PT_CEMP &&
    workflowDetails?.data?.applicationBusinessService === "PT.MUTATION" &&
    workflowDetails?.data?.actionState?.nextActions?.find((act) => act.action === "PAY")
  ) {
    workflowDetails.data.actionState.nextActions = workflowDetails?.data?.actionState?.nextActions.map((act) => {
      if (act.action === "PAY") {
        return {
          action: "PAY",
          forcedName: "WF_EMPLOYEE_PT.MUTATION_PAY",
          redirectionUrl: { pathname: `/digit-ui/employee/payment/collect/PT.MUTATION/${appDetailsToShow?.applicationData?.acknowldgementNumber}` },
        };
      }
      return act;
    });
  }

  const wfDocs = workflowDetails.data?.timeline?.reduce((acc, { wfDocuments }) => {
    return wfDocuments ? [...acc, ...wfDocuments] : acc;
  }, []);
  let appdetailsDocuments = appDetailsToShow?.applicationDetails?.find((e) => e.title === "PT_OWNERSHIP_INFO_SUB_HEADER")?.additionalDetails
    ?.documents;

  if (appdetailsDocuments && wfDocs?.length && !appdetailsDocuments?.find((e) => e.title === "PT_WORKFLOW_DOCS")) {
    appDetailsToShow.applicationDetails.find((e) => e.title === "PT_OWNERSHIP_INFO_SUB_HEADER").additionalDetails.documents = [
      ...appdetailsDocuments,
      {
        title: "PT_WORKFLOW_DOCS",
        values: wfDocs?.map?.((e) => ({ ...e, title: e.documentType })),
      },
    ];
  }
  const handleDownloadPdf = async () => {
    const Property = appDetailsToShow?.applicationData ;
    const tenantInfo  = tenants.find((tenant) => tenant.code === Property.tenantId);

    const data = await getPTAcknowledgementData(Property, tenantInfo, t);
    Digit.Utils.pdf.generate(data);
  };

  const propertyDetailsPDF = {
    order: 1,
    label: t("PT_APPLICATION"),
    onClick: () => handleDownloadPdf(),
  };
  let dowloadOptions = [propertyDetailsPDF];

  return (
    <div>
        <div className={"employee-application-details"} style={{ marginBottom: "15px" }}>
      <Header styles={{ marginLeft: "0px", paddingTop: "10px", fontSize: "16px" }}>{t("PT_APPLICATION_TITLE")}</Header>
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
        applicationDetails={appDetailsToShow}
        assmentSearchData={assmentSearchData}
        isLoading={isLoading}
        isDataLoading={isLoading}
        applicationData={appDetailsToShow?.applicationData}
        mutate={mutate}
        workflowDetails={workflowDetails}
        businessService={businessService}
        moduleCode="PT"
        showToast={showToast}
        setShowToast={setShowToast}
        closeToast={closeToast}
        userRole={'ASSIGNING_OFFICER'}
        timelineStatusPrefix={""}
        forcedActionPrefix={"WF_EMPLOYEE_ASMT"}
        statusAttribute={"state"}
        MenuStyle={{ color: "#FFFFFF", fontSize: "18px" }}
        ActionBarStyle={{float: "right"}}
      />
    
    </div>
  );
};

export default React.memo(AssessmentWorkflow);
