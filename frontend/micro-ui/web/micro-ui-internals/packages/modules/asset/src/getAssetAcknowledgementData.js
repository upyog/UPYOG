/*
*  @author - Shivank Shukla - NIUA
*/

const capitalize = (text) => text.substr(0, 1).toUpperCase() + text.substr(1);
const ulbCamel = (ulb) => ulb.toLowerCase().split(" ").map(capitalize).join(" ");



const getAssessmentInfo = (application, t) => {
  let values = [
    ...(application?.assetParentCategory=== "LAND"
      ? [
      { title: t("AST_TYPE"), value: application?.additionalDetails?.assetParentCategory },
      { title: t("AST_LAND_AREA"), value: application?.additionalDetails?.Area },
      { title: t("AST_AWARD_NUMBER"), value: application?.additionalDetails?.AwardNumber },
      { title: t("AST_BOOK_VALUE"), value: application?.additionalDetails?.BookValue  + " Rupees" },
      { title: t("AST_CURRENT_VALUE"), value: application?.additionalDetails?.Currentassetvalue   + " Rupees" },
      { title: t("AST_COLLECT_ORDER_NUM"), value: application?.additionalDetails?.CollectororderNumber },
      { title: t("AST_DEPRICIATION_RATE"), value: application?.additionalDetails?.DepriciationRate },
      { title: t("AST_COST_AFTER_DEPRICIAT"), value: application?.additionalDetails?.Costafterdepriciation  + " Rupees"},
      { title: t("AST_COUNCIL_RES_NUM"), value: application?.additionalDetails?.CouncilResolutionNumber },
      { title: t("AST_GOVT_ORDER_NUM"), value: application?.additionalDetails?.GovernmentorderNumber },
      { title: t("AST_FROM_DEED_TAKEN"), value: application?.additionalDetails?.FromWhomDeedTaken },
      { title: t("AST_HOW_ASSET_USE"), value: application?.additionalDetails?.howassetbeingused },
      { title: t("AST_FENCED"), value: application?.additionalDetails?.isitfenced },
      { title: t("AST_ANY_BUILTUP"), value: application?.additionalDetails?.AnyBuiltup },
      { title: t("AST_REVENUE_GENERATED"), value: application?.additionalDetails?.Revenuegeneratedbyasset  + " Rupees"},
      { title: t("AST_TOTAL_COST"), value: application?.additionalDetails?.Totalcost  + " Rupees"},
      ]

      :[]),

      ...(application?.assetParentCategory === "BUILDING"
        ? [
          { title: t("AST_TYPE"), value: application?.additionalDetails?.assetParentCategory },
          { title: t("AST_PLINTH_AREA"), value: application?.additionalDetails?.plintharea},
          { title: t("AST_PLOT_AREA"), value: application?.additionalDetails?.plotarea },
          { title: t("AST_FLOORS_NO"), value: application?.additionalDetails?.floorno },
          { title: t("AST_AREA_FLOOR"), value: application?.additionalDetails?.floorarea },
          { title: t("AST_DIMENSIONS"), value: application?.additionalDetails?.dimensions },
          { title: t("AST_TOTAL_COST"), value: application?.additionalDetails?.Totalcost + " Rupees"},
          { title: t("AST_REVENUE_GENERATED"), value: application?.additionalDetails?.Revenuegeneratedbyasset + " Rupees"},
          { title: t("AST_DEPRICIATION_RATE"), value: application?.additionalDetails?.DepriciationRate },
          { title: t("AST_COST_AFTER_DEPRICIAT"), value: application?.additionalDetails?.Costafterdepriciation + " Rupees"},
          { title: t("AST_CURRENT_VALUE"), value: application?.additionalDetails?.Currentassetvalue + " Rupees"},
          { title: t("AST_BUILDING_NO"), value: application?.additionalDetails?.Buildingsno},
          { title: t("AST_BOOK_VALUE"), value: application?.additionalDetails?.BookValue + " Rupees"},
          ]
        : []),

        ...(application?.assetParentCategory === "SERVICE"
          ? [
            { title: t("AST_TYPE"), value: application?.additionalDetails?.assetAssetSubCategory },
            { title: t("AST_ROAD_TYPE"), value: application?.additionalDetails?.RoadType?.code },
            { title: t("AST_ROAD_WIDTH"), value: application?.additionalDetails?.RoadWidth },
            { title: t("AST_SURFACE_TYPE"), value: application?.additionalDetails?.SurfaceType?.code},
            { title: t("AST_LAST_MAINTAINENCE"), value: application?.additionalDetails?.LastMaintainence },
            { title: t("AST_NEXT_MAINTAINENCE"), value: application?.additionalDetails?.NextMaintainence },
            { title: t("AST_PROTECTION_LENGTH"), value: application?.additionalDetails?.ProtectionLength },
            { title: t("AST_PEDASTRIAN_CROSSING_NUMBER"), value: application?.additionalDetails?.NumofPedastrianCross },
            { title: t("AST_METROSTATION_NUMBER"), value: application?.additionalDetails?.NumofMetroStation },
            { title: t("AST_FOOTPATH_NUMBER"), value: application?.additionalDetails?.NumofFootpath },
            { title: t("AST_BUSSTOP_NUMBER"), value: application?.additionalDetails?.NumofBusStop },
            { title: t("AST_CYCLETRACK_LENGTH"), value: application?.additionalDetails?.LengthofCycletrack },
            { title: t("AST_DRAINAGE_CHANNEL_LENGTH"), value: application?.additionalDetails?.DrainageLength },
            { title: t("AST_TOTAL_COST"), value: application?.additionalDetails?.Totalcost + " Rupees"},
            { title: t("AST_DEPRICIATION_RATE"), value: application?.additionalDetails?.DepriciationRate },
            { title: t("AST_COST_AFTER_DEPRICIAT"), value: application?.additionalDetails?.Costafterdepriciation + " Rupees"},
            { title: t("AST_BOOK_VALUE"), value: application?.additionalDetails?.BookValue + " Rupees"},
            ]
          :[]),

  


  ];
 
  return {
    
    title: t("AST_DETAILS"),
    values: values,
  };
};





