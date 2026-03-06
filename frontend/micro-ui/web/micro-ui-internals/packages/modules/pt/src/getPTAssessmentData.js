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
  getTypeOfRoad,
} from "./utils";
import { values } from "lodash";

const capitalize = (text) => text.substr(0, 1).toUpperCase() + text.substr(1);
const ulbCamel = (ulb) => ulb.toLowerCase().split(" ").map(capitalize).join(" ");

const getOwner = (application, t, customTitle,res) => {
  let owners = [];
  if(customTitle && customTitle?.includes("TRANSFEROR"))
  if (application?.isTransferor && application?.transferorDetails) {
    application.ownershipCategory = application?.transferorDetails?.ownershipCategory;
    owners = [...(application?.transferorDetails?.owners) || []];
  } else {
    owners = [...(application?.owners.filter((owner) => owner.status == "INACTIVE") || [])];
  }
  else
  owners = [...(application?.owners.filter((owner) => owner.status == "ACTIVE") || [])];
  if (application?.ownershipCategory == "INDIVIDUAL.SINGLEOWNER") {
    let values = [{
      title: t(customTitle || "PT_OWNERSHIP_INFO_SUB_HEADER"),
      values: [
        { title: t("PT_OWNERSHIP_INFO_NAME"), value: owners[0]?.name || t("CS_NA") },
        { title: t("PT_OWNERSHIP_INFO_MOBILE_NO"), value: owners[0]?.mobileNumber || t("CS_NA") },
        { title: t("PT_OWNERSHIP_INFO_GENDER"), value: t(owners[0]?.gender) || t("CS_NA") },
        { title: t("PT_SEARCHPROPERTY_TABEL_GUARDIANNAME"), value: owners[0]?.fatherOrHusbandName || t("CS_NA") },
        { title: t("PT_COMMON_APPLICANT_RELATIONSHIP_LABEL"), value: t('PT_RELATION_'+owners[0]?.relationship) || t("CS_NA") },
        // { title: t("PT_OWNERSHIP_INFO_EMAIL_ID"), value: owners[0]?.emailId || t("CS_NA") },
        { title: t("PT_OWNERSHIP_INFO_USER_CATEGORY"), value: t(getPropertyOwnerTypeLocale(owners[0]?.ownerType)) || t("CS_NA") },
        { title: t("PT_OWNERSHIP_INFO_CORR_ADDR"), value: owners[0]?.permanentAddress || t("CS_NA") },
      ]
    }];
    let obj = {
      title: t("Owner Documents"),
      values:
      owners[0]?.documents && owners[0]?.documents.length > 0
        ? owners[0]?.documents.map((document, index) => {
          console.log("res?.data==",res?.data);
            let documentLink = pdfDownloadLink(res?.data, document?.fileStoreId);
            console.log("documentLink===",documentLink);
            if(document?.documentType){
              return {
                title: t(document?.documentType=="PROPERTY_PHOTO" ? 'PT_PROPERTY_PHOTO' : document?.documentType || t("CS_NA")),
                // human readable filename shown in PDF
                value: pdfDocumentName(documentLink, index) || t("CS_NA"),
                // raw download link included so the PDF renderer or consumer can use it
                downloadLink: documentLink,
                // optional: HTML anchor if your PDF generator supports HTML in values
                htmlValue: documentLink ? `<a href="${documentLink}" target="_blank" rel="noopener">${pdfDocumentName(documentLink, index) || t("CS_NA")}</a>` : (pdfDocumentName(documentLink, index) || t("CS_NA"))
              };
            }
          })
        : {
          title: t("PT_NO_DOCUMENTS"),
          value: " ",
        },
    }
      values.push({...obj});
      return values;
  } else if (application?.ownershipCategory?.includes("INDIVIDUAL")) {
    let values = [
      {
        title: t(customTitle || "PT_OWNERSHIP_INFO_SUB_HEADER"),
        values: [{ title: t("PT_FORM3_OWNERSHIP_TYPE"), value: t(application?.ownershipCategory) || t("CS_NA") }]
      }
    ];
    owners.map((owner, index) => {
      let doc = [
        { title: t("PT_OWNERSHIP_INFO_NAME"), value: owner?.name || t("CS_NA") },
        { title: t("PT_OWNERSHIP_INFO_MOBILE_NO"), value: owner?.mobileNumber || t("CS_NA") },
        { title: t("PT_OWNERSHIP_INFO_GENDER"), value: t(owner?.gender) || t("CS_NA") },
        { title: t("PT_SEARCHPROPERTY_TABEL_GUARDIANNAME"), value: owner?.fatherOrHusbandName || t("CS_NA") },
        { title: t("PT_COMMON_APPLICANT_RELATIONSHIP_LABEL"), value: t('PT_RELATION_'+owner?.relationship) || t("CS_NA") },
        // { title: t("PT_OWNERSHIP_INFO_EMAIL_ID"), value: owner?.emailId || t("CS_NA") },
        { title: t("PT_OWNERSHIP_INFO_USER_CATEGORY"), value: t(getPropertyOwnerTypeLocale(owner?.ownerType)) || t("CS_NA") },
        { title: t("PT_OWNERSHIP_INFO_CORR_ADDR"), value: owner?.permanentAddress || t("CS_NA") },
        
      ];
      let obj2 = {
          title: t('Owner ') + ` ${index + 1}` + ' documents',
          values:
          owner?.documents && owner?.documents.length > 0
              ? owner?.documents.map((document, index) => {
                  let documentLink = pdfDownloadLink(res?.data, document?.fileStoreId);
                  console.log("documentLink===",documentLink);
                  if(document?.documentType){
                    return {
                      title: t(document?.documentType=="PROPERTY_PHOTO" ? 'PT_PROPERTY_PHOTO' : document?.documentType || t("CS_NA")),
                      // human readable filename shown in PDF
                      value: pdfDocumentName(documentLink, index) || t("CS_NA"),
                      // raw download link included so the PDF renderer or consumer can use it
                      downloadLink: documentLink,
                      // optional: HTML anchor if your PDF generator supports HTML in values
                      htmlValue: documentLink ? `<a href="${documentLink}" target="_blank" rel="noopener">${pdfDocumentName(documentLink, index) || t("CS_NA")}</a>` : (pdfDocumentName(documentLink, index) || t("CS_NA"))
                    };
                  }
                })
              : {
                title: t("PT_NO_DOCUMENTS"),
                value: " ",
              },
        }
      let obj = {
        title: t('Owner ') + ` ${index + 1}`,
        values: doc,
      }
      values.push(obj);
      values.push({...obj2});
      // values.push(...doc);
    });
    console.log("values---", values);
    return values;
    // return {
    //   title: t(customTitle || "PT_OWNERSHIP_INFO_SUB_HEADER"),
    //   values: values,
    // };
  } else if (application?.ownershipCategory?.includes("INSTITUTIONAL")) {
    let values =[{
      title: t("PT_OWNERSHIP_INFO_SUB_HEADER"),
      values: [
        { title: t("PT_COMMON_INSTITUTION_NAME"), value: application?.institution?.name || t("CS_NA") },
        { title: t("PT_TYPE_OF_INSTITUTION"), value: application?.institution?.type ? t('COMMON_MASTERS_OWNERSHIPCATEGORY_'+application?.institution?.type) : t("CS_NA") },
        { title: t("PT_OWNER_NAME"), value: application?.institution?.nameOfAuthorizedPerson || t("CS_NA") },
        { title: t("PT_COMMON_AUTHORISED_PERSON_DESIGNATION"), value: application?.institution?.designation || t("CS_NA") },
        { title: t("PT_FORM3_MOBILE_NUMBER"), value: owners[0]?.mobileNumber || t("CS_NA") },
        { title: t("PT_OWNERSHIP_INFO_TEL_PHONE_NO"), value: owners[0]?.altContactNumber || t("CS_NA") },
        { title: t("PT_OWNERSHIP_INFO_CORR_ADDR"), value: owners[0]?.correspondenceAddress || t("CS_NA") },
        // { title: t("PT_COMMON_APPLICANT_RELATIONSHIP_LABEL"), value: t('PT_RELATION_'+owners[0]?.relationship) || t("CS_NA") },
      ],
    }];
    let obj = {
      
      title: t('Owner Documents'),
      values:
      owners[0]?.documents && owners[0]?.documents.length > 0
          ? owners[0]?.documents.map((document, index) => {
              let documentLink = pdfDownloadLink(res?.data, document?.fileStoreId);
              console.log("documentLink===",documentLink);
              if(document?.documentType){
                return {
                  title: t(document?.documentType=="PROPERTY_PHOTO" ? 'PT_PROPERTY_PHOTO' : document?.documentType || t("CS_NA")),
                  // human readable filename shown in PDF
                  value: pdfDocumentName(documentLink, index) || t("CS_NA"),
                  // raw download link included so the PDF renderer or consumer can use it
                  downloadLink: documentLink,
                  // optional: HTML anchor if your PDF generator supports HTML in values
                  htmlValue: documentLink ? `<a href="${documentLink}" target="_blank" rel="noopener">${pdfDocumentName(documentLink, index) || t("CS_NA")}</a>` : (pdfDocumentName(documentLink, index) || t("CS_NA"))
                };
              }
            })
          : {
            title: t("PT_NO_DOCUMENTS"),
            value: " ",
          },
    }
    values.push({...obj});
    return values;

  } else {
    return {
      title: t("PT_OWNERSHIP_INFO_SUB_HEADER"),
      values: [{ title: t("PT_NO_OWNERS"), value: t("CS_NA") }],
    };
  }
};

