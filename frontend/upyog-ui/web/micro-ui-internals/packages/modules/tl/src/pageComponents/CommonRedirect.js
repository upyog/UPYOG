import React, { useEffect, useState } from "react";

const CommonRedirect = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const tenantId = Digit.ULBService.getCurrentTenantId() || Digit.ULBService.getStateId();

  useEffect(() => {
    const fetchAndOpenPdf = async () => {
      try {
        setLoading(true);
        const searchParams = new URLSearchParams(window.location.search);
        let filestoreId = searchParams.get("filestore");

        if (!filestoreId && window.location.href.includes("filestore=")) {
          filestoreId = window.location.href.split("filestore=")[1]?.split("&")[0];
        }

        if (!filestoreId) {
          const pathSegments = window.location.pathname.split("/");
          const lastSegment = pathSegments[pathSegments.length - 1];
          if (lastSegment && lastSegment !== "common") {
            filestoreId = lastSegment;
          }
        }

        if (filestoreId) {
          const res = await Digit.UploadServices.Filefetch([filestoreId], tenantId);

          let pdfUrl =
            res?.data?.fileStoreIds?.[0]?.url ||
            res?.data?.[filestoreId] ||
            (typeof res?.data === "string" ? res.data : null);

          if (!pdfUrl && res?.data?.fileStoreIds?.[0]?.fileStoreId) {
            pdfUrl = res?.data?.fileStoreIds?.[0]?.url;
          }

          if (pdfUrl && typeof pdfUrl === "string" && pdfUrl.includes(",")) {
            pdfUrl = Digit.Utils.getFileUrl(pdfUrl);
          }

          if (pdfUrl) {
            window.open(pdfUrl, "_blank");
            setLoading(false);
          } else {
            setError("PDF URL not found from Filestore API");
            setLoading(false);
          }
        } else {
          setError("Filestore ID not found in URL");
          setLoading(false);
        }
      } catch (err) {
        console.error("Error fetching PDF from Filestore API:", err);
        setError("Failed to fetch PDF from Filestore API");
        setLoading(false);
      }
    };

    fetchAndOpenPdf();
  }, []);

  return (
    <div style={{ padding: "30px", textAlign: "center" }}>
      {loading && <h3>Loading E-Signed PDF...</h3>}
      {error && <h3 style={{ color: "red" }}>{error}</h3>}
      {!loading && !error && <h3>E-Signed PDF opened in a new tab.</h3>}
    </div>
  );
};

export default CommonRedirect;