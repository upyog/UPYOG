/**
 * Transforms timeline/workflow data into a format suitable for PDF generation.
 * Follows the same pattern as OBPS getAcknowledgementData.
 */

const getTimelineAcknowledgementData = (workflowDetails, tenantInfo, pdfFiles = {}, t) => {

  const timeline = workflowDetails?.data?.timeline || workflowDetails?.timeline || [];
  const processInstances = workflowDetails?.data?.processInstances || workflowDetails?.processInstances || [];
  
  const businessId = processInstances?.[0]?.businessId || "N/A";
  const businessService = processInstances?.[0]?.businessService || "N/A";
  const moduleName = processInstances?.[0]?.moduleName || "N/A";

  const pdfDownloadLink = (documents, fileStoreId) => {
    const downloadLink = documents?.[fileStoreId] || "";
    const formats = downloadLink?.split(",")?.filter(Boolean) || [];
    return formats?.find((link) => !link?.includes("large") && !link?.includes("medium") && !link?.includes("small")) || formats?.[0] || "";
  };

  // regex pattern for trimming
  const pattern = /\[#\?.*?\*\*\]/;

  const timelineRows = timeline.map((item, index) => {
    const createdDate = item?.auditDetails?.created || "N/A";
    const timing = item?.auditDetails?.timing || "N/A";
    const assignerName = item?.assigner?.name || "N/A";
    const assignerType = item?.assigner?.type || "N/A";
    const mobileNumber = item?.assigner?.mobileNumber || "N/A";
    const action = item?.performedAction || "N/A";
    const status = item?.status || item?.state || "N/A";

    // sanitize wfComment before using
    const rawComment = item?.wfComment?.[0] || "-";
    const comment = typeof rawComment === "string" ? rawComment.split(pattern)[0] : rawComment;

    const documents = item?.wfDocuments || [];
    const sla = item?.sla || "N/A";
    const assignedTo = Array.isArray(item?.assignes) ? item.assignes.map(a => a?.name).filter(Boolean).join(", ") : "";

    return {
      sNo: index + 1,
      action: t ? t(action) : action,
      status: t ? t(status) : status,
      assignerName,
      assignerType,
      mobileNumber,
      designation: assignerType,
      date: createdDate,
      time: timing,
      dateTime: `${createdDate} ${timing !== "N/A" ? timing : ""}`.trim(),
      comment,
      documents: documents.map(doc => ({
        name: doc?.fileName || doc?.documentType || "Document",
        type: doc?.documentType || "Document",
        fileStoreId: doc?.fileStoreId,
        link: pdfDownloadLink(pdfFiles, doc?.fileStoreId)
      })),
      hasDocuments: documents.length > 0,
      sla,
      assignedTo,
    };
  });

  return {
    t,
    tenantId: tenantInfo?.code || "",
    tenantName: tenantInfo?.name || tenantInfo?.i18nKey || "Government Department",
    name: t ? t("TIMELINE_PDF_TITLE") : "Application Timeline",
    heading: t ? t("TIMELINE_PDF_HEADING") : "File Movement Report",
    businessId,
    businessService: t ? t(`CS_COMMON_${businessService?.toUpperCase()}`) : businessService,
    moduleName,
    currentStatus: t ? t(timeline?.[0]?.status || timeline?.[0]?.state || "N/A") : (timeline?.[0]?.status || timeline?.[0]?.state || "N/A"),
    generatedDate: new Date().toLocaleDateString("en-IN", {
      day: "2-digit",
      month: "long",
      year: "numeric",
    }),
    generatedDateTime: new Date().toLocaleString("en-IN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
      hour12: true
    }),
    timelineRows,
    totalSteps: timelineRows.length
  };
};

export default getTimelineAcknowledgementData;
