import {
  BreakLine,
  Card,
  CardSectionHeader,
  CardSubHeader,
  CheckPoint,
  ConnectingCheckPoints,
  Loader,
  Row,
  StatusTable,
  LinkButton,
  PDFSvg,
  EditIcon,
  ViewsIcon,
  DeleteIcon,
} from "@nudmcdgnpm/digit-ui-react-components";
import { values } from "lodash";
import React, { Fragment, useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link, useParams } from "react-router-dom";
import BPADocuments from "./BPADocuments";
import InspectionReport from "./InspectionReport";
import NOCDocuments from "./NOCDocuments";
import PermissionCheck from "./PermissionCheck";
import PropertyDocuments from "./PropertyDocuments";
import PropertyEstimates from "./PropertyEstimates";
import PropertyFloors from "./PropertyFloors";
import PropertyOwners from "./PropertyOwners";
import ScruntinyDetails from "./ScruntinyDetails";
import SubOccupancyTable from "./SubOccupancyTable";
import TLCaption from "./TLCaption";
import TLTradeAccessories from "./TLTradeAccessories";
import TLTradeUnits from "./TLTradeUnits";
import WSAdditonalDetails from "./WSAdditonalDetails";
import WSFeeEstimation from "./WSFeeEstimation";
import DocumentsPreview from "./DocumentsPreview";
import InfoDetails from "./InfoDetails";
import ViewBreakup from "./ViewBreakup";
import ArrearSummary from "../../../common/src/payments/citizen/bills/routes/bill-details/arrear-summary";
import { MarkOnMap, ViewOnMap, DiginpinMapPopup } from "@nudmcdgnpm/upyog-ui-module-gis";
import { GeoLocationWithDigipin } from "@nudmcdgnpm/digit-ui-react-components";

// Helper function to convert "lat,lng" string to {lat:..., lng:...} object
const coordinateFormatter = (locationString) => {
  if (locationString && typeof locationString === "string") {
    const [lat, lng] = locationString.split(", ").map((coord) => parseFloat(coord.trim()));
    return { lat, lng };
  }
  return locationString; // return as-is if already an object
};

