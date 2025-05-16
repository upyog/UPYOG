import React, { useEffect, useState, Fragment } from "react";
import { FormStep, TextInput, CardLabel, Loader, CardLabelError, LabelFieldPair, ActionBar, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import { LocationIcon } from "@nudmcdgnpm/digit-ui-react-components";
import { fetchCurrentLocation, reverseGeocode } from "../components/locationUtils";
import { fetchGrievanceCategories } from "../utils/index";
import PhotoUpload from "../components/Document";
import AddressPopup from "../components/AddressPopup";

const styles = {
  suggestionContainer: {
    position: "absolute",
    top: "60%",
    left: 0,
    right: 0,
    backgroundColor: "#fff",
    border: "1px solid #ccc",
    borderRadius: "4px",
    zIndex: 10,
    maxHeight: "200px",
    overflowY: "auto",
    boxShadow: "0 2px 4px rgba(0,0,0,0.1)",
    width: "58.5%"
  },
  suggestionItem: {
    padding: "10px 12px",
    cursor: "pointer",
    borderBottom: "1px solid #eee",
    transition: "background-color 0.2s"
  },
  lastSuggestionItem: {
    borderBottom: "none"
  },
  suggestionSubtype: {
    fontSize: "0.9em",
    color: "#666"
  },
  locationField: {
    display: "flex",
    position: "relative"
  },
  locationIcon: {
    position: "relative",
    left: "8px",
    cursor: "pointer"
  },
  readOnlyInput: {
    color: "#666"
  },
  errorMessage: {
    color: "red",
    margin: "10px 0"
  }
};

const NewGrievance = ({ t, config, onSelect, userType, formData }) => {
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

  const user = Digit.UserService.getUser().info;

  const goNext = () => {
    
    const formStepData = {
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
        setAddress("Address not available");
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
          district:addressData.city?.code.split(".")[0]
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
        addressData.locality?.name || addressData.locality,
        addressData.city?.name || addressData.city,
        addressData.pincode,
      ]
        .filter(Boolean)
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
      }
    } catch (error) {
      console.error("API Error:", error);
      setApiError("Failed to fetch suggestions. Please try again.");
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

  useEffect(() => {
    const debounceTimer = setTimeout(() => {
      if (grievanceText.trim() !== "") {
        fetchGrievanceCategoriesHandler(grievanceText);
      } else {
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
  }, [grievanceText, grievanceType, grievanceSubType, location, documents, address]);

  return (
    <Fragment>
      <FormStep config={config} onSelect={goNext} onSkip={onSkip}
       isDisabled={!grievanceText || !grievanceType || !grievanceSubType}
      >
        <CardLabel>
          {`${t("PGR_AI_INPUT_GRIEVANCE")}`} <span className="astericColor">*</span>
        </CardLabel>
        <div style={{ position: "relative" }}>
          <TextInput
            t={t}
            type="text"
            isMandatory={false}
            name="grievance"
            value={grievanceText}
            style={{ width: user.type === "EMPLOYEE" ? "50%" : "86%" }}
            onChange={(e) => setGrievanceText(e.target.value)}
            onFocus={handleInputFocus}
            onBlur={handleInputBlur}
          />

          {showSuggestions && suggestions.length > 0 && (
            <div style={styles.suggestionContainer}>
              {suggestions.map((suggestion, index) => (
                <div
                  key={suggestion.id || index}
                  onClick={() => handleSuggestionSelect(suggestion)}
                  style={index < suggestions.length - 1 ? styles.suggestionItem : {...styles.suggestionItem, ...styles.lastSuggestionItem}}
                >
                  <div style={{ fontWeight: "500" }}>{suggestion.type}</div>
                  <div style={styles.suggestionSubtype}>{suggestion.subtype}</div>
                </div>
              ))}
            </div>
          )}
        </div>

        {isLoading && <Loader />}
        {apiError && <div style={styles.errorMessage}>{apiError}</div>}

        <CardLabel>
          {`${t("PGR_AI_GRIEVANCE_TYPE")}`} <span className="astericColor">*</span>
        </CardLabel>
        <TextInput
          t={t}
          type="text"
          isMandatory={false}
          style={{ width: user.type === "EMPLOYEE" ? "50%" : "86%" }}
          name="grievanceType"
          value={grievanceType}
          onChange={(e) => setGrievanceType(e.target.value)}
          disabled={isLoading}
        />

        <CardLabel>
          {`${t("PGR_AI_GRIEVANCE_SUB_TYPE")}`} <span className="astericColor">*</span>
        </CardLabel>
        <TextInput
          t={t}
          type="text"
          style={{ width: user.type === "EMPLOYEE" ? "50%" : "86%" }}
          isMandatory={false}
          name="grievanceSubType"
          value={grievanceSubType}
          onChange={(e) => setGrievanceSubType(e.target.value)}
          disabled={isLoading}
        />

        <CardLabel>
          {`${t("PGR_AI_GRIEVANCE_LOCATION")}`} <span className="astericColor">*</span>
        </CardLabel>
        <div className="field" style={styles.locationField}>
            {/* Text input for entering or displaying the location. 
      The width dynamically adjusts based on the user type (EMPLOYEE or others). */}
          <TextInput
            t={t}
            value={location}
            onChange={handleLocationChange}
            style={{ paddingRight: "30px", width: user.type === "EMPLOYEE" ? "50%" : "86%"}}
          />
          {/* Icon for fetching the user's current location. 
      Clicking this triggers the handleFetchLocation function. */}
          <div style={styles.locationIcon} onClick={handleFetchLocation}>
            <LocationIcon styles={{ width: "16px", border: "none" }} className="fill-path-primary-main" />
          </div>
        </div>

        <LabelFieldPair>
          <CardLabel>{`${t("PGR_AI_LANDMARK")}`} <span className="astericColor">*</span></CardLabel>
        </LabelFieldPair>
        <TextInput
          t={t}
          type="text"
          name="landmark"
          style={{ width: user.type === "EMPLOYEE" ? "50%" : "86%" }}
          value={addressDetails.landmark || ""}
          onChange={(e) => setAddressDetails({ ...addressDetails, landmark: e.target.value })}
          disabled={isLoading}
        />
        {locationError && <CardLabelError>{locationError}</CardLabelError>}

        {address && (
       <div>
            <CardLabel>{`${t("PGR_AI_ADDRESS")}`}</CardLabel>
            <div className="field">
              <TextInput t={t} value={address} readOnly style={{  ...styles.readOnlyInput,  width: user.type === "EMPLOYEE" ? "50%" : "86%" }} />
            </div>
          </div>
        )}

        <PhotoUpload t={t} config={{ key: "documents" }} onSelect={handlePhotoUpload} formData={{ documents }}  setDocumentUploaded={ setVerificationDocuments} />

        <SubmitBar label={t("PGR_AI_ADD_ADDRESS_DETAILS")} onSubmit={handleAddAddressClick} style={{ marginTop: "16px" }} />

        {showAddressPopup && (
          <AddressPopup t={t} isOpen={showAddressPopup} onClose={() => setShowAddressPopup(false)} onSubmit={handleAddressSubmit} />
        )}
      </FormStep>
    </Fragment>
  );
};

export default NewGrievance;
