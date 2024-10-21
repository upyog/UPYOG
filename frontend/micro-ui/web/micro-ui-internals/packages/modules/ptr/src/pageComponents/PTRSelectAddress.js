import React, { useEffect, useState, useContext } from "react";
import { FormStep, TextInput, CardLabel, Dropdown, TextArea, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import { useLocation, useParams } from "react-router-dom";
import { useForm, Controller } from "react-hook-form";
import Timeline from "../components/PTRTimeline";

const PTRSelectAddress = ({ t, config, onSelect, userType, formData, value = formData.slotlist, renewApplication }) => {
  const { pathname: url } = useLocation();
  const convertToObject = (String) => String ? { i18nKey: String, code: String, value: String } : null;
  const allCities = Digit.Hooks.ptr.useTenants();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const {pathname} = useLocation();
  let validation = {};

  const user = Digit.UserService.getUser().info;


  let appData; // appData is used to get data from employee or citizen side and pass into fields

  // On citizen side appData takes data of renewapplication
  if(pathname.includes("citizen")){
    appData = renewApplication || null;
  } else {
    // On employee side appData takes data from the hook while 
    const {applicationNumber} = useParams();
    const { isLoading: auditDataLoading, isError: isAuditError, data: app_data } = Digit.Hooks.ptr.usePTRSearch(
      {
        tenantId,
        filters: { applicationNumber: applicationNumber, audit: true },
      },
    );

    appData = app_data && app_data?.PetRegistrationApplications[0];
  }

  const [pincode, setPincode] = useState(appData?.address?.pincode|| formData?.address?.pincode || "");
  const [city, setCity] = useState(convertToObject(appData?.address?.city) || formData?.address?.city || "");
  const [locality, setLocality] = useState(convertToObject(appData?.address?.locality) || formData?.address?.locality || "");
  const [streetName, setStreetName] = useState(appData?.address?.street || formData?.address?.street || "");
  const [houseNo, setHouseNo] = useState(appData?.address?.houseNo || formData?.address?.houseNo || "");
  const [landmark, setLandmark] = useState(appData?.address?.landmark || formData?.address?.landmark || "");
  const [houseName, setHouseName] = useState(appData?.address?.buildingName || formData?.address?.buildingName || "");
  const [addressline1, setAddressline1] = useState(appData?.address?.addressLine1 || formData?.address?.addressLine1 || "");
  const [addressline2, setAddressline2] = useState(appData?.address?.addressLine2 || formData?.address?.addressLine2 || "");


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

  // Fixing the locality data coming from the useboundarylocalities hook
  let structuredLocality = [];
  fetchedLocalities && fetchedLocalities.map((local, index) => {
    structuredLocality.push({i18nKey: local.i18nkey, code: local.code, label: local.label, area: local.area, boundaryNum: local.boundaryNum})
  })


  const setAddressPincode = (e) => {
    const newPincode = e.target.value.slice(0, 6);
    setPincode(newPincode);
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

  const sethouseName = (e) => {
    setHouseName(e.target.value)
  }

  const setaddressline1 = (e) => {
    setAddressline1(e.target.value)
  }

  const setaddressline2 = (e) => {
    setAddressline2(e.target.value)
  }

  const goNext = () => {
    let owner = formData.address ;
    let ownerStep = { ...owner, pincode, city, locality, streetName, houseNo, landmark, houseName, addressline1, addressline2 };
    onSelect(config.key, { ...formData[config.key], ...ownerStep }, false);
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
      <FormStep
        config={config}
        onSelect={goNext}
        onSkip={onSkip}
        t={t}
        isDisabled={!pincode || !city || !streetName || !houseNo || !landmark || !locality || !addressline1 || !landmark || !city || !locality || !pincode}
      >
        <div>
          <CardLabel>{`${t("PTR_HOUSE_NO")}`} <span className="check-page-link-button">*</span></CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="houseNo"
            value={houseNo}
            placeholder={"Enter House No"}
            onChange={setApplicantHouseNo}
            style={{ width: user.type === "EMPLOYEE" ? "50%" : "86%" }}
            ValidationRequired={true}
            {...(validation = {
              isRequired: true,
              pattern: "^[a-zA-Z0-9 ,\\-]+$",
              type: "text",
              title: t("PTR_HOUSE_NO_ERROR_MESSAGE"),
            })}
          />


          <CardLabel>{`${t("PTR_HOUSE_NAME")}`}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="houseName"
            value={houseName}
            placeholder={"Enter House Name"}
            onChange={sethouseName}
            style={{ width: user.type === "EMPLOYEE" ? "50%" : "86%" }}
            ValidationRequired={false}
          />

          <CardLabel>{`${t("PTR_STREET_NAME")}`} <span className="check-page-link-button">*</span></CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="streetName"
            value={streetName}
            placeholder={"Enter Street Name"}
            onChange={setApplicantStreetName}
            style={{ width: user.type === "EMPLOYEE" ? "50%" : "86%" }}
            ValidationRequired={true}
            {...(validation = {
              pattern: "^[a-zA-Z0-9 ,\\-]+$",
              type: "text",
              title: t("PTR_STREET_NAME_ERROR_MESSAGE"),
            })}
          />

          <CardLabel>{`${t("PTR_ADDRESS_LINE1")}`} <span className="check-page-link-button">*</span></CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="addressline1"
            value={addressline1}
            placeholder={"Enter Address"}
            onChange={setaddressline1}
            style={{ width: user.type === "EMPLOYEE" ? "50%" : "86%" }}
            ValidationRequired={false}

          />

          <CardLabel>{`${t("PTR_ADDRESS_LINE2")}`}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="addressline2"
            value={addressline2}
            placeholder={"Enter Address"}
            onChange={setaddressline2}
            style={{ width: user.type === "EMPLOYEE" ? "50%" : "86%" }}
            ValidationRequired={false}
          />
          <CardLabel>{`${t("PTR_LANDMARK")}`} <span className="check-page-link-button">*</span></CardLabel>
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
              title: t("PTR_LANDMARK_ERROR_MESSAGE"),
            })}
          />

          <CardLabel>{`${t("PTR_CITY")}`} <span className="check-page-link-button">*</span></CardLabel>
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
                option={allCities}
                optionKey="i18nKey"
                t={t}
                placeholder={"Select"}
              />
            )}
          />
          <CardLabel>{`${t("PTR_LOCALITY")}`} <span className="check-page-link-button">*</span></CardLabel>
          <Controller
            control={control}
            name={"locality"}
            defaultValue={locality}
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
              <Dropdown
                className="form-field"
                selected={locality}
                select={setLocality}
                option={structuredLocality}
                optionCardStyles={{ overflowY: "auto", maxHeight: "300px" }}
                optionKey="i18nKey"
                t={t}
                placeholder={"Select"}
              />
            )}
          />

          <CardLabel>{`${t("PTR_ADDRESS_PINCODE")}`} <span className="check-page-link-button">*</span></CardLabel>
          <TextInput
            t={t}
            type="text"
            isMandatory={false}
            optionKey="i18nKey"
            name="pincode"
            value={pincode}
            onChange={setAddressPincode}
            placeholder="Enter Pincode"
            style={{ width: user.type === "EMPLOYEE" ? "50%" : "86%" }}
            ValidationRequired={true}
            validation={{
              pattern: "[0-9]{6}",
              type: "text",
              title: t("CHB_ADDRESS_PINCODE_INVALID"),
            }}
            minLength={6}
            maxLength={6}
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

export default PTRSelectAddress;
