/**
*@author - Shivank shukla -NIUA
*this file is created to show the Application Detail page from both search application and inbox
*as this file is specially designed for Pet-Registration 
*The hook useApplicationdetailPTR uses the useQuery hook to call the PTRSearch.applicationDetails method, passing in the parameters.

*The data returned from that API call is processed in the select method to extract just the applicationDetails array from the response.
*/

import { PTRService } from "../../elements/PTR";
export const PTRSearch = {
  all: async (tenantId, filters = {}) => {
    const response = await PTRService.search({ tenantId, filters });
    return response;
  },
  application: async (tenantId, filters = {}) => {
    const response = await PTRService.search({ tenantId, filters });
    return response.PetRegistrationApplications[0];
  },
  RegistrationDetails: ({ PetRegistrationApplications: response, t,pet_color }) => {
    const filterEmptyValues = (values) => values.filter(item => item.value);
    const getDate = (epochdate) => {
      return epochdate
        ? new Date(epochdate * 1000).getDate() + 
          "/" + 
          (new Date(epochdate * 1000).getMonth() + 1) + 
          "/" + 
          new Date(epochdate * 1000).getFullYear().toString()
        : "NA";
    };
    const convertEpochToDate = (dateEpoch) => {
      // Returning null in else case because new Date(null) returns initial date from calender
      if (dateEpoch) {
        const dateFromApi = new Date(dateEpoch);
        let month = dateFromApi.getMonth() + 1;
        let day = dateFromApi.getDate();
        let year = dateFromApi.getFullYear();
        month = (month > 9 ? "" : "0") + month;
        day = (day > 9 ? "" : "0") + day;
        return `${year}-${month}-${day}`;
      } else {
        return null;
      }
    };
    return [

      {
        title: "PTR_APPLICANT_DETAILS_HEADER",
        asSectionHeader: true,
        values: [
          { title: "PTR_APPLICATION_NUMBER", value: response?.applicationNumber},
          { title: "PTR_VALIDITY_DATE", value:getDate(response?.validityDate)},
          ...(response?.petToken && response.petToken.length > 0 
            ? [{title: "PTR_TOKEN", value: response.petToken, isBold:true}]
            : []
          ),
          { title: "PTR_APPLICANT_NAME", value: response?.applicantName },
          { title: "PTR_FATHER/HUSBAND_NAME", value: response?.fatherName },
          { title: "PTR_APPLICANT_MOBILE_NO", value: response?.mobileNumber },
          { title: "PTR_APPLICANT_EMAILID", value: response?.emailId },
        ],
      },

      {
        title: "PTR_PET_DETAILS_HEADER",
        asSectionHeader: true,
        values: filterEmptyValues([
          { title: "PTR_PET_TOKEN", value: response?.petToken },
          { title: "PTR_PET_TYPE", value: response?.petDetails?.petType },
          { title: "PTR_BREED_TYPE", value: response?.petDetails?.breedType },
          { title: "PTR_PET_NAME", value: response?.petDetails?.petName },
          { title: "PTR_DOCTOR_NAME", value: response?.petDetails?.doctorName },
          { title: "PTR_CLINIC_NAME", value: response?.petDetails?.clinicName },
          { title: "PTR_VACCINATED_DATE", value: response?.petDetails?.lastVaccineDate },
          { title: "PTR_VACCINATION_NUMBER", value: response?.petDetails?.vaccinationNumber },
          { title: "PTR_PET_AGE", value: response?.petDetails?.petAge + " Months" },
          { title: "PTR_IDENTIFICATION_MARK", value: response?.petDetails?.identificationMark },
          { title: "PTR_PET_SEX", value: response?.petDetails?.petGender },
          { title: "PTR_PET_COLOR", value: pet_color?.filter((color) => color?.colourCode === response?.petDetails?.petColor)?.[0]?.i18nKey },
          { title: "PTR_BIRTH", value: convertEpochToDate(response?.petDetails?.birthDate) },
          { title: "PTR_ADOPTION", value: convertEpochToDate(response?.petDetails?.adoptionDate) },
        ]),
      },

      {
        title: "PTR_ADDRESS_HEADER",
        asSectionHeader: true,
        values: [
          { title: "PTR_ADDRESS_PINCODE", value: response?.address?.pincode },
          { title: "PTR_ADDRESS_CITY", value: response?.address?.city },
          { title: "PTR_STREET_NAME",value: response?.address?.street },
          { title: "PTR_HOUSE_NO",value: response?.address?.doorNo},
          { title: "PTR_ADDRESS_LINE1",value: response?.address?.addressLine1},
          { title: "PTR_ADDRESS_LINE2",value: response?.address?.addressLine2},
          { title: "PTR_HOUSE_NAME",value: response?.address?.buildingName},
          { title: "PTR_LOCALITY",value: response?.address?.locality},
          { title: "PTR_LANDMARK",value: response?.address?.landmark},
        ],
      },

      {
        title: "PTR_DOCUMENT_DETAILS",
        additionalDetails: {
          
          documents: [
            {
             
              values: response?.documents
                ?.map((document) => {

                  return {
                    title: `PTR_${document?.documentType.replace(".", "_")}`,
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
  applicationDetails: async (t, tenantId, applicationNumber, pet_color, args) => {
    const filter = { applicationNumber, ...args };
    const response = await PTRSearch.application(tenantId, filter);

    return {
      tenantId: response.tenantId,
      applicationDetails: PTRSearch.RegistrationDetails({ PetRegistrationApplications: response, t,pet_color }),
      applicationData: response,
      transformToAppDetailsForEmployee: PTRSearch.RegistrationDetails,
      
    };
  },
};
