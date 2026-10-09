import React from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import { KeyNote } from "@nudmcdgnpm/digit-ui-react-components";
import { getAddress } from "../../../utils/index";
import _ from "lodash";

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

const WSConnection = ({ application }) => {
  const { t } = useTranslation();
  let encodeApplicationNo = encodeURIComponent(application?.applicationNo || "");
  const statusClass = getStatusClass(application?.status || "");

  return (
    <div className="ws-citizen-card">
      <div>
        <div className="ws-card-header">
          <div className="ws-card-id-wrap">
            <span className="ws-card-id-label">{t("WS_MYCONNECTIONS_CONSUMER_NO")}</span>
            <span className="ws-card-id-value">{application?.connectionNo || application?.applicationNo || "N/A"}</span>
          </div>
          <span className={`ws-status-badge ${statusClass}`}>{t(application?.status || "NA")}</span>
        </div>

        <div className="ws-card-body">
          <div className="ws-card-field">
            <span className="ws-field-label">{t("WS_SERVICE_NAME_LABEL")}</span>
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

          <div className="ws-card-field full-width">
            <span className="ws-field-label">{t("WS_MYCONNECTION_ADDRESS")}</span>
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
                    requestParam: { tenantId: application?.tenantId, propertyIds: application?.property?.propertyId },
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
        <Link to={`/upyog-ui/citizen/ws/connection/details/${encodeApplicationNo}`} state={{ ...application }}>
          <button type="button" className="ws-btn-view-details">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
              <circle cx="12" cy="12" r="3" />
            </svg>
            {t("WS_VIEW_DETAILS_LABEL")}
          </button>
        </Link>
      </div>
    </div>
  );
};

export default WSConnection;

