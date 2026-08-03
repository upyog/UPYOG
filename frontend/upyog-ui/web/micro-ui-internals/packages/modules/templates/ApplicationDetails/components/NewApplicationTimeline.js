import React, { useMemo } from "react";
import { useTranslation } from "react-i18next";
import { Loader } from "@nudmcdgnpm/digit-ui-react-components";
import getTimelineAcknowledgementData from "../getTimelineAcknowledgementData";
const PDFSvg = React.memo(({ width = 20, height = 20, style }) => (
  <svg style={style} xmlns="http://www.w3.org/2000/svg" width={width} height={height} viewBox="0 0 20 20" fill="gray">
    <path d="M20 2H8c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-8.5 7.5c0 .83-.67 1.5-1.5 1.5H9v2H7.5V7H10c.83 0 1.5.67 1.5 1.5v1zm5 2c0 .83-.67 1.5-1.5 1.5h-2.5V7H15c.83 0 1.5.67 1.5 1.5v3zm4-3H19v1h1.5V11H19v2h-1.5V7h3v1.5zM9 9.5h1v-1H9v1zM4 6H2v14c0 1.1.9 2 2 2h14v-2H4V6zm10 5.5h1v-3h-1v3z" />
  </svg>
));


// new timeline made
const pdfDownloadLink = (documents, fileStoreId) => {
  const downloadLink = documents?.[fileStoreId] || "";
  const formats = downloadLink?.split(",")?.filter(Boolean) || [];
  return formats?.find((link) => !link?.includes("large") && !link?.includes("medium") && !link?.includes("small")) || formats?.[0] || "";
};

const TimelineDocument = React.memo(({ value, Code, index }) => {
  const { isLoading, data } = Digit.Hooks.ads.useADSDocumentSearch({ value }, { value }, Code, index);

  const documents = useMemo(() => {
    const docs = value?.documents ? value?.documents?.documents : value;
    return docs?.filter((doc) => doc?.documentType === Code) || [];
  }, [value, Code]);

  if (isLoading) return <Loader />;

  return (
    <div className="custom-doc-container">
      {documents?.map((document, idx) => {
        const documentLink = pdfDownloadLink(data?.pdfFiles, document?.fileStoreId || document?.documentUid);
        if (!documentLink) return null;
        return (
          <a key={idx} target="_blank" rel="noopener noreferrer" href={documentLink} className="custom-doc-link">
            <PDFSvg />
            <span>{document?.fileName || "Document"}</span>
          </a>
        );
      })}
    </div>
  );
});

