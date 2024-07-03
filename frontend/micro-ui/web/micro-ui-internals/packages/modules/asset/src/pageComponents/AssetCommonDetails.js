  import {
    CardLabel,
    CardLabelError,
    Dropdown,
    LabelFieldPair,
    LinkButton,
    //MobileNumber,
    TextInput,
    Toast,
    } from "@nudmcdgnpm/digit-ui-react-components";
    import _ from "lodash";
    import React, { useEffect, useMemo, useState } from "react";
    import { Controller, useForm } from "react-hook-form";
    import { useTranslation } from "react-i18next";
    import { useLocation } from "react-router-dom";

    const createAssetcommonforAll = () => ({


    

        // serialNo: "",
        BookPagereference: "",
        // AssetId: "",
        AssetName: "",
        Assetdescription: "",
        Department : "",

        key: Date.now(),
    });

    const AssetCommonDetails = ({ config, onSelect, userType, formData, setError, formState, clearErrors }) => {
    const { t } = useTranslation();

    const { pathname } = useLocation();
    const [assetcommonforall, setAssetsCommonforAll] = useState(formData?.assetcommonforall || [createAssetcommonforAll()]);

    
    const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });

    const tenantId = Digit.ULBService.getCurrentTenantId();
    const stateId = Digit.ULBService.getStateId();




    useEffect(() => {
      onSelect(config?.key, assetcommonforall);

    }, [assetcommonforall]);

    // useEffect(() => {
    //     if (onSelect) {
    //         onSelect(config?.key, assetcommonforall);
    //     }
    //     }, [assetcommonforall, onSelect]);




    const commonProps = {
        focusIndex,
        allAssets: assetcommonforall,
        setFocusIndex,
        formData,
        formState,
        setAssetsCommonforAll,
        t,
        setError,
        clearErrors,
        config,
    
    };

    return (
        <React.Fragment>
        {assetcommonforall.map((assetcommonforall, index) => (
            <OwnerForm key={assetcommonforall.key} index={index} assetcommonforall={assetcommonforall} {...commonProps} />
        ))}
        </React.Fragment>
    )
    };

    const OwnerForm = (_props) => {
    const {
        assetcommonforall,
        index,
        focusIndex,
        allAssets,      
        setAssetsCommonforAll,
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
        setAssetsCommonforAll((prev) => prev.map((o) => (o.key && o.key === assetcommonforall.key ? { ...o, ...formValue/*, ..._ownerType*/ } : { ...o })));
        trigger();
        }
    }, [formValue]);

    useEffect(() => {
      if (Object.keys(errors).length && !_.isEqual(formState.errors[config.key]?.type || {}, errors))
        setError(config.key, { type: errors });
      else if (!Object.keys(errors).length && formState.errors[config.key]) clearErrors(config.key);
    }, [errors]);

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

    
                {/* <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_SERIAL_NO") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"serialNo"}
                        defaultValue={assetcommonforall?.serialNo}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                        }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetcommonforall?.key && focusIndex.type === "serialNo"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetcommonforall.key, type: "serialNo" });
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
                <CardLabelError style={errorStyle}>{localFormState.touched.serialNo ? errors?.serialNo?.message : ""}</CardLabelError> */}

                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_BOOK_REF_SERIAL_NUM") + " *"}</CardLabel>
                    <div className="field">
                    <Controller
                    control={control}
                    name={"BookPagereference"}
                    defaultValue={assetcommonforall?.BookPagereference}
                    rules={{
                        required: t("CORE\_COMMON\_REQUIRED\_ERRMSG"),
                        validate: {
                        pattern: (val) => /^[a-zA-Z0-9\s\/\[\]\*]+$/.test(val) || t("ERR\_DEFAULT\_INPUT\_FIELD\_MSG"),
                        },
                    }}
                    render={(props) => (
                        <TextInput
                        value={props.value}
                        // disable={isEditScreen}
                        autoFocus={focusIndex.index === assetcommonforall?.key && focusIndex.type === "BookPagereference"}
                        onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetcommonforall.key, type: "BookPagereference" });
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

                {/* <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_ID") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"AssetId"}
                        defaultValue={assetcommonforall?.AssetId}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                        }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetcommonforall?.key && focusIndex.type === "AssetId"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetcommonforall.key, type: "AssetId" });
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
                <CardLabelError style={errorStyle}>{localFormState.touched.AssetId ? errors?.AssetId?.message : ""}</CardLabelError> */}

                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_NAME") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"AssetName"}
                        defaultValue={assetcommonforall?.AssetName}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                        }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetcommonforall?.key && focusIndex.type === "AssetName"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetcommonforall.key, type: "AssetName" });
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
                    <CardLabel className="card-label-smaller">{t("ASSET_DESCRIPTION") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"Assetdescription"}
                        defaultValue={assetcommonforall?.Assetdescription}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                        }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetcommonforall?.key && focusIndex.type === "Assetdescription"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetcommonforall.key, type: "Assetdescription" });
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
                    <CardLabel className="card-label-smaller">{t("AST_DEPARTMENT") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"Department"}
                        defaultValue={assetcommonforall?.Department}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                        }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetcommonforall?.key && focusIndex.type === "Department"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetcommonforall.key, type: "Department" });
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
                <CardLabelError style={errorStyle}>{localFormState.touched.Department ? errors?.Department?.message : ""}</CardLabelError>

                
                

              
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

    export default AssetCommonDetails;