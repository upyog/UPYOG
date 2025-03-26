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
    console.log("rreeehgvbde sfews",response);
    return response.cndApplicationDetail[0];
  },

  RegistrationDetails: ({ cndApplicationDetail: response, t }) => {
    // function to filter out the fields which have values
    const filterEmptyValues = (values) => values.filter(item => item.value);
    

    return [
      {
        title: "COMMON_CND_DETAILS",
        asSectionHeader: true,
        values: filterEmptyValues([
          { title: "CND_APPLICATION_NUMBER", value: response?.applicationNumber },
          { title: "CND_REQUEST_TYPE", value: response?.applicationType },
          { title: "CND_PROPERTY_USAGE", value: response?.propertyType },
          { title: "CND_TYPE_CONSTRUCTION", value: response?.typeOfConstruction },
          { title: "CND_WASTE_QUANTITY", value: response?.totalWasteQuantity },
          { title: "CND_SCHEDULE_PICKUP", value: response?.requestedPickupDate },
          { title: "CND_TIME_CONSTRUCTION", value: response?.constructionFromDate + " to " + response?.constructionToDate},
        ]),
      },
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
