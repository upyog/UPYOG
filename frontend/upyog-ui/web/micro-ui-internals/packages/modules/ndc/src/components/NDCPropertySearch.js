import "../../css/ndc.css";
import React, { useState, useEffect, useRef } from "react";

import { TextInput, Toast } from "@nudmcdgnpm/digit-ui-react-components";

import { useTranslation } from "react-i18next";

import { useDispatch, useSelector } from "react-redux";

import { updateNDCForm } from "../redux/actions/NDCFormActions";

import { useLocation, Link } from "react-router-dom";

import { Loader } from "../components/Loader";

/*
 * =========================================================
 * ADDRESS
 * =========================================================
 */

const getAddress = (address, t) => {
  return `${address?.doorNo ? `${address?.doorNo}, ` : ""} ${address?.street ? `${address?.street}, ` : ""}${
    address?.landmark ? `${address?.landmark}, ` : ""
  }${t(Digit.Utils.pt.getMohallaLocale(address?.locality?.code, address?.tenantId))}, ${t(Digit.Utils.pt.getCityLocale(address?.tenantId))}${
    address?.pincode && t(address?.pincode) ? `, ${address.pincode}` : " "
  }`;
};

/*
 * =========================================================
 * PROPERTY SEARCH / FETCH DETAILS
 * COMPONENT USED IN NDC
 * =========================================================
 */

