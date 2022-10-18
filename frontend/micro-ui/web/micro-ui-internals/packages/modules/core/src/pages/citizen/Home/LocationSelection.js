import React, { Fragment, useMemo, useState,setValue,useEffect } from "react";
import { PageBasedInput, CardHeader, BackButton, SearchOnRadioButtons, CardLabelError, RadioOrSelect, CardLabel } from "@egovernments/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";



const LocationSelection = () => {
 
  
  /////////////////////////////////////////////////////////////////////////////////
  const { t } = useTranslation();
  const history = useHistory(); 
  console.log(Digit.Hooks.useStore.getInitData());
  const { data: { districts } = {}, isLoad } = Digit.Hooks.useStore.getInitData(); 
  const { data: localbodies, isLoading } = Digit.Hooks.useTenants();
  // console.log(localbodies);
  const [lbs, setLbs] = useState();
  const [selectedCity, setSelectedCity] = useState(() => ({ code: Digit.ULBService.getCitizenCurrentTenant(true) }));
  const [selectedDistrict, setSelectedDistrict] = useState(); 
  const [showError, setShowError] = useState(false);
  let districtid=null;
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
    setSelectedCity(null);
    setLbs(null);
    // console.log(district.districtid);
    districtid=district.districtid
    // console.log(localbodies);
    setShowError(false);
    if(districtid){
      // console.log(districtid);
    }
   
  }
  useEffect(() => {
    // console.log(districtid);
    if(selectedDistrict){
      // console.log(selectedDistrict.districtid);
      // const _locality = localities?.filter((e) => e.code === code)[0];districtid===selectedDistrict.districtid
      setLbs(localbodies.filter( (localbodies) => localbodies.city.districtid===selectedDistrict.districtid));
      // console.log(lbs);
    }
  }, [lbs]);

  // useEffect(() => {
  //   // executed only once
  // }, [])
 
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
          // onChange={(e) => onChangeLB(e.target.value)}
        //  disabled={isEdit}
        />
        <CardLabel>Local Body</CardLabel>
        <RadioOrSelect 
         options={lbs}
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
