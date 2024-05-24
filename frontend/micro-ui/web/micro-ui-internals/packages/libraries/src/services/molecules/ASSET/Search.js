
import { ASSETService } from "../../elements/ASSET";

export const ASSETSearch = {
  
  all: async (tenantId, filters = {}) => {
    console.log("filterrsrs",filters)
    
    const response = await ASSETService.search({ tenantId, filters });
    
    return response;
  },

  
  application: async (tenantId, filters = {}) => {
    const response = await ASSETService.search({ tenantId, filters });
   
    return response.Assets[0];
  },
  RegistrationDetails: ({ Assets: response, t }) => {
    console.log("responseeeeee",response);
    return [

      {
        title: "AST_COMMON_DETAILS",
        asSectionHeader: true,
        values: [
          { title: "AST_APPLICATION_NUMBER", value: response?.applicationNo },
          { title: "AST_BOOK_REF_SERIAL_NUM", value: response?.assetBookRefNo},
          { title: "AST_CATEGORY", value: response?.assetClassification },
          { title: "AST_PARENT_CATEGORY", value: response?.assetParentCategory},
          { title: "AST_SUB_CATEGORY", value: response?.additionalDetails?.assetAssetSubCategory},

          
          
          
        ],
      },

      {
        title: "AST_ADDRESS_DETAILS",
        asSectionHeader: true,
        values: [
          { title: "AST_PINCODE", value: response?.addressDetails?.pincode },
          { title: "MYCITY_CODE_LABEL", value: response?.addressDetails?.city },
          { title: "AST_DOOR_NO", value: response?.addressDetails?.doorNo },
          { title: "AST_STREET", value: response?.addressDetails?.street },
          { title: "AST_ADDRESS_LINE_1", value: response?.addressDetails?.addressLine1 },
          { title: "AST_ADDRESS_LINE_2", value: response?.addressDetails?.addressLine2 },


        ],
      },

      
      {
        title: "AST_DETAILS",
        asSectionHeader: true,
        values: [
          ...(response?.assetParentCategory=== "LAND"
            ? [
                { title: "AST_TYPE", value: response?.additionalDetails?.assetParentCategory },
                { title: "AST_LAND_AREA", value: response?.additionalDetails?.Area },
                { title: "AST_AWARD_NUMBER", value: response?.additionalDetails?.AwardNumber },
                { title: "AST_BOOK_VALUE", value: response?.additionalDetails?.BookValue  + " Rupees" },
                { title: "AST_CURRENT_VALUE", value: response?.additionalDetails?.Currentassetvalue   + " Rupees" },
                { title: "AST_COLLECT_ORDER_NUM", value: response?.additionalDetails?.CollectororderNumber },
                { title: "AST_DEPRICIATION_RATE", value: response?.additionalDetails?.DepriciationRate },
                { title: "AST_COST_AFTER_DEPRICIAT", value: response?.additionalDetails?.Costafterdepriciation  + " Rupees"},
                { title: "AST_COUNCIL_RES_NUM", value: response?.additionalDetails?.CouncilResolutionNumber },
                { title: "AST_GOVT_ORDER_NUM", value: response?.additionalDetails?.GovernmentorderNumber },
                { title: "AST_FROM_DEED_TAKEN", value: response?.additionalDetails?.FromWhomDeedTaken },
                { title: "AST_HOW_ASSET_USE", value: response?.additionalDetails?.howassetbeingused },
                { title: "AST_FENCED", value: response?.additionalDetails?.isitfenced?.code },
                { title: "AST_ANY_BUILTUP", value: response?.additionalDetails?.AnyBuiltup?.code },
                { title: "AST_REVENUE_GENERATED", value: response?.additionalDetails?.Revenuegeneratedbyasset  + " Rupees"},
                { title: "AST_TOTAL_COST", value: response?.additionalDetails?.Totalcost  + " Rupees"},
                

              ]
            : []),
          ...(response?.assetParentCategory === "BUILDING"
            ? [
              { title: "AST_TYPE", value: response?.additionalDetails?.assetParentCategory },
              { title: "AST_PLINTH_AREA", value: response?.additionalDetails?.plintharea},
              { title: "AST_PLOT_AREA", value: response?.additionalDetails?.plotarea },
              { title: "AST_FLOORS_NO", value: response?.additionalDetails?.floorno },
              { title: "AST_AREA_FLOOR", value: response?.additionalDetails?.floorarea },
              { title: "AST_DIMENSIONS", value: response?.additionalDetails?.dimensions },
              { title: "AST_TOTAL_COST", value: response?.additionalDetails?.Totalcost + " Rupees"},
              { title: "AST_REVENUE_GENERATED", value: response?.additionalDetails?.Revenuegeneratedbyasset + " Rupees"},
              { title: "AST_DEPRICIATION_RATE", value: response?.additionalDetails?.DepriciationRate },
              { title: "AST_COST_AFTER_DEPRICIAT", value: response?.additionalDetails?.Costafterdepriciation + " Rupees"},
              { title: "AST_CURRENT_VALUE", value: response?.additionalDetails?.Currentassetvalue + " Rupees"},
              { title: "AST_BUILDING_NO", value: response?.additionalDetails?.Buildingsno},
              { title: "AST_BOOK_VALUE", value: response?.additionalDetails?.BookValue + " Rupees"},
              ]
            : []),
          
          ...(response?.assetParentCategory === "SERVICE"
          ? [
            { title: "AST_TYPE", value: response?.additionalDetails?.assetAssetSubCategory },
            { title: "AST_ROAD_TYPE", value: response?.additionalDetails?.RoadType?.code },
            { title: "AST_ROAD_WIDTH", value: response?.additionalDetails?.RoadWidth },
            { title: "AST_SURFACE_TYPE", value: response?.additionalDetails?.SurfaceType?.code},
            { title: "AST_LAST_MAINTAINENCE", value: response?.additionalDetails?.LastMaintainence },
            { title: "AST_NEXT_MAINTAINENCE", value: response?.additionalDetails?.NextMaintainence },
            { title: "AST_PROTECTION_LENGTH", value: response?.additionalDetails?.ProtectionLength },
            { title: "AST_PEDASTRIAN_CROSSING_NUMBER", value: response?.additionalDetails?.NumofPedastrianCross },
            { title: "AST_METROSTATION_NUMBER", value: response?.additionalDetails?.NumofMetroStation },
            { title: "AST_FOOTPATH_NUMBER", value: response?.additionalDetails?.NumofFootpath },
            { title: "AST_BUSSTOP_NUMBER", value: response?.additionalDetails?.NumofBusStop },
            { title: "AST_CYCLETRACK_LENGTH", value: response?.additionalDetails?.LengthofCycletrack },
            { title: "AST_DRAINAGE_CHANNEL_LENGTH", value: response?.additionalDetails?.DrainageLength },
            { title: "AST_TOTAL_COST", value: response?.additionalDetails?.Totalcost + " Rupees"},
            { title: "AST_DEPRICIATION_RATE", value: response?.additionalDetails?.DepriciationRate },
            { title: "AST_COST_AFTER_DEPRICIAT", value: response?.additionalDetails?.Costafterdepriciation + " Rupees"},
            { title: "AST_BOOK_VALUE", value: response?.additionalDetails?.BookValue + " Rupees"},
            ]
          :[]),

          ...(response?.additionalDetails?.assetAssetSubCategory === "Vehicles"
          ? [
            { title: "AST_TYPE", value: response?.additionalDetails?.assetAssetSubCategory },
            { title: "AST_ACQUIRED_SOURCE", value: response?.additionalDetails?.acquiredFrom },
            { title: "AST_ACQUISTION_DATE", value: response?.additionalDetails?.asquistionDate },
            { title: "AST_CHASIS_NUMBER", value: response?.additionalDetails?.chasisNumber },
            { title: "AST_ENGINE_NUMBER", value: response?.additionalDetails?.engineNumber },
            { title: "AST_REGISTRATION_NUMBER ", value: response?.additionalDetails?.registrationNumber },
            { title: "AST_PARKING_LOCATION", value: response?.additionalDetails?.parkingLocation },
            { title: "AST_IMPROVEMENT_COST", value: response?.additionalDetails?.improvementCost + " Rupees"},
            { title: "AST_TOTAL_COST", value: response?.additionalDetails?.Totalcost + " Rupees"},
            { title: "AST_CURRENT_VALUE", value: response?.additionalDetails?.Currentassetvalue + " Rupees"},
            { title: "AST_BOOK_VALUE", value: response?.additionalDetails?.BookValue + " Rupees"},
          ]
          :[]),

        ],
      },

      {
        title: "AST_DOCUMENT_DETAILS",
        additionalDetails: {
          
          documents: [
            {
             
              values: response?.documents
                ?.map((document) => {
                  console.log("documnet",document);

                  return {
                    title: `ASSET_${document?.documentType}`,
                    documentType: document?.documentType,
                    documentUid: document?.documentUid,
                    fileStoreId: document?.fileStoreId,
                    status: document.status,
                  };
                }),
            },
          ],
        },
      },
    ];
  },
  applicationDetails: async (t, tenantId, applicationNo, userType, args) => {
    const filter = { applicationNo, ...args };
    const response = await ASSETSearch.application(tenantId, filter);
    
    

    return {
      tenantId: response.tenantId,
      applicationDetails: ASSETSearch.RegistrationDetails({ Assets: response, t }),
      applicationData: response,
      transformToAppDetailsForEmployee: ASSETSearch.RegistrationDetails,
      
    };
  },
};
