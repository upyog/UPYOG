import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, Card, CardSubHeader, TextArea, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import { useLocation } from "react-router-dom";
import { useForm } from "react-hook-form";
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
  const [showToast, setShowToast] = useState(null);  // State for Toast

  const { data: fetchedLocalities, isLoading: isLoadingLocalities } = Digit.Hooks.useBoundaryLocalities(
    city?.code,
    "revenue",
    {
      enabled: !!city,
    },
    t
  );

  useEffect(() => {
    if (pincode.length === 6) {
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
        setLocality(null);  // Clear locality if no match found
        setShowToast({ error: true, label: t("CHB_PINCODE_INVALID") });
      }
    } else {
      setCity(null);  // Clear city when pincode is less than 6
      setLocality(null);  // Clear locality when pincode is less than 6
      setLocalities([]);  // Clear localities list when pincode is less than 6
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

    if (newPincode.length < 6) {
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

  useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => {
        setShowToast(null);
      }, 2000); // Close toast after 2 seconds

      return () => clearTimeout(timer); // Clear timer on cleanup
    }
  }, [showToast]);

  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={3} /> : null}
      <Card>
        <CardSubHeader>
          {value?.bookingSlotDetails && value.bookingSlotDetails.length > 0
            ? formatSlotDetails(value.bookingSlotDetails)
            : null}
        </CardSubHeader>
        <ChbCancellationPolicy slotDetail={value?.bookingSlotDetails} />
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
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="city"
            value={city?.name || ""}
            placeholder={"City Auto Select"}
            style={{ width: "86%" }}
            onChange={setCity}
            disabled={true}
          />
          <CardLabel>{`${t("CHB_LOCALITY")}`} <span style={{ color: 'red' }}>*</span></CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="locality"
            value={locality?.name || ""}
            placeholder={"Locality Auto Select"}
            style={{ width: "86%" }}
            onChange={selectLocality}
            disabled={true}
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
            ValidationRequired={true}
            {...(validation = {
              pattern: "^[a-zA-Z0-9 ,\\-]+$",
              type: "text",
              title: t("CHB_STREET_NAME_ERROR_MESSAGE"),
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
            placeholder={"Enter House No"}
            onChange={setApplicantHouseNo}
            style={{ width: "86%" }}
            ValidationRequired={true}
            {...(validation = {
              isRequired: true,
              pattern: "^[a-zA-Z0-9 ,\\-]+$",
              type: "text",
              title: t("CHB_HOUSE_NO_ERROR_MESSAGE"),
            })}
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
              pattern: "^[a-zA-Z0-9 ,\\-]+$",
              type: "textarea",
              title: t("CHB_LANDMARK_ERROR_MESSAGE"),
            })}
          />
        </div>
      </FormStep>
      {showToast && (
        <Toast
          error={showToast.error}
          warning={showToast.warning}
          label={t(showToast.label)}
          onClose={() => {
            setShowToast(null);
          }}
        />
      )}
    </React.Fragment>
  );
};

export default CHBAddressDetails;
