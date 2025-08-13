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
    UploadFile,
    InfoBannerIcon
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
    isAssetDisposedInFacility: true,
    purchaserName: "",
    paymentMode: "",
    receipt: "",
    disposedFacility: "",
    currentAgeOfAsset: "",
    assetId: "",
    key: Date.now(),
});

const AssetDispose = ({ config, onSelect, formData, formState, clearErrors }) => {
    const { t } = useTranslation();
    const [disposeDetails, setdisposeDetails] = useState(formData?.disposeDetails || [createAssetcommonforAll()]);

    const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });
    const [error, setError] = useState(null);

    //  this is use for state set
    useEffect(() => {
        onSelect(config?.key, disposeDetails);
    }, [disposeDetails]);


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
        config
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
        setFocusIndex
    } = _props;


    const [showToast, setShowToast] = useState(null);
    const [isDisposed, setIsDisposed] = useState(false);
    const [currentAgeOfAsset, setCurrentAgeOfAsset] = useState('');

    const { control, formState: localFormState, watch, setError: setLocalError, clearErrors: clearLocalErrors, setValue, trigger, register } = useForm();
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

    const { data: assetDisposalMDMS } = Digit.Hooks.useEnabledMDMS(Digit.ULBService.getStateId(), "ASSET", [{ name: "AssetDisposalType" }], {
        select: (data) => {
            const formattedData = data?.["ASSET"]?.["AssetDisposalType"];
            const activeData = formattedData?.filter((item) => item.active === true);
            return activeData;
        },
    });
    let reasonDisposal = [];
    assetDisposalMDMS &&
    assetDisposalMDMS.map((row) => {
            reasonDisposal.push({ i18nKey: `AST_${row.code}`, code: `${row.code}`, value: `${row.name}` });
        });

    useEffect(() => {
        if (applicationDetails) {
            const age = calculateAssetAge(applicationDetails?.applicationData?.applicationData?.purchaseDate);
            register("currentAgeOfAsset");
            register("assetId");
            register("lifeOfAsset");
            setValue("currentAgeOfAsset", age);
            setValue("assetId", applicationDetails?.applicationData?.applicationData?.id);
            setValue("lifeOfAsset", applicationDetails?.applicationData?.applicationData?.lifeOfAsset);
        }
    }, [applicationDetails]);


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

    function calculateAssetAge(purchaseDate) {
        // Convert purchaseDate (Unix timestamp) to JavaScript Date object
        const purchaseDateObj = new Date(purchaseDate * 1000); // Convert seconds to milliseconds
        const currentDate = new Date();

        // Calculate the difference in time (milliseconds)
        const differenceInTime = currentDate - purchaseDateObj;

        // Convert time difference to days
        const differenceInDays = Math.floor(differenceInTime / (1000 * 60 * 60 * 24));

        // Return the total number of days
        return differenceInDays;
    }


    useEffect(() => {
        register("fileStoreId");
        if (uploadedFile) {
            setValue("fileStoreId", uploadedFile);
        }
        console.log('test valie :- ', uploadedFile);
    }, [uploadedFile, register, setValue]);

    // const reasonDisposal = [
    //     {
    //         code: "End of Life",
    //         i18nKey: "END_OF_LIFE",
    //     },
    //     {
    //         code: "Obsolete(outdated)",
    //         i18nKey: "OBSOLETE(OUTDATED)",
    //     },
    //     {
    //         code: "Damaged",
    //         i18nKey: "DAMAGED",
    //     },
    //     {
    //         code: "Others",
    //         i18nKey: "OTHERS",
    //     },
    // ];

    function selectfile(e) {
        setFile(e.target.files[0]);
    }

    useEffect(() => {
        if (file) {
            if (file.size >= 5242880) {
                setError(t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
                setUploadedFile(null); // Clear previous successful upload
            } else {
                setError(""); // Clear any previous errors
                Digit.UploadServices.Filestorage("ASSET", file, Digit.ULBService.getStateId())
                    .then(response => {
                        console.log('Upload Response:', response);
                        if (response?.data?.files?.length > 0) {
                            setUploadedFile(response.data.files[0].fileStoreId);
                        } else {
                            setError(t("CS_FILE_UPLOAD_ERROR"));
                        }
                    })
                    .catch(() => setError(t("CS_FILE_UPLOAD_ERROR")));
            }
        }
    }, [file, t]);

    // Sample function to handle file selection
    const handleFileSelection = (selectedFile) => {
        setFile(selectedFile);
    };

    const selectFile = (e) => {
        const selectedFile = e.target.files[0];
        if (selectedFile) {
            // Example validation for file size (e.g., max 5MB)
            if (selectedFile.size > 5 * 1024 * 1024) {
                setUploadError("File size should not exceed 5MB");
                return;
            }

            // Set the file if validation passes
            setFile(selectedFile);
            setUploadError(""); // Clear any previous errors
            // Optionally, you can call props.onChange(selectedFile) if needed
        }
    };

    const calculateResidualLife = () => {
        let purchaseDate = applicationDetails?.applicationData?.applicationData?.purchaseDate; // in seconds
        let lifeOfAsset = applicationDetails?.applicationData?.applicationData?.lifeOfAsset;   // in years
        let currentDate = Date.now(); // in milliseconds

        // Convert purchaseDate from seconds to milliseconds
        let purchaseDateInMs = purchaseDate * 1000;

        // Calculate the difference in milliseconds
        let diffInMs = currentDate - purchaseDateInMs;

        // Convert milliseconds to years
        let diffInYears = diffInMs / (1000 * 60 * 60 * 24 * 365.25);

        // Calculate residual life
        let residualLife = lifeOfAsset - diffInYears;

        // Check if residual life is negative or zero
        if (residualLife <= 0) {
            return "0 years, 0 months, 0 days";
        }

        // Split residual life into years, months, and days
        let years = Math.floor(residualLife);
        let fractionalYear = residualLife - years;

        let totalDaysInFractionalYear = fractionalYear * 365.25;
        let months = Math.floor(totalDaysInFractionalYear / 30.44); // Average month length
        let days = Math.round(totalDaysInFractionalYear % 30.44);

        return `${years} years, ${months} months, ${days} days`;
    };
    const Tooltip = ({ message, children }) => {
        return (
            <div
                style={{
                    position: "relative",
                    display: "inline-block",
                }}
                onMouseEnter={(e) => {
                    const tooltip = e.currentTarget.querySelector(".tooltiptext");
                    tooltip.style.visibility = "visible";
                    tooltip.style.opacity = 1;
                }}
                onMouseLeave={(e) => {
                    const tooltip = e.currentTarget.querySelector(".tooltiptext");
                    tooltip.style.visibility = "hidden";
                    tooltip.style.opacity = 0;
                }}
            >
                {children}
                <span
                    style={{
                        visibility: "hidden",
                        position: "absolute",
                        backgroundColor: "#555",
                        color: "#fff",
                        textAlign: "center",
                        borderRadius: "4px",
                        padding: "5px",
                        fontSize: "small",
                        wordWrap: "break-word",
                        width: "300px",
                        top: "100%",
                        left: "50%",
                        transform: "translateX(-50%)",
                        marginTop: "5px",
                        zIndex: "1",
                        opacity: 0,
                        transition: "opacity 0.3s ease-in-out",
                    }}
                    className="tooltiptext"
                >
                    {message}
                </span>
            </div>
        );
    };
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
                            text={`${t(checkForNA(applicationDetails?.applicationData?.applicationData?.assetName))}`}
                        />
                    </StatusTable>
                    <StatusTable>
                        <Row
                            label={t("AST_CURRENT_COST")}
                            text={`${t(checkForNA(applicationDetails?.applicationData?.applicationData?.bookValue))}`}
                        />
                    </StatusTable>
                    <StatusTable>
                        <Row
                            label={t("AST_LIFE")}
                            text={`${t(checkForNA(applicationDetails?.applicationData?.applicationData?.lifeOfAsset))}`}
                        />
                    </StatusTable>
                    <StatusTable>
                        <Row
                            label={t("AST_RESIDUAL_LIFE")}
                            text={calculateResidualLife()}
                        />
                    </StatusTable>
                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_DISPOSAL_DATE")}</CardLabel>
                        <div className="field">
                            <Controller
                                control={control}
                                name={"disposalDate"}
                                defaultValue={disposeDetails?.disposalDate}
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
                        <CardLabel className="card-label-smaller">{t("AST_REASON_DISPOSAL")}
                        <Tooltip message={t("TOOLTIP_AST_REASON_DISPOSAL")}>
                            <div style={{ marginLeft: "8px"}}>
                                <InfoBannerIcon style={{ verticalAlign: "middle", cursor: "pointer"}} />
                            </div>
                            </Tooltip>
                            </CardLabel>
                        <Controller
                            control={control}
                            name={"reasonForDisposal"}
                            defaultValue={disposeDetails?.reasonForDisposal}
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
                                name={"comments"}
                                defaultValue={disposeDetails?.comments}
                                rules={{
                                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                                }}
                                render={(props) => (
                                    <TextArea
                                        type={"textarea"}
                                        value={props.value}
                                        autoFocus={focusIndex.index === disposeDetails?.key && focusIndex.type === "comments"}
                                        onChange={(e) => {
                                            props.onChange(e.target.value);
                                            setFocusIndex({ index: disposeDetails.key, type: "comments" });
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
                                defaultValue={disposeDetails?.amountReceived}
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
                                        autoFocus={focusIndex.index === disposeDetails?.key && focusIndex.type === "amountReceived"}
                                        onChange={(e) => {
                                            props.onChange(e.target.value);
                                            setFocusIndex({ index: disposeDetails.key, type: "amountReceived" });
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
                        <CardLabel className="card-label-smaller">{t("AST_DISPOSAL_CODE")}
                      
                        </CardLabel>
                        <div className="field" style={{ marginTop: "20px", marginBottom: "20px" }}>
                            <Controller
                                control={control}
                                name={"isAssetDisposedInFacility"}
                                defaultValue={disposeDetails?.isAssetDisposedInFacility}
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
                                        value={props.value}
                                        checked={!isDisposed}
                                    />
                                )}
                            />
                        </div>
                    </LabelFieldPair>
                    <CardLabelError style={errorStyle}>{localFormState.touched.employeeCode ? errors?.employeeCode?.message : ""}</CardLabelError>

                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_DISPOSE_RECIPT")}
                       
                        </CardLabel>
                        <div className="field">
                            <Controller
                                control={control}
                                name={"disposalFile"}
                                render={(props) => (
                                    <UploadFile
                                        id={"disposalFile"}
                                        onUpload={selectFile}
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

                    {isDisposed &&
                        <div style={{ marginTop: '15px' }}>
                            <LabelFieldPair>
                                <CardLabel className="card-label-smaller">{t("AST_PURCHASER_NAME")}</CardLabel>
                                <div className="field">
                                    <Controller
                                        control={control}
                                        name={"purchaserName"}
                                        defaultValue={disposeDetails?.purchaserName}
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
                                                autoFocus={focusIndex.index === disposeDetails?.key && focusIndex.type === "purchaserName"}
                                                onChange={(e) => {
                                                    props.onChange(e.target.value);
                                                    setFocusIndex({ index: disposeDetails.key, type: "purchaserName" });
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
                                        defaultValue={disposeDetails?.paymentMode}
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
                                                autoFocus={focusIndex.index === disposeDetails?.key && focusIndex.type === "paymentMode"}
                                                onChange={(e) => {
                                                    props.onChange(e.target.value);
                                                    setFocusIndex({ index: disposeDetails.key, type: "paymentMode" });
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
                                        defaultValue={disposeDetails?.receiptNumber}
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
                                                autoFocus={focusIndex.index === disposeDetails?.key && focusIndex.type === "receiptNumber"}
                                                onChange={(e) => {
                                                    props.onChange(e.target.value);
                                                    setFocusIndex({ index: disposeDetails.key, type: "receiptNumber" });
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