const getAssessmentInfo = (application, t) => {
  let values = [
    { title: t("PT_COMMONS_PROPERTY_USAGE_TYPE"), value: application?.usageCategory ? `${t(
      (application?.usageCategory !== "RESIDENTIAL" ? "COMMON_PROPUSGTYPE_NONRESIDENTIAL_" : "COMMON_PROPUSGTYPE_") +
        (application?.usageCategory?.split(".")[1] ? application?.usageCategory?.split(".")[1] : application?.usageCategory)
    )}` : t("CS_NA") },
    { title: t("PT_ASSESMENT_INFO_TYPE_OF_BUILDING"), value: t(getPropertyTypeLocale(application?.propertyType)) || t("CS_NA") },
    { title: t("PT_ASSESMENT_INFO_PLOT_SIZE"), value: t(application?.landArea) || t("CS_NA") },
    { title: t("PT_ASSESMENT_INFO_NO_OF_FLOOR"), value: t(application?.noOfFloors) || t("CS_NA") },
     { title: "Vacant Land Usage Type", value: (ptCalculationEstimateData?.Calculation[0]?.vacantland[0] && ptCalculationEstimateData?.Calculation[0]?.vacantland[0]?.vacantlandtype) ? "COMMON_PROPUSGTYPE_NONRESIDENTIAL_"+ptCalculationEstimateData?.Calculation[0]?.vacantland[0]?.vacantlandtype : ''},
    { title: "APV of Vacant Land", value: ptCalculationEstimateData?.Calculation[0]?.vacantland[0]?.vacantlandamount }
  ];
  return {
    title: t("PT_ASSESMENT_INFO_SUB_HEADER"),
    values: [
        { title: t("PT_COMMONS_PROPERTY_USAGE_TYPE"), value: application?.usageCategory ? `${t(
        (application?.usageCategory !== "RESIDENTIAL" ? "COMMON_PROPUSGTYPE_NONRESIDENTIAL_" : "COMMON_PROPUSGTYPE_") +
            (application?.usageCategory?.split(".")[1] ? application?.usageCategory?.split(".")[1] : application?.usageCategory)
        )}` : t("CS_NA") },
        { title: t("PT_ASSESMENT_INFO_TYPE_OF_BUILDING"), value: t(getPropertyTypeLocale(application?.propertyType)) || t("CS_NA") },
        { title: t("PT_ASSESMENT_INFO_PLOT_SIZE"), value: t(application?.landArea) || t("CS_NA") },
        { title: t("PT_ASSESMENT_INFO_NO_OF_FLOOR"), value: t(application?.noOfFloors) || t("CS_NA") },
        { title: "Vacant Land Usage Type", value: (ptCalculationEstimateData?.Calculation[0]?.vacantland[0] && ptCalculationEstimateData?.Calculation[0]?.vacantland[0]?.vacantlandtype) ? "COMMON_PROPUSGTYPE_NONRESIDENTIAL_"+ptCalculationEstimateData?.Calculation[0]?.vacantland[0]?.vacantlandtype : ''},
        { title: "APV of Vacant Land", value: ptCalculationEstimateData?.Calculation[0]?.vacantland[0]?.vacantlandamount }
    ],
  };
};