const getAssetAcknowledgementData = async (application, tenantInfo, t) => {
  

  return {
    t: t,
    tenantId: tenantInfo?.code,
    name: `${t(tenantInfo?.i18nKey)} ${ulbCamel(t(`ULBGRADE_${tenantInfo?.city?.ulbGrade.toUpperCase().replace(" ", "_").replace(".", "_")}`))}`,
    email: tenantInfo?.emailId,
    phoneNumber: tenantInfo?.contactNumber,
    heading: t("ASSET_MANAGEMENT_ACKNOWLEDGEMENT"),
    details: [
      {
        title: t("AST_COMMON_DETAILS"),
        values: [
          { title: t("AST_APPLICATION_NUMBER"), value: application?.applicationNo },
          { title: t("AST_BOOK_REF_SERIAL_NUM"), value: application?.assetBookRefNo},
          { title: t("AST_CATEGORY"), value: application?.assetClassification },
          { title: t("AST_PARENT_CATEGORY"), value: application?.assetParentCategory},
          { title: t("AST_SUB_CATEGORY"), value: application?.additionalDetails?.assetAssetSubCategory},
        ],
      },
    
      getAssessmentInfo(application, t),
      {
        
        title: t("AST_ADDRESS_DETAILS"),
        values: [
          { title: t("AST_PINCODE"),        value: application?.addressDetails?.pincode },
          { title: t("MYCITY_CODE_LABEL"),  value: application?.addressDetails?.city },
          { title: t("AST_DOOR_NO"),        value: application?.addressDetails?.doorNo },
          { title: t("AST_STREET"),         value: application?.addressDetails?.street },
          { title: t("AST_ADDRESS_LINE_1"), value: application?.addressDetails?.addressLine1 },
          { title: t("AST_ADDRESS_LINE_2"), value: application?.addressDetails?.addressLine2 },
        ],
      },
     
    ],
  };
};

export default getAssetAcknowledgementData;