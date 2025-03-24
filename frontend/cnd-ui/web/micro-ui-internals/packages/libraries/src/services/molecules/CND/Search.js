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
    console.log("responsnenneneee",response);
    // function to filter out the fields which have values
    const filterEmptyValues = (values) => values.filter(item => item.value);
    

    return [
      {
        title: "COMMON_PERSONAL_DETAILS",
        asSectionHeader: true,
        values: filterEmptyValues([
          { title: "CND_APPLICATION_NUMBER", value: response?.applicationNumber },
        ]),
      },

    ];
  },

  applicationDetails: async (t, tenantId, applicationNumber,isDraftApplication, userType, args) => {
    const filter = { applicationNumber, ...args,isDraftApplication };
    const response = await CNDSearch.application(tenantId, filter);

    return {
      tenantId: response.tenantId,
      applicationDetails: CNDSearch.RegistrationDetails({ cndApplicationDetail: response, t }),
      applicationData: response,
      transformToAppDetailsForEmployee: CNDSearch.RegistrationDetails,
    };
  },
};
