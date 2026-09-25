import { BackButton, CardHeader, CardLabelError, Loader, PageBasedInput, SearchOnRadioButtons, Toast } from "@nudmcdgnpm/upyog-ui-react-components-lts";
import React, { useMemo, useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useLocation } from "react-router-dom";

const LocationSelection = () => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const location = useLocation();
  const { data: cities, isLoading } = Digit.Hooks.useTenants();

  // Initialize state with the home city code from sessionStorage if it exists, otherwise set to null representing no selection.
  // This prevents initializing as an empty object { code: null } which behaves as truthy and bypasses validation.
  const [selectedCity, setSelectedCity] = useState(() => {
    const homeCity = Digit.ULBService.getCitizenCurrentTenant(true);
    return homeCity ? { code: homeCity } : null;
  });
  const [showError, setShowError] = useState(false);
  // State to manage whether the validation warning Toast is visible
  const [showToast, setShowToast] = useState(null);

  // Auto-dismiss the Toast notification after 3 seconds to keep the UI clean
  useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => {
        setShowToast(null);
      }, 3000);
      return () => clearTimeout(timer);
    }
  }, [showToast]);

  const texts = useMemo(
    () => ({
      header: t("CS_COMMON_CHOOSE_LOCATION"),
      submitBarLabel: t("CORE_COMMON_CONTINUE"),
    }),
    [t]
  );

  function selectCity(city) {
    setSelectedCity(city);
    setShowError(false);
  }

  const RadioButtonProps = useMemo(() => {
    return {
      options: cities,
      optionsKey: "i18nKey",
      additionalWrapperClass: "reverse-radio-selection-wrapper",
      onSelect: selectCity,
      selectedOption: selectedCity,
    };
  }, [cities, t, selectedCity]);

  function onSubmit() {
    // Validate that a city is selected (has a valid code) before proceeding
    if (!selectedCity?.code) {
      // Display the validation Toast warning and inline error label, then block further navigation
      setShowToast({ error: true, label: "CS_COMMON_LOCATION_SELECTION_ERROR" });
      setShowError(true);
      return;
    }
    Digit.SessionStorage.set("CITIZEN.COMMON.HOME.CITY", selectedCity);
    const redirectBackTo = location.state?.redirectBackTo;
    if (redirectBackTo) {
      navigate(redirectBackTo, { replace: true });
    } else navigate("/sv-ui/citizen");
  }

  return isLoading ? (
    <Loader />
  ) : (
    <div className="selection-card-wrapper">
      <BackButton />
      <PageBasedInput texts={texts} onSubmit={onSubmit} className="location-selection-container">
        <CardHeader>{t("CS_COMMON_CHOOSE_LOCATION")}</CardHeader>
        <SearchOnRadioButtons {...RadioButtonProps} placeholder={t("COMMON_TABLE_SEARCH")} />
        {showError ? <CardLabelError>{t("CS_COMMON_LOCATION_SELECTION_ERROR")}</CardLabelError> : null}
      </PageBasedInput>
      {/* Toast component to show error message if user clicks continue without selecting location */}
      {showToast && (
        <Toast
          isDleteBtn={true}
          error={showToast.error}
          label={t(showToast.label)}
          onClose={() => setShowToast(null)}
        />
      )}
    </div>
  );
};

export default LocationSelection;
