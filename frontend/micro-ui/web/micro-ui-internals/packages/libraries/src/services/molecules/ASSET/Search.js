
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
          { title: "AST_FINANCIAL_YEAR", value: response?.financialYear },
          { title: "AST_SOURCE_FINANCE", value: response?.sourceOfFinance },
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
                { title: "AST_TYPE", value: response?.additionalDetails?.landType },
                { title: "AST_LAND_AREA", value: response?.additionalDetails?.area },
                { title: "AST_AWARD_NUMBER", value: response?.additionalDetails?.awardNumber },
                { title: "AST_BOOK_VALUE", value: response?.additionalDetails?.bookValue  + " Rupees" },
                { title: "AST_CURRENT_VALUE", value: response?.additionalDetails?.currentAssetValue   + " Rupees" },
                { title: "AST_COLLECT_ORDER_NUM", value: response?.additionalDetails?.collectorOrderNumber },
                { title: "AST_DEPRICIATION_RATE", value: response?.additionalDetails?.depreciationRate },
                { title: "AST_COST_AFTER_DEPRICIAT", value: response?.additionalDetails?.costAfterDepreciation  + " Rupees"},
                { title: "AST_COUNCIL_RES_NUM", value: response?.additionalDetails?.councilResolutionNumber },
                { title: "AST_GOVT_ORDER_NUM", value: response?.additionalDetails?.governmentOrderNumber },
                { title: "AST_FROM_DEED_TAKEN", value: response?.additionalDetails?.fromWhomDeedTaken },
                { title: "AST_HOW_ASSET_USE", value: response?.additionalDetails?.howAssetBeingUsed },
                { title: "AST_FENCED", value: response?.additionalDetails?.isitFenced?.code },
                { title: "AST_ANY_BUILTUP", value: response?.additionalDetails?.anyBuiltup?.code },
                { title: "AST_REVENUE_GENERATED", value: response?.additionalDetails?.revenueGeneratedByAsset  + " Rupees"},
                { title: "AST_TOTAL_COST", value: response?.additionalDetails?.totalCost  + " Rupees"},
                

              ]
            : []),
          ...(response?.assetParentCategory === "BUILDING"
            ? [
              { title: "AST_TYPE", value: response?.additionalDetails?.assetParentCategory },
              { title: "AST_PLINTH_AREA", value: response?.additionalDetails?.plinthArea},
              { title: "AST_PLOT_AREA", value: response?.additionalDetails?.plotArea },
              { title: "AST_FLOORS_NO", value: response?.additionalDetails?.floorNo },
              { title: "AST_AREA_FLOOR", value: response?.additionalDetails?.floorArea },
              { title: "AST_DIMENSIONS", value: response?.additionalDetails?.dimensions },
              { title: "AST_TOTAL_COST", value: response?.additionalDetails?.totalCost + " Rupees"},
              { title: "AST_REVENUE_GENERATED", value: response?.additionalDetails?.revenueGeneratedByAsset + " Rupees"},
              { title: "AST_DEPRICIATION_RATE", value: response?.additionalDetails?.depreciationRate },
              { title: "AST_COST_AFTER_DEPRICIAT", value: response?.additionalDetails?.costAfterDepreciation + " Rupees"},
              { title: "AST_CURRENT_VALUE", value: response?.additionalDetails?.currentAssetValue + " Rupees"},
              { title: "AST_BUILDING_NO", value: response?.additionalDetails?.buildingSno},
              { title: "AST_BOOK_VALUE", value: response?.additionalDetails?.bookValue + " Rupees"},
              ]
            : []),
          
          ...(response?.assetParentCategory === "SERVICE"
          ? [
            { title: "AST_TYPE", value: response?.additionalDetails?.assetAssetSubCategory },
            { title: "AST_ROAD_TYPE", value: response?.additionalDetails?.roadType?.code },
            { title: "AST_ROAD_WIDTH", value: response?.additionalDetails?.RoadWidth },
            { title: "AST_SURFACE_TYPE", value: response?.additionalDetails?.surfaceType?.code},
            { title: "AST_LAST_MAINTAINENCE", value: response?.additionalDetails?.lastMaintainence },
            { title: "AST_NEXT_MAINTAINENCE", value: response?.additionalDetails?.nextMaintainence },
            { title: "AST_PROTECTION_LENGTH", value: response?.additionalDetails?.protectionLength },
            { title: "AST_PEDASTRIAN_CROSSING_NUMBER", value: response?.additionalDetails?.numOfPedastrianCross },
            { title: "AST_METROSTATION_NUMBER", value: response?.additionalDetails?.numOfMetroStation },
            { title: "AST_FOOTPATH_NUMBER", value: response?.additionalDetails?.numOfFootpath },
            { title: "AST_BUSSTOP_NUMBER", value: response?.additionalDetails?.numOfBusStop },
            { title: "AST_CYCLETRACK_LENGTH", value: response?.additionalDetails?.lengthOfCycletrack },
            { title: "AST_DRAINAGE_CHANNEL_LENGTH", value: response?.additionalDetails?.drainageLength },
            { title: "AST_TOTAL_COST", value: response?.additionalDetails?.totalCost + " Rupees"},
            { title: "AST_DEPRICIATION_RATE", value: response?.additionalDetails?.depreciationRate },
            { title: "AST_COST_AFTER_DEPRICIAT", value: response?.additionalDetails?.costAfterDepreciation + " Rupees"},
            { title: "AST_BOOK_VALUE", value: response?.additionalDetails?.bookValue + " Rupees"},
            ]
          :[]),

          ...(response?.additionalDetails?.assetAssetSubCategory === "Vehicles"
          ? [
            { title: "AST_TYPE", value: response?.additionalDetails?.assetAssetSubCategory },
            { title: "AST_ACQUIRED_SOURCE", value: response?.additionalDetails?.acquiredFrom },
            { title: "AST_ACQUISTION_DATE", value: response?.additionalDetails?.acquisitionDate },
            { title: "AST_CHASIS_NUMBER", value: response?.additionalDetails?.chasisNumber },
            { title: "AST_ENGINE_NUMBER", value: response?.additionalDetails?.engineNumber },
            { title: "AST_REGISTRATION_NUMBER ", value: response?.additionalDetails?.registrationNumber },
            { title: "AST_PARKING_LOCATION", value: response?.additionalDetails?.parkingLocation },
            { title: "AST_IMPROVEMENT_COST", value: response?.additionalDetails?.improvementCost + " Rupees"},
            { title: "AST_TOTAL_COST", value: response?.additionalDetails?.totalCost + " Rupees"},
            { title: "AST_CURRENT_VALUE", value: response?.additionalDetails?.currentAssetValue + " Rupees"},
            { title: "AST_BOOK_VALUE", value: response?.additionalDetails?.bookValue + " Rupees"},
          ]
          :[]),

          ...(response?.assetParentCategory === "IT" ?
            [
            { title: "AST_BRAND", value: response?.additionalDetails?.brand },
            { title: "AST_INVOICE_DATE", value: response?.additionalDetails?.invoiceDate },
            { title: "AST_ASSET_AGE", value: response?.additionalDetails?.assetAge },
            { title: "AST_ASSIGNED_USER", value: response?.additionalDetails?.assignedUser },
            { title: "AST_CURRENT_LOCATION", value: response?.additionalDetails?.currentLocation },
            { title: "AST_MANUFACTURER ", value: response?.additionalDetails?.manufacturer },
            { title: "AST_PURCHASE_COST", value: response?.additionalDetails?.purchaseCost + " Rupees"},
            { title: "AST_PURCHASE_DATE", value: response?.additionalDetails?.purchaseDate},
            { title: "AST_PURCHASE_ORDER", value: response?.additionalDetails?.purchaseOrderNumber},
            { title: "AST_WARRANTY", value: response?.additionalDetails?.warranty},

            ]
            : []),



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
