import {
  getFixedFilename,
  getPropertyTypeLocale,
  getPropertyOwnerTypeLocale,
  getPropertyUsageTypeLocale,
  getPropertySubUsageTypeLocale,
  getPropertyOccupancyTypeLocale,
  getMohallaLocale,
  pdfDocumentName,
  pdfDownloadLink,
  getCityLocale,
} from "./indexForGetAck";

const capitalize = (text) => text.substr(0, 1).toUpperCase() + text.substr(1);
const ulbCamel = (ulb) => ulb.toLowerCase().split(" ").map(capitalize).join(" ");

// const getOwner = (application, t, customTitle) => {
//   let owners = [];
//   if(customTitle && customTitle.includes("TRANSFEROR"))
//   if (application?.isTransferor && application?.transferorDetails) {
//     application.ownershipCategory = application?.transferorDetails?.ownershipCategory;
//     owners = [...(application?.transferorDetails?.owners) || []];
//   } else {
//     owners = [...(application?.owners.filter((owner) => owner.status == "INACTIVE") || [])];
//   }
//   else
//   owners = [...(application?.owners.filter((owner) => owner.status == "ACTIVE") || [])];
//   if (application?.ownershipCategory == "INDIVIDUAL.SINGLEOWNER") {
//     return {
//       title: t(customTitle || "PT_OWNERSHIP_INFO_SUB_HEADER"),
//       values: [
//         { title: t("PT_OWNERSHIP_INFO_NAME"), value: owners[0]?.name || t("CS_NA") },
//         { title: t("PT_OWNERSHIP_INFO_MOBILE_NO"), value: owners[0]?.mobileNumber || t("CS_NA") },
//         { title: t("PT_SEARCHPROPERTY_TABEL_GUARDIANNAME"), value: owners[0]?.fatherOrHusbandName || t("CS_NA") },
//         { title: t("PT_OWNERSHIP_INFO_GENDER"), value: t(owners[0]?.gender) || t("CS_NA") },
//         { title: t("PT_FORM3_OWNERSHIP_TYPE"), value: t(application?.ownershipCategory) || t("CS_NA") },
//         { title: t("PT_OWNERSHIP_INFO_EMAIL_ID"), value: owners[0]?.emailId || t("CS_NA") },
//         { title: t("PT_OWNERSHIP_INFO_USER_CATEGORY"), value: t(getPropertyOwnerTypeLocale(owners[0]?.ownerType)) || t("CS_NA") },
//         { title: t("PT_OWNERSHIP_INFO_CORR_ADDR"), value: owners[0]?.permanentAddress || t("CS_NA") },
//       ],
//     };
//   } else if (application?.ownershipCategory.includes("INDIVIDUAL")) {
//     let values = [];
//     owners.map((owner) => {
//       let doc = [
//         { title: t("PT_OWNERSHIP_INFO_NAME"), value: owner?.name || t("CS_NA") },
//         { title: t("PT_OWNERSHIP_INFO_MOBILE_NO"), value: owner?.mobileNumber || t("CS_NA") },
//         { title: t("PT_SEARCHPROPERTY_TABEL_GUARDIANNAME"), value: owner?.fatherOrHusbandName || t("CS_NA") },
//         { title: t("PT_OWNERSHIP_INFO_GENDER"), value: t(owner?.gender) || t("CS_NA") },
//         { title: t("PT_FORM3_OWNERSHIP_TYPE"), value: t(application?.ownershipCategory) || t("CS_NA") },
//         { title: t("PT_OWNERSHIP_INFO_EMAIL_ID"), value: owner?.emailId || t("CS_NA") },
//         { title: t("PT_OWNERSHIP_INFO_USER_CATEGORY"), value: t(getPropertyOwnerTypeLocale(owner?.ownerType)) || t("CS_NA") },
//         { title: t("PT_OWNERSHIP_INFO_CORR_ADDR"), value: owner?.permanentAddress || t("CS_NA") },
//       ];
//       values.push(...doc);
//     });
//     return {
//       title: t(customTitle || "PT_OWNERSHIP_INFO_SUB_HEADER"),
//       values: values,
//     };
//   } else if (application?.ownershipCategory.includes("INSTITUTIONAL")) {
//     return {
//       title: t("PT_OWNERSHIP_INFO_SUB_HEADER"),
//       values: [
//         { title: t("PT_COMMON_INSTITUTION_NAME"), value: application?.institution?.name || t("CS_NA") },
//         { title: t("PT_TYPE_OF_INSTITUTION"), value: application?.institution?.type || t("CS_NA") },
//         { title: t("PT_OWNER_NAME"), value: application?.institution?.nameOfAuthorizedPerson || t("CS_NA") },
//         { title: t("PT_COMMON_AUTHORISED_PERSON_DESIGNATION"), value: application?.institution?.designation || t("CS_NA") },
//         { title: t("PT_FORM3_MOBILE_NUMBER"), value: owners[0]?.mobileNumber || t("CS_NA") },
//         { title: t("PT_OWNERSHIP_INFO_TEL_PHONE_NO"), value: owners[0]?.altContactNumber || t("CS_NA") },
//         { title: t("PT_OWNERSHIP_INFO_CORR_ADDR"), value: owners[0]?.correspondenceAddress || t("CS_NA") },
//         { title: t("PT_FORM3_OWNERSHIP_TYPE"), value: t(application?.ownershipCategory) || t("CS_NA") },
//       ],
//     };
//   } else {
//     return {
//       title: t("PT_OWNERSHIP_INFO_SUB_HEADER"),
//       values: [{ title: t("PT_NO_OWNERS"), value: t("CS_NA") }],
//     };
//   }
// };

