import React, { useEffect, useState, useMemo } from "react";
import {
  FormStep,
  TextInput,
  LocationIcon,
  InfoBannerIcon,
  Dropdown,
  CardHeader,
  CardLabel,
  UploadFile,
  Toast,
  LabelFieldPair,
  TextArea
} from "@nudmcdgnpm/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import exifr from "exifr";
import { assetStyles } from "../utils/assetStyles";
import { validateMandatoryFields } from "../utils";
import { MarkOnMap } from "@nudmcdgnpm/upyog-ui-module-gis";

const AssetAllDetails = ({ t, config, onSelect, userType, formData }) => {
  const { control } = useForm({
    defaultValues: {
      city: formData?.address?.city || "",
      locality: formData?.address?.locality || "",
    },
  });
  const allCities = Digit.Hooks.asset.useTenants();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateTenantId = Digit.ULBService.getStateId();
  let validation = {};
  const calculateCurrentFinancialYear = () => {
    const currentDate = new Date();
    const year = currentDate.getFullYear();
    const month = currentDate.getMonth() + 1; // getMonth() is zero-based
    if (month >= 4) {
      return `${year}-${year + 1}`;
    } else {
      return `${year - 1}-${year}`;
    }
  };

  const initialFinancialYear = calculateCurrentFinancialYear();

  // data set priveis
  const [assetclassification, setassetclassification] = useState(formData?.asset?.assetclassification || "");
  const [assettype, setassettype] = useState(formData?.asset?.assettype || "");

  const [assetsubtype, setassetsubtype] = useState(formData?.asset?.assetsubtype || "");

  const [showToast, setShowToast] = useState(null);
  // const [assetparentsubCategory, setassetparentsubCategory] = useState(
  //    formData?.asset?.assetparentsubCategory || ""
  // );

  const [BookPagereference, setBookPagereference] = useState(formData?.asset?.BookPagereference || "");
  const [AssetName, setAssetName] = useState(formData?.asset?.AssetName || "");
  const [Assetdescription, setAssetdescription] = useState(formData?.asset?.Assetdescription || "");
  const [Department, setDepartment] = useState(formData?.asset?.Department || "");

  const [financialYear, setfinancialYear] = useState(formData?.asset?.financialYear || initialFinancialYear);
  const [sourceOfFinance, setsourceOfFinance] = useState(formData?.asset?.sourceOfFinance || "");
  const [address, setAddress] = useState(formData?.address || {});

  // Document State - NEW
  const [documents, setDocuments] = useState(formData?.documents?.documents || []);
  const [documentError, setDocumentError] = useState(null);
  const [enableSubmit, setEnableSubmit] = useState(true);
  const [checkRequiredFields, setCheckRequiredFields] = useState(true);

  const [geometry, setGeometry] = useState(formData.assetDetails?.geometry || null);
  const [showMap, setShowMap] = useState(false);
  const [area, setArea] = useState(formData.assetDetails?.area || null);

  const [assetDetails, setAssetDetails] = useState(
    formData?.assetDetails
      ? formData?.assetDetails
      : {
        assetParentCategory: formData?.asset?.assettype?.code,
        geometry: formData?.assetDetails?.geometry || null,
        area: formData?.assetDetails?.area || null,
      }
  );

  const cities = allCities?.filter((city) => city.code === tenantId);

  const { data: fetchedLocalities } = Digit.Hooks.useBoundaryLocalities(
    tenantId,
    "revenue",
    {
      enabled: !!tenantId,
    },
    t
  );

  useEffect(() => {
    if (cities && cities.length === 1) {
      setAddress((prev) => ({ ...prev, city: cities[0] }));
    }
  }, [tenantId]);

  let structuredLocality = [];
  fetchedLocalities &&
    fetchedLocalities.map((localityData, index) => {
      structuredLocality.push({
        i18nKey: localityData?.i18nkey,
        code: localityData?.code,
        label: localityData?.label,
        area: localityData?.area,
        boundaryNum: localityData?.boundaryNum,
      });
    });

  const [currentLocation, setCurrentLocation] = useState(null);

  const [categoriesWiseData, setCategoriesWiseData] = useState();

  // const { data: Menu_Asset } = Digit.Hooks.asset.useAssetClassification(stateTenantId, "ASSETV2", "assetClassification"); // hook for asset classification Type
  const { data: Menu_Asset } = Digit.Hooks.useEnabledMDMS(Digit.ULBService.getStateId(), "ASSETV2", [{ name: "assetClassification" }], {
    select: (data) => {
      const formattedData = data?.["ASSETV2"]?.["assetClassification"];
      const activeData = formattedData?.filter((item) => item.active === true);
      return activeData;
    },
  });

  // const { data: Asset_Type } = Digit.Hooks.asset.useAssetType(stateTenantId, "ASSETV2", "assetParentCategory");
  const { data: Asset_Type } = Digit.Hooks.useEnabledMDMS(Digit.ULBService.getStateId(), "ASSETV2", [{ name: "assetParentCategory" }], {
    select: (data) => {
      const formattedData = data?.["ASSETV2"]?.["assetParentCategory"];
      const activeData = formattedData?.filter((item) => item.active === true);
      return activeData;
    },
  });

  // const { data: Asset_Sub_Type } = Digit.Hooks.asset.useAssetSubType(stateTenantId, "ASSETV2", "assetCategory"); // hooks for Asset Parent Category

  const { data: Asset_Sub_Type } = Digit.Hooks.useEnabledMDMS(Digit.ULBService.getStateId(), "ASSETV2", [{ name: "assetCategory" }], {
    select: (data) => {
      const formattedData = data?.["ASSETV2"]?.["assetCategory"];
      return formattedData;
    },
  });

  // const { data: Asset_Parent_Sub_Type } = Digit.Hooks.asset.useAssetparentSubType(stateTenantId, "ASSETV2", "assetSubCategory");

  // For Sub Catagories
  const { data: Asset_Parent_Sub_Type } = Digit.Hooks.useEnabledMDMS(Digit.ULBService.getStateId(), "ASSETV2", [{ name: "assetSubCategory" }], {
    select: (data) => {
      const formattedData = data?.["ASSETV2"]?.["assetSubCategory"];
      const activeData = formattedData?.filter((item) => item.active === true);
      return activeData;
    },
  });


  const { data: sourceofFinanceMDMS } = Digit.Hooks.useEnabledMDMS(Digit.ULBService.getStateId(), "ASSETV2", [{ name: "SourceFinance" }], {
    select: (data) => {
      const formattedData = data?.["ASSETV2"]?.["SourceFinance"];
      const activeData = formattedData?.filter((item) => item.active === true);
      return activeData;
    },
  }); // Note : used direct custom MDMS to get the Data ,Do not copy and paste without understanding the Context

  let sourcefinance = [];

  sourceofFinanceMDMS &&
    sourceofFinanceMDMS.map((finance) => {
      sourcefinance.push({ i18nKey: `AST_${finance.code}`, code: `${finance.code}`, value: `${finance.name}` });
    });

  const { data: assetCurrentUsageData } = Digit.Hooks.useEnabledMDMS(Digit.ULBService.getStateId(), "ASSETV2", [{ name: "AssetUsage" }], {
    select: (data) => {
      const formattedData = data?.["ASSETV2"]?.["AssetUsage"];
      return formattedData;
    },
  });
  let assetCurrentUsage = [];

  assetCurrentUsageData &&
    assetCurrentUsageData.map((assT) => {
      assetCurrentUsage.push({ i18nKey: `${assT.code}`, code: `${assT.code}`, value: `${assT.name}` });
    });

  // This is use for Asset Assigned / Not Assigned menu
  let assetAssignableMenu = [
    { i18nKey: "YES", code: "YES", value: "YES" },
    { i18nKey: "NO", code: "NO", value: "NO" },
  ];

  const { data: currentFinancialYear } = Digit.Hooks.useEnabledMDMS(Digit.ULBService.getStateId(), "ASSETV2", [{ name: "FinancialYear" }], {
    select: (data) => {
      const formattedData = data?.["ASSETV2"]?.["FinancialYear"];
      return formattedData;
    },
  });

  let financal = [];

  currentFinancialYear &&
    currentFinancialYear.map((financialyear) => {
      financal.push({ i18nKey: `${financialyear.code}`, code: `${financialyear.code}`, value: `${financialyear.name}` });
    });

  const { data: departmentName } = Digit.Hooks.useEnabledMDMS(Digit.ULBService.getStateId(), "common-masters", [{ name: "Department" }], {
    select: (data) => {
      const formattedData = data?.["common-masters"]?.["Department"];
      const activeData = formattedData?.filter((item) => item.active === true);
      return activeData;
    },
  });

  // This call with stateTenantId (Get state-level data)
  const stateResponseObject = Digit.Hooks.useEnabledMDMS(stateTenantId, "ASSETV2", [{ name: "AssetParentCategoryFields" }], {
    select: (data) => {
      const formattedData = data?.["ASSETV2"]?.["AssetParentCategoryFields"];
      return formattedData;
    },
  });

  const { isLoading: isDocumentLoading, data: documentData } = Digit.Hooks.asset.useAssetDocumentsMDMS(stateTenantId, "ASSET", "Documents");

  useEffect(() => {
    let combinedData;
    // if city level master is not available then fetch  from state-level
    if (stateResponseObject?.data) {
      combinedData = stateResponseObject.data;
    } else {
      combinedData = []; // Or an appropriate default value for empty data
    }
    setCategoriesWiseData(combinedData);
  }, [stateResponseObject]);

  let formJson = [];
  if (Array.isArray(categoriesWiseData)) {
    // Filter categories based on the selected assetParentCategory
    formJson = categoriesWiseData
      .filter((category) => {
        const isMatch = category.assetParentCategory === assettype?.code || category.assetParentCategory === "COMMON";
        return isMatch;
      })
      .map((category) => category.fields) // Extract the fields array
      .flat() // Flatten the fields array
      .filter((field) => field.active === true && field.isNeeded !== false); // Filter by active status
  };

  const isFormValid = useMemo(() => {
    return validateMandatoryFields(formJson, assetDetails);
  }, [formJson, assetDetails]); // Dependencies: recalculate when these change


  useEffect(() => {
    if (documentData?.ASSET?.Documents) {
      let count = 0;
      documentData.ASSET.Documents.forEach((doc) => {
        doc.hasDropdown = true;
        let isRequired = documents.some((data) => doc.required && data?.documentType.includes(doc.code));
        if (!isRequired && doc.required) count += 1;
      });
      setEnableSubmit(!(count === 0 && documents.length > 0));
    }
  }, [documents, checkRequiredFields, documentData]);

  let departNamefromMDMS = [];

  departmentName &&
    departmentName.map((departmentname) => {
      departNamefromMDMS.push({
        i18nKey: `COMMON_MASTERS_DEPARTMENT_${departmentname.code}`,
        code: `${departmentname.code}`,
        value: `COMMON_MASTERS_DEPARTMENT_${departmentname.code}`,
      });
    });
  let menu_Asset = []; //variable name for assetCalssification
  let asset_type = []; //variable name for asset type
  let asset_sub_type = []; //variable name for asset sub  parent caregory
  let asset_parent_sub_category = [];

  Menu_Asset &&
    Menu_Asset.map((asset_mdms) => {
      if (asset_mdms?.code === assettype?.assetClassification) {
        menu_Asset.push({ i18nKey: `${asset_mdms.name}`, code: `${asset_mdms.code}`, value: `${asset_mdms.name}` });
      }
    });

  Asset_Type &&
    Asset_Type.map((asset_type_mdms) => {
      asset_type.push({
        i18nKey: `${asset_type_mdms.name}`,
        code: `${asset_type_mdms.code}`,
        value: `${asset_type_mdms.name}`,
        assetClassification: `${asset_type_mdms.assetClassification}`,
        depriciationRate: `${asset_type_mdms.depriciationRate}`,
        depriciationMethod: `${asset_type_mdms.depriciationMethod}`,
      });
    });

  Asset_Sub_Type &&
    Asset_Sub_Type.map((asset_sub_type_mdms) => {
      if (asset_sub_type_mdms.assetParentCategory == assettype?.code) {
        asset_sub_type.push({
          i18nKey: `${asset_sub_type_mdms.name}`,
          code: `${asset_sub_type_mdms.code}`,
          value: `${asset_sub_type_mdms.name}`,
        });
      }
    });

  Asset_Parent_Sub_Type &&
    Asset_Parent_Sub_Type.map((asset_parent_mdms) => {
      if (asset_parent_mdms.assetCategory == assetsubtype?.code) {
        asset_parent_sub_category.push({
          i18nKey: `${asset_parent_mdms.name}`,
          code: `${asset_parent_mdms.code}`,
          value: `${asset_parent_mdms.name}`,
        });
      }
    });

  const regexPattern = (columnType) => {
    if (!columnType) {
      return "^[-+]?([1-8]?[0-9](\\.[0-9]+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7][0-9])|([1-9]?[0-9]))(\\.[0-9]+)?)$";
    } else if (columnType === "number") {
      return "^[0-9]+(\\.[0-9]+)?$";
    } else if (columnType === "text") {
      return "^[a-zA-Z0-9 ,./\\-]+$";
    }
  };

  function setassetname(e) {
    setAssetName(e.target.value);
  }
  function setassetDescription(e) {
    setAssetdescription(e.target.value);
  }


  const additionalNumberValidation = (e) => {
    if (e.key === "-" || e.key === "e" || e.key === "E") {
      e.preventDefault();
    }
  };

  const specialCharacterValidation = (e) => {
    // Allow letters, numbers, and space
    if (/^[a-zA-Z0-9 \-/]$/.test(e.key)) {
      return; // valid — do nothing
    }

    // Block only special characters
    if (!/^[a-zA-Z0-9 ]$/.test(e.key)) {
      e.preventDefault();
    }
  };



  // below line will check if mode of Acquistion is any one of them, than it will return the boolena value
  const isCostFieldsDisable = ["DONATED", "GIFTED"].includes(assetDetails?.modeOfPossessionOrAcquisition?.code);



  // Set State Dynamically!
  const handleInputChange = (e) => {
    // Get the name & value from the input and select field
    const { name, value } = e.target ? e.target : { name: e.name, value: e };

    if (name === "area" && value.length > 8) {
      // Validation for life of Asset
      setShowToast({ error: true, label: t("NUMBER_CANT_GO_MORE_THAN_THIS") });
      return false;
    }

    setAssetDetails((prevData) => {
      // Update the current field
      const updatedData = {
        ...prevData,
        [name]: value,
      };

      // Check if both acquisitionCost and purchaseCost are set and calculate bookValue
      const acquisitionCost = parseFloat(updatedData.acquisitionCost) || 0;
      const purchaseCost = parseFloat(updatedData.purchaseCost) || 0;

      if (acquisitionCost >= 0 || purchaseCost >= 0) {
        updatedData.bookValue = acquisitionCost + purchaseCost;
      }

      return updatedData;
    });
  };

  useEffect(() => {
    if (assettype?.assetClassification && menu_Asset.length > 0) {
      // Automatically set the filtered classification
      setassetclassification(menu_Asset[0]);
    } else if (!assettype) {
      // Clear when no parent selected
      setassetclassification("");
    }
  }, [assettype, menu_Asset.length]);

  useEffect(() => {
    if (assettype?.code && assettype?.depriciationRate) {
      // Auto-populate depreciation rate from selected asset type
      setAssetDetails((prevDetails) => ({
        ...prevDetails,
        depriciationRate: assettype.depriciationRate,
        // Also set depreciation method if it exists
        ...(assettype.depriciationMethod && {
          depriciationMethod: {
            code: assettype.depriciationMethod,
            i18nKey: assettype.depriciationMethod,
            value: assettype.depriciationMethod,
          },
        }),
      }));
    } else if (!assettype) {
      // Clear depreciation rate when no asset type is selected
      setAssetDetails((prevDetails) => ({
        ...prevDetails,
        depriciationRate: "",
        depriciationMethod: "",
      }));
    }
  }, [assettype]);

  useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => setShowToast(null), 2000);
      return () => clearTimeout(timer);
    }
  }, [showToast]);

  //  Get location
  const fetchCurrentLocation = (name) => {
    if ("geolocation" in navigator) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const { latitude, longitude } = position.coords;
          const locationString = `${latitude}, ${longitude}`;
          setAssetDetails((prevDetails) => ({
            ...prevDetails,
            [name]: locationString, // Update the specific field
          }));
          // Set the location for the map component
          setCurrentLocation({ lat: latitude, lng: longitude });
        },
        (error) => {
          console.error("Error getting location:", error);
          alert("Unable to retrieve your location. Please check your browser settings.");
        }
      );
    } else {
      alert("Geolocation is not supported by your browser.");
    }
  };

  const goNext = () => {
    // Create separate objects for each section
    const assetData = {
      financialYear,
      sourceOfFinance,
      assetclassification,
      assetsubtype,
      assettype,
      BookPagereference,
      AssetName,
      Assetdescription,
      Department,
    };

    const assetDetailsData = { ...assetDetails };
    const addressData = { ...address };
    const documentsData = { documents };

    // Pass them as a single object that contains all sections
    const allData = {
      asset: assetData,
      assetDetails: assetDetailsData,
      address: addressData,
      documents: documentsData,
    };

    onSelect(config.key, allData, false);
  };

  const onSkip = () => onSelect();


  return (
    <React.Fragment>

      <FormStep
        config={config}
        onSelect={goNext}
        onSkip={onSkip}
        t={t}
        isDisabled={
          !assettype ||
          !assetsubtype ||
          !financialYear ||
          !Department ||
          !isFormValid ||
          !address?.plotNumber || !address?.city || !address?.locality || !address?.addressLineOne || !address?.addressLineTwo
        }
      >
        <div>
          <div style={assetStyles.formGridStyles}>
            <div>
              <div>
                {t("AST_FINANCIAL_YEAR")}
                <span style={{ color: "red" }}>*</span>
                <div className="tooltip" style={assetStyles.toolTip}>
                  <InfoBannerIcon />
                  <span
                    className="tooltiptext"
                    style={assetStyles.toolTipText}
                  >
                    {`${t(`AST_WHICH_FINANCIAL_YEAR`)}`}
                  </span>
                </div>
              </div>
              <Controller
                control={control}
                name={"financialYear"}
                defaultValue={financialYear}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={({ field }) => (
                  <Dropdown selected={financialYear} select={setfinancialYear} option={financal} optionKey="i18nKey" placeholder={"Select"} t={t} style={{ width: "80%" }} />
                )}
              />
            </div>
            <div>
              <div>
                {t("AST_DEPARTMENT")} <span style={{ color: "red" }}>*</span>
                <div className="tooltip" style={assetStyles.toolTip}>
                  <InfoBannerIcon />
                  <span
                    className="tooltiptext"
                    style={assetStyles.toolTipText}
                  >
                    {`${t(`AST_PROCURED_DEPARTMENT`)}`}
                  </span>
                </div>
              </div>
              <Controller
                control={control}
                name={"Department"}
                defaultValue={Department}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <Dropdown
                    selected={Department}
                    select={setDepartment}
                    option={departNamefromMDMS}
                    optionKey="i18nKey"
                    placeholder={"Select"}
                    t={t}
                    style={{ width: "80%" }}
                  />
                )}
              />
            </div>
          </div>
        </div>
        <React.Fragment>
          <CardHeader>{t("ASSET_GENERAL_DETAILS")}</CardHeader>
          <div style={assetStyles.formGridStyles}>
            <div>
              <div>
                {`${t("AST_PARENT_CATEGORY")}`} <span style={{ color: "red" }}>*</span>
              </div>
              <Controller
                control={control}
                name={"assettype"}
                defaultValue={assettype}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <Dropdown selected={assettype} select={setassettype} option={asset_type} optionKey="i18nKey" placeholder={"Select"} t={t} style={{ width: "80%" }} />
                )}
              />
            </div>

            {/* Field 4 - Asset Classification */}
            {/* <div>
              <div>
                {t("AST_CATEGORY")} <span style={{ color: "red" }}>*</span>
                <div className="tooltip" style={ assetStyles.toolTip }>
                  <InfoBannerIcon />
                  <span
                    className="tooltiptext"
                    style={{
                      whiteSpace: "pre-wrap",
                      fontSize: "small",
                      wordWrap: "break-word",
                      width: "300px",
                      marginLeft: "15px",
                      marginBottom: "-10px",
                    }}
                  >
                    {`${t(`AST_CLASSIFICATION_ASSET`)}`}
                  </span>
                </div>
              </div>
              <Controller
                control={control}
                name={"assetclassification"}
                defaultValue={assetclassification}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <Dropdown
                    selected={assetclassification}
                    select={setassetclassification}
                    option={menu_Asset}
                    optionKey="i18nKey"
                    placeholder={"Select"}
                    t={t}
                    disable={true}
                  />
                )}
              />
            </div> */}

            {/* Field 5 - Sub Category */}
            <div>
              <div>
                {`${t("AST_CATEGORY_SUB_CATEGORY")}`} <span style={{ color: "red" }}>*</span>
              </div>
              <Controller
                control={control}
                name={"assetsubtype"}
                defaultValue={assetsubtype}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <Dropdown
                    selected={assetsubtype}
                    select={setassetsubtype}
                    option={asset_sub_type}
                    optionKey="i18nKey"
                    placeholder={"Select"}
                    t={t}
                    style={{ width: "80%" }}
                  />
                )}
              />
            </div>

            {/* Field 6 - Parent Sub Category */}
            {/* <div>
              <div>{`${t("AST_CATEGORY_SUB_CATEGORY")}`}</div>
              <Controller
                control={control}
                name={"assetparentsubCategory"}
                defaultValue={assetparentsubCategory}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <Dropdown
                    selected={assetparentsubCategory}
                    select={setassetparentsubCategory}
                    option={asset_parent_sub_category}
                    optionKey="i18nKey"
                    placeholder={"Select"}
                    t={t}
                  />
                )}
              />
            </div> */}

            {/* Field 10 - Department */}

            {/* Field 8 - Asset Name */}
            <div>
              <div>
                {`${t("AST_NAME")}`} <span style={{ color: "red" }}>*</span>
              </div>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                onKeyPress={specialCharacterValidation}
                name="AssetName"
                value={AssetName}
                onChange={setassetname}
                style={{ width: "80%" }}
                ValidationRequired={true}
                validation={{
                  isRequired: true,
                  // pattern: "^[a-zA-Z ]*$",
                  type: "text",
                  title: t("PT_NAME_ERROR_MESSAGE"),
                }}
              />
            </div>

            {assettype?.code &&
              formJson
                .filter((e) => e.group === "generalDetails")
                .map((row, index) => {
                  if (row.conditionalField) {
                    const { dependsOn, showWhen } = row.conditionalField;
                    if (assetDetails[dependsOn]?.code !== showWhen) {
                      return null;
                    }
                  }

                  return (
                    <div key={index}>
                      <div>
                        {`${t(row.code)}`} {row.isMandatory ? <span style={{ color: "red" }}>*</span> : null}
                        <div className="tooltip" style={assetStyles.toolTip}>
                          <InfoBannerIcon />
                          <span className="tooltiptext" style={assetStyles.toolTipText}>
                            {`${t(row.code + "_INFO")} `}
                          </span>
                        </div>
                      </div>

                      {row.type === "date" ? (
                        <TextInput
                          t={t}
                          type={"date"}
                          isMandatory={false}
                          optionKey="i18nKey"
                          name={row.name}
                          value={assetDetails[row.name]}
                          onChange={handleInputChange}
                          style={{ width: "80%" }}
                          // max={new Date().toISOString().split("T")[0]}
                          rules={{
                            required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                            validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                          }}
                        />
                      ) : row.type === "num" ? (
                        <TextInput
                          t={t}
                          onKeyPress={additionalNumberValidation}
                          optionKey="i18nKey"
                          name={row.name}
                          value={assetDetails[row.name] || ""}
                          onChange={handleInputChange}
                          ValidationRequired={true}
                          validation={{
                            isRequired: true,
                            // pattern: /^[0-9]*$/,
                            type: "number",
                            title: t("PT_NAME_ERROR_MESSAGE"),

                          }}
                          style={{ width: "80%" }}
                        />

                      ) : row.type === "dropdown" ? (
                        <Controller
                          control={control}
                          name={row.name}
                          isMandatory={row.isMandatory}
                          defaultValue={assetDetails[row.name] ? assetDetails[row.name] : ""}
                          rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                          render={(props) => (
                            <Dropdown
                              // className="form-field"
                              selected={assetDetails[row.name]}
                              select={(value) => {
                                handleInputChange({ name: row.name, ...value });
                              }}
                              option={row.options}
                              optionKey="i18nKey"
                              placeholder={"Select"}
                              isMandatory={row.isMandatory}
                              t={t}
                              style={{ width: "80%" }}
                            />
                          )}
                        />
                      ) : (
                        <TextInput
                          t={t}
                          type={row.type}
                          isMandatory={row.isMandatory}
                          optionKey="i18nKey"
                          name={row.name}
                          onKeyPress={specialCharacterValidation}
                          value={assetDetails[row.name] || ""}
                          onChange={handleInputChange}
                          {...(validation = {
                            isRequired: row.isMandatory,
                            pattern: regexPattern(row.columnType),
                            type: row.columnType,
                            title: t("PT_NAME_ERROR_MESSAGE"),
                          })}
                          style={{ width: "80%" }}
                          readOnly={row.isReadOnly}
                          disable={row.disable}
                          placeholder={row.name === "assetId" ? t("AST_AUTO_GENERATE") : ""}
                        />
                      )}
                    </div>
                  );
                })}

            <div>
              <div>
                {`${t("AST_LOCATION_DETAILS")}`} <span style={{ color: "red" }}>*</span>
                <div className="tooltip" style={assetStyles.toolTip}>
                  <InfoBannerIcon />
                  <span className="tooltiptext" style={assetStyles.toolTipText}>
                    {`${t("ASSET_LOCATION_DETAILS")} `}
                  </span>
                </div>
              </div>
              <div style={{ position: "relative" }}>
                <TextInput
                  t={t}
                  type="text"
                  name="location"
                  value={assetDetails["location"] || ""}
                  onChange={handleInputChange}
                  style={{ width: "80%" }}
                  {...{
                    isRequired: true,
                    pattern: "^[-+]?([1-8]?[0-9](\\.[0-9]+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7][0-9])|([1-9]?[0-9]))(\\.[0-9]+)?)$",
                    title: t("VALID_LAT_LONG"),
                  }}
                  placeholder={t("AST_LAT_LONG_PLACEHOLDERS")}
                />
                <div
                  className="butt-icon"
                  onClick={() => fetchCurrentLocation("location")}
                  style={{
                    position: "absolute",
                    right: "21%",
                    top: "50%",
                    transform: "translateY(-50%)",
                    cursor: "pointer",
                    display: "flex",
                    alignItems: "center",
                    padding: "2px 5px",
                  }}
                >
                  <LocationIcon styles={{ width: "16px", border: "none" }} className="fill-path-primary-main" />
                </div>
              </div>

              {assetDetails?.location && (
                <div>
                  <button style={{ color: "#a82227" }} onClick={() => setShowMap(true)}>
                    {geometry ? "Edit Marked Asset" : "Mark Asset on Map"}
                  </button>
                </div>
              )}

              {showMap && (
                <MarkOnMap
                  key={showMap ? "open-map" : "closed-map"}
                  onGeometrySave={(geoJson) => {
                    setGeometry(geoJson);
                    setAssetDetails((prev) => ({
                      ...prev,
                      geometry: geoJson,
                    }));
                  }}
                  onAreaSave={(polygonArea) => {
                    setArea(polygonArea);
                    setAssetDetails((prev) => ({
                      ...prev,
                      area: polygonArea,
                    }));
                  }}
                  savedGeometry={geometry}
                  savedArea={area}
                  closeModal={() => setShowMap(false)}
                  location={currentLocation}
                />
              )}
            </div>

            <div>
              <div>
                {`${t("AST_PLOT_NO")}`} <span style={{ color: "red" }}>*</span>
              </div>
              <TextInput
                t={t}
                type={"tel"}
                isMandatory={false}
                optionKey="i18nKey"
                onKeyPress={specialCharacterValidation}
                name="plotNumber"
                value={address?.plotNumber || ""}
                onChange={(e) => {
                  setAddress({ ...address, plotNumber: e.target.value });
                }}
                {...(validation = {
                  isRequired: true,
                  pattern: regexPattern("text"),
                  type: "text",
                  title: t("VALID_LAT_LONG"),
                })}
                style={{ width: "80%" }}
                maxLength={6}
              />
            </div>

            <div>
              <div>
                {`${t("AST_ADDRESS_LINE_ONE")}`} <span style={{ color: "red" }}>*</span>
              </div>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                onKeyPress={specialCharacterValidation}
                name="addressLineOne"
                value={address?.addressLineOne || ""}
                onChange={(e) => {
                  setAddress({ ...address, addressLineOne: e.target.value });
                }}
                validation={{
                  required: true,
                  pattern: regexPattern("text"),
                  type: "text",
                  title: t("ADDRESS_ONE_INVALID"),
                }}
                style={{ width: "80%" }}
                maxLength={6}
              />
            </div>

            <div>
              <div>
                {`${t("AST_ADDRESS_LINE_TWO")}`} <span style={{ color: "red" }}>*</span>
              </div>
              <TextInput
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                onKeyPress={specialCharacterValidation}
                name="addressLineTwo"
                value={address?.addressLineTwo || ""}
                onChange={(e) => {
                  setAddress({ ...address, addressLineTwo: e.target.value });
                }}
                validation={{
                  required: true,
                  pattern: "^[a-zA-Z0-9/- ]*$",
                  type: "text",
                  title: t("ADDRESS_INVALID"),
                }}
                style={{ width: "80%" }}
                maxLength={6}
              />
            </div>

            <div>
              <div>
                {`${t("AST_PINCODE")}`} <span style={{ color: "red" }}>*</span>
              </div>
              <TextInput
                t={t}
                type={"tel"}
                isMandatory={false}
                optionKey="i18nKey"
                onKeyPress={additionalNumberValidation}
                name="pincode"
                value={address?.pincode || ""}
                onChange={(e) => {
                  setAddress({ ...address, pincode: e.target.value });
                }}
                {...(validation = {
                  isRequired: true,
                  pattern: regexPattern("number"),
                  type: "number",
                  title: t("VALID_LAT_LONG"),
                })}
                style={{ width: "80%" }}
                maxLength={6}
                placeholder={t("ENT_NUMERIC_VALUE_ONLY")}
              />
            </div>
            <div>
              <div>
                {`${t("ASSET_CITY")}`} <span style={{ color: "red" }}>*</span>
              </div>
              <Controller
  control={control}
  name={"city"}
  defaultValue={address?.city || ""}
  rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
  render={({ field }) => (
    <Dropdown
      selected={address?.city}
      select={(value) => {
        field.onChange(value);
        setAddress((prev) => ({
          ...prev,
          city: value,
        }));
      }}
      option={allCities}
      optionKey="code"
      t={t}
      placeholder={"Select"}
      style={{ width: "80%" }}
      disable={true}
    />
  )}
