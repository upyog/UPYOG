/**
 * @file FireNocSearch.js
 *
 * @description
 * Provides search and application detail utilities for the Fire NOC module.
 * It fetches Fire NOC application data, transforms the response into a
 * structured format for the Application Details screen, and dynamically
 * maps building UOM details and supporting documents for display.
 *
 * @features
 * - Searches Fire NOC applications using tenant and application filters.
 * - Retrieves Fire NOC application details.
 * - Formats owner and building information for the details screen.
 * - Dynamically maps building UOM codes and values.
 * - Processes uploaded application documents.
 *
 * @dependencies
 * - NOCService: Used to communicate with Fire NOC backend APIs.
 */

import { NOCService } from "../../elements/NOC";

export const FireNocSearch = {
  application: async (tenantId, filters = {}) => {
    const response = await NOCService.search( tenantId, filters );
    return response?.FireNOCs[0];
  },

  RegistrationDetails: ({ FireNocDetails: response, t }) => {

    const buildingUoms = response?.fireNOCDetails?.buildings?.[0]?.uoms?.map((uom) => ({
      title: uom.code,
      value: uom.value,
    })) || [];
    return [
      {
      title: "FN_OWNER_DETAILS",
        asSectionHeader: true,
        values: [
          { title: "FN_APPLICATION_NUMBER_LABEL", value: response?.fireNOCDetails?.applicationNumber},
          { title: "FN_APPLICANT_NAME", value: response?.fireNOCDetails?.applicantDetails?.owners?.[0]?.name },
          { title: "FN_MOBILE_NUMBER", value: response?.fireNOCDetails?.applicantDetails?.owners?.[0]?.mobileNumber },
          { title: "FN_OWNERSHIPTYPE", value: response?.fireNOCDetails?.applicantDetails?.ownerShipType },
          { title: "FN_APPLICANT_EMAILID", value: response?.fireNOCDetails?.applicantDetails?.owners?.[0]?.emailId },
        ],
      },
      {
      title: "FN_BUILDING_DETAILS",
        asSectionHeader: true,
        values: [
          { title: "FN_BUILDING_USAGE_TYPE", value: response?.fireNOCDetails?.buildings?.[0]?.usageType },
          { title: "FN_NOC_TYPE", value: response?.fireNOCDetails?.fireNOCType },
          { title: "FN_BUILDING_NAME", value: response?.fireNOCDetails?.buildings?.[0]?.name },
          { title: "FN_NO_BUILDINGS", value: response?.fireNOCDetails?.noOfBuildings },
          ...buildingUoms,
        ],
      },
      {
        title: "FN_DOCUMENT_DETAILS",
        additionalDetails: {
          
          documents: [
            {
              values: response?.fireNOCDetails?.applicantDetails?.additionalDetail?.documents
                ?.map((document) => {
                  return {
                    title: `${document?.documentType.replace(".", "_")}`,
                    documentType: document?.documentType,
                    fileStoreId: document?.fileStoreId
                  };
                }),
            },
          ],
        },
      },
    ];
  },
  applicationDetails: async (t, tenantId, applicationNumber, args) => {
    const filter = { applicationNumber, ...args };
    const response = await FireNocSearch.application(tenantId, filter);

    return {
      tenantId: response.tenantId,
      applicationDetails: FireNocSearch.RegistrationDetails({ FireNocDetails: response, t }),
      applicationData: response
      
    };
  },
};