export const PropertySearchNSummary = ({ config, onSelect, formData }) => {
  const { t } = useTranslation();

  const myElementRef = useRef(null);

  const dispatch = useDispatch();

  let { pathname, state } = useLocation();

  state = state && (typeof state === "string" || state instanceof String) ? JSON.parse(state) : state;

  /*
   * =========================================================
   * REDUX DATA
   * =========================================================
   */

  const apiDataCheck = useSelector((state) => state.ndc.NDCForm?.formData?.responseData);

  /*
   * =========================================================
   * EDIT SCREEN
   * =========================================================
   */

  const isEditScreen = pathname.includes("/modify-application/");

  /*
   * =========================================================
   * TENANT
   * =========================================================
   */

  const tenantId = Digit.ULBService.getCurrentTenantId();

  /*
   * =========================================================
   * URL PROPERTY ID
   * =========================================================
   */

  const search = useLocation().search;

  const urlPropertyId = new URLSearchParams(search).get("propertyId");

  const isfirstRender = useRef(true);

  /*
   * =========================================================
   * LOADER
   * =========================================================
   */

  const [getLoader, setLoader] = useState(false);

  /*
   * =========================================================
   * PROPERTY FROM API
   * =========================================================
   */

  const ptFromApi = apiDataCheck?.[0]?.NdcDetails?.find((item) => item.businessService === "PT");

  /*
   * =========================================================
   * PROPERTY ID
   * =========================================================
   */

  const [propertyId, setPropertyId] = useState(formData?.cpt?.id || (urlPropertyId !== "null" ? urlPropertyId : "") || ptFromApi?.consumerCode || "");

  const [searchPropertyId, setSearchPropertyId] = useState(
    formData?.cpt?.id || (urlPropertyId !== "null" ? urlPropertyId : "") || ptFromApi?.consumerCode || "",
  );

  /*
   * =========================================================
   * TOAST
   * =========================================================
   */

  const [showToast, setShowToast] = useState(null);

  /*
   * =========================================================
   * PROPERTY DETAILS
   * =========================================================
   */

  const [propertyDetails, setPropertyDetails] = useState(() => {
    if (formData?.cpt?.details && Object.keys(formData?.cpt?.details).length > 0) {
      return {
        Properties: [
          {
            ...formData?.cpt?.details,
          },
        ],
      };
    }

    return {
      Properties: [],
    };
  });

  /*
   * =========================================================
   * PROPERTY DUES
   * =========================================================
   */

  const [propertyDues, setPropertyDues] = useState(() => {
    if (formData?.cpt?.dues && Object.keys(formData?.cpt?.dues).length > 0) {
      return {
        dues: {
          ...formData?.cpt?.dues,
        },
      };
    }

    return {
      dues: {},
    };
  });

  /*
   * =========================================================
   * UI STATES
   * =========================================================
   */

  const [isSearchClicked, setIsSearchClicked] = useState(false);

  const [getNoDue, setNoDue] = useState(false);

  const [getCheckStatus, setCheckStats] = useState(false);

  const [getPayDuesButton, setPayDuesButton] = useState(false);

  /*
   * =========================================================
   * PROPERTY SEARCH API
   * =========================================================
   */

  const {
    isLoading,
    isError,
    error,
    data: propertyDetailsFetch,
  } = Digit.Hooks.pt.usePropertySearch(
    {
      filters: {
        propertyIds: searchPropertyId,
      },

      tenantId: tenantId,
    },

    {
      filters: {
        propertyIds: searchPropertyId,
      },

      tenantId: tenantId,

      enabled: searchPropertyId ? true : false,

      privacy: Digit.Utils.getPrivacyObject(),
    },
  );

  /*
   * =========================================================
   * PROPERTY DATA FROM API
   * =========================================================
   */

  useEffect(() => {
    if (ptFromApi?.consumerCode) {
      setIsSearchClicked(true);

      setPropertyId(ptFromApi.consumerCode);

      setSearchPropertyId(ptFromApi.consumerCode);

      setNoDue(true);

      setPropertyDues({
        dues: {
          totalAmount: 0,
        },
      });

      const updated = {
        ...formData[config.key],

        id: ptFromApi.consumerCode,
      };

      onSelect(config.key, updated);

      dispatch(updateNDCForm(config.key, updated));
    }
  }, [ptFromApi]);

  /*
   * =========================================================
   * PROPERTY DETAILS FETCH
   * =========================================================
   */

  useEffect(() => {
    if (propertyDetailsFetch && propertyDetailsFetch?.Properties && propertyDetailsFetch?.Properties?.length > 0) {
      setPropertyDetails(propertyDetailsFetch);

      setShowToast(null);

      setCheckStats(true);
    } else {
      if (isfirstRender.current) {
        isfirstRender.current = false;

        return;
      }

      if (!formData?.cpt?.details && isSearchClicked) {
        setPropertyDetails({
          Properties: [],
        });

        setShowToast({
          error: true,

          label: "CS_PT_NO_PROPERTIES_FOUND",
        });
      }
    }
  }, [propertyDetailsFetch]);

  /*
   * =========================================================
   * UPDATE SEARCH PROPERTY ID
   * =========================================================
   */

  useEffect(() => {
    if (propertyId && (window.location.href.includes("/renew-application-details/") || window.location.href.includes("/edit-application-details/"))) {
      setSearchPropertyId(propertyId);
    }
  }, [propertyId]);

  /*
   * =========================================================
   * PROPERTY SEARCH ERROR
   * =========================================================
   */

  useEffect(() => {
    if (isLoading === false && error && error === true && propertyDetails?.Properties?.length === 0) {
      setShowToast({
        error: true,

        label: "CS_PT_NO_PROPERTIES_FOUND",
      });
    }
  }, [error, propertyDetails, isLoading]);

  /*
   * =========================================================
   * UPDATE PROPERTY DETAILS
   * =========================================================
   */

  useEffect(() => {
    const updated = {
      ...formData[config.key],

      details: propertyDetails?.Properties?.[0],
    };

    onSelect(config.key, updated);

    dispatch(updateNDCForm(config.key, updated));
  }, [propertyDetails, pathname]);

  /*
   * =========================================================
   * UPDATE PROPERTY DUES
   * =========================================================
   */

  useEffect(() => {
    const updated = {
      ...formData[config.key],

      dues: propertyDues?.dues,
    };

    onSelect(config.key, updated);

    dispatch(updateNDCForm(config.key, updated));
  }, [propertyDues, pathname]);

  /*
   * =========================================================
   * SEARCH PROPERTY
   * =========================================================
   */

  const searchProperty = () => {
    if (!propertyId) {
      setShowToast({
        error: true,

        label: "PT_ENTER_PROPERTY_ID_AND_SEARCH",
      });

      return;
    }

    if (propertyId !== searchPropertyId) {
      setPropertyDetails({
        Properties: [],
      });

      setSearchPropertyId(propertyId);

      setIsSearchClicked(true);

      setPropertyDues({
        dues: null,
      });

      onSelect("PropertyDetails", {
        email: "",

        propertyBillData: {
          isLoading: false,
          billData: {},
        },

        waterConnection: [],

        sewerageConnection: [],

        firstName: "",

        mobileNumber: "",

        address: "",
      });
    }
  };

  /*
   * =========================================================
   * PROPERTY ID CHANGE
   * =========================================================
   */

  const handlePropertyChange = (e) => {
    const value = e.target.value;

    setPropertyId(value);

    setValue(value, propertyIdInput.name);

    setIsSearchClicked(false);

    setNoDue(false);

    setCheckStats(false);

    setPayDuesButton(false);
  };

  /*
   * =========================================================
   * EDIT SCREEN
   * =========================================================
   */

  if (isEditScreen) {
    return <React.Fragment />;
  }

  /*
   * =========================================================
   * PROPERTY ADDRESS
   * =========================================================
   */

  let propertyAddress = "";

  if (propertyDetails && propertyDetails?.Properties?.length) {
    propertyAddress = getAddress(propertyDetails?.Properties?.[0]?.address, t);
  }

  /*
   * =========================================================
   * PROPERTY INPUT CONFIG
   * =========================================================
   */

  const propertyIdInput = {
    label: "PROPERTY_ID",

    type: "text",

    name: "id",
  };

  /*
   * =========================================================
   * SET VALUE
   * =========================================================
   */

  function setValue(value, input) {
    const updated = {
      ...formData[config.key],

      [input]: value,
    };

    onSelect(config.key, updated);

    dispatch(updateNDCForm(config.key, updated));
  }

  /*
   * =========================================================
   * GET VALUE
   * =========================================================
   */

  function getValue(input) {
    return formData && formData[config.key] ? formData[config.key][input] : undefined;
  }

  /*
   * =========================================================
   * FETCH PROPERTY BILL
   * =========================================================
   */

  async function fetchBill() {
    setLoader(true);

    try {
      const result = await Digit.PaymentService.fetchBill(tenantId, {
        businessService: "PT",

        consumerCode: propertyId,
      });

      if (result?.Bill?.length > 0) {
        if (result?.Bill?.[0]?.totalAmount > 0) {
          setShowToast({
            error: true,

            label: t("NDC_MESSAGE_DUES_FOUND_PLEASE_PAY"),
          });

          setPayDuesButton(true);

          setNoDue(false);
        } else {
          setShowToast({
            error: false,

            label: t("NDC_NO_BILLS_FOUND_PROPERTY"),
          });

          setNoDue(true);

          setCheckStats(false);

          setPayDuesButton(false);
        }

        setPropertyDues({
          dues: result?.Bill?.[0],
        });
      } else if (result?.Bill) {
        setShowToast({
          error: false,

          label: t("NDC_NO_BILLS_FOUND_PROPERTY"),
        });

        setPropertyDues({
          dues: {
            totalAmount: 0,
          },
        });

        setNoDue(true);

        setCheckStats(false);

        setPayDuesButton(false);
      } else {
        setShowToast({
          error: false,

          label: t("NDC_NO_BILLS_FOUND_PROPERTY"),
        });

        setPropertyDues({
          dues: {
            totalAmount: 0,
          },
        });

        setNoDue(true);

        setCheckStats(false);

        setPayDuesButton(false);
      }

      setLoader(false);
    } catch (error) {
      console.error("Error while fetching property bill:", error);

      setLoader(false);

      setShowToast({
        error: true,

        label: t("NDC_MESSAGE_FETCH_FAILED"),
      });
    }
  }

  /*
   * =========================================================
   * AUTO CLOSE TOAST
   * =========================================================
   */

  useEffect(() => {
    if (!showToast) {
      return;
    }

    const timer = setTimeout(() => {
      setShowToast(null);
    }, 3000);

    return () => clearTimeout(timer);
  }, [showToast]);

  /*
   * =========================================================
   * NDC LAYOUT
   * =========================================================
   */

  const FIELD_WIDTH = "340px";

  const FIELD_HEIGHT = "40px";

  /*
   * ---------------------------------------------------------
   * FIELD WRAPPER
   * ---------------------------------------------------------
   */

  /*
   * ---------------------------------------------------------
   * LABEL
   * ---------------------------------------------------------
   */

  /*
   * ---------------------------------------------------------
   * INPUT ONLY WRAPPER
   *
   * IMPORTANT:
   * ONLY THE INPUT + SEARCH ICON LIVE HERE.
   *
   * Buttons are intentionally OUTSIDE this wrapper.
   * ---------------------------------------------------------
   */

  /*
   * ---------------------------------------------------------
   * INPUT
   * ---------------------------------------------------------
   */

  /*
   * ---------------------------------------------------------
   * SEARCH ICON
   * ---------------------------------------------------------
   */

  /*
   * ---------------------------------------------------------
   * ACTION AREA
   *
   * THIS IS THE IMPORTANT FIX.
   *
   * It has its own height/spacing and is outside the
   * 40px input wrapper.
   * ---------------------------------------------------------
   */

  /*
   * ---------------------------------------------------------
   * BUTTON
   * ---------------------------------------------------------
   */

  /*
   * ---------------------------------------------------------
   * NO DUES
   * ---------------------------------------------------------
   */

  /*
   * =========================================================
   * RENDER
   * =========================================================
   */

  return (
    <React.Fragment>
<div
        className="ndc-property-search-container ndc-margin-bottom-16 ndc-css-80d9af92"
        
      >
        {/* ===================================================
            PROPERTY ID
        ==================================================== */}

        <div className="ndc-css-e4092fcf" >
          <label className="ndc-css-690dab27" >
            {t(propertyIdInput.label)}

            <span className="ndc-css-38096d7a"
              
            >
              *
            </span>
          </label>

          {/* =================================================
              INPUT + SEARCH ICON ONLY
              
              IMPORTANT:
              DO NOT PUT BUTTONS INSIDE THIS DIV.
          ================================================= */}

          <div ref={myElementRef} id="search-property-field" className="ndc-property-input ndc-css-f8d9d5bb" >
            <TextInput className="ndc-css-fadab511"
              key={propertyIdInput.name}
              value={propertyId || ""}
              onChange={handlePropertyChange}
              disable={false}
              defaultValue={undefined}
              {...propertyIdInput.validation}
              
            />

            {/* ===============================================
                SEARCH ICON
            ================================================ */}

            <button
              type="button"
              className="ndc-property-search-icon ndc-css-27db4f2f"
              
              onClick={searchProperty}
              aria-label={t("PT_SEARCH")}
              title={t("PT_SEARCH")}
            >
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="10.8" cy="10.8" r="6.6" stroke="#555555" strokeWidth="2" />

                <path d="M16 16L21 21" stroke="#555555" strokeWidth="2" strokeLinecap="round" />
              </svg>
            </button>
          </div>

          {/* =================================================
              ACTIONS ARE OUTSIDE INPUT WRAPPER
              
              THIS FIXES THE OVERLAP.
          ================================================= */}

          <div className="ndc-property-actions ndc-css-c1711082" >
            {/* ===============================================
                CHECK STATUS
            ================================================ */}

            {!apiDataCheck?.[0]?.NdcDetails && getCheckStatus && !getPayDuesButton && (
              <button
                type="button"
                className="ndc-property-action-button ndc-css-a999e0c4"
                
                onClick={() => {
                  fetchBill();
                }}
              >
                {t("CHECK_STATUS_PROPERTY")}
              </button>
            )}

            {/* ===============================================
                PAY DUES AMOUNT
            ================================================ */}

            {getPayDuesButton && (
              <div className="ndc-css-a999e0c4"
                
              >
                <span className="ndc-css-577ca9f3"
                  
                >
                  Rs. {propertyDues?.dues?.totalAmount}
                </span>
              </div>
            )}

            {/* ===============================================
                PAY DUES
            ================================================ */}

            {getPayDuesButton && (
              <Link className="ndc-property-pay-link" to={`/upyog-ui/citizen/payment/my-bills/PT/${propertyId}`}>
                <button
                  type="button"
                  className="ndc-property-action-button ndc-css-985bd392"
                  
                >
                  {t("PAY_DUES")}
                </button>
              </Link>
            )}

            {/* ===============================================
                NO DUES
            ================================================ */}

            {getNoDue && (
              <div className="ndc-property-no-dues ndc-css-f93c4492" >
                <span className="ndc-css-577ca9f3"
                  
                >
                  {t("NO_DUES_FOUND_FOR_PROPERTY")}
                </span>
              </div>
            )}
          </div>
        </div>

        {/* ===================================================
            TOAST
        ==================================================== */}

        {showToast && (
          <Toast
            isDleteBtn={true}
            labelClassName="ndc-label-width-100"
            error={showToast.error}
            warning={showToast.warning}
            label={t(showToast.label)}
            onClose={() => {
              setShowToast(null);
            }}
          />
        )}
      </div>

      {/* =====================================================
          LOADER
      ===================================================== */}

      {(isLoading || getLoader) && <Loader page={true} />}
    </React.Fragment>
  );
};

export default PropertySearchNSummary;