// const getAssessmentInfo = (application, t) => {
//   let values = [
//     { title: t("PT_ASSESMENT_INFO_USAGE_TYPE"), value: application?.usageCategory ? `${t(
//       (application?.usageCategory !== "RESIDENTIAL" ? "COMMON_PROPUSGTYPE_NONRESIDENTIAL_" : "COMMON_PROPSUBUSGTYPE_") +
//         (application?.usageCategory?.split(".")[1] ? application?.usageCategory?.split(".")[1] : application?.usageCategory)
//     )}` : t("CS_NA") },
//     { title: t("PT_ASSESMENT_INFO_TYPE_OF_BUILDING"), value: t(getPropertyTypeLocale(application?.propertyType)) || t("CS_NA") },
//     { title: t("PT_ASSESMENT_INFO_PLOT_SIZE"), value: t(application?.landArea) || t("CS_NA") },
//     { title: t("PT_ASSESMENT_INFO_NO_OF_FLOOR"), value: t(application?.noOfFloors) || t("CS_NA") },
//   ];
//   application.units = application?.units?.filter((unit) => unit.active == true) || [];
//   let flrno,
//     i = 0;
//   flrno = application.units && application.units[0]?.floorNo;
//   application.units.map((unit) => {
//     let doc = [
//       {
//         title: (flrno !== unit?.floorNo ? (i = 1) : (i = i + 1)) && i === 1 ? t(`PROPERTYTAX_FLOOR_${unit?.floorNo}`) : "",
//       },
//       {
//         title: t(""),
//       },
//       {
//         title: t(""),
//       },
//       {
//         title: t(""),
//       },
//       { title: t("PT_UNIT")+" "+ i },
//       {
//         title: t(""),
//       },
//       {
//         title: t(""),
//       },
//       {
//         title: t(""),
//       },
//       {
//         title: (flrno = unit?.floorNo) > -3 ? t("PT_ASSESSMENT_UNIT_USAGE_TYPE") : "",
//         value: (flrno = unit?.floorNo) > -3 ? t(getPropertySubUsageTypeLocale(unit?.usageCategory)) || t("CS_NA") : "",
//       },
//       {
//         title: (flrno = unit?.floorNo) > -3 ? t("PT_ASSESMENT_INFO_OCCUPLANCY") : "",
//         value: (flrno = unit?.floorNo) > -3 ? t(getPropertyOccupancyTypeLocale(unit?.occupancyType)) || t("CS_NA") : "",
//       },
//       {
//         title: (flrno = unit?.floorNo) > -3 ? t("PT_FORM2_BUILT_AREA") : "",
//         value: (flrno = unit?.floorNo) > -3 ? t(unit?.constructionDetail?.builtUpArea) || t("CS_NA") : "",
//       },
//       {
//         title:
//           (flrno = unit?.floorNo) > -3
//             ? t(getPropertyOccupancyTypeLocale(unit?.occupancyType)) === "Rented"
//               ? t("PT_FORM2_TOTAL_ANNUAL_RENT")
//               : t("")
//             : "",
//         value:
//           (flrno = unit?.floorNo) > -3
//             ? t(getPropertyOccupancyTypeLocale(unit?.occupancyType)) === "Rented"
//               ? (unit?.arv && `₹${t(unit?.arv)}`) || "NA"
//               : t("")
//             : "",
//       },
//     ];