const getUnitInfo = (application, t) => {
  let values = [];
  application.units = application?.units?.filter((unit) => unit.active == true) || [];
  let flrno = null,
    i = 0;
  flrno = application.units && application.units[0]?.floorNo;
  if(application.units?.length>1)
  application.units.sort((a,b) => a.floorNo - b.floorNo);
  console.log("application.units===",application.units);
  application.units.map((unit) => {
    if (unit?.floorNo !== flrno) {
      i = 1;
      flrno = unit?.floorNo;
    } else {
      i += 1;
    }
    let doc = [
      
      { title: t("PT_UNIT")+" "+ i },
      {
        title: t(""),
      },
      {
        title: t(""),
      },
      {
        title: t(""),
      },
     
      {
        title: (flrno = unit?.floorNo) > -3 ? t("PT_ASSESSMENT_UNIT_USAGE_TYPE") : "",
        value: (flrno = unit?.floorNo) > -3 ? t(getPropertySubUsageTypeLocale(unit?.usageCategory)) || t("CS_NA") : "",
      },      
      {
        title: (flrno = unit?.floorNo) > -3 ? t("PT_ASSESMENT_INFO_OCCUPLANCY") : "",
        value: (flrno = unit?.floorNo) > -3 ? t(getPropertyOccupancyTypeLocale(unit?.occupancyType)) || t("CS_NA") : "",
      },
      {
        title: (flrno = unit?.floorNo) > -3 ? t("PT_STRUCTURE_TYPE") : "",
        value: (flrno = unit?.floorNo) > -3 ? t("PROPERTYTAX_STRUCTURETYPE_" +unit?.structureType) || t("CS_NA") : "",
      },
      {
        title: (flrno = unit?.floorNo) > -3 ? t("PT_AGE_OF_PROPERTY") : "",
        value: (flrno = unit?.floorNo) > -3 ? t("PROPERTYTAX_AGEOFPROPERTY_" +unit?.ageOfProperty) || t("CS_NA") : "",
      },
      {
        title: (flrno = unit?.floorNo) > -3 ? t("PT_FORM2_BUILT_AREA") : "",
        value: (flrno = unit?.floorNo) > -3 ? t(unit?.constructionDetail?.builtUpArea) || t("CS_NA") : "",
      },
      {
        title:
          (flrno = unit?.floorNo) > -3
            ? t(getPropertyOccupancyTypeLocale(unit?.occupancyType)) === "Rented"
              ? t("PT_FORM2_TOTAL_ANNUAL_RENT")
              : t("")
            : "",
        value:
          (flrno = unit?.floorNo) > -3
            ? t(getPropertyOccupancyTypeLocale(unit?.occupancyType)) === "Rented"
              ? (unit?.arv && `₹${t(unit?.arv)}`) || "NA"
              : t("")
            : "",
      }
    ];
    let obj = {
      title: i===1 ? t(`PROPERTYTAX_FLOOR_${unit?.floorNo}`) : "",
      values: doc,
    }
    
    values.push(obj);
  });
  console.log("values---", values);
  return values;
  // return {
  //   title: t("PT_ASSESMENT_INFO_SUB_HEADER"),
  //   values: values,
  // };
};

