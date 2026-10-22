import {
  Card,
  CardSubHeader,
  Header,
  LinkButton,
  Loader,
  Row,
  StatusTable,
  CardSectionHeader,
  MultiLink,
  CardText,
  CardHeader,
  SubmitBar,
} from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState, useRef } from "react";
import { useTranslation } from "react-i18next";
import { Link, useLocation } from "react-router-dom";
//import PropertyDocument from "../../pageComponents/PropertyDocument";
import WSWFApplicationTimeline from "../../pageComponents/WSWFApplicationTimeline";
import WSDocument from "../../pageComponents/WSDocument";
import getPDFData from "../../utils/getWSAcknowledgementData";
import getDisconnectPDFData from "../../utils/getWSDisconnectionApplicationForm";
import { convertEpochToDateDMY, getFiles } from "../../utils";
import { stringReplaceAll, convertEpochToDate } from "../../utils";
import WSInfoLabel from "../../pageComponents/WSInfoLabel";
import { getAddress } from "../../utils";
import _ from "lodash";

const WSApplicationDetails = () => {
  const {
    t
  } = useTranslation();
  const menuRef = useRef();
  const user = Digit.UserService.getUser();
  const tenantId = Digit.SessionStorage.get("CITIZEN.COMMON.HOME.CITY")?.code || user?.info?.permanentCity || Digit.ULBService.getCurrentTenantId();
  const stateCode = Digit.ULBService.getStateId();
  const [showOptions, setShowOptions] = useState(false);
  const [viewTimeline, setViewTimeline] = useState(false);
  const applicationNobyData = window.location.href.includes("SW_") ? window.location.href.substring(window.location.href.indexOf("SW_")) : window.location.href.substring(window.location.href.indexOf("WS_"));
  //const { acknowledgementIds } = useParams();

  let filter1 = {
    tenantId: tenantId,
    applicationNumber: applicationNobyData
  };
  const {
    isLoading,
    isError,
    error,
    data
  } = Digit.Hooks.ws.useMyApplicationSearch({
    filters: filter1,
    BusinessService: applicationNobyData?.includes("SW") ? "SW" : "WS"
  }, {
    filters: filter1,
    privacy: Digit.Utils.getPrivacyObject()
  });
  const closeModal = () => {
    setShowOptions(false);
  };
  Digit.Hooks.useClickOutside(menuRef, closeModal, showOptions);

  // const fetchBillParams = { consumerCode: data?.WaterConnection?.[0]?.connectionNo };
  const fetchBillParams = {
    consumerCode: applicationNobyData?.includes("DC") ? data?.WaterConnection?.[0]?.connectionNo || data?.SewerageConnections?.[0]?.connectionNo : data?.WaterConnection?.[0]?.applicationNo || data?.SewerageConnections?.[0]?.applicationNo
  };
  const {
    data: generatePdfKey
  } = Digit.Hooks.useCommonMDMS(tenantId, "common-masters", "ReceiptKey", {
    select: data => data["common-masters"]?.uiCommonPay?.filter(({
      code
    }) => "WS.ONE_TIME_FEE"?.includes(code))[0]?.receiptKey || "consolidatedreceipt"
  });
  const paymentDetails = Digit.Hooks.useFetchBillsForBuissnessService({
    businessService: applicationNobyData?.includes("SW") ? applicationNobyData?.includes("DC") ? "SW" : "SW.ONE_TIME_FEE" : applicationNobyData?.includes("DC") ? "WS" : "WS.ONE_TIME_FEE",
    ...fetchBillParams,
    tenantId: tenantId
  }, {
    enabled: data?.WaterConnection?.[0]?.applicationNo || data?.SewerageConnections?.[0]?.applicationNo ? true : false,
    retry: false
  });
  const {
    isLoading: isPTLoading,
    isError: isPTError,
    error: PTerror,
    data: PTData
  } = Digit.Hooks.pt.usePropertySearch({
    filters: {
      propertyIds: data?.WaterConnection?.[0]?.propertyId
    }
  }, {
    filters: {
      propertyIds: data?.WaterConnection?.[0]?.propertyId
    },
    privacy: Digit.Utils.getPrivacyObject()
  });
  const checkifPrivacyenabled = Digit.Hooks.ws.useToCheckPrivacyEnablement({
    privacy: {
      uuid: applicationNobyData?.includes("WS") ? data?.WaterConnection?.[0]?.connectionHolders?.[0]?.uuid : data?.SewerageConnections?.[0]?.connectionHolders?.[0]?.uuid,
      fieldName: "connectionHoldersMobileNumber",
      model: "WnSConnectionOwner"
    }
  }) || false;
  const isPaid = data?.WaterConnection?.[0]?.applicationStatus === 'CONNECTION_ACTIVATED' || data?.WaterConnection?.[0]?.applicationStatus === 'PENDING_FOR_CONNECTION_ACTIVATION' || data?.SewerageConnections?.[0]?.applicationStatus === 'CONNECTION_ACTIVATED' || data?.SewerageConnections?.[0]?.applicationStatus === 'PENDING_FOR_CONNECTION_ACTIVATION' ? true : false;
  if (isLoading) {
    return <Loader />;
  }
  const handleDownloadPdf = async () => {
    const tenantInfo = data?.WaterConnection?.[0]?.tenantId || data?.SewerageConnections?.[0]?.tenantId;
    let res = data?.WaterConnection?.[0] || data?.SewerageConnections?.[0];
    const PDFdata = getPDFData({
      ...res
    }, {
      ...PTData?.Properties?.[0]
    }, tenantInfo, t);
    PDFdata.then(ress => Digit.Utils.pdf.generatev1(ress));
    setShowOptions(false);
  };
  const handleDownloadDisconnectPdf = async () => {
    const tenantInfo = data?.WaterConnection?.[0]?.tenantId || data?.SewerageConnections?.[0]?.tenantId;
    let res = data?.WaterConnection?.[0] || data?.SewerageConnections?.[0];
    const PDFdata = getDisconnectPDFData({
      ...res
    }, {
      ...PTData?.Properties?.[0]
    }, tenantInfo, t);
    PDFdata.then(ress => Digit.Utils.pdf.generatev1(ress));
    setShowOptions(false);
  };
  async function getDisconnectionNoticeSearch() {
    let key = "ws-waterdisconnectionnotice";
    let details = {};
    if (applicationNobyData?.includes("WS")) {
      details = {
        WaterConnection: [{
          ...data?.WaterConnection?.[0],
          property: PTData?.Properties?.[0]
        }]
      };
    } else {
      key = "ws-seweragedisconnectionnotice";
      details = {
        SewerageConnection: [{
          ...data?.SewerageConnections?.[0],
          property: PTData?.Properties?.[0]
        }]
      };
    }
    let response = await Digit.WSService.WSDisconnectionNotice(data?.WaterConnection?.[0]?.tenantId || data?.SewerageConnections?.[0]?.tenantId, details, key);
    const fileStore = await Digit.PaymentService.printReciept(data?.WaterConnection?.[0]?.tenantId || data?.SewerageConnections?.[0]?.tenantId, {
      fileStoreIds: response.filestoreIds[0]
    });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  }
  const handleViewTimeline = () => {
    const timelineSection = document.getElementById('timeline');
    if (timelineSection) {
      timelineSection.scrollIntoView({
        behavior: 'smooth'
      });
    }
    setViewTimeline(true);
  };
  const printApplicationReceipts = async () => {
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const state = Digit.ULBService.getStateId();
    let key = data?.WaterConnection?.[0] ? "WS.ONE_TIME_FEE" : "SW.ONE_TIME_FEE";
    const payments = await Digit.PaymentService.getReciept(tenantId, key, {
      consumerCodes: data?.WaterConnection?.[0] ? data?.WaterConnection?.[0]?.applicationNo : data?.SewerageConnections?.[0]?.applicationNo
    });
    let response = {
      filestoreIds: [payments.Payments[0]?.fileStoreId]
    };
    if (!payments.Payments[0]?.fileStoreId) {
      response = await Digit.PaymentService.generatePdf(state, {
        Payments: payments.Payments
      }, generatePdfKey);
    }
    const fileStore = await Digit.PaymentService.printReciept(state, {
      fileStoreIds: response.filestoreIds[0]
    });
    window.open(fileStore[response.filestoreIds[0]], "_blank");
  };

  // const { WaterConnection: applicationsList } = data || {};
  let downloadOptions = [];

  // downloadOptions.push({
  //   label: t("CS_COMMON_APPLICATION_ACKNOWLEDGEMENT"),
  //   onClick: handleDownloadPdf,
  // })

  const wsEstimateDownloadObject = {
    order: 1,
    label: t("WS_ESTIMATION_NOTICE"),
    onClick: () => data?.WaterConnection?.[0] ? getFiles([data?.WaterConnection?.[0]?.additionalDetails?.estimationFileStoreId], tenantId) : getFiles([data?.SewerageConnections?.[0]?.additionalDetails?.estimationFileStoreId], tenantId)
  };
  const sanctionDownloadObject = {
    order: 2,
    label: t("WS_SANCTION_LETTER"),
    onClick: () => data?.WaterConnection?.[0] ? getFiles([data?.WaterConnection?.[0]?.additionalDetails?.sanctionFileStoreId], tenantId) : getFiles([data?.SewerageConnections?.[0]?.additionalDetails?.sanctionFileStoreId], tenantId)
  };
  const applicationDownloadObject = {
    order: 3,
    label: t("WS_APPLICATION"),
    onClick: handleDownloadPdf
  };
  const receiptApplicationFeeDownloadObject = {
    order: 4,
    label: t("DOWNLOAD_RECEIPT_HEADER"),
    onClick: printApplicationReceipts
  };
  const disconnectionNoticeNApplicationFormOptions = [{
    order: 1,
    label: t("WS_APPLICATION"),
    onClick: handleDownloadDisconnectPdf
  }, {
    order: 2,
    label: t("WS_DISCONNECTION_NOTICE_LABEL"),
    onClick: () => getDisconnectionNoticeSearch()
  }];
  const isDisconnection = data?.WaterConnection?.[0].applicationType?.includes("DISCONNECT") || data?.SewerageConnections?.[0].applicationType?.includes("DISCONNECT");
  const appStatus = data?.WaterConnection?.[0]?.applicationStatus || data?.SewerageConnections?.[0]?.applicationStatus;
  switch (appStatus) {
    case "PENDING_FOR_DOCUMENT_VERIFICATION":
      if (data?.WaterConnection?.[0].applicationType?.includes("DISCONNECT") || data?.SewerageConnections?.[0].applicationType?.includes("DISCONNECT")) {
        downloadOptions = disconnectionNoticeNApplicationFormOptions
      }
      else {
        downloadOptions = downloadOptions?.concat(applicationDownloadObject);
      }
      break;
    case "PENDING_FOR_CITIZEN_ACTION":
    case "PENDING_FOR_FIELD_INSPECTION":
      downloadOptions = downloadOptions?.concat(applicationDownloadObject);
      // dowloadOptions = [applicationDownloadObject];
      break;
    case "PENDING_APPROVAL_FOR_CONNECTION":
    case "PENDING_FOR_PAYMENT":
      downloadOptions = downloadOptions?.concat(applicationDownloadObject, wsEstimateDownloadObject);
      break;
    case "PENDING_FOR_CONNECTION_ACTIVATION":
    case "CONNECTION_ACTIVATED":
      downloadOptions = downloadOptions?.concat(
        sanctionDownloadObject,
        wsEstimateDownloadObject,
        applicationDownloadObject,
        receiptApplicationFeeDownloadObject
      );
      break;
    case "REJECTED":
      downloadOptions = downloadOptions?.concat(applicationDownloadObject);
      break;
    case "PENDING_FOR_DISCONNECTION_EXECUTION":
    case "DISCONNECTION_EXECUTED":
      if (data?.WaterConnection?.[0].applicationType?.includes("DISCONNECT") || data?.SewerageConnections?.[0].applicationType?.includes("DISCONNECT")) {
        downloadOptions = disconnectionNoticeNApplicationFormOptions
      }
      else {
        downloadOptions = downloadOptions?.concat(applicationDownloadObject);
      }
      break;
    default:
      downloadOptions = downloadOptions?.concat(applicationDownloadObject);
      break;
  }
  downloadOptions.sort(function (a, b) {
    return a.order - b.order;
  });
  let serviceType = data && data?.WaterConnection?.[0] ? "WATER" : "SEWERAGE";
  //const application = data?.Properties[0];
  sessionStorage.setItem("ApplicationNoState", applicationNobyData);

  const getStatusClass = (status = "") => {
    const s = (status || "").toUpperCase();
    if (s.includes("ACTIVE") || s.includes("CONNECTED") || s.includes("APPROVED") || s.includes("PAID")) {
      return "status-active";
    }
    if (s.includes("PENDING") || s.includes("INITIATED") || s.includes("SUBMITTED") || s.includes("PROGRESS") || s.includes("INSPECTION") || s.includes("APPROVAL")) {
      return "status-pending";
    }
    if (s.includes("REJECT") || s.includes("DISCONNECT") || s.includes("INACTIVE")) {
      return "status-rejected";
    }
    return "status-default";
  };

  return (
    <div className="ws-details-page-container">
      <div className="ws-details-header-wrap">
        <div className="ws-details-title-block">
          <h1 className="ws-details-page-title">{t("WS_APPLICATION_DETAILS_HEADER")}</h1>
        </div>
        <div className="ws-details-actions">
          {downloadOptions && downloadOptions.length > 0 && (
            <div ref={menuRef}>
              <MultiLink
                className="multilinkWrapper"
                onHeadClick={() => setShowOptions(!showOptions)}
                displayOptions={showOptions}
                options={downloadOptions}
              />
            </div>
          )}
          <button type="button" className="ws-btn-timeline" onClick={handleViewTimeline}>
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <circle cx="12" cy="12" r="10" />
              <polyline points="12 6 12 12 16 14" />
            </svg>
            {t("VIEW_TIMELINE")}
          </button>
        </div>
      </div>
      {checkifPrivacyenabled && <WSInfoLabel t={t} />}
      <div className="hide-seperator">
        {/* 1. Hero Overview Card */}
        <div className="ws-app-hero-card">
          <div className="ws-app-hero-header">
            <div className="ws-app-id-section">
              <div className="ws-app-id-label">{t("WS_MYCONNECTIONS_APPLICATION_NO")}</div>
              <div className="ws-app-id-value">
                {data?.WaterConnection?.[0]?.applicationNo || data?.SewerageConnections?.[0]?.applicationNo || applicationNobyData}
              </div>
              {(data?.WaterConnection?.[0]?.connectionNo || data?.SewerageConnections?.[0]?.connectionNo) && (
                <div className="ws-app-sub-id">
                  {t("WS_MYCONNECTIONS_CONSUMER_NO")}: {data?.WaterConnection?.[0]?.connectionNo || data?.SewerageConnections?.[0]?.connectionNo}
                </div>
              )}
            </div>
            <div className={`ws-app-status-badge ${getStatusClass(appStatus)}`}>
              {t(`CS_${appStatus}`) || t(appStatus) || t("CS_NA")}
            </div>
          </div>

          <div className="ws-app-summary-grid">
            <div className="ws-app-summary-item">
              <span className="ws-app-summary-label">{t("WS_SERVICE_NAME_LABEL")}</span>
              <span className="ws-app-summary-val">
                {t(`WS_APPLICATION_TYPE_${data?.WaterConnection?.[0]?.applicationType || data?.SewerageConnections?.[0]?.applicationType}`) || t("CS_NA")}
              </span>
            </div>

            <div className="ws-app-summary-item">
              <span className="ws-app-summary-label">{t("WS_COMMON_TABLE_COL_AMT_DUE_LABEL")}</span>
              <span className={`ws-app-summary-val ${paymentDetails?.data?.Bill?.[0]?.billDetails?.[0]?.amount ? "due-val" : ""}`}>
                {paymentDetails?.data?.Bill?.[0]?.billDetails?.[0]?.amount
                  ? "₹ " + Number(paymentDetails?.data?.Bill?.[0]?.billDetails?.[0]?.amount).toFixed(2)
                  : t("₹0")}
              </span>
            </div>

            {(data?.WaterConnection?.[0]?.applicationType?.includes("DISCONNECT") ||
              data?.SewerageConnections?.[0]?.applicationType?.includes("DISCONNECT")) && (
                <>
                  <div className="ws-app-summary-item">
                    <span className="ws-app-summary-label">{t("WS_DISCONNECTION_PROPOSED_DATE")}</span>
                    <span className="ws-app-summary-val">
                      {applicationNobyData?.includes("WS")
                        ? convertEpochToDate(data?.WaterConnection?.[0]?.dateEffectiveFrom)
                        : convertEpochToDate(data?.SewerageConnections?.[0]?.dateEffectiveFrom)}
                    </span>
                  </div>
                  <div className="ws-app-summary-item">
                    <span className="ws-app-summary-label">{t("WS_DISCONNECTION_EXECUTED_DATE")}</span>
                    <span className="ws-app-summary-val">
                      {applicationNobyData?.includes("WS")
                        ? convertEpochToDate(data?.WaterConnection?.[0]?.disconnectionExecutionDate)
                        : convertEpochToDate(data?.SewerageConnections?.[0]?.disconnectionExecutionDate)}
                    </span>
                  </div>
                  <div className="ws-app-summary-item">
                    <span className="ws-app-summary-label">{t("WS_DISCONNECTION_REASON")}</span>
                    <span className="ws-app-summary-val">
                      {data?.WaterConnection?.[0]?.disconnectionReason != null
                        ? data?.WaterConnection?.[0]?.disconnectionReason
                        : data?.SewerageConnections?.[0]?.disconnectionReason != null
                          ? data?.SewerageConnections?.[0]?.disconnectionReason
                          : t("NA")}
                    </span>
                  </div>
                </>
              )}
          </div>
        </div>

        {/* 2. Fee Details Card */}
        {paymentDetails?.data?.Bill?.[0]?.billDetails?.[0]?.billAccountDetails.length > 0 && (
          <div className="ws-details-card">
            <div className="ws-section-header">
              <div className="ws-section-title">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#0284c7" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <rect x="2" y="4" width="20" height="16" rx="2" />
                  <line x1="2" y1="10" x2="22" y2="10" />
                </svg>
                {t("WS_FEE_DEATAILS_HEADER")}
              </div>
            </div>
            <table className="ws-fee-table">
              <tbody>
                {paymentDetails?.data?.Bill?.[0]?.billDetails?.[0]?.billAccountDetails.map((bill, index) => (
                  <tr key={index}>
                    <td className="fee-label">{t(bill?.taxHeadCode)}</td>
                    <td className="fee-val">₹{Number(bill?.amount).toFixed(2)}</td>
                  </tr>
                ))}
                <tr className="total-row">
                  <td className="fee-label">{t("WS_TOTAL_AMOUNT_DUE")}</td>
                  <td className="fee-val">
                    ₹{Number(isPaid ? 0 : paymentDetails?.data?.Bill?.[0]?.billDetails?.[0]?.amount).toFixed(2)}
                  </td>
                </tr>
                <tr>
                  <td className="fee-label">{t("WS_COMMON_TABLE_COL_APPLICATION_STATUS")}</td>
                  <td className="fee-val">
                    <span
                      className={`ws-app-status-badge ${isPaid || Number(paymentDetails?.data?.Bill?.[0]?.billDetails?.[0]?.amount).toFixed(2) === "0.00"
                          ? "status-paid"
                          : "status-rejected"
                        }`}
                    >
                      {isPaid || Number(paymentDetails?.data?.Bill?.[0]?.billDetails?.[0]?.amount).toFixed(2) === "0.00"
                        ? t("WS_COMMON_PAID_LABEL")
                        : t("WS_COMMON_NOT_PAID")}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        )}

        {/* 3. Property Details Card */}
        <div className="ws-details-card">
          <div className="ws-section-header">
            <div className="ws-section-title">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#0284c7" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
                <polyline points="9 22 9 12 15 12 15 22" />
              </svg>
              {t("WS_COMMON_PROPERTY_DETAILS")}
            </div>
          </div>
          <StatusTable>
            <Row
              className="border-none"
              label={t("WS_PROPERTY_ID_LABEL")}
              text={data?.WaterConnection?.[0]?.propertyId || data?.SewerageConnections?.[0]?.propertyId}
              textStyle={{ wordBreak: "break-word" }}
            />
            <Row
              className="border-none"
              label={t("WS_OWN_DETAIL_OWN_NAME_LABEL")}
              text={
                PTData?.Properties?.[0]?.owners.sort(
                  (a, b) => a?.additionalDetails?.ownerSequence - b?.additionalDetails?.ownerSequence
                )?.[0]?.name
              }
              textStyle={{ whiteSpace: "pre" }}
            />
            <Row
              className="border-none"
              label={t("WS_PROPERTY_ADDRESS")}
              text={getAddress(PTData?.Properties?.[0]?.address, t) || t("CS_NA")}
              textStyle={{ wordBreak: "break-word" }}
              privacy={{
                uuid: PTData?.Properties?.[0]?.owners?.[0]?.uuid,
                fieldName: ["doorNo", "street", "landmark"],
                model: "Property",
                hide: !PTData?.Properties?.[0]?.address,
                showValue: true,
                loadData: {
                  serviceName: "/property-services/property/_search",
                  requestBody: {},
                  requestParam: {
                    tenantId: tenantId,
                    propertyIds: data?.WaterConnection?.[0]?.propertyId || data?.SewerageConnections?.[0]?.propertyId,
                  },
                  jsonPath: "Properties[0].address.street",
                  isArray: false,
                  d: (res) => {
                    let resultString =
                      (_.get(res, "Properties[0].address.doorNo") ? `${_.get(res, "Properties[0].address.doorNo")}, ` : "") +
                      (_.get(res, "Properties[0].address.street") ? `${_.get(res, "Properties[0].address.street")}, ` : "") +
                      (_.get(res, "Properties[0].address.landmark") ? `${_.get(res, "Properties[0].address.landmark")}` : "");
                    return resultString;
                  },
                },
              }}
            />
            <Link
              to={`/upyog-ui/citizen/commonpt/view-property?propertyId=${data?.WaterConnection?.[0]?.propertyId || data?.SewerageConnections?.[0]?.propertyId
                }&tenantId=${data?.WaterConnection?.[0]?.tenantId || data?.SewerageConnections?.[0]?.tenantId}`}
            >
              <LinkButton label={t("WS_VIEW_PROPERTY")} className="ws-application-details-btn-2" />
            </Link>
          </StatusTable>
        </div>

        {/* 4. Connection Holder Details Card */}
        {data?.WaterConnection?.[0]?.connectionHolders?.length > 0 || data?.SewerageConnections?.[0]?.connectionHolders?.length > 0 ? (
          <div className="ws-details-card">
            <div className="ws-section-header">
              <div className="ws-section-title">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#0284c7" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                  <circle cx="12" cy="7" r="4" />
                </svg>
                {t("WS_COMMON_CONNECTION_HOLDER_DETAILS_HEADER")}
              </div>
            </div>
            <StatusTable>
              <Row
                className="border-none"
                label={t("WS_OWN_DETAIL_MOBILE_NO_LABEL")}
                text={
                  data?.WaterConnection?.[0]?.connectionHolders?.[0]?.mobileNumber ||
                  data?.SewerageConnections?.[0]?.connectionHolders?.[0]?.mobileNumber
                }
                textStyle={{ whiteSpace: "pre" }}
                privacy={{
                  uuid: applicationNobyData?.includes("WS")
                    ? data?.WaterConnection?.[0]?.connectionHolders?.[0]?.uuid
                    : data?.SewerageConnections?.[0]?.connectionHolders?.[0]?.uuid,
                  fieldName: "connectionHoldersMobileNumber",
                  model: "WnSConnectionOwner",
                  showValue: false,
                  loadData: {
                    serviceName: serviceType === "WATER" ? "/ws-services/wc/_search" : "/sw-services/swc/_search",
                    requestBody: {},
                    requestParam: {
                      tenantId,
                      applicationNumber: applicationNobyData,
                    },
                    jsonPath:
                      serviceType === "WATER"
                        ? "WaterConnection[0].connectionHolders[0].mobileNumber"
                        : "SewerageConnections[0].connectionHolders[0].mobileNumber",
                    isArray: false,
                  },
                }}
              />
              <Row
                className="border-none"
                label={t("WS_OWN_DETAIL_OWN_NAME_LABEL")}
                text={
                  data?.WaterConnection?.[0]?.connectionHolders?.[0]?.name ||
                  data?.SewerageConnections?.[0]?.connectionHolders?.[0]?.name
                }
                textStyle={{ whiteSpace: "pre" }}
              />
              <Row
                className="border-none"
                label={t("WS_OWN_DETAIL_GENDER_LABEL")}
                text={t(
                  data?.WaterConnection?.[0]?.connectionHolders?.[0]?.gender ||
                  data?.SewerageConnections?.[0]?.connectionHolders?.[0]?.gender
                )}
                textStyle={{ whiteSpace: "pre" }}
                privacy={{
                  uuid: applicationNobyData?.includes("WS")
                    ? data?.WaterConnection?.[0]?.connectionHolders?.[0]?.uuid
                    : data?.SewerageConnections?.[0]?.connectionHolders?.[0]?.uuid,
                  fieldName: "gender",
                  model: "WnSConnectionOwner",
                  showValue: false,
                  loadData: {
                    serviceName: serviceType === "WATER" ? "/ws-services/wc/_search" : "/sw-services/swc/_search",
                    requestBody: {},
                    requestParam: {
                      tenantId,
                      applicationNumber: applicationNobyData,
                    },
                    jsonPath:
                      serviceType === "WATER"
                        ? "WaterConnection[0].connectionHolders[0].gender"
                        : "SewerageConnections[0].connectionHolders[0].gender",
                    isArray: false,
                  },
                }}
              />
              <Row
                className="border-none"
                label={t("WS_OWN_DETAIL_FATHER_OR_HUSBAND_NAME")}
                text={
                  data?.WaterConnection?.[0]?.connectionHolders?.[0]?.fatherOrHusbandName ||
                  data?.SewerageConnections?.[0]?.connectionHolders?.[0]?.fatherOrHusbandName ||
                  t("CS_NA")
                }
                textStyle={{ whiteSpace: "pre" }}
                privacy={{
                  uuid: applicationNobyData?.includes("WS")
                    ? data?.WaterConnection?.[0]?.connectionHolders?.[0]?.uuid
                    : data?.SewerageConnections?.[0]?.connectionHolders?.[0]?.uuid,
                  fieldName: "fatherOrHusbandName",
                  model: "WnSConnectionOwner",
                  showValue: false,
                  loadData: {
                    serviceName: serviceType === "WATER" ? "/ws-services/wc/_search" : "/sw-services/swc/_search",
                    requestBody: {},
                    requestParam: {
                      tenantId,
                      applicationNumber: applicationNobyData,
                    },
                    jsonPath:
                      serviceType === "WATER"
                        ? "WaterConnection[0].connectionHolders[0].fatherOrHusbandName"
                        : "SewerageConnections[0].connectionHolders[0].fatherOrHusbandName",
                    isArray: false,
                  },
                }}
              />
              <Row
                className="border-none"
                label={t("WS_OWN_DETAIL_RELATION_LABEL")}
                text={
                  data?.WaterConnection?.[0]?.connectionHolders?.[0]?.relationship ||
                  data?.SewerageConnections?.[0]?.connectionHolders?.[0]?.relationship ||
                  t("CS_NA")
                }
                textStyle={{ whiteSpace: "pre" }}
                privacy={{
                  uuid: applicationNobyData?.includes("WS")
                    ? data?.WaterConnection?.[0]?.connectionHolders?.[0]?.uuid
                    : data?.SewerageConnections?.[0]?.connectionHolders?.[0]?.uuid,
                  fieldName: "relationship",
                  model: "WnSConnection",
                  showValue: false,
                  loadData: {
                    serviceName: serviceType === "WATER" ? "/ws-services/wc/_search" : "/sw-services/swc/_search",
                    requestBody: {},
                    requestParam: {
                      tenantId,
                      applicationNumber: applicationNobyData,
                    },
                    jsonPath:
                      serviceType === "WATER"
                        ? "WaterConnection[0].connectionHolders[0].relationship"
                        : "SewerageConnections[0].connectionHolders[0].relationship",
                    isArray: false,
                  },
                }}
              />
              <Row
                className="border-none"
                label={t("WS_OWN_DETAIL_CROSADD")}
                text={
                  data?.WaterConnection?.[0]?.connectionHolders?.[0]?.correspondenceAddress ||
                  data?.SewerageConnections?.[0]?.connectionHolders?.[0]?.correspondenceAddress ||
                  t("CS_NA")
                }
                privacy={{
                  uuid: applicationNobyData?.includes("WS")
                    ? data?.WaterConnection?.[0]?.connectionHolders?.[0]?.uuid
                    : data?.SewerageConnections?.[0]?.connectionHolders?.[0]?.uuid,
                  fieldName: "correspondenceAddress",
                  model: "WnSConnectionOwner",
                  showValue: false,
                  loadData: {
                    serviceName: serviceType === "WATER" ? "/ws-services/wc/_search" : "/sw-services/swc/_search",
                    requestBody: {},
                    requestParam: {
                      tenantId,
                      applicationNumber: applicationNobyData,
                    },
                    jsonPath:
                      serviceType === "WATER"
                        ? "WaterConnection[0].connectionHolders[0].correspondenceAddress"
                        : "SewerageConnections[0].connectionHolders[0].correspondenceAddress",
                    isArray: false,
                  },
                }}
              />
              <Row
                className="border-none"
                label={t("WS_OWN_DETAIL_SPECIAL_APPLICANT_LABEL")}
                text={
                  (
                    applicationNobyData?.includes("WS")
                      ? !data?.WaterConnection?.[0]?.connectionHolders?.[0]?.ownerType?.includes("*")
                      : !data?.SewerageConnections?.[0]?.connectionHolders?.[0]?.ownerType?.includes("*")
                  )
                    ? t(
                      `COMMON_MASTERS_OWNERTYPE_${applicationNobyData?.includes("WS")
                        ? data?.WaterConnection?.[0]?.connectionHolders?.[0]?.ownerType
                        : data?.SewerageConnections?.[0]?.connectionHolders?.[0]?.ownerType
                      }`
                    )
                    : applicationNobyData?.includes("WS")
                      ? data?.WaterConnection?.[0]?.connectionHolders?.[0]?.ownerType
                      : data?.SewerageConnections?.[0]?.connectionHolders?.[0]?.ownerType
                }
                textStyle={{ whiteSpace: "pre" }}
                privacy={{
                  uuid: applicationNobyData?.includes("WS")
                    ? data?.WaterConnection?.[0]?.connectionHolders?.[0]?.uuid
                    : data?.SewerageConnections?.[0]?.connectionHolders?.[0]?.uuid,
                  fieldName: "ownerType",
                  model: "WnSConnection",
                  showValue: false,
                  loadData: {
                    serviceName: serviceType === "WATER" ? "/ws-services/wc/_search" : "/sw-services/swc/_search",
                    requestBody: {},
                    requestParam: {
                      tenantId,
                      applicationNumber: applicationNobyData,
                    },
                    jsonPath:
                      serviceType === "WATER"
                        ? "WaterConnection[0].connectionHolders[0].ownerType"
                        : "SewerageConnections[0].connectionHolders[0].ownerType",
                    isArray: false,
                    d: (res) => {
                      let resultString = res?.WaterConnection?.[0]
                        ? t(`PROPERTYTAX_OWNERTYPE_${res?.WaterConnection?.[0]?.connectionHolders?.[0]?.ownerType?.toUpperCase()}`)
                        : t(`PROPERTYTAX_OWNERTYPE_${res?.SewerageConnections?.[0]?.connectionHolders?.[0]?.ownerType?.toUpperCase()}`);
                      return resultString;
                    },
                  },
                }}
              />
              <Row
                className="border-none"
                label={t("WS_OWN_DETAIL_EMAIL_ID_LABEL")}
                text={
                  data?.WaterConnection?.[0]?.connectionHolders?.[0]?.emailId ||
                  data?.SewerageConnections?.[0]?.connectionHolders?.[0]?.emailId ||
                  "NA"
                }
                textStyle={{ whiteSpace: "pre" }}
                privacy={{
                  uuid: applicationNobyData?.includes("WS")
                    ? data?.WaterConnection?.[0]?.connectionHolders?.[0]?.uuid
                    : data?.SewerageConnections?.[0]?.connectionHolders?.[0]?.uuid,
                  fieldName: "connectionHoldersEmailId",
                  model: "WnSConnectionOwner",
                  showValue: false,
                  loadData: {
                    serviceName: serviceType === "WATER" ? "/ws-services/wc/_search" : "/sw-services/swc/_search",
                    requestBody: {},
                    requestParam: {
                      tenantId,
                      applicationNumber: applicationNobyData,
                    },
                    jsonPath:
                      serviceType === "WATER"
                        ? "WaterConnection[0].connectionHolders[0].emailId"
                        : "SewerageConnections[0].connectionHolders[0].emailId",
                    isArray: false,
                  },
                }}
              />
            </StatusTable>
          </div>
        ) : (
          <div className="ws-details-card">
            <div className="ws-section-header">
              <div className="ws-section-title">{t("WS_COMMON_CONNECTION_HOLDER_DETAILS_HEADER")}</div>
            </div>
            <CardText>{t("WS_SAME_AS_PROPERTY_OWNERS")}</CardText>
          </div>
        )}

        {/* 5. Technical Connection Details Card */}
        {!isDisconnection && (
          <div className="ws-details-card">
            <div className="ws-section-header">
              <div className="ws-section-title">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#0284c7" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <circle cx="12" cy="12" r="10" />
                  <line x1="12" y1="8" x2="12" y2="12" />
                  <line x1="12" y1="16" x2="12.01" y2="16" />
                </svg>
                {t("WS_COMMON_CONNECTION_DETAIL")}
              </div>
            </div>
            {data?.WaterConnection && data?.WaterConnection?.length > 0 && (
              <StatusTable>
                <Row
                  className="border-none"
                  label={t("WS_TASK_DETAILS_CONN_DETAIL_NO_OF_TAPS_PROPOSED")}
                  text={data?.WaterConnection?.[0]?.proposedTaps || t("CS_NA")}
                  textStyle={{ whiteSpace: "pre" }}
                />
                <Row
                  className="border-none"
                  label={t("WS_SERV_DETAIL_PIPE_SIZE")}
                  text={`${data?.WaterConnection?.[0]?.proposedPipeSize} ${t("WS_INCHES_LABEL")}` || t("CS_NA")}
                  textStyle={{ whiteSpace: "pre" }}
                />
                <Link to={`/upyog-ui/citizen/ws/connection/additional/${data?.WaterConnection?.[0]?.applicationNo}`}>
                  <LinkButton label={t("WS_ADDITIONAL_DETAILS")} className="ws-application-details-btn-2" />
                </Link>
              </StatusTable>
            )}
            {data?.SewerageConnections && data?.SewerageConnections?.length > 0 && (
              <StatusTable>
                <Row
                  className="border-none"
                  label={t("WS_NO_OF_WATER_CLOSETS_LABEL")}
                  text={data?.SewerageConnections?.[0]?.proposedWaterClosets}
                  textStyle={{ whiteSpace: "pre" }}
                />
                <Row
                  className="border-none"
                  label={t("WS_SERV_DETAIL_NO_OF_TOILETS")}
                  text={data?.SewerageConnections?.[0]?.proposedToilets || t("CS_NA")}
                  textStyle={{ whiteSpace: "pre" }}
                />
                <Link to={`/upyog-ui/citizen/ws/connection/additional/${data?.SewerageConnections?.[0]?.applicationNo}`}>
                  <LinkButton label={t("WS_ADDITIONAL_DETAILS")} className="ws-application-details-btn-2" />
                </Link>
              </StatusTable>
            )}
          </div>
        )}

        {/* 6. Document Details Card */}
        {((data?.WaterConnection?.[0]?.documents && data?.WaterConnection?.[0]?.documents.length > 0) ||
          (data?.SewerageConnections?.[0]?.documents && data?.SewerageConnections?.[0]?.documents.length > 0)) && (
            <div className="ws-details-card">
              <div className="ws-section-header">
                <div className="ws-section-title">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#0284c7" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
                    <polyline points="14 2 14 8 20 8" />
                    <line x1="16" y1="13" x2="8" y2="13" />
                    <line x1="16" y1="17" x2="8" y2="17" />
                    <polyline points="10 9 9 9 8 9" />
                  </svg>
                  {t("WS_COMMON_DOCUMENT_DETAILS")}
                </div>
              </div>
              <div className="ws-documents-grid">
                {data?.WaterConnection?.[0]?.documents &&
                  data?.WaterConnection?.[0]?.documents.map((doc, index) => (
                    <div key={`doc-${index}`} className="ws-doc-card">
                      <div className="ws-doc-header">
                        <div className="ws-doc-icon-wrap">
                          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
                            <polyline points="14 2 14 8 20 8" />
                            <line x1="16" y1="13" x2="8" y2="13" />
                            <line x1="16" y1="17" x2="8" y2="17" />
                          </svg>
                        </div>
                        <div className="ws-doc-info">
                          <span className="ws-doc-type-title">
                            {t(doc?.documentType?.split(".").slice(0, 2).join("_"))}
                          </span>
                          <span className="ws-doc-subtext">{t("WS_ATTACHED_DOCUMENT")}</span>
                        </div>
                      </div>
                      <div className="ws-doc-body">
                        <WSDocument value={data?.WaterConnection?.[0]?.documents} Code={doc?.documentType} index={index} />
                      </div>
                    </div>
                  ))}
                {data?.SewerageConnections?.[0]?.documents &&
                  data?.SewerageConnections?.[0]?.documents.map((doc, index) => (
                    <div key={`doc-${index}`} className="ws-doc-card">
                      <div className="ws-doc-header">
                        <div className="ws-doc-icon-wrap">
                          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
                            <polyline points="14 2 14 8 20 8" />
                            <line x1="16" y1="13" x2="8" y2="13" />
                            <line x1="16" y1="17" x2="8" y2="17" />
                          </svg>
                        </div>
                        <div className="ws-doc-info">
                          <span className="ws-doc-type-title">
                            {t(doc?.documentType?.split(".").slice(0, 2).join("_"))}
                          </span>
                          <span className="ws-doc-subtext">{t("WS_ATTACHED_DOCUMENT")}</span>
                        </div>
                      </div>
                      <div className="ws-doc-body">
                        <WSDocument value={data?.SewerageConnections?.[0]?.documents} Code={doc?.documentType} index={index} />
                      </div>
                    </div>
                  ))}
              </div>
            </div>
          )}

        {/* 7. Timeline Card */}
        <div className="ws-details-card" id="timeline">
          <div className="ws-section-header">
            <div className="ws-section-title">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#0284c7" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <circle cx="12" cy="12" r="10" />
                <polyline points="12 6 12 12 16 14" />
              </svg>
              {t("WS_APPLICATION_TIMELINE") || t("VIEW_TIMELINE")}
            </div>
          </div>
          <WSWFApplicationTimeline
            application={data?.WaterConnection?.[0] || data?.SewerageConnections?.[0]}
            id={data?.WaterConnection?.[0]?.applicationNo || data?.SewerageConnections?.[0]?.applicationNo}
            paymentbuttonenabled={false}
          />
        </div>

        {/* 8. Action Bar */}
        {(data?.WaterConnection?.[0]?.applicationStatus === "PENDING_FOR_PAYMENT" ||
          data?.SewerageConnections?.[0]?.applicationStatus === "PENDING_FOR_PAYMENT" ||
          (!data?.WaterConnection?.[0]?.applicationType.includes("DISCONNECT") &&
            data?.WaterConnection?.[0]?.applicationStatus.includes("PENDING_FOR_CITIZEN_ACTION")) ||
          (!data?.SewerageConnections?.[0]?.applicationType.includes("DISCONNECT") &&
            data?.SewerageConnections?.[0]?.applicationStatus.includes("PENDING_FOR_CITIZEN_ACTION")) ||
          (data?.WaterConnection?.[0]?.applicationType.includes("DISCONNECT") &&
            data?.WaterConnection?.[0]?.applicationStatus.includes("PENDING_FOR_CITIZEN_ACTION")) ||
          (data?.SewerageConnections?.[0]?.applicationType.includes("DISCONNECT") &&
            data?.SewerageConnections?.[0]?.applicationStatus.includes("PENDING_FOR_CITIZEN_ACTION"))) && (
            <div className="ws-action-bar-wrap">
              {(data?.WaterConnection?.[0]?.applicationStatus === "PENDING_FOR_PAYMENT" ||
                data?.SewerageConnections?.[0]?.applicationStatus === "PENDING_FOR_PAYMENT") && (
                  <Link
                    to={`/upyog-ui/citizen/payment/my-bills/${paymentDetails?.data?.Bill?.[0]?.businessService
                      }/${applicationNobyData?.includes("DC")
                        ? stringReplaceAll(data?.WaterConnection?.[0]?.connectionNo, "/", "+") ||
                        stringReplaceAll(data?.SewerageConnections?.[0]?.connectionNo, "/", "+")
                        : stringReplaceAll(data?.WaterConnection?.[0]?.applicationNo, "/", "+") ||
                        stringReplaceAll(data?.SewerageConnections?.[0]?.applicationNo, "/", "+")
                      }?workflow=WNS&tenantId=${data?.WaterConnection?.[0]?.tenantId || data?.SewerageConnections?.[0]?.tenantId
                      }&ConsumerName=${data?.WaterConnection?.[0]?.connectionHolders?.map((owner) => owner.name).join(",") ||
                      data?.SewerageConnections?.[0]?.connectionHolders?.map((owner) => owner.name).join(",") ||
                      PTData?.Properties?.[0]?.owners?.map((owner) => owner.name).join(",")
                      }&isDisoconnectFlow=${applicationNobyData?.includes("DC")}`}
                  >
                    <button type="button" className="ws-submit-action-btn">
                      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                        <rect x="1" y="4" width="22" height="16" rx="2" ry="2" />
                        <line x1="1" y1="10" x2="23" y2="10" />
                      </svg>
                      {t("MAKE_PAYMENT")}
                    </button>
                  </Link>
                )}

              {((!data?.WaterConnection?.[0]?.applicationType.includes("DISCONNECT") &&
                data?.WaterConnection?.[0]?.applicationStatus.includes("PENDING_FOR_CITIZEN_ACTION")) ||
                (!data?.SewerageConnections?.[0]?.applicationType.includes("DISCONNECT") &&
                  data?.SewerageConnections?.[0]?.applicationStatus.includes("PENDING_FOR_CITIZEN_ACTION"))) && (
                  <Link
                    to={{
                      pathname: `/upyog-ui/citizen/ws/edit-application/${data?.WaterConnection?.[0]?.tenantId || data?.SewerageConnections?.[0]?.tenantId
                        }`,
                      state: {
                        id: `${data?.WaterConnection?.[0]?.applicationNo || data?.SewerageConnections?.[0]?.applicationNo}`,
                      },
                    }}
                  >
                    <button type="button" className="ws-secondary-action-btn">
                      {t("COMMON_EDIT")}
                    </button>
                  </Link>
                )}

              {((data?.WaterConnection?.[0]?.applicationType.includes("DISCONNECT") &&
                data?.WaterConnection?.[0]?.applicationStatus.includes("PENDING_FOR_CITIZEN_ACTION")) ||
                (data?.SewerageConnections?.[0]?.applicationType.includes("DISCONNECT") &&
                  data?.SewerageConnections?.[0]?.applicationStatus.includes("PENDING_FOR_CITIZEN_ACTION"))) && (
                  <Link
                    to={{
                      pathname: `/upyog-ui/citizen/ws/resubmit-disconnect-application`,
                      state: {
                        id: `${data?.WaterConnection?.[0]?.applicationNo || data?.SewerageConnections?.[0]?.applicationNo}`,
                      },
                    }}
                  >
                    <button
                      type="button"
                      className="ws-submit-action-btn"
                      onClick={() =>
                        Digit.SessionStorage.set(
                          "WS_DISCONNECTION",
                          applicationNobyData?.includes("SW")
                            ? {
                              applicationData: data?.SewerageConnections?.[0],
                              serviceType: "SEWERAGE",
                              WSDisconnectionForm: {
                                type: data?.SewerageConnections?.[0]?.isDisconnectionTemporary
                                  ? {
                                    code: "type",
                                    value: {
                                      name: "Temporary",
                                      i18nKey: "WS_DISCONNECTIONTYPE_TEMPORARY",
                                      active: true,
                                      code: "Temporary",
                                    },
                                  }
                                  : {
                                    code: "type",
                                    value: {
                                      name: "Permanent",
                                      i18nKey: "WS_DISCONNECTIONTYPE_PERMANENT",
                                      active: true,
                                      code: "Permanent",
                                    },
                                  },
                                date: convertEpochToDateDMY(data?.SewerageConnections?.[0]?.dateEffectiveFrom, true),
                                reason: {
                                  code: "reason",
                                  value: data?.SewerageConnections?.[0]?.disconnectionReason,
                                },
                                documents: data?.SewerageConnections?.[0]?.documents,
                              },
                            }
                            : {
                              applicationData: data?.WaterConnection?.[0],
                              serviceType: "WATER",
                              WSDisconnectionForm: {
                                type: data?.WaterConnection?.[0]?.isDisconnectionTemporary
                                  ? {
                                    code: "type",
                                    value: {
                                      name: "Temporary",
                                      i18nKey: "WS_DISCONNECTIONTYPE_TEMPORARY",
                                      active: true,
                                      code: "Temporary",
                                    },
                                  }
                                  : {
                                    code: "type",
                                    value: {
                                      name: "Permanent",
                                      i18nKey: "WS_DISCONNECTIONTYPE_PERMANENT",
                                      active: true,
                                      code: "Permanent",
                                    },
                                  },
                                date: convertEpochToDateDMY(data?.WaterConnection?.[0]?.dateEffectiveFrom, true),
                                reason: {
                                  code: "reason",
                                  value: data?.WaterConnection?.[0]?.disconnectionReason,
                                },
                                documents: data?.WaterConnection?.[0]?.documents,
                              },
                            }
                        )
                      }
                    >
                      {t("RESUBMIT_APPLICATION")}
                    </button>
                  </Link>
                )}
            </div>
          )}
      </div>
    </div>
  );
};
export default WSApplicationDetails;
