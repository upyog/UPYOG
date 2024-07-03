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

        const createAssetbuildings = () => ({


            Buildingsno: "",
            plotarea: "",
            plintharea: "",
            floorno: "",
            dimensions: "",
            floorarea: "",
            BookValue: "",
            Totalcost: "",
            DepriciationRate: "",
            Costafterdepriciation: "",
            Currentassetvalue: "",
            Revenuegeneratedbyasset: "",
            howassetbeingused: "",


        

        key: Date.now(),
        });

        const AssetBuildings = ({ config, onSelect, userType, formData, setError, formState, clearErrors }) => {
        const { t } = useTranslation();

        const { pathname } = useLocation();
        const [assetsbuildingdetail, setAssets] = useState(formData?.assetsbuildingdetail || [createAssetbuildings()]);

        
        const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });

        const tenantId = Digit.ULBService.getCurrentTenantId();
        const stateId = Digit.ULBService.getStateId();




        

        // By adding this conditional check, i am  ensuring that onSelect is a function before trying to call it.
        // This should resolve the "onSelect is not a function" error i am  encountering. 

        useEffect(() => {
            if (onSelect) {
                onSelect(config?.key, assetsbuildingdetail);
            }
            }, [assetsbuildingdetail, onSelect]);




        const commonProps = {
            focusIndex,
            allAssets: assetsbuildingdetail,
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
            {assetsbuildingdetail.map((assetsbuildingdetail, index) => (
                <OwnerForm key={assetsbuildingdetail.key} index={index} assetsbuildingdetail={assetsbuildingdetail} {...commonProps} />
            ))}
            </React.Fragment>
        )
        };

        const OwnerForm = (_props) => {
        const {
            assetsbuildingdetail,
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
            setAssets((prev) => prev.map((o) => (o.key && o.key === assetsbuildingdetail.key ? { ...o, ...formValue/*, ..._ownerType*/ } : { ...o })));
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

        
                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("ASSET_BUILDING_NO") + " *"}</CardLabel>
                        <div className="field">
                        <Controller
                            control={control}
                            name={"Buildingsno"}
                            defaultValue={assetsbuildingdetail?.Buildingsno}
                            rules={{
                            required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                            validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                            }}
                            render={(props) => (
                            <TextInput
                                value={props.value}
                                // disable={isEditScreen}
                                autoFocus={focusIndex.index === assetsbuildingdetail?.key && focusIndex.type === "Buildingsno"}
                                onChange={(e) => {
                                props.onChange(e.target.value);
                                setFocusIndex({ index: assetsbuildingdetail.key, type: "Buildingsno" });
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
                    <CardLabelError style={errorStyle}>{localFormState.touched.Buildingsno ? errors?.Buildingsno?.message : ""}</CardLabelError>

                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("ASSET_PLOT_AREA") + " *"}</CardLabel>
                        <div className="field">
                        <Controller
                            control={control}
                            name={"plotarea"}
                            defaultValue={assetsbuildingdetail?.plotarea}
                            rules={{
                            required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                            validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                            }}
                            render={(props) => (
                            <TextInput
                                value={props.value}
                                // disable={isEditScreen}
                                autoFocus={focusIndex.index === assetsbuildingdetail?.key && focusIndex.type === "plotarea"}
                                onChange={(e) => {
                                props.onChange(e.target.value);
                                setFocusIndex({ index: assetsbuildingdetail.key, type: "plotarea" });
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
                    <CardLabelError style={errorStyle}>{localFormState.touched.plotarea ? errors?.plotarea?.message : ""}</CardLabelError>

                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("ASSET_PLINTH_AREA") + " *"}</CardLabel>
                        <div className="field">
                        <Controller
                            control={control}
                            name={"plintharea"}
                            defaultValue={assetsbuildingdetail?.plintharea}
                            rules={{
                            required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                            validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                            }}
                            render={(props) => (
                            <TextInput
                                value={props.value}
                                // disable={isEditScreen}
                                autoFocus={focusIndex.index === assetsbuildingdetail?.key && focusIndex.type === "plintharea"}
                                onChange={(e) => {
                                props.onChange(e.target.value);
                                setFocusIndex({ index: assetsbuildingdetail.key, type: "plintharea" });
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
                    <CardLabelError style={errorStyle}>{localFormState.touched.plintharea ? errors?.plintharea?.message : ""}</CardLabelError>

                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("ASSET_FLOORS_NO") + " *"}</CardLabel>
                        <div className="field">
                        <Controller
                            control={control}
                            name={"floorno"}
                            defaultValue={assetsbuildingdetail?.floorno}
                            rules={{
                            required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                            validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                            }}
                            render={(props) => (
                            <TextInput
                                value={props.value}
                                // disable={isEditScreen}
                                autoFocus={focusIndex.index === assetsbuildingdetail?.key && focusIndex.type === "floorno"}
                                onChange={(e) => {
                                props.onChange(e.target.value);
                                setFocusIndex({ index: assetsbuildingdetail.key, type: "floorno" });
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
                    <CardLabelError style={errorStyle}>{localFormState.touched.floorno ? errors?.floorno?.message : ""}</CardLabelError>

                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("ASSET_DIMENSIONS") + " *"}</CardLabel>
                        <div className="field">
                        <Controller
                            control={control}
                            name={"dimensions"}
                            defaultValue={assetsbuildingdetail?.dimensions}
                            rules={{
                            required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                            validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                            }}
                            render={(props) => (
                            <TextInput
                                value={props.value}
                                // disable={isEditScreen}
                                autoFocus={focusIndex.index === assetsbuildingdetail?.key && focusIndex.type === "dimensions"}
                                onChange={(e) => {
                                props.onChange(e.target.value);
                                setFocusIndex({ index: assetsbuildingdetail.key, type: "dimensions" });
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
                    <CardLabelError style={errorStyle}>{localFormState.touched.dimensions ? errors?.dimensions?.message : ""}</CardLabelError>

                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("ASSET_AREA_FLOOR") + " *"}</CardLabel>
                        <div className="field">
                        <Controller
                            control={control}
                            name={"floorarea"}
                            defaultValue={assetsbuildingdetail?.floorarea}
                            rules={{
                            required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                            validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                            }}
                            render={(props) => (
                            <TextInput
                                value={props.value}
                                // disable={isEditScreen}
                                autoFocus={focusIndex.index === assetsbuildingdetail?.key && focusIndex.type === "floorarea"}
                                onChange={(e) => {
                                props.onChange(e.target.value);
                                setFocusIndex({ index: assetsbuildingdetail.key, type: "floorarea" });
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
                    <CardLabelError style={errorStyle}>{localFormState.touched.floorarea ? errors?.floorarea?.message : ""}</CardLabelError>

                    <br></br>
                <div style={{fontWeight: 'bold', fontSize: '24px'}}>{t("ASSET_ACQUISTION_DETAILS")}<br></br></div>
                <br></br>

                    <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("ASSET_ACQUISTION_COST_BOOK_VALUE") + " *"}</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"BookValue"}
                        defaultValue={assetsbuildingdetail?.BookValue}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                        }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetsbuildingdetail?.key && focusIndex.type === "BookValue"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetsbuildingdetail.key, type: "BookValue" });
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
                <CardLabelError style={errorStyle}>{localFormState.touched.BookValue ? errors?.BookValue?.message : ""}</CardLabelError>


                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("ASSET_TOTAL_COST") + " *"}</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"Totalcost"}
                        defaultValue={assetsbuildingdetail?.Totalcost}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                        }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetsbuildingdetail?.key && focusIndex.type === "Totalcost"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetsbuildingdetail.key, type: "Totalcost" });
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
                <CardLabelError style={errorStyle}>{localFormState.touched.Totalcost ? errors?.Totalcost?.message : ""}</CardLabelError>

                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("ASSET_DEPRICIATION_RATE") + " *"}</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"DepriciationRate"}
                        defaultValue={assetsbuildingdetail?.DepriciationRate}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                        }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetsbuildingdetail?.key && focusIndex.type === "DepriciationRate"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetsbuildingdetail.key, type: "DepriciationRate" });
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
                <CardLabelError style={errorStyle}>{localFormState.touched.DepriciationRate ? errors?.DepriciationRate?.message : ""}</CardLabelError>

                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("ASSET_COST_DEPRICIATION") + " *"}</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"Costafterdepriciation"}
                        defaultValue={assetsbuildingdetail?.Costafterdepriciation}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                        }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetsbuildingdetail?.key && focusIndex.type === "Costafterdepriciation"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetsbuildingdetail.key, type: "Costafterdepriciation" });
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
                <CardLabelError style={errorStyle}>{localFormState.touched.Costafterdepriciation ? errors?.Costafterdepriciation?.message : ""}</CardLabelError>

                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("ASSET_CURRENT_VALUE") + " *"}</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"Currentassetvalue"}
                        defaultValue={assetsbuildingdetail?.Currentassetvalue}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                        }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetsbuildingdetail?.key && focusIndex.type === "Currentassetvalue"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetsbuildingdetail.key, type: "Currentassetvalue" });
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
                <CardLabelError style={errorStyle}>{localFormState.touched.Currentassetvalue ? errors?.Currentassetvalue?.message : ""}</CardLabelError>

                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("ASSET_REVENUE_GENERATED") + " *"}</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"Revenuegeneratedbyasset"}
                        defaultValue={assetsbuildingdetail?.Revenuegeneratedbyasset}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                        }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetsbuildingdetail?.key && focusIndex.type === "Revenuegeneratedbyasset"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetsbuildingdetail.key, type: "Revenuegeneratedbyasset" });
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
                <CardLabelError style={errorStyle}>{localFormState.touched.Revenuegeneratedbyasset ? errors?.Revenuegeneratedbyasset?.message : ""}</CardLabelError>

                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("ASSET_BEING_USED") + " *"}</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"howassetbeingused"}
                        defaultValue={assetsbuildingdetail?.howassetbeingused}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                        }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetsbuildingdetail?.key && focusIndex.type === "howassetbeingused"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetsbuildingdetail.key, type: "howassetbeingused" });
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
                <CardLabelError style={errorStyle}>{localFormState.touched.howassetbeingused ? errors?.howassetbeingused?.message : ""}</CardLabelError>



            

                

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

        export default AssetBuildings;