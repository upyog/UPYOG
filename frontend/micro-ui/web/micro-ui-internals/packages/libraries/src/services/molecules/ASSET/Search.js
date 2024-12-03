
import { ASSETService } from "../../elements/ASSET";

const getData = (res, combinedData) => {

  let formJson = combinedData
    .filter((category) => {
      const isMatch = category.assetParentCategory === res?.assetParentCategory || category.assetParentCategory === "COMMON";
      return isMatch;
    })
    .map((category) => category.fields) // Extract the fields array
    .flat() // Flatten the fields array
    .filter((field) => field.active === true);


  let rows = []
  formJson.map((row, index) => (
    // rows.push({ title: row.code, value: res?.additionalDetails[row.name]) })
    rows.push({ title: row.code, value: extractValue(res?.additionalDetails[row.name]) })
  ))

  return rows
}
const extractValue = (key) => {

  if (typeof key === 'object') {
    return key['value']
  }
  return key
}


export const ASSETSearch = {
  all: async (tenantId, filters = {}) => {
    const response = await ASSETService.search({ tenantId, filters });
    return response;
  },

  application: async (tenantId, filters = {}) => {
    const response = await ASSETService.search({ tenantId, filters });
    return response.Assets[0];
  },
  RegistrationDetails: ({ Assets: response, combinedData, t }) => {

    const formatDate = (epochTime) => {
      if (!epochTime) return '';
      const date = new Date(epochTime);
      return date.toLocaleDateString('en-GB', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
      }).replace(/\//g, '/');
    };
    return [

      {
        title: "AST_COMMON_DETAILS",
        asSectionHeader: true,
        values: [
          { title: "AST_FINANCIAL_YEAR", value: response?.financialYear },
          { title: "AST_SOURCE_FINANCE", value: response?.sourceOfFinance },
          { title: "AST_APPLICATION_NUMBER", value: response?.applicationNo },
          { title: "AST_BOOK_REF_SERIAL_NUM", value: response?.assetBookRefNo },
          { title: "AST_CATEGORY", value: response?.assetClassification },
          { title: "AST_PARENT_CATEGORY", value: response?.assetParentCategory },
          { title: "AST_SUB_CATEGORY", value: response?.assetCategory },
          { title: "AST_NAME", value: response?.assetName },
          { title: "AST_DEPARTMENT", value: 'COMMON_MASTERS_DEPARTMENT_'+response?.department },
          { title: "AST_USAGE", value: response?.assetCurrentUsage },

        ],
      },
      {
        title: "AST_ADDRESS_DETAILS",
        asSectionHeader: true,
        values: [
          { title: "MYCITY_CODE_LABEL", value: response?.addressDetails?.city },
        ],
      },
      {
        title: "AST_DETAILS",
        asSectionHeader: true,
        values: getData(response, combinedData)
        
      },

      // Conditionally include AST_ALLOCATION_DETAILS
      ...(response?.assetAssignment?.isAssigned
        ? [
          {
            title: "AST_ALLOCATION_DETAILS",
            asSectionHeader: true,
            values: [
              // { title: "AST_EMP_CODE", value: response?.assetAssignment?.employeeCode },
              { title: "AST_ASSIGNED_USER", value: response?.assetAssignment?.assignedUserName },
              { title: "AST_DEPARTMENT", value: response?.assetAssignment?.department },
              { title: "AST_ASSIGNED_DATE", value: formatDate(response?.assetAssignment?.assignedDate) },
              // { title: "AST_DESIGNATION", value: response?.assetAssignment?.designation },
            ],
          }
        ]
        : []),

      {
        title: "AST_DOCUMENT_DETAILS",
        additionalDetails: {

          documents: [
            {

              values: response?.documents
                ?.map((document) => {
                  console.log("documnet", document);

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
  applicationDetails: async (t, tenantId, applicationNo, userType, combinedData, args) => {
    const filter = { applicationNo, ...args };
    const response = await ASSETSearch.application(tenantId, filter);

    console.log("::", combinedData);

    return {
      tenantId: response.tenantId,
      applicationDetails: ASSETSearch.RegistrationDetails({ Assets: response, combinedData, t }),
      applicationData: response,
      transformToAppDetailsForEmployee: ASSETSearch.RegistrationDetails,

    };
  },
};
