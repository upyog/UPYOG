import { CHBServices } from "../../elements/CHB";

export const CHBSearch = {
  
  all: async (tenantId, filters = {}) => {
    
    const response = await CHBServices.search({ tenantId, filters });
    
    return response;
  },

  
  application: async (tenantId, filters = {}) => {
    const response = await CHBServices.search({ tenantId, filters });
    return response.hallsBookingApplication[0];
  },
  RegistrationDetails: ({ hallsBookingApplication: response, t }) => {
    return [

      {
        title: "CHB_APPLICANT_DETAILS_HEADER",
        asSectionHeader: true,
        values: [
          { title: "CHB_APPLICATION_NUMBER", value: response?.applicationNumber },
          { title: "CHB_APPLICANT_NAME", value: response?.applicantName },
          { title: "CHB_APPLICANT_MOBILE_NO", value: response?.mobileNumber },
          { title: "CHB_APPLICANT_EMAIL_ID", value: response?.emailId },
        ],
      },

      {
        title: "CHB_SLOT_DETAILS_HEADER",
        asSectionHeader: true,
        values: [
          { title: "SELECT_SLOT", value: response?.slots?.selectslot },
          { title: "RESIDENT_TYPE", value: response?.slots?.residentType },
          { title: "SPECIAL_CATEGORY", value: response?.slots?.specialCategory },
          { title: "PURPOSE", value: response?.slots?.purpose },

        ],
      },

      {
        title: "CHB_BANK_DETAILS_HEADER",
        asSectionHeader: true,
        values: [
          { title: "ACCOUNT_NUMBER", value: response?.bankdetails?.accountNumber },
          { title: "CONFIRM_ACCOUNT_NUMBER", value: response?.bankdetails?.confirmAccountNumbers },
          { title: "IFSC_CODE",value: response?.bankdetails?.ifscCode },
          { title: "BANK_NAME",value: response?.bankdetails?.bankName},
          { title: "BANK_BRANCH_NAME",value: response?.bankdetails?.bankBranchName},
          { title: "ACCOUNT_HOLDER_NAME",value: response?.bankdetails?.accountHolderName},
          
  
        ],
      },

      {
        title: "CHB_DOCUMENT_DETAILS",
        additionalDetails: {
          
          documents: [
            {
             
              values: response?.documents
                ?.map((document) => {

                  return {
                    title: `CHB_${document?.documentType.replace(".", "_")}`,
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
    const response = await CHBSearch.application(tenantId, filter);

    return {
      tenantId: response.tenantId,
      applicationDetails: CHBSearch.RegistrationDetails({ hallsBookingApplication: response, t }),
      applicationData: response,
      transformToAppDetailsForEmployee: CHBSearch.RegistrationDetails,
      
    };
  },
};
