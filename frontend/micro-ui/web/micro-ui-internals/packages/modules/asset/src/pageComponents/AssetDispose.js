import {
    CardLabel,
    CardLabelError,
    LabelFieldPair,
    TextInput,
    Toast,
    Row,
    StatusTable,
    TextArea,
    Dropdown,
    CheckBox
} from "@nudmcdgnpm/digit-ui-react-components";
import _ from "lodash";
import React, { useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import { checkForNA } from "../utils";


const createAssetcommonforAll = () => ({

    department: "",
    assignedUser: "",
    designation: "",
    employeeCode: "",
    transferDate: "",
    returnDate: "",
    allocatedDepartment: "",
    key: Date.now(),
});

const AssetDispose = ({ config, onSelect, formData, setError, formState, clearErrors }) => {
    const { t } = useTranslation();


    const [disposeDetails, setdisposeDetails] = useState(formData?.disposeDetails || [createAssetcommonforAll()]);

    const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });
    useEffect(() => {
        onSelect(config?.key, disposeDetails);

    }, [disposeDetails]);



    const { data: departmentName } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "common-masters", [{ name: "Department" }],
        {
            select: (data) => {
                const formattedData = data?.["common-masters"]?.["Department"]
                const activeData = formattedData?.filter(item => item.active === true);
                return activeData;
            },
        });


    let departNamefromMDMS = [];

    departmentName && departmentName.map((departmentname) => {
        departNamefromMDMS.push({ i18nKey: `${departmentname.name}`, code: `${departmentname.code}`, value: `${departmentname.name}` })
    })


    const { data: designationData } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "common-masters", [{ name: "Designation" }],
        {
            select: (data) => {
                const formattedData = data?.["common-masters"]?.["Designation"]
                const activeData = formattedData?.filter(item => item.active === true);
                return activeData;
            },
        });
    const designationNamefromMDMS = [];

    designationData && designationData.map((designation) => {
        designationNamefromMDMS.push({ i18nKey: `${designation.name}`, code: `${designation.code}`, value: `${designation.name}` })
    })

    const commonProps = {
        focusIndex,
        allAssets: disposeDetails,
        setFocusIndex,
        formData,
        formState,
        setdisposeDetails,
        t,
        setError,
        clearErrors,
        config,
        departNamefromMDMS,
        designationNamefromMDMS
    };

    return (
        <React.Fragment>
            {disposeDetails.map((disposeDetails, index) => (
                <OwnerForm key={disposeDetails.key} index={index} disposeDetails={disposeDetails} {...commonProps} />
            ))}
        </React.Fragment>
    )
};
const OwnerForm = (_props) => {
    const {
        disposeDetails,
        focusIndex,
        allAssets,
        setdisposeDetails,
        t,
        config,
        setError,
        clearErrors,
        formState,
        setFocusIndex,
        departNamefromMDMS,
        designationNamefromMDMS
    } = _props;


    const [showToast, setShowToast] = useState(null);
    const [isDisposed, setIsDisposed] = useState(true);

    const { control, formState: localFormState, watch, setError: setLocalError, clearErrors: clearLocalErrors, setValue, trigger, } = useForm();
    const formValue = watch();
    const { errors } = localFormState;

    const { id: applicationNo } = useParams();
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const { data: applicationDetails } = Digit.Hooks.asset.useAssetApplicationDetail(t, tenantId, applicationNo);
    const [part, setPart] = React.useState({});


    useEffect(() => {
        if (!_.isEqual(part, formValue)) {
            setPart({ ...formValue });
            setdisposeDetails((prev) => prev.map((o) => (o.key && o.key === disposeDetails.key ? { ...o, ...formValue/*, ..._ownerType*/ } : { ...o })));
            trigger();
        }
    }, [formValue]);

    useEffect(() => {
        if (Object.keys(errors).length && !_.isEqual(formState.errors[config.key]?.type || {}, errors))
            setError(config.key, { type: errors });
        else if (!Object.keys(errors).length && formState.errors[config.key]) clearErrors(config.key);
    }, [errors]);

    const errorStyle = { width: "70%", marginLeft: "30%", fontSize: "12px", marginTop: "-21px" };

  
// Function to toggle the visibility
  const toggleDisposed = () => {
    setIsDisposed(!isDisposed);
  };
    const reasonDisposal = [
        {
          code: "End of Life",
          i18nKey: "END_OF_LIFE",
        },
        {
          code: "Obsolete(outdated)",
          i18nKey: "OBSOLETE(OUTDATED)",
        },
        {
          code: "Damaged",
          i18nKey: "DAMAGED",
        },
        {
          code: "Others",
          i18nKey: "OTHERS",
        },
      ];

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
                            label={t("AST_NAME")}
                            text={`${t(checkForNA(applicationDetails?.applicationData?.applicationData?.applicationNo))}`}
                        />
                    </StatusTable>
                    <StatusTable>
                        <Row
                            label={t("AST_CURRENT_COST")}
                            text={`${t(checkForNA('COMMON_MASTERS_DEPARTMENT_'+applicationDetails?.applicationData?.applicationData?.department))}`}
                        />
                    </StatusTable>
                    <StatusTable>
                        <Row
                            label={t("AST_LIFE")}
                            text={`${t(checkForNA('COMMON_MASTERS_DEPARTMENT_'+applicationDetails?.applicationData?.applicationData?.department))}`}
                        />
                    </StatusTable>
                    <StatusTable>
                        <Row
                            label={t("AST_RESIDUAL_LIFE")}
                            text={`${t(checkForNA('COMMON_MASTERS_DEPARTMENT_'+applicationDetails?.applicationData?.applicationData?.department))}`}
                        />
                    </StatusTable>
                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_DISPOSAL_DATE")}</CardLabel>
                        <div className="field">
                            <Controller
                                control={control}
                                name={"transferDate"}
                                defaultValue={disposeDetails?.transferDate}
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
                    
                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_REASON_DISPOSAL")}</CardLabel>

                        <Controller
                            control={control}
                            name={"designation"}
                            defaultValue={disposeDetails?.designation}
                            render={(props) => (
                                <Dropdown
                                    className="form-field"
                                    selected={props.value}
                                    select={props.onChange}
                                    onBlur={props.onBlur}
                                    option={reasonDisposal}
                                    optionKey="i18nKey"
                                    t={t}
                                />
                            )}
                        />
                    </LabelFieldPair>

                    <CardLabelError style={errorStyle}>{localFormState.touched.designation ? errors?.designation?.message : ""}</CardLabelError>
                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_ADD_COMMENTS")}</CardLabel>
                        <div className="field">
                            <Controller
                                control={control}
                                name={"assignedUser"}
                                defaultValue={disposeDetails?.assignedUser}
                                rules={{
                                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                                }}
                                render={(props) => (
                                    <TextArea
                                    type={"textarea"}
                                        value={props.value}
                                        autoFocus={focusIndex.index === disposeDetails?.key && focusIndex.type === "assignedUser"}
                                        onChange={(e) => {
                                            props.onChange(e.target.value);
                                            setFocusIndex({ index: disposeDetails.key, type: "assignedUser" });
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
                        <CardLabel className="card-label-smaller">{t("AST_AMOUNT_RECEIVED_DISPOSAL")}</CardLabel>
                        <div className="field">
                            <Controller
                                control={control}
                                name={"employeeCode"}
                                defaultValue={disposeDetails?.employeeCode}
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
                                        autoFocus={focusIndex.index === disposeDetails?.key && focusIndex.type === "employeeCode"}
                                        onChange={(e) => {
                                            props.onChange(e.target.value);
                                            setFocusIndex({ index: disposeDetails.key, type: "employeeCode" });
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
                        <CardLabel className="card-label-smaller">{t("AST_DISPOSAL_CODE")}</CardLabel>
                        <div className="field" style={{ marginTop: "20px", marginBottom: "20px" }}>
                            <Controller
                                control={control}
                                name={"employeeCode"}
                                defaultValue={disposeDetails?.employeeCode}
                                rules={{
                                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                                    validate: {
                                        pattern: (val) =>
                                            /^[a-zA-Z0-9\s\-/]+$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG")
                                    },
                                }}
                                render={(props) => (
                                    <CheckBox
                                                label={t(" If disposed in facility, amount received will become zero")}
                                                onChange={toggleDisposed}
                                                styles={{ height: "auto" }}
                                                //disabled={!agree}
                                              />
                                )}
                            />
                        </div>
                    </LabelFieldPair>
                    <CardLabelError style={errorStyle}>{localFormState.touched.employeeCode ? errors?.employeeCode?.message : ""}</CardLabelError>
                    { isDisposed && 
                    <div>
                        <LabelFieldPair>
                            <CardLabel className="card-label-smaller">{t("AST_PURCHASER_NAME")}</CardLabel>
                            <div className="field">
                                <Controller
                                    control={control}
                                    name={"employeeCode"}
                                    defaultValue={disposeDetails?.employeeCode}
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
                                            autoFocus={focusIndex.index === disposeDetails?.key && focusIndex.type === "employeeCode"}
                                            onChange={(e) => {
                                                props.onChange(e.target.value);
                                                setFocusIndex({ index: disposeDetails.key, type: "employeeCode" });
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
                            <CardLabel className="card-label-smaller">{t("AST_PAYMENT_MODE")}</CardLabel>
                            <div className="field">
                                <Controller
                                    control={control}
                                    name={"employeeCode"}
                                    defaultValue={disposeDetails?.employeeCode}
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
                                            autoFocus={focusIndex.index === disposeDetails?.key && focusIndex.type === "employeeCode"}
                                            onChange={(e) => {
                                                props.onChange(e.target.value);
                                                setFocusIndex({ index: disposeDetails.key, type: "employeeCode" });
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
                            <CardLabel className="card-label-smaller">{t("AST_RECEIPT")}</CardLabel>
                            <div className="field">
                                <Controller
                                    control={control}
                                    name={"employeeCode"}
                                    defaultValue={disposeDetails?.employeeCode}
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
                                            autoFocus={focusIndex.index === disposeDetails?.key && focusIndex.type === "employeeCode"}
                                            onChange={(e) => {
                                                props.onChange(e.target.value);
                                                setFocusIndex({ index: disposeDetails.key, type: "employeeCode" });
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
                            <CardLabel className="card-label-smaller">{t("AST_DISPOSAL_CODE")}</CardLabel>
                            <div className="field">
                                <Controller
                                    control={control}
                                    name={"employeeCode"}
                                    defaultValue={disposeDetails?.employeeCode}
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
                                            autoFocus={focusIndex.index === disposeDetails?.key && focusIndex.type === "employeeCode"}
                                            onChange={(e) => {
                                                props.onChange(e.target.value);
                                                setFocusIndex({ index: disposeDetails.key, type: "employeeCode" });
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
                    </div>
                    }

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

export default AssetDispose;