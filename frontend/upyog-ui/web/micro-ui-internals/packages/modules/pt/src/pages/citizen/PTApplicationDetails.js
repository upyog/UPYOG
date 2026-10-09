import { Card, CardSubHeader, Header, LinkButton, Loader, Row, StatusTable, MultiLink, PopUp, Toast, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams, } from "react-router-dom";
import getPTAcknowledgementData from "../../getPTAcknowledgementData";
import PropertyDocument from "../../pageComponents/PropertyDocument";
import PTWFApplicationTimeline from "../../pageComponents/PTWFApplicationTimeline";
import { getCityLocale, getPropertyTypeLocale, propertyCardBodyStyle, getMohallaLocale, pdfDownloadLink } from "../../utils";
import PTCitizenFeedbackPopUp from "../../pageComponents/PTCitizenFeedbackPopUp";
//import PTCitizenFeedback from "@upyog/digit-ui-module-core/src/components/PTCitizenFeedback";

import get from "lodash/get";
import { size } from "lodash";


const PTApplicationDetails = () => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const { acknowledgementIds, tenantId } = useParams();
  const [acknowldgementData, setAcknowldgementData] = useState([]);
  const [showOptions, setShowOptions] = useState(false);
  const [popup, setpopup] = useState(false);
  const [showToast, setShowToast] = useState(null);
  const state = Digit.ULBService.getStateId();
  // const tenantId = Digit.ULBService.getCurrentTenantId();
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};
  const { isLoading, isError, error, data } = Digit.Hooks.pt.usePropertySearch(
    { filters: { acknowledgementIds, tenantId } },
    { filters: { acknowledgementIds, tenantId } }
  );
  const [billAmount, setBillAmount] = useState(null);
  const [billStatus, setBillStatus] = useState(null);
  const [viewTimeline, setViewTimeline] = useState(false);
  let serviceSearchArgs = {
    tenantId: tenantId,
    code: [`PT_${data?.Properties?.[0]?.creationReason}`],
    module: ["PT"],
    referenceIds: [data?.Properties?.[0]?.acknowldgementNumber]
    //removing thid as of now sending ack no in referenceId
    // attributes: {
    //         "attributeCode": "referenceId",
    //         "value": data?.Properties?.[0]?.acknowldgementNumber,
    //     }
  }

  const { isLoading: serviceloading, error: serviceerror, data: servicedata } = Digit.Hooks.pt.useServiceSearchCF({ filters: { serviceSearchArgs } }, { filters: { serviceSearchArgs }, enabled: data?.Properties?.[0]?.acknowldgementNumber ? true : false, cacheTime: 0 });


  const properties = get(data, "Properties", []);
  const propertyId = get(data, "Properties[0].propertyId", []);
  let property = (properties && properties.length > 0 && properties[0]) || {};
  const application = property;
  sessionStorage.setItem("pt-property", JSON.stringify(application));

  useMemo(() => {
    if ((data?.Properties?.[0]?.status === "ACTIVE" || data?.Properties?.[0]?.status === "INACTIVE") && popup == false && servicedata?.Service?.length == 0)
      setpopup(true);
  }, [data, servicedata])

  useEffect(() => {
    const fetchBillDetails = async () => {
      if (acknowledgementIds && tenantId && property) {
        const res = await Digit.PaymentService.searchBill(tenantId, { Service: "PT.MUTATION", consumerCode: acknowledgementIds });
        if (!res.Bill.length) {
          const res1 = await Digit.PTService.ptCalculateMutation({ Property: property }, tenantId);
          setBillAmount(res1?.[acknowledgementIds]?.totalAmount || t("CS_NA"));
          setBillStatus(t(`PT_MUT_BILL_ACTIVE`));
        } else {
          setBillAmount(res?.Bill[0]?.totalAmount || t("CS_NA"));
          setBillStatus(t(`PT_MUT_BILL_${res?.Bill[0]?.status?.toUpperCase()}`));
        }
      }
    };
    fetchBillDetails();
  }, [tenantId, acknowledgementIds, property]);

  const { isLoading: auditDataLoading, isError: isAuditError, data: auditResponse } = Digit.Hooks.pt.usePropertySearch(
    {
      tenantId,
      filters: { propertyIds: propertyId, audit: true },
    },
    {
      enabled: true,
      // select: (d) =>
      //   d.Properties.filter((e) => e.status === "ACTIVE")?.sort((a, b) => b.auditDetails.lastModifiedTime - a.auditDetails.lastModifiedTime),
    }
  );

  const { data: reciept_data, isLoading: recieptDataLoading } = Digit.Hooks.useRecieptSearch(
    {
      tenantId: tenantId,
      businessService: "PT.MUTATION",
      consumerCodes: acknowledgementIds,
      isEmployee: false,
    },
    { enabled: acknowledgementIds ? true : false }
  );

  if (!property.workflow) {
    let workflow = {
      id: null,
      tenantId: tenantId,
      businessService: "PT.MUTATION",
      businessId: application?.acknowldgementNumber,
      action: "",
      moduleName: "PT",
      state: null,
      comment: null,
      documents: null,
      assignes: null,
    };
    property.workflow = workflow;
  }

  if (property && property.owners && property.owners.length > 0) {
    let ownersTemp = [];
    let owners = [];
    property.owners.map((owner) => {
      owner.documentUid = owner.documents ? owner.documents[0].documentUid : "NA";
      owner.documentType = owner.documents ? owner.documents[0].documentType : "NA";
      if (owner.status == "ACTIVE") {
        ownersTemp.push(owner);
      } else {
        owners.push(owner);
      }
    });
    property.ownersInit = owners;
    property.ownersTemp = ownersTemp;
  }
  property.ownershipCategoryTemp = property?.ownershipCategory;
  property.ownershipCategoryInit = "NA";
  // Set Institution/Applicant info card visibility
  if (get(application, "Properties[0].ownershipCategory", "")?.startsWith("INSTITUTION")) {
    property.institutionTemp = property.institution;
  }

  if (auditResponse && Array.isArray(get(auditResponse, "Properties", [])) && get(auditResponse, "Properties", []).length > 0) {
    const propertiesAudit = get(auditResponse, "Properties", []);
    const propertyIndex = property.status == "ACTIVE" ? 1 : 0;
    // const previousActiveProperty = propertiesAudit.filter(property => property.status == 'ACTIVE').sort((x, y) => y.auditDetails.lastModifiedTime - x.auditDetails.lastModifiedTime)[propertyIndex];
    // Removed filter(property => property.status == 'ACTIVE') condition to match result in qa env
    const previousActiveProperty = propertiesAudit
      .filter((property) => property.status == "ACTIVE")
      .sort((x, y) => y.auditDetails.lastModifiedTime - x.auditDetails.lastModifiedTime)[propertyIndex];
    property.ownershipCategoryInit = previousActiveProperty?.ownershipCategory;
    property.ownersInit = previousActiveProperty?.owners?.filter((owner) => owner.status == "ACTIVE");

    const curWFProperty = propertiesAudit.sort((x, y) => y.auditDetails.lastModifiedTime - x.auditDetails.lastModifiedTime)[0];
    property.ownersTemp = curWFProperty.owners.filter((owner) => owner.status == "ACTIVE");

    if (property?.ownershipCategoryInit?.startsWith("INSTITUTION")) {
      property.institutionInit = previousActiveProperty.institution;
    }
  }

  let transfereeOwners = get(property, "ownersTemp", []);
  let transferorOwners = get(property, "ownersInit", []);

  let transfereeInstitution = get(property, "institutionTemp", []);
  let isInstitution = property?.ownershipCategoryInit?.startsWith("INSTITUTION");
  let transferorInstitution = get(property, "institutionInit", []);

  let units = [];
  units = application?.units;
  units &&
    units.sort((x, y) => {
      let a = x.floorNo,
        b = y.floorNo;
      if (x.floorNo < 0) {
        a = x.floorNo * -20;
      }
      if (y.floorNo < 0) {
        b = y.floorNo * -20;
      }
      if (a > b) {
        return 1;
      } else {
        return -1;
      }
    });
  let owners = [];
  owners = application?.owners;
  let docs = [];
  docs = application?.documents;

  if (isLoading || auditDataLoading) {
    return <Loader />;
  }

  let flrno,
    i = 0;
  flrno = units && units[0]?.floorNo;

  const isPropertyTransfer = property?.creationReason && property.creationReason === "MUTATION" ? true : false;

  const getAcknowledgementData = async () => {
    const applications = application || {};
    const tenantInfo = tenants.find((tenant) => tenant.code === applications.tenantId);
    const acknowldgementDataAPI = await getPTAcknowledgementData({ ...applications }, tenantInfo, t);
    Digit.Utils.pdf.generate(acknowldgementDataAPI);
    //setAcknowldgementData(acknowldgementDataAPI);
  };

  let documentDate = t("CS_NA");
  if (property?.additionalDetails?.documentDate) {
    const date = new Date(property?.additionalDetails?.documentDate);
    const month = Digit.Utils.date.monthNames[date.getMonth()];
    documentDate = `${date.getDate()} ${month} ${date.getFullYear()}`;
  }

  async function getRecieptSearch({ tenantId, payments, ...params }) {
    let response = { filestoreIds: [payments?.fileStoreId] };
    if (response !== null) {
      const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
      window.open(fileStore[response?.filestoreIds[0]], "_blank");
    }
    else {
      response = await Digit.PaymentService.generatePdf(tenantId, { Payments: [{ ...payments }] }, "property-receipt");
      const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
      window.open(fileStore[response?.filestoreIds[0]], "_blank");
    }
  }

  const handleViewTimeline = () => {
    const timelineSection = document.getElementById('timeline');
    if (timelineSection) {
      timelineSection.scrollIntoView({ behavior: 'smooth' });
    }
    setViewTimeline(true);
  };
  const handleDownload = async (document, tenantid) => {
    let tenantId = tenantid ? tenantid : tenantId;
    const res = await Digit.UploadServices.Filefetch([document?.fileStoreId], tenantId);
    let documentLink = pdfDownloadLink(res.data, document?.fileStoreId);
    window.open(documentLink, "_blank");
  };

  const printCertificate = async () => {
    let response = await Digit.PaymentService.generatePdf(tenantId, { Properties: [data?.Properties?.[0]] }, "ptmutationcertificate");
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  };
  state
  let dowloadOptions = [];

  dowloadOptions.push({
    label: data?.Properties?.[0]?.creationReason === "MUTATION" ? t("MT_APPLICATION") : t("PT_APPLICATION_ACKNOWLEDGMENT"),
    onClick: () => getAcknowledgementData(),
  });
  if (reciept_data && reciept_data?.Payments.length > 0 && recieptDataLoading == false)
    dowloadOptions.push({
      label: t("MT_FEE_RECIEPT"),
      onClick: () => getRecieptSearch({ tenantId: state, payments: reciept_data?.Payments[0] }),
    });
  if (data?.Properties?.[0]?.creationReason === "MUTATION" && data?.Properties?.[0]?.status === "ACTIVE")
    dowloadOptions.push({
      label: t("MT_CERTIFICATE"),
      onClick: () => printCertificate(),
    });

  const reversedOwners = Array.isArray(data?.Properties?.[0]?.owners) ? data?.Properties?.[0]?.owners.slice().reverse() : [];
  const statusClass = (data?.Properties?.[0]?.status || property?.status || "INWORKFLOW").toLowerCase();

  return (
    <div className="pt-citizen-application-details-wrapper">
      <div className="pt-app-page-header-wrap">
        <Header className="pt-app-page-title">
          {isPropertyTransfer ? t("PT_MUTATION_APPLICATION_DETAILS") : t("PT_APPLICATION_DETAILS_HEADER") || t("PT_MUTATION_APPLICATION_DETAILS")}
        </Header>
        <div className="pt-app-page-header-actions">
          {dowloadOptions && dowloadOptions.length > 0 && (
            <MultiLink
              className="multilinkWrapper"
              onHeadClick={() => setShowOptions(!showOptions)}
              displayOptions={showOptions}
              options={dowloadOptions}
            />
          )}
          <button type="button" className="pt-app-view-timeline-btn" onClick={handleViewTimeline}>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <circle cx="12" cy="12" r="10" />
              <polyline points="12 6 12 12 16 14" />
            </svg>
            {t("VIEW_TIMELINE")}
          </button>
        </div>
      </div>

      {/* 1. Hero KPI Card */}
      <div className="pt-app-hero-card">
        <div className="pt-app-hero-header">
          <div className="pt-app-id-section">
            <div className="pt-app-id-label">{t("PT_APPLICATION_NUMBER_LABEL")}</div>
            <div className="pt-app-id-value">{property?.acknowldgementNumber || t("CS_NA")}</div>
            {property?.propertyId && (
              <div className="pt-app-sub-id">
                {t("PT_SEARCHPROPERTY_TABEL_PTUID")}: <strong>{property?.propertyId}</strong>
              </div>
            )}
          </div>
          <div className="pt-app-status-wrap">
            <span className={`pt-app-status-pill status-${statusClass}`}>
              {t(`PT_COMMON_${property?.status || "INWORKFLOW"}`)}
            </span>
          </div>
        </div>

        <div className="pt-app-hero-kpi-grid">
          <div className="pt-app-kpi-item">
            <div className="pt-app-kpi-label">{t("PT_APPLICATION_CHANNEL_LABEL")}</div>
            <div className="pt-app-kpi-val">
              {t(`ES_APPLICATION_DETAILS_APPLICATION_CHANNEL_${property?.channel}`) || t("CS_NA")}
            </div>
          </div>
          {isPropertyTransfer ? (
            <>
              <div className="pt-app-kpi-item">
                <div className="pt-app-kpi-label">{t("PT_FEE_AMOUNT")}</div>
                <div className="pt-app-kpi-val val-fee">₹{billAmount || 0}</div>
              </div>
              <div className="pt-app-kpi-item">
                <div className="pt-app-kpi-label">{t("PT_PAYMENT_STATUS")}</div>
                <div className="pt-app-kpi-val val-status">{billStatus || t("CS_NA")}</div>
              </div>
            </>
          ) : (
            <div className="pt-app-kpi-item">
              <div className="pt-app-kpi-label">{t("PT_COMMON_TABLE_COL_APP_TYPE")}</div>
              <div className="pt-app-kpi-val">
                {property?.creationReason ? t(`PT.${property.creationReason}`) : t("CS_NA")}
              </div>
            </div>
          )}
        </div>
      </div>

      {/* 2. Address Details Section Card */}
      <div className="pt-app-section-card">
        <div className="pt-app-section-header">
          <div className="pt-app-section-title-wrap">
            <span className="pt-app-section-accent-bar" />
            <h2 className="pt-app-section-title">{t("PT_PROPERTY_ADDRESS_SUB_HEADER")}</h2>
          </div>
        </div>
        <div className="pt-app-data-grid">
          <div className="pt-app-data-item">
            <span className="pt-app-data-label">{t("PT_PROPERTY_ADDRESS_PINCODE")}</span>
            <span className="pt-app-data-value">{property?.address?.pincode || t("CS_NA")}</span>
          </div>
          <div className="pt-app-data-item">
            <span className="pt-app-data-label">{t("PT_COMMON_CITY")}</span>
            <span className="pt-app-data-value">{t(getCityLocale(property?.tenantId || tenantId)) || property?.address?.city || t("CS_NA")}</span>
          </div>
          <div className="pt-app-data-item">
            <span className="pt-app-data-label">{t("PT_COMMON_LOCALITY_OR_MOHALLA")}</span>
            <span className="pt-app-data-value">{t(property?.address?.locality?.name || property?.address?.locality?.area) || t("CS_NA")}</span>
          </div>
          <div className="pt-app-data-item">
            <span className="pt-app-data-label">{t("PT_PROPERTY_ADDRESS_STREET_NAME")}</span>
            <span className="pt-app-data-value">{property?.address?.street || t("CS_NA")}</span>
          </div>
          <div className="pt-app-data-item">
            <span className="pt-app-data-label">
              {isPropertyTransfer ? t("PT_DOOR_OR_HOUSE") : t("PT_PROPERTY_ADDRESS_COLONY_NAME")}
            </span>
            <span className="pt-app-data-value">
              {(isPropertyTransfer ? property?.address?.doorNo : property?.address?.buildingName) || t("CS_NA")}
            </span>
          </div>
        </div>
      </div>

      {isPropertyTransfer ? (
        <>
          {/* 3A. Transferor Details */}
          <div className="pt-app-section-card">
            <div className="pt-app-section-header">
              <div className="pt-app-section-title-wrap">
                <span className="pt-app-section-accent-bar" />
                <h2 className="pt-app-section-title">{t("PT_MUTATION_TRANSFEROR_DETAILS")}</h2>
              </div>
            </div>
            <div>
              {Array.isArray(transferorOwners) &&
                transferorOwners
                  .sort((item, item2) => (item?.additionalDetails?.ownerSequence || 0) - (item2?.additionalDetails?.ownerSequence || 0))
                  .map((owner, index) => (
                    <div key={index} className="pt-app-owner-card">
                      <div className="pt-app-owner-header">
                        <span className="pt-app-owner-badge badge-transferor">
                          {transferorOwners.length > 1 ? `${t("PT_TRANSFEROR_HEADER")} ${index + 1}` : t("PT_TRANSFEROR_DETAILS_LABEL") || t("PT_MUTATION_TRANSFEROR_DETAILS")}
                        </span>
                      </div>
                      <div className="pt-app-data-grid">
                        <div className="pt-app-data-item">
                          <span className="pt-app-data-label">{t("PT_COMMON_APPLICANT_NAME_LABEL")}</span>
                          <span className="pt-app-data-value">{owner?.name || t("CS_NA")}</span>
                        </div>
                        <div className="pt-app-data-item">
                          <span className="pt-app-data-label">{t("PT_FORM3_GUARDIAN_NAME")}</span>
                          <span className="pt-app-data-value">{owner?.fatherOrHusbandName || t("CS_NA")}</span>
                        </div>
                        <div className="pt-app-data-item">
                          <span className="pt-app-data-label">{t("PT_FORM3_MOBILE_NUMBER")}</span>
                          <span className="pt-app-data-value">{owner?.mobileNumber || t("CS_NA")}</span>
                        </div>
                        <div className="pt-app-data-item">
                          <span className="pt-app-data-label">{t("PT_MUTATION_AUTHORISED_EMAIL")}</span>
                          <span className="pt-app-data-value">{owner?.emailId || t("CS_NA")}</span>
                        </div>
                        <div className="pt-app-data-item">
                          <span className="pt-app-data-label">{t("PT_MUTATION_TRANSFEROR_SPECIAL_CATEGORY")}</span>
                          <span className="pt-app-data-value">{owner?.ownerType || t("CS_NA")}</span>
                        </div>
                        <div className="pt-app-data-item">
                          <span className="pt-app-data-label">{t("PT_OWNERSHIP_INFO_CORR_ADDR")}</span>
                          <span className="pt-app-data-value">{owner?.correspondenceAddress || t("CS_NA")}</span>
                        </div>
                      </div>
                    </div>
                  ))}
            </div>
          </div>

          {/* 3B. Transferee Details */}
          <div className="pt-app-section-card">
            <div className="pt-app-section-header">
              <div className="pt-app-section-title-wrap">
                <span className="pt-app-section-accent-bar" />
                <h2 className="pt-app-section-title">{t("PT_MUTATION_TRANSFEREE_DETAILS")}</h2>
              </div>
            </div>
            {isInstitution ? (
              <div>
                {Array.isArray(transfereeOwners) &&
                  transfereeOwners
                    .sort((item, item2) => (item.additionalDetails?.ownerSequence || 0) - (item2.additionalDetails?.ownerSequence || 0))
                    .map((owner, index) => (
                      <div key={index} className="pt-app-owner-card">
                        <div className="pt-app-owner-header">
                          <span className="pt-app-owner-badge badge-transferee">
                            {transfereeOwners.length > 1 ? `${t("PT_TRANSFEREE_HEADER")} ${index + 1}` : t("PT_TRANSFEREE_DETAILS_LABEL") || t("PT_MUTATION_TRANSFEREE_DETAILS")}
                          </span>
                        </div>
                        <div className="pt-app-data-grid">
                          <div className="pt-app-data-item">
                            <span className="pt-app-data-label">{t("PT_INSTITUTION_NAME")}</span>
                            <span className="pt-app-data-value">{transferorInstitution?.name || t("CS_NA")}</span>
                          </div>
                          <div className="pt-app-data-item">
                            <span className="pt-app-data-label">{t("PT_TYPE_OF_INSTITUTION")}</span>
                            <span className="pt-app-data-value">{t(transferorInstitution?.type) || t("CS_NA")}</span>
                          </div>
                          <div className="pt-app-data-item">
                            <span className="pt-app-data-label">{t("PT_NAME_AUTHORIZED_PERSON")}</span>
                            <span className="pt-app-data-value">{transferorInstitution?.nameOfAuthorizedPerson || t("CS_NA")}</span>
                          </div>
                          <div className="pt-app-data-item">
                            <span className="pt-app-data-label">{t("PT_LANDLINE_NUMBER")}</span>
                            <span className="pt-app-data-value">{owner?.altContactNumber || t("CS_NA")}</span>
                          </div>
                          <div className="pt-app-data-item">
                            <span className="pt-app-data-label">{t("PT_FORM3_MOBILE_NUMBER")}</span>
                            <span className="pt-app-data-value">{owner?.mobileNumber || t("CS_NA")}</span>
                          </div>
                          <div className="pt-app-data-item">
                            <span className="pt-app-data-label">{t("PT_INSTITUTION_DESIGNATION")}</span>
                            <span className="pt-app-data-value">{transferorInstitution?.designation || t("CS_NA")}</span>
                          </div>
                          <div className="pt-app-data-item">
                            <span className="pt-app-data-label">{t("PT_MUTATION_AUTHORISED_EMAIL")}</span>
                            <span className="pt-app-data-value">{owner?.emailId || t("CS_NA")}</span>
                          </div>
                          <div className="pt-app-data-item">
                            <span className="pt-app-data-label">{t("PT_OWNERSHIP_INFO_CORR_ADDR")}</span>
                            <span className="pt-app-data-value">{owner?.correspondenceAddress || t("CS_NA")}</span>
                          </div>
                        </div>
                      </div>
                    ))}
              </div>
            ) : (
              <div>
                {Array.isArray(transfereeOwners) &&
                  transfereeOwners
                    .sort((item, item2) => (item?.additionalDetails?.ownerSequence || 0) - (item2?.additionalDetails?.ownerSequence || 0))
                    .map((owner, index) => (
                      <div key={index} className="pt-app-owner-card">
                        <div className="pt-app-owner-header">
                          <span className="pt-app-owner-badge badge-transferee">
                            {transfereeOwners.length > 1 ? `${t("PT_TRANSFEREE_HEADER")} ${index + 1}` : t("PT_TRANSFEREE_DETAILS_LABEL") || t("PT_MUTATION_TRANSFEREE_DETAILS")}
                          </span>
                        </div>
                        <div className="pt-app-data-grid">
                          <div className="pt-app-data-item">
                            <span className="pt-app-data-label">{t("PT_COMMON_APPLICANT_NAME_LABEL")}</span>
                            <span className="pt-app-data-value">{owner?.name || t("CS_NA")}</span>
                          </div>
                          <div className="pt-app-data-item">
                            <span className="pt-app-data-label">{t("PT_FORM3_GUARDIAN_NAME")}</span>
                            <span className="pt-app-data-value">{owner?.fatherOrHusbandName || t("CS_NA")}</span>
                          </div>
                          <div className="pt-app-data-item">
                            <span className="pt-app-data-label">{t("PT_COMMON_GENDER_LABEL")}</span>
                            <span className="pt-app-data-value">{owner?.gender || t("CS_NA")}</span>
                          </div>
                          <div className="pt-app-data-item">
                            <span className="pt-app-data-label">{t("PT_FORM3_OWNERSHIP_TYPE")}</span>
                            <span className="pt-app-data-value">
                              {application?.ownershipCategory ? t(`PT_OWNERSHIP_${application?.ownershipCategory}`) : t("CS_NA")}
                            </span>
                          </div>
                          <div className="pt-app-data-item">
                            <span className="pt-app-data-label">{t("PT_FORM3_MOBILE_NUMBER")}</span>
                            <span className="pt-app-data-value">{owner?.mobileNumber || t("CS_NA")}</span>
                          </div>
                          <div className="pt-app-data-item">
                            <span className="pt-app-data-label">{t("PT_MUTATION_AUTHORISED_EMAIL")}</span>
                            <span className="pt-app-data-value">{owner?.emailId || t("CS_NA")}</span>
                          </div>
                          <div className="pt-app-data-item">
                            <span className="pt-app-data-label">{t("PT_MUTATION_TRANSFEROR_SPECIAL_CATEGORY")}</span>
                            <span className="pt-app-data-value">{owner?.ownerType || t("CS_NA")}</span>
                          </div>
                          <div className="pt-app-data-item">
                            <span className="pt-app-data-label">{t("PT_OWNERSHIP_INFO_CORR_ADDR")}</span>
                            <span className="pt-app-data-value">{owner?.correspondenceAddress || t("CS_NA")}</span>
                          </div>
                        </div>
                      </div>
                    ))}
              </div>
            )}
          </div>

          {/* 3C. Mutation Details */}
          <div className="pt-app-section-card">
            <div className="pt-app-section-header">
              <div className="pt-app-section-title-wrap">
                <span className="pt-app-section-accent-bar" />
                <h2 className="pt-app-section-title">{t("PT_MUTATION_DETAILS")}</h2>
              </div>
            </div>
            <div className="pt-app-data-grid">
              <div className="pt-app-data-item">
                <span className="pt-app-data-label">{t("PT_MUTATION_PENDING_COURT")}</span>
                <span className="pt-app-data-value">{property?.additionalDetails?.isMutationInCourt || t("CS_NA")}</span>
              </div>
              <div className="pt-app-data-item">
                <span className="pt-app-data-label">{t("PT_DETAILS_COURT_CASE")}</span>
                <span className="pt-app-data-value">{property?.additionalDetails?.caseDetails || t("CS_NA")}</span>
              </div>
              <div className="pt-app-data-item">
                <span className="pt-app-data-label">{t("PT_PROP_UNDER_GOV_AQUISITION")}</span>
                <span className="pt-app-data-value">{property?.additionalDetails?.isPropertyUnderGovtPossession || t("CS_NA")}</span>
              </div>
              <div className="pt-app-data-item">
                <span className="pt-app-data-label">{t("PT_DETAILS_GOV_AQUISITION")}</span>
                <span className="pt-app-data-value">{t("CS_NA")}</span>
              </div>
            </div>
          </div>

          {/* 3D. Registration Details */}
          <div className="pt-app-section-card">
            <div className="pt-app-section-header">
              <div className="pt-app-section-title-wrap">
                <span className="pt-app-section-accent-bar" />
                <h2 className="pt-app-section-title">{t("PT_REGISTRATION_DETAILS")}</h2>
              </div>
            </div>
            <div className="pt-app-data-grid">
              <div className="pt-app-data-item">
                <span className="pt-app-data-label">{t("PT_REASON_PROP_TRANSFER")}</span>
                <span className="pt-app-data-value">{t(property?.additionalDetails?.reasonForTransfer) || t("CS_NA")}</span>
              </div>
              <div className="pt-app-data-item">
                <span className="pt-app-data-label">{t("PT_PROP_MARKET_VALUE")}</span>
                <span className="pt-app-data-value">{property?.additionalDetails?.marketValue ? `₹${property.additionalDetails.marketValue}` : t("CS_NA")}</span>
              </div>
              <div className="pt-app-data-item">
                <span className="pt-app-data-label">{t("PT_REG_NUMBER")}</span>
                <span className="pt-app-data-value">{property?.additionalDetails?.documentNumber || t("CS_NA")}</span>
              </div>
              <div className="pt-app-data-item">
                <span className="pt-app-data-label">{t("PT_DOC_ISSUE_DATE")}</span>
                <span className="pt-app-data-value">{documentDate}</span>
              </div>
              <div className="pt-app-data-item">
                <span className="pt-app-data-label">{t("PT_REG_DOC_VALUE")}</span>
                <span className="pt-app-data-value">{property?.additionalDetails?.documentValue ? `₹${property.additionalDetails.documentValue}` : t("CS_NA")}</span>
              </div>
              <div className="pt-app-data-item">
                <span className="pt-app-data-label">{t("PT_REMARKS")}</span>
                <span className="pt-app-data-value">{t("CS_NA")}</span>
              </div>
            </div>
          </div>
        </>
      ) : (
        <>
          {/* 3A. Assessment Details */}
          <div className="pt-app-section-card">
            <div className="pt-app-section-header">
              <div className="pt-app-section-title-wrap">
                <span className="pt-app-section-accent-bar" />
                <h2 className="pt-app-section-title">{t("PT_PROPERTY_ASSESSMENT_DETAILS_HEADER")}</h2>
              </div>
            </div>
            <div className="pt-app-data-grid">
              <div className="pt-app-data-item">
                <span className="pt-app-data-label">{t("PT_ASSESMENT_INFO_USAGE_TYPE")}</span>
                <span className="pt-app-data-value">
                  {t(
                    (property?.usageCategory !== "RESIDENTIAL" ? "COMMON_PROPUSGTYPE_NONRESIDENTIAL_" : "COMMON_PROPSUBUSGTYPE_") +
                      (property?.usageCategory?.split(".")[1] ? property?.usageCategory?.split(".")[1] : property?.usageCategory)
                  ) || t("CS_NA")}
                </span>
              </div>
              <div className="pt-app-data-item">
                <span className="pt-app-data-label">{t("PT_COMMON_PROPERTY_TYPE")}</span>
                <span className="pt-app-data-value">{t(getPropertyTypeLocale(property?.propertyType)) || t("CS_NA")}</span>
              </div>
              <div className="pt-app-data-item">
                <span className="pt-app-data-label">{t("PT_ASSESMENT1_PLOT_SIZE")}</span>
                <span className="pt-app-data-value">{property?.landArea ? `${property.landArea} sq.ft` : t("CS_NA")}</span>
              </div>
              <div className="pt-app-data-item">
                <span className="pt-app-data-label">{t("PT_ASSESMENT_INFO_NO_OF_FLOOR")}</span>
                <span className="pt-app-data-value">{property?.noOfFloors || t("CS_NA")}</span>
              </div>
              <div className="pt-app-data-item">
                <span className="pt-app-data-label">{t("PT_ASSESMENT1_ELECTRICITY_NUMBER")}</span>
                <span className="pt-app-data-value">{property.additionalDetails?.electricity || t("CS_NA")}</span>
              </div>
              <div className="pt-app-data-item">
                <span className="pt-app-data-label">{t("PT_ASSESMENT1_ELECTRICITY_UID")}</span>
                <span className="pt-app-data-value">{property.additionalDetails?.uid || t("CS_NA")}</span>
              </div>
              <div className="pt-app-data-item">
                <span className="pt-app-data-label">{t("PT_STRUCTURE_TYPE_LABEL")}</span>
                <span className="pt-app-data-value">{property?.additionalDetails?.structureType?.i18nKey || t("CS_NA")}</span>
              </div>
              <div className="pt-app-data-item">
                <span className="pt-app-data-label">{t("PT_AGE_OF_PROPERTY_LABEL")}</span>
                <span className="pt-app-data-value">{property?.additionalDetails?.ageOfProperty?.code || t("CS_NA")}</span>
              </div>
            </div>
          </div>

          {/* 3B. Units & Floors */}
          {Array.isArray(units) && units.length > 0 && (
            <div className="pt-app-section-card">
              <div className="pt-app-section-header">
                <div className="pt-app-section-title-wrap">
                  <span className="pt-app-section-accent-bar" />
                  <h2 className="pt-app-section-title">{t("PT_ASSESSMENT_FLOOR_DETAILS_HEADER")}</h2>
                </div>
              </div>
              <div>
                {units.map((unit, index) => {
                  const isNewFloor = flrno !== unit?.floorNo;
                  if (isNewFloor) {
                    i = 1;
                    flrno = unit?.floorNo;
                  } else {
                    i = i + 1;
                  }
                  return (
                    <div key={index} className="pt-app-floor-group">
                      {isNewFloor && (
                        <div className="pt-app-floor-title">
                          {t(`PROPERTYTAX_FLOOR_${unit?.floorNo}`)}
                        </div>
                      )}
                      <div className="pt-app-unit-card">
                        <div className="pt-app-unit-title">
                          {t("ES_APPLICATION_DETAILS_UNIT")} {i}
                        </div>
                        <div className="pt-app-data-grid">
                          <div className="pt-app-data-item">
                            <span className="pt-app-data-label">{t("PT_ASSESSMENT_UNIT_USAGE_TYPE")}</span>
                            <span className="pt-app-data-value">
                              {t(
                                (property?.usageCategory !== "RESIDENTIAL" ? "COMMON_PROPUSGTYPE_NONRESIDENTIAL_" : "COMMON_PROPUSGTYPE_") +
                                  (property?.usageCategory?.split(".")[1] ? property?.usageCategory?.split(".")[1] : property?.usageCategory)
                              ) || t("CS_NA")}
                            </span>
                          </div>
                          <div className="pt-app-data-item">
                            <span className="pt-app-data-label">{t("PT_OCCUPANY_TYPE_LABEL")}</span>
                            <span className="pt-app-data-value">{t("PROPERTYTAX_OCCUPANCYTYPE_" + unit?.occupancyType) || t("CS_NA")}</span>
                          </div>
                          <div className="pt-app-data-item">
                            <span className="pt-app-data-label">{t("PT_BUILTUP_AREA_LABEL")}</span>
                            <span className="pt-app-data-value">{unit?.constructionDetail?.builtUpArea ? `${unit?.constructionDetail?.builtUpArea} sq.ft` : t("CS_NA")}</span>
                          </div>
                          {unit.occupancyType == "RENTED" && (
                            <div className="pt-app-data-item">
                              <span className="pt-app-data-label">{t("PT_FORM2_TOTAL_ANNUAL_RENT")}</span>
                              <span className="pt-app-data-value">{unit?.arv ? `₹${unit?.arv}` : t("CS_NA")}</span>
                            </div>
                          )}
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          )}

          {/* 3C. Ownership Details */}
          <div className="pt-app-section-card">
            <div className="pt-app-section-header">
              <div className="pt-app-section-title-wrap">
                <span className="pt-app-section-accent-bar" />
                <h2 className="pt-app-section-title">{t("PT_COMMON_PROPERTY_OWNERSHIP_DETAILS_HEADER")}</h2>
              </div>
            </div>
            <div>
              {Array.isArray(owners) &&
                reversedOwners.map((owner, index) => (
                  <div key={index} className="pt-app-owner-card">
                    <div className="pt-app-owner-header">
                      <span className="pt-app-owner-badge">
                        {owners.length > 1 ? `${t("PT_OWNER_SUB_HEADER")} ${index + 1}` : t("PT_OWNER_DETAILS_LABEL") || t("PT_COMMON_PROPERTY_OWNERSHIP_DETAILS_HEADER")}
                      </span>
                    </div>
                    <div className="pt-app-data-grid">
                      <div className="pt-app-data-item">
                        <span className="pt-app-data-label">{t("PT_COMMON_APPLICANT_NAME_LABEL")}</span>
                        <span className="pt-app-data-value">{owner?.name || t("CS_NA")}</span>
                      </div>
                      <div className="pt-app-data-item">
                        <span className="pt-app-data-label">{t("PT_FORM3_GUARDIAN_NAME")}</span>
                        <span className="pt-app-data-value">{owner?.fatherOrHusbandName || t("CS_NA")}</span>
                      </div>
                      <div className="pt-app-data-item">
                        <span className="pt-app-data-label">{t("PT_COMMON_GENDER_LABEL")}</span>
                        <span className="pt-app-data-value">{owner?.gender || t("CS_NA")}</span>
                      </div>
                      <div className="pt-app-data-item">
                        <span className="pt-app-data-label">{t("PT_FORM3_OWNERSHIP_TYPE")}</span>
                        <span className="pt-app-data-value">
                          {property?.ownershipCategory ? t(`PT_OWNERSHIP_${property?.ownershipCategory}`) : t("CS_NA")}
                        </span>
                      </div>
                      <div className="pt-app-data-item">
                        <span className="pt-app-data-label">{t("PT_FORM3_MOBILE_NUMBER")}</span>
                        <span className="pt-app-data-value">{owner?.mobileNumber || t("CS_NA")}</span>
                      </div>
                      <div className="pt-app-data-item">
                        <span className="pt-app-data-label">{t("PT_MUTATION_AUTHORISED_EMAIL")}</span>
                        <span className="pt-app-data-value">{owner?.emailId || t("CS_NA")}</span>
                      </div>
                      <div className="pt-app-data-item">
                        <span className="pt-app-data-label">{t("PT_MUTATION_TRANSFEROR_SPECIAL_CATEGORY")}</span>
                        <span className="pt-app-data-value">{owner?.ownerType || t("CS_NA")}</span>
                      </div>
                      <div className="pt-app-data-item">
                        <span className="pt-app-data-label">{t("PT_OWNERSHIP_INFO_CORR_ADDR")}</span>
                        <span className="pt-app-data-value">{owner?.permanentAddress || t("CS_NA")}</span>
                      </div>
                    </div>
                  </div>
                ))}
            </div>
          </div>
        </>
      )}

      {/* 4. Documents Section Card */}
      <div className="pt-app-section-card">
        <div className="pt-app-section-header">
          <div className="pt-app-section-title-wrap">
            <span className="pt-app-section-accent-bar" />
            <h2 className="pt-app-section-title">{t("PT_COMMON_DOCS")}</h2>
          </div>
        </div>
        <div>
          {Array.isArray(docs) && docs.length > 0 ? (
            <PropertyDocument property={property} />
          ) : (
            <div className="pt-app-data-item">
              <span className="pt-app-data-value">{t("PT_NO_DOCUMENTS_MSG")}</span>
            </div>
          )}
        </div>
      </div>

      {/* 5. Timeline Card */}
      <div className="pt-app-timeline-card" id="timeline">
        <div className="pt-app-section-header">
          <div className="pt-app-section-title-wrap">
            <span className="pt-app-section-accent-bar" />
            <h2 className="pt-app-section-title">{t("CS_APPLICATION_DETAILS_APPLICATION_TIMELINE") || t("PT_APPLICATION_TIMELINE") || "Application Timeline"}</h2>
          </div>
        </div>
        <PTWFApplicationTimeline application={application} id={acknowledgementIds} userType={"citizen"} />
      </div>

      {showToast && (
        <Toast
          error={showToast.key}
          label={t(showToast.label)}
          className="pt-inline-toast-bottom"
          onClose={() => {
            setShowToast(null);
          }}
        />
      )}

      {popup && <PTCitizenFeedbackPopUp setpopup={setpopup} setShowToast={setShowToast} data={data} />}
    </div>
  );
};

export default PTApplicationDetails;
