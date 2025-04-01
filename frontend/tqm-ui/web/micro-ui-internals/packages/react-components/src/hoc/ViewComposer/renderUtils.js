import React, { Fragment, useState, useEffect } from "react";
import CardSectionHeader from "../../atoms/CardSectionHeader";
import { StatusTable, Row } from "../../atoms/StatusTable";
import CardSubHeader from "../../atoms/CardSubHeader";
import { useTranslation } from "react-i18next";
import { PDFSvg } from "../../atoms/svgindex";
import WorkflowTimeline from "../../atoms/WorkflowTimeline";
import WorkflowActions from "../../atoms/WorkflowActions";
import { Link } from "react-router-dom";

export const RenderDataSection = ({ section }) => {
  const { t } = useTranslation();
  return (
    <>
      {section.cardHeader && <CardSubHeader style={section?.cardHeader?.inlineStyles}>{section.cardHeader.value}</CardSubHeader>}
      <StatusTable style={section?.inlineStyles}>
        {section.sectionHeader && <CardSectionHeader style={section?.sectionHeader?.inlineStyles}>{section.sectionHeader.value}</CardSectionHeader>}
        {section.values.map((row, rowIdx) => {
          return (
            <Row
              key={row.key}
              label={t(row.key)}
              text={
                row?.isLink ? (
                  <div>
                    <Link to={row?.to}>
                      <span className="link" style={{ color: "#F47738" }}>
                        {row?.value}
                      </span>
                    </Link>
                  </div>
                ) : row?.isSla ? (
                  <span className={row.isSuccess ? "sla-cell-success" : "sla-cell-error"}> {row?.value} </span>
                ) : (
                  row.value
                )
              }
              last={rowIdx === section.values?.length - 1}
              caption={row.caption}
              className="border-none"
              /* privacy object set to the Row Component */
              privacy={row?.value?.privacy}
              rowContainerStyle={{}}
              textStyle={{}}
              labelStyle={{}}
              amountStyle={{}}
            />
          );
        })}
      </StatusTable>
    </>
  );
};

export const RenderDocumentsSection = ({ section }) => {
  const { documents } = section;
  const { t } = useTranslation();
  const [filesArray, setFilesArray] = useState(() => []);
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [pdfFiles, setPdfFiles] = useState({});

  useEffect(() => {
    let acc = [];
    documents?.forEach((element, index, array) => {
      acc = [...acc, ...(element.values ? element.values : [])];
    });
    setFilesArray(acc?.map((value) => value?.fileStoreId));
  }, [documents]);

  useEffect(() => {
    if (filesArray?.length && documents?.[0]?.BS === "BillAmend") {
      Digit.UploadServices.Filefetch(filesArray, Digit.ULBService.getCurrentTenantId()).then((res) => {
        setPdfFiles(res?.data);
      });
    } else if (filesArray?.length) {
      Digit.UploadServices.Filefetch(filesArray, Digit.ULBService.getCurrentTenantId()).then((res) => {
        setPdfFiles(res?.data);
      });
    }
  }, [filesArray]);

  return (
    <div style={section?.inlineStyles}>
      {documents?.map((document, index) => (
        <React.Fragment key={index}>
          {document?.title ? <CardSectionHeader>{t(document?.title)}</CardSectionHeader> : null}
          <div style={{ display: "flex", flexWrap: "wrap", justifyContent: "flex-start" }}>
            {document?.values && document?.values.length > 0
              ? document?.values?.map((value, index) => (
                  <a
                    target="_"
                    href={pdfFiles[value.fileStoreId]?.split(",")[0]}
                    style={{ minWidth: "80px", marginRight: "10px", maxWidth: "100px", height: "auto" }}
                    key={index}
                  >
                    <div style={{ display: "flex", justifyContent: "center" }}>
                      <PDFSvg />
                    </div>
                    <p style={{ marginTop: "8px", fontWeight: "bold", wordBreak: "break-word", marginLeft: "1rem" }}>{t(value?.title)}</p>
                  </a>
                ))
              : !window.location.href.includes("citizen") && (
                  <div>
                    <p>{t("BPA_NO_DOCUMENTS_UPLOADED_LABEL")}</p>
                  </div>
                )}
          </div>
        </React.Fragment>
      ))}
    </div>
  );
};

export const RenderWfHistorySection = ({ section }) => {
  const { businessService, applicationNo, tenantId, timelineStatusPrefix = undefined, statusAttribute = undefined } = section;
  return (
    <WorkflowTimeline
      businessService={businessService}
      applicationNo={applicationNo}
      tenantId={tenantId}
      timelineStatusPrefix={timelineStatusPrefix}
      statusAttribute={statusAttribute}
    />
  );
};

export const RenderWfActions = ({ section }) => {
  const {
    forcedActionPrefix = undefined,
    businessService,
    applicationNo,
    tenantId,
    applicationDetails,
    url,
    moduleCode = "Estimate",
    editApplicationNumber,
  } = section;

  return (
    <WorkflowActions
      forcedActionPrefix={`WF_${businessService}_ACTION`}
      businessService={businessService}
      applicationNo={applicationNo}
      tenantId={tenantId}
      applicationDetails={applicationDetails}
      url={url}
      moduleCode={moduleCode}
      editApplicationNumber={editApplicationNumber}
    />
  );
};
