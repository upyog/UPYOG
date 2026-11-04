import {
  Card,
  CardSubHeader,
  EditIcon,
  Header,
  LinkButton,
  Loader,
  PopUp,
  Row,
  StatusTable,
  SubmitBar,
  LinkLabel
} from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Link, useParams,  } from "react-router-dom";
import PropertyDocument from "../../../pageComponents/PropertyDocument";
import { getCityLocale, getPropertyTypeLocale, stringReplaceAll } from "../../../utils";
import ActionModal from "../../../../../templates/ApplicationDetails/Modal/index"
import ArrearSummary from "../../../../../common/src/payments/citizen/bills/routes/bill-details/arrear-summary";
const setBillData = async (tenantId, propertyIds, updatefetchBillData, updateCanFetchBillData) => {
  const assessmentData = await Digit.PTService.assessmentSearch({ tenantId, filters: { propertyIds } });
  let billData = {};
  if (assessmentData?.Assessments?.length > 0) {
    billData = await Digit.PaymentService.fetchBill(tenantId, {
      businessService: "PT",
      consumerCode: propertyIds,
    });
  }
  updatefetchBillData(billData);
  updateCanFetchBillData({
    loading: false,
    loaded: true,
    canLoad: true,
  });
};

const getBillAmount = (fetchBillData = null) => {
  if (fetchBillData == null) return "CS_NA";
  return fetchBillData ? (fetchBillData?.Bill && fetchBillData.Bill[0] ? fetchBillData.Bill[0]?.totalAmount : "0") : "0";
};