function ApplicationDetailsContent({
  applicationDetails,
  workflowDetails,
  isDataLoading,
  applicationData,
  businessService,
  timelineStatusPrefix,
  id,
  showTimeLine = true,
  statusAttribute = "status",
  paymentsList,
  oldValue,
  isInfoLabel = false,
}) {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  let { id: applicationNo } = useParams(); // Extracts PG-1013-2025-I-001019
  const isAssetModule = window.location.href.includes("/asset/");
  const [applicationDetailsofAsset, setApplicationDetailsofAsset] = useState(null);
  const [copiedAppNo, setCopiedAppNo] = useState(false);

  useEffect(() => {
    if (!isAssetModule) return;

    const fetchAssetDetails = async () => {
      try {
        const response = await Digit.AssetService.search(tenantId, { applicationNumber: applicationNo });
        setApplicationDetailsofAsset(response);
      } catch (err) {
        console.error("Failed to fetch asset details", err);
      }
    };

    fetchAssetDetails();
  }, [tenantId, applicationNo]);

  function OpenImage(imageSource, index, thumbnailsToShow) {
    window.open(thumbnailsToShow?.fullImage?.[0], "_blank");
  }

  const [showMapModal, setShowMapModal] = useState(false);
  const [selectedLocation, setSelectedLocation] = useState(null);

  const [geometry, setGeometry] = useState(null);
  const [showMap, setShowMap] = useState(false);
  const [area, setArea] = useState(null);

  // Holds the lat, lng, and digipin to pass into the popup; its presence also controls modal visibility
  const [digipinMapData, setDigipinMapData] = useState(null);

  const handleOpenMap = (geometry) => {
    setSelectedLocation(geometry);
    setShowMapModal(true);
  };

  const handleCloseMap = () => {
    setShowMapModal(false);
    setSelectedLocation(null);
  };

  // Opens the digipin map modal; parses lat/lng strings to floats for Leaflet
  const handleOpenDigipinMap = (lat, lng, digipin) => {
    setDigipinMapData({ digipin, lat: parseFloat(lat), lng: parseFloat(lng) });
  };

  // Closes the digipin map modal and clears its data
  const handleCloseDigipinMap = () => {
    setDigipinMapData(null);
  };

  const [fetchBillData, updatefetchBillData] = useState({});

  const [billData, updateCanFetchBillData] = useState({
    loading: false,
    loaded: false,
    canLoad: false,
  });

  useEffect(() => {
    const propId = applicationData?.propertyId;
    const resTenant = applicationData?.tenantId || tenantId;
    if (applicationData?.status === "ACTIVE" && propId && resTenant && !billData.loading && !billData.loaded && !billData.canLoad) {
      const fetchBill = async () => {
        updateCanFetchBillData({ loading: true, loaded: false, canLoad: true });
        try {
          const assessmentData = await Digit.PTService.assessmentSearch({ tenantId: resTenant, filters: { propertyIds: propId } });
          let bill = {};
          if (assessmentData?.Assessments?.length > 0) {
            bill = await Digit.PaymentService.fetchBill(resTenant, { businessService: "PT", consumerCode: propId });
          }
          updatefetchBillData(bill);
          updateCanFetchBillData({ loading: false, loaded: true, canLoad: true });
        } catch (err) {
          console.error(err);
          updateCanFetchBillData({ loading: false, loaded: true, canLoad: true });
        }
      };
      fetchBill();
    }
  }, [applicationData?.status, applicationData?.propertyId, applicationData?.tenantId, tenantId, billData.loading, billData.loaded, billData.canLoad]);

  const convertEpochToDateDMY = (dateEpoch) => {
    if (dateEpoch == null || dateEpoch == undefined || dateEpoch == "") {
      return "NA";
    }
    const dateFromApi = new Date(dateEpoch);
    let month = dateFromApi.getMonth() + 1;
    let day = dateFromApi.getDate();
    let year = dateFromApi.getFullYear();
    month = (month > 9 ? "" : "0") + month;
    day = (day > 9 ? "" : "0") + day;
    return `${day}/${month}/${year}`;
  };

  const getTimelineCaptions = (checkpoint, index = 0, timeline) => {
    if (checkpoint.state === "OPEN" || (checkpoint.status === "INITIATED" && !window.location.href.includes("/obps/"))) {
      const caption = {
        date: convertEpochToDateDMY(applicationData?.auditDetails?.createdTime),
        source: applicationData?.channel || "",
      };
      return <TLCaption data={caption} />;
    } else if (window.location.href.includes("/obps/") || window.location.href.includes("/noc/") || window.location.href.includes("/ws/")) {
      const privacy = {
        uuid: checkpoint?.assignes?.[0]?.uuid,
        fieldName: "mobileNumber",
        model: "User",
        showValue: false,
        loadData: {
          serviceName: "/egov-workflow-v2/egov-wf/process/_search",
          requestBody: {},
          requestParam: { tenantId: applicationDetails?.tenantId, businessIds: applicationDetails?.applicationNo, history: true },
          jsonPath: "ProcessInstances[0].assignes[0].mobileNumber",
          isArray: false,
          d: (res) => {
            let resultstring = "";
            resultstring = `+91 ${_.get(res, `ProcessInstances[${index}].assignes[0].mobileNumber`)}`;
            return resultstring;
          },
        },
      };
      const previousCheckpoint = timeline && timeline[index - 1] && timeline?.[index - 1];
      const caption = {
        date: checkpoint?.auditDetails?.lastModified,
        name: checkpoint?.assignes?.[0]?.name,
        mobileNumber:
          applicationData?.processInstance?.assignes?.[0]?.uuid === checkpoint?.assignes?.[0]?.uuid &&
            applicationData?.processInstance?.assignes?.[0]?.mobileNumber
            ? applicationData?.processInstance?.assignes?.[0]?.mobileNumber
            : checkpoint?.assignes?.[0]?.mobileNumber,
        comment: t(checkpoint && checkpoint?.comment),
        wfComment: previousCheckpoint ? previousCheckpoint && previousCheckpoint.wfComment : [],
        thumbnailsToShow: checkpoint?.thumbnailsToShow,
      };

      return <TLCaption data={caption} OpenImage={OpenImage} privacy={privacy} />;
    } else {
      const caption = {
        date: checkpoint?.auditDetails?.lastModified || convertEpochToDateDMY(applicationData?.auditDetails?.lastModifiedTime),
        name: checkpoint?.assignes?.[0]?.name,
        wfComment: checkpoint?.wfComment,
        mobileNumber: checkpoint?.assignes?.[0]?.mobileNumber,
        thumbnailsToShow: checkpoint?.thumbnailsToShow,
      };

      return <TLCaption data={caption} OpenImage={OpenImage} />;
    }
  };

  const getTranslatedValues = (dataValue, isNotTranslated) => {
    if (dataValue) {
      return !isNotTranslated ? t(dataValue) : dataValue;
    } else {
      return t("NA");
    }
  };

  const getTextValue = (value) => {
    // Handle time values specially
    if (value?.isTimeValue && value?.value) {
      // Format time from "HH:MM" to "H:MM AM/PM"
      const timeString = value.value;
      const [hours, minutes] = timeString.split(":").map((part) => parseInt(part, 10));

      if (!isNaN(hours) && !isNaN(minutes)) {
        const period = hours >= 12 ? "PM" : "AM";
        const hour12 = hours % 12 || 12; // Convert 0 to 12 for 12 AM
        const paddedMinutes = String(minutes).padStart(2, "0"); // Ensures minutes are always two digits
        return `${hour12}:${paddedMinutes} ${period}`;
      }
      return value.value; // Return original if parsing failed
    }
    // Handle other values as before
    if (value?.skip) return value.value;
    else if (value?.isUnit) return value?.value ? `${getTranslatedValues(value?.value, value?.isNotTranslated)} ${t(value?.isUnit)}` : t("N/A");
    else return value?.value ? getTranslatedValues(value?.value, value?.isNotTranslated) : t("N/A");
  };

  const getClickInfoDetails = () => {
    if (window.location.href.includes("disconnection") || window.location.href.includes("application")) {
      return "WS_DISCONNECTION_CLICK_ON_INFO_LABEL";
    } else {
      return "WS_CLICK_ON_INFO_LABEL";
    }
  };

  const [showAllTimeline, setShowAllTimeline] = useState(false);
  const getClickInfoDetails1 = () => {
    if (window.location.href.includes("disconnection") || window.location.href.includes("application")) {
      return "WS_DISCONNECTION_CLICK_ON_INFO1_LABEL";
    } else {
      return "";
    }
  };
  const toggleTimeline = () => {
    setShowAllTimeline((prev) => !prev);
  };

  // File open service
  const openFilePDF = (fileId) => {
    Digit.UploadServices.Filefetch([fileId], Digit.ULBService.getStateId())
      .then((res) => {
        const concatenatedUrls = res?.data?.fileStoreIds?.[0]?.url;
        if (concatenatedUrls) {
          const urlArray = concatenatedUrls.split(",");
          const fileUrl = urlArray[0];
          if (fileUrl) {
            window.open(fileUrl, "_blank");
          } else {
            console.error("No valid URL found to open!");
          }
        } else {
          console.error("URL is missing in the response!");
        }
      })
      .catch((error) => {
        console.error("Error fetching file:", error);
      });
  };

  // Copy Application Number to Clipboard
  const handleCopyAppNo = (textToCopy) => {
    if (!textToCopy) return;
    if (navigator?.clipboard?.writeText) {
      navigator.clipboard.writeText(textToCopy);
    } else {
      const textarea = document.createElement("textarea");
      textarea.value = textToCopy;
      document.body.appendChild(textarea);
      textarea.select();
      document.execCommand("copy");
      document.body.removeChild(textarea);
    }
    setCopiedAppNo(true);
    setTimeout(() => setCopiedAppNo(false), 2000);
  };

  // Helper to determine status badge styling
  const getStatusBadgeClass = (statusStr) => {
    if (!statusStr) return "status-badge-neutral";
    const upper = String(statusStr).toUpperCase();

    if (
      upper.includes("APPROV") ||
      upper.includes("ACTIVE") ||
      upper.includes("COMPLETE") ||
      upper.includes("PAID") ||
      upper.includes("VALIDAT") ||
      upper.includes("SANCTION") ||
      upper.includes("RESOLV") ||
      upper.includes("ENABLE") ||
      upper.includes("SUCCESS") ||
      upper.includes("GRANT") ||
      upper.includes("AUTHORIZ")
    ) {
      return "status-badge-success";
    }

    if (
      upper.includes("IN_PROGRESS") ||
      upper.includes("INPROGRESS") ||
      upper.includes("PAYMENT") ||
      upper.includes("VERIF") ||
      upper.includes("INSPECT") ||
      upper.includes("FORWARD") ||
      upper.includes("PENDINGAPPROVAL") ||
      upper.includes("ARCHITECT")
    ) {
      return "status-badge-info";
    }

    if (
      upper.includes("INITIAT") ||
      upper.includes("PENDING") ||
      upper.includes("SUBMIT") ||
      upper.includes("SENDBACK") ||
      upper.includes("REOPEN") ||
      upper.includes("DRAFT") ||
      upper.includes("MODIFY")
    ) {
      return "status-badge-warning";
    }

    if (
      upper.includes("REJECT") ||
      upper.includes("CANCEL") ||
      upper.includes("EXPIRE") ||
      upper.includes("DISALLOW") ||
      upper.includes("CLOSE") ||
      upper.includes("TERMINAT") ||
      upper.includes("DEACTIVAT") ||
      upper.includes("INVALID")
    ) {
      return "status-badge-danger";
    }

    return "status-badge-neutral";
  };

  // Compute Primary Application Number & Dynamic Status
  const primaryAppNumber =
    applicationDetails?.applicationNo ||
    applicationData?.applicationNumber ||
    applicationDetails?.applicationData?.applicationNumber ||
    applicationDetails?.applicationData?.acknowldgementNumber ||
    id ||
    applicationNo;

  const rawStatus =
    workflowDetails?.data?.timeline?.[0]?.state ||
    applicationData?.status ||
    applicationDetails?.status ||
    workflowDetails?.data?.processInstances?.[0]?.state?.state ||
    workflowDetails?.data?.state ||
    "";

  const statusPrefixToUse = timelineStatusPrefix || (businessService ? `${businessService.toUpperCase()}_COMMON_STATUS_` : "");
  const formattedStatus = rawStatus ? t(`${statusPrefixToUse}${rawStatus}`) : null;
  const isEmployee = Digit.SessionStorage.get("user_type") === "employee";
  const createdDate = convertEpochToDateDMY(applicationData?.auditDetails?.createdTime);
  const applicationChannel = applicationData?.channel
    ? t("ES_APPLICATION_DETAILS_APPLICATION_CHANNEL_" + applicationData?.channel?.toUpperCase())
    : null;

  return (
    <Card className={"employeeCard-override tmpl-application-details-content-card"}>
      {/* For UM-4418 changes */}
      {isInfoLabel ? (
        <InfoDetails
          t={t}
          userType={false}
          infoBannerLabel={"CS_FILE_APPLICATION_INFO_LABEL"}
          infoClickLable={"WS_CLICK_ON_LABEL"}
          infoClickInfoLabel={getClickInfoDetails()}
          infoClickInfoLabel1={getClickInfoDetails1()}
        />
      ) : null}

      {/* 1. Header Hero / Summary Card */}
      {primaryAppNumber ? (
        <div className="app-details-header-card">
          <div className="app-details-header-top-bar">
            <div className="app-details-service-tag">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                <polyline points="14 2 14 8 20 8"></polyline>
              </svg>
              <span>{businessService ? t(`MODULE_${businessService?.toUpperCase()}`) : t("APPLICATION_DETAILS")}</span>
            </div>

            <div className="app-details-header-quick-links">
              {showTimeLine && workflowDetails?.data?.timeline?.length > 0 && (
                <a
                  href="#timeline"
                  className="app-details-quick-link-btn"
                  onClick={(e) => {
                    e.preventDefault();
                    document.getElementById("timeline")?.scrollIntoView({ behavior: "smooth" });
                  }}
                >
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <circle cx="12" cy="12" r="10"></circle>
                    <polyline points="12 6 12 12 16 14"></polyline>
                  </svg>
                  {t("VIEW_TIMELINE") || "Timeline"}
                </a>
              )}
            </div>
          </div>

          <div className="app-details-header-main">
            <div className="app-details-header-left">
              <span className="app-details-header-label">{t("APPLICATION_NUMBER") || "Application Number"}</span>
              <div className="app-details-header-app-no-wrap">
                <span className="app-details-header-app-no">{primaryAppNumber}</span>
                <button
                  type="button"
                  className={`app-details-copy-btn ${copiedAppNo ? "copied" : ""}`}
                  onClick={() => handleCopyAppNo(primaryAppNumber)}
                  title={t("COPY_TO_CLIPBOARD") || "Copy to clipboard"}
                >
                  {copiedAppNo ? (
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                      <polyline points="20 6 9 17 4 12"></polyline>
                    </svg>
                  ) : (
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
                      <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
                    </svg>
                  )}
                  {copiedAppNo && <span className="app-details-copy-tooltip">{t("COPIED") || "Copied!"}</span>}
                </button>
              </div>

              <div className="app-details-header-meta">
                {createdDate !== "NA" && (
                  <span>
                    <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                      <line x1="16" y1="2" x2="16" y2="6"></line>
                      <line x1="8" y1="2" x2="8" y2="6"></line>
                      <line x1="3" y1="10" x2="21" y2="10"></line>
                    </svg>
                    {t("SUBMITTED_ON") || "Submitted"}: {createdDate}
                  </span>
                )}
                {applicationChannel && (
                  <span>
                    <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                    </svg>
                    {applicationChannel}
                  </span>
                )}
              </div>
            </div>

            {formattedStatus && (
              <div className={`app-details-status-badge ${getStatusBadgeClass(rawStatus)}`}>
                <span className="app-details-status-dot" />
                <span>{formattedStatus}</span>
              </div>
            )}
          </div>
        </div>
      ) : null}

      {/* 2. Structured Section Cards */}
      {applicationDetails?.applicationDetails?.map((detail, index) => (
        <div className="app-details-section-card" key={detail.title || index}>
          {detail?.title ? (
            <div className="app-details-section-header-wrap">
              <div className="app-details-section-title-wrap">
                <span className="app-details-section-accent-bar" />
                <CardSectionHeader className="app-details-section-title">
                  {t(detail.title)}
                </CardSectionHeader>
              </div>
              {detail?.Component ? <detail.Component /> : null}
            </div>
          ) : detail?.Component ? (
            <div className="app-details-section-header-wrap">
              <detail.Component />
            </div>
          ) : null}

          {/* Render Table for adjustment amount details or tabular sections */}
          {detail?.isTable && (
            <div className="app-details-table-wrapper">
              <table className="app-details-modern-table">
                <thead>
                  <tr>
                    {detail?.headers?.map((header, hIdx) => (
                      <th key={hIdx}>{t(header)}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {detail?.tableRows?.map((row, rIdx) => (
                    <tr key={rIdx}>
                      {row.map((element, idx) =>
                        Array.isArray(element) && element.length > 1 && detail.isMaintenance === true ? (
                          <td key={idx}>
                            <div className="tmpl-application-details-content-flex-container">
                              {element.map((file, fileIndex) => (
                                <a
                                  key={fileIndex}
                                  onClick={() => openFilePDF(file.fileStoreId)}
                                  rel="noopener noreferrer"
                                  className="tmpl-application-details-content-clickable"
                                >
                                  <PDFSvg className="tmpl-application-details-content-icon" />
                                </a>
                              ))}
                            </div>
                          </td>
                        ) : (
                          <td key={idx}>
                            {element && element.editButton === true ? (
                              <span className="tmpl-application-details-content-wrapper">
                                <Link
                                  to={{
                                    pathname: `/upyog-ui/employee/asset/assetservice/maintenance-edit/${applicationNo}`,
                                    state: { data: element.data },
                                  }}
                                >
                                  <button type="button">
                                    <EditIcon />
                                  </button>
                                </Link>
                              </span>
                            ) : (
                              t(element)
                            )}
                          </td>
                        )
                      )}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          {/* Render Key-Value Grid */}
          {detail?.values && detail?.values.length > 0 && (
            <div className="app-details-kv-grid">
              {detail?.values?.map((value, vIdx) => {
                // Digipin special row
                if (value?.showDigipin) {
                  return (
                    <div className="app-details-kv-cell full-width" key={t(value.title) || vIdx}>
                      <p className="app-details-kv-label">{t(value.title)}</p>
                      <div className="application-details-digipin-wrapper">
                        <GeoLocationWithDigipin
                          t={t}
                          viewOnly
                          digipin={value.digipin}
                          showMapLink={value.showMapLink}
                          inputStyle={{ marginTop: 0, width: isEmployee ? "50%" : "100%" }}
                        />
                        {value.digipinMapPopup && value.digipinLat && value.digipinLng && value.digipin && (
                          <button
                            type="button"
                            className="application-details-map-btn"
                            onClick={() => handleOpenDigipinMap(value.digipinLat, value.digipinLng, value.digipin)}
                          >
                            {t("CS_VIEW_ON_MAP")}
                          </button>
                        )}
                      </div>
                    </div>
                  );
                }

                // View On Map special row
                if (value?.isViewOnMap) {
                  return (
                    <div className="app-details-kv-cell" key={t(value.title) || vIdx}>
                      <p className="app-details-kv-label">{t(value.title)}</p>
                      <div className="tmpl-application-details-content-flex-row">
                        <span className="app-details-kv-value">{getTextValue(value)}</span>
                        {isAssetModule && (
                          applicationDetailsofAsset?.applicationData?.applicationData?.additionalDetails?.geometry ? (
                            <button
                              type="button"
                              className="application-details-map-btn"
                              onClick={() =>
                                handleOpenMap(applicationDetailsofAsset?.applicationData?.applicationData?.additionalDetails?.geometry)
                              }
                            >
                              {t("View on Map")}
                            </button>
                          ) : (
                            <button
                              type="button"
                              className="application-details-map-btn"
                              onClick={() => setShowMap(true)}
                            >
                              {t("Mark on Map")}
                            </button>
                          )
                        )}
                      </div>
                    </div>
                  );
                }

                // Map Image preview row
                if (value.map === true && value.value !== "N/A") {
                  return (
                    <div className="app-details-kv-cell" key={t(value.title) || vIdx}>
                      <p className="app-details-kv-label">{t(value.title)}</p>
                      <div className="app-details-kv-value">
                        <img src={t(value.value)} alt="" privacy={value?.privacy} />
                      </div>
                    </div>
                  );
                }

                // Link row
                if (value?.isLink === true) {
                  return (
                    <div className="app-details-kv-cell" key={t(value.title) || vIdx}>
                      <p className="app-details-kv-label">{t(value.title)}</p>
                      <div className="app-details-kv-value">
                        <Link to={value?.to}>
                          <span>{value?.value || t(value?.title)}</span>
                        </Link>
                      </div>
                    </div>
                  );
                }

                // Modify Application comparison flow
                if (window.location.href.includes("modify")) {
                  return (
                    <div className="app-details-kv-cell" key={t(value.title) || vIdx}>
                      <p className="app-details-kv-label">{t(value.title)}</p>
                      <div className="app-details-kv-value">
                        <Row
                          className="border-none"
                          key={`${value.title}`}
                          label=""
                          privacy={value?.privacy}
                          text={value?.oldValue ? value?.oldValue : value?.value ? value?.value : ""}
                        />
                      </div>
                    </div>
                  );
                }

                // Standard Key-Value Item
                return (
                  <div className="app-details-kv-cell" key={t(value.title) || vIdx}>
                    <p className="app-details-kv-label">{t(value.title)}</p>
                    <div className={`app-details-kv-value ${value.isBold ? "bold-italic" : ""} ${getTextValue(value) === t("N/A") || getTextValue(value) === "NA" ? "muted" : ""}`}>
                      {value?.privacy ? (
                        <Row
                          key={t(value.title)}
                          label=""
                          text={getTextValue(value)}
                          className="border-none"
                          privacy={value?.privacy}
                        />
                      ) : (
                        getTextValue(value)
                      )}
                      {value.caption && <div className="app-details-kv-caption">{value.caption}</div>}
                    </div>
                    {value.title === "PT_TOTAL_DUES" ? <ArrearSummary bill={fetchBillData.Bill?.[0]} /> : null}
                  </div>
                );
              })}
            </div>
          )}

          {/* Module-Specific Subcomponents Rendered Inside Section Container */}
          {detail?.belowComponent && <detail.belowComponent />}
          {detail?.additionalDetails?.inspectionReport && (
            <ScruntinyDetails scrutinyDetails={detail?.additionalDetails} paymentsList={paymentsList} />
          )}
          {applicationDetails?.applicationData?.additionalDetails?.fieldinspection_pending?.length > 0 && detail?.additionalDetails?.fiReport && (
            <InspectionReport fiReport={applicationDetails?.applicationData?.additionalDetails?.fieldinspection_pending} />
          )}
          {detail?.additionalDetails?.floors && <PropertyFloors floors={detail?.additionalDetails?.floors} />}
          {detail?.additionalDetails?.owners && <PropertyOwners owners={detail?.additionalDetails?.owners} />}
          {detail?.additionalDetails?.units && <TLTradeUnits units={detail?.additionalDetails?.units} />}
          {detail?.additionalDetails?.accessories && <TLTradeAccessories units={detail?.additionalDetails?.accessories} />}
          {detail?.additionalDetails?.permissions && workflowDetails?.data?.nextActions?.length > 0 && (
            <PermissionCheck applicationData={applicationDetails?.applicationData} t={t} permissions={detail?.additionalDetails?.permissions} />
          )}
          {detail?.additionalDetails?.obpsDocuments && (
            <BPADocuments
              t={t}
              applicationData={applicationDetails?.applicationData}
              docs={detail.additionalDetails.obpsDocuments}
              bpaActionsDetails={workflowDetails}
            />
          )}
          {detail?.additionalDetails?.noc && (
            <NOCDocuments
              t={t}
              isNoc={true}
              NOCdata={detail.values}
              applicationData={applicationDetails?.applicationData}
              docs={detail.additionalDetails.noc}
              noc={detail.additionalDetails?.data}
              bpaActionsDetails={workflowDetails}
            />
          )}
          {detail?.additionalDetails?.scruntinyDetails && <ScruntinyDetails scrutinyDetails={detail?.additionalDetails} />}
          {detail?.additionalDetails?.buildingExtractionDetails && <ScruntinyDetails scrutinyDetails={detail?.additionalDetails} />}
          {detail?.additionalDetails?.subOccupancyTableDetails && (
            <SubOccupancyTable edcrDetails={detail?.additionalDetails} applicationData={applicationDetails?.applicationData} />
          )}
          {detail?.additionalDetails?.documentsWithUrl && <DocumentsPreview documents={detail?.additionalDetails?.documentsWithUrl} />}
          {detail?.additionalDetails?.documents && <PropertyDocuments documents={detail?.additionalDetails?.documents} />}
          {detail?.additionalDetails?.taxHeadEstimatesCalculation && (
            <PropertyEstimates taxHeadEstimatesCalculation={detail?.additionalDetails?.taxHeadEstimatesCalculation} />
          )}
          {detail?.isWaterConnectionDetails && <WSAdditonalDetails wsAdditionalDetails={detail} oldValue={oldValue} />}
          {detail?.additionalDetails?.redirectUrl && (
            <div className="tmpl-application-details-content-container-padding">
              <Link to={detail?.additionalDetails?.redirectUrl?.url}>
                <span className="link tmpl-application-details-content-wrapper-3">
                  {detail?.additionalDetails?.redirectUrl?.title}
                </span>
              </Link>
            </div>
          )}
          {detail?.additionalDetails?.estimationDetails && <WSFeeEstimation wsAdditionalDetails={detail} workflowDetails={workflowDetails} />}
          {detail?.additionalDetails?.estimationDetails && <ViewBreakup wsAdditionalDetails={detail} workflowDetails={workflowDetails} />}
        </div>
      ))}

      {/* 3. Activity Timeline Section */}
      {showTimeLine && workflowDetails?.data?.timeline?.length > 0 && (
        <div id="timeline" className="app-details-timeline-card">
          <div className="app-details-timeline-header-wrap">
            <div className="app-details-section-title-wrap">
              <span className="app-details-section-accent-bar" />
              <CardSectionHeader className="app-details-section-title">
                {t("ES_APPLICATION_DETAILS_APPLICATION_TIMELINE")}
              </CardSectionHeader>
            </div>
            <span className="app-details-timeline-count-badge">
              {workflowDetails?.data?.timeline?.length} {t("EVENTS") || "Events"}
            </span>
          </div>

          {(workflowDetails?.isLoading || isDataLoading) && <Loader />}
          {!workflowDetails?.isLoading && !isDataLoading && (
            <Fragment>
              {workflowDetails?.data?.timeline && workflowDetails?.data?.timeline?.length === 1 ? (
                <CheckPoint
                  isCompleted={true}
                  label={t(`${timelineStatusPrefix || ""}${workflowDetails?.data?.timeline[0]?.state}`)}
                  customChild={getTimelineCaptions(workflowDetails?.data?.timeline[0], workflowDetails?.data?.timeline)}
                />
              ) : (
                <ConnectingCheckPoints>
                  {workflowDetails?.data?.timeline &&
                    workflowDetails?.data?.timeline
                      .slice(0, showAllTimeline ? workflowDetails?.data.timeline.length : 2)
                      .map((checkpoint, index, arr) => {
                        let timelineStatusPostfix = "";
                        if (window.location.href.includes("/obps/")) {
                          if (
                            workflowDetails?.data?.timeline[index - 1]?.state?.includes("BACK_FROM") ||
                            workflowDetails?.data?.timeline[index - 1]?.state?.includes("SEND_TO_CITIZEN")
                          )
                            timelineStatusPostfix = `_NOT_DONE`;
                          else if (checkpoint?.performedAction === "SEND_TO_ARCHITECT") timelineStatusPostfix = `_BY_ARCHITECT_DONE`;
                          else timelineStatusPostfix = index == 0 ? "" : `_DONE`;
                        }

                        return (
                          <React.Fragment key={checkpoint.id || checkpoint.performedAction || index}>
                            <CheckPoint
                              keyValue={index}
                              isCompleted={index === 0}
                              info={checkpoint.comment}
                              label={t(
                                `${timelineStatusPrefix || ""}${
                                  checkpoint?.performedAction === "REOPEN" ? checkpoint?.performedAction : checkpoint?.[statusAttribute]
                                }${timelineStatusPostfix}`
                              )}
                              customChild={getTimelineCaptions(checkpoint, index, workflowDetails?.data?.timeline)}
                            />
                          </React.Fragment>
                        );
                      })}
                </ConnectingCheckPoints>
              )}
              {workflowDetails?.data?.timeline?.length > 2 && (
                <button type="button" className="app-details-timeline-toggle-btn" onClick={toggleTimeline}>
                  {showAllTimeline ? t("COLLAPSE") : t("VIEW_TIMELINE")}
                </button>
              )}
            </Fragment>
          )}

          {isAssetModule && showMapModal && (
            <ViewOnMap
              closeModal={handleCloseMap}
              location={selectedLocation}
              assetDetails={applicationDetailsofAsset?.applicationData?.applicationData}
            />
          )}
          {isAssetModule && showMap && (
            <MarkOnMap
              onGeometrySave={(geoJson) => {
                setGeometry(geoJson);
              }}
              onAreaSave={(polygonArea) => {
                setArea(polygonArea);
              }}
              closeModal={() => setShowMap(false)}
              location={coordinateFormatter(applicationDetailsofAsset?.applicationData?.applicationData?.location)}
            />
          )}
          {/* Render the Leaflet map modal when a digipin row's "View on Map" button is clicked */}
          {digipinMapData && (
            <DiginpinMapPopup
              lat={digipinMapData.lat}
              lng={digipinMapData.lng}
              digipin={digipinMapData.digipin}
              onClose={handleCloseDigipinMap}
            />
          )}
        </div>
      )}
    </Card>
  );
}

export default ApplicationDetailsContent;

