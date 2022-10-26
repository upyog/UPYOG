<<<<<<< HEAD
import React, { Fragment, useMemo, useState } from "react";
=======
import React, { Fragment, useMemo, useState,setValue,useEffect } from "react";
>>>>>>> 98956340d46c8cd43ff9bc1be576479d43931349
import { PageBasedInput, CardHeader, BackButton, SearchOnRadioButtons, CardLabelError, RadioOrSelect, CardLabel } from "@egovernments/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";



const LocationSelection = () => {
<<<<<<< HEAD



  
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
=======
 
  
  /////////////////////////////////////////////////////////////////////////////////
  const { t } = useTranslation();
  const history = useHistory(); 
  const { data: { districts } = {}, isLoad } = Digit.Hooks.useStore.getInitData(); 
  const { data: localbodies, isLoading } = Digit.Hooks.useTenants();
  const [lbs, setLbs] = useState(0);
  const [isInitialRender, setIsInitialRender] = useState(true);
  const [selectedCity, setSelectedCity] = useState(() => ({ code: Digit.ULBService.getCitizenCurrentTenant(true) }));
  const [selectedDistrict, setSelectedDistrict] = useState(); 
>>>>>>> 98956340d46c8cd43ff9bc1be576479d43931349
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
<<<<<<< HEAD
  function selectDistrict(district) {
    setSelectedDistrict(district);
    setShowError(false);
  }

=======
  
 function selectDistrict(district) {
    setIsInitialRender(true);
    setSelectedDistrict(district);
    setSelectedCity(null);
    setLbs(null);
    districtid=district.districtid
    setShowError(false);
    // if(districtid){
    // }
   
  }
  useEffect(() => {
    if (isInitialRender) {
      
      if(selectedDistrict){
        setIsInitialRender(false);
        setLbs(localbodies.filter( (localbodies) => localbodies.city.districtid===selectedDistrict.districtid));
      }
    }
  }, [lbs,isInitialRender]);

 
>>>>>>> 98956340d46c8cd43ff9bc1be576479d43931349
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
    } else if (selectedDistrict) {
      Digit.SessionStorage.set("CITIZEN.COMMON.HOME.DISTRICT", selectedDistrict);
      history.push("/digit-ui/citizen");
    } else {
      setShowError(true);
    }
  }
<<<<<<< HEAD
  function onSubmit() {
    if (selectedCity) {
      Digit.SessionStorage.set("CITIZEN.COMMON.HOME.CITY", selectedCity);
      history.push("/digit-ui/citizen");
    } else {
      setShowError(true);
    }
  }

=======
  
>>>>>>> 98956340d46c8cd43ff9bc1be576479d43931349
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
<<<<<<< HEAD
=======
          // onChange={(e) => onChangeLB(e.target.value)}
>>>>>>> 98956340d46c8cd43ff9bc1be576479d43931349
        //  disabled={isEdit}
        />
        <CardLabel>Local Body</CardLabel>
        <RadioOrSelect 
<<<<<<< HEAD
         options={cities}
=======
         options={lbs}
>>>>>>> 98956340d46c8cd43ff9bc1be576479d43931349
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