/>
            </div>
            <div>
              <div>
                {`${t("ASSET_LOCALITY")}`} <span style={{ color: "red" }}>*</span>
              </div>

              <Controller
  control={control}
  name={"locality"}
  defaultValue={address?.locality || ""}
  rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
  render={({ field }) => (
    <Dropdown
      selected={address?.locality}
      select={(value) => {
        field.onChange(value);
        setAddress((prev) => ({
          ...prev,
          locality: value,
        }));
      }}
      option={structuredLocality}
      optionCardStyles={{
        overflowY: "auto",
        maxHeight: "300px",
      }}
      optionKey="i18nKey"
      t={t}
      placeholder={"Select"}
      style={{ width: "80%" }}
    />
  )}
/>
            </div>
          </div>
        </React.Fragment>

        <React.Fragment>
          <CardHeader>{t("AST_ACQUSTION_DETAILS")}</CardHeader>

          <div style={assetStyles.formGridStyles}>
            {assetDetails?.modeOfPossessionOrAcquisition?.code === "PURCHASE" && (
              <React.Fragment>
                <div>
                  <div>
                    {`${t("AST_PURCHASE_DATE")}`}
                    <div className="tooltip" style={assetStyles.toolTip}>
                      <InfoBannerIcon />
                      <span className="tooltiptext" style={assetStyles.toolTipText}>
                        {`${t("ASSET_PURCHASE_DATE")}`}
                      </span>
                    </div>
                  </div>
                  <TextInput
                    t={t}
                    type={"date"}
                    isMandatory={false}
                    optionKey="i18nKey"
                    name={"purchaseDate"}
                    value={assetDetails["purchaseDate"]}
                    onChange={handleInputChange}
                    style={{ width: "80%" }}
                    max={new Date().toISOString().split("T")[0]}
                    rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                    }}
                  />
                </div>

                <div>
                  <div>{`${t("AST_PURCHASE_ORDER")}`}</div>
                  <TextInput
                    t={t}
                    type={"text"}
                    isMandatory={false}
                    optionKey="i18nKey"
                    onKeyPress={specialCharacterValidation}
                    name="purchaseOrderNumber"
                    value={assetDetails["purchaseOrderNumber"]}
                    onChange={handleInputChange}
                    validation={{
                      isRequired: false,
                      pattern: "^[a-zA-Z0-9/-]*$",
                      type: "text",
                      title: t("PT_NAME_ERROR_MESSAGE"),
                    }}
                    style={{ width: "80%" }}
                    placeholder={t("ENT_ALPHANUMERIC_PLACEHOLDER")}
                  />
                </div>
              </React.Fragment>
            )}
            {assetDetails?.modeOfPossessionOrAcquisition?.code === "CONSTRUCTED" && (
              <React.Fragment>
                <div>
                  <div>
                    {`${t("AST_ESTIMATED_DATE_CONSTRUCTION")}`}
                    <div className="tooltip" style={assetStyles.toolTip}>
                      <InfoBannerIcon />
                      <span className="tooltiptext" style={assetStyles.toolTipText}>
                        {`${t("ASSET_ESTIMATED_DATE")}`}
                      </span>
                    </div>
                  </div>
                  <TextInput
                    t={t}
                    type={"date"}
                    isMandatory={false}
                    optionKey="i18nKey"
                    name={"estimateConstructionDate"}
                    value={assetDetails["estimateConstructionDate"]}
                    onChange={handleInputChange}
                    style={{ width: "80%" }}
                    max={new Date().toISOString().split("T")[0]}
                    rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                    }}
                  />
                </div>
                <div>
                  <div>
                    {`${t("AST_DATE_CONSTRUCTION")}`}
                    <div className="tooltip" style={assetStyles.toolTip}>
                      <InfoBannerIcon />
                      <span className="tooltiptext" style={assetStyles.toolTipText}>
                        {`${t("ASSET_DATE_CONSTRUCTION")}`}
                      </span>
                    </div>
                  </div>
                  <TextInput
                    t={t}
                    type={"date"}
                    isMandatory={false}
                    optionKey="i18nKey"
                    name={"dateOfConstruction"}
                    value={assetDetails["dateOfConstruction"]}
                    onChange={handleInputChange}
                    style={{ width: "80%" }}
                    max={new Date().toISOString().split("T")[0]}
                    rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                    }}
                  />
                </div>

                <div>
                  <div>{`${t("AST_COST_CONSTRUCTION")}`}</div>
                  <div style={assetStyles.rupeeIconPostions}>
                    <span style={assetStyles.rupeeIcon}>
                      ₹
                    </span>
                    <TextInput
                      t={t}
                      type={"text"}
                      isMandatory={false}
                      optionKey="i18nKey"
                      onKeyPress={additionalNumberValidation}
                      name="costOfCostruction"
                      value={assetDetails["costOfCostruction"]}
                      onChange={handleInputChange}
                      {...(validation = {
                        isRequired: false,
                        pattern: "^[a-zA-Z0-9/-]*$",
                        type: "text",
                        title: t("PT_NAME_ERROR_MESSAGE"),
                      })}
                      style={{ width: "80%", paddingLeft: "35px" }}
                      placeholder={t("ENT_NUMERIC_VALUE_ONLY")}
                    />
                  </div>
                </div>
              </React.Fragment>
            )}
            <div>
              <div>
                {t("AST_SOURCE_FINANCE")}
                <div className="tooltip" style={assetStyles.toolTip}>
                  <InfoBannerIcon />
                  <span
                    className="tooltiptext"
                    style={{
                      whiteSpace: "pre-wrap",
                      fontSize: "small",
                      wordWrap: "break-word",
                      width: "300px",
                      marginLeft: "15px",
                      marginBottom: "-10px",
                    }}
                  >
                    {`${t(`AST_SOURCE_OF_FUNDING`)}`}
                  </span>
                </div>
              </div>
              <Controller
                control={control}
                name={"sourceOfFinance"}
                defaultValue={sourceOfFinance}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <Dropdown
                    selected={sourceOfFinance}
                    select={setsourceOfFinance}
                    option={sourcefinance}
                    optionKey="i18nKey"
                    placeholder={"Select"}
                    style={{ width: "80%" }}
                    t={t}
                  />
                )}
              />
            </div>
            <div>
              <div>
                {`${t("AST_PURCHASE_COST")}`}
                <div className="tooltip" style={assetStyles.toolTip}>
                  <InfoBannerIcon />
                  <span className="tooltiptext" style={assetStyles.toolTipText}>
                    {`${t("ASSET_PURCHASE_COST")} `}
                  </span>
                </div>
              </div>
              <div style={assetStyles.rupeeIconPostions}>
                <span style={assetStyles.rupeeIcon}>
                  ₹
                </span>
                <TextInput
                  t={t}
                  // type={"number"}
                  onKeyPress={additionalNumberValidation}
                  isMandatory={false}
                  optionKey="i18nKey"
                  name="purchaseCost"
                  value={assetDetails["purchaseCost"]}
                  onChange={handleInputChange}
                  ValidationRequired={true}
                  validation={{
                    isRequired: false,
                    // pattern: /^[0-9]*$/,
                    type: "number",
                    title: t("PT_NAME_ERROR_MESSAGE"),

                  }}
                  style={{ width: "80%", paddingLeft: "35px" }}
                  disabled={isCostFieldsDisable}
                  placeholder={t("ENT_NUMERIC_VALUE_ONLY")}
                />
              </div>
            </div>

            <div>
              <div>
                {`${t("AST_ACQUISITION_COST")}`}

                <div className="tooltip" style={assetStyles.toolTip}>
                  <InfoBannerIcon />
                  <span className="tooltiptext" style={assetStyles.toolTipText}>
                    {`${t("ASSET_ACQUISITION_COST")} `}
                  </span>
                </div>
              </div>
              <div style={assetStyles.rupeeIconPostions}>
                <span style={assetStyles.rupeeIcon}>
                  ₹
                </span>
                <TextInput
                  t={t}
                  type={"text"}
                  isMandatory={false}
                  onKeyPress={additionalNumberValidation}
                  optionKey="i18nKey"
                  name="acquisitionCost"
                  value={assetDetails["acquisitionCost"]}
                  onChange={handleInputChange}
                  ValidationRequired={true}
                  {...(validation = {
                    isRequired: false,
                    pattern: /^[0-9]*$/,
                    type: "number",
                    title: t("PT_NAME_ERROR_MESSAGE"),
                  })}
                  style={{ width: "80%", paddingLeft: "35px" }}
                  placeholder={t("ENT_NUMERIC_VALUE_ONLY")}
                  disabled={isCostFieldsDisable}
                />
              </div>
            </div>

            <div>
              <div>
                {`${t("AST_BOOK_VALUE")}`}
                <div className="tooltip" style={assetStyles.toolTip}>
                  <InfoBannerIcon />
                  <span className="tooltiptext" style={assetStyles.toolTipText}>
                    {`${t("ASSET_BOOK_VALUE")} `}
                  </span>
                </div>
              </div>
              <div style={assetStyles.rupeeIconPostions}>
                <span style={assetStyles.rupeeIcon}>
                  ₹
                </span>
                <TextInput
                  t={t}
                  type={"number"}
                  isMandatory={false}
                  optionKey="i18nKey"
                  onKeyPress={additionalNumberValidation}
                  name="bookValue"
                  value={assetDetails["bookValue"]}
                  onChange={handleInputChange}
                  {...(validation = {
                    isRequired: false,
                    // pattern: /^[0-9]*$/,
                    type: "number",
                    title: t("PT_NAME_ERROR_MESSAGE"),
                  })}
                  style={{ width: "80%", paddingLeft: "35px" }}
                  disabled={isCostFieldsDisable}
                  placeholder={t("ENT_NUMERIC_VALUE_ONLY")}
                />
              </div>
            </div>

            <div>
              <div>
                {`${t("AST_MARKET_RATE_EVALUATION")}`}
                <div className="tooltip" style={assetStyles.toolTip}>
                  <InfoBannerIcon />
                  <span className="tooltiptext" style={assetStyles.toolTipText}>
                    {`${t("ASSET_MARKET_VALUE")} `}
                  </span>
                </div>
              </div>
              <div style={assetStyles.rupeeIconPostions}>
                <span style={assetStyles.rupeeIcon}>
                  ₹
                </span>
                <TextInput
                  t={t}
                  type={"number"}
                  isMandatory={false}
                  onKeyPress={additionalNumberValidation}
                  optionKey="i18nKey"
                  name="marketRateEvaluation"
                  value={assetDetails["marketRateEvaluation"]}
                  onChange={handleInputChange}
                  ValidationRequired={true}
                  {...(validation = {
                    isRequired: false,
                    // pattern: regexPattern("number"),
                    type: "number",
                    title: t("PT_NAME_ERROR_MESSAGE"),
                  })}
                  style={{ width: "80%", paddingLeft: "35px" }}
                  placeholder={t("ENT_NUMERIC_VALUE_ONLY")}
                />
              </div>
            </div>

            <div>
              <div>
                {`${t("AST_MARKET_RATE_CIRCLE")}`}
                <div className="tooltip" style={assetStyles.toolTip}>
                  <InfoBannerIcon />
                  <span className="tooltiptext" style={assetStyles.toolTipText}>
                    {`${t("ASSET_MARKET_VALUE")} `}
                  </span>
                </div>
              </div>
              <div style={assetStyles.rupeeIconPostions}>
                <span style={assetStyles.rupeeIcon}>
                  ₹
                </span>
                <TextInput
                  t={t}
                  type={"number"}
                  isMandatory={false}
                  onKeyPress={additionalNumberValidation}
                  optionKey="i18nKey"
                  name="marketRateCircle"
                  value={assetDetails["marketRateCircle"]}
                  onChange={handleInputChange}
                  ValidationRequired={true}
                  {...(validation = {
                    isRequired: false,
                    // pattern: regexPattern("number"),
                    type: "number",
                    title: t("PT_NAME_ERROR_MESSAGE"),
                  })}
                  style={{ width: "80%", paddingLeft: "35px" }}
                  placeholder={t("ENT_NUMERIC_VALUE_ONLY")}
                />
              </div>
            </div>

            {!["LAND", "BUILDING"].includes(assettype?.code) && (
              <React.Fragment>
                <div>
                  <div>
                    {`${t("AST_INVOICE_DATE")}`}
                    <div className="tooltip" style={assetStyles.toolTip}>
                      <InfoBannerIcon />
                      <span className="tooltiptext" style={assetStyles.toolTipText}>
                        {`${t("ASSET_INVOICE_ISSUE_DATE")} `}
                      </span>
                    </div>
                  </div>
                  <TextInput
                    t={t}
                    type={"date"}
                    isMandatory={false}
                    optionKey="i18nKey"
                    name={"invoiceDate"}
                    value={assetDetails["invoiceDate"]}
                    onChange={handleInputChange}
                    style={{ width: "80%" }}
                    rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG"))
                    }}
                  />
                </div>

                <div>
                  <div>
                    {`${t("AST_INVOICE_NUMBER")}`}
                    <div className="tooltip" style={assetStyles.toolTip}>
                      <InfoBannerIcon />
                      <span className="tooltiptext" style={assetStyles.toolTipText}>
                        {`${t("ASSET_INVOICE_ISSUE_DATE")} `}
                      </span>
                    </div>
                  </div>
                  <TextInput
                    t={t}
                    type={"text"}
                    isMandatory={false}
                    optionKey="i18nKey"
                    onKeyPress={specialCharacterValidation}
                    name="invoiceNumber"
                    value={assetDetails["invoiceNumber"]}
                    onChange={handleInputChange}
                    {...(validation = {
                      isRequired: false,
                      pattern: "^[a-zA-Z0-9/-]*$",
                      type: "text",
                      title: t("PT_NAME_ERROR_MESSAGE"),
                    })}
                    style={{ width: "80%" }}
                  />
                </div>
              </React.Fragment>
            )}

            {assettype?.code &&
              formJson
                .filter((e) => e.group === "acquistionDetails")
                .map((row, index) => {
                  if (row.conditionalField) {
                    const { dependsOn, showWhen } = row.conditionalField;
                    if (assetDetails[dependsOn]?.code !== showWhen) {
                      return null;
                    }
                  }

                  return (
                    <div key={index}>
                      <div>
                        {`${t(row.code)}`} {row.isMandatory ? <span style={{ color: "red" }}>*</span> : null}
                        <div className="tooltip" style={assetStyles.toolTip}>
                          <InfoBannerIcon />
                          <span className="tooltiptext" style={assetStyles.toolTipText}>
                            {`${t(row.code + "_INFO")} `}
                          </span>
                        </div>
                      </div>

                      {row.type === "date" ? (
                        <TextInput
                          t={t}
                          type={"date"}
                          isMandatory={false}
                          optionKey="i18nKey"
                          name={row.name}
                          value={assetDetails[row.name]}
                          onChange={handleInputChange}
                          style={{ width: "80%" }}
                          // max={new Date().toISOString().split("T")[0]}
                          rules={{
                            required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                            validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                          }}
                        />
                      ) : row.type === "dropdown" ? (
                        <Controller
                          control={control}
                          name={row.name}
                          isMandatory={row.isMandatory}
                          defaultValue={assetDetails[row.name] ? assetDetails[row.name] : ""}
                          rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                          render={(props) => (
                            <Dropdown
                              // className="form-field"
                              selected={assetDetails[row.name]}
                              select={(value) => handleInputChange({ name: row.name, ...value })}
                              option={row.options}
                              optionKey="i18nKey"
                              placeholder={"Select"}
                              isMandatory={row.isMandatory}
                              disable={row.disable}
                              style={{ width: "80%" }}
                              t={t}
                            />
                          )}
                        />
                      ) : row.type === "num" ? (
                        <TextInput
                          t={t}
                          type={"number"}
                          isMandatory={row.isMandatory}
                          onKeyPress={additionalNumberValidation}
                          optionKey="i18nKey"
                          name={row.name}
                          value={assetDetails[row.name] || ""}
                          onChange={handleInputChange}
                          {...(validation = {
                            isRequired: row.isMandatory,
                            pattern: regexPattern(row.columnType),
                            type: row.columnType,
                            title: t("PT_NAME_ERROR_MESSAGE"),
                          })}
                          style={{ width: "80%" }}
                          readOnly={row.isReadOnly}
                          disabled={row.disable}
                        />
                      ) : row.addCurrentLocationButton === true ? (
                        <div style={{ position: "relative" }}>
                          <TextInput
                            t={t}
                            type={row.type}
                            isMandatory={row.isMandatory}
                            optionKey="i18nKey"
                            onKeyPress={specialCharacterValidation}
                            name={row.name}
                            value={assetDetails[row.name] || ""}
                            onChange={handleInputChange}
                            style={{ flex: 1 }}
                            ValidationRequired={false}
                            {...(validation = {
                              isRequired: true,
                              pattern: regexPattern(row.columnType),
                              type: row.columnType,
                              title: t("VALID_LAT_LONG"),
                            })}
                          />
                          <div
                            className="butt-icon"
                            onClick={() => fetchCurrentLocation(row.name)}
                            style={{
                              position: "absolute",
                              right: "0",
                              top: "50%",
                              transform: "translateY(-50%)",
                              cursor: "pointer",
                              display: "flex",
                              alignItems: "center",
                              padding: "2px 5px",
                            }}
                          >
                            <LocationIcon styles={{ width: "16px", border: "none" }} className="fill-path-primary-main" />
                          </div>
                        </div>
                      ) : (
                        <TextInput
                          t={t}
                          type={row.type}
                          isMandatory={row.isMandatory}
                          optionKey="i18nKey"
                          onKeyPress={specialCharacterValidation}
                          name={row.name}
                          value={assetDetails[row.name] || ""}
                          onChange={handleInputChange}
                          {...(validation = {
                            isRequired: row.isMandatory,
                            pattern: regexPattern(row.columnType),
                            type: row.columnType,
                            title: t("PT_NAME_ERROR_MESSAGE"),
                          })}
                          style={{ width: "80%" }}
                          readOnly={row.isReadOnly}
                        />
                      )}
                    </div>
                  );
                })}
          </div>
        </React.Fragment>

        <React.Fragment>
          <CardHeader>{t("AST_PHYSICAL_CHARACTERISTICS_DETAILS")}</CardHeader>
          <div style={assetStyles.formGridStyles}>
            {assettype?.code &&
              formJson
                .filter((e) => e.group === "physicialCharacteristics")
                .map((row, index) => {
                  if (row.conditionalField) {
                    const { dependsOn, showWhen } = row.conditionalField;
                    if (assetDetails[dependsOn]?.code !== showWhen) {
                      return null;
                    }
                  }

                  return (
                    <div key={index}>
                      <div>
                        {`${t(row.code)}`} {row.isMandatory ? <span style={{ color: "red" }}>*</span> : null}
                        <div className="tooltip" style={assetStyles.toolTip}>
                          <InfoBannerIcon />
                          <span className="tooltiptext" style={assetStyles.toolTipText}>
                            {`${t(row.code + "_INFO")} `}
                          </span>
                        </div>
                      </div>

                      {row.type === "date" ? (
                        <TextInput
                          t={t}
                          type={"date"}
                          isMandatory={false}
                          optionKey="i18nKey"
                          name={row.name}
                          value={assetDetails[row.name]}
                          onChange={handleInputChange}
                          style={{ width: "80%" }}
                          // max={new Date().toISOString().split("T")[0]}
                          rules={{
                            required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                            validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                          }}
                        />
                      ) : row.type === "dropdown" ? (
                        <Controller
                          control={control}
                          name={row.name}
                          isMandatory={row.isMandatory}
                          defaultValue={assetDetails[row.name] ? assetDetails[row.name] : ""}
                          rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                          render={(props) => (
                            <Dropdown
                              // className="form-field"
                              selected={assetDetails[row.name]}
                              select={(value) => handleInputChange({ name: row.name, ...value })}
                              option={row.options}
                              optionKey="i18nKey"
                              placeholder={"Select"}
                              isMandatory={row.isMandatory}
                              style={{ width: "80%" }}
                              t={t}
                            />
                          )}
                        />
                      ) : row.type === "num" ? (
                        <div style={assetStyles.rupeeIconPostions}>
                          {row.name === "improvementCost" && (
                            <span style={assetStyles.rupeeIcon}>
                              ₹
                            </span>
                          )}
                          <TextInput
                            t={t}
                            type={"number"}
                            isMandatory={row.isMandatory}
                            onKeyPress={additionalNumberValidation}
                            optionKey="i18nKey"
                            name={row.name}
                            value={assetDetails[row.name] || ""}
                            onChange={handleInputChange}
                            {...(validation = {
                              isRequired: row.isMandatory,
                              pattern: regexPattern(row.columnType),
                              type: row.columnType,
                              title: t("PT_NAME_ERROR_MESSAGE"),
                            })}
                            style={{
                              width: "80%",
                              ...(row.name === "improvementCost" && { paddingLeft: "35px" })
                            }}
                            readOnly={row.isReadOnly}
                            disabled={row.disable}
                          />
                        </div>
                      ) : row.type === "textArea" ? (
                        <TextArea
                          t={t}
                          type={"textarea"}
                          isMandatory={row.isMandatory}
                          optionKey="i18nKey"
                          onKeyPress={specialCharacterValidation}
                          name={row.name}
                          value={assetDetails[row.name] || ""}
                          onChange={handleInputChange}
                          placeholder={row.placeHolder}
                          style={{ width: "80%" }}
                          ValidationRequired={true}
                          {...(validation = {
                            type: "textarea",
                            isRequired: row.isMandatory,
                            pattern: "^[a-zA-Z0-9 ,\\-]+$",
                            title: t("PT_NAME_ERROR_MESSAGE"),
                          })}
                        />
                      ) :
                        (
                          <TextInput
                            t={t}
                            type={row.type}
                            isMandatory={row.isMandatory}
                            optionKey="i18nKey"
                            onKeyPress={specialCharacterValidation}
                            name={row.name}
                            value={assetDetails[row.name] || ""}
                            onChange={handleInputChange}
                            {...(validation = {
                              isRequired: row.isMandatory,
                              pattern: regexPattern(row.columnType),
                              type: row.columnType,
                              title: t("PT_NAME_ERROR_MESSAGE"),
                            })}
                            style={{ width: "80%" }}
                            readOnly={row.isReadOnly}
                          />
                        )}
                    </div>
                  );
                })}
          </div>
        </React.Fragment>

        {/* NEW DOCUMENT UPLOAD SECTION */}
        <React.Fragment>
          <CardHeader>{t("AST_DOCUMENT_DETAILS")}</CardHeader>
          <div style={assetStyles.formGridStyles}>
            {documentData?.ASSET?.Documents?.map((document, index) => (
              <DocumentUploadField
                key={index}
                document={document}
                t={t}
                error={documentError}
                setError={setDocumentError}
                setDocuments={setDocuments}
                documents={documents}
                setCheckRequiredFields={setCheckRequiredFields}
                formData={formData}
              />
            ))}
            {documentError && <Toast label={documentError} onClose={() => setDocumentError(null)} error />}
          </div>
        </React.Fragment>
      </FormStep>
      {showToast && <Toast error={showToast.error} warning={showToast.warning} label={t(showToast.label)} onClose={() => setShowToast(null)} />}
    </React.Fragment>
  );
};