//     values.push(...doc);
//   });
//   return {
//     title: t("PT_ASSESMENT_INFO_SUB_HEADER"),
//     values: values,
//   };
// };

// const getMutationDetails = (application, t) => {
//   return {
//     title: t("PT_MUTATION_DETAILS"),
//     values: [
//       {
//         title: t("PT_MUTATION_COURT_PENDING_OR_NOT"),
//         value: application?.additionalDetails?.isMutationInCourt
//           ? t(`PT_MUTATION_PENDING_${application?.additionalDetails.isMutationInCourt}`)
//           : t("CS_NA"),
//       },
//       { title: t("PT_MUTATION_COURT_CASE_DETAILS"), value: application?.additionalDetails?.caseDetails || t("CS_NA") },
//       { title: t("PT_MUTATION_STATE_ACQUISITION"), value: application?.additionalDetails?.isPropertyUnderGovtPossession ? t(`PT_MUTATION_STATE_ACQUISITION_${application?.additionalDetails?.isPropertyUnderGovtPossession}`) : t("CS_NA") },
//       { title: t("PT_MUTATION_GOVT_ACQUISITION_DETAILS"), value: application?.additionalDetails?.govtAcquisitionDetails || t("CS_NA") },
//     ],
//   };
// };

const mutationRegistrationDetails = (application, t) => {
  console.log("mutationRegistrationDetails ",application)
  let values = [];
  application?.previous_bill[0].map((owner,index) => {
      let doc =
      //  index===0?
      [
        { title: t("<strong>Bill Date</strong>"), value: owner?.billDate },
        { title: t("<strong>Bill No</strong>"), value: owner?.bilNo },
      { title: t("Bill Amount"), value:  owner?.billAmount },
      { title: t("Deduction Amount"), value:  owner?.deductionAmount },
      { title: t("Remark"), value:  owner?.remark }
      ]
      // :[
      // { title:"", value: owner?.billDate },
      // { title:"", value: owner?.bilNo },
      // { title:"", value:  owner?.billAmount },
      // { title:"", value:  owner?.deductionAmount },
      // { title:"", value:  owner?.remark }
      // ];
      values.push(...doc);
    });
    return {
      title: t("WMS_RUNNING_ACCOUNT_FINAL_BILL_PREVIOUS_RUNNING_BILL_INFORMATION"),
      values: values,
    }
  // {
  //   title: t("WMS_RUNNING_ACCOUNT_FINAL_BILL_PREVIOUS_RUNNING_BILL_INFORMATION"),
  //   values: [
  //     { title: t("Bill No"), value: application?.previous_bill[0][0]?.bilNo },
  //     { title: t("Bill Amount"), value:  application?.previous_bill[0][0]?.billAmount },
  //     { title: t("Deduction Amount"), value:  application?.previous_bill[0][0]?.deductionAmount },
  //     { title: t("Remark"), value:  application?.previous_bill[0][0]?.remark },
    
  //    ]
  //   }
    
    
  //   application?.previous_bill[0].map((item)=> {
  //     console.log("item", item)
  //     let dd = ( {title: t("Bill Date"), value: item?.billDate },
  //     { title: t("Bill No"), value: item?.bilNo },
  //     { title: t("Bill Amount"), value: item?.billAmount },
  //     { title: t("Deduction Amount"), value: item?.deductionAmount },
  //     { title: t("Remark"), value: item?.remark }
  //     )
  //     console.log("return dd ", dd)
  //     return dd
  // }
  // )
    
  // },

  // return {
  //   title: t("PT_MUTATION_REGISTRATION_DETAILS"),
  //   values: [
  //     {
  //       title: t("PT_MUTATION_TRANSFER_REASON"),
  //       value: t(`PROPERTYTAX_REASONFORTRANSFER_${application?.additionalDetails?.reasonForTransfer.replaceAll(".", "_")}`),
  //     },
  //     { title: t("PT_MUTATION_MARKET_VALUE"), value: application?.additionalDetails?.marketValue || t("CS_NA") },
  //     { title: t("PT_MUTATION_DOCUMENT_NO"), value: application?.additionalDetails?.documentNumber || t("CS_NA") },
  //     { title: t("PT_MUTATION_DOCUMENT_VALUE"), value: application?.additionalDetails?.documentValue || t("CS_NA") },
  //     {
  //       title: t("PT_MUTATION_DOCUMENT_ISSUE_DATE"),
  //       value: application?.additionalDetails?.documentDate ? new Date(application?.additionalDetails?.documentDate).toDateString() : t("CS_NA"),
  //     },
  //     {
  //       title: t(""),
  //     },
  //     { title: t("PT_MUTATION_REMARKS"), value: application?.additionalDetails?.remarks || t("CS_NA") },
  //   ],
  // };
};