const getMutationDetails = (application, t) => {
  return {
    title: t("PT_MUTATION_DETAILS"),
    values: [
      {
        title: t("PT_MUTATION_COURT_PENDING_OR_NOT"),
        value: application?.additionalDetails?.isMutationInCourt
          ? t(`PT_MUTATION_PENDING_${application?.additionalDetails.isMutationInCourt}`)
          : t("CS_NA"),
      },
      { title: t("PT_MUTATION_COURT_CASE_DETAILS"), value: application?.additionalDetails?.caseDetails || t("CS_NA") },
      { title: t("PT_MUTATION_STATE_ACQUISITION"), value: application?.additionalDetails?.isPropertyUnderGovtPossession ? t(`PT_MUTATION_STATE_ACQUISITION_${application?.additionalDetails?.isPropertyUnderGovtPossession}`) : t("CS_NA") },
      { title: t("PT_MUTATION_GOVT_ACQUISITION_DETAILS"), value: application?.additionalDetails?.govtAcquisitionDetails || t("CS_NA") },
    ],
  };
};

const mutationRegistrationDetails = (application, t) => {
  return {
    title: t("PT_MUTATION_REGISTRATION_DETAILS"),
    values: [
      {
        title: t("PT_MUTATION_TRANSFER_REASON"),
        value: t(`PROPERTYTAX_REASONFORTRANSFER_${application?.additionalDetails?.reasonForTransfer.replaceAll(".", "_")}`),
      },
      { title: t("PT_MUTATION_MARKET_VALUE"), value: application?.additionalDetails?.marketValue || t("CS_NA") },
      { title: t("PT_MUTATION_DOCUMENT_NO"), value: application?.additionalDetails?.documentNumber || t("CS_NA") },
      { title: t("PT_MUTATION_DOCUMENT_VALUE"), value: application?.additionalDetails?.documentValue || t("CS_NA") },
      {
        title: t("PT_MUTATION_DOCUMENT_ISSUE_DATE"),
        value: application?.additionalDetails?.documentDate ? new Date(application?.additionalDetails?.documentDate).toDateString() : t("CS_NA"),
      },
      {
        title: t(""),
      },
      { title: t("PT_MUTATION_REMARKS"), value: application?.additionalDetails?.remarks || t("CS_NA") },
    ],
  };
};

