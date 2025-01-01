import React, { useEffect, useState, useRef } from "react";
import { CardLabel, Dropdown, FormStep, RadioButtons } from "@nudmcdgnpm/digit-ui-react-components";

const SelectAddress = ({ t, config, onSelect, value }) => {
  const allCities = Digit.Hooks.pgr.useTenants();
  const cities = value?.pincode ? allCities.filter((city) => city?.pincode?.some((pin) => pin == value["pincode"])) : allCities;
  const pttype=sessionStorage.getItem("type")
  const citynew=sessionStorage.getItem("tenantId")  
  const localitynew=sessionStorage.getItem("localityCode")
  let value2=value
  const [selectedCity, setSelectedCity] = useState(() => {
    if(pttype=="PT"){
      let filteredcities=cities.filter(city=>city.code === citynew);
      console.log("filteredcities",filteredcities)
      if(filteredcities){
         value2=filteredcities[0]
      }
      console.log("val2",value2)
      return value2;
    }
    else{
    const { city_complaint } = value;
    return city_complaint ? city_complaint : null;
    }
  });
  const { data: fetchedLocalities } = Digit.Hooks.useBoundaryLocalities(
    selectedCity?.code,
    "admin",
    {
      enabled: !!selectedCity,
    },
    t
  );
  const [localities, setLocalities] = useState(null);

  const [selectedLocality, setSelectedLocality] = useState(() => {
    const { locality_complaint } = value;
    return locality_complaint ? locality_complaint : null;
  });

  useEffect(async () => {
    if (selectedCity && fetchedLocalities) {
      const { pincode } = value;
      let __localityList = pincode ? fetchedLocalities.filter((city) => city["pincode"] == pincode) : fetchedLocalities;
      await setLocalities(__localityList);
      if (pttype == "PT") {
        let filteredLocalities = __localityList.filter(locality => locality.code === localitynew);
        if (filteredLocalities) {
          setSelectedLocality(filteredLocalities[0])
        }
      }
      else {
        setLocalities(__localityList);
      }
    }
  }, [selectedCity, fetchedLocalities]);

  function selectCity(city) {
    setSelectedLocality(null);
    setLocalities(null);
    setSelectedCity(city);
    // Digit.SessionStorage.set("city_complaint", city);
  }

  function selectLocality(locality) {
    setSelectedLocality(locality);
    // Digit.SessionStorage.set("locality_complaint", locality);
  }

  function onSubmit() {
    onSelect({ city_complaint: selectedCity, locality_complaint: selectedLocality });
  }
  return (
    <FormStep config={config} onSelect={onSubmit} t={t} isDisabled={selectedLocality ? false : true}>
      <div>
        <CardLabel>{t("MYCITY_CODE_LABEL")}</CardLabel>
        {cities?.length < 5 ? (
          <RadioButtons selectedOption={selectedCity} options={cities} optionsKey="i18nKey" onSelect={selectCity} />
        ) : (
          <Dropdown isMandatory selected={selectedCity} option={cities} select={selectCity} optionKey="i18nKey" t={t} />
        )}
        {selectedCity && localities && <CardLabel>{t("CS_CREATECOMPLAINT_MOHALLA")}</CardLabel>}
        {selectedCity && localities && (
          <React.Fragment>
            {localities?.length < 5 ? (
              <RadioButtons selectedOption={selectedLocality} options={localities} optionsKey="i18nkey" onSelect={selectLocality} />
            ) : (
              <Dropdown isMandatory selected={selectedLocality} optionKey="i18nkey" option={localities} select={selectLocality} t={t} />
            )}
          </React.Fragment>
        )}
      </div>
    </FormStep>
  );
};

export default SelectAddress;
