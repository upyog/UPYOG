import {
    CardLabel,
    CardLabelError,
    LabelFieldPair,
    TextInput,
    Toast,
    Row,
    StatusTable,
    Dropdown
    } from "@nudmcdgnpm/digit-ui-react-components";
    import _ from "lodash";
    import React, { useEffect, useState } from "react";
    import { Controller, useForm } from "react-hook-form";
    import { useTranslation } from "react-i18next";
    import { useParams } from "react-router-dom";
    import { checkForNA } from "../utils";


    const EditAssetDet = () => ({
        key: Date.now(),
    });

    const EditAssetDetails = ({ config, onSelect, formData, setError, formState, clearErrors }) => {
    const { t } = useTranslation();


    const [editAssetDetails, seteditAssetDetails] = useState(formData?.editAssetDetails || [EditAssetDet()]);

    const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });
    useEffect(() => {
    onSelect(config?.key, editAssetDetails);

    }, [editAssetDetails]);



    const commonProps = {
        focusIndex,
        allAssets: editAssetDetails,
        setFocusIndex,
        formData,
        formState,
        seteditAssetDetails,
        t,
        setError,
        clearErrors,
        config,
        
    };

    return (
        <React.Fragment>
        {editAssetDetails.map((editAssetDetails, index) => (
            <OwnerForm key={editAssetDetails.key} index={index} editAssetDetails={editAssetDetails} {...commonProps} />
        ))}
        </React.Fragment>
    )
    };
    const OwnerForm = (_props) => {
    const {
        editAssetDetails,
        focusIndex,     
        seteditAssetDetails,
        t,
        config,
        setError,
        clearErrors,
        formState,
        setFocusIndex,
       
    } = _props;


    const [showToast, setShowToast] = useState(null);
    const { control, formState: localFormState, watch, setError: setLocalError, clearErrors: clearLocalErrors, setValue, trigger, } = useForm();
    const formValue = watch();
    const { errors } = localFormState;

    const { id: applicationNo } = useParams();
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const { data: applicationDetails } = Digit.Hooks.asset.useAssetApplicationDetail(t, tenantId, applicationNo);
    const [part, setPart] = React.useState({});
    console.log("Editgenearldetails page application details",applicationDetails)

    useEffect(() => {
        if (!_.isEqual(part, formValue)) {
        setPart({ ...formValue });
        seteditAssetDetails((prev) => prev.map((o) => (o.key && o.key === editAssetDetails.key ? { ...o, ...formValue/*, ..._ownerType*/ } : { ...o })));
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

                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_ASSIGNED_USER") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"assignedUser"}
                        defaultValue={editAssetDetails?.assignedUser}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                        }}
                        render={(props) => (
                            console.log("propejesfhgewdgewhdesnf",props),
                        <TextInput
                            value={props.value}
                            autoFocus={focusIndex.index === editAssetDetails?.key && focusIndex.type === "assignedUser"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: editAssetDetails.key, type: "assignedUser" });
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
                <CardLabelError style={errorStyle}>{localFormState.touched.assignedUser ? errors?.assignedUser?.message : ""}</CardLabelError>

                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_DESIGNATION") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"designation"}
                        defaultValue={editAssetDetails?.designation}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                        }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === editAssetDetails?.key && focusIndex.type === "designation"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: editAssetDetails.key, type: "designation" });
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
                <CardLabelError style={errorStyle}>{localFormState.touched.designation ? errors?.designation?.message : ""}</CardLabelError>
                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_EMP_CODE") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"employeeCode"}
                        defaultValue={editAssetDetails?.employeeCode}
                        rules={{
                            required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                            validate: {
                                pattern: (val) => 
                                  /^[a-zA-Z0-9\s\-/]+$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG")
                              },
                          }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === editAssetDetails?.key && focusIndex.type === "employeeCode"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: editAssetDetails.key, type: "employeeCode" });
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
                <CardLabelError style={errorStyle}>{localFormState.touched.employeeCode ? errors?.employeeCode?.message : ""}</CardLabelError>
                <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("AST_ALLOCATED_DEPARTMENT")}</CardLabel>
                <Controller
                control={control}
                name={"allocatedDepartment"}
                defaultValue={editAssetDetails?.allocatedDepartment}
              
                render={(props) => (
                    <Dropdown
                    className="form-field"
                    selected={props.value}
                    select={props.onChange}
                    onBlur={props.onBlur}
                    option={""}
                    optionKey="i18nKey"
                    t={t}
                    />
                )}
                />
                </LabelFieldPair>
                <CardLabelError style={errorStyle}>{localFormState.touched.allocatedDepartment ? errors?.allocatedDepartment?.message : ""}</CardLabelError>

                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_TRANSFER_DATE") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"transferDate"}
                        defaultValue={editAssetDetails?.transferDate}
                        rules={{
                            required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                            validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                          }}
                        render={(props) => (
                        <TextInput
                            type="date"
                            value={props.value}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            }}
                            max={new Date().toISOString().split('T')[0]}
                        />
                        )}
                    />
                    </div>
                </LabelFieldPair>
                <CardLabelError style={errorStyle}>{localFormState.touched.employeeCode ? errors?.employeeCode?.message : ""}</CardLabelError>

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

    export default EditAssetDetails;