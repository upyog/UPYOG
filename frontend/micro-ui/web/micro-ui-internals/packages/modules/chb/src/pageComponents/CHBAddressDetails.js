import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, Card, CardSubHeader, Dropdown, TextArea } from "@nudmcdgnpm/digit-ui-react-components";
import { useLocation } from "react-router-dom";
import { Controller, useForm } from "react-hook-form";
import Timeline from "../components/CHBTimeline";
import ChbCancellationPolicy from "../components/ChbCancellationPolicy";

const CHBAddressDetails = ({ t, config, onSelect, userType, formData, value = formData.slotlist }) => {
  const { pathname: url } = useLocation();
  let index = window.location.href.charAt(window.location.href.length - 1);
  const allCities = Digit.Hooks.chb.useTenants();
  let validation = {};

  const [pincode, setPincode] = useState((formData.address && formData.address[index] && formData.address[index].pincode) || formData?.address?.pincode || "");
  const [city, setCity] = useState((formData.address && formData.address[index] && formData.address[index].city) || formData?.address?.city || "");
  const [locality, setLocality] = useState((formData.address && formData.address[index] && formData.address[index].locality) || formData?.address?.locality || "");
  const [streetName, setStreetName] = useState((formData.address && formData.address[index] && formData.address[index].streetName) || formData?.address?.streetName || "");
  const [houseNo, setHouseNo] = useState((formData.address && formData.address[index] && formData.address[index].houseNo) || formData?.address?.houseNo || "");
  const [landmark, setLandmark] = useState((formData.address && formData.address[index] && formData.address[index].landmark) || formData?.address?.landmark || "");

  const [localities, setLocalities] = useState([]);

  const { data: fetchedLocalities, isLoading: isLoadingLocalities } = Digit.Hooks.useBoundaryLocalities(
    city?.code,
    "revenue",
    {
      enabled: !!city,
    },
    t
  );

  useEffect(() => {
    if (pincode) {
      const matchedCity = allCities?.find((city) => city?.pincode?.some((pin) => pin == pincode));
      if (matchedCity) {
        setCity(matchedCity);
        const matchedLocality = fetchedLocalities?.find((locality) => locality.pincode?.includes(Number(pincode)));
        if (matchedLocality) {
          setLocality(matchedLocality);
        } else {
          setLocality(null);  // Clear locality if no match found
        }
      } else {
        setCity(null);  // Clear city if no match found
      }
    } else {
      setCity(null);  // Clear city when pincode is empty
      setLocality(null);  // Clear locality when pincode is empty
      setLocalities([]);  // Clear localities list when pincode is empty
    }
  }, [pincode, allCities, fetchedLocalities]);

  useEffect(() => {
    if (city && fetchedLocalities) {
      let __localityList = fetchedLocalities || [];
      let filteredLocalityList = [];

      if (formData?.address?.locality) {
        setLocality(formData.address.locality);
      }

      if (formData?.address?.pincode) {
        filteredLocalityList = __localityList.filter((obj) => obj.pincode?.includes(Number(formData.address.pincode)));
        if (!formData?.address?.locality) setLocality(null);
      }

      setLocalities(filteredLocalityList.length > 0 ? filteredLocalityList : __localityList);

      if (filteredLocalityList.length === 1) {
        setLocality(filteredLocalityList[0]);
      }
    } else {
      setLocalities([]);  // Clear localities list when city is empty
      setLocality(null);  // Clear locality when city is empty
    }
  }, [city, formData?.address?.pincode, fetchedLocalities, formData]);

  const selectLocality = (locality) => {
    if (formData?.address?.locality) {
      formData.address.locality = locality;
    }
    setLocality(locality);
  };

  const setAddressPincode = (e) => {
    const newPincode = e.target.value.slice(0, 6); // Truncate input to first 6 characters
    setPincode(newPincode);
    
    if (newPincode === "") {
      setCity(null);       // Clear city
      setLocality(null);   // Clear locality
      setLocalities([]);   // Clear localities list
    }
  };

  const setApplicantStreetName = (e) => {
    setStreetName(e.target.value);
  };

  const setApplicantHouseNo = (e) => {
    setHouseNo(e.target.value);
  };

  const setApplicantLandmark = (e) => {
    setLandmark(e.target.value);
  };

  const goNext = () => {
    let owner = formData.address && formData.address[index];
    let ownerStep = { ...owner, pincode, city, locality, streetName, houseNo, landmark };
    onSelect(config.key, { ...formData[config.key], ...ownerStep }, false, index);
    console.log(ownerStep);
  };

  const { control } = useForm();
  const onSkip = () => onSelect();

  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [goNext, userType]);

  const formatSlotDetails = (slots) => {
    const sortedSlots = slots.sort((a, b) => new Date(a.bookingDate) - new Date(b.bookingDate));
    const firstDate = sortedSlots[0]?.bookingDate;
    const lastDate = sortedSlots[sortedSlots.length - 1]?.bookingDate;
    if (firstDate === lastDate) {
      return `${sortedSlots[0]?.name} (${firstDate})`;
    } else {
      return `${sortedSlots[0]?.name} (${firstDate} - ${lastDate})`;
    }
  };

  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={3} /> : null}
      <Card>
        <CardSubHeader>
          {value?.bookingSlotDetails && value.bookingSlotDetails.length > 0
            ? formatSlotDetails(value.bookingSlotDetails)
            : null}
        </CardSubHeader>
        <ChbCancellationPolicy count={value?.bookingSlotDetails.length}/>
      </Card>
      <FormStep
        config={config}
        onSelect={goNext}
        onSkip={onSkip}
        t={t}
        isDisabled={!pincode || !city || !streetName || !houseNo || !landmark || !locality}
      >
        <div>
          <CardLabel>{`${t("CHB_PINCODE")}`} <span style={{ color: 'red' }}>*</span></CardLabel>
          <TextInput
            t={t}
            type="text"
            isMandatory={false}
            optionKey="i18nKey"
            name="pincode"
            value={pincode}
            onChange={setAddressPincode}
            placeholder="Enter Pincode"
            style={{ width: "86%" }}
            ValidationRequired={true}
            validation={{
              pattern: "[0-9]{6}",
              type: "text",
              title: t("CHB_ADDRESS_PINCODE_INVALID"),
            }}
            minLength={6}
            maxLength={6}
          />
          <CardLabel>{`${t("CHB_CITY")}`} <span style={{ color: 'red' }}>*</span></CardLabel>
          <Controller
            control={control}
            name={"city"}
            defaultValue={city}
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
              <Dropdown
                className="form-field"
                selected={city}
                select={setCity}
                placeholder={"Select City"}
                optionKey="i18nKey"
                t={t}
                isDisabled
              />
            )}
          />
          <CardLabel>{`${t("CHB_LOCALITY")}`} <span style={{ color: 'red' }}>*</span></CardLabel>
          <Controller
            control={control}
            name={"locality"}
            defaultValue={locality}
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
              <Dropdown
                className="form-field"
                selected={locality}
                select={selectLocality}
                option={localities.sort((a, b) => a.name.localeCompare(b.name))}
                placeholder={"Select Locality"}
                optionKey="name"
                t={t}
                isLoading={isLoadingLocalities}
              />
            )}
          />
          <CardLabel>{`${t("CHB_STREET_NAME")}`} <span style={{ color: 'red' }}>*</span></CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="streetName"
            value={streetName}
            placeholder={"Enter Street Name"}
            onChange={setApplicantStreetName}
            style={{ width: "86%" }}
            ValidationRequired = {true}
            {...(validation = {
              // isRequired: true,
              pattern: "^[a-zA-Z ]+$",
              type: "text",
              title: t("CHB_NAME_ERROR_MESSAGE"),
            })}
         
          />
          <CardLabel>{`${t("CHB_HOUSE_NO")}`} <span style={{ color: 'red' }}>*</span></CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="houseNo"
            value={houseNo}
            placeholder={"Enter HouseNo"}
            onChange={setApplicantHouseNo}
            style={{ width: "86%" }}
          />
          <CardLabel>{`${t("CHB_LANDMARK")}`} <span style={{ color: 'red' }}>*</span></CardLabel>
          <TextArea
            t={t}
            type={"textarea"}
            isMandatory={false}
            optionKey="i18nKey"
            name="landmark"
            value={landmark}
            placeholder={"Enter Landmark"}
            onChange={setApplicantLandmark}
            style={{ width: "50%" }}
            ValidationRequired={true}
            {...(validation = {
              isRequired: true,
              pattern: "^[a-zA-Z ]+$",
              type: "textarea",
              title: t("_ERROR_MESSAGE"),
            })}
          />
        </div>
      </FormStep>
    </React.Fragment>
  );
};

export default CHBAddressDetails;
