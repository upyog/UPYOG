import { CardSubHeader, Row, StatusTable } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
// pdfDocumentName util is in pt utils; imported for filename fallback if needed
// import { pdfDocumentName } from "../../../../pt/src/utils";

function PropertyOwners({ owners = [] }) {
  const { t } = useTranslation();

  const checkLocation = true;
  const checkOwnerLength = owners?.length || 1;
  let cardStyles = { marginTop: "19px" };
  let statusTableStyles = { position: "relative" };
  let rowContainerStyle = {  fontSize: "16px", lineHeight: "19px", color: "#0B0C0C" };
  if (checkLocation && Number(checkOwnerLength) > 1) {
    cardStyles = {
      marginTop: "19px",
      background: "#FAFAFA",
      border: "1px solid #D6D5D4",
      borderRadius: "4px",
      padding: "8px",
      lineHeight: "19px",
      maxWidth: "600px",
      minWidth: "280px",
    };
  } else if (checkLocation && !(Number(checkOwnerLength) > 1)) {
    cardStyles = { marginTop: "19px", lineHeight: "19px", maxWidth: "600px", minWidth: "280px" };
    statusTableStyles = { position: "relative", marginTop: "19px" };
  }

  if (window.location.href.includes("obps")) {
    cardStyles = { ...cardStyles, maxWidth: "950px" };
    rowContainerStyle = {};
  }

  // state to hold mapping returned by Digit.UploadServices.Filefetch
  const [pdfFilesMap, setPdfFilesMap] = useState({});

  // collect all fileStoreIds present inside owners' values (when value is an array of documents)
  useEffect(() => {
    const ids = [];
    (owners || []).forEach((owner) => {
      (owner.values || []).forEach((val) => {
        if (Array.isArray(val?.value)) {
          val.value.forEach((doc) => {
            if (doc?.fileStoreId) ids.push(doc.fileStoreId);
          });
        }
      });
    });

    const uniqueIds = [...new Set(ids)];
    if (uniqueIds.length === 0) {
      console.debug("PropertyOwners: no fileStoreIds found in owners values");
      return;
    }

    // Defensive: ensure Digit and UploadServices are available before calling
    if (typeof Digit === "undefined" || !Digit.UploadServices || !Digit.ULBService) {
      console.warn("PropertyOwners: Digit or UploadServices not available — skipping file fetch");
      return;
    }

    const fetchFiles = async () => {
      try {
        const tenant = Digit.ULBService.getCurrentTenantId();
        console.debug("PropertyOwners: fetching files for ids", uniqueIds, "tenant", tenant);
        const res = await Digit.UploadServices.Filefetch(uniqueIds, tenant);
        // other modules expect res.data mapping; tolerate both shapes
        const files = res?.data || res || {};
        setPdfFilesMap(files);
      } catch (e) {
        console.error("PropertyOwners: error fetching files", e);
        // swallow — render without links
        setPdfFilesMap({});
      }
    };

    fetchFiles();
  }, [owners]);

  // helper to pick primary file URL from the mapping (same behaviour as pdfDownloadLink util)
  const pickFileUrl = (documents = {}, fileStoreId = "") => {
    let downloadLink = (documents && documents[fileStoreId]) || "";
    let differentFormats = downloadLink?.split(",") || [];
    let fileURL = "";
    differentFormats.forEach((link) => {
      if (!link.includes("large") && !link.includes("medium") && !link.includes("small")) {
        fileURL = link;
      }
    });
    console.log("fileURL==",fileURL)
    return fileURL;
  };

  return (
    <React.Fragment>
      {owners.map((owner, ownerIndex) => (
        <div key={`${owner?.title || "owner"}-${ownerIndex}`} style={cardStyles}>
          <CardSubHeader
            style={
              checkLocation && Number(checkOwnerLength) > 1
                ? { marginBottom: "8px", paddingBottom: "9px", color: "#0B0C0C", fontSize: "16px", lineHeight: "19px" }
                : { marginBottom: "8px", color: "#505A5F", fontSize: "16px" }
            }
          >
            {checkLocation && Number(checkOwnerLength) > 1 ? `${t(owner?.title)} ${ownerIndex + 1}` : t(owner?.title)}
          </CardSubHeader>

          <StatusTable style={statusTableStyles}>
            <div
              style={{
                maxWidth: "640px",
                top: 0,
                left: 0,
                bottom: 0,
                right: 0,
                width: "auto",
              }}
            ></div>

            {owner?.values?.map((value, idx) => {
              // image/map values
              if (value.map === true && value.value !== "N/A") {
                return (
                  <Row
                    key={`${value.title}-${idx}`}
                    label={t(value.title)}
                    text={<img src={value.value} alt="" privacy={value?.privacy} />}
                  />
                );
              }

              const rawVal = value?.value;

              // If the value is an array of documents, render them as links (if file URLs are available)
              if (Array.isArray(rawVal)) {
                return (
                  <div style={{ marginBottom: "8px" }} key={`${value.title}-${idx}`}>
                    <h2 style={{ fontSize: "16px", color: "rgb(11, 12, 12)", marginBottom: "6px", fontWeight: '700' }}>{t(value.title)}</h2>
                    <div style={{ display: "flex", flexDirection: "column", gap: "6px" }}>
                      {rawVal.map((doc, di) => {
                        const fileUrl = doc?.fileStoreId ? pickFileUrl(pdfFilesMap, doc.fileStoreId) : null;
                        const docLabel = doc?.documentType ? t(`PT_${doc.documentType.replace(".", "_")}`) : doc?.fileStoreId || `Document ${di + 1}`;
                        return fileUrl ? (
                          <a key={di} href={fileUrl} target="_blank" rel="noopener noreferrer">
                            {docLabel}
                          </a>
                        ) : (
                          <div key={di}>{docLabel}</div>
                        );
                      })}
                    </div>
                  </div>
                );
              }

              const displayValue =
                typeof rawVal === "string"
                  ? rawVal || "N/A"
                  : rawVal && typeof rawVal === "object"
                  ? rawVal.value || rawVal.downloadLink || "N/A"
                  : "N/A";

              return (
                <span key={`${value.title}-${idx}`}>
                  <Row
                    key={t(value.title)}
                    label={!checkLocation ? t(value.title) : `${t(value.title)}`}
                    text={displayValue}
                    last={idx === value?.values?.length - 1}
                    caption={value.caption}
                    className="border-none"
                    textStyle={value.textStyle}
                    privacy={value?.privacy}
                    rowContainerStyle={rowContainerStyle}
                  />
                </span>
              );
            })}
          </StatusTable>
        </div>
      ))}
    </React.Fragment>
  );
}

export default PropertyOwners;