const calculateTaxableAPVForDownload = (taxHeadEstimates) => {
  let val = 0;
  if(taxHeadEstimates && taxHeadEstimates.length>0) {
    val = taxHeadEstimates
      .filter(i =>
        ["PT_TAX", "PT_VACANT_LAND_EXEMPTION"]
          .includes(i.taxHeadCode)
      )
      .reduce((sum, i) => sum + (i.estimateAmount || 0), 0);

  }
  return val.toFixed(2)
}
const calculateApplicableRebateForDownload = (taxHeadEstimates, taxableAPV) => {
  let filteredData;
  if(taxHeadEstimates && taxHeadEstimates.length>0) {
    filteredData = taxHeadEstimates
      .filter(i =>
        ["PT_MODEOFPAYMENT_REBATE"]
          .includes(i.taxHeadCode)
      )
      //.reduce((sum, i) => sum + (i.estimateAmount || 0), 0);

  }
  // console.log("calculatedPercentage==",filteredData)
  let val = 0;
  if(filteredData && filteredData.length>0) {
    let per = filteredData[0]?.calculatedPercentage / 100;
    val = taxableAPV * per
  }
  // console.log("val==",val);
  return val.toFixed(2);

  // val = val.toFixed(2);

}
const calculateAnnualPropertyTaxForDownload = (taxableAPV, applicableRebate) => {
  
  let val = 0;
  if(taxableAPV) {
    val = taxableAPV
  }
  // console.log("val==",val);
  let APT = val * 0.08;
  return APT.toFixed(2);

  // val = val.toFixed(2);

}

