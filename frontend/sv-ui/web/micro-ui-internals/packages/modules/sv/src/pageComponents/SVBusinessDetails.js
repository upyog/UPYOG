import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, Dropdown, LinkButton, Toast, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import GIS from "./GIS";
import Timeline from "../components/Timeline";
import ApplicationTable from "../components/inbox/ApplicationTable";

/**
 * SVBusinessDetails component handles the business details form for street vending applications.
 * It manages state for vending type, zones, location, area required, authority name, license, and days of operation.
 * Includes validation for selected days and time slots, and integrates GIS for location selection.
 * Utilizes react-hook-form for form handling and custom dropdowns for user selections.
 * The goNext function validates input and passes the collected data to the parent component.
 */

const SVBusinessDetails = ({ t, config, onSelect, userType, formData, editdata, previousData }) => {
  let validation = {};
  const user = Digit.UserService.getUser().info;
  const allCities = Digit.Hooks.sv.useTenants();

  const convertToObject = (String) => String ? { i18nKey: String, code: String, value: String } : null;
  const [vendingType, setvendingType] = useState(formData?.businessDetails?.vendingType || convertToObject(previousData?.vendingActivity || editdata?.vendingActivity) || formData?.businessDetails?.vendingType || "");
  const [vendingZones, setvendingZones] = useState(formData?.businessDetails?.vendingZones || "");
  const [location, setlocation] = useState(formData?.businessDetails?.location || "");
  const [vendorLocality, setVendorLocality] = useState(formData?.businessDetails?.vendorLocality || "");
  const [areaRequired, setareaRequired] = useState(formData?.businessDetails?.areaRequired || previousData?.vendingArea || editdata?.vendingArea || "2.12");
  const [nameOfAuthority, setnameOfAuthority] = useState(formData?.businessDetails?.nameOfAuthority || previousData?.localAuthorityName || editdata?.localAuthorityName || "");
  const [vendingPayment, setVendingPayment] = useState(formData?.businessDetails?.vendingPayment || convertToObject(previousData?.vendorPaymentFrequency || editdata?.vendorPaymentFrequency) || formData?.businessDetails?.vendorPaymentFrequency || "");
  const [vendingLiscence, setvendingLiscence] = useState(formData?.businessDetails?.vendingLiscence || previousData?.vendingLiscence || editdata?.vendingLiscence || "");
  const inputStyles = { width: user.type === "EMPLOYEE" ? "50%" : "86%" };
  const [showToast, setShowToast] = useState(null);
  const [isSameForAll, setIsSameForAll] = useState(previousData?.vendingOperationTimeDetails?.length === 7 || editdata?.vendingOperationTimeDetails?.length === 7 ? true : false || formData?.businessDetails?.isSameForAll); // Flag to check if same for all days 
  const [daysOfOperation, setDaysOfOperation] = useState( // Array to store selected days of operation
    formData?.businessDetails?.daysOfOperation || [
      { name: "Monday", isSelected: false, startTime: previousData?.vendingOperationTimeDetails?.[0]?.fromTime || editdata?.vendingOperationTimeDetails?.[0]?.fromTime || "", endTime: previousData?.vendingOperationTimeDetails?.[0]?.toTime || editdata?.vendingOperationTimeDetails?.[0]?.toTime || "" },
      { name: "Tuesday", isSelected: false, startTime: previousData?.vendingOperationTimeDetails?.[1]?.fromTime || editdata?.vendingOperationTimeDetails?.[1]?.fromTime || "", endTime: previousData?.vendingOperationTimeDetails?.[1]?.toTime || editdata?.vendingOperationTimeDetails?.[1]?.toTime || "" },
      { name: "Wednesday", isSelected: false, startTime: previousData?.vendingOperationTimeDetails?.[2]?.fromTime || editdata?.vendingOperationTimeDetails?.[2]?.fromTime || "", endTime: previousData?.vendingOperationTimeDetails?.[2]?.toTime || editdata?.vendingOperationTimeDetails?.[2]?.toTime || "" },
      { name: "Thursday", isSelected: false, startTime: previousData?.vendingOperationTimeDetails?.[3]?.fromTime || editdata?.vendingOperationTimeDetails?.[3]?.fromTime || "", endTime: previousData?.vendingOperationTimeDetails?.[3]?.toTime || editdata?.vendingOperationTimeDetails?.[3]?.toTime || "" },
      { name: "Friday", isSelected: false, startTime: previousData?.vendingOperationTimeDetails?.[4]?.fromTime || editdata?.vendingOperationTimeDetails?.[4]?.fromTime || "", endTime: previousData?.vendingOperationTimeDetails?.[4]?.toTime || editdata?.vendingOperationTimeDetails?.[4]?.toTime || "" },
      { name: "Saturday", isSelected: false, startTime: previousData?.vendingOperationTimeDetails?.[5]?.fromTime || editdata?.vendingOperationTimeDetails?.[5]?.fromTime || "", endTime: previousData?.vendingOperationTimeDetails?.[5]?.toTime || editdata?.vendingOperationTimeDetails?.[5]?.toTime || "" },
      { name: "Sunday", isSelected: false, startTime: previousData?.vendingOperationTimeDetails?.[6]?.fromTime || editdata?.vendingOperationTimeDetails?.[6]?.fromTime || "", endTime: previousData?.vendingOperationTimeDetails?.[6]?.toTime || editdata?.vendingOperationTimeDetails?.[6]?.toTime || "" },
    ]
  );
  const [backupDays, setBackupDays] = useState(formData?.businessDetails?.backupDays || [...daysOfOperation]); // Backup array to store original days of operation


  // Fetch vending zones based on selected locality (vendorLocality or editdata.locality)
  // Uses Digit.Hooks.useBoundaryLocalities to get vending zones for the selected city/locality
  // The result is mapped to a structured array for use in dropdowns
  const { data: fetchedVendingZones } = Digit.Hooks.useBoundaryLocalities(
    editdata?.locality || vendorLocality?.code,
    "vendingzones",
    {
      enabled: !!vendorLocality,
    },
    t
  );
  let structuredVendingZone = [];
  fetchedVendingZones && fetchedVendingZones.map((vendingData) => {
    structuredVendingZone.push({ i18nKey: vendingData?.i18nkey, code: vendingData?.code, value: vendingData?.name })
  })

  /**
 * useEffect hook to synchronize vendor locality and vending zones based on editdata and previousData.
 *
 * - If `editdata.locality` is present and `allCities` are loaded, sets the vendor locality to the matching city.
 * - If `editdata.vendingZone` is present and vending zones are loaded, sets the vending zone to the matching zone.
 * - If `previousData.locality` is present and `allCities` are loaded, sets the vendor locality to the matching city.
 * - If `previousData.vendingZone` is present and vending zones are loaded, sets the vending zone to the matching zone.
 *
 * Dependencies: [allCities, editdata?.locality, fetchedVendingZones, previousData?.locality]
 */
  useEffect(() => {
  if (editdata?.locality && allCities && allCities.length > 0) {
    allCities.map((city) => {
      if (city.code === editdata?.locality) setVendorLocality(city);
    }
    )
  }
  if(editdata?.vendingZone && structuredVendingZone) {
    structuredVendingZone.map((zone) => {
      if (zone.code === editdata?.vendingZone) setvendingZones(zone);
    })
  }
  if (previousData?.locality && allCities && allCities.length > 0) {
    allCities.map((city) => {
      if (city.code === previousData?.locality) setVendorLocality(city);
    }
    )
  }
  if(previousData?.vendingZone && structuredVendingZone) {
    structuredVendingZone.map((zone) => {
      if (zone.code === previousData?.vendingZone) setvendingZones(zone);
    })
  }
  }, [allCities, editdata?.locality, fetchedVendingZones, previousData?.locality]);

  /* this checks two conditions:
   1. At least one day of the week is selected.
   2. If a day is selected, both the startTime and endTime for that day are filled.*/

  const validateDaysOfOperation = () => {
    const atLeastOneDaySelected = daysOfOperation.some(day => day.startTime, day => day.endTime);
    return atLeastOneDaySelected;
  };


  /**
   * 
      Scenario 1: Select All behavior

      User selects Monday with 9:00-17:00
      User clicks "Select All" → All days get 9:00-17:00 ✅

      Scenario 2: Reselect after deselect

      Monday, Tuesday, Wednesday all have 9:00-17:00
      User deselects Tuesday → Tuesday times cleared
      User reselects Tuesday → Tuesday automatically gets 9:00-17:00 (same as other selected days) ✅}
   */


  const handleRowChange = (rowIndex) => {
  const updatedDays = [...daysOfOperation];
  const isCurrentlySelected = updatedDays[rowIndex].isSelected;
  
  if (isCurrentlySelected) {
    // Deselecting: Clear the times
    updatedDays[rowIndex] = {
      ...updatedDays[rowIndex],
      isSelected: false,
      startTime: "",
      endTime: ""
    };
  } else {
    // Reselecting: Find the first selected day's time and apply it
    const firstSelectedDay = updatedDays.find(day => day.isSelected && (day.startTime || day.endTime));
    
    updatedDays[rowIndex] = {
      ...updatedDays[rowIndex],
      isSelected: true,
      startTime: firstSelectedDay?.startTime || "",
      endTime: firstSelectedDay?.endTime || ""
    };
  }
  
  setDaysOfOperation(updatedDays);
};


  // function to handle day time selection
  const onTimeChange = (index, time, value) => {
  let updatedDays = [...daysOfOperation];
  updatedDays[index][time] = value;

  if (updatedDays[index].startTime || updatedDays[index].endTime) {
    updatedDays[index].isSelected = true;
  } else {
    updatedDays[index].isSelected = false;
  }

  // Apply change only to selected rows or the edited row itself
  if (isSameForAll) {
    const { startTime, endTime } = updatedDays[index];

    updatedDays = updatedDays.map((day, i) => {
      if (day.isSelected || i === index) {
        return {
          ...day,
          startTime,
          endTime,
        };
      }
      return day;
    });
  }

  setDaysOfOperation(updatedDays);
};

  // Handle toggle for isSameForAll and apply backup if needed
  const handleToggleIsSameForAll = () => {
    if (!isSameForAll) {
      setBackupDays([...daysOfOperation]);

      const { startTime, endTime, isSelected } = daysOfOperation.find(
        (day) => day.startTime || day.endTime
      ) || daysOfOperation[0];

      // Set all rows to have the same start and end times
      setDaysOfOperation(
        daysOfOperation.map((day) => ({
          ...day,
          startTime,
          endTime,
          isSelected
        }))
      );
    } else {
      setDaysOfOperation([...backupDays]);
    }

    setIsSameForAll(!isSameForAll);
  };

  // Checkbox column setup
  const checkboxColumn = {
    id: "selection",
    Header: () => (
      <div className="SVTableHeader">
        <input
          className="SVCheckbox"
          type="checkbox"
          checked={isSameForAll}
          disabled={!validateDaysOfOperation()}
          onChange={handleToggleIsSameForAll}
        />
        <h4 style={{ marginLeft: "12px" }}>{t("SV_SELECT_ALL")}</h4>
      </div>
    ),
    Cell: ({ row }) => (
      <div className="SVTableCells">
        <input className="SVCheckbox" type="checkbox" checked={daysOfOperation[row.index].isSelected} onChange={() => handleRowChange(row.index)} />
      </div>
    ),
  };

  // columns for table
  const columns = [
    checkboxColumn,
    { Header: t("SV_DAY"), accessor: "name" },
    {
      Header: t("SV_FROM_TIME"),
      accessor: "startTime",
      Cell: ({ row }) => (
        <TextInput
          style={{ width: "126px" }}
          type="time"
          name="startTime"
          value={daysOfOperation[row.index]?.startTime || ""}
          onChange={(e) => onTimeChange(row.index, "startTime", e.target.value)}
          placeholder={"Start Time"}
        />
      ),
    },
    {
      Header: t("SV_TO_TIME"),
      accessor: "endTime",
      Cell: ({ row }) => (
        <TextInput
          style={{ width: "128px" }}
          type="time"
          name="endTime"
          value={daysOfOperation[row.index]?.endTime || ""}
          onChange={(e) => onTimeChange(row.index, "endTime", e.target.value)}
          placeholder={"End Time"}
        />
      ),
    },
  ];

  const vendingPaymentOptions = [
    {
      i18nKey: "Monthly",
      code: "MONTHLY",
      value: "Monthly"
    },
    {
      i18nKey: "Quarterly",
      code: "QUARTERLY",
      value: "Quarterly"
    },
    {
      i18nKey: "Half Yearly",
      code: "HALFYEARLY",
      value: "Half Yearly"
    },
    {
      i18nKey: "Yearly",
      code: "YEARLY",
      value: "Yearly"
    }
  ]

  const { control } = useForm();
  const [isOpen, setIsOpen] = useState(false);

  const { data: vendingTypeData } = Digit.Hooks.useEnabledMDMS(Digit.ULBService.getStateId(), "StreetVending", [{ name: "VendingActivityType" }],
    {
      select: (data) => {
        const formattedData = data?.["StreetVending"]?.["VendingActivityType"]
        return formattedData;
      },
    });
  let vendingTypeOptions = [];
  vendingTypeData && vendingTypeData.map((vending) => {
    vendingTypeOptions.push({ i18nKey: `${vending.name}`, code: `${vending.code}`, value: `${vending.name}` })
  })

  const handleGIS = () => {
    setIsOpen(!isOpen);
  }

  const handleRemove = () => {
    setIsOpen(!isOpen);
  }

  function onSave(location) {
    setlocation(location);
    setIsOpen(false);
  }

  function selectlocation(e) {
    formData.businessDetails["location"] = (typeof e === 'object' && e !== null) ? e.target.value : e;
    // setlocation((typeof e === 'object' && e !== null) ? e.target.value : e);
    setlocation(e.target.value);
  }

  function setNameOfAuthority(e) {
    setnameOfAuthority(e.target.value);
  }
  function setVendingLiscence(e) {
    setvendingLiscence(e.target.value);
  }

  function setAreaRequired(e) {
    if (/[a-zA-Z]/.test(e.target.value)) {
      setShowToast({ error: true, label: t("SV_CITIZEN_AREA_VALIDATION") });
    }
    setareaRequired(e.target.value);
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

    const daysOfOperations = daysOfOperation;
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
          addressLine1: "",
          addressLine2: "",
          addressType: "",
          city: "",
          cityCode: "",
          doorNo: "",
          houseNo: "",
          landmark: "",
          locality: "",
          localityCode: "",
          pincode: "",
          streetName: "",
          vendorId: ""
        },
        { // sending correspondence address here
          addressId: "",
          addressLine1: "",
          addressLine2: "",
          addressType: "",
          city: "",
          cityCode: "",
          doorNo: "",
          houseNo: "",
          landmark: "",
          locality: "",
          localityCode: "",
          pincode: "",
          streetName: "",
          vendorId: "",
          isAddressSame: ""
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
      locality: vendorLocality?.code,
      localityValue: "",
      vendingZoneValue: "",
      vendorPaymentFrequency: vendingPayment?.code,
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
      localAuthorityName: nameOfAuthority,
      tenantId: tenantId,
      termsAndCondition: "Y",
      tradeLicenseNo: formData?.owner?.units?.[0]?.tradeNumber,
      vendingActivity: vendingType?.code,
      vendingArea: areaRequired || "0",
      vendingLicenseCertificateId: "",
      vendingOperationTimeDetails,
      vendingZone: vendingZones?.code,
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
    if (!validateDaysOfOperation()) {
      setShowToast({ error: true, label: t("SV_TIME_OPERATIONS_VALIDATION") });
      return;
    }
    let business = formData.businessDetails;
    let businessStep;

    businessStep = { ...business, vendingType, vendingZones, location, areaRequired, nameOfAuthority, vendingLiscence, daysOfOperation, isSameForAll, backupDays, vendorLocality, vendingPayment };
    onSelect(config.key, businessStep, false);
    window.location.href.includes("edit") ? null : handleSaveasDraft();
  };

  const onSkip = () => onSelect();



  useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => {
        setShowToast(null);
      }, 2000); // Close toast after 1 seconds

      return () => clearTimeout(timer); // Clear timer on cleanup
    }
  }, [showToast]);
  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [vendingType, vendingZones, location, areaRequired, nameOfAuthority, vendingLiscence, daysOfOperation, vendorLocality, vendingPayment]);


  return (
    <React.Fragment>
      {
        <Timeline currentStep={2} />
      }
      <div>
        {isOpen && <GIS t={t} onSelect={onSelect} formData={formData} handleRemove={handleRemove} onSave={onSave} />}

        {!isOpen && <FormStep
          config={config}
          onSelect={goNext}
          onSkip={onSkip}
          t={t}
          isDisabled={!vendingType || !vendingZones || (vendingType?.code === "STATIONARY" ? !areaRequired : null) || !nameOfAuthority || !daysOfOperation || !vendorLocality || !vendingPayment}
        >

          <div
            style={{
              border: "solid",
              borderRadius: "5px",
              padding: "10px",
              paddingTop: "20px",
              marginTop: "10px",
              borderColor: "#f3f3f3",
              background: "#FAFAFA",
              width: inputStyles,
            }}
          >
            <CardLabel>{`${t("SV_VENDING_TYPE")}`} <span className="astericColor">*</span></CardLabel>
            <Controller
              control={control}
              name={"vendingType"}
              defaultValue={vendingType}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={vendingType}
                  select={setvendingType}
                  option={vendingTypeOptions}
                  optionKey="i18nKey"
                  t={t}
                  placeholder={"Select"}
                />
              )}
            />
            <CardLabel>{`${t("SV_VENDING_LOCALITY")}`} <span className="astericColor">*</span></CardLabel>
            <Controller
              control={control}
              name={"locality"}
              defaultValue={vendorLocality}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={vendorLocality}
                  select={setVendorLocality}
                  option={allCities}
                  optionCardStyles={{ overflowY: "auto", maxHeight: "300px" }}
                  optionKey="i18nKey"
                  t={t}
                  placeholder={"Select"}
                />
              )}
            />
            <CardLabel>{`${t("SV_VENDING_ZONES")}`} <span className="astericColor">*</span></CardLabel>
            <Controller
              control={control}
              name={"vendingZones"}
              defaultValue={vendingZones}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={vendingZones}
                  select={setvendingZones}
                  option={structuredVendingZone}
                  optionCardStyles={{ overflowY: "auto", maxHeight: "300px" }}
                  optionKey="i18nKey"
                  t={t}
                  placeholder={"Select"}
                />
              )}
            />
            <CardLabel>{`${t("SV_LOCATION")}`}</CardLabel>
            <div>
              <TextInput
                isMandatory={false}
                optionKey="i18nKey"
                t={t}
                style={inputStyles}
                name="location"
                value={location}
                onChange={selectlocation}
              />
              <LinkButton
                label={
                  <div>
                    <span>
                      <svg
                        style={{ float: "right", position: "relative", bottom: "25px", marginTop: "-17px", marginRight: "51%" }} width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M11 7C8.79 7 7 8.79 7 11C7 13.21 8.79 15 11 15C13.21 15 15 13.21 15 11C15 8.79 13.21 7 11 7ZM19.94 10C19.48 5.83 16.17 2.52 12 2.06V0H10V2.06C5.83 2.52 2.52 5.83 2.06 10H0V12H2.06C2.52 16.17 5.83 19.48 10 19.94V22H12V19.94C16.17 19.48 19.48 16.17 19.94 12H22V10H19.94ZM11 18C7.13 18 4 14.87 4 11C4 7.13 7.13 4 11 4C14.87 4 18 7.13 18 11C18 14.87 14.87 18 11 18Z" fill="#a82227" />
                      </svg>
                    </span>
                  </div>
                }
                onClick={(e) => handleGIS()}
              />
            </div>
          </div>

          <div
            style={{
              border: "solid",
              borderRadius: "5px",
              padding: "10px",
              paddingTop: "20px",
              marginTop: "10px",
              borderColor: "#f3f3f3",
              background: "#FAFAFA",
              width: inputStyles,
            }}
          >
            <CardLabel>{t("SV_DAY_HOUR_OPERATION")} <span className="astericColor">*</span></CardLabel>
            <ApplicationTable
              t={t}
              styles={{ width: "500px" }}
              data={daysOfOperation}
              columns={columns}
              getCellProps={(cellInfo) => ({
                style: {
                  padding: "4px 0px 0px 18px",
                  height: "18px",
                },
              })}
              isPaginationRequired={false}
            />
          </div>
          <div
            style={{
              border: "solid",
              borderRadius: "5px",
              padding: "10px",
              paddingTop: "20px",
              marginTop: "10px",
              borderColor: "#f3f3f3",
              background: "#FAFAFA",
              width: inputStyles,
            }}
          >

            {vendingType?.code === "STATIONARY" &&
              <React.Fragment>
                <CardLabel>{`${t("SV_AREA_REQUIRED")}`} <span className="astericColor">*</span></CardLabel>
                <TextInput
                  t={t}
                  type={"text"}
                  isMandatory={false}
                  optionKey="i18nKey"
                  name="areaRequired"
                  value={areaRequired}
                  onChange={setAreaRequired}
                  style={inputStyles}
                  ValidationRequired={false}
                />
              </React.Fragment>
            }

            <CardLabel>{`${t("SV_LOCAL_AUTHORITY_NAME")}`} <span className="astericColor">*</span></CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="nameOfAuthority"
              value={nameOfAuthority}
              onChange={setNameOfAuthority}
              style={inputStyles}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-/ ]*$",
                type: "text",
                title: t("SV_INPUT_DID_NOT_MATCH"),
              })}
            />

            <CardLabel>{`${t("SV_VENDING_PAYMENT")}`} <span className="astericColor">*</span></CardLabel>
            <Controller
              control={control}
              name={"vendingPayment"}
              defaultValue={vendingPayment}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={vendingPayment}
                  select={setVendingPayment}
                  option={vendingPaymentOptions}
                  optionKey="i18nKey"
                  t={t}
                  placeholder={"Select"}
                />
              )}
            />

            <CardLabel>{`${t("SV_VENDING_LISCENCE")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="vendingLiscence"
              value={vendingLiscence}
              onChange={setVendingLiscence}
              style={inputStyles}
              ValidationRequired={false}
              {...(validation = {
                isRequired: false,
                pattern: "^[a-zA-Z0-9-/ ]+$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
          </div>

        </FormStep>}
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
      </div>
    </React.Fragment >
  );
};

export default SVBusinessDetails;