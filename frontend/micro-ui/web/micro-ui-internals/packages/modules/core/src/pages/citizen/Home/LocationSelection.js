import React, { Fragment, useMemo, useState } from "react";
import { PageBasedInput, CardHeader, BackButton, SearchOnRadioButtons, CardLabelError, RadioOrSelect, CardLabel } from "@egovernments/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";

const LocationSelection = () => {



  
  /////////////////////////////////////////////////////////////////////////////////
  const { t } = useTranslation();
  const history = useHistory();
  const { data: { districts } = {}, isLoad } = Digit.Hooks.useStore.getInitData();
  console.log(Digit.Hooks.useStore.getInitData());
  const { data: cities, isLoading } = Digit.Hooks.useTenants();
  
  // console.log(Digit.Hooks.useStore.getInitData());
  // const { data: districts, isLoad } = Digit.Hooks.useTenantsDistrict();

  const [selectedCity, setSelectedCity] = useState(() => ({ code: Digit.ULBService.getCitizenCurrentTenant(true) }));
  const [selectedDistrict, setSelectedDistrict] = useState(() => ({ code: true }));
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
  function selectDistrict(district) {
    setSelectedDistrict(district);
    setShowError(false);
  }

  // const RadioButtonProps = useMemo(() => {
  //   return {
  //     options: cities,
  //     optionsKey: "i18nKey",
  //     additionalWrapperClass: "reverse-radio-selection-wrapper",
  //     onSelect: selectCity,
  //     selectedOption: selectedCity,
  //   };
  // }, [cities, t, selectedCity]);

  function onSubmit() {
    if (selectedCity) {
      Digit.SessionStorage.set("CITIZEN.COMMON.HOME.CITY", selectedCity);
      history.push("/digit-ui/citizen");
    } else {
      setShowError(true);
    }
  }
  function onSubmit() {
    if (selectedCity) {
      Digit.SessionStorage.set("CITIZEN.COMMON.HOME.CITY", selectedCity);
      history.push("/digit-ui/citizen");
    } else {
      setShowError(true);
    }
  }

  return isLoading ,isLoad? (
    <loader />
  ) : (
    <>
      <BackButton />
      <PageBasedInput texts={texts} onSubmit={onSubmit}>
        <CardHeader>
          Choose Your Local Body
          {/* {t("CS_COMMON_CHOOSE_LOCATION")} */}
        </CardHeader>
        <CardLabel>Districts</CardLabel>
        <RadioOrSelect 
          options={districts}
          selectedOption={selectedDistrict}
          optionKey="name"
          onSelect={selectDistrict}
          t={t}
          labelKey=""
        //  disabled={isEdit}
        />
        <CardLabel>Local Body</CardLabel>
        <RadioOrSelect 
         options={cities}
         selectedOption={selectedCity}
         optionKey="name"
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
