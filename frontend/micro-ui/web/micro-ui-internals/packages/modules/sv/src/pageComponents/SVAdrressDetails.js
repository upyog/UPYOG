import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, CardHeader, Dropdown, TextArea, CheckBox } from "@upyog/digit-ui-react-components";
import { useForm, Controller } from "react-hook-form";
import { useLocation } from "react-router-dom";
import Timeline from "../components/Timeline";

/**
 * SVAdrressDetails Component
 * 
 * This component manages both permanent and correspondence address forms for street vendors.
 * It conditionally renders different address forms based on the URL path.
 * 
 * @param {Object} props Component properties
 * @param {Function} props.t - Translation function for internationalization
 * @param {Object} props.config - Configuration object for the form step
 * @param {Function} props.onSelect - Callback when form data is submitted
 * @param {String} props.userType - Type of user (citizen or employee)
 * @param {Object} props.formData - Existing form data
 * @param {Object} props.editdata - Data for editing mode
 * @param {Object} props.previousData - Previously saved data
 */
const SVAdrressDetails = ({ t, config, onSelect, userType, formData, editdata, previousData }) => {
  const allCities = Digit.Hooks.sv.useTenants();
  const convertToObject = (String) => String ? { i18nKey: String, code: String, value: String } : null;
  const { pathname } = useLocation();
  let validation = {};
  const user = Digit.UserService.getUser().info;
  const [pincode, setPincode] = useState(formData?.address?.pincode || previousData?.addressDetails?.[0]?.pincode || editdata?.addressDetails?.[0]?.pincode || "");
  const [city, setCity] = useState(formData?.address?.city || convertToObject(previousData?.addressDetails?.[0]?.city || editdata?.addressDetails?.[0]?.city) || "");
  const [locality, setLocality] = useState(formData?.address?.locality || convertToObject(previousData?.addressDetails?.[0]?.locality || editdata?.addressDetails?.[0]?.locality) || "");
  const [houseNo, setHouseNo] = useState(formData?.address?.houseNo || previousData?.addressDetails?.[0]?.houseNo || editdata?.addressDetails?.[0]?.houseNo || "");
  const [landmark, setLandmark] = useState(formData?.address?.landmark || previousData?.addressDetails?.[0]?.landmark || editdata?.addressDetails?.[0]?.landmark || "");
  const [addressline1, setAddressline1] = useState(formData?.address?.addressline1 || previousData?.addressDetails?.[0]?.addressLine1 || editdata?.addressDetails?.[0]?.addressLine1 || "");
  const [addressline2, setAddressline2] = useState(formData?.address?.addressline2 || previousData?.addressDetails?.[0]?.addressLine2 || editdata?.addressDetails?.[0]?.addressLine2 || "");

  // states for the correspondence address input fields
  const [cpincode, setCPincode] = useState(formData?.correspondenceAddress?.cpincode || previousData?.addressDetails?.[1]?.pincode || editdata?.addressDetails?.[1]?.pincode || "");
  const [ccity, setCCity] = useState(formData?.correspondenceAddress?.ccity || convertToObject(previousData?.addressDetails?.[1]?.city || editdata?.addressDetails?.[1]?.city) || "");
  const [clocality, setCLocality] = useState(formData?.correspondenceAddress?.clocality || convertToObject(previousData?.addressDetails?.[1]?.locality || editdata?.addressDetails?.[1]?.locality) || "");
  const [chouseNo, setCHouseNo] = useState(formData?.correspondenceAddress?.chouseNo || previousData?.addressDetails?.[1]?.houseNo || editdata?.addressDetails?.[1]?.houseNo || "");
  const [clandmark, setCLandmark] = useState(formData?.correspondenceAddress?.clandmark || previousData?.addressDetails?.[1]?.landmark || editdata?.addressDetails?.[1]?.landmark || "");
  const [caddressline1, setCAddressline1] = useState(formData?.correspondenceAddress?.caddressline1 || previousData?.addressDetails?.[1]?.addressLine1 || editdata?.addressDetails?.[1]?.addressLine1 || "");
  const [caddressline2, setCAddressline2] = useState(formData?.correspondenceAddress?.caddressline2 || previousData?.addressDetails?.[1]?.addressLine2 || editdata?.addressDetails?.[1]?.addressLine2 || "");
  const [isAddressSame, setIsAddressSame] = useState(formData?.correspondenceAddress?.isAddressSame || previousData?.addressDetails?.[1]?.isAddressSame || editdata?.addressDetails?.[1]?.isAddressSame || false)

  const { control } = useForm();
  const inputStyles = { width: user.type === "EMPLOYEE" ? "50%" : "86%" };

  // The hook fetches the data of localities based on the city selected
  const { data: fetchedLocalities } = Digit.Hooks.useBoundaryLocalities(
    city?.code,
    "revenue",
    {
      enabled: !!city,
    },
    t
  );
  let structuredLocality = [];
  fetchedLocalities && fetchedLocalities.map((localityData, index) => {
    structuredLocality.push({ i18nKey: localityData?.i18nkey, code: localityData?.code, label: localityData?.label, area: localityData?.area, boundaryNum: localityData?.boundaryNum })
  })


  /** The function sets the data in the corresponding address
   *  fields if "same as permanent address" checkbox is selected 
   */
  function selectChecked(e) {
    if (isAddressSame == false) {
      setIsAddressSame(true);
      setCPincode(pincode);
      setCCity(city);
      setCLocality(locality);
      setCHouseNo(houseNo);
      setCLandmark(landmark);
      setCAddressline1(addressline1);
      setCAddressline2(addressline2);
    }
    else {
      setIsAddressSame(false);
      setCPincode("");
      setCCity("");
      setCLocality("");
      setCHouseNo("");
      setCLandmark("");
      setCAddressline1("");
      setCAddressline2("");
    }
  }

  const setAddressPincode = (e) => {
    setPincode(e.target.value);
  };

  const sethouseNo = (e) => {
    setHouseNo(e.target.value);
  };

  const setlandmark = (e) => {
    setLandmark(e.target.value);
  };


  const setaddressline1 = (e) => {
    setAddressline1(e.target.value)
  }

  const setaddressline2 = (e) => {
    setAddressline2(e.target.value)
  }

  const setcAddressPincode = (e) => {
    setCPincode(e.target.value);
  };

  const setchouseNo = (e) => {
    setCHouseNo(e.target.value);
  };

  const setclandmark = (e) => {
    setCLandmark(e.target.value);
  };

  const setcaddressline1 = (e) => {
    setCAddressline1(e.target.value)
  }

  const setcaddressline2 = (e) => {
    setCAddressline2(e.target.value)
  }
  //Custom function fo rthe payload whic we can use while goint to next

  const handleSaveasDraft = () => {
    let vendordetails = [];
    let tenantId = Digit.ULBService.getCitizenCurrentTenant(true);
    const createVendorObject = (formData) => ({
      applicationId: "",
      auditDetails: {
        createdBy: "",
        createdTime: 0,
        lastModifiedBy: "",
        lastModifiedTime: 0
      },
      dob: formData?.owner?.units?.[0]?.vendorDateOfBirth,
      userCategory: formData?.owner?.units?.[0]?.userCategory?.code,
      emailId: formData?.owner?.units?.[0]?.email,
      fatherName: formData?.owner?.units?.[0]?.fatherName,
      gender: formData?.owner?.units?.[0]?.gender?.code.charAt(0),
      id: "",
      mobileNo: formData?.owner?.units?.[0]?.mobileNumber,
      name: formData?.owner?.units?.[0]?.vendorName,
      relationshipType: "VENDOR",
      vendorId: null
    });

    const createSpouseObject = (formData) => ({
      applicationId: "",
      auditDetails: {
        createdBy: "",
        createdTime: 0,
        lastModifiedBy: "",
        lastModifiedTime: 0
      },
      dob: formData?.owner?.units?.[0]?.spouseDateBirth,
      userCategory: formData?.owner?.units?.[0]?.userCategory?.code,
      emailId: "",
      isInvolved: formData?.owner?.spouseDependentChecked,
      fatherName: "",
      gender: "O",
      id: "",
      mobileNo: "",
      name: formData?.owner?.units?.[0]?.spouseName,
      relationshipType: "SPOUSE",
      vendorId: null
    });

    const createDependentObject = (formData) => ({
      applicationId: "",
      auditDetails: {
        createdBy: "",
        createdTime: 0,
        lastModifiedBy: "",
        lastModifiedTime: 0
      },
      dob: formData?.owner?.units?.[0]?.dependentDateBirth,
      userCategory: formData?.owner?.units?.[0]?.userCategory?.code,
      emailId: "",
      isInvolved: formData?.owner?.dependentNameChecked,
      fatherName: "",
      gender: formData?.owner?.units?.[0]?.dependentGender?.code.charAt(0),
      id: "",
      mobileNo: "",
      name: formData?.owner?.units?.[0]?.dependentName,
      relationshipType: "DEPENDENT",
      vendorId: null
    });

    // Helper function to check if a string is empty or undefined
    const isEmpty = (str) => !str || str.trim() === '';

    // Main logic
    if (!isEmpty(formData?.owner?.units?.[0]?.vendorName)) {
      const spouseName = formData?.owner?.units?.[0]?.spouseName;
      const dependentName = formData?.owner?.units?.[0]?.dependentName;

      if (isEmpty(spouseName) && isEmpty(dependentName)) {
        // Case 1: Only vendor exists
        vendordetails = [createVendorObject(formData)];
      } else if (!isEmpty(spouseName) && isEmpty(dependentName)) {
        // Case 2: Both vendor and spouse exist
        vendordetails = [
          createVendorObject(formData),
          createSpouseObject(formData)
        ];
      } else if (!isEmpty(spouseName) && !isEmpty(dependentName)) {
        // Case 3: All three exist (vendor, spouse, and dependent)
        vendordetails = [
          createVendorObject(formData),
          createSpouseObject(formData),
          createDependentObject(formData)
        ];
      }
    }

    const daysOfOperations = formData?.businessDetails?.daysOfOperation;
    const vendingOperationTimeDetails = daysOfOperations
      .filter(day => day.isSelected) // Filter only selected days
      .map(day => ({
        applicationId: "", // Add actual applicationId if available
        auditDetails: {
          createdBy: "", // Adjust these fields based on your data
          createdTime: 0,
          lastModifiedBy: "",
          lastModifiedTime: 0,
        },
        dayOfWeek: day.name.toUpperCase(),
        fromTime: day.startTime,
        toTime: day.endTime,
        id: ""
      }));

    const api_response = sessionStorage.getItem("Response");
    const response = JSON.parse(api_response);

    let streetVendingDetail = {
      addressDetails: [
        {
          addressId: "",
          addressLine1: formData?.address?.addressline1 || addressline1,
          addressLine2: formData?.address?.addressline2 || addressline1,
          addressType: "",
          city: previousData?.addressDetails?.[0]?.city || formData?.address?.city?.name || city?.name,
          cityCode: previousData?.addressDetails?.[0]?.cityCode || formData?.address?.city?.code || city?.code,
          doorNo: "",
          houseNo: formData?.address?.houseNo || houseNo,
          landmark: formData?.address?.landmark || landmark,
          locality: formData?.address?.locality?.i18nKey || locality?.i18nKey,
          localityCode: formData?.address?.locality?.code || locality?.code,
          pincode: formData?.address?.pincode || pincode,
          streetName: "",
          vendorId: ""
        },
        { // sending correspondence address here
          addressId: "",
          addressLine1: caddressline1,
          addressLine2: caddressline2,
          addressType: "",
          city: previousData?.addressDetails?.[1]?.city || ccity?.name,
          cityCode: previousData?.addressDetails?.[1]?.cityCode || ccity?.code,
          doorNo: "",
          houseNo: chouseNo,
          landmark: clandmark,
          locality: clocality?.i18nKey,
          localityCode: clocality?.code,
          pincode: cpincode,
          streetName: "",
          vendorId: "",
          isAddressSame: isAddressSame
        }
      ],
      applicationDate: 0,
      applicationId: "",
      applicationNo: "",
      applicationStatus: "",
      approvalDate: 0,
      auditDetails: {
        createdBy: "",
        createdTime: 0,
        lastModifiedBy: "",
        lastModifiedTime: 0
      },
      bankDetail: {
        accountHolderName: "",
        accountNumber: "",
        applicationId: "",
        bankBranchName: "",
        bankName: "",
        id: "",
        ifscCode: "",
        refundStatus: "",
        refundType: "",
        auditDetails: {
          createdBy: "",
          createdTime: 0,
          lastModifiedBy: "",
          lastModifiedTime: 0
        },
      },
      benificiaryOfSocialSchemes: [],
      applicationCreatedBy: formData?.owner?.applicationCreatedBy,
      locality: formData?.businessDetails?.vendorLocality?.code || "",
      localityValue: "",
      vendingZoneValue: "",
      vendorPaymentFrequency: formData?.businessDetails?.vendingPayment?.code,
      enrollmentId: "",
      cartLatitude: 0,
      cartLongitude: 0,
      certificateNo: null,
      disabilityStatus: "",
      draftId: previousData?.draftId || response?.SVDetail?.draftId,
      documentDetails: [
        {
          applicationId: "",
          auditDetails: {
            createdBy: "",
            createdTime: 0,
            lastModifiedBy: "",
            lastModifiedTime: 0
          },
          documentDetailId: "",
          documentType: "",
          fileStoreId: ""
        }
      ],
      localAuthorityName: formData?.businessDetails?.nameOfAuthority,
      tenantId: tenantId,
      termsAndCondition: "Y",
      tradeLicenseNo: formData?.owner?.units?.[0]?.tradeNumber,
      vendingActivity: formData?.businessDetails?.vendingType?.code,
      vendingArea: formData?.businessDetails?.areaRequired || "0",
      vendingLicenseCertificateId: "",
      vendingOperationTimeDetails,
      vendingZone: formData?.businessDetails?.vendingZones?.code,
      vendorDetail: [
        ...vendordetails
      ],
      workflow: {
        action: "APPLY",
        comments: "",
        businessService: "street-vending",
        moduleName: "sv-services",
        businessService: "street-vending",
        moduleName: "sv-services",
        varificationDocuments: [
          {
            additionalDetails: {},
            auditDetails: {
              createdBy: "",
              createdTime: 0,
              lastModifiedBy: "",
              lastModifiedTime: 0
            },
            documentType: "",
            documentUid: "",
            fileStoreId: "",
            id: ""
          }
        ]
      }
    };

    Digit.SVService.create({ streetVendingDetail, draftApplication: true }, tenantId)
      .then(response => {
        sessionStorage.setItem("Response", JSON.stringify(response));
      })
      .catch(error => {
        console.log("Something Went Wrong", error);
      })

  };

  const goNext = () => {
    let owner = formData.address;
    /** Saves the data of the fields in the formdata based on if its correspondence data or not
     * Saves data with different keys
     */
    if (!pathname.includes("correspondence")) {
      let ownerStep = { ...owner, pincode, city, locality, houseNo, landmark, addressline1, addressline2 };
      onSelect(config.key, { ...formData[config.key], ...ownerStep }, false);
      window.location.href.includes("edit") ? null : handleSaveasDraft();
    } else {
      owner = formData?.correspondenceAddress;
      let ownerStep = { ...owner, cpincode, ccity, clocality, chouseNo, clandmark, caddressline1, caddressline2, isAddressSame };
      onSelect(config.key, { ...formData[config.key], ...ownerStep }, false);
      window.location.href.includes("edit") ? null : handleSaveasDraft();
    }
  };


  const onSkip = () => onSelect();

  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [pincode, city, locality, houseNo, landmark, addressline1, addressline2, ccity, chouseNo, clocality, caddressline1, caddressline2]);


  /**
   * Renders the fields for permanent address or correspondence address
   */
  if (!pathname.includes("correspondence")) {
    return (
      <React.Fragment>
        {window.location.href.includes("/citizen") ? <Timeline currentStep={3} /> : null}
        <FormStep
          config={config}
          onSelect={goNext}
          onSkip={onSkip}
          t={t}
          isDisabled={!city || !houseNo || !locality || !addressline1 || !addressline2 || !pincode}
        >
          <div>
            <CardLabel>{`${t("SV_HOUSE_NO")}`} <span className="astericColor">*</span></CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="houseNo"
              value={houseNo}
              placeholder={"Enter House No"}
              onChange={sethouseNo}
              style={inputStyles}
              ValidationRequired={true}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-z0-9- ]*$",
                type: "text",
                title: t("SV_HOUSE_NO_ERROR_MESSAGE"),
              })}
            />

            <CardLabel>{`${t("SV_ADDRESS_LINE1")}`} <span className="astericColor">*</span></CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="addressline1"
              value={addressline1}
              placeholder={"Enter Address"}
              onChange={setaddressline1}
              style={inputStyles}
              ValidationRequired={true}
              {...(validation = {
                isRequired: false,
                pattern: "^[a-zA-Z,-/ ]*$",
                type: "textarea",
                title: t("SV_LANDMARK_ERROR_MESSAGE"),
              })}

            />

            <CardLabel>{`${t("SV_ADDRESS_LINE2")}`} <span className="astericColor">*</span></CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="addressline2"
              value={addressline2}
              placeholder={"Enter Address"}
              onChange={setaddressline2}
              style={inputStyles}
              ValidationRequired={true}
              {...(validation = {
                isRequired: false,
                pattern: "^[a-zA-Z,-/ ]*$",
                type: "textarea",
                title: t("SV_LANDMARK_ERROR_MESSAGE"),
              })}
            />
            <CardLabel>{`${t("SV_LANDMARK")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="landmark"
              value={landmark}
              placeholder={"Enter Landmark"}
              onChange={setlandmark}
              style={inputStyles}
              ValidationRequired={true}
              {...(validation = {
                isRequired: false,
                pattern: "^[a-zA-Z,- ]*$",
                type: "text",
                title: t("SV_LANDMARK_ERROR_MESSAGE"),
              })}
            />

            <CardLabel>{`${t("SV_CITY")}`} <span className="astericColor">*</span></CardLabel>
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
            <CardLabel>{`${t("SV_LOCALITY")}`} <span className="astericColor">*</span></CardLabel>
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
            <CardLabel>{`${t("SV_ADDRESS_PINCODE")}`} <span className="astericColor">*</span></CardLabel>
            <TextInput
              t={t}
              type="tel"
              isMandatory={false}
              optionKey="i18nKey"
              name="pincode"
              value={pincode}
              onChange={setAddressPincode}
              placeholder="Enter Pincode"
              style={inputStyles}
              ValidationRequired={true}
              validation={{
                required: true,
                pattern: "^[0-9]{6}$",
                type: "tel",
                title: t("SV_ADDRESS_PINCODE_INVALID"),
              }}
              maxLength={6}
            />
          </div>
        </FormStep>
      </React.Fragment>
    );
  } else {
    return (
      <React.Fragment>
        {window.location.href.includes("/citizen") ? <Timeline currentStep={3} /> : null}
        <FormStep
          config={config}
          onSelect={goNext}
          onSkip={onSkip}
          t={t}
          isDisabled={!ccity || !chouseNo || !clocality || !caddressline1 || !caddressline2 || !cpincode}
        >
          <div>
            <CheckBox
              label={t("SV_SAME_AS_PERMANENT_ADDRESS")}
              onChange={(e) => selectChecked(e)}
              checked={isAddressSame}
              style={{ paddingBottom: "10px", paddingTop: "10px" }}
            />

            <CardLabel>{`${t("SV_HOUSE_NO")}`} <span className="astericColor">*</span></CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="chouseNo"
              value={chouseNo}
              placeholder={"Enter House No"}
              onChange={setchouseNo}
              style={{ width: user.type === "EMPLOYEE" ? "50%" : "86%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-z0-9- ]*$",
                type: "text",
                title: t("SV_HOUSE_NO_ERROR_MESSAGE"),
              })}
            />

            <CardLabel>{`${t("SV_ADDRESS_LINE1")}`} <span className="astericColor">*</span></CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="caddressline1"
              value={caddressline1}
              placeholder={"Enter Address"}
              onChange={setcaddressline1}
              style={{ width: user.type === "EMPLOYEE" ? "50%" : "86%" }}
              ValidationRequired={true}
              {...(validation = {
                isRequired: false,
                pattern: "^[a-zA-Z,-/ ]*$",
                type: "textarea",
                title: t("SV_LANDMARK_ERROR_MESSAGE"),
              })}

            />

            <CardLabel>{`${t("SV_ADDRESS_LINE2")}`} <span className="astericColor">*</span></CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="caddressline2"
              value={caddressline2}
              placeholder={"Enter Address"}
              onChange={setcaddressline2}
              style={{ width: user.type === "EMPLOYEE" ? "50%" : "86%" }}
              ValidationRequired={true}
              {...(validation = {
                isRequired: false,
                pattern: "^[a-zA-Z,-/ ]*$",
                type: "textarea",
                title: t("SV_LANDMARK_ERROR_MESSAGE"),
              })}
            />
            <CardLabel>{`${t("SV_LANDMARK")}`}</CardLabel>
            <TextArea
              t={t}
              type={"textarea"}
              isMandatory={false}
              optionKey="i18nKey"
              name="clandmark"
              value={clandmark}
              placeholder={"Enter Landmark"}
              onChange={setclandmark}
              style={{ width: "50%" }}
              ValidationRequired={true}
              {...(validation = {
                isRequired: false,
                pattern: "^[a-zA-Z,- ]*$",
                type: "textarea",
                title: t("SV_LANDMARK_ERROR_MESSAGE"),
              })}
            />

            <CardLabel>{`${t("SV_CITY")}`} <span className="astericColor">*</span></CardLabel>
            <Controller
              control={control}
              name={"ccity"}
              defaultValue={ccity}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={ccity}
                  select={setCCity}
                  option={allCities}
                  optionKey="i18nKey"
                  t={t}
                  placeholder={"Select"}
                />
              )}
            />
            <CardLabel>{`${t("SV_LOCALITY")}`} <span className="astericColor">*</span></CardLabel>
            <Controller
              control={control}
              name={"clocality"}
              defaultValue={clocality}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={clocality}
                  select={setCLocality}
                  option={structuredLocality}
                  optionCardStyles={{ overflowY: "auto", maxHeight: "300px" }}
                  optionKey="i18nKey"
                  t={t}
                  placeholder={"Select"}
                />
              )}
            />
            <CardLabel>{`${t("SV_ADDRESS_PINCODE")}`} <span className="astericColor">*</span></CardLabel>
            <TextInput
              t={t}
              type="tel"
              isMandatory={false}
              optionKey="i18nKey"
              name="cpincode"
              value={cpincode}
              onChange={setcAddressPincode}
              placeholder="Enter Pincode"
              style={{ width: user.type === "EMPLOYEE" ? "50%" : "86%" }}
              ValidationRequired={true}
              validation={{
                required: true,
                pattern: "^[0-9]{6}$",
                type: "tel",
                title: t("SV_ADDRESS_PINCODE_INVALID"),
              }}
              minLength={6}
              maxLength={6}
            />


          </div>
        </FormStep>
      </React.Fragment>
    );
  }
};

export default SVAdrressDetails;


