import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, InfoBannerIcon, Dropdown, TextArea } from "@upyog/digit-ui-react-components";
import { useLocation } from "react-router-dom";
import Timeline from "../components/ASTTimeLineUp";
import FormGridWrapper from "../utils/FormGridWrapper";
import { Controller, useForm } from "react-hook-form";

const AssetRegistry = ({ t, config, onSelect, userType, formData }) => {
  const [assetDetails, setAssetDetails] = useState(
    formData.assetDetails && formData.assetDetails.assetParentCategory === formData?.asset?.assettype?.code
      ? formData.assetDetails
      : { assetParentCategory: formData?.asset?.assettype?.code }
  );
  const [categoriesWiseData, setCategoriesWiseData] = useState();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateTenantId = Digit.ULBService.getStateId();

  const { control } = useForm();
  const { pathname: url } = useLocation();
  let index = 0;
  let validation = {};

  const goNext = () => {
    let owner = formData.assetDetails && formData.assetDetails[index];
    assetDetails.owner = owner;
    onSelect(config.key, assetDetails, false, index);
  };

  const { data: Menu_Asset } = Digit.Hooks.useEnabledMDMS(stateTenantId, "ASSET", [{ name: "assetClassification" }], {
    select: (data) => {
      const formattedData = data?.["ASSET"]?.["assetClassification"];
      const activeData = formattedData?.filter((item) => item.active === true);
      return activeData;
    },
  });
  const { data: Vendor_Details } = Digit.Hooks.useEnabledMDMS(stateTenantId, "ASSET", [{ name: "VendorDetails" }], {
    select: (data) => {
      const formattedData = data?.["ASSET"]?.["VendorDetails"];
      const activeData = formattedData?.filter((item) => item.active === true);
      return activeData;
    },
  });

  const { data: Asset_Type } = Digit.Hooks.useEnabledMDMS(stateTenantId, "ASSET", [{ name: "assetParentCategory" }], {
    select: (data) => {
      const formattedData = data?.["ASSET"]?.["assetParentCategory"];
      const activeData = formattedData?.filter((item) => item.active === true);
      return activeData;
    },
  });
  const { data: Asset_Sub_Type } = Digit.Hooks.asset.useAssetSubType(stateTenantId, "ASSET", "assetCategory");

  // This call with stateTenantId (Get state-level data)
  const stateResponseObject = Digit.Hooks.useEnabledMDMS(stateTenantId, "ASSET", [{ name: "AssetParentCategoryFields" }], {
    select: (data) => {
      const formattedData = data?.["ASSET"]?.["AssetParentCategoryFields"];
      return formattedData;
    },
  });

  let menu_Asset = []; //variable name for assetCalssification
  let asset_type = []; //variable name for asset type
  let asset_sub_type = [];
  let vendorDetails = [];

  Menu_Asset &&
    Menu_Asset.map((asset_mdms) => {
      menu_Asset.push({ i18nKey: `${asset_mdms.name}`, code: `${asset_mdms.code}`, value: `${asset_mdms.name}` });
    });
  Vendor_Details &&
    Vendor_Details.map((asset_mdms) => {
      vendorDetails.push({ i18nKey: `${asset_mdms.name}`, code: `${asset_mdms.code}`, value: `${asset_mdms.name}` });
    });

  Asset_Type &&
    Asset_Type.map((asset_type_mdms) => {
      if (asset_type_mdms.assetClassification == assetDetails?.assetclassification?.code) {
        asset_type.push({
          i18nKey: `${asset_type_mdms.name}`,
          code: `${asset_type_mdms.code}`,
          value: `${asset_type_mdms.name}`,
        });
      }
    });

  Asset_Sub_Type &&
    Asset_Sub_Type.map((asset_sub_type_mdms) => {
      if (asset_sub_type_mdms.assetParentCategory == assetDetails?.assettype?.code) {
        asset_sub_type.push({
          i18nKey: `${asset_sub_type_mdms.name}`,
          code: `${asset_sub_type_mdms.code}`,
          value: `${asset_sub_type_mdms.name}`,
        });
      }
    });

  const onSkip = () => onSelect();
  useEffect(() => {
    let combinedData;
    // if city level master is not available then fetch  from state-level
    if (stateResponseObject?.data) {
      combinedData = stateResponseObject.data;
    } else {
      combinedData = []; // Or an appropriate default value for empty data
      console.log("StateResponse Object data is unavailable.");
    }
    setCategoriesWiseData(combinedData);
  }, [stateResponseObject]);

  let formJson = [];
  if (Array.isArray(categoriesWiseData)) {
    // Filter categories based on the selected assetParentCategory
    formJson = categoriesWiseData
      .filter((category) => {
        const isMatch = category.assetParentCategory === assetDetails?.assettype?.code;
        // console.log(`Matching ${category.assetParentCategory} with ${formData?.asset?.assettype?.code}:`, isMatch);
        return isMatch;
      })
      .map((category) => category.fields) // Extract the fields array
      .flat() // Flatten the fields array
      .filter((field) => field.active === true); // Filter by active status
  }

  const handleInputChange = (e, name) => {
    let fieldName, value;

    // Case 1: TextInput (event object with target)
    if (e?.target) {
      fieldName = e.target.name;
      value = e.target.value;
    }
    // Case 2: Dropdown (value passed + explicit name)
    else {
      fieldName = name;
      value = e;
    }

    setAssetDetails((prevData) => {
      const updatedData = { ...prevData, [fieldName]: value };

      // Reset dependent dropdowns when parent changes
      if (fieldName === "assetclassification") {
        updatedData.assettype = null;
        updatedData.assetsubtype = null;
      } else if (fieldName === "assettype") {
        updatedData.assetsubtype = null;
      }

      return updatedData;
    });
  };

  //Dropdown get data form masters
  const dropDownData = (masterName) => {
    const trimmedName = masterName ? masterName.trim() : "";
    const { data: masterDropdown } = Digit.Hooks.useEnabledMDMS(stateTenantId, "ASSET", [{ name: trimmedName }], {
      select: (data) => {
        const formattedData = data?.["ASSET"]?.[trimmedName];
        return formattedData;
      },
    });
    let dropDown = [];

    masterDropdown &&
      masterDropdown.map((row) => {
        dropDown.push({ i18nKey: `${row.code}`, code: `${row.code}`, name: `${row.name}` });
      });
    return dropDown;
  };

  return (
    <React.Fragment>
      {window.location.href.includes("/employee") ? <Timeline currentStep={1} /> : null}
      <FormStep config={config} onSelect={goNext} onSkip={onSkip} t={t}>
        <FormGridWrapper>
          <div>
            <CardLabel>
              {t("AST_PARENT_CATEGORY_LV1")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <Controller
              control={control}
              name="assetclassification"
              defaultValue={assetDetails?.assetclassification}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={assetDetails?.assetclassification}
                  select={(e) => handleInputChange(e, "assetclassification")}
                  option={menu_Asset}
                  optionKey="i18nKey"
                  placeholder={"Select"}
                  t={t}
                />
              )}
            />
          </div>

          <div>
            <CardLabel>
              {t("AST_PARENT_CATEGORY_LV2")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <Controller
              control={control}
              name={"assettype"}
              defaultValue={assetDetails?.assettype}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={assetDetails?.assettype}
                  select={(e) => handleInputChange(e, "assettype")}
                  option={asset_type}
                  optionKey="i18nKey"
                  placeholder={"Select"}
                  t={t}
                />
              )}
            />
          </div>

          <div>
            <CardLabel> {t("AST_PARENT_CATEGORY_LV3")} </CardLabel>
            <Controller
              control={control}
              name={"assetsubtype"}
              defaultValue={assetDetails?.assetsubtype}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={assetDetails?.assetsubtype}
                  select={(e) => handleInputChange(e, "assetsubtype")}
                  option={asset_sub_type}
                  optionKey="i18nKey"
                  placeholder={"Select"}
                  t={t}
                />
              )}
            />
          </div>
          <div>
            <CardLabel>{t("AST_PARENT_CATEGORY_LV4")} </CardLabel>

            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="otherCategory"
              value={assetDetails.otherCategory || ""}
              onChange={handleInputChange}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z0-9/-]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
          </div>

          <div>
            <CardLabel>
              {t("AST_NAME")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <TextInput
              t={t}
              type="text"
              isMandatory={false}
              optionKey="i18nKey"
              name="assetName"
              value={assetDetails.assetName || ""}
              onChange={handleInputChange}
              ValidationRequired={false}
              validation={{
                isRequired: true,
                pattern: "^[a-zA-Z0-9/-]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              }}
            />
          </div>

          <div>
            <CardLabel>
              {t("ASSET_DESCRIPTION")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="assetDescription"
              value={assetDetails.assetDescription || ""}
              placeholder={t("Enter Asset Description")}
              ValidationRequired={false}
              onChange={handleInputChange}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z0-9/-]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
          </div>

          <div>
            <CardLabel>
              {t("AST_ACQUISITION_COST")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <TextInput
              t={t}
              type={"number"}
              isMandatory={false}
              optionKey="i18nKey"
              name="acquisitionCost"
              value={assetDetails.acquisitionCost || ""}
              ValidationRequired={false}
              onChange={handleInputChange}
              step="0.01"
              min="0"
              placeholder="Enter amount (e.g., 1000.50)"
              {...(validation = {
                isRequired: true,
                pattern: "^\\d+(\\.\\d{1,2})?$",
                type: "number",
                title: t("Please enter a valid amount"),
              })}
            />
          </div>

          <div>
            <CardLabel>
              {t("AST_ACQUISITION_DATE")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <TextInput
              t={t}
              type={"date"}
              isMandatory={false}
              optionKey="i18nKey"
              name="acquisitionDate"
              value={assetDetails.acquisitionDate || ""}
              ValidationRequired={false}
              onChange={handleInputChange}
              rules={{
                required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
              }}
            />
          </div>

          <div>
            <CardLabel>
              {t("AST_VENDOR")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <Controller
              control={control}
              name={"vendorDetail"}
              defaultValue={assetDetails?.vendorDetail}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={assetDetails?.vendorDetail}
                  select={(e) => handleInputChange(e, "vendorDetail")}
                  option={vendorDetails}
                  optionKey="i18nKey"
                  placeholder={"Select"}
                  t={t}
                />
              )}
            />
          </div>
          {formJson.map((row, index) => (
            <div key={index}>
              <CardLabel>
                {`${t(row.code + "_INFO")} `} <span style={{ color: "red" }}>*</span>
              </CardLabel>
              {row.type === "dropdown" ? (
                <Controller
                  control={control}
                  name={row.name}
                  defaultValue={assetDetails[row.name] || ""}
                  rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                  render={(props) => (
                    <Dropdown
                      className="form-field"
                      selected={assetDetails[row.name]}
                      select={(e) => handleInputChange(e, row.name)}
                      option={dropDownData(row.masterName)}
                      optionKey="i18nKey"
                      placeholder="Select"
                      t={t}
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
                  value={assetDetails[row.name] || ""}
                  onChange={handleInputChange}
                  validation={{
                    isRequired: row.isMandatory,
                    pattern: "^[a-zA-Z0-9/-]*$",
                    type: row.columnType,
                    title: t("PT_NAME_ERROR_MESSAGE"),
                  }}
                />
              )}
            </div>
          ))}
        </FormGridWrapper>
      </FormStep>
    </React.Fragment>
  );
};

export default AssetRegistry;
