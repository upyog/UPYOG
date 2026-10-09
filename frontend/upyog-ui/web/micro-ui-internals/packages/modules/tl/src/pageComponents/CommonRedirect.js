import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Card, Header, Loader, PDFSvg, DownloadIcon, ExternalLinkIcon } from "@nudmcdgnpm/digit-ui-react-components";

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
    <div className="tl-cert-page-wrapper">
      <Header className="tl-cert-header">{t("TL_ESIGNED_CERTIFICATE_HEADER", "E-Signed Trade License Certificate")}</Header>

      <Card className="tl-cert-card">
        {error ? (
          <div className="tl-cert-error-box">
            <div className="tl-cert-error-icon">!</div>
            <h3 className="tl-cert-error-title">
              {t("TL_COMMON_REDIRECT_ERROR_HEADER", "Unable to Load Certificate")}
            </h3>
            <p className="tl-cert-error-msg">{error}</p>
            <button onClick={handleDownloadAndOpen} className="tl-cert-retry-btn">
              {t("TL_COMMON_REDIRECT_RETRY", "Try Again")}
            </button>
          </div>
        ) : (
          <div className="tl-cert-content-box">
            <div className="tl-cert-preview-card">
              <div className="tl-cert-pdf-icon">
                <PDFSvg width="80px" height="80px" />
              </div>
              <h3 className="tl-cert-title">
                {t("TL_ESIGNED_CERTIFICATE_TITLE", "E-Signed Trade License Certificate")}
              </h3>
              <p className="tl-cert-desc">
                {t(
                  "TL_ESIGNED_CERTIFICATE_DESC",
                  "Click the button below to fetch, open in a new tab, and download your e-signed Trade License certificate."
                )}
              </p>
              {filestoreId && (
                <span className="tl-cert-filestore-pill">
                  {t("TL_FILESTORE_ID", "File Store ID")}: {filestoreId}
                </span>
              )}
            </div>

            <div className="tl-cert-btn-row">
              <button
                onClick={handleDownloadAndOpen}
                disabled={loading}
                className="tl-cert-download-btn"
              >
                {loading ? (
                  <React.Fragment>
                    <Loader className="tl-cert-loader" />
                    <span>{t("TL_DOWNLOADING_CERTIFICATE", "Fetching Certificate...")}</span>
                  </React.Fragment>
                ) : (
                  <React.Fragment>
                    <DownloadIcon fill="#ffffff" />
                    <span>{t("TL_DOWNLOAD_AND_OPEN_CERTIFICATE", "Download & View Certificate")}</span>
                    <ExternalLinkIcon fill="#ffffff" />
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