const PropertyInformation = () => {
  const { t } = useTranslation();
  const { propertyIds } = useParams();
const [showModal,setshowModal] = useState(false)
  var isMobile = window.Digit.Utils.browser.isMobile();
  const [enableAudit, setEnableAudit] = useState(false);
const moduleCode="PT"
const navigate = Digit.Hooks.useCustomNavigate();
const selectedAction =    {
  action: "ASSESS_PROPERTY",
  forcedName: "PT_ASSESS",
  showFinancialYearsModal: true,
  customFunctionToExecute: (data) => {
    //const navigate = Digit.Hooks.useCustomNavigate();
    delete data.customFunctionToExecute;
    navigate(`/upyog-ui/citizen/pt/assessment-details/${property.propertyId}`, { replace: true, state: { ...data } });
  },
  tenantId: Digit.ULBService.getStateId(),
}
const { id: applicationNumber } = useParams();
const [isEnableLoader, setIsEnableLoader] = useState(false);
const [isWarningPop, setWarningPopUp] = useState(false);
const businessService="PT"
const state = Digit.ULBService.getStateId();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { data: UpdateNumberConfig } = Digit.Hooks.useCommonMDMSV2(Digit.ULBService.getStateId(),"PropertyTax",["UpdateNumber"],{
    select: (data) => {
      return data?.PropertyTax?.UpdateNumber?.[0];
    },
    retry:false,
    enable:false
  });

  const { isLoading, isError, error, data } = Digit.Hooks.pt.usePropertySearch({ filters: { propertyIds, tenantId } }, { filters: { propertyIds, tenantId } });

  const { isLoading: auditDataLoading, isError: isAuditError, data: auditData } = Digit.Hooks.pt.usePropertySearch(
    {
      tenantId,
      filters: { propertyIds, audit: true },
    },
    {
      enabled: enableAudit,
      select: (d) =>
        d.Properties.filter((e) => e.status === "ACTIVE")?.sort((a, b) => b.auditDetails.lastModifiedTime - a.auditDetails.lastModifiedTime),
    }
  );

  const [popup, showPopup] = useState(false);
  const [billData, updateCanFetchBillData] = useState({
    loading: false,
    loaded: false,
    canLoad: false,
  });

  const [fetchBillData, updatefetchBillData] = useState({});

  const [property, setProperty] = useState(() => data?.Properties[0] || " ");
  const mutation = Digit.Hooks.pt.usePropertyAPI(property?.tenantId, false);

  let specialCategoryDoc = [];
  property?.documents?.filter(ob => ob.documentType.includes("SPECIALCATEGORYPROOF")).map((doc) => {
      specialCategoryDoc.push(doc);
  })
  
  useEffect(() => {
    if (data) {
      setProperty(data?.Properties[0]);
      if (data?.Properties[0]?.status !== "ACTIVE") setEnableAudit(true);
    }
  }, [data]);

  useEffect(() => {
    if (auditData?.[0]) {
      const property = auditData?.[0] || {};
      property.owners = property?.owners?.filter((owner) => owner.status == "ACTIVE");
      setProperty(property);
    }
  }, [enableAudit, auditData]);
const handleClick=()=>{

  setshowModal(true)
}
  sessionStorage.setItem("pt-property", JSON.stringify(property));
  let docs = [];
  docs = property?.documents;
  let units = [];
  let owners = [];
  owners = property?.owners;
  units = property?.units;
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

  if (isLoading) {
    return <Loader />;
  }

  if (property?.status == "ACTIVE" && !billData.loading && !billData.loaded && !billData.canLoad) {
    updateCanFetchBillData({
      loading: false,
      loaded: false,
      canLoad: true,
    });
  }
  if (billData?.canLoad && !billData.loading && !billData.loaded) {
    updateCanFetchBillData({
      loading: true,
      loaded: false,
      canLoad: true,
    });
    setBillData(property?.tenantId || tenantId, propertyIds, updatefetchBillData, updateCanFetchBillData);
  }

  let flrno,
    i = 0;
  flrno = units && units[0]?.floorNo;
  const ActionButton = ({ jumpTo }) => {
    const { t } = useTranslation();
    const navigate = Digit.Hooks.useCustomNavigate();
    function routeTo() {
      navigate(jumpTo);
    }
    return (
      <button type="button" className="pt-prop-owner-history-btn" onClick={routeTo}>
        {t("PT_OWNER_HISTORY")}
      </button>
    );
  };
  const UpdatePropertyNumberComponent = Digit?.ComponentRegistryService?.getComponent("UpdateNumber");
 
  const submitAction = async (data, nocData = false, isOBPS = {}) => {
      setIsEnableLoader(true);
      if (typeof data?.customFunctionToExecute === "function") {
        data?.customFunctionToExecute({ ...data });
      }
      if (nocData !== false && nocMutation) {
        const nocPrmomises = nocData?.map((noc) => {
          return nocMutation?.mutateAsync(noc);
        });
        try {
          setIsEnableLoader(true);
          const values = await Promise.all(nocPrmomises);
          values &&
            values.map((ob) => {
              Digit.SessionStorage.del(ob?.Noc?.[0]?.nocType);
            });
        } catch (err) {
          setIsEnableLoader(false);
          let errorValue = err?.response?.data?.Errors?.[0]?.code
            ? t(err?.response?.data?.Errors?.[0]?.code)
            : err?.response?.data?.Errors?.[0]?.message || err;
          closeModal();
          setShowToast({ key: "error", error: { message: errorValue } });
          setTimeout(closeToast, 5000);
          return;
        }
      }
      closeModal();
  };
  if (isLoading || isEnableLoader) {
    return <Loader />;
  }
  const closeModal = () => {
    setshowModal(false);
  };

  const closeWarningPopup = () => {
    setWarningPopUp(false);
  };
  const handleClickOnPtPgr = () => {
    sessionStorage.setItem("type", "PT");
    sessionStorage.setItem("pincode", data?.Properties?.[0]?.address?.pincode);
    sessionStorage.setItem("tenantId", data?.Properties?.[0]?.address?.tenantId);
    sessionStorage.setItem("localityCode", data?.Properties?.[0]?.address?.locality?.code);
    sessionStorage.setItem("landmark", data?.Properties?.[0]?.address?.landmark); 
    sessionStorage.setItem("propertyid", data?.Properties?.[0]?.propertyId);
    navigate(`/upyog-ui/citizen/pgr/create-complaint/complaint-type?propertyId=${property?.propertyId}`);
  };

  const statusClass = property?.status ? property.status.toLowerCase() : "active";

  return (
    <div className="pt-citizen-prop-details-wrapper">
      <Header className="pt-prop-page-header">{t("PT_PROPERTY_INFORMATION")}</Header>
      
      {/* 1. Hero Overview KPI Card */}
      <div className="pt-prop-hero-card">
        <div className="pt-prop-hero-header">
          <div className="pt-prop-id-section">
            <div className="pt-prop-id-label">{t("PT_PROPERTY_PTUID")}</div>
            <div className="pt-prop-id-value">{property?.propertyId || t("CS_NA")}</div>
          </div>
          <div className="pt-prop-status-wrap">
            <span className={`pt-prop-status-pill status-${statusClass}`}>
              {t(property?.status || "ACTIVE")}
            </span>
          </div>
        </div>

        <div className="pt-prop-hero-kpi-grid">
          <div className="pt-prop-kpi-item">
            <div className="pt-prop-kpi-label">{t("CS_COMMON_TOTAL_AMOUNT_DUE")}</div>
            <div className="pt-prop-kpi-val val-due">₹{t(getBillAmount(fetchBillData))}</div>
          </div>
          <div className="pt-prop-hero-actions">
            <Link
              to={`/upyog-ui/citizen/pt/payment-details/${property?.propertyId}`}
              className="pt-prop-view-payment-btn"
            >
              {t("PT_VIEW_PAYMENT")}
            </Link>
          </div>
        </div>

        {fetchBillData?.Bill?.[0] ? <ArrearSummary bill={fetchBillData.Bill[0]} /> : null}
      </div>

      {/* 2. Address Details Section Card */}
      <div className="pt-prop-section-card">
        <div className="pt-prop-section-header">
          <div className="pt-prop-section-title-wrap">
            <span className="pt-prop-section-accent-bar" />
            <h2 className="pt-prop-section-title">{t("PT_PROPERTY_ADDRESS_SUB_HEADER")}</h2>
          </div>
        </div>
        <div className="pt-prop-data-grid">
          <div className="pt-prop-data-item">
            <span className="pt-prop-data-label">{t("PT_PROPERTY_ADDRESS_PINCODE")}</span>
            <span className="pt-prop-data-value">{property.address?.pincode || t("CS_NA")}</span>
          </div>
          <div className="pt-prop-data-item">
            <span className="pt-prop-data-label">{t("PT_COMMON_CITY")}</span>
            <span className="pt-prop-data-value">{t(getCityLocale(property?.tenantId)) || t("CS_NA")}</span>
          </div>
          <div className="pt-prop-data-item">
            <span className="pt-prop-data-label">{t("PT_COMMON_LOCALITY_OR_MOHALLA")}</span>
            <span className="pt-prop-data-value">{t(property?.address?.locality?.name) || t("CS_NA")}</span>
          </div>
          <div className="pt-prop-data-item">
            <span className="pt-prop-data-label">{t("PT_PROPERTY_ADDRESS_STREET_NAME")}</span>
            <span className="pt-prop-data-value">{property.address?.street || t("CS_NA")}</span>
          </div>
          <div className="pt-prop-data-item">
            <span className="pt-prop-data-label">{t("PT_PROPERTY_ADDR_DOOR_HOUSE_NO")}</span>
            <span className="pt-prop-data-value">{property.address?.doorNo || t("CS_NA")}</span>
          </div>
        </div>
      </div>

      {/* 3. Assessment & Property Structure Details Section Card */}
      <div className="pt-prop-section-card">
        <div className="pt-prop-section-header">
          <div className="pt-prop-section-title-wrap">
            <span className="pt-prop-section-accent-bar" />
            <h2 className="pt-prop-section-title">{t("PT_PROPERTY_ASSESSMENT_DETAILS_HEADER")}</h2>
          </div>
        </div>
        <div className="pt-prop-data-grid">
          <div className="pt-prop-data-item">
            <span className="pt-prop-data-label">{t("PT_COMMON_PROPERTY_TYPE")}</span>
            <span className="pt-prop-data-value">{t(getPropertyTypeLocale(property?.propertyType)) || t("CS_NA")}</span>
          </div>
          <div className="pt-prop-data-item">
            <span className="pt-prop-data-label">{t("PT_ASSESMENT1_PLOT_SIZE")}</span>
            <span className="pt-prop-data-value">{property.landArea ? `${property.landArea} sq.ft` : t("CS_NA")}</span>
          </div>
          <div className="pt-prop-data-item">
            <span className="pt-prop-data-label">{t("PT_ASSESMENT_INFO_NO_OF_FLOOR")}</span>
            <span className="pt-prop-data-value">{property.noOfFloors || t("CS_NA")}</span>
          </div>
          <div className="pt-prop-data-item">
            <span className="pt-prop-data-label">{t("PT_ASSESSMENT1_ELECTRICITY")}</span>
            <span className="pt-prop-data-value">{property?.additionalDetails?.electricity || t("CS_NA")}</span>
          </div>
          <div className="pt-prop-data-item">
            <span className="pt-prop-data-label">{t("PT_ASSESSMENT1_UID")}</span>
            <span className="pt-prop-data-value">{property?.additionalDetails?.uid || t("CS_NA")}</span>
          </div>
        </div>
      </div>

      {/* 4. Floors & Units Details Section Card */}
      {Array.isArray(units) && units.length > 0 && (
        <div className="pt-prop-section-card">
          <div className="pt-prop-section-header">
            <div className="pt-prop-section-title-wrap">
              <span className="pt-prop-section-accent-bar" />
              <h2 className="pt-prop-section-title">{t("PT_ASSESSMENT_FLOOR_DETAILS_HEADER")}</h2>
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
                <div key={index} className="pt-prop-floor-group">
                  {isNewFloor && (
                    <div className="pt-prop-floor-title">
                      {t(`PROPERTYTAX_FLOOR_${unit?.floorNo}`)}
                    </div>
                  )}
                  <div className="pt-prop-unit-card">
                    <div className="pt-prop-unit-title">
                      {t("ES_APPLICATION_DETAILS_UNIT")} {i}
                    </div>
                    <div className="pt-prop-data-grid">
                      <div className="pt-prop-data-item">
                        <span className="pt-prop-data-label">{t("PT_ASSESSMENT_UNIT_USAGE_TYPE")}</span>
                        <span className="pt-prop-data-value">
                          {t(
                            (property.usageCategory !== "RESIDENTIAL" ? "COMMON_PROPUSGTYPE_NONRESIDENTIAL_" : "COMMON_PROPUSGTYPE_") +
                              (property?.usageCategory?.split(".")[1] ? property?.usageCategory?.split(".")[1] : property.usageCategory)
                          ) || t("CS_NA")}
                        </span>
                      </div>
                      <div className="pt-prop-data-item">
                        <span className="pt-prop-data-label">{t("PT_OCCUPANY_TYPE_LABEL")}</span>
                        <span className="pt-prop-data-value">{t("PROPERTYTAX_OCCUPANCYTYPE_" + unit?.occupancyType) || t("CS_NA")}</span>
                      </div>
                      <div className="pt-prop-data-item">
                        <span className="pt-prop-data-label">{t("PT_BUILTUP_AREA_LABEL")}</span>
                        <span className="pt-prop-data-value">{unit?.constructionDetail?.builtUpArea ? `${unit?.constructionDetail?.builtUpArea} sq.ft` : t("CS_NA")}</span>
                      </div>
                      {unit.occupancyType == "RENTED" && (
                        <div className="pt-prop-data-item">
                          <span className="pt-prop-data-label">{t("PT_FORM2_TOTAL_ANNUAL_RENT")}</span>
                          <span className="pt-prop-data-value">{unit?.arv ? `₹${unit?.arv}` : t("CS_NA")}</span>
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

      {/* 5. Ownership Details Section Card */}
      <div className="pt-prop-section-card">
        <div className="pt-prop-section-header">
          <div className="pt-prop-section-title-wrap">
            <span className="pt-prop-section-accent-bar" />
            <h2 className="pt-prop-section-title">{t("PT_COMMON_PROPERTY_OWNERSHIP_DETAILS_HEADER")}</h2>
          </div>
        </div>
        <div>
          {Array.isArray(owners) &&
            owners
              .sort((item, item2) => (item?.additionalDetails?.ownerSequence || 0) - (item2?.additionalDetails?.ownerSequence || 0))
              .map((owner, index) => (
                <div key={index} className="pt-prop-owner-card">
                  <div className="pt-prop-owner-header">
                    <span className="pt-prop-owner-badge">
                      {owners.length > 1 ? `${t("PT_OWNER_SUB_HEADER")} ${index + 1}` : t("PT_OWNER_DETAILS_LABEL")}
                    </span>
                    <ActionButton jumpTo={`/upyog-ui/citizen/pt/property/owner-history/${property.tenantId}/${property.propertyId}`} />
                  </div>
                  <div className="pt-prop-data-grid">
                    {property?.institution?.name && (
                      <div className="pt-prop-data-item">
                        <span className="pt-prop-data-label">{t("PT_INSTITUTION_NAME")}</span>
                        <span className="pt-prop-data-value">{property?.institution?.name}</span>
                      </div>
                    )}
                    {property?.institution?.type && (
                      <div className="pt-prop-data-item">
                        <span className="pt-prop-data-label">{t("PT_INSTITUTION_TYPE")}</span>
                        <span className="pt-prop-data-value">{t(`COMMON_MASTERS_OWNERSHIPCATEGORY_${property?.institution?.type}`) || t("CS_NA")}</span>
                      </div>
                    )}
                    <div className="pt-prop-data-item">
                      <span className="pt-prop-data-label">{t("PT_COMMON_APPLICANT_NAME_LABEL")}</span>
                      <span className="pt-prop-data-value">{owner?.name || t("CS_NA")}</span>
                    </div>
                    <div className="pt-prop-data-item">
                      <span className="pt-prop-data-label">{t("PT_COMMON_GENDER_LABEL")}</span>
                      <span className="pt-prop-data-value">{owner?.gender ? owner.gender.toLowerCase() : t("CS_NA")}</span>
                    </div>
                    <div className="pt-prop-data-item">
                      <span className="pt-prop-data-label">{t("PT_FORM3_MOBILE_NUMBER")}</span>
                      <span className="pt-prop-data-value">
                        {t(owner?.mobileNumber) || t("CS_NA")}
                        {property?.status === "ACTIVE" &&
                          owner?.mobileNumber &&
                          Digit.UserService.getUser()?.info?.mobileNumber &&
                          owner.mobileNumber === Digit.UserService.getUser()?.info?.mobileNumber && (
                            <span
                              className="pt-prop-edit-icon-wrap"
                              onClick={() => showPopup({ name: owner?.name, mobileNumber: owner?.mobileNumber, ownerIndex: index })}
                            >
                              <EditIcon />
                            </span>
                          )}
                      </span>
                    </div>
                    {property?.institution?.designation && (
                      <div className="pt-prop-data-item">
                        <span className="pt-prop-data-label">{t("Designation")}</span>
                        <span className="pt-prop-data-value">{property?.institution?.designation}</span>
                      </div>
                    )}
                    <div className="pt-prop-data-item">
                      <span className="pt-prop-data-label">{t("PT_FORM3_GUARDIAN_NAME")}</span>
                      <span className="pt-prop-data-value">{owner?.fatherOrHusbandName || t("CS_NA")}</span>
                    </div>
                    <div className="pt-prop-data-item">
                      <span className="pt-prop-data-label">{t("PT_FORM3_OWNERSHIP_TYPE")}</span>
                      <span className="pt-prop-data-value">
                        {property?.ownershipCategory ? t(`PT_OWNERSHIP_${property?.ownershipCategory}`) : t("CS_NA")}
                      </span>
                    </div>
                    <div className="pt-prop-data-item">
                      <span className="pt-prop-data-label">{t("PT_FORM3_RELATIONSHIP")}</span>
                      <span className="pt-prop-data-value">{owner?.relationship || t("CS_NA")}</span>
                    </div>
                    {specialCategoryDoc && specialCategoryDoc.length > 0 && (
                      <>
                        <div className="pt-prop-data-item">
                          <span className="pt-prop-data-label">{t("PT_SPL_CAT_DOC_TYPE")}</span>
                          <span className="pt-prop-data-value">{t(stringReplaceAll(specialCategoryDoc[index]?.documentType, ".", "_")) || t("NA")}</span>
                        </div>
                        <div className="pt-prop-data-item">
                          <span className="pt-prop-data-label">{t("PT_SPL_CAT_DOC_ID")}</span>
                          <span className="pt-prop-data-value">{t(specialCategoryDoc[index]?.id) || t("CS_NA")}</span>
                        </div>
                      </>
                    )}
                    <div className="pt-prop-data-item">
                      <span className="pt-prop-data-label">{t("PT_MUTATION_AUTHORISED_EMAIL")}</span>
                      <span className="pt-prop-data-value">{owner?.emailId || t("CS_NA")}</span>
                    </div>
                    <div className="pt-prop-data-item">
                      <span className="pt-prop-data-label">{t("PT_OWNERSHIP_INFO_CORR_ADDR")}</span>
                      <span className="pt-prop-data-value">{t(owner?.permanentAddress) || t("CS_NA")}</span>
                    </div>
                    {specialCategoryDoc?.length == 0 && (
                      <div className="pt-prop-data-item">
                        <span className="pt-prop-data-label">{t("PT_SPL_CAT")}</span>
                        <span className="pt-prop-data-value">{owner?.ownerType || t("CS_NA")}</span>
                      </div>
                    )}
                  </div>
                </div>
              ))}
        </div>
      </div>

      {/* 6. Documents Section Card */}
      <div className="pt-prop-section-card">
        <div className="pt-prop-section-header">
          <div className="pt-prop-section-title-wrap">
            <span className="pt-prop-section-accent-bar" />
            <h2 className="pt-prop-section-title">{t("PT_COMMON_DOCS")}</h2>
          </div>
        </div>
        <div>
          {Array.isArray(docs) && docs.length > 0 ? (
            <PropertyDocument property={property} />
          ) : (
            <div className="pt-prop-data-item">
              <span className="pt-prop-data-value">{t("PT_NO_DOCUMENTS_MSG")}</span>
            </div>
          )}
        </div>
      </div>

      {/* 7. Bottom Actions */}
      {property?.status === "ACTIVE" && !enableAudit && (
        <div className="pt-prop-bottom-actions">
          <div className="pt-prop-action-item">
            <button className="submit-bar btn-pgr" type="button" onClick={handleClickOnPtPgr}>
              {t("PT_PGR")}
            </button>
          </div>
          <div className="pt-prop-action-item">
            <Link to={{ pathname: `/upyog-ui/citizen/pt/property/edit-application/action=UPDATE/${property.propertyId}` }}>
              <button className="submit-bar btn-update" type="button">
                {t("PT_UPDATE_PROPERTY_BUTTON")}
              </button>
            </Link>
          </div>
          <div className="pt-prop-action-item">
            <button className="submit-bar btn-assess" type="button" onClick={handleClick}>
              {t("PT_SELF_ASSES_PROPERTY")}
            </button>
          </div>
        </div>
      )}

      {popup && (
        <PopUp className="updatenumber-warper-citizen">
          <UpdatePropertyNumberComponent
            showPopup={showPopup}
            name={popup?.name}
            UpdateNumberConfig={UpdateNumberConfig}
            mobileNumber={popup?.mobileNumber}
            t={t}
            onValidation={(data, showToast) => {
              let newProp = { ...property };
              newProp.owners[popup?.ownerIndex].mobileNumber = data.mobileNumber;
              newProp.creationReason = "UPDATE";
              newProp.workflow = null;
              mutation.mutate(
                {
                  Property: newProp,
                },
                {
                  onError: () => {},
                  onSuccess: async () => {
                    showToast();
                    setTimeout(() => {
                      window.location.reload();
                    }, 3000);
                  },
                }
              );
            }}
          />
        </PopUp>
      )}

      {showModal ? (
        <ActionModal
          t={t}
          action={selectedAction}
          tenantId={tenantId}
          state={state}
          id={property.propertyId}
          applicationDetails={property}
          applicationData={property}
          closeModal={closeModal}
          submitAction={submitAction}
          businessService={businessService}
          moduleCode={moduleCode}
        />
      ) : null}
    </div>
  );
};

export default PropertyInformation;
