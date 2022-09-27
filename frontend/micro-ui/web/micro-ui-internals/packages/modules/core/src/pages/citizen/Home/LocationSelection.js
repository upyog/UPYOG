import React, { Fragment, useMemo, useState } from "react";
import { PageBasedInput, CardHeader, BackButton, SearchOnRadioButtons, CardLabelError, RadioOrSelect, CardLabel } from "@egovernments/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";

const LocationSelection = () => {



  
  /////////////////////////////////////////////////////////////////////////////////
  const { t } = useTranslation();
  const history = useHistory();

  const { data: cities, isLoading } = Digit.Hooks.useTenants();

  const [selectedCity, setSelectedCity] = useState(() => ({ code: Digit.ULBService.getCitizenCurrentTenant(true) }));
  const [showError, setShowError] = useState(false);

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
    if (selectedCity) {
      Digit.SessionStorage.set("CITIZEN.COMMON.HOME.CITY", selectedCity);
      history.push("/digit-ui/citizen");
    } else {
      setShowError(true);
    }
  }

  return isLoading ? (
    <loader />
  ) : (
    <>
      <BackButton />
      <PageBasedInput texts={texts} onSubmit={onSubmit}>
        <CardHeader>
          Choose Your Local Body
          {/* {t("CS_COMMON_CHOOSE_LOCATION")} */}
        </CardHeader>
        <CardLabel>Local Body</CardLabel>
        <RadioOrSelect 
         options={cities}
         selectedOption={selectedCity}
         optionKey="i18nKey"
         onSelect={selectCity}
         t={t}
         labelKey=""
        //  disabled={isEdit}
        />

        {/* <SearchOnRadioButtons {...RadioButtonProps} placeholder={t("COMMON_TABLE_SEARCH")} /> */}
        {showError ? <CardLabelError>{t("CS_COMMON_LOCATION_SELECTION_ERROR")}</CardLabelError> : null}
      </PageBasedInput>
    </>
  );
};

export default LocationSelection;
