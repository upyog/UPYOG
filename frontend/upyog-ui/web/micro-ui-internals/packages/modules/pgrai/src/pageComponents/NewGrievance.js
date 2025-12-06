import React, { useEffect, useState, Fragment } from "react";
import { FormStep, TextInput, CardLabel, Loader, CardLabelError, LabelFieldPair, SubmitBar, TextArea } from "@upyog/digit-ui-react-components";
import { fetchCurrentLocation, reverseGeocode } from "../components/locationUtils";
import { fetchGrievanceCategories } from "../utils/index";
import { styles } from "../utils/styles";
import PhotoUpload from "../components/Document";
import AddressPopup from "../components/AddressPopup";
import Dropdown from "../components/Dropdown";

const NewGrievance = ({ t, config, onSelect, userType, formData }) => {
  const [name, setName] = useState(formData?.name || "");
  const [phoneNumber, setPhoneNumber] = useState(formData?.phoneNumber || "");
  const [grievanceText, setGrievanceText] = useState("");
  const [grievanceType, setGrievanceType] = useState("");
  const [grievanceSubType, setGrievanceSubType] = useState("");
  const [location, setLocation] = useState(formData?.location || "");
  const [address, setAddress] = useState(formData?.address || "");
  const [locationError, setLocationError] = useState(null);
  const [isFetchingLocation, setIsFetchingLocation] = useState(false);
  const [isFetchingAddress, setIsFetchingAddress] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [apiError, setApiError] = useState(null);
  const [addressDetails, setAddressDetails] = useState(formData?.addressDetails || {});
  const [documents, setDocuments] = useState(formData?.documents || {});
  const [showAddressPopup, setShowAddressPopup] = useState(false);
  const [suggestions, setSuggestions] = useState([]);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [geoLocation, setGeoLocation] = useState(formData?.geoLocation || {});
  const [verificationDocuments, setVerificationDocuments] = useState();
  const [menuPaths, setMenuPaths] = useState([]);
  const [servicesByMenuPath, setServicesByMenuPath] = useState({});
  const user = Digit.UserService.getUser().info;
  const tenantId = Digit.ULBService.getStateId();
  // custom hook to fetch type and sub type  from MDMS
  const { data: type } = Digit.Hooks.useCustomMDMS(tenantId, "RAINMAKER-PGR", [{ name: "ServiceDefs" }], {
    select: (data) => {
      const formattedData = data?.["RAINMAKER-PGR"]?.["ServiceDefs"];
      return formattedData;
    },
  });

  const goNext = () => {

    const formStepData = {
      // Adds employee-specific fields if the user type is "EMPLOYEE".
      ...(user.type === "EMPLOYEE" && {
        name,
        phoneNumber: phoneNumber,
        citizenName: name,
        mobileNumber: phoneNumber,
        citizenMobile: phoneNumber
      }),
      grievance: grievanceText,
      grievanceType,
      grievanceSubType,
      documents,
      address,
      addressDetails,
      verificationDocuments,
      geoLocation
    };

    if (userType === "citizen") {
      onSelect(config.key, { ...formData[config.key], ...formStepData }, false, 0);
    } else {
      onSelect(config.key, formStepData, false, 0);
    }
  };

  const handleLocationChange = (e) => {
    setLocation(e.target.value);
    if (locationError) setLocationError(null);
  };

  const handlePhoneNumberChange = (e) => {
    const value = e.target.value;
    setPhoneNumber(value);
  };


  const handleFetchLocation = async () => {
    setIsFetchingLocation(true);
    setLocationError(null);

    try {
      const { coords, latitude, longitude } = await fetchCurrentLocation(t);
      setLocation(coords);
      setGeoLocation({ latitude:latitude, longitude:longitude, additionalDetails: {} });

      setIsFetchingAddress(true);
      try {
        const { formatted, addressDetails } = await reverseGeocode(latitude, longitude, t);
        setAddressDetails(addressDetails);
        setAddress(formatted);
      } catch (error) {
        console.error(error.message);
        setAddress(t("PGR_ADDRESS_NOT_AVAILABLE"));
      }
      setIsFetchingAddress(false);
    } catch (error) {
      setLocationError(error.error);
    } finally {
      setIsFetchingLocation(false);
    }
  };

  const handleAddAddressClick = () => {
    setShowAddressPopup(true);
  };

  const handleAddressSubmit = (addressData) => {
    if (addressData) {
      const addressDetails = {
        houseNo: addressData.houseNo,
        streetName: addressData.streetName,
        locality: {
          code:addressData.locality?.code,
          name:addressData.locality?.i18nKey
        },
        city: {
          name:addressData.city?.name,
          district: addressData.city?.code?.split(".")[0]
        },
        pincode: addressData.pincode,
        landmark: addressData.landmark,
        addressLine1: addressData.addressLine1,
        addressLine2: addressData.addressLine2,
      };
      setAddressDetails(addressDetails);
      const formattedAddress = [
        addressData.houseNo,
        addressData.streetName,
        addressData.locality?.name || addressData.locality?.i18nKey,
        addressData.city?.name || addressData.city,
        addressData.pincode,
      ]
        .filter(item => item && typeof item === 'string')
        .join(", ");
      setAddress(formattedAddress);
    }
    setShowAddressPopup(false);
  };

  const handlePhotoUpload = (key, documentStep) => {
    setDocuments(documentStep);
    if (userType === "citizen") {
      goNext();
    }
  };
 /**
   * Handles fetching grievance categories based on the provided prompt.
   * It updates the suggestions list and manages the loading state.
   * If the API call fails, it sets an error message and clears suggestions.
   */
  const fetchGrievanceCategoriesHandler = async (prompt) => {
    setIsLoading(true);
    setApiError(null);
    try {
      const data = await fetchGrievanceCategories(prompt, t);
      if (data && data.length > 0) {
        setSuggestions(data);
        setShowSuggestions(true);
      } else {
        setSuggestions([]);
        setShowSuggestions(false);
        setApiError(t("PGR_NO_SUGGESTIONS_FOUND"));
      }
    } catch (error) {
      console.error("API Error:", error);
      setApiError(t("PGR_FAILED_TO_FETCH_SUGGESTIONS"));
      setSuggestions([]);
      setShowSuggestions(false);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSuggestionSelect = (suggestion) => {
    setGrievanceType(suggestion.type || "");
    setGrievanceSubType(suggestion.subtype || "");
    setShowSuggestions(false);
  };

  const handleInputBlur = () => {
    setTimeout(() => {
      setShowSuggestions(false);
    }, 200);
  };

  const handleInputFocus = () => {
    if (suggestions.length > 0) {
      setShowSuggestions(true);
    }
  };

  // Process service definitions to extract unique grievance type and organize services by sub type
  useEffect(() => {
    if (type && type.length > 0) {
      // Extract unique menu paths for Grievance Type dropdown
      const uniqueMenuPaths = [...new Set(type.filter(item => item.menuPath && item.active).map(item => item.menuPath))].sort();
      setMenuPaths(uniqueMenuPaths);

      // Organize services by menu path for Sub Type dropdown
      const serviceMap = {};
      uniqueMenuPaths.forEach(path => {
        serviceMap[path] = type.filter(item => item.menuPath === path && item.active)
          .sort((a, b) => (a.order || 999) - (b.order || 999));
      });
      setServicesByMenuPath(serviceMap);
    }
  }, [type]);

  useEffect(() => {
    // Store the current text value for comparison
    const currentText = grievanceText;
    const debounceTimer = setTimeout(() => {
      // Only fetch new suggestions if the text hasn't changed since the timeout started
      if (currentText.trim() !== "" && currentText === grievanceText) {
        fetchGrievanceCategoriesHandler(currentText);
      } else if (currentText.trim() === "") {
        setSuggestions([]);
        setShowSuggestions(false);
      }
    }, 1000);

    return () => clearTimeout(debounceTimer);
  }, [grievanceText]);

  const onSkip = () => {
    onSelect();
  };

  useEffect(() => {
    if (userType === "citizen") goNext();
  }, [grievanceText, grievanceType, grievanceSubType, location, documents, address,
    ...(user.type === "EMPLOYEE" ? [name, phoneNumber] : [])]);

  return (
    <Fragment>
      <FormStep config={config} onSelect={goNext} t={t} onSkip={onSkip}
        isDisabled={!grievanceText || !grievanceType || !grievanceSubType ||
          (user.type === "EMPLOYEE" && (!name || !phoneNumber))}
        style={styles.submitBarStyle}
      >
        {user.type === "EMPLOYEE" && (// Employee-specific fields for name and phone number
          <>
            <CardLabel style={styles.cardLabelStyle}>
              {`${t("PGR_NAME")}`} <span style={styles.requiredAsterisk}>*</span>
            </CardLabel>
            <TextInput
              t={t}
              type="text"
              isMandatory={false}
              name="name"
              placeholder={t("PGR_AI_NAME")}
              value={name}
              style={{ width: "45%", ...styles.textInputStyle }}
              onChange={(e) => setName(e.target.value)}
            />
          </>
        )}

        {/* Phone Number Field - Only shown for EMPLOYEE users */}
        {user.type === "EMPLOYEE" && (
          <>
            <CardLabel style={styles.cardLabelStyle}>
              {`${t("PGR_PHONE_NUMBER")}`} <span style={styles.requiredAsterisk}>*</span>
            </CardLabel>
            <TextInput
              t={t}
              type="tel"
              isMandatory={false}
              name="phoneNumber"
              placeholder={t("PGR_AI_PHONE_NUMBER")}
              value={phoneNumber}
              style={{ width: "45%", ...styles.textInputStyle }}
              onChange={handlePhoneNumberChange}
            />
          </>
        )}
        <CardLabel style={styles.cardLabelStyle}>
          {`${t("PGR_AI_INPUT_GRIEVANCE")}`} <span style={styles.requiredAsterisk}>*</span>
        </CardLabel>
        <div style={{
          color: "#902434",
          fontSize: "14px",
          marginBottom: "8px",
          fontStyle: "italic",
          width: user.type === "EMPLOYEE" ? "45%" : "75%"
        }}>
          {t("START_TYPING_TO_GET_SUGGESTIONS")}
        </div>
        <div style={{ position: "relative" }}>
          <TextArea
            t={t}
            type="text"
            isMandatory={false}
            name="grievance"
            value={grievanceText}
            placeholder={t("PGR_AI_GRIEVANCE_PLACEHOLDER")}
            style={{ width: user.type === "EMPLOYEE" ? "45%" : "44%", ...styles.textInputStyle }}
            onChange={(e) => {
              const newValue = e.target.value;
              setGrievanceText(newValue);
              // Clear suggestions if text changes after suggestions were shown
              if (showSuggestions && suggestions.length > 0) {
                setSuggestions([]);
                setShowSuggestions(false);
              }
            }}
            onFocus={handleInputFocus}
            onBlur={handleInputBlur}
          />

          {showSuggestions && suggestions.length > 0 && (
            <div style={{ ...styles.suggestionContainer, width: "45%" }}>
              {suggestions.map((suggestion, index) => (
                <div
                  key={suggestion.id || index}
                  onClick={() => handleSuggestionSelect(suggestion)}
                  style={index < suggestions.length - 1 ? styles.suggestionItem : { ...styles.suggestionItem, ...styles.lastSuggestionItem }}
                >
                  <div style={styles.suggestionTitle}>{suggestion.type}</div>
                  <div style={styles.suggestionSubtype}>{suggestion.subtype}</div>
                </div>
              ))}
            </div>
          )}
        </div>

        {isLoading && (
          <div style={styles.loaderContainer}>
            <div className="spinner" style={styles.customLoader}></div>
            <span style={styles.loadingText}>{t("PGR_FINDING_SUGGESTIONS")}</span>
          </div>
        )}
        {apiError && <div style={styles.errorMessage}>{apiError}</div>}

        <CardLabel style={styles.cardLabelStyle}>
          {`${t("PGR_AI_GRIEVANCE_TYPE")}`} <span style={styles.requiredAsterisk}>*</span>
        </CardLabel>
        <Dropdown
          t={t}
          option={menuPaths.map(path => ({ name: path, code: path }))}
          selected={menuPaths.find(path => path === grievanceType) ? { name: grievanceType, code: grievanceType } : ""}
          select={(value) => {
            if (value) {
              setGrievanceType(value.code);
              setGrievanceSubType("");
            }
          }}
          optionKey="name"
          style={{
            width: user.type === "EMPLOYEE" ? "45%" : "75%",
            ...styles.textInputStyle
          }}
          placeholder={t("PGR_AI_GRIEVANCE_TYPE_PLACEHOLDER")}
          disable={isLoading || menuPaths.length === 0}
        />

        <CardLabel style={styles.cardLabelStyle}>
          {`${t("PGR_AI_GRIEVANCE_SUB_TYPE")}`} <span style={styles.requiredAsterisk}>*</span>
        </CardLabel>
        <Dropdown
          t={t}
          option={(servicesByMenuPath[grievanceType] || []).map(service => ({
            name: service.name,
            code: service.serviceCode
          }))}
          selected={
            grievanceSubType && servicesByMenuPath[grievanceType] ?
              servicesByMenuPath[grievanceType]
                .filter(service => service.serviceCode === grievanceSubType)
                .map(service => ({ name: service.name, code: service.serviceCode }))[0] || ""
              : ""
          }
          select={(value) => {
            if (value) {
              setGrievanceSubType(value.code);
            }
          }}
          optionKey="name"
          style={{
            width: user.type === "EMPLOYEE" ? "45%" : "75%",
            ...styles.textInputStyle
          }}
          placeholder={t("PGR_AI_GRIEVANCE_SUB_TYPE_PLACEHOLDER")}
          disable={isLoading || !grievanceType}
        />

        <CardLabel style={styles.cardLabelStyle}>
          {`${t("PGR_AI_GRIEVANCE_LOCATION")}`} <span style={styles.requiredAsterisk}>*</span>
        </CardLabel>
        <div className="field">
          {/* Text input for entering or displaying the location. 
      The width dynamically adjusts based on the user type (EMPLOYEE or others). */}
          <TextInput
            t={t}
            value={location}
            placeholder={t("PGR_AI_GRIEVANCE_LOCATION_PLACEHOLDER")}
            onChange={handleLocationChange}
            style={{ paddingRight: "30px", width: user.type === "EMPLOYEE" ? "45%" : "75%", ...styles.textInputStyle }}
          />
          {/* Icon for fetching the user's current location. 
      Clicking this triggers the handleFetchLocation function. */}
          <button
            style={styles.locationButton}
            onClick={handleFetchLocation}
            type="button" >
            <svg width="22" height="20" viewBox="0 0 24 24" style={styles.locationIcon}>
              <path d="M12,2 C12.3796958,2 12.693491,2.28215388 12.7431534,2.64822944 L12.75,2.75 L12.7490685,4.53770881 C16.292814,4.88757432 19.1124257,7.70718602 19.4632195,11.2525316 L19.5,11.25 L21.25,11.25 C21.6642136,11.25 22,11.5857864 22,12 C22,12.3796958 21.7178461,12.693491 21.3517706,12.7431534 L21.25,12.75 L19.4616558,12.7490368 C19.1124257,16.292814 16.292814,19.1124257 12.7474684,19.4632195 L12.75,19.5 L12.75,21.25 C12.75,21.6642136 12.4142136,22 12,22 C11.6203042,22 11.306509,21.7178461 11.2568466,21.3517706 L11.25,21.25 L11.2509632,19.4616558 C7.70718602,19.1124257 4.88757432,16.292814 4.53678051,12.7474684 L4.5,12.75 L2.75,12.75 C2.33578644,12.75 2,12.4142136 2,12 C2,11.6203042 2.28215388,11.306509 2.64822944,11.2568466 L2.75,11.25 L4.53770881,11.2509315 C4.88757432,7.70718602 7.70718602,4.88757432 11.2525316,4.53678051 L11.25,4.5 L11.25,2.75 C11.25,2.33578644 11.5857864,2 12,2 Z M12,6 C8.6862915,6 6,8.6862915 6,12 C6,15.3137085 8.6862915,18 12,18 C15.3137085,18 18,15.3137085 18,12 C18,8.6862915 15.3137085,6 12,6 Z M12,8 C14.209139,8 16,9.790861 16,12 C16,14.209139 14.209139,16 12,16 C9.790861,16 8,14.209139 8,12 C8,9.790861 9.790861,8 12,8 Z" />
            </svg>
            {t("PGR_CLICK_TO_GET_LOCATION")}
          </button>
        </div>
        <div style={styles.orDividerContainer}>
          <span style={styles.orDivider}>{t("PGR_OR")}</span>
        </div>
        <button
          style={styles.locationButton}
          onClick={handleAddAddressClick}
          type="button"
        >
          {t("PGR_ADD_ADDRESS_DETAILS")}
        </button>
        {showAddressPopup && (
          <AddressPopup t={t} isOpen={showAddressPopup} onClose={() => setShowAddressPopup(false)} onSubmit={handleAddressSubmit} />

        )}
        <LabelFieldPair style={{ marginTop: "16px" }}>
          <CardLabel style={styles.cardLabelStyle}>{`${t("PGR_AI_LANDMARK")}`} <span style={styles.requiredAsterisk}>*</span></CardLabel>
        </LabelFieldPair>
        <TextInput
          t={t}
          type="text"
          name="landmark"
          placeholder={t("PGR_AI_LANDMARK_PLACEHOLDER")}
          style={{
            width: user.type === "EMPLOYEE" ? "45%" : "75%",
            ...styles.textInputStyle
          }}
          value={addressDetails.landmark || ""}
          onChange={(e) => setAddressDetails({ ...addressDetails, landmark: e.target.value })}
          disabled={isLoading}
        />
        {locationError && <CardLabelError>{locationError}</CardLabelError>}

        {address && (
       <div>
            <CardLabel style={styles.cardLabelStyle}>{`${t("PGR_AI_ADDRESS")}`}</CardLabel>
            <div className="field">
              <TextInput t={t} value={address}readOnly style={{ ...styles.readOnlyInput,width: user.type === "EMPLOYEE" ? "45%" : "75%",   ...styles.textInputStyle  }} />
            </div>
          </div>
        )}

        <PhotoUpload t={t} config={{ key: "documents" }} onSelect={handlePhotoUpload} formData={{ documents }} setDocumentUploaded={setVerificationDocuments} />

      </FormStep>
    </Fragment>
  );
};

export default NewGrievance;