// NEW COMPONENT: Extracted from NewDocument
function DocumentUploadField({ t, document: doc, setDocuments, setError, documents, formData }) {
  const filteredDocument = documents?.find((item) => item?.documentType?.includes(doc?.code));

  const [selectedDocument, setSelectedDocument] = useState(
    filteredDocument
      ? { ...filteredDocument, active: doc?.active === true, code: filteredDocument?.documentType }
      : doc?.dropdownData?.length === 1
        ? doc?.dropdownData[0]
        : {}
  );

  const [file, setFile] = useState(null);
  const [uploadedFile, setUploadedFile] = useState(filteredDocument?.fileStoreId || null);
  const [latitude, setLatitude] = useState(formData?.latitude || null);
  const [longitude, setLongitude] = useState(formData?.longitude || null);
  const [isUploading, setIsUploading] = useState(false);

  const LoadingSpinner = () => (
    <div
      className="loading-spinner"
      style={{
        border: "2px solid #f3f3f3",
        borderTop: "2px solid #a82227",
        borderRadius: "50%",
        width: "16px",
        height: "16px",
        animation: "spin 1s linear infinite",
        display: "inline-block",
      }}
    />
  );

  const extractGeoLocation = async (file) => {
  try {
    const gpsData = await exifr.gps(file);

    if (gpsData?.latitude && gpsData?.longitude) {
      return {
        latitude: gpsData.latitude,
        longitude: gpsData.longitude,
      };
    }

    return { latitude: null, longitude: null };
  } catch (error) {
    console.warn("EXIF extraction failed:", error);
    return { latitude: null, longitude: null };
  }
};

  const handleFileUpload = async (e) => {
  const file = e.target.files[0];
  setFile(file);

  const { latitude, longitude } = await extractGeoLocation(file);

  setLatitude(latitude);
  setLongitude(longitude);

  if (doc?.code === "OWNER.ASSETPHOTO" && (!latitude || !longitude)) {
    setError("Please upload a photo with location details");
  }
};

  useEffect(() => {
    (async () => {
      setError(null);
      if (file) {
        if (file.size >= 5242880) {
          setError(t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
        } else {
          try {
            setUploadedFile(null);
            setIsUploading(true);
            const response = await Digit.UploadServices.Filestorage("ASSETV2", file, Digit.ULBService.getStateId());
            if (response?.data?.files?.length > 0) {
              setUploadedFile(response?.data?.files[0]?.fileStoreId);
            } else {
              setError(t("CS_FILE_UPLOAD_ERROR"));
            }
          } catch (err) {
            setError(t("CS_FILE_UPLOAD_ERROR"));
          } finally {
            setIsUploading(false);
          }
        }
      }
    })();
  }, [file]);

  useEffect(() => {
    if (selectedDocument?.code) {
      setDocuments((prev) => {
        const filteredDocumentsByDocumentType = prev?.filter((item) => item?.documentType !== selectedDocument?.code);
        if (!uploadedFile) {
          return filteredDocumentsByDocumentType;
        }
        const filteredDocumentsByFileStoreId = filteredDocumentsByDocumentType?.filter((item) => item?.fileStoreId !== uploadedFile);
        return [
          ...filteredDocumentsByFileStoreId,
          {
            documentType: selectedDocument?.code,
            fileStoreId: uploadedFile,
            documentUid: uploadedFile,
            latitude,
            longitude,
          },
        ];
      });
    }
  }, [uploadedFile, selectedDocument, latitude, longitude, setDocuments]);

  return (
    <div style={{ marginBottom: "24px", width: "80%" }}>
      {doc?.hasDropdown && (
        <LabelFieldPair>
          <CardLabel className="card-label-smaller">{t(doc.code.replaceAll(".", "_"))}</CardLabel>
        </LabelFieldPair>
      )}
      <LabelFieldPair>
        {/* <div className="field"> */}
        <UploadFile
          onUpload={handleFileUpload}
          onDelete={() => {
            setUploadedFile(null);
            setLatitude(null);
            setLongitude(null);
          }}
          message={
            isUploading ? (
              <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                <LoadingSpinner />
                <span>Uploading...</span>
              </div>
            ) : uploadedFile ? (
              "1 File Uploaded"
            ) : (
              "No File Uploaded"
            )
          }
          textStyles={{ width: "80%" }}
          inputStyles={{ width: "280px" }}
          accept=".pdf, .jpeg, .jpg, .png"
          buttonType="button"
          error={!uploadedFile}
        />
        {/* </div> */}
      </LabelFieldPair>
      {doc?.code === "OWNER.ASSETPHOTO" && latitude && longitude && (
        <div style={{ marginTop: "10px", textAlign: "center" }}>
          <p>
            <strong>{t("Location Details")}:</strong>
          </p>
          <p>
            {t("Latitude")}: {latitude}
          </p>
          <p>
            {t("Longitude")}: {longitude}
          </p>
        </div>
      )}
    </div>
  );
}

export default AssetAllDetails;
