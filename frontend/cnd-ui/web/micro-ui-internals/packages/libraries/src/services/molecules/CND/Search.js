import { CNDService } from "../../elements/CND";

/**
 * This CNDSearch component returns its input data after structuring it to be rendered as application's Data
 * It takes input of the application's data
 * Returns the data to the applicationDetails component
 */

export const CNDSearch = {
  
  all: async (tenantId, filters = {}) => {
    const response = await CNDService.search({ tenantId, filters });
    return response;
  },

  application: async (tenantId, filters = {}) => {
    const response = await CNDService.search({ tenantId, filters });
    return response.cndApplicationDetail[0];
  },

  RegistrationDetails: ({ cndApplicationDetail: response, t }) => {
    // function to filter out the fields which have values
    const filterEmptyValues = (values) => values.filter(item => item.value);

    const slotlistRows = response?.wasteTypeDetails.map((items,index)=>(
      [
        index+1,
        t(items?.wasteType),
        items?.quantity,
        items?.metrics ? items?.metrics :"-",
      ]
    )) || [];


    return [
      {
        title: "COMMON_CND_DETAILS",
        asSectionHeader: true,
        values: filterEmptyValues([
          { title: "CND_APPLICATION_NUMBER", value: response?.applicationNumber },
          { title: "CND_REQUEST_TYPE", value: t(response?.applicationType) },
          { title: "CND_PROPERTY_USAGE", value: t(response?.propertyType) },
          { title: "CND_TYPE_CONSTRUCTION", value: t(response?.typeOfConstruction) },
          { title: "CND_WASTE_QUANTITY", value: response?.totalWasteQuantity + " Tons"},
          { title: "CND_SCHEDULE_PICKUP", value: response?.requestedPickupDate },
          ...(response?.applicationStatus==="COMPLETED" 
            ? [{title: "CND_EMP_SCHEDULE_PICKUP", value: response.pickupDate, isBold:true}]
            : []
          ),
          { title: "CND_TIME_CONSTRUCTION", value: response?.constructionFromDate + " to " + response?.constructionToDate},
        ]),
      },
      // Conditionally include AST_ALLOCATION_DETAILS
      ...(response?.applicationStatus==="COMPLETED"
        ? [
          {
            title: "CND_FACILITY_DETAILS",
            asSectionHeader: true,
            values: [
              { title: "CND_DISPOSE_DATE", value: response?.facilityCenterDetail?.disposalDate?.split(" ")[0]},
              { title: "CND_DISPOSE_TYPE", value: response?.facilityCenterDetail?.disposalType },
              { title: "CND_DUMPING_STATION", value: response?.facilityCenterDetail?.dumpingStationName},
              { title: "CND_DISPOSAL_SITE_NAME", value: response?.facilityCenterDetail?.nameOfDisposalSite},
              { title: "CND_GROSS_WEIGHT", value: response?.facilityCenterDetail?.grossWeight + " Ton"},
              { title: "CND_NET_WEIGHT", value: response?.facilityCenterDetail?.netWeight + " Ton"},
            ],
          }
        ]
        : []),
      {
        title: "COMMON_PERSONAL_DETAILS",
        asSectionHeader: true,
        values: filterEmptyValues([
          { title: "COMMON_APPLICANT_NAME", value: response?.applicantDetail?.nameOfApplicant },
          { title: "COMMON_MOBILE_NUMBER", value: response?.applicantDetail?.mobileNumber },
          { title: "COMMON_EMAIL_ID", value: response?.applicantDetail?.emailId },
        ]),
      },
      {
        title: "CND_WASTE_PICKUP_ADDRESS",
        asSectionHeader: true,
        values: filterEmptyValues([
          { title: "HOUSE_NO", value: response?.addressDetail?.houseNumber },
          { title: "ADDRESS_LINE1", value: response?.addressDetail?.addressLine1 },
          { title: "ADDRESS_LINE2", value: response?.addressDetail?.addressLine2 },
          { title: "LANDMARK", value: response?.addressDetail?.landmark },
          { title: "CITY", value: response?.addressDetail?.city },
          { title: "LOCALITY", value: response?.addressDetail?.locality },
          { title: "PINCODE", value: response?.addressDetail?.pinCode },
        ]),
      },
      {
        title: "CND_WASTE_DETAILS",
        asSectionHeader: true,
        isTable: true,
        headers: ["CND_S_NO", "CND_WASTE_TYPE", "CND_QUANTITY", "CND_METRICS"],
        tableRows: slotlistRows,
      },
      ...(response?.documentDetails && response?.documentDetails.length > 0 
        ? [
            {
              title: "CND_DOC_DETAILS",
              additionalDetails: {
                documents: [
                  {
                    values: response?.documentDetails
                      ?.map((document) => {
                        return {
                          title: `${t(document?.documentType?.toUpperCase())}`,
                          documentType: document?.documentType,
                          documentUid: document?.documentDetailId,
                          fileStoreId: document?.fileStoreId,
                          status: document.status,
                        };
                      }),
                  },
                ],
              },
            }
          ] 
        : [])
    ];
  },

  applicationDetails: async (t, tenantId, applicationNumber,isUserDetailRequired, userType, args) => {
    const filter = { applicationNumber, ...args,isUserDetailRequired };
    const response = await CNDSearch.application(tenantId, filter);

    return {
      tenantId: response.tenantId,
      applicationDetails: CNDSearch.RegistrationDetails({ cndApplicationDetail: response, t }),
      applicationData: response,
      transformToAppDetailsForEmployee: CNDSearch.RegistrationDetails,
    };
  },
};
