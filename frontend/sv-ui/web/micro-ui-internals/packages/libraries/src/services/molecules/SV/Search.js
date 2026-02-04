import { SVService } from "../../elements/SV";

/**
 * This SVSearch component returns its input data after structuring it to be rendered as application's Data
 * It takes input of the application's data
 * Returns the data to the applicationDetails component
 */

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
    // function to filter out the fields which have values
    const filterEmptyValues = (values) => values.filter(item => item.value);
    let gender, dgender;
    if(response?.vendorDetail[0]?.gender){
       gender = response?.vendorDetail[0]?.gender == "M" ? "Male" : "Female";
    }
    if(response?.vendorDetail[0]?.dependentGender){
      dgender = response?.vendorDetail[0]?.dependentGender == "M" ? "Male" : "Female";
    }

    // function to get gender string from gender code
    const getGender = (genderCode) => {
      switch (genderCode) {
        case 'M':
          return 'Male';
        case 'F':
          return 'Female';
        default:
          return '';
      }
    };

    // Extracting details based on relationship type because vendorDetail is an array of objects
    const vendorPersonalDetails = response?.vendorDetail?.find((item)=>item.relationshipType==="VENDOR") || {};
    const vendorSpouseDetails = response?.vendorDetail?.find((item)=>item.relationshipType==="SPOUSE") || {};
    const vendorDependentDetails = response?.vendorDetail?.filter((item) => item.relationshipType === "DEPENDENT") || [];

    return [

      {
        title: "SV_VENDOR_PERSONAL_DETAILS",
        asSectionHeader: true,
        values: filterEmptyValues([
          { title: "SV_APPLICATION_NUMBER", value: response?.applicationNo },
          ...(response?.applicationStatus==="REGISTRATIONCOMPLETED" ?
          [{ title: "SV_VALIDITY_DATE", value: response?.validityDate, isBold:true }]
          :[]
        ),
          { title: "SV_VENDOR_NAME", value: vendorPersonalDetails?.name},
          { title: "SV_FATHER_NAME", value: vendorPersonalDetails?.fatherName },
          { title: "SV_REGISTERED_MOB_NUMBER", value: vendorPersonalDetails?.mobileNo},
          { title: "SV_EMAIL", value: vendorPersonalDetails?.emailId },
          { title: "SV_DATE_OF_BIRTH", value: vendorPersonalDetails?.dob },
          { title: "SV_GENDER", value: getGender(vendorPersonalDetails?.gender) },
          { title: "SV_SPOUSE_NAME", value: vendorSpouseDetails?.name },
          { title: "SV_SPOUSE_DATE_OF_BIRTH", value: vendorSpouseDetails?.dob },
          ...vendorDependentDetails.flatMap((dep, index) => ([
          {
            title: `SV_DEPENDENT_${index + 1}_NAME`,
            value: dep?.name,
          },
          {
            title: `SV_DEPENDENT_${index + 1}_DATE_OF_BIRTH`,
            value: dep?.dob,
          },
          {
            title: `SV_DEPENDENT_${index + 1}_GENDER`,
            value: getGender(dep?.gender),
          }
        ])),
          { title: "SV_TRADE_NUMBER", value: response?.vendorDetail[0]?.tradeNumber },
        ]),
      },

      {
        title: "SV_VENDOR_BUSINESS_DETAILS",
        asSectionHeader: true,
        values: filterEmptyValues([
          { title: "SV_VENDING_TYPE", value: response?.vendingActivity },
          { title: "SV_VENDING_LOCALITY", value: response?.localityValue },
          { title: "SV_VENDING_ZONES", value: response?.vendingZoneValue },
          { title: "SV_AREA_REQUIRED", value: response?.vendingArea },
          { title: "SV_LOCAL_AUTHORITY_NAME", value: response?.localAuthorityName },
          { title: "SV_VENDING_LISCENCE", value: response?.vendingLicenseCertificateId },
        ]),
      },

      {
        title: "SV_BANK_DETAILS",
        asSectionHeader: true,
        values: filterEmptyValues([
          { title: "SV_ACCOUNT_NUMBER", value: response?.bankDetail?.accountNumber },
          { title: "SV_IFSC_CODE", value: response?.bankDetail?.ifscCode },
          { title: "SV_BANK_NAME",value: response?.bankDetail?.bankName },
          { title: "SV_BANK_BRANCH_NAME",value: response?.bankDetail?.bankBranchName},
          { title: "SV_ACCOUNT_HOLDER_NAME",value: response?.bankDetail?.accountHolderName},
  
        ]),
      },

      {
        title: "SV_ADDRESS_DETAILS",
        asSectionHeader: true,
        values: filterEmptyValues([
          { title: "SV_ADDRESS_LINE1", value: response?.addressDetails[0]?.addressLine1 },
          { title: "SV_ADDRESS_LINE2", value: response?.addressDetails[0]?.addressLine2 },
          { title: "SV_CITY",value: response?.addressDetails[0]?.city },
          { title: "SV_LOCALITY",value: response?.addressDetails[0]?.locality},
          { title: "SV_ADDRESS_PINCODE",value: response?.addressDetails[0]?.pincode},
          { title: "SV_LANDMARK",value: response?.addressDetails[0]?.landmark},  
        ]),
      },
      {
        title: "SV_DOCUMENT_DETAILS_LABEL",
        additionalDetails: {
          documents: [
          {
            values: response?.documentDetails?.filter((document) => document?.documentType !== "NONE") // Add this filter
              ?.map((document) => {
                return {
                  title: `${document?.documentType.replace(".", "_")}`,
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

  applicationDetails: async (t, tenantId, applicationNumber,isDraftApplication, userType, args) => {
    const filter = { applicationNumber, ...args,isDraftApplication };
    const response = await SVSearch.application(tenantId, filter);

    return {
      tenantId: response.tenantId,
      applicationDetails: SVSearch.RegistrationDetails({ SVDetail: response, t }),
      applicationData: response,
      transformToAppDetailsForEmployee: SVSearch.RegistrationDetails,
    };
  },
};
