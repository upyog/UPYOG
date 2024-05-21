
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
          { title: "AST_SUB_CATEGORY", value: response?.assetSubCategory },
          { title: "AST_APPROVAL_DATE", value: response?.approvalDate},
          { title: "AST_APPLICATION_DATE", value: response?.applicationDate },
          
        ],
      },

      {
        title: "AST_ADDRESS_DETAILS",
        asSectionHeader: true,
        values: [
          { title: "AST_CITY", value: response?.addressDetails?.city },
          { title: "AST_PINCODE", value: response?.addressDetails?.pincode },
          { title: "AST_LOCALITY", value: response?.addressDetails?.locality?.name },
          { title: "AST_DOOR_NO", value: response?.addressDetails?.doorNo },
          { title: "AST_STREET", value: response?.addressDetails?.street },
          { title: "AST_BUILDING_NAME", value: response?.addressDetails?.buildingName },
          { title: "AST_ADDRESS_LINE_1", value: response?.addressDetails?.addressLine1 },
          { title: "AST_ADDRESS_LINE_2", value: response?.addressDetails?.addressLine2 },


        ],
      },

      
      {
        title: "AST_DETAILS",
        asSectionHeader: true,
        values: [
          ...(response?.assetParentCategory=== "Land"
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
                { title: "AST_FENCED", value: response?.additionalDetails?.isitfenced },
                { title: "AST_ANY_BUILTUP", value: response?.additionalDetails?.AnyBuiltup },
                { title: "AST_REVENUE_GENERATED", value: response?.additionalDetails?.Revenuegeneratedbyasset  + " Rupees"},
                { title: "AST_TOTAL_COST", value: response?.additionalDetails?.Totalcost  + " Rupees"},
                

              ]
            : []),
          ...(response?.assetParentCategory === "BUILDINGS"
            ? [
                // { title: "AST_CURRENT_VALUE", value: response?.additionalDetails?.currentassetvalue },
                // { title: "AST_DEPRICIATION_RATE", value: response?.additionalDetails?.depriciationRate },
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
