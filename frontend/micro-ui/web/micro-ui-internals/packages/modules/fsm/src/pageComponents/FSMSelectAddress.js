import React, { useEffect, useState } from "react";
import { FormStep, CardLabel, Dropdown, RadioButtons, LabelFieldPair, RadioOrSelect } from "@egovernments/digit-ui-react-components";
import Timeline from "../components/TLTimelineInFSM";
import { useLocation } from "react-router-dom";

const FSMSelectAddress = ({ t, config, onSelect, userType, formData }) => {
  const allCities = Digit.Hooks.fsm.useTenants();
  let tenantId = Digit.ULBService.getCurrentTenantId();

  if (userType !== "employee") {
    tenantId = Digit.SessionStorage.get("CITIZEN.COMMON.HOME.CITY")?.code;
  }
  const location = useLocation();
  const isNewVendor = location.pathname.includes("new-vendor");
  const isEditVendor = location.pathname.includes("modify-vendor");
  const inputs = [
    {
      active: true,
      code: "WITHIN_ULB_LIMITS",
      i18nKey: "WITHIN_ULB_LIMITS",
      name: "Witnin ULB Limits",
    },
    {
      active: true,
      code: "FROM_GRAM_PANCHAYAT",
      i18nKey: "FROM_GRAM_PANCHAYAT",
      name: "From Gram Panchayat",
    },
  ];

  const { pincode, city } = formData?.address || "";
  const cities =
    userType === "employee"
      ? allCities.filter((city) => city.code === tenantId)
      : pincode
      ? allCities.filter((city) => city?.pincode?.some((pin) => pin == pincode))
      : allCities;

  const [selectedCity, setSelectedCity] = useState(
    () => formData?.address?.city || Digit.SessionStorage.get("fsm.file.address.city") || Digit.SessionStorage.get("CITIZEN.COMMON.HOME.CITY")
  );
  const [newLocality, setNewLocality] = useState();
  const { data: fetchedLocalities } = Digit.Hooks.useBoundaryLocalities(
    selectedCity?.code,
    "revenue",
    {
      enabled: !!selectedCity,
    },
    t
  );

  const { data: urcConfig } = Digit.Hooks.fsm.useMDMS(tenantId, "FSM", "UrcConfig");
  const isUrcEnable = urcConfig && urcConfig.length > 0 && urcConfig[0].URCEnable;
  const [selectLocation, setSelectLocation] = useState(() =>
    formData?.address?.propertyLocation
      ? formData?.address?.propertyLocation
      : Digit.SessionStorage.get("locationType")
      ? Digit.SessionStorage.get("locationType")
      : inputs[0]
  );

  const [localities, setLocalities] = useState();
  const [selectedLocality, setSelectedLocality] = useState();

  useEffect(() => {
    if (cities) {
      if (cities.length === 1) {
        setSelectedCity(cities[0]);
      }
    }
  }, [cities]);

  useEffect(() => {
    if (selectedCity && selectLocation) {
      if (userType === "employee") {
        onSelect(config.key, {
          ...formData[config.key],
          city: selectedCity,
          propertyLocation: selectLocation,
        });
      }
    }
    if ((!isUrcEnable || isNewVendor || isEditVendor) && selectedCity && fetchedLocalities) {
      let __localityList = fetchedLocalities;
      let filteredLocalityList = [];

      if (formData?.address?.locality) {
        setSelectedLocality(formData.address.locality);
      }

      if (formData?.address?.pincode) {
        filteredLocalityList = __localityList.filter((obj) => obj.pincode?.find((item) => item == formData.address.pincode));
        if (!formData?.address?.locality) setSelectedLocality();
      }

      if (userType === "employee") {
        onSelect(config.key, { ...formData[config.key], city: selectedCity });
      }
      setLocalities(() => (filteredLocalityList.length > 0 ? filteredLocalityList : __localityList));
      if (filteredLocalityList.length === 1) {
        setSelectedLocality(filteredLocalityList[0]);
        if (userType === "employee") {
          onSelect(config.key, { ...formData[config.key], locality: filteredLocalityList[0] });
        }
      }
    }
  }, [selectedCity, selectLocation, fetchedLocalities]);

  function selectCity(city) {
    setSelectedLocality(null);
    setLocalities(null);
    Digit.SessionStorage.set("fsm.file.address.city", city);
    setSelectedCity(city);
  }

  function selectedValue(value) {
    setSelectLocation(value);
    Digit.SessionStorage.set("locationType", value);
    if (userType === "employee") {
      if (value.code === "FROM_GRAM_PANCHAYAT") {
        onSelect("tripData", {
          ...formData["tripData"],
          amountPerTrip: "",
          amount: "",
        });
        onSelect(config.key, {
          ...formData[config.key],
          propertyLocation: value,
        });
      } else {
        onSelect(config.key, {
          ...formData[config.key],
          propertyLocation: value,
        });
      }
    }
  }

  function selectLocality(locality) {
    setSelectedLocality(locality);
    if (userType === "employee") {
      onSelect(config.key, { ...formData[config.key], locality: locality });
    }
  }

  const onNewLocality = (value) => {
    setNewLocality(value);
    if (userType === "employee") {
      onSelect(config.key, { ...formData[config.key], newLocality: value });
    }
  };

  function onSubmit() {
    onSelect(config.key, {
      city: selectedCity,
      propertyLocation: Digit.SessionStorage.get("locationType") ? Digit.SessionStorage.get("locationType") : selectLocation,
    });
  }

  if (userType === "employee") {
    return (
      <div>
        <LabelFieldPair>
          <CardLabel className="card-label-smaller">
            {t("MYCITY_CODE_LABEL")}
            {config.isMandatory ? " * " : null}
          </CardLabel>
          <Dropdown
            className="form-field"
            isMandatory
            selected={cities?.length === 1 ? cities[0] : selectedCity}
            disable={cities?.length === 1}
            option={cities}
            select={selectCity}
            optionKey="code"
            t={t}
          />
        </LabelFieldPair>
        {!isUrcEnable || isNewVendor || isEditVendor ? (
          <div>
            <LabelFieldPair>
              <CardLabel className="card-label-smaller">
                {t("ES_NEW_APPLICATION_LOCATION_MOHALLA")}
                {config.isMandatory ? " * " : null}
              </CardLabel>
              <Dropdown
                className="form-field"
                isMandatory
                selected={selectedLocality}
                option={localities}
                select={selectLocality}
                optionKey="i18nkey"
                t={t}
              />
            </LabelFieldPair>
            {!isNewVendor && !isEditVendor && !isUrcEnable && formData?.address?.locality?.name === "Other" && (
              <LabelFieldPair>
                <CardLabel className="card-label-smaller">{`${t("ES_INBOX_PLEASE_SPECIFY_LOCALITY")} *`}</CardLabel>
                <div className="field">
                  <TextInput id="newLocality" key="newLocality" value={newLocality} onChange={(e) => onNewLocality(e.target.value)} />
                </div>
              </LabelFieldPair>
            )}
          </div>
        ) : (
          <LabelFieldPair>
            <CardLabel>{`${t("CS_PROPERTY_LOCATION")} *`}</CardLabel>
            <div className="field">
              <RadioButtons
                selectedOption={selectLocation}
                onSelect={selectedValue}
                style={{ display: "flex", marginBottom: 0 }}
                innerStyles={{ marginLeft: "10px" }}
                options={inputs}
                optionsKey="i18nKey"
                // disabled={editScreen}
              />
            </div>
          </LabelFieldPair>
        )}
      </div>
    );
  }
  return (
    <React.Fragment>
      <Timeline currentStep={1} flow="APPLY" />
      <FormStep config={config} onSelect={onSubmit} t={t} isDisabled={selectLocation ? false : true}>
        {isUrcEnable && (
          <React.Fragment>
            <CardLabel>{`${t("CS_PROPERTY_LOCATION")} *`}</CardLabel>
            <RadioOrSelect
              isMandatory={config.isMandatory}
              options={inputs}
              selectedOption={Digit.SessionStorage.get("locationType") ? Digit.SessionStorage.get("locationType") : selectLocation}
              optionKey="i18nKey"
              onSelect={selectedValue}
              t={t}
            />
          </React.Fragment>
        )}
        <CardLabel>{`${t("MYCITY_CODE_LABEL")} *`}</CardLabel>
        <RadioOrSelect options={cities} selectedOption={selectedCity} optionKey="i18nKey" onSelect={selectCity} t={t} />
      </FormStep>
    </React.Fragment>
  );
};

export default FSMSelectAddress;