const getPTAssessmentData = async (application, billingPeriod, calculationData, tenantInfo, t) => {
  console.log("application in getPTAssessmentData===",application, billingPeriod, calculationData, tenantInfo);
  // Taxable APV
    const base = calculationData?.taxHeadEstimates || [];
    console.log("base===",base);
    
    const taxableAPV = calculateTaxableAPVForDownload(base);
    const applicableRebate = calculateApplicableRebateForDownload(base, taxableAPV);
    const annualPropertyTax = calculateAnnualPropertyTaxForDownload(taxableAPV, applicableRebate);
    
    // clone, then inject BEFORE the last item (or at end if length < 1)
    const arr = [...base];

    const alreadyTaxableAPV = arr.some(e => e.taxHeadCode === "PT_TAXABLE_APV");
    if (!alreadyTaxableAPV) {
      const insertAt = Math.max(arr.length - 4, 0);
      arr.splice(insertAt, 0, {
        taxHeadCode: "PT_TAXABLE_APV",
        estimateAmount: taxableAPV,
        category: "REBATE",
      });
    } else {
      // if it already exists, update the amount (optional)
      const idx = arr.findIndex(e => e.taxHeadCode === "PT_TAXABLE_APV");
      if (idx > -1) arr[idx] = { ...arr[idx], estimateAmount: taxableAPV };
    }
    const APT = arr.some(e => e.taxHeadCode === "PT_ANNUAL_PROPERTY_TAX");
    if (!APT) {
      const insertAt = Math.max(arr.length - 4, 0);
      arr.splice(insertAt, 0, {
        taxHeadCode: "PT_ANNUAL_PROPERTY_TAX",
        estimateAmount: annualPropertyTax,
        category: "REBATE",
      });
    } else {
      // if it already exists, update the amount (optional)
      const idx = arr.findIndex(e => e.taxHeadCode === "PT_ANNUAL_PROPERTY_TAX");
      if (idx > -1) arr[idx] = { ...arr[idx], estimateAmount: annualPropertyTax };
    }
    
    const taxHeadEstimates = arr;

  // End of Taxable APV


  const filesArray = application?.documents?.map((value) => value?.fileStoreId).filter( (el)=> el);
  const tenant = Digit.ULBService.getStateId();
  const res = filesArray?.length>0 && await Digit.UploadServices.Filefetch(filesArray, tenant);

  // const res = filesArray?.length>0 && await Digit.UploadServices.Filefetch(filesArray, tenantInfo?.code || Digit.ULBService.getStateId());
  const ensureArray = (v) => (Array.isArray(v) ? v : [v]);
  let address_to_display=application?.address;
    if(address_to_display?.doorNo){
        address_to_display=address_to_display?.doorNo+','+address_to_display?.principalRoadName+','+t("PROPERTYTAX_ROADTYPE_"+address_to_display?.typeOfRoad?.code)+','+address_to_display?.locality?.name+','+address_to_display?.city;
    }
    else{
        address_to_display=address_to_display?.principalRoadName+','+t("PROPERTYTAX_ROADTYPE_"+address_to_display?.typeOfRoad?.code)+','+address_to_display?.locality?.name+','+address_to_display?.city;
    }

    console.log("taxHeadEstimates===",taxHeadEstimates);

  return {
    t: t,
    tenantId: tenantInfo?.code,
    // name: `${t(tenantInfo?.i18nKey)} ${ulbCamel(t(`ULBGRADE_${tenantInfo?.city?.ulbGrade.toUpperCase().replace(" ", "_").replace(".", "_")}`))}`,
    name: `${t(tenantInfo?.i18nKey)} ${(t(`${tenantInfo?.city?.ulbGrade}`))}`,
    email: tenantInfo?.emailId,
    phoneNumber: tenantInfo?.contactNumber,
    heading: t("Assessment Details"),
    details: [
        {
            title: t("CS_TITLE_APPLICATION_DETAILS"),
            values: [
            //   { title: t("PT_APPLICATION_NO"), value: application?.acknowldgementNumber },
            { title: t("PT_PROPERRTYID"), value: application?.propertyId },
            { title: t("PT_PROPERTY_ADDRESS_SUB_HEADER"), value: address_to_display || t("CS_NA") },
            {title: t("ES_PT_TITLE_BILLING_PERIOD"), value: billingPeriod || t("CS_NA")},
            //   {
            //     title: t("PT_BILLING_DUE_DATE"),
            //     value: Digit.DateUtils.ConvertTimestampToDate(application?.auditDetails?.createdTime, "dd/MM/yyyy"),
            //   },
            //   { title: t("Apportioned Property"), value: application?.isPartOfProperty ? t("PT_COMMON_YES") : t("PT_COMMON_NO") }
            ],
        },
        {
            title: t("Property Tax Amount"),
            values: [
                ...taxHeadEstimates
                ?.filter((e) => e.taxHeadCode !== "PT_COMPLEMENTARY_REBATE" && e.taxHeadCode !== "PT_ROUNDOFF")
                ?.map((estimate, index) => {
                    return {
                        title: t(estimate.taxHeadCode),
                        value: `₹ ${estimate.estimateAmount || 0}`,
                    };
                }) || [],
                {
                    title: t("ES_PT_TITLE_TOTAL_DUE_AMOUNT"),
                    value: `₹ ${calculationData?.totalAmount || 0}`,
                }
            ],
        },
        {
            title: t("PT_ASSESMENT_INFO_SUB_HEADER"),
            values: [
                { title: t("PT_COMMONS_PROPERTY_USAGE_TYPE"), value: application?.usageCategory ? `${t(
                (application?.usageCategory !== "RESIDENTIAL" ? "COMMON_PROPUSGTYPE_NONRESIDENTIAL_" : "COMMON_PROPUSGTYPE_") +
                    (application?.usageCategory?.split(".")[1] ? application?.usageCategory?.split(".")[1] : application?.usageCategory)
                )}` : t("CS_NA") },
                { title: t("PT_ASSESMENT_INFO_TYPE_OF_BUILDING"), value: t(getPropertyTypeLocale(application?.propertyType)) || t("CS_NA") },
                { title: t("PT_ASSESMENT_INFO_PLOT_SIZE"), value: t(application?.landArea) || t("CS_NA") },
                { title: t("PT_ASSESMENT_INFO_NO_OF_FLOOR"), value: t(application?.noOfFloors) || t("CS_NA") },
                { title: "Vacant Land Usage Type", value: (calculationData?.vacantland[0] && calculationData?.vacantland[0]?.vacantlandtype) ? "COMMON_PROPUSGTYPE_NONRESIDENTIAL_"+calculationData?.vacantland[0]?.vacantlandtype : 'NA'},
                { title: "APV of Vacant Land", value: calculationData?.vacantland[0]?.vacantlandamount }
            ],
        },
        {
            title: t("Unit Details"),
            values: calculationData?.units
            // ?.filter((e) => e.active)
            ?.sort?.((a, b) => a.floorNo - b.floorNo)
            ?.map((unit, index) => {
                let floorName = t(`PROPERTYTAX_FLOOR_${unit.floorNo}`);
                return [
                {
                    title: `${t("ES_APPLICATION_DETAILS_UNIT")} ${index + 1}`,
                },
                {
                    title: "Floor No",
                    value: floorName,
                },
                {
                    title: t("PT_ASSESSMENT_UNIT_USAGE_TYPE"),
                    // value: `PROPERTYTAX_${ unit?.usageCategory
                    value: t(`PROPERTYTAX_${ unit?.usageCategoryMajor
                    }`),
                },
                {
                    title: t("PT_STRUCTURE_TYPE"),
                    value: t(`PROPERTYTAX_STRUCTURETYPE_${ unit?.structureType}`),
                },
                {
                    title: t("PT_ASSESMENT_INFO_OCCUPLANCY"),
                    value: t(`PROPERTYTAX_OCCUPANCYTYPE_${unit?.occupancyType}`),
                },
                {
                    title: "Built Up Area (sq ft)",
                    value: unit?.unitArea
                    // value: unit?.constructionDetail?.builtUpArea,
                },
                {
                    title: "APV of Covered Area",
                    value: unit?.taxamount || 0,
                },
                ];
            }),
        },
        
        
    //   ...ensureArray(getOwner(application, t,'',res)),
    //   ...ensureArray(getUnitInfo(application, t)),
    //   {
    //     title: t("PT_PROPERTY_ADDRESS_SUB_HEADER"),
    //     values: [
    //       { title: t("PT_PROPERTY_ADDRESS_PINCODE"), value: application?.address?.pincode || t("CS_NA") },
    //       { title: t("PT_PROPERTY_ADDRESS_CITY"), value: t(getCityLocale(application?.tenantId)) || t("CS_NA") },
    //       {
    //         title: t("PT_PROPERTY_ADDRESS_MOHALLA"),
    //         value: application?.address?.locality?.name || t("CS_NA"),
    //       },
    //       { title: t("PT_PROPERTY_ADDRESS_STREET_NAME"), value: application?.address?.street || t("CS_NA") },
    //       { title: t("PT_PROPERTY_ADDRESS_HOUSE_NO"), value: application?.address?.doorNo || t("CS_NA") },
    //       { title: t("Dag No."), value: application?.address?.dagNo || t("CS_NA") },
    //       { title: t("Patta No."), value: application?.address?.pattaNo || t("CS_NA") },
    //       { title: t("Principal Road Name"), value: application?.address?.principalRoadName || t("CS_NA") },
    //       { title: t("Sub-Side Road Name"), value: application?.address?.subSideRoadName || t("CS_NA") },
    //       { title: t("Type of Road"), value: t(getTypeOfRoad(application?.address?.typeOfRoad?.code)) || t("CS_NA") },

    //       application?.channel === "CITIZEN" ? { title: t("PT_PROPERTY_ADDRESS_LANDMARK"), value: application?.address?.landmark || t("CS_NA") }: {},
    //     ],
    //   },
    //   {
    //     title: t("PT_COMMON_DOCS"),
    //     values:
    //     application.documents && application.documents.length > 0
    //         ? application.documents.map((document, index) => {
    //           console.log("res?.data==",res?.data);
    //             let documentLink = pdfDownloadLink(res?.data, document?.fileStoreId);
    //             console.log("documentLink===",documentLink);
    //             if(document?.documentType){
    //               return {
    //                 title: t(document?.documentType=="PROPERTY_PHOTO" ? 'PT_PROPERTY_PHOTO' : document?.documentType || t("CS_NA")),
    //                 // human readable filename shown in PDF
    //                 value: pdfDocumentName(documentLink, index) || t("CS_NA"),
    //                 // raw download link included so the PDF renderer or consumer can use it
    //                 downloadLink: documentLink,
    //                 // optional: HTML anchor if your PDF generator supports HTML in values
    //                 htmlValue: documentLink ? `<a href="${documentLink}" target="_blank" rel="noopener">${pdfDocumentName(documentLink, index) || t("CS_NA")}</a>` : (pdfDocumentName(documentLink, index) || t("CS_NA"))
    //               };
    //             }
    //           })
    //         : {
    //           title: t("PT_NO_DOCUMENTS"),
    //           value: " ",
    //         },
    //   },
    ],
  };
};

export default getPTAssessmentData;
