import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Card, KeyNote, Loader, SubmitBar, Header } from "@nudmcdgnpm/digit-ui-react-components";
import { Fragment } from "react";
import { Link,  } from "react-router-dom";
import { getBPAFormData } from "../../../utils/index";

const getServiceType = () => {
  return `BPA_APPLICATIONTYPE_BUILDING_PLAN_SCRUTINY`;
};

const MyApplication = () => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const [finalData, setFinalData] = useState([]);
  const [labelMessage, setLableMessage] = useState(false);
  const tenantId = Digit.ULBService.getCitizenCurrentTenant();
  // const userInfo = Digit.UserService.getUser();
  // const requestor = userInfo?.info?.mobileNumber;

  const userInfos = sessionStorage.getItem("Digit.citizen.userRequestObject");
  const userInfoData = userInfos ? JSON.parse(userInfos) : {};
  const userInfo = userInfoData?.value;
  const requestor = userInfo?.info?.mobileNumber;


  const { data, isLoading, revalidate } = Digit.Hooks.obps.useBPAREGSearch(tenantId, {}, {mobileNumber: requestor}, {cacheTime : 0});
  const { data: bpaData, isLoading: isBpaSearchLoading, revalidate: bpaRevalidate } = Digit.Hooks.obps.useBPASearch(tenantId, {
    requestor,
    mobileNumber: requestor,
    limit: 50 - (data?.Licenses?.length ? Number(data?.Licenses?.length) : 0),
    offset: 0,
  }, {enabled: !isLoading ? true : false});
  const { isMdmsLoading, data: mdmsData } = Digit.Hooks.obps.useMDMS(Digit.ULBService.getStateId(), "BPA", ["RiskTypeComputation"]);

  const getBPAREGFormData = (data) => {
    let license = data;
    let intermediateData = {
      Correspondenceaddress:
        license?.tradeLicenseDetail?.owners?.[0]?.correspondenceAddress ||
        `${license?.tradeLicenseDetail?.address?.doorNo ? `${license?.tradeLicenseDetail?.address?.doorNo}, ` : ""} ${
          license?.tradeLicenseDetail?.address?.street ? `${license?.tradeLicenseDetail?.address?.street}, ` : ""
        }${license?.tradeLicenseDetail?.address?.landmark ? `${license?.tradeLicenseDetail?.address?.landmark}, ` : ""}${t(
          license?.tradeLicenseDetail?.address?.locality.code
        )}, ${t(license?.tradeLicenseDetail?.address?.city ? license?.tradeLicenseDetail?.address?.city.code : "")},${
          t(license?.tradeLicenseDetail?.address?.pincode) ? `${license.tradeLicenseDetail?.address?.pincode}` : " "
        }`,
      formData: {
        LicneseDetails: {
          PanNumber: license?.tradeLicenseDetail?.owners?.[0]?.pan,
          PermanentAddress: license?.tradeLicenseDetail?.owners?.[0]?.permanentAddress,
          email: license?.tradeLicenseDetail?.owners?.[0]?.emailId,
          gender: {
            code: license?.tradeLicenseDetail?.owners?.[0]?.gender,
            i18nKey: `COMMON_GENDER_${license?.tradeLicenseDetail?.owners?.[0]?.gender}`,
            value: license?.tradeLicenseDetail?.owners?.[0]?.gender,
          },
          mobileNumber: license?.tradeLicenseDetail?.owners?.[0]?.mobileNumber,
          name: license?.tradeLicenseDetail?.owners?.[0]?.name,
        },
        LicneseType: {
          LicenseType: {
            i18nKey: `TRADELICENSE_TRADETYPE_${license?.tradeLicenseDetail?.tradeUnits?.[0]?.tradeType.split(".")[0]}`,
            role: [`BPA_${license?.tradeLicenseDetail?.tradeUnits?.[0]?.tradeType.split(".")[0]}`],
            tradeType: license?.tradeLicenseDetail?.tradeUnits?.[0]?.tradeType,
          },
          ArchitectNo: license?.tradeLicenseDetail?.additionalDetail?.counsilForArchNo || null,
        },
      },
      isAddressSame:
        license?.tradeLicenseDetail?.owners?.[0]?.correspondenceAddress === license?.tradeLicenseDetail?.owners?.[0]?.permanentAddress ? true : false,
      result: {
        Licenses: [{ ...data }],
      },
      initiationFlow: true,
    };

    sessionStorage.setItem("BPAREGintermediateValue", JSON.stringify(intermediateData));
    navigate("/upyog-ui/citizen/obps/stakeholder/apply/stakeholder-docs-required");
  };
  useEffect(() => {
    return () => {
      setFinalData([]);
      revalidate?.();
      bpaRevalidate?.();
    };
  }, []);

  useEffect(() => {
    if (!isLoading && !isBpaSearchLoading) {
      let searchConvertedArray = [];
      let sortConvertedArray = [];
      if (data?.Licenses?.length) {
        data?.Licenses?.forEach((license) => {
          license.sortNumber = 0;
          license.modifiedTime = license.auditDetails.lastModifiedTime;
          license.type = "BPAREG";
          searchConvertedArray.push(license);
        });
      }
      if (bpaData?.length) {
        bpaData?.forEach((bpaDta) => {
          bpaDta.sortNumber = 0;
          bpaDta.modifiedTime = bpaDta.auditDetails.lastModifiedTime;
          bpaDta.type = "BPA";
          searchConvertedArray.push(bpaDta);
        });
      }
      sortConvertedArray = [].slice.call(searchConvertedArray).sort(function (a, b) {
        return new Date(b.modifiedTime) - new Date(a.modifiedTime) || a.sortNumber - b.sortNumber;
      });
      setFinalData(sortConvertedArray);
      let userInfos = sessionStorage.getItem("Digit.citizen.userRequestObject");
      const userInfoDetails = userInfos ? JSON.parse(userInfos) : {};
      if (userInfoDetails?.value?.info?.roles?.length == 1 && userInfoDetails?.value?.info?.roles?.[0]?.code == "CITIZEN") setLableMessage(true);
    }
  }, [isLoading, isBpaSearchLoading, bpaData, data]);

  if (isLoading || isBpaSearchLoading) {
    return <Loader />;
  }

  const getStatusClass = (status = "") => {
    const s = (status || "").toUpperCase();
    if (s.includes("APPROVED") || s.includes("ACTIVE") || s.includes("PAID") || s.includes("COMPLETED")) {
      return "status-approved";
    }
    if (
      s.includes("PENDING") ||
      s.includes("INITIATED") ||
      s.includes("SUBMITTED") ||
      s.includes("PROGRESS") ||
      s.includes("SCRUTINY") ||
      s.includes("SEND_TO_ARCHITECT")
    ) {
      return "status-pending";
    }
    if (s.includes("REJECT") || s.includes("CANCEL") || s.includes("INACTIVE")) {
      return "status-rejected";
    }
    return "status-default";
  };

  const totalCount = (data?.Licenses?.length || 0) + (bpaData?.length || 0);

  return (
    <div className="obps-citizen-container">
      {/* Header */}
      <div className="obps-citizen-header-wrap">
        <div className="obps-citizen-title-block">
          <h1 className="obps-citizen-page-title">{t("BPA_MY_APPLICATIONS")}</h1>
          {totalCount > 0 && <span className="obps-citizen-count-badge">{totalCount}</span>}
        </div>
        <div className="obps-citizen-header-actions">
          <Link to="/upyog-ui/citizen/obps/search/obps-application" className="obps-btn-search-shortcut">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <circle cx="11" cy="11" r="8" />
              <line x1="21" y1="21" x2="16.65" y2="16.65" />
            </svg>
            {t("BPA_SEARCH_APPLICATIONS") || t("ES_COMMON_SEARCH") || "Search Applications"}
          </Link>
        </div>
      </div>

      {/* Grid or Empty */}
      {finalData && finalData.length > 0 ? (
        <div className="obps-citizen-grid">
          {finalData.map((application, index) => {
            if (application.type === "BPAREG") {
              const status = application?.status;
              return (
                <div key={index} className="obps-citizen-card card-type-stakeholder">
                  <div className="obps-card-header">
                    <div className="obps-card-id-wrap">
                      <span className="obps-card-type-badge">{t("BPA_STAKEHOLDER_REGISTRATION") || "Stakeholder Registration"}</span>
                      <span className="obps-card-id-label">{t("BPA_APPLICATION_NUMBER_LABEL")}</span>
                      <span className="obps-card-id-value">{application?.applicationNumber}</span>
                    </div>
                    <span className={`obps-status-badge ${getStatusClass(status)}`}>
                      {t(`WF_ARCHITECT_${status}`) || t(status)}
                    </span>
                  </div>

                  <div className="obps-card-body">
                    <div className="obps-card-field">
                      <span className="obps-field-label">{t("BPA_LICENSE_TYPE")}</span>
                      <span className="obps-field-value">
                        {t(`TRADELICENSE_TRADETYPE_${application?.tradeLicenseDetail?.tradeUnits?.[0]?.tradeType?.split(".")[0]}`) || t("CS_NA")}
                      </span>
                    </div>

                    <div className="obps-card-field">
                      <span className="obps-field-label">{t("BPA_APPLICANT_NAME_LABEL")}</span>
                      <span className="obps-field-value">
                        {application?.tradeLicenseDetail?.owners?.[0]?.name || t("CS_NA")}
                      </span>
                    </div>

                    {application?.tradeLicenseDetail?.tradeUnits?.[0]?.tradeType?.includes("ARCHITECT") && (
                      <div className="obps-card-field full-width">
                        <span className="obps-field-label">{t("BPA_COUNCIL_OF_ARCH_NO_LABEL")}</span>
                        <span className="obps-field-value">
                          {application?.tradeLicenseDetail?.additionalDetail?.counsilForArchNo || t("CS_NA")}
                        </span>
                      </div>
                    )}
                  </div>

                  <div className="obps-card-actions">
                    {application.status !== "INITIATED" ? (
                      <Link to={{ pathname: `/upyog-ui/citizen/obps/stakeholder/${application?.applicationNumber}`, state: { tenantId: "" } }}>
                        <button type="button" className="obps-btn-view-details">
                          {t("TL_VIEW_DETAILS")}
                        </button>
                      </Link>
                    ) : (
                      <button type="button" className="obps-btn-complete" onClick={() => getBPAREGFormData(application)}>
                        {t("BPA_COMP_WORKFLOW")}
                      </button>
                    )}

                    {application.status === "PENDINGPAYMENT" && (
                      <Link to={{ pathname: `/upyog-ui/citizen/payment/collect/${application?.businessService}/${application?.applicationNumber}` }}>
                        <button type="button" className="obps-btn-pay-now">
                          {t("COMMON_MAKE_PAYMENT")}
                        </button>
                      </Link>
                    )}
                  </div>
                </div>
              );
            } else {
              const status = application?.state || application?.status;
              const isPreApproved = application?.additionalDetails?.isPreApproved
                ? application?.additionalDetails?.isPreApproved
                : application?.businessService === "BPA-PAP"
                ? true
                : false;
              const slaValue =
                typeof application?.sla === "string" && application?.sla?.includes("NA")
                  ? t("CS_NA")
                  : application?.sla;

              return (
                <div key={index} className="obps-citizen-card">
                  <div className="obps-card-header">
                    <div className="obps-card-id-wrap">
                      <span className="obps-card-type-badge">{t("BPA_BUILDING_PLAN_PERMIT") || "Building Plan Permit"}</span>
                      <span className="obps-card-id-label">{t("BPA_APPLICATION_NUMBER_LABEL")}</span>
                      <span className="obps-card-id-value">{application?.applicationNo}</span>
                    </div>
                    <span className={`obps-status-badge ${getStatusClass(status)}`}>
                      {t(`WF_BPA_${status}`) || t(status)}
                    </span>
                  </div>

                  <div className="obps-card-body">
                    <div className="obps-card-field">
                      <span className="obps-field-label">{t("BPA_BASIC_DETAILS_APPLICATION_TYPE_LABEL")}</span>
                      <span className="obps-field-value">
                        {application?.businessService !== "BPA_OC"
                          ? t("WF_BPA_BUILDING_PLAN_SCRUTINY")
                          : t("WF_BPA_BUILDING_OC_PLAN_SCRUTINY")}
                      </span>
                    </div>

                    <div className="obps-card-field">
                      <span className="obps-field-label">{t("BPA_COMMON_SERVICE")}</span>
                      <span className="obps-field-value">{t("BPA_SERVICETYPE_NEW_CONSTRUCTION")}</span>
                    </div>

                    <div className="obps-card-field">
                      <span className="obps-field-label">{t("BPA_IS_PREAPPROVED")}</span>
                      <span className="obps-field-value">{t(isPreApproved ? "CS_COMMON_YES" : "CS_COMMON_NO")}</span>
                    </div>

                    {slaValue && (
                      <div className="obps-card-field">
                        <span className="obps-field-label">{t("BPA_COMMON_SLA")}</span>
                        <span className="obps-field-value highlight-sla">{slaValue}</span>
                      </div>
                    )}
                  </div>

                  <div className="obps-card-actions">
                    {application.action === "SEND_TO_ARCHITECT" || application.status !== "INITIATED" ? (
                      <Link to={{ pathname: `/upyog-ui/citizen/obps/bpa/${application?.applicationNo}`, state: { tenantId: "" } }}>
                        <button type="button" className="obps-btn-view-details">
                          {t("TL_VIEW_DETAILS")}
                        </button>
                      </Link>
                    ) : (
                      <>
                        {labelMessage ? (
                          <Link to={{ pathname: `/upyog-ui/citizen/obps/bpa/${application?.applicationNo}`, state: { tenantId: "" } }}>
                            <button type="button" className="obps-btn-view-details">
                              {t("TL_VIEW_DETAILS")}
                            </button>
                          </Link>
                        ) : (
                          <button
                            type="button"
                            className="obps-btn-complete"
                            onClick={() => getBPAFormData(application, mdmsData, navigate, t)}
                          >
                            {t("BPA_COMP_WORKFLOW")}
                          </button>
                        )}
                      </>
                    )}

                    {application.status === "PENDINGPAYMENT" && (
                      <Link to={{ pathname: `/upyog-ui/citizen/payment/collect/${application?.businessService}/${application?.applicationNumber || application?.applicationNo}` }}>
                        <button type="button" className="obps-btn-pay-now">
                          {t("COMMON_MAKE_PAYMENT")}
                        </button>
                      </Link>
                    )}
                  </div>
                </div>
              );
            }
          })}
        </div>
      ) : (
        <div className="obps-empty-state">
          <div className="obps-empty-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
              <polyline points="14 2 14 8 20 8" />
              <line x1="16" y1="13" x2="8" y2="13" />
              <line x1="16" y1="17" x2="8" y2="17" />
            </svg>
          </div>
          <h3 className="obps-empty-title">{t("BPA_NO_APPLICATION_PRESENT") || t("CS_NO_APPLICATIONS") || "No Applications Found"}</h3>
          <p className="obps-empty-subtitle">{t("BPA_NO_APPLICATION_SUBTITLE") || "You haven't submitted any building permit or stakeholder applications yet."}</p>
        </div>
      )}

      {/* Search Footer Banner */}
      <div className="obps-search-footer-card">
        <div className="obps-search-text-wrap">
          <div className="obps-search-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <circle cx="11" cy="11" r="8" />
              <line x1="21" y1="21" x2="16.65" y2="16.65" />
            </svg>
          </div>
          <span className="obps-search-desc">{t("BPA_NOT_ABLE_TO_FIND_APP_MSG")}</span>
        </div>
        <Link to="/upyog-ui/citizen/obps/search/obps-application" className="obps-search-action-link">
          {t("BPA_CLICK_HERE_TO_SEARCH_LINK")}
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <polyline points="9 18 15 12 9 6" />
          </svg>
        </Link>
      </div>
    </div>
  );
};

export default MyApplication;
