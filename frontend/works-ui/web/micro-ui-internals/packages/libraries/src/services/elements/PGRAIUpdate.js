/**
 * Developed for the Update Call for PGR AI Application, 
 * we passed all these complaintDetails, action, employeeData, comments, uploadedDocument, tenantId as a arguements
 * and receiving here as a parameter from ComplaintDetail Componet using Digit.PGRAIUpdate.assign
 */

export const PGRAIUpdate = {
    assign: async (complaintDetails, action, employeeData, comments, uploadedDocument, tenantId) => {
      complaintDetails.workflow.action = action;
      complaintDetails.workflow.assignes = employeeData ? [employeeData.uuid] : null;
      complaintDetails.workflow.comments = comments;
      uploadedDocument
        ? (complaintDetails.workflow.verificationDocuments = [
              {
                documentType: "PHOTO",
                fileStoreId: uploadedDocument,
                documentUid: "",
                additionalDetails: {},
              },
            ])
        : null;
  
      if (!uploadedDocument) complaintDetails.workflow.verificationDocuments = [];
      
      //TODO: get tenant id
      const response = await Digit.PGRAIService.update(complaintDetails, tenantId);
      return response;
    },
  };
  