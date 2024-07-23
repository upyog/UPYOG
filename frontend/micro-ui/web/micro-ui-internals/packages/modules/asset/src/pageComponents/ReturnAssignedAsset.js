import {
    CardLabel,
    CardLabelError,
    LabelFieldPair,
    TextInput,
    Toast,
    Row,
    StatusTable
    } from "@nudmcdgnpm/digit-ui-react-components";
    import _ from "lodash";
    import React, { useEffect, useState } from "react";
    import { Controller, useForm } from "react-hook-form";
    import { useTranslation } from "react-i18next";
    import { useParams } from "react-router-dom";
    import { checkForNA } from "../utils";


    const createAssetcommonforAll = () => ({
        returnDate: "",
        key: Date.now(),
    });

    const ReturnAssignedAsset = ({ config, onSelect, formData, setError, formState, clearErrors }) => {
    const { t } = useTranslation();


    const [returndetails, setreturndetails] = useState(formData?.returndetails || [createAssetcommonforAll()]);
    

    const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });
    useEffect(() => {
    onSelect(config?.key, returndetails);

    }, [returndetails]);






    const commonProps = {
        focusIndex,
        allAssets: returndetails,
        setFocusIndex,
        formData,
        formState,
        setreturndetails,
        t,
        setError,
        clearErrors,
        config,
        
    };

    return (
        <React.Fragment>
        {returndetails.map((returndetails, index) => (
            <OwnerForm key={returndetails.key} index={index} returndetails={returndetails} {...commonProps} />
        ))}
        </React.Fragment>
    )
    };
    const OwnerForm = (_props) => {
    const {
        returndetails,
        focusIndex,
        allAssets,      
        setreturndetails,
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

    

    const formatDate = (epochTime) => {
        if (!epochTime) return '';
        const date = new Date(epochTime);
        return date.toLocaleDateString('en-GB', { 
          day: '2-digit', 
          month: '2-digit', 
          year: 'numeric' 
        }).replace(/\//g, '/');
      };

    useEffect(() => {
        if (!_.isEqual(part, formValue)) {
        setPart({ ...formValue });
        setreturndetails((prev) => prev.map((o) => (o.key && o.key === returndetails.key ? { ...o, ...formValue/*, ..._ownerType*/ } : { ...o })));
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

                <StatusTable>
                <Row
                    label={t("ES_ASSET_RESPONSE_CREATE_LABEL")}
                    text={`${t(checkForNA(applicationDetails?.applicationData?.applicationData?.applicationNo))}`}
                />
                </StatusTable>
                <StatusTable>
                <Row
                    label={t("AST_ASSIGNED_USER")}
                    text={`${t(checkForNA(applicationDetails?.applicationData?.applicationData?.assetAssignment?.assignedUserName))}`}
                />
                </StatusTable>
                <StatusTable>
                <Row
                    label={t("AST_ASSIGNED_DATE")}
                    text={`${t(checkForNA(formatDate(applicationDetails?.applicationData?.applicationData?.assetAssignment?.assignedDate)))}`}
                />
                </StatusTable>
                <StatusTable>
                <Row
                    label={t("AST_DESIGNATION")}
                    text={`${t(checkForNA(applicationDetails?.applicationData?.applicationData?.assetAssignment?.designation))}`}
                />
                </StatusTable>
                <StatusTable>
                <Row
                    label={t("AST_EMP_CODE")}
                    text={`${t(checkForNA(applicationDetails?.applicationData?.applicationData?.assetAssignment?.employeeCode))}`}
                />
                </StatusTable>
                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_RETURN_DATE") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"returnDate"}
                        defaultValue={returndetails?.returnDate}
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

    export default ReturnAssignedAsset;