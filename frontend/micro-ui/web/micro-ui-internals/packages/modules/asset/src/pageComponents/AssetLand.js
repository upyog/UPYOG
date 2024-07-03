import {
    CardLabel,
    CardLabelError,
    Dropdown,
    LabelFieldPair,
    LinkButton,
    //MobileNumber,
    TextInput,
    Toast,
  } from "@upyog/digit-ui-react-components";
  import _ from "lodash";
  import React, { useEffect, useMemo, useState } from "react";
  import { Controller, useForm } from "react-hook-form";
  import { useTranslation } from "react-i18next";
  import { useLocation } from "react-router-dom";
  
  const createAssetland = () => ({

    OSRLand: "",
    isitfenced: "",
    AnyBuiltup: "",
    TypeofTrees: "",
    LandType: "",
    Area: "",
    BookValue: "",
    DateofDeedExecution: "",
    DateofPossesion: "",
    FromWhomDeedTaken: "",
    GovernmentorderNumber: "",
    CollectororderNumber: "",
    CouncilResolutionNumber: "",
    AwardNumber: "", 
    OandMCOI: "",
    OandMTaskDetail: "",
    Totalcost: "",
    DepriciationRate: "",
    Costafterdepriciation: "",
    Currentassetvalue: "",
    Revenuegeneratedbyasset: "",
    howassetbeingused: "",

  
    key: Date.now(),
  });
  
  const AssetLand = ({ config, onSelect, userType, formData, setError, formState, clearErrors }) => {
    const { t } = useTranslation();
  
    const { pathname } = useLocation();
    const [assetslanddetail, setAssets] = useState(formData?.assetslanddetail || [createAssetland()]);

    
    const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });
  
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const stateId = Digit.ULBService.getStateId();
  



    // useEffect(() => {
    //   onSelect(config?.key, assetslanddetail);
  
    // }, [assetslanddetail]);

    useEffect(() => {
        if (onSelect) {
          onSelect(config?.key, assetslanddetail);
        }
      }, [assetslanddetail, onSelect]);
  
  
  
  
    const commonProps = {
      focusIndex,
      allAssets: assetslanddetail,
      setFocusIndex,
      formData,
      formState,
      setAssets,
      t,
      setError,
      clearErrors,
      config,
    
    };
  
    return (
      <React.Fragment>
        {assetslanddetail.map((assetslanddetail, index) => (
          <OwnerForm key={assetslanddetail.key} index={index} assetslanddetail={assetslanddetail} {...commonProps} />
        ))}
      </React.Fragment>
    )
  };
  
  const OwnerForm = (_props) => {
    const {
      assetslanddetail,
      index,
      focusIndex,
      allAssets,      
      setAssets,
      t,
      formData,
      config,
      setError,
      clearErrors,
      formState,
      setFocusIndex
      

    
  
    } = _props;
  
    const [showToast, setShowToast] = useState(null);
    const {
      control, formState: localFormState, watch, setError: setLocalError, clearErrors: clearLocalErrors, setValue, trigger, } = useForm();
    const formValue = watch();
    const { errors } = localFormState;
    const tenantId = Digit.ULBService.getCurrentTenantId();
  
   
  
    const [part, setPart] = React.useState({});
  
    useEffect(() => {
    
  
      if (!_.isEqual(part, formValue)) {
        setPart({ ...formValue });
        setAssets((prev) => prev.map((o) => (o.key && o.key === assetslanddetail.key ? { ...o, ...formValue/*, ..._ownerType*/ } : { ...o })));
        trigger();
      }
    }, [formValue]);
  
    // useEffect(() => {
    //   if (Object.keys(errors).length && !_.isEqual(formState.errors[config.key]?.type || {}, errors))
    //     setError(config.key, { type: errors });
    // //   else if (!Object.keys(errors).length && formState.errors[config.key]) clearErrors(config.key);
    // }, [errors]);
  
    const errorStyle = { width: "70%", marginLeft: "30%", fontSize: "12px", marginTop: "-21px" };
  
    return (
      <React.Fragment>
        <div style={{ marginBottom: "16px" }}>
          <div style={{ border: "1px solid #E3E3E3", padding: "16px", marginTop: "8px" }}>
            {allAssets?.length > 2 ? (
              <div style={{ marginBottom: "16px", padding: "5px", cursor: "pointer", textAlign: "right" }}>
                X
              </div>
            ) : null}

            <br></br>
            <div style={{fontWeight: 'bold', fontSize: '24px'}}>{t("ASSET_POSSESSION_ACQUISTION_DETAILS")}<br></br></div>
            <br></br> 
            <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("ASSET_LAND_TYPE") + " *"}</CardLabel>
                <div className="field">
                <Controller
                    control={control}
                    name={"LandType"}
                    defaultValue={assetslanddetail?.LandType}
                    rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                    }}
                    render={(props) => (
                    <TextInput
                        value={props.value}
                        // disable={isEditScreen}
                        autoFocus={focusIndex.index === assetslanddetail?.key && focusIndex.type === "LandType"}
                        onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: assetslanddetail.key, type: "LandType" });
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
            {/*<CardLabelError style={errorStyle}>{localFormState.touched.LandType ? errors?.LandType?.message : ""}</CardLabelError>*/}

            <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("ASSET_AREA_SIZE") + " *"}</CardLabel>
                <div className="field">
                <Controller
                    control={control}
                    name={"Area"}
                    defaultValue={assetslanddetail?.Area}
                    rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                    }}
                    render={(props) => (
                    <TextInput
                        value={props.value}
                        // disable={isEditScreen}
                        autoFocus={focusIndex.index === assetslanddetail?.key && focusIndex.type === "Area"}
                        onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: assetslanddetail.key, type: "Area" });
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
            {/*<CardLabelError style={errorStyle}>{localFormState.touched.Area ? errors?.Area?.message : ""}</CardLabelError>*/}

            <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("ASSET_ACQUISTION_COST_BOOK_VALUE") + " *"}</CardLabel>
                <div className="field">
                <Controller
                    control={control}
                    name={"BookValue"}
                    defaultValue={assetslanddetail?.BookValue}
                    rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                    }}
                    render={(props) => (
                    <TextInput
                        value={props.value}
                        // disable={isEditScreen}
                        autoFocus={focusIndex.index === assetslanddetail?.key && focusIndex.type === "BookValue"}
                        onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: assetslanddetail.key, type: "BookValue" });
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
            {/*<CardLabelError style={errorStyle}>{localFormState.touched.BookValue ? errors?.BookValue?.message : ""}</CardLabelError>*/}

            <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("ASSET_DATE_DEED_EXECUTION") + " *"}</CardLabel>
                <div className="field">
                <Controller
                    control={control}
                    name={"DateofDeedExecution"}
                    defaultValue={assetslanddetail?.DateofDeedExecution}
                    rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                    }}
                    render={(props) => (
                    <TextInput
                        value={props.value}
                        // disable={isEditScreen}
                        autoFocus={focusIndex.index === assetslanddetail?.key && focusIndex.type === "DateofDeedExecution"}
                        onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: assetslanddetail.key, type: "DateofDeedExecution" });
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
            {/*<CardLabelError style={errorStyle}>{localFormState.touched.DateofDeedExecution ? errors?.DateofDeedExecution?.message : ""}</CardLabelError>*/}

            <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("ASSET_DATE_POSSESION") + " *"}</CardLabel>
                <div className="field">
                <Controller
                    control={control}
                    name={"DateofPossesion"}
                    defaultValue={assetslanddetail?.DateofPossesion}
                    rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                    }}
                    render={(props) => (
                    <TextInput
                        value={props.value}
                        // disable={isEditScreen}
                        autoFocus={focusIndex.index === assetslanddetail?.key && focusIndex.type === "DateofPossesion"}
                        onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: assetslanddetail.key, type: "DateofPossesion" });
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
            {/*<CardLabelError style={errorStyle}>{localFormState.touched.DateofPossesion ? errors?.DateofPossesion?.message : ""}</CardLabelError>*/}

            <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("ASSET_FROM_DEED_TAKEN") + " *"}</CardLabel>
                <div className="field">
                <Controller
                    control={control}
                    name={"FromWhomDeedTaken"}
                    defaultValue={assetslanddetail?.FromWhomDeedTaken}
                    rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                    }}
                    render={(props) => (
                    <TextInput
                        value={props.value}
                        // disable={isEditScreen}
                        autoFocus={focusIndex.index === assetslanddetail?.key && focusIndex.type === "FromWhomDeedTaken"}
                        onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: assetslanddetail.key, type: "FromWhomDeedTaken" });
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
            {/*<CardLabelError style={errorStyle}>{localFormState.touched.FromWhomDeedTaken ? errors?.FromWhomDeedTaken?.message : ""}</CardLabelError>*/}

            <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("ASSET_GOVT_ORDER_NO.") + " *"}</CardLabel>
                <div className="field">
                <Controller
                    control={control}
                    name={"GovernmentorderNumber"}
                    defaultValue={assetslanddetail?.GovernmentorderNumber}
                    rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                    }}
                    render={(props) => (
                    <TextInput
                        value={props.value}
                        // disable={isEditScreen}
                        autoFocus={focusIndex.index === assetslanddetail?.key && focusIndex.type === "GovernmentorderNumber"}
                        onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: assetslanddetail.key, type: "GovernmentorderNumber" });
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
            {/*<CardLabelError style={errorStyle}>{localFormState.touched.GovernmentorderNumber ? errors?.GovernmentorderNumber?.message : ""}</CardLabelError>*/}

            <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("ASSET_COLLECTOR_ORDER_NO") + " *"}</CardLabel>
                <div className="field">
                <Controller
                    control={control}
                    name={"CollectororderNumber"}
                    defaultValue={assetslanddetail?.CollectororderNumber}
                    rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                    }}
                    render={(props) => (
                    <TextInput
                        value={props.value}
                        // disable={isEditScreen}
                        autoFocus={focusIndex.index === assetslanddetail?.key && focusIndex.type === "CollectororderNumber"}
                        onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: assetslanddetail.key, type: "CollectororderNumber" });
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
            {/*<CardLabelError style={errorStyle}>{localFormState.touched.CollectororderNumber ? errors?.CollectororderNumber?.message : ""}</CardLabelError>*/}

            <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("ASSET_COUNCIL_RESOLUTION_NO") + " *"}</CardLabel>
                <div className="field">
                <Controller
                    control={control}
                    name={"CouncilResolutionNumber"}
                    defaultValue={assetslanddetail?.CouncilResolutionNumber}
                    rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                    }}
                    render={(props) => (
                    <TextInput
                        value={props.value}
                        // disable={isEditScreen}
                        autoFocus={focusIndex.index === assetslanddetail?.key && focusIndex.type === "CouncilResolutionNumber"}
                        onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: assetslanddetail.key, type: "CouncilResolutionNumber" });
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
            {/*<CardLabelError style={errorStyle}>{localFormState.touched.CouncilResolutionNumber ? errors?.CouncilResolutionNumber?.message : ""}</CardLabelError>*/}

            <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("ASSET_AWARD_NO") + " *"}</CardLabel>
                <div className="field">
                <Controller
                    control={control}
                    name={"AwardNumber"}
                    defaultValue={assetslanddetail?.AwardNumber}
                    rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                    }}
                    render={(props) => (
                    <TextInput
                        value={props.value}
                        // disable={isEditScreen}
                        autoFocus={focusIndex.index === assetslanddetail?.key && focusIndex.type === "AwardNumber"}
                        onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: assetslanddetail.key, type: "AwardNumber" });
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
            {/*<CardLabelError style={errorStyle}>{localFormState.touched.AwardNumber ? errors?.AwardNumber?.message : ""}</CardLabelError>*/}

            <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("ASSET_COI") + " *"}</CardLabel>
                <div className="field">
                <Controller
                    control={control}
                    name={"OandMCOI"}
                    defaultValue={assetslanddetail?.OandMCOI}
                    rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                    }}
                    render={(props) => (
                    <TextInput
                        value={props.value}
                        // disable={isEditScreen}
                        autoFocus={focusIndex.index === assetslanddetail?.key && focusIndex.type === "OandMCOI"}
                        onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: assetslanddetail.key, type: "OandMCOI" });
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
            {/*<CardLabelError style={errorStyle}>{localFormState.touched.OandMCOI ? errors?.OandMCOI?.message : ""}</CardLabelError>*/}

            <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("ASSET_TASK_DETAILS") + " *"}</CardLabel>
                <div className="field">
                <Controller
                    control={control}
                    name={"OandMTaskDetail"}
                    defaultValue={assetslanddetail?.OandMTaskDetail}
                    rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                    }}
                    render={(props) => (
                    <TextInput
                        value={props.value}
                        // disable={isEditScreen}
                        autoFocus={focusIndex.index === assetslanddetail?.key && focusIndex.type === "OandMTaskDetail"}
                        onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: assetslanddetail.key, type: "OandMTaskDetail" });
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
            {/*<CardLabelError style={errorStyle}>{localFormState.touched.OandMTaskDetail ? errors?.OandMTaskDetail?.message : ""}</CardLabelError>*/}

            <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("ASSET_TOTAL_COST") + " *"}</CardLabel>
                <div className="field">
                <Controller
                    control={control}
                    name={"Totalcost"}
                    defaultValue={assetslanddetail?.Totalcost}
                    rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                    }}
                    render={(props) => (
                    <TextInput
                        value={props.value}
                        // disable={isEditScreen}
                        autoFocus={focusIndex.index === assetslanddetail?.key && focusIndex.type === "Totalcost"}
                        onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: assetslanddetail.key, type: "Totalcost" });
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
            {/*<CardLabelError style={errorStyle}>{localFormState.touched.Totalcost ? errors?.Totalcost?.message : ""}</CardLabelError>*/}

            <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("ASSET_DEPRICIATION_RATE") + " *"}</CardLabel>
                <div className="field">
                <Controller
                    control={control}
                    name={"DepriciationRate"}
                    defaultValue={assetslanddetail?.DepriciationRate}
                    rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                    }}
                    render={(props) => (
                    <TextInput
                        value={props.value}
                        // disable={isEditScreen}
                        autoFocus={focusIndex.index === assetslanddetail?.key && focusIndex.type === "DepriciationRate"}
                        onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: assetslanddetail.key, type: "DepriciationRate" });
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
            {/*<CardLabelError style={errorStyle}>{localFormState.touched.DepriciationRate ? errors?.DepriciationRate?.message : ""}</CardLabelError>*/}

            <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("ASSET_COST_DEPRICIATION") + " *"}</CardLabel>
                <div className="field">
                <Controller
                    control={control}
                    name={"Costafterdepriciation"}
                    defaultValue={assetslanddetail?.Costafterdepriciation}
                    rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                    }}
                    render={(props) => (
                    <TextInput
                        value={props.value}
                        // disable={isEditScreen}
                        autoFocus={focusIndex.index === assetslanddetail?.key && focusIndex.type === "Costafterdepriciation"}
                        onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: assetslanddetail.key, type: "Costafterdepriciation" });
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
            {/*<CardLabelError style={errorStyle}>{localFormState.touched.Costafterdepriciation ? errors?.Costafterdepriciation?.message : ""}</CardLabelError>*/}

            <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("ASSET_CURRENT_VALUE") + " *"}</CardLabel>
                <div className="field">
                <Controller
                    control={control}
                    name={"Currentassetvalue"}
                    defaultValue={assetslanddetail?.Currentassetvalue}
                    rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                    }}
                    render={(props) => (
                    <TextInput
                        value={props.value}
                        // disable={isEditScreen}
                        autoFocus={focusIndex.index === assetslanddetail?.key && focusIndex.type === "Currentassetvalue"}
                        onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: assetslanddetail.key, type: "Currentassetvalue" });
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
            {/*<CardLabelError style={errorStyle}>{localFormState.touched.Currentassetvalue ? errors?.Currentassetvalue?.message : ""}</CardLabelError>*/}

            <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("ASSET_REVENUE_GENERATED") + " *"}</CardLabel>
                <div className="field">
                <Controller
                    control={control}
                    name={"Revenuegeneratedbyasset"}
                    defaultValue={assetslanddetail?.Revenuegeneratedbyasset}
                    rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                    }}
                    render={(props) => (
                    <TextInput
                        value={props.value}
                        // disable={isEditScreen}
                        autoFocus={focusIndex.index === assetslanddetail?.key && focusIndex.type === "Revenuegeneratedbyasset"}
                        onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: assetslanddetail.key, type: "Revenuegeneratedbyasset" });
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
            {/*<CardLabelError style={errorStyle}>{localFormState.touched.Revenuegeneratedbyasset ? errors?.Revenuegeneratedbyasset?.message : ""}</CardLabelError>*/}

            <br></br>
            <div style={{fontWeight: 'bold', fontSize: '24px'}}>{t("ASSET_ADDITIONAL_DETAILS")}<br></br></div>
            <br></br> 
            <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("ASSET_OSR_LAND") + " *"}</CardLabel>
                <div className="field">
                <Controller
                    control={control}
                    name={"OSRLand"}
                    defaultValue={assetslanddetail?.OSRLand}
                    rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                    }}
                    render={(props) => (
                    <TextInput
                        value={props.value}
                        // disable={isEditScreen}
                        autoFocus={focusIndex.index === assetslanddetail?.key && focusIndex.type === "OSRLand"}
                        onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: assetslanddetail.key, type: "OSRLand" });
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
            {/*<CardLabelError style={errorStyle}>{localFormState.touched.OSRLand ? errors?.OSRLand?.message : ""}</CardLabelError>*/}

            <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("ASSET_FENCED_PROTECTION") + " *"}</CardLabel>
                <div className="field">
                <Controller
                    control={control}
                    name={"isitfenced"}
                    defaultValue={assetslanddetail?.isitfenced}
                    rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                    }}
                    render={(props) => (
                    <TextInput
                        value={props.value}
                        // disable={isEditScreen}
                        autoFocus={focusIndex.index === assetslanddetail?.key && focusIndex.type === "isitfenced"}
                        onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: assetslanddetail.key, type: "isitfenced" });
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
            {/*<CardLabelError style={errorStyle}>{localFormState.touched.isitfenced ? errors?.isitfenced?.message : ""}</CardLabelError>*/}

            <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("ASSET_ANY_BUITUP") + " *"}</CardLabel>
                <div className="field">
                <Controller
                    control={control}
                    name={"AnyBuiltup"}
                    defaultValue={assetslanddetail?.AnyBuiltup}
                    rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                    }}
                    render={(props) => (
                    <TextInput
                        value={props.value}
                        // disable={isEditScreen}
                        autoFocus={focusIndex.index === assetslanddetail?.key && focusIndex.type === "AnyBuiltup"}
                        onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: assetslanddetail.key, type: "AnyBuiltup" });
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
            {/*<CardLabelError style={errorStyle}>{localFormState.touched.AnyBuiltup ? errors?.AnyBuiltup?.message : ""}</CardLabelError>*/}

            <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("ASSET_TREES_TYPE") + " *"}</CardLabel>
                <div className="field">
                <Controller
                    control={control}
                    name={"TypeofTrees"}
                    defaultValue={assetslanddetail?.TypeofTrees}
                    rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                    }}
                    render={(props) => (
                    <TextInput
                        value={props.value}
                        // disable={isEditScreen}
                        autoFocus={focusIndex.index === assetslanddetail?.key && focusIndex.type === "TypeofTrees"}
                        onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: assetslanddetail.key, type: "TypeofTrees" });
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
            {/*<CardLabelError style={errorStyle}>{localFormState.touched.TypeofTrees ? errors?.TypeofTrees?.message : ""}</CardLabelError>*/}

            
            <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("ASSET_BEING_USED") + " *"}</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"howassetbeingused"}
                        defaultValue={assetslanddetail?.howassetbeingused}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                        }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetslanddetail?.key && focusIndex.type === "howassetbeingused"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetslanddetail.key, type: "howassetbeingused" });
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
                {/*<CardLabelError style={errorStyle}>{localFormState.touched.howassetbeingused ? errors?.howassetbeingused?.message : ""}</CardLabelError>*/}





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
  
  export default AssetLand;