import { CardLabel, CardLabelError, CardCaption, LabelFieldPair, TextInput, Toast, Dropdown, TextArea } from "@upyog/digit-ui-react-components";
import _ from "lodash";
import React, { useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";

const editAssetDetails = () => ({
  financialYear: "",
  assetclassification: "",
  assettype: "",
  assetparentsubCategory: "",
  BookPagereference: "",
  AssetName: "",
  assetsubtype: "",
  Assetdescription: "",
  Department: "",
  sourceOfFinance: "",
  assetclassification: "",
  key: Date.now(),
});

const EditGeneralDetails = ({ config, onSelect, formData, setError, clearErrors }) => {
  const { t } = useTranslation();
  const stateId = Digit.ULBService.getStateId();
  const [editAssignDetails, seteditAssignDetails] = useState(formData?.editAssignDetails || [editAssetDetails()]);
  const { id: applicationNo } = useParams();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { data: applicationDetails } = Digit.Hooks.asset.useAssetApplicationDetail(t, tenantId, applicationNo);
  let comingDataFromAPI = applicationDetails?.applicationData?.applicationData;

  const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });
  useEffect(() => {
    onSelect(config?.key, editAssignDetails);
  }, [editAssignDetails]);

  const { data: Menu_Asset } = Digit.Hooks.asset.useAssetClassification(stateId, "ASSET", "assetClassification"); // hook for asset classification Type

  const { data: Asset_Type } = Digit.Hooks.asset.useAssetType(stateId, "ASSET", "assetParentCategory");

  const { data: Asset_Sub_Type } = Digit.Hooks.asset.useAssetSubType(stateId, "ASSET", "assetCategory"); // hooks for Asset Parent Category

  const { data: Asset_Parent_Sub_Type } = Digit.Hooks.asset.useAssetparentSubType(stateId, "ASSET", "assetSubCategory");

  const { data: sourceofFinanceMDMS } = Digit.Hooks.useEnabledMDMS(Digit.ULBService.getStateId(), "ASSET", [{ name: "SourceFinance" }], {
    select: (data) => {
      const formattedData = data?.["ASSET"]?.["SourceFinance"];
      const activeData = formattedData?.filter((item) => item.active === true);
      return activeData;
    },
  }); // Note : used direct custom MDMS to get the Data ,Do not copy and paste without understanding the Context

  let sourcefinance = [];

  sourceofFinanceMDMS &&
    sourceofFinanceMDMS.map((finance) => {
      sourcefinance.push({ i18nKey: `AST_${finance.code}`, code: `${finance.code}`, value: `${finance.name}` });
    });

  const { data: currentFinancialYear } = Digit.Hooks.useEnabledMDMS(Digit.ULBService.getStateId(), "ASSET", [{ name: "FinancialYear" }], {
    select: (data) => {
      const formattedData = data?.["ASSET"]?.["FinancialYear"];
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
  let departNamefromMDMS = [];

  departmentName &&
    departmentName.map((departmentname) => {
      departNamefromMDMS.push({
        i18nKey: `COMMON_MASTERS_DEPARTMENT_${departmentname.code}`,
        code: `${departmentname.code}`,
        value: `COMMON_MASTERS_DEPARTMENT_${departmentname.code}`,
      });
    });

  const { data: assetTypeData } = Digit.Hooks.useEnabledMDMS(Digit.ULBService.getStateId(), "ASSET", [{ name: "AssetType" }], {
    select: (data) => {
      const formattedData = data?.["ASSET"]?.["AssetType"];
      return formattedData;
    },
  });
  let assetType = [];

  assetTypeData &&
    assetTypeData.map((assT) => {
      assetType.push({ i18nKey: `${assT.code}`, code: `${assT.code}`, value: `${assT.name}` });
    });
  const { data: assetCurrentUsageData } = Digit.Hooks.useEnabledMDMS(Digit.ULBService.getStateId(), "ASSET", [{ name: "AssetUsage" }], {
    select: (data) => {
      const formattedData = data?.["ASSET"]?.["AssetUsage"];
      return formattedData;
    },
  });
  let assetCurrentUsage = [];

  assetCurrentUsageData &&
    assetCurrentUsageData.map((assT) => {
      assetCurrentUsage.push({ i18nKey: `${assT.code}`, code: `${assT.code}`, value: `${assT.name}` });
    });

  const { data: masterDropdown } = Digit.Hooks.useEnabledMDMS(Digit.ULBService.getStateId(), "ASSET", [{ name: "ModeOfPossessionOrAcquisition" }], {
    select: (data) => {
      const formattedData = data?.["ASSET"]?.["ModeOfPossessionOrAcquisition"];
      return formattedData;
    },
  });
  let modeOfAcquisition = [];

  masterDropdown &&
    masterDropdown.map((row) => {
      modeOfAcquisition.push({ i18nKey: `${row.code}`, code: `${row.code}`, name: `${row.name}` });
    });

  let menu_Asset = []; //variable name for assetCalssification
  let asset_type = []; //variable name for asset type
  let asset_sub_type = []; //variable name for asset sub  parent caregory
  let asset_parent_sub_category = [];

  Menu_Asset &&
    Menu_Asset.map((asset_mdms) => {
      menu_Asset.push({ i18nKey: `${asset_mdms.name}`, code: `${asset_mdms.code}`, value: `${asset_mdms.name}` });
    });

  Asset_Type &&
    Asset_Type.map((asset_type_mdms) => {
      if (asset_type_mdms.assetClassification == editAssignDetails?.[0]?.assetclassification?.code) {
        asset_type.push({
          i18nKey: `${asset_type_mdms.name}`,
          code: `${asset_type_mdms.code}`,
          value: `${asset_type_mdms.name}`,
        });
      }
    });

  Asset_Sub_Type &&
    Asset_Sub_Type.map((asset_sub_type_mdms) => {
      if (asset_sub_type_mdms.assetParentCategory == editAssignDetails?.[0]?.assettype?.code) {
        asset_sub_type.push({
          i18nKey: `${asset_sub_type_mdms.name}`,
          code: `${asset_sub_type_mdms.code}`,
          value: `${asset_sub_type_mdms.name}`,
        });
      }
    });

  Asset_Parent_Sub_Type &&
    Asset_Parent_Sub_Type.map((asset_parent_mdms) => {
      if (asset_parent_mdms.assetCategory == editAssignDetails?.[0]?.assetsubtype?.code) {
        asset_parent_sub_category.push({
          i18nKey: `${asset_parent_mdms.name}`,
          code: `${asset_parent_mdms.code}`,
          value: `${asset_parent_mdms.name}`,
        });
      }
    });

  const commonProps = {
    focusIndex,
    allAssets: editAssignDetails,
    setFocusIndex,
    formData,
    seteditAssignDetails,
    t,
    setError,
    clearErrors,
    config,
    menu_Asset,
    asset_type,
    asset_sub_type,
    asset_parent_sub_category,
    departNamefromMDMS,
    financal,
    sourcefinance,
    comingDataFromAPI,
    assetType,
    assetCurrentUsage,
    modeOfAcquisition,
  };

  return (
    <React.Fragment>
      {editAssignDetails.map((editAssignDetails, index) => (
        <OwnerForm key={editAssignDetails.key} index={index} editAssignDetails={editAssignDetails} {...commonProps} />
      ))}
    </React.Fragment>
  );
};

const convertTimestampToDate = (timestamp) => {
  const date = new Date(timestamp * 1000); // Convert seconds to milliseconds
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0"); // Months are zero-based
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};
const OwnerForm = (_props) => {
  const {
    editAssignDetails,
    focusIndex,
    seteditAssignDetails,
    t,
    config,
    setError,
    clearErrors,
    setFocusIndex,
    menu_Asset,
    asset_type,
    asset_sub_type,
    asset_parent_sub_category,
    departNamefromMDMS,
    financal,
    sourcefinance,
    assetType,
    comingDataFromAPI,
    assetCurrentUsage,
    modeOfAcquisition,
  } = _props;

  const [showToast, setShowToast] = useState(null);
  const { control, formState: localFormState, watch, trigger } = useForm();
  const formValue = watch();
  const { errors } = localFormState;
  const [part, setPart] = React.useState({});

  let isEdit = true;

  const convertToObject = (String) => (String ? { i18nKey: String, code: String, value: String } : null);

  useEffect(() => {
    if (!_.isEqual(part, formValue)) {
      setPart({ ...formValue });
      seteditAssignDetails((prev) => prev.map((o) => (o.key === editAssignDetails.key ? { ...o, ...formValue } : o)));
      trigger();
    }
  }, [formValue]);

  useEffect(() => {
    if (Object.keys(errors).length && !_.isEqual(localFormState.errors[config.key]?.type || {}, errors)) setError(config.key, { type: errors });
    else if (!Object.keys(errors).length && localFormState.errors[config.key]) clearErrors(config.key);
  }, [errors]);

  const errorStyle = { width: "70%", marginLeft: "30%", fontSize: "12px", marginTop: "-21px" };

  return (
    <React.Fragment>
      <div style={{ marginBottom: "16px" }}>
        <div style={{ border: "1px solid #E3E3E3", padding: "16px", marginTop: "8px" }}>
          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("AST_FINANCIAL_YEAR")}</CardLabel>
            <Controller
              control={control}
              name={"financialYear"}
              defaultValue={convertToObject(comingDataFromAPI?.financialYear)}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={props.value}
                  select={props.onChange}
                  onBlur={props.onBlur}
                  disable={false}
                  option={financal}
                  optionKey="i18nKey"
                  t={t}
                />
              )}
            />
          </LabelFieldPair>
          <CardLabelError style={errorStyle}>{localFormState.touched.financialYear ? errors?.financialYear?.message : ""}</CardLabelError>
          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("AST_SOURCE_FINANCE")}</CardLabel>
            <Controller
              control={control}
              name={"sourceOfFinance"}
              defaultValue={convertToObject(comingDataFromAPI?.sourceOfFinance)}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={props.value}
                  select={props.onChange}
                  onBlur={props.onBlur}
                  option={sourcefinance}
                  optionKey="i18nKey"
                  t={t}
                />
              )}
            />
          </LabelFieldPair>
          <CardLabelError style={errorStyle}>{localFormState.touched.sourceOfFinance ? errors?.sourceOfFinance?.message : ""}</CardLabelError>
          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("AST_CATEGORY")}</CardLabel>
            <Controller
              control={control}
              name={"assetclassification"}
              defaultValue={convertToObject(comingDataFromAPI?.assetClassification)}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={props.value}
                  select={props.onChange}
                  onBlur={props.onBlur}
                  disable={isEdit}
                  option={menu_Asset}
                  optionKey="i18nKey"
                  t={t}
                />
              )}
            />
          </LabelFieldPair>
          <CardLabelError style={errorStyle}>{localFormState.touched.assetclassification ? errors?.assetclassification?.message : ""}</CardLabelError>
          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("AST_PARENT_CATEGORY")}</CardLabel>
            <Controller
              control={control}
              name={"assettype"}
              defaultValue={convertToObject(comingDataFromAPI?.assetParentCategory)}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={props.value}
                  select={props.onChange}
                  onBlur={props.onBlur}
                  disable={isEdit}
                  option={asset_type}
                  optionKey="i18nKey"
                  t={t}
                />
              )}
            />
          </LabelFieldPair>
          <CardLabelError style={errorStyle}>{localFormState.touched.assettype ? errors?.assettype?.message : ""}</CardLabelError>

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("AST_SUB_CATEGORY")}</CardLabel>
            <Controller
              control={control}
              name={"assetsubtype"}
              defaultValue={convertToObject(comingDataFromAPI?.assetCategory)}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={props.value}
                  select={props.onChange}
                  onBlur={props.onBlur}
                  disable={false}
                  option={asset_sub_type}
                  optionKey="i18nKey"
                  t={t}
                />
              )}
            />
          </LabelFieldPair>
          <CardLabelError style={errorStyle}>{localFormState.touched.assetsubtype ? errors?.assetsubtype?.message : ""}</CardLabelError>

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("AST_CATEGORY_SUB_CATEGORY")}</CardLabel>
            <Controller
              control={control}
              name={"assetparentsubCategory"}
              defaultValue={convertToObject(comingDataFromAPI?.assetSubCategory)}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={props.value}
                  select={props.onChange}
                  onBlur={props.onBlur}
                  disable={false}
                  option={asset_parent_sub_category}
                  optionKey="i18nKey"
                  t={t}
                />
              )}
            />
          </LabelFieldPair>
          <CardLabelError style={errorStyle}>
            {localFormState.touched.assetparentsubCategory ? errors?.assetparentsubCategory?.message : ""}
          </CardLabelError>

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("AST_BOOK_REF_SERIAL_NUM")}</CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"BookPagereference"}
                defaultValue={comingDataFromAPI?.assetBookRefNo}
                rules={{
                  required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  validate: { pattern: (val) => (/^[a-zA-Z0-9/-\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                }}
                render={(props) => (
                  <TextInput
                    value={props.value}
                    disable={false}
                    autoFocus={focusIndex.index === editAssignDetails?.key && focusIndex.type === "BookPagereference"}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      setFocusIndex({ index: editAssignDetails.key, type: "BookPagereference" });
                    }}
                    onBlur={(e) => {
                      setFocusIndex({ index: -1 });
                      props.onBlur(e);
                    }}
                  />
                )}
              />
            </div>
          </LabelFieldPair>
          <CardLabelError style={errorStyle}>{localFormState.touched.BookPagereference ? errors?.BookPagereference?.message : ""}</CardLabelError>

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("AST_NAME")}</CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"AssetName"}
                defaultValue={comingDataFromAPI?.assetName}
                rules={{
                  required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  validate: {
                    pattern: (val) => /^[a-zA-Z\s\-/]+$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"),
                  },
                }}
                render={(props) => (
                  <TextInput
                    value={props.value}
                    disable={false}
                    autoFocus={focusIndex.index === editAssignDetails?.key && focusIndex.type === "AssetName"}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      setFocusIndex({ index: editAssignDetails.key, type: "AssetName" });
                    }}
                    onBlur={(e) => {
                      setFocusIndex({ index: -1 });
                      props.onBlur(e);
                    }}
                  />
                )}
              />
            </div>
          </LabelFieldPair>
          <CardLabelError style={errorStyle}>{localFormState.touched.AssetName ? errors?.AssetName?.message : ""}</CardLabelError>

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("ASSET_DESCRIPTION")}</CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"Assetdescription"}
                defaultValue={comingDataFromAPI?.description}
                rules={{
                  required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  validate: {
                    pattern: (val) => /^[a-zA-Z\s\-/]+$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"),
                  },
                }}
                render={(props) => (
                  <TextArea
                    value={props.value}
                    disable={false}
                    autoFocus={focusIndex.index === editAssignDetails?.key && focusIndex.type === "Assetdescription"}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      setFocusIndex({ index: editAssignDetails.key, type: "Assetdescription" });
                    }}
                    onBlur={(e) => {
                      setFocusIndex({ index: -1 });
                      props.onBlur(e);
                    }}
                  />
                )}
              />
            </div>
          </LabelFieldPair>
          <CardLabelError style={errorStyle}>{localFormState.touched.Assetdescription ? errors?.Assetdescription?.message : ""}</CardLabelError>

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("AST_DEPARTMENT")}</CardLabel>
            <Controller
              control={control}
              name={"Department"}
              defaultValue={convertToObject("COMMON_MASTERS_DEPARTMENT_" + comingDataFromAPI?.department)}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={props.value}
                  disable={false}
                  select={props.onChange}
                  onBlur={props.onBlur}
                  option={departNamefromMDMS}
                  optionKey="i18nKey"
                  t={t}
                />
              )}
            />
          </LabelFieldPair>
          <CardLabelError style={errorStyle}>{localFormState.touched.Department ? errors?.Department?.message : ""}</CardLabelError>
          <br />
          <CardCaption>Asset Common Details </CardCaption>
          <br />
          <br />
          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("AST_TYPE")}</CardLabel>
            <Controller
              control={control}
              name={"assetType"}
              defaultValue={convertToObject(comingDataFromAPI?.assetType)}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={props.value}
                  select={props.onChange}
                  onBlur={props.onBlur}
                  disable={false}
                  option={assetType}
                  optionKey="i18nKey"
                  t={t}
                />
              )}
            />
          </LabelFieldPair>
          <CardLabelError style={errorStyle}>{localFormState.touched.assetType ? errors?.assetType?.message : ""}</CardLabelError>

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("AST_USAGE")}</CardLabel>
            <Controller
              control={control}
              name={"assetUsage"}
              defaultValue={convertToObject(comingDataFromAPI?.assetUsage)}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={props.value}
                  select={props.onChange}
                  onBlur={props.onBlur}
                  disable={false}
                  option={assetCurrentUsage}
                  optionKey="i18nKey"
                  t={t}
                />
              )}
            />
          </LabelFieldPair>
          <CardLabelError style={errorStyle}>{localFormState.touched.assetUsage ? errors?.assetUsage?.message : ""}</CardLabelError>

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("AST_MODE_OF_POSSESSION_OR_ACQUISITION")}</CardLabel>
            <Controller
              control={control}
              name={"modeOfPossessionOrAcquisition"}
              defaultValue={convertToObject(comingDataFromAPI?.modeOfPossessionOrAcquisition)}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={props.value}
                  select={props.onChange}
                  disable={false}
                  onBlur={props.onBlur}
                  option={modeOfAcquisition}
                  optionKey="i18nKey"
                  t={t}
                />
              )}
            />
          </LabelFieldPair>
          <CardLabelError style={errorStyle}>
            {localFormState.touched.modeOfPossessionOrAcquisition ? errors?.modeOfPossessionOrAcquisition?.message : ""}
          </CardLabelError>

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("AST_INVOICE_DATE")}</CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"invoiceDate"}
                defaultValue={convertTimestampToDate(comingDataFromAPI?.invoiceDate)}
                rules={{
                  required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  validate: {
                    pattern: (val) => /^[a-zA-Z\s\-/]+$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"),
                  },
                }}
                render={(props) => (
                  <TextInput
                    value={props.value}
                    type={"date"}
                    disable={false}
                    autoFocus={focusIndex.index === editAssignDetails?.key && focusIndex.type === "invoiceDate"}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      setFocusIndex({ index: editAssignDetails.key, type: "invoiceDate" });
                    }}
                    onBlur={(e) => {
                      setFocusIndex({ index: -1 });
                      props.onBlur(e);
                    }}
                  />
                )}
              />
            </div>
          </LabelFieldPair>
          <CardLabelError style={errorStyle}>{localFormState.touched.Assetdescription ? errors?.Assetdescription?.message : ""}</CardLabelError>

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("AST_INVOICE_NUMBER")}</CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"invoiceNumber"}
                defaultValue={comingDataFromAPI?.invoiceNumber}
                rules={{
                  required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  validate: {
                    pattern: (val) => /^[a-zA-Z\s\-/]+$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"),
                  },
                }}
                render={(props) => (
                  <TextInput
                    value={props.value}
                    disable={false}
                    autoFocus={focusIndex.index === editAssignDetails?.key && focusIndex.type === "invoiceNumber"}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      setFocusIndex({ index: editAssignDetails.key, type: "invoiceNumber" });
                    }}
                    onBlur={(e) => {
                      setFocusIndex({ index: -1 });
                      props.onBlur(e);
                    }}
                  />
                )}
              />
            </div>
          </LabelFieldPair>
          {/* <CardLabelError style={errorStyle}>{localFormState.touched.invoiceNumber ? errors?.invoiceNumber?.message : ""}</CardLabelError> */}

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("AST_PURCHASE_DATE")}</CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"purchaseDate"}
                defaultValue={convertTimestampToDate(comingDataFromAPI?.purchaseDate)}
                rules={{
                  required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  validate: {
                    pattern: (val) => /^[a-zA-Z\s\-/]+$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"),
                  },
                }}
                render={(props) => (
                  <TextInput
                    value={props.value}
                    type={"date"}
                    disable={false}
                    autoFocus={focusIndex.index === editAssignDetails?.key && focusIndex.type === "purchaseDate"}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      setFocusIndex({ index: editAssignDetails.key, type: "purchaseDate" });
                    }}
                    onBlur={(e) => {
                      setFocusIndex({ index: -1 });
                      props.onBlur(e);
                    }}
                  />
                )}
              />
            </div>
          </LabelFieldPair>
          {/* <CardLabelError style={errorStyle}>{localFormState.touched.purchaseDate ? errors?.purchaseDate?.message : ""}</CardLabelError> */}

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("AST_PURCHASE_ORDER")}</CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"purchaseOrderNumber"}
                defaultValue={comingDataFromAPI?.purchaseOrderNumber}
                rules={{
                  required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  validate: {
                    pattern: (val) => /^[a-zA-Z\s\-/]+$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"),
                  },
                }}
                render={(props) => (
                  <TextInput
                    value={props.value}
                    disable={false}
                    autoFocus={focusIndex.index === editAssignDetails?.key && focusIndex.type === "purchaseOrderNumber"}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      setFocusIndex({ index: editAssignDetails.key, type: "purchaseOrderNumber" });
                    }}
                    onBlur={(e) => {
                      setFocusIndex({ index: -1 });
                      props.onBlur(e);
                    }}
                  />
                )}
              />
            </div>
          </LabelFieldPair>
          {/* <CardLabelError style={errorStyle}>{localFormState.touched.purchaseOrderNumber ? errors?.purchaseOrderNumber?.message : ""}</CardLabelError> */}

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("AST_LIFE")}</CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"lifeOfAsset"}
                defaultValue={comingDataFromAPI?.purchaseOrderNumber}
                rules={{
                  required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  validate: {
                    pattern: (val) => /^[a-zA-Z\s\-/]+$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"),
                  },
                }}
                render={(props) => (
                  <TextInput
                    value={props.value}
                    disable={false}
                    autoFocus={focusIndex.index === editAssignDetails?.key && focusIndex.type === "lifeOfAsset"}
                    onChange={(e) => {
                      if((e.target.value).length > 3) { alert('Maximum limit is 3 digits only!'); return false }
                      props.onChange(e.target.value);
                      setFocusIndex({ index: editAssignDetails.key, type: "lifeOfAsset" });
                    }}
                    onBlur={(e) => {
                      setFocusIndex({ index: -1 });
                      props.onBlur(e);
                    }}
                  />
                )}
              />
            </div>
          </LabelFieldPair>

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("AST_LOCATION_DETAILS")}</CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"location"}
                defaultValue={comingDataFromAPI?.location}
                rules={{
                  required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  validate: {
                    pattern: (val) => /^[a-zA-Z\s\-/]+$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"),
                  },
                }}
                render={(props) => (
                  <TextInput
                    value={props.value}
                    disable={false}
                    autoFocus={focusIndex.index === editAssignDetails?.key && focusIndex.type === "location"}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      setFocusIndex({ index: editAssignDetails.key, type: "location" });
                    }}
                    onBlur={(e) => {
                      setFocusIndex({ index: -1 });
                      props.onBlur(e);
                    }}
                  />
                )}
              />
            </div>
          </LabelFieldPair>
          {/* <CardLabelError style={errorStyle}>{localFormState.touched.location ? errors?.location?.message : ""}</CardLabelError> */}

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("AST_PURCHASE_COST")}</CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"purchaseCost"}
                defaultValue={comingDataFromAPI?.purchaseCost}
                rules={{
                  required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  validate: {
                    pattern: (val) => /^[a-zA-Z\s\-/]+$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"),
                  },
                }}
                render={(props) => (
                  <TextInput
                    value={props.value}
                    disable={false}
                    autoFocus={focusIndex.index === editAssignDetails?.key && focusIndex.type === "purchaseCost"}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      setFocusIndex({ index: editAssignDetails.key, type: "purchaseCost" });
                    }}
                    onBlur={(e) => {
                      setFocusIndex({ index: -1 });
                      props.onBlur(e);
                    }}
                  />
                )}
              />
            </div>
          </LabelFieldPair>
          {/* <CardLabelError style={errorStyle}>{localFormState.touched.purchaseCost ? errors?.purchaseCost?.message : ""}</CardLabelError> */}

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("AST_ACQUISITION_COST")}</CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"acquisitionCost"}
                defaultValue={comingDataFromAPI?.acquisitionCost}
                rules={{
                  required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  validate: {
                    pattern: (val) => /^[a-zA-Z\s\-/]+$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"),
                  },
                }}
                render={(props) => (
                  <TextInput
                    value={props.value}
                    disable={false}
                    autoFocus={focusIndex.index === editAssignDetails?.key && focusIndex.type === "acquisitionCost"}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      setFocusIndex({ index: editAssignDetails.key, type: "acquisitionCost" });
                    }}
                    onBlur={(e) => {
                      setFocusIndex({ index: -1 });
                      props.onBlur(e);
                    }}
                  />
                )}
              />
            </div>
          </LabelFieldPair>
          {/* <CardLabelError style={errorStyle}>{localFormState.touched.acquisitionCost ? errors?.acquisitionCost?.message : ""}</CardLabelError> */}

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("AST_BOOK_VALUE")}</CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"bookValue"}
                defaultValue={comingDataFromAPI?.bookValue}
                rules={{
                  required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  validate: {
                    pattern: (val) => /^[a-zA-Z\s\-/]+$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"),
                  },
                }}
                render={(props) => (
                  <TextInput
                    value={props.value}
                    disable={false}
                    autoFocus={focusIndex.index === editAssignDetails?.key && focusIndex.type === "bookValue"}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      setFocusIndex({ index: editAssignDetails.key, type: "bookValue" });
                    }}
                    onBlur={(e) => {
                      setFocusIndex({ index: -1 });
                      props.onBlur(e);
                    }}
                  />
                )}
              />
            </div>
          </LabelFieldPair>
          {/* <CardLabelError style={errorStyle}>{localFormState.touched.bookValue ? errors?.bookValue?.message : ""}</CardLabelError> */}
        </div>
      </div>
      {showToast?.label && (
        <Toast
          label={showToast?.label}
          onClose={(w) => {
            setShowToast((x) => null);
          }}
        />
      )}
    </React.Fragment>
  );
};

export default EditGeneralDetails;
