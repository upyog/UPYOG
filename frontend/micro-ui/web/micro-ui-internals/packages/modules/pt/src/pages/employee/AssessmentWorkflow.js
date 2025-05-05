import { ActionBar, Header, MultiLink, SubmitBar } from "@upyog/digit-ui-react-components";
import _ from "lodash";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import ApplicationDetailsTemplate from "../../../../templates/ApplicationDetails";
import { newConfigMutate } from "../../config/Mutate/config";
import TransfererDetails from "../../pageComponents/Mutate/TransfererDetails";
import MutationApplicationDetails from "./MutationApplicatinDetails";
import getPTAcknowledgementData from "../../getPTAcknowledgementData";
import { useQueryClient } from "react-query";

// const assessmentDataSearch =async (tenantId)=>{
//     const assData = await Digit.PTService.assessmentSearch({ tenantId, filters: { assessmentNumbers:'MN-AS-2024-04-14-000289' } });
//     return assData?.Assessments;
// } 

const setBillData = async (tenantId, assessmentId, updatefetchBillData, updateCanFetchBillData, updateassmentSearchData) => {
    const assessmentData = await Digit.PTService.assessmentSearch({ tenantId, filters: { assessmentNumbers:assessmentId } });
    let billData = {};
    if (assessmentData?.Assessments?.length > 0) {
        updateassmentSearchData(assessmentData?.Assessments)
      // billData = await Digit.PaymentService.fetchBill(tenantId, {
      //   businessService: "PT",
      //   consumerCode: assessmentData?.Assessments[0].propertyId,
      // });
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
  const queryClient = useQueryClient();
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

  sessionStorage.setItem("applicationNoinAppDetails",propertyId);
//   const assessmentSerachResult = Digit.PTService.assessmentSearch({ tenantId, filters: { assessmentNumbers:'MN-AS-2024-04-14-000289' } });
//   let assessmentData = assessmentDataSearch(tenantId);
  const { isLoading, isError, data: applicationDetails, error } = Digit.Hooks.pt.useApplicationDetail(t, tenantId, propertyId);
const { isLoading: assessmentLoading, mutate: assessmentMutate } = Digit.Hooks.pt.usePropertyAssessment(applicationDetails && applicationDetails?.tenantId ? applicationDetails?.tenantId : '');

  const {
    isLoading: updatingApplication,
    isError: updateApplicationError,
    data: updateResponse,
    error: updateError,
    mutate,
  } = Digit.Hooks.pt.useApplicationActions(tenantId, 'ASMT');

  const {
    isLoading: ptCalculationEstimateLoading,
    data: ptCalculationEstimateData,
    mutate: ptCalculationEstimateMutate,
    error: errorE
  } = Digit.Hooks.pt.usePtCalculationEstimate(tenantId);

  let workflowDetails = Digit.Hooks.useWorkflowDetails({
    tenantId: applicationDetails?.tenantId || tenantId,
    id: assessmentId,
    // applicationDetails?.applicationData?.acknowldgementNumber,
    moduleCode: businessService,
    role: "PT_CEMP",
  });
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


  const closeToast = () => {
    setShowToast(null);
  };
  // let obj = {
  //   tenantId: applicationDetails && applicationDetails?.tenantId ? applicationDetails?.tenantId : tenantId,
  //   finanacialYear: assmentSearchData[0]?.financialYear,

  // }
  // assmentSearchData.tenantId = applicationDetails && applicationDetails?.tenantId ? applicationDetails?.tenantId : tenantId;
  useEffect(() => {
    // estimate calculation
    if(assmentSearchData && assmentSearchData[0])
    ptCalculationEstimateMutate({ Assessment: {
      financialYear: assmentSearchData[0]?.financialYear,
      modeOfPayment: assmentSearchData[0]?.modeOfPayment,
      propertyId: assmentSearchData[0]?.propertyId,
      tenantId: assmentSearchData[0]?.tenantId,
      source: assmentSearchData[0]?.source,
      channel: assmentSearchData[0]?.channel,
      assessmentDate: assmentSearchData[0]?.assessmentDate,
    }});
    }, [assmentSearchData]);

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
  const getPropertyTypeLocale = (value) => {
    // return `PROPERTYTAX_${value?.split(".")[1]}`;
    return value ? value == 'VACANT' ? 'Vacant' :`${value?.split(".")[1]}`.toLocaleLowerCase() : '';
  };
  
  const getPropertySubtypeLocale = (value) => `PROPERTYTAX_${value}`;  
  

  let address_to_display=applicationDetails?.applicationData?.address;
  if(address_to_display?.doorNo){
      address_to_display=address_to_display?.doorNo+','+address_to_display?.locality?.area+','+address_to_display?.city;
  }
  else{
      address_to_display=address_to_display?.locality?.area+','+address_to_display?.city;
  }
  const getDate = (date)=> {
    let convertedDate = new Date(date).toDateString()
    return convertedDate
  }
  let propertyDetailsValuse = [];
  if(applicationDetails?.applicationData?.propertyType=='BUILTUP.INDEPENDENTPROPERTY') {
    propertyDetailsValuse = [
      { title: "PT_ASSESMENT_INFO_TYPE_OF_BUILDING", value: getPropertyTypeLocale(applicationDetails?.applicationData?.propertyType) },
      { title: "PT_ASSESMENT_INFO_USAGE_TYPE", value: getPropertySubtypeLocale(applicationDetails?.applicationData?.usageCategory) },
      { title: "PT_ASSESMENT_INFO_PLOT_SIZE", value: applicationDetails?.applicationData?.landArea },
      { title: "PT_ASSESMENT_INFO_NO_OF_FLOOR", value: applicationDetails?.applicationData?.noOfFloors },
      { title: "Vacant Land Usage Type", value: (ptCalculationEstimateData?.Calculation[0]?.vacantland[0] && ptCalculationEstimateData?.Calculation[0]?.vacantland[0]?.vacantlandtype) ? "COMMON_PROPUSGTYPE_NONRESIDENTIAL_"+ptCalculationEstimateData?.Calculation[0]?.vacantland[0]?.vacantlandtype : ''},
      { title: "Vacant Land Tax Amount", value: ptCalculationEstimateData?.Calculation[0]?.vacantland[0]?.vacantlandamount }
    ]
  } else {
    propertyDetailsValuse = [
      { title: "PT_ASSESMENT_INFO_TYPE_OF_BUILDING", value: getPropertyTypeLocale(applicationDetails?.applicationData?.propertyType) },
      { title: "PT_ASSESMENT_INFO_USAGE_TYPE", value: getPropertySubtypeLocale(applicationDetails?.applicationData?.usageCategory) },
      { title: "PT_ASSESMENT_INFO_PLOT_SIZE", value: applicationDetails?.applicationData?.landArea },
      { title: "PT_ASSESMENT_INFO_NO_OF_FLOOR", value: applicationDetails?.applicationData?.noOfFloors },
    ]
  }
  return (
    <div>
      <Header>{t("PT_TX_ASSESSMENT")}</Header>
      <ApplicationDetailsTemplate
        assmentSearchData={assmentSearchData}
        applicationDetails={
          {
            applicationDetails:[
            {
              title: "PT_ESTIMATE_DETAILS_HEADER",
              values: [ 
                {
                  title: "PT_PROPERTY_PTUID",
                  value: propertyId,  
                },
                {
                  title: "PT_ADDRESS",
                  value: address_to_display,
                },
                {
                  title: "ES_PT_TITLE_BILLING_PERIOD",
                  value: assmentSearchData?.[0]?.financialYear,
                },
                // {
                //   title:"PT_BILLING_DUE_DATE",
                //   //value:date.getDate()+'-'+date.getMonth()+'-'+date.getFullYear(),
                //   value:convertEpochToDate(paymentDetails?.data?.Bill?.[0]?.billDetails?.[0]?.expiryDate,"PT") || t("CS_NA"),
                // },
              ],
              },
              {
                title:"PT_TAX_ESTIMATION_HEADER",
                additionalDetails: {
                  taxHeadEstimatesCalculation: ptCalculationEstimateData?.Calculation[0],
                },
              },
              {
                title: "Mode of Payments",
                additionalDetails: {
                  floors: ptCalculationEstimateData?.Calculation[0]?.modeOfPaymentDetails
                    // ?.filter((e) => e.active)
                    // ?.sort?.((a, b) => a.floorNo - b.floorNo)
                    ?.map((paymentMode, index) => {
                      const values = [
                        // {
                        //   title: `${t("ES_APPLICATION_DETAILS_UNIT")} ${index + 1}`,
                        //   value: "",
                        // },
                        {
                          title: "Payment Mode",
                          value: paymentMode?.paymentMode,
                        },
                        {
                          title: "From Date",
                          // value: `PROPERTYTAX_${ unit?.usageCategory
                          value: paymentMode?.formDate ? getDate(paymentMode?.formDate) : '' ,
                        },
                        {
                          title: "To Date",
                          value: paymentMode?.toDate ? getDate(paymentMode?.toDate) : '',
                        },
                        {
                          title: "Tax Amount",
                          value: paymentMode?.taxAmount
                          // value: unit?.constructionDetail?.builtUpArea,
                        }
                      ];
        
                      // if (unit.occupancyType === "RENTED") values.push({ title: "PT_FORM2_TOTAL_ANNUAL_RENT", value: unit.arv });
        
                      return {
                        //title: floorName,
                        title:"",
                        values: [
                          {
                            title: "",
                            values,
                          },
                        ],
                      };
                    }),
                },
              },
              // {
              //   belowComponent:()=><LinkLabel onClick={()=>{showPopUp(true)}} style={isMobile ? {color:"#0f4f9e",marginLeft:"0px"} : {color:"#0f4f9e"}}>{t("PT_ADD_REBATE_PENALITY")}</LinkLabel>
              // },
              {
                title: "PT_ASSESMENT_INFO_SUB_HEADER",
                values: propertyDetailsValuse,
                // [
                //   { title: "PT_ASSESMENT_INFO_TYPE_OF_BUILDING", value: getPropertyTypeLocale(applicationDetails?.applicationData?.propertyType) },
                //   { title: "PT_ASSESMENT_INFO_USAGE_TYPE", value: getPropertySubtypeLocale(applicationDetails?.applicationData?.usageCategory) },
                //   { title: "PT_ASSESMENT_INFO_PLOT_SIZE", value: applicationDetails?.applicationData?.landArea },
                //   { title: "PT_ASSESMENT_INFO_NO_OF_FLOOR", value: applicationDetails?.applicationData?.noOfFloors },
                //   applicationDetails?.applicationData?.propertyType!='VACANT' && { title: "Vacant Land Usage Type", value: (ptCalculationEstimateData?.Calculation[0]?.vacantland[0] && ptCalculationEstimateData?.Calculation[0]?.vacantland[0]?.vacantlandtype) ? "COMMON_PROPUSGTYPE_NONRESIDENTIAL_"+ptCalculationEstimateData?.Calculation[0]?.vacantland[0]?.vacantlandtype : ''},
                //   { title: "Vacant Land Tax Amount", value: ptCalculationEstimateData?.Calculation[0]?.vacantland[0]?.vacantlandamount }
                // ],
                additionalDetails: {
                  floors: ptCalculationEstimateData?.Calculation[0]?.units
                    // ?.filter((e) => e.active)
                    ?.sort?.((a, b) => a.floorNo - b.floorNo)
                    ?.map((unit, index) => {
                      let floorName = `PROPERTYTAX_FLOOR_${unit.floorNo}`;
                      const values = [
                        {
                          title: `${t("ES_APPLICATION_DETAILS_UNIT")} ${index + 1}`,
                          value: "",
                        },
                        {
                          title: "Floor No",
                          value: unit?.floorNo,
                        },
                        {
                          title: "PT_ASSESSMENT_UNIT_USAGE_TYPE",
                          // value: `PROPERTYTAX_${ unit?.usageCategory
                          value: `PROPERTYTAX_${ unit?.usageCategoryMajor
                          }`,
                        },
                        {
                          title: "PT_ASSESMENT_INFO_OCCUPLANCY",
                          value: unit?.occupancyType,
                        },
                        {
                          title: "PT_FORM2_BUILT_AREA",
                          value: unit?.unitArea
                          // value: unit?.constructionDetail?.builtUpArea,
                        },
                        {
                          title: "Tax Amount",
                          value: unit?.taxamount || 0,
                        },
                      ];
        
                      // if (unit.occupancyType === "RENTED") values.push({ title: "PT_FORM2_TOTAL_ANNUAL_RENT", value: unit.arv });
        
                      return {
                        //title: floorName,
                        title:"",
                        values: [
                          {
                            title: "",
                            values,
                          },
                        ],
                      };
                    }),
                },
              },
            // {
            //   belowComponent:()=>{
            //     return (
            //       <div style={{marginTop:"19px"}}>
            //       <CardSubHeader style={{marginBottom:"8px",color:"#0B0C0C",fontSize:"24px"}}>
            //       {t("PT_CALC_DETAILS")}<br/>
            //       </CardSubHeader>
            //       <CardSectionHeader style={{marginBottom:"16px",color:"#0B0C0C",fontSize:"16px",marginTop:"revert"}}>{t("PT_CALC_LOGIC_HEADER")}</CardSectionHeader>
            //       <CardText style={{fontSize:"16px"}}>{t("PT_CALC_LOGIC")}</CardText>
                    
            //         <div style={{ border: "1px solid #D6D5D4", padding: "16px", marginTop: "8px", borderRadius: "4px", background: "#FAFAFA" }}>
            //         <div className="row border-none"><h2>{t("PT_APPLICABLE_CHARGE_SLABS")}</h2></div>
                    
            //         <StatusTable>
            //         {applicationDetails?.applicationData?.units
            //         ?.filter((e) => e.active)
            //         ?.sort?.((a, b) => a.floorNo - b.floorNo)
            //         ?.map((unit, index) => (
            //         <Row label={`${t(`PROPERTYTAX_FLOOR_${unit?.floorNo}`)} ${t(`PT_UNIT`)} - ${index+1}`} text={ChargeSlabsMenu?.PropertyTax && ChargeSlabsMenu?.PropertyTax?.ChargeSlabs?.filter((ob) => ob.floorNo == unit.floorNo)?.[0]?.name} />
            //         ))}
            //         </StatusTable>
            //        </div>
            //       </div>
                  
            //     )
            //   }
            // }
          ]}
        }
        showTimeLine={false}
        isLoading={isLoading}
        isDataLoading={isLoading}
        applicationData={appDetailsToShow?.applicationData}
        mutate={mutate}
        workflowDetails={workflowDetails}
        businessService="PT"
        assessmentMutate={assessmentMutate}
        ptCalculationEstimateMutate={ptCalculationEstimateMutate}
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
