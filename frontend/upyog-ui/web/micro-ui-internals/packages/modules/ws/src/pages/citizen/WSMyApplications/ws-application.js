import { Card, KeyNote, SubmitBar, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import { getAddress } from "../../../utils/index";
import _ from "lodash";
import { stringReplaceAll, convertEpochToDate } from "../../../utils";

const WSApplication = ({
  application
}) => {
  const {
    t
  } = useTranslation();
  let encodeApplicationNo = encodeURI(application.applicationNo);
  // let workflowDetails = Digit.Hooks.useWorkflowDetails({
  //    tenantId: application?.tenantId,
  //    id: application?.applicationNo,
  //    moduleCode: "WS",
  //    config: {
  //      enabled: !!application?.applicationNo
  //    }
  //  });

  let filter1 = {
    tenantId: application.tenantId,
    applicationNumber: application.applicationNo
  };
  const {
    isLoading,
    isError,
    error,
    data
  } = Digit.Hooks.ws.useMyApplicationSearch({
    filters: filter1,
    BusinessService: application.applicationNo?.includes("SW") ? "SW" : "WS"
  }, {
    filters: filter1,
    privacy: Digit.Utils.getPrivacyObject()
  });
  const {
    isLoading: isPTLoading,
    isError: isPTError,
    error: PTerror,
    data: PTData
  } = Digit.Hooks.pt.usePropertySearch({
    filters: {
      propertyIds: application?.propertyId
    }
  }, {
    filters: {
      propertyIds: application?.propertyId
    },
    privacy: Digit.Utils.getPrivacyObject()
  });
  const businessService = application?.applicationNo?.includes("SW") ? application?.applicationNo?.includes("DC") ? "SW" : "SW.ONE_TIME_FEE" : application?.applicationNo?.includes("DC") ? "WS" : "WS.ONE_TIME_FEE";
  const fetchBillParams = {
    consumerCode: application?.applicationNo?.includes("DC") ? application?.connectionNo : application?.connectionNo
  };
  if (isLoading) {
    return <Loader />;
  }

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

  const statusClass = getStatusClass(application?.applicationStatus || "");

  return (
    <div className="ws-citizen-card">
      <div>
        <div className="ws-card-header">
          <div className="ws-card-id-wrap">
            <span className="ws-card-id-label">{t("WS_MYCONNECTIONS_APPLICATION_NO")}</span>
            <span className="ws-card-id-value">{application?.applicationNo || "N/A"}</span>
          </div>
          <span className={`ws-status-badge ${statusClass}`}>
            {t(`CS_${application?.applicationStatus}`) || t(application?.applicationStatus) || t("CS_NA")}
          </span>
        </div>

        <div className="ws-card-body">
          <div className="ws-card-field">
            <span className="ws-field-label">{t("WS_SERVICE_NAME")}</span>
            <span className="ws-field-value">{t(`WS_APPLICATION_TYPE_${application?.applicationType}`) || t("CS_NA")}</span>
          </div>

          <div className="ws-card-field">
            <span className="ws-field-label">{t("WS_CONSUMER_NAME")}</span>
            <span className="ws-field-value">
              {application?.connectionHolders?.map((owner) => owner.name).join(",") ||
                application?.property?.owners?.sort((a, b) => a?.additionalDetails?.ownerSequence - b?.additionalDetails?.ownerSequence)?.map((owner) => owner.name).join(",") ||
                t("CS_NA")}
            </span>
          </div>

          <div className="ws-card-field">
            <span className="ws-field-label">{t("WS_PROPERTY_ID")}</span>
            <span className="ws-field-value">{application?.propertyId || t("CS_NA")}</span>
          </div>

          <div className="ws-card-field">
            <span className="ws-field-label">{t("WS_SLA")}</span>
            <span className="ws-field-value">
              {Math.round(application?.sla / (24 * 60 * 60 * 1000)) ? `${Math.round(application?.sla / (24 * 60 * 60 * 1000))} Days` : t("CS_NA")}
            </span>
          </div>

          <div className="ws-card-field full-width">
            <span className="ws-field-label">{t("WS_PROPERTY_ADDRESS")}</span>
            <div className="ws-field-value">
              <KeyNote
                keyValue=""
                note={getAddress(application?.property?.address, t)}
                privacy={{
                  uuid: application?.property?.owners?.[0]?.uuid,
                  fieldName: ["doorNo", "street", "landmark"],
                  model: "Property",
                  showValue: true,
                  loadData: {
                    serviceName: "/property-services/property/_search",
                    requestBody: {},
                    requestParam: {
                      tenantId: application?.tenantId,
                      propertyIds: application?.propertyId,
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
            </div>
          </div>
        </div>
      </div>

      <div className="ws-card-actions">
        <Link to={`/upyog-ui/citizen/ws/connection/application/${encodeApplicationNo}`}>
          <button type="button" className="ws-btn-view-details">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
              <circle cx="12" cy="12" r="3" />
            </svg>
            {t("WS_VIEW_DETAILS_LABEL")}
          </button>
        </Link>
        {application?.applicationStatus === "PENDING_FOR_PAYMENT" ? (
          <Link
            to={`/upyog-ui/citizen/payment/my-bills/${businessService}/${
              application?.applicationNo?.includes("DC")
                ? stringReplaceAll(application?.connectionNo, "/", "+")
                : stringReplaceAll(application?.applicationNo, "/", "+")
            }?workflow=WNS&tenantId=${application?.tenantId}&ConsumerName=${
              application?.connectionHolders?.map((owner) => owner.name).join(",") ||
              PTData?.Properties?.[0]?.owners?.map((owner) => owner.name).join(",")
            }&isDisoconnectFlow=${application?.applicationNo?.includes("DC")}`}
          >
            <button type="button" className="ws-btn-pay-now">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <rect x="1" y="4" width="22" height="16" rx="2" ry="2" />
                <line x1="1" y1="10" x2="23" y2="10" />
              </svg>
              {t("MAKE_PAYMENT")}
            </button>
          </Link>
        ) : null}
      </div>
    </div>
  );
};
export default WSApplication;