export default function NewApplicationTimeline({ workflowDetails, t, tenantId = Digit.ULBService.getCurrentTenantId(), timeObj, empUserName = null, handleSetEmpDesignation = () => { } }) {
  const { isLoading, data: docData } = Digit.Hooks.ads.useADSDocumentSearch(
    { value: workflowDetails?.data?.timeline?.flatMap((item) => item?.wfDocuments) || [] },
    { value: workflowDetails?.data?.timeline?.flatMap((item) => item?.wfDocuments) || [] },
    null,
    0
  );

  const handleDownloadPDF = React.useCallback(() => {
    if (!isLoading) {
      const tenantInfo = Digit.SessionStorage.get("CITIZEN.COMMON.HOME.CITY") || {};
      const acknowledgementData = getTimelineAcknowledgementData(workflowDetails, tenantInfo, docData?.pdfFiles || {}, t);
      Digit.Utils.pdf.generateTimelinePDF(acknowledgementData);
    }
  }, [workflowDetails, docData, t, isLoading]);

  const parseActionDateTime = (auditDetails) => {
    if (!auditDetails?.created || !auditDetails?.timing) return null;

    // created = "21/12/2025"
    // timing  = "03:52 PM"
    const [day, month, year] = auditDetails.created.split("/");

    // build a safe datetime string
    return new Date(`${year}-${month}-${day} ${auditDetails.timing}`);
  };

  const calculateSLA = (currentDate, previousDate) => {
    if (!currentDate || !previousDate) return null;

    const diffMs = currentDate - previousDate;

    const totalMinutes = Math.floor(diffMs / (1000 * 60));
    const hours = Math.floor(totalMinutes / 60);
    const days = Math.floor(hours / 24);

    if (days > 0) return `${days} day(s)`;
    if (hours > 0) return `${hours} hour(s)`;
    return `${totalMinutes} minute(s)`;
  };

  const normalizeTimeline = (workflowDetails) => {
    const details = workflowDetails?.data || workflowDetails;
    const rawTimeline = details?.timeline;

    if (!Array.isArray(rawTimeline)) return [];

    // IMPORTANT: oldest → newest for SLA calculation
    const orderedTimeline = [...rawTimeline].reverse();

    const normalized = orderedTimeline.map((item, index) => {
      const currentDate = parseActionDateTime(item.auditDetails);
      const previousDate = index > 0 ? parseActionDateTime(orderedTimeline[index - 1]?.auditDetails) : null;

      return {
        ...item,

        // semantic correctness
        status: item?.state || item?.status,
        assigner: item?.assigner || null,
        assignes: Array.isArray(item?.assignes) ? item.assignes : [],

        wfComment: Array.isArray(item?.wfComment) ? item.wfComment : item?.comment ? [item.comment] : [],

        wfDocuments: Array.isArray(item?.wfDocuments) ? item.wfDocuments : [],

        // SLA fields
        actionDateTime: currentDate, // raw Date object (DO NOT SHOW IN UI)
        sla: calculateSLA(currentDate, previousDate),
      };
    });

    // return newest → oldest for UI
    return normalized.reverse();
  };

  const data = useMemo(() => normalizeTimeline(workflowDetails), [workflowDetails]);
  // Assuming data is latest first, we don't reverse.
  const sortedData = data?.filter((val) => !(val?.performedAction === "SAVE_AS_DRAFT")) || [];

  const codesArray = sortedData.map((item) => item?.assigner?.userName).filter(Boolean);

  const uniqueCodes = [...new Set(codesArray)].join(",");

  const employeeData = Digit.Hooks.useEmployeeSearch(tenantId, { codes: uniqueCodes, isActive: true }, { enabled: !!uniqueCodes });

  const deptMap = {};
  employeeData?.data?.Employees?.forEach((emp) => {
    const assignment = emp?.assignments?.[0];
    const translationKey = `COMMON_MASTERS_DESIGNATION_${assignment?.designation}`;
    deptMap[emp?.code] = translationKey;
  });

  if (employeeData && empUserName) {
    handleSetEmpDesignation(t(deptMap[empUserName]));
  }

  if (isLoading) return <Loader />;
  return (
    <React.Fragment>
      <div className="custom-timeline-container">
        {timeObj && (
          <div style={{ marginBottom: "8px", fontStyle: "italic" }}>
            {t("TOTAL_TIME_TAKEN")}: {timeObj?.days} {t("DAYS")} {timeObj?.hours} {t("HOURS")} {timeObj?.minutes} {t("MINUTES")} {timeObj?.seconds}{" "}
            {t("SECONDS")}
          </div>
        )}
        <div className="custom-timeline-header">
          <div className="custom-tracking-status-header">
            <div className="custom-tracking-line"></div>
            <span className="custom-tracking-status-text">{t("TRACKING STATUS")}</span>
            <div className="custom-tracking-line"></div>
          </div>
          <div className="custom-title-bar-row">
            <h2 className="custom-timeline-title">{t("Application History")}</h2>
            <span onClick={handleDownloadPDF} className="download-button">
              <PDFSvg width={50} height={40} style={{ marginRight: "8px" }} />
            </span>
          </div>
        </div>

        <div className="custom-timeline-entries">
          {sortedData?.map((item, index) => (
            <div key={index} className="custom-timeline-entry">
              <div className="custom-vertical-line"></div>

              {/* Date badge */}
              <div className="custom-date-badge">
                {item?.auditDetails?.created} {item?.auditDetails?.timing}
              </div>

              {/* Main timeline entry container */}
              <div className="custom-entry-content">
                {/* Left side: circular icon on vertical line */}
                <div className="custom-icon-container">
                  <div className="custom-circular-icon">
                    <svg className="custom-mail-icon" viewBox="0 0 24 24">
                      <rect x="3" y="5" width="18" height="14" rx="2" />
                      <path d="M3 7l9 6 9-6" />
                    </svg>
                  </div>
                </div>

                {/* Right side: content card */}
                <div className="custom-content-card">
                  <div className="custom-card-top">
                    {/* Column 1: Action taken by */}
                    <div className="custom-card-column">
                      <h3 className="custom-action-title">{t("Action taken by")}</h3>
                      {item?.assigner && (
                        <div className="custom-officer-info">
                          <div className="custom-officer-name">
                            {item?.assigner?.name || t("CS_COMMON_NA")}
                            {deptMap[item?.assigner?.userName] && (
                              <span className="custom-officer-name">- {t(deptMap[item?.assigner?.userName])}</span>
                            )}
                          </div>
                          {item?.assigner?.emailId && (
                            <div className="custom-officer-email">
                              <span className="custom-email-label">{t("Email")}</span> {item?.assigner?.emailId}
                            </div>
                          )}
                        </div>
                      )}
                    </div>

                    {/* Column 2: Time Taken (Centered/Balanced) */}
                    <div className="custom-card-column custom-card-column-mid">
                      {item?.sla && (
                        <React.Fragment>
                          <h3 className="custom-action-title">{t("Time Taken")}</h3>
                          <div className="custom-officer-email">
                            <span className="custom-email-label">{item?.sla}</span>
                          </div>
                        </React.Fragment>
                      )}
                    </div>

                    {/* Column 3: Action (Right Aligned) */}
                    <div className="custom-card-column custom-card-column-right">
                      <h3 className="custom-action-title">{t("Action")}</h3>
                      <div className="custom-status-text">{t(item?.performedAction || "CS_COMMON_NA")}</div>
                    </div>
                  </div>

                  {item?.wfComment && item?.wfComment?.length > 0 && item?.wfComment?.some((c) => c?.trim()) && (
                    <div className="custom-comments-section container-full-width">
                      <div className="custom-comments-content">
                        <h4 className="custom-comments-title">{t("Officer Comments")}</h4>
                        <div className="custom-comment-text">
                          {item?.wfComment?.map((comment, idx) => {
                            const pattern = /\[#\?.*?\*\*\]/;
                            const truncatedComment = typeof comment === "string" ? comment?.split(pattern)[0] : comment;
                            return <p key={idx}>{truncatedComment}</p>;
                          })}
                        </div>
                      </div>
                      {item?.assignes?.length > 0 && (
                        <div className="custom-assigned-to-footer">
                          <h3 className="custom-comments-title">{t("Assigned To")}</h3>
                          <div className="custom-officer-info">
                            <div className="custom-officer-name">
                              {item.assignes[0]?.name}
                              {deptMap[item.assignes[0]?.userName] && (
                                <span className="custom-officer-email">- {t(deptMap[item.assignes[0]?.userName])}</span>
                              )}
                            </div>
                            {/* {deptMap[item.assignes[0]?.userName] && (
                              <div className="custom-officer-email">{t(deptMap[item.assignes[0]?.userName])}</div>
                            )} */}
                          </div>
                        </div>
                      )}
                    </div>
                  )}

                  {item?.wfDocuments && item?.wfDocuments?.length > 0 && (
                    <div className="custom-comments-section-no-border">
                      <h4 className="custom-comments-title">Document Attached:</h4>
                      <div className="custom-comment-text">
                        {item?.wfDocuments?.map((doc, index) => (
                          <TimelineDocument key={`${doc?.documentType}-${index}`} value={item?.wfDocuments} Code={doc?.documentType} index={index} />
                        ))}
                      </div>
                    </div>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </React.Fragment>
  );
}