var arrayData = [
  { name: 'John', details: [25, 'USA'] },
  { name: 'Alice', details: [30, 'Canada'] },
  { name: 'Bob', details: [28, 'UK'] }
];

var tableData = arrayData.map(function(obj) {
  return [obj.name, ...obj.details];
});
const getWMSrafbAcknowledgementData = async (application, tenantInfo, t) => {
  console.log("application, tenantInfo ",application, tenantInfo)
  console.log("application, tenantInfo application?.ProjectInfo?.ProjectName ",application?.ProjectInfo?.ProjectName)
  const filesArray = application?.documents?.map((value) => value?.fileStoreId);
  const res = filesArray?.length>0 && await Digit.UploadServices.Filefetch(filesArray, Digit.ULBService.getStateId());
  return {
    t: t,
    tenantId: tenantInfo?.code,
    name: `${t(tenantInfo?.i18nKey)} ${ulbCamel(t(`ULBGRADE_${tenantInfo?.city?.ulbGrade.toUpperCase().replace(" ", "_").replace(".", "_")}`))}`,
    email: tenantInfo?.emailId,
    phoneNumber: tenantInfo?.contactNumber,
    heading: t("WMS_RUNNING_ACCOUNT_FINAL_BILL_ACKNOWLEDGEMENT"),
    details: [
      {
        title: t("WMS_RUNNING_ACCOUNT_FINAL_BILL_PROJECT_INFORMATION"),
        values: [
          { title: t("Project Name"), value: JSON.stringify(application?.ProjectInfo?.ProjectName)},
          { title: t("Work Name"), value: application?.ProjectInfo?.WorkName },
          {title: t("Work Order No"),value: application?.ProjectInfo?.WorkOrderNo,},
        ],
      },
      mutationRegistrationDetails(application,t),
      {
        title: t("WMS_RUNNING_ACCOUNT_FINAL_BILL_MB_NOT_PAID"),
        values: [
          { title: t("Select"), value: application?.mbNotPaid?.i18nKey },
          { title: t("Date"), value: application?.mbNotPaid?.mbDate },
          { title: t("Bill No"), value: application?.mbNotPaid?.mbNumber },
          { title: t("Amount"), value: application?.mbNotPaid?.amount },
        ],
      },
      {
        title: t("WMS_RUNNING_ACCOUNT_FINAL_BILL_TENDER_WORK_DETAILS"),
        values: [
          { title: t("Work Name"), value: application?.TenderWorkDetail?.workName },
          { title: t("Estimated Work Cost"), value: application?.TenderWorkDetail?.estimatedWorkCost },
          { title: t("Tender Type"), value: application?.TenderWorkDetail?.tenderType },
          { title: t("Percentage Type"), value: application?.TenderWorkDetail?.percentageType },
          { title: t("Amount"), value: application?.TenderWorkDetail?.amount },

        ],
      },
      {
        title: t("WMS_RUNNING_ACCOUNT_FINAL_BILL_WITHHELD_DEDUCTIONS_DETAILS"),
        values: application?.withheldDeductionsDetail?.withheldDeductionsDetail.map((res)=>(
          [
            { title: t("Tax Category"), value: res?.taxcategory.i18nKey },
            { title: t("Amount"), value: res?.amount },
            { title: t("Remark"), value: res?.remark },
            
          ]
          ))
      },
      {
        title: t("WMS_RUNNING_ACCOUNT_FINAL_BILL_RA_BILL_TAX_DETAILS"),
        values: [
          { title: t("PT_APPLICATION_NO"), value: application?.acknowldgementNumber },
          { title: t("PT_PROPERRTYID"), value: application?.propertyId },
          {
            title: t("CS_APPLICATION_DETAILS_APPLICATION_DATE"),
            value: Digit.DateUtils.ConvertTimestampToDate(application?.auditDetails?.createdTime, "dd/MM/yyyy"),
          },
        ],
      },

    ],
  };
};

export default getWMSrafbAcknowledgementData;
