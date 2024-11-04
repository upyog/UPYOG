/**
 * This SVSearch component returns its input data after structuring it to be rendered as application's Data
 * It takes input of the application's data
 * Returns the data to the applicationDetails component
 */

import { SVService } from "../../elements/SV";

export const SVSearch = {
  
  all: async (tenantId, filters = {}) => {
    const response = await SVService.search({ tenantId, filters });
    return response;
  },

  application: async (tenantId, filters = {}) => {
    const response = await SVService.search({ tenantId, filters });
    return response.SVDetail[0];
  },

  RegistrationDetails: ({ SVDetail: response, t }) => {
    console.log("data ins search of svdetails: ", response)
    return [

      {
        title: "SV_VENDOR_PERSONAL_DETAILS",
        asSectionHeader: true,
        values: [
          { title: "SV_APPLICATION_NUMBER", value: response?.applicationNo },
          { title: "SV_APPLICANT_NAME", value: response?.vendorDetail[0]?.name},
          { title: "SV_FATHER/HUSBAND_NAME", value: response?.vendorDetail[0]?.fatherName },
          { title: "SV_APPLICANT_MOBILE_NO", value: response?.vendorDetail[0]?.mobileNo},
          { title: "SV_APPLICANT_EMAILID", value: response?.vendorDetail[0]?.emailId },
          { title: "SV_DATE_OF_BIRTH", value: response?.vendorDetail[0]?.dob },
          { title: "SV_GENDER", value: response?.vendorDetail[0]?.gender },
          { title: "SV_SPOUSE_NAME", value: response?.vendorDetail[0]?.emailId },
          { title: "SV_SPOUSE_DATE_OF_BIRTH", value: response?.vendorDetail[0]?.emailId },
          { title: "SV_DEPENDENT_NAME", value: response?.vendorDetail[0]?.emailId },
          { title: "SV_DEPENDENT_DATE_OF_BIRTH", value: response?.vendorDetail[0]?.emailId },
          { title: "SV_DEPENDENT_GENDER", value: response?.vendorDetail[0]?.emailId },
          { title: "SV_TRADE_NUMBER", value: response?.vendorDetail[0]?.emailId },
        ],
      },

      {
        title: "SV_VENDOR_BUSINESS_DETAILS",
        asSectionHeader: true,
        values: [
          { title: "SV_VENDING_TYPE", value: response?.vendingZone },
          { title: "SV_VENDING_ZONES", value: response?.vendingZone },
          { title: "SV_AREA_REQUIRED", value: response?.vendingArea },
          { title: "SV_LOCAL_AUTHORITY_NAME", value: response?.localAuthorityName },
          { title: "SV_VENDING_LISCENCE", value: response?.vendingLicenseCertificateId },
        ],
      },

      {
        title: "SV_BANK_DETAILS",
        asSectionHeader: true,
        values: [
          { title: "SV_ACCOUNT_NUMBER", value: response?.bankDetail?.accountNumber },
          { title: "SV_IFSC_CODE", value: response?.bankDetail?.ifscCode },
          { title: "SV_BANK_NAME",value: response?.bankDetail?.bankName },
          { title: "SV_BANK_BRANCH_NAME",value: response?.bankDetail?.bankBranchName},
          { title: "SV_ACCOUNT_HOLDER_NAME",value: response?.bankDetail?.accountHolderName},
  
        ],
      },

      {
        title: "SV_ADDRESS_DETAILS",
        asSectionHeader: true,
        values: [
          { title: "SV_ADDRESS_LINE1", value: response?.addressDetails[0]?.addressLine1 },
          { title: "SV_ADDRESS_LINE2", value: response?.addressDetails[0]?.addressLine1 },
          { title: "SV_CITY",value: response?.addressDetails[0]?.city },
          { title: "SV_LOCALITY",value: response?.addressDetails[0]?.locality},
          { title: "SV_ADDRESS_PINCODE",value: response?.addressDetails[0]?.pincode},
          { title: "SV_LANDMARK",value: response?.addressDetails[0]?.landmark},  
        ],
      },

      {
        title: "SV_DOCUMENT_DETAILS",
        additionalDetails: {
          
          documents: [
            {
             
              values: response?.documentDetails
                ?.map((document) => {

                  return {
                    title: `SV_${document?.documentType.replace(".", "_")}`,
                    documentType: document?.documentType,
                    documentUid: document?.documentUid,
                    fileStoreId: document?.filestoreId,
                    status: document.status,
                  };
                }),
            },
          ],
        },
      },
    ];
  },

  applicationDetails: async (t, tenantId, applicationNumber, userType, args) => {
    const filter = { applicationNumber, ...args };
    const response = await SVSearch.application(tenantId, filter);

    return {
      tenantId: response.tenantId,
      applicationDetails: SVSearch.RegistrationDetails({ SVDetail: response, t }),
      applicationData: response,
      transformToAppDetailsForEmployee: SVSearch.RegistrationDetails,
    };
  },
};
