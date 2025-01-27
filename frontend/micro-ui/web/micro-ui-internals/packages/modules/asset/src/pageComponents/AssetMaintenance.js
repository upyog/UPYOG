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
    CheckBox,
    UploadFile
} from "@upyog/digit-ui-react-components";
import _ from "lodash";
import React, { useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import { checkForNA } from "../utils";


const createAssetcommonforAll = () => ({

    disposalDate: "",
    reasonForDisposal: "",
    comments: "",
    amountReceived: "",
    isAssetDisposedInFacility : true,
    purchaserName: "",
    paymentMode: "",
    receipt: "",
    disposedFacility :"",
    currentAgeOfAsset:"",
    assetId:"",
    key: Date.now(),
});

const AssetMaintenance = ({ config, onSelect, formData, formState, clearErrors }) => {
    const { t } = useTranslation();
    const [maintenanceDetails, setMaintenanceDetails] = useState(formData?.maintenanceDetails || [createAssetcommonforAll()]);
    
    const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });
     const [error, setError] = useState(null);
    
    useEffect(() => {
        onSelect(config?.key, maintenanceDetails);

    }, [maintenanceDetails]);

      
    const commonProps = {
        focusIndex,
        allAssets: maintenanceDetails,
        setFocusIndex,
        formData,
        formState,
        setMaintenanceDetails,
        t,
        setError,
        clearErrors,
        config
    };
    
    return (
        <React.Fragment>
            {maintenanceDetails.map((maintenanceDetails, index) => (
                <OwnerForm key={maintenanceDetails.key} index={index} maintenanceDetails={maintenanceDetails} {...commonProps} />
            ))}
        </React.Fragment>
    )
};
const OwnerForm = (_props) => {
    const {
        maintenanceDetails,
        focusIndex,
        allAssets,
        setMaintenanceDetails,
        t,
        config,
        setError,
        clearErrors,
        formState,
        setFocusIndex
    } = _props;


    const [showToast, setShowToast] = useState(null);
    const [isDisposed, setIsDisposed] = useState(false);
    const [currentAgeOfAsset, setCurrentAgeOfAsset] = useState('');

    const { control, formState: localFormState, watch, setError: setLocalError, clearErrors: clearLocalErrors, setValue, trigger,register } = useForm();
    const formValue = watch();
    const { errors } = localFormState;

    const { id: applicationNo } = useParams();
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const [part, setPart] = React.useState({});
    const [file, setFile] = useState(null);
    const [uploadedFile, setUploadedFile] = useState("");
    const [uploadError, setUploadError] = useState("");
    const [applicationData, setApplicationData] = useState({});
    const { data: applicationDetails } = Digit.Hooks.asset.useAssetApplicationDetail(t, tenantId, applicationNo);
   
    

    useEffect(() => {
        if (Object.keys(errors).length && !_.isEqual(formState.errors[config.key]?.type || {}, errors))
            setError(config.key, { type: errors });
        else if (!Object.keys(errors).length && formState.errors[config.key]) clearErrors(config.key);
    }, [errors]);
const errorStyle = { width: "70%", marginLeft: "30%", fontSize: "12px", marginTop: "-21px" };

    const typeOfServices = [
        {
          code: "AMC",
          i18nKey: "AST_AMC",
        },
        {
          code: "CMC",
          i18nKey: "AST_CMC",
        },
        {
          code: "OTHER",
          i18nKey: "AST_OTHER",
        }
      ];
      const routineMaintenance = [
        {
          code: "MONTHLY",
          i18nKey: "AST_MONTHLY_SERVICE",
        },
        {
          code: "QUARTERLY",
          i18nKey: "AST_QUARTERLY_SERVICE",
        },
        {
          code: "HALF YEARLY",
          i18nKey: "AST_HALF_YEARLY_SERVICE",
        },
        {
          code: "YEARLY",
          i18nKey: "AST_YEARLY_SERVICE",
        },
        {
          code: "OTHER",
          i18nKey: "AST_OTHER_SERVICE",
        }
      ];


  
    return (
        <React.Fragment>
            <div style={{ marginBottom: "16px" }}>
                <div style={{ border: "1px solid #E3E3E3", padding: "16px", marginTop: "8px" }}>
                  
                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_DISPOSAL_DATE")}</CardLabel>
                        <div className="field">
                            <Controller
                                control={control}
                                name={"disposalDate"}
                                defaultValue={maintenanceDetails?.disposalDate}
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
                        <CardLabel className="card-label-smaller">{t("AST_TYPE_OF_SERVICE")}</CardLabel>
                        <Controller
                            control={control}
                            name={"reasonForDisposal"}
                            defaultValue={maintenanceDetails?.reasonForDisposal}
                            render={(props) => (
                                <Dropdown
                                    className="form-field"
                                    selected={props.value}
                                    select={props.onChange}
                                    onBlur={props.onBlur}
                                    option={typeOfServices}
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
                                name={"comments"}
                                defaultValue={maintenanceDetails?.comments}
                                rules={{
                                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                                }}
                                render={(props) => (
                                    <TextArea
                                    type={"textarea"}
                                        value={props.value}
                                        autoFocus={focusIndex.index === maintenanceDetails?.key && focusIndex.type === "comments"}
                                        onChange={(e) => {
                                            props.onChange(e.target.value);
                                            setFocusIndex({ index: maintenanceDetails.key, type: "comments" });
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
                                name={"amountReceived"}
                                defaultValue={maintenanceDetails?.amountReceived}
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
                                        autoFocus={focusIndex.index === maintenanceDetails?.key && focusIndex.type === "amountReceived"}
                                        onChange={(e) => {
                                            props.onChange(e.target.value);
                                            setFocusIndex({ index: maintenanceDetails.key, type: "amountReceived" });
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
                                name={"isAssetDisposedInFacility"}
                                defaultValue={maintenanceDetails?.isAssetDisposedInFacility}
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
                                                // onChange={toggleDisposed}
                                                styles={{ height: "auto" }}
                                                value={props.value}
                                                checked={!isDisposed}
                                              />
                                )}
                            />
                        </div>
                    </LabelFieldPair>
                    <CardLabelError style={errorStyle}>{localFormState.touched.employeeCode ? errors?.employeeCode?.message : ""}</CardLabelError>

                    <LabelFieldPair>
                            <CardLabel className="card-label-smaller">{t("AST_SERVICE_UNDER_WARRANTY")}</CardLabel>
                            <div className="field">
                            <Controller
                                control={control}
                                name={"disposalFile"}
                                render={(props) => (
                                <UploadFile
                                    id={"disposalFile"}
                                    // onUpload={selectFile}
                                    onDelete={() => {
                                    setFile(null);
                                    props.onChange(null); // Clear the file in form state
                                    }}
                                    message={file ? `1 ${t(`CS_ACTION_FILEUPLOADED`)}` : t(`CS_ACTION_NO_FILEUPLOADED`)}
                                    accept="image/*, .pdf, .png, .jpeg, .jpg"
                                    buttonType="button"
                                    error={uploadError || !file} // Show error if uploadError is not empty or no file
                                />
                                )}
                            />
                            </div>
                        </LabelFieldPair>

                    { isDisposed && 
                    <div style={{ marginTop:'15px'}}>
                        <LabelFieldPair>
                            <CardLabel className="card-label-smaller">{t("AST_PURCHASER_NAME")}</CardLabel>
                            <div className="field">
                                <Controller
                                    control={control}
                                    name={"purchaserName"}
                                    defaultValue={maintenanceDetails?.purchaserName}
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
                                            autoFocus={focusIndex.index === maintenanceDetails?.key && focusIndex.type === "purchaserName"}
                                            onChange={(e) => {
                                                props.onChange(e.target.value);
                                                setFocusIndex({ index: maintenanceDetails.key, type: "purchaserName" });
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
                                    name={"paymentMode"}
                                    defaultValue={maintenanceDetails?.paymentMode}
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
                                            autoFocus={focusIndex.index === maintenanceDetails?.key && focusIndex.type === "paymentMode"}
                                            onChange={(e) => {
                                                props.onChange(e.target.value);
                                                setFocusIndex({ index: maintenanceDetails.key, type: "paymentMode" });
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
                                    name={"receiptNumber"}
                                    defaultValue={maintenanceDetails?.receiptNumber}
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
                                            autoFocus={focusIndex.index === maintenanceDetails?.key && focusIndex.type === "receiptNumber"}
                                            onChange={(e) => {
                                                props.onChange(e.target.value);
                                                setFocusIndex({ index: maintenanceDetails.key, type: "receiptNumber" });
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
                                    defaultValue={maintenanceDetails?.employeeCode}
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
                                            autoFocus={focusIndex.index === maintenanceDetails?.key && focusIndex.type === "employeeCode"}
                                            onChange={(e) => {
                                                props.onChange(e.target.value);
                                                setFocusIndex({ index: maintenanceDetails.key, type: "employeeCode" });
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

export default AssetMaintenance;