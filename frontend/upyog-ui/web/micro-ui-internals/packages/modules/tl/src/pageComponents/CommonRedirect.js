import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Card, Header, Loader, PDFSvg, DownloadIcon, ExternalLinkIcon } from "@upyog/digit-ui-react-components";

const CommonRedirect = () => {
  const { t } = useTranslation();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [pdfUrl, setPdfUrl] = useState(null);
  const [filestoreId, setFilestoreId] = useState(null);
  const tenantId = Digit.ULBService.getStateId();

  useEffect(() => {
    // Extract filestoreId from URL parameters on mount without triggering API call or popup window
    const searchParams = new URLSearchParams(window.location.search);
    let id = searchParams.get("filestore");

    if (!id && window.location.href.includes("filestore=")) {
      id = window.location.href.split("filestore=")[1]?.split("&")[0];
    }

    if (!id) {
      const pathSegments = window.location.pathname.split("/");
      const lastSegment = pathSegments[pathSegments.length - 1];
      if (lastSegment && lastSegment !== "common") {
        id = lastSegment;
      }
    }

    if (id) {
      setFilestoreId(id);
    } else {
      setError(t("TL_FILESTORE_ID_NOT_FOUND", "Filestore ID not found in URL"));
    }
  }, []);

  const triggerDownload = async (url, fileName) => {
    if (!url) return;
    const name = fileName || `TL_Esigned_Certificate_${filestoreId || "doc"}.pdf`;
    try {
      const response = await fetch(url);
      const blob = await response.blob();
      const blobUrl = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = blobUrl;
      link.download = name;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(blobUrl);
    } catch (err) {
      console.warn("Direct blob download failed, falling back to link click:", err);
      const link = document.createElement("a");
      link.href = url;
      link.download = name;
      link.target = "_blank";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
  };

  const handleDownloadAndOpen = async () => {
    if (!filestoreId) {
      setError(t("TL_FILESTORE_ID_NOT_FOUND", "Filestore ID not found in URL"));
      return;
    }

    let urlToUse = pdfUrl;

    if (!urlToUse) {
      try {
        setLoading(true);
        setError(null);
        const res = await Digit.UploadServices.Filefetch([filestoreId], tenantId);

        let url =
          res?.data?.fileStoreIds?.[0]?.url ||
          res?.data?.[filestoreId] ||
          (typeof res?.data === "string" ? res.data : null);

        if (!url && res?.data?.fileStoreIds?.[0]?.fileStoreId) {
          url = res?.data?.fileStoreIds?.[0]?.url;
        }

        if (url && typeof url === "string" && url.includes(",")) {
          url = Digit.Utils.getFileUrl(url);
        }

        if (url) {
          urlToUse = url;
          setPdfUrl(url);
        } else {
          setError(t("TL_PDF_URL_NOT_FOUND", "PDF URL not found from Filestore API"));
          setLoading(false);
          return;
        }
      } catch (err) {
        console.error("Error fetching PDF from Filestore API:", err);
        setError(t("TL_FAILED_FETCH_PDF", "Failed to fetch PDF from Filestore API"));
        setLoading(false);
        return;
      } finally {
        setLoading(false);
      }
    }

    if (urlToUse) {
      // 1. Open in new tab
      window.open(urlToUse, "_blank");
      // 2. Trigger file download
      await triggerDownload(urlToUse);
    }
  };

  return (
    <div style={{ padding: "40px 16px", maxWidth: "680px", margin: "0 auto" }}>
      <Header style={{ textAlign: "center" }}>{t("TL_ESIGNED_CERTIFICATE_HEADER", "E-Signed Trade License Certificate")}</Header>

      <Card style={{ padding: "36px 28px", marginTop: "20px", borderRadius: "12px", boxShadow: "0 6px 18px rgba(0, 0, 0, 0.06)" }}>
        {error ? (
          <div style={{ textAlign: "center", padding: "20px 16px" }}>
            <div
              style={{
                width: "56px",
                height: "56px",
                borderRadius: "50%",
                backgroundColor: "#fbebe8",
                color: "#d32f2f",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                margin: "0 auto 16px auto",
                fontSize: "24px",
                fontWeight: "bold",
              }}
            >
              !
            </div>
            <h3 style={{ fontSize: "18px", color: "#d32f2f", fontWeight: "600", marginBottom: "8px" }}>
              {t("TL_COMMON_REDIRECT_ERROR_HEADER", "Unable to Load Certificate")}
            </h3>
            <p style={{ color: "#505a5f", fontSize: "14px", marginBottom: "24px" }}>{error}</p>
            <button
              onClick={handleDownloadAndOpen}
              style={{
                backgroundColor: "#f47738",
                color: "#fff",
                border: "none",
                borderRadius: "4px",
                padding: "10px 24px",
                fontSize: "14px",
                fontWeight: "600",
                cursor: "pointer",
              }}
            >
              {t("TL_COMMON_REDIRECT_RETRY", "Try Again")}
            </button>
          </div>
        ) : (
          <div style={{ textAlign: "center" }}>
            <div
              style={{
                backgroundColor: "#f8f9fa",
                border: "1px solid #e5e7eb",
                borderRadius: "10px",
                padding: "32px 24px",
                marginBottom: "28px",
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
              }}
            >
              <div style={{ marginBottom: "16px", display: "inline-block" }}>
                <PDFSvg width="80px" height="80px" style={{ width: "80px", height: "80px" }} />
              </div>
              <h3 style={{ fontSize: "22px", fontWeight: "700", color: "#0b0c0c", marginBottom: "8px" }}>
                {t("TL_ESIGNED_CERTIFICATE_TITLE", "E-Signed Trade License Certificate")}
              </h3>
              <p style={{ fontSize: "14px", color: "#505a5f", maxWidth: "480px", lineHeight: "1.6", marginBottom: "16px" }}>
                {t(
                  "TL_ESIGNED_CERTIFICATE_DESC",
                  "Click the button below to fetch, open in a new tab, and download your e-signed Trade License certificate."
                )}
              </p>
              {filestoreId && (
                <span
                  style={{
                    display: "inline-block",
                    backgroundColor: "#e5edf5",
                    color: "#1d70b8",
                    padding: "6px 14px",
                    borderRadius: "16px",
                    fontSize: "13px",
                    fontWeight: "600",
                    letterSpacing: "0.3px",
                  }}
                >
                  {t("TL_FILESTORE_ID", "File Store ID")}: {filestoreId}
                </span>
              )}
            </div>

            <div style={{ display: "flex", justifyContent: "center", alignItems: "center" }}>
              <button
                onClick={handleDownloadAndOpen}
                disabled={loading}
                style={{
                  backgroundColor: loading ? "#f8a57c" : "#f47738",
                  color: "#ffffff",
                  border: "none",
                  borderRadius: "6px",
                  padding: "16px 32px",
                  fontSize: "16px",
                  fontWeight: "600",
                  cursor: loading ? "not-allowed" : "pointer",
                  display: "inline-flex",
                  alignItems: "center",
                  gap: "12px",
                  boxShadow: "0 4px 10px rgba(244, 119, 56, 0.3)",
                  transition: "all 0.2s ease-in-out",
                }}
              >
                {loading ? (
                  <React.Fragment>
                    <Loader style={{ width: "20px", height: "20px", margin: 0 }} />
                    <span>{t("TL_DOWNLOADING_CERTIFICATE", "Fetching Certificate...")}</span>
                  </React.Fragment>
                ) : (
                  <React.Fragment>
                    <DownloadIcon fill="#ffffff" styles={{ width: "20px", height: "20px" }} />
                    <span>{t("TL_DOWNLOAD_AND_OPEN_CERTIFICATE", "Download & View Certificate")}</span>
                    <ExternalLinkIcon fill="#ffffff" styles={{ width: "16px", height: "16px", marginLeft: "4px" }} />
                  </React.Fragment>
                )}
              </button>
            </div>
          </div>
        )}
      </Card>
    </div>
  );
};

export default CommonRedirect;