import {
    CardLabel,
    CardLabelError,
    LabelFieldPair,
    TextInput,
    Toast,
    Row,
    StatusTable,
    Dropdown,
    TextArea,
    UploadFile,
    RadioButtons,
    Card,
    InfoBannerIcon
} from "@upyog/digit-ui-react-components";
import _ from "lodash";
import React, { useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import { checkForNA } from "../utils";


const createAssetcommonforAll = () => ({

    warrantyStatus: "",
    assetWarrantyDescription: "",
    amcDetails: "",
    maintenanceType: "",
    paymentType: "",
    costOfMaintenance: "",
    description: "",
    vendor: "",
    maintenanceCycle: "",
    partsAddedOrReplaced: "",
    postConditionRemarks: "",
    preConditionRemarks: "",
    isAMCExpired: "",
    isWarrantyExpired: "",
    key: Date.now(),
});

const AssetMaintenance = ({ config, onSelect, formData, formState, clearErrors, setError }) => {

    const { t } = useTranslation();
    const [maintenanceDetails, setMaintenanceDetails] = useState(formData?.maintenanceDetails || [createAssetcommonforAll()]);
    const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });

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
    const [supportingDocumentFile, setSupportingDocumentFile] = useState(null);
    const [preConditionFile, setPreConditionFile] = useState(null);
    const [postConditionFile, setPostConditionFile] = useState(null);

    const [uploadError, setUploadError] = useState("");
    const [warrantyExp, setWarrantyExp] = useState({ i18nKey: "NA", code: "NA" });
    const [isLifeOfAssetAffected, setIsLifeOfAssetAffected] = useState({ i18nKey: "FALSE", code: "FALSE" });

    const [part, setPart] = React.useState({});
    const { control, formState: localFormState, watch, clearErrors: clearLocalErrors, setValue, trigger, register, getValues } = useForm();
    const formValue = watch();
    const { errors } = localFormState;

    const { id: applicationNo } = useParams();
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const { data: applicationDetails } = Digit.Hooks.asset.useAssetApplicationDetail(t, tenantId, applicationNo);

    const { data: maintainenceTypeMDMS } = Digit.Hooks.useEnabledMDMS(Digit.ULBService.getStateId(), "ASSET", [{ name: "AssetMaintenanceType" }], {
        select: (data) => {
            const formattedData = data?.["ASSET"]?.["AssetMaintenanceType"];
            const activeData = formattedData?.filter((item) => item.active === true);
            return activeData;
        },
    }); // Note : used direct custom MDMS to get the Data ,Do not copy and paste without understanding the Context

    let maintenanceOpt = [];

    maintainenceTypeMDMS &&
        maintainenceTypeMDMS.map((maintainenceType) => {
            maintenanceOpt.push({ i18nKey: `AST_${maintainenceType.code}`, code: `${maintainenceType.code}`, value: `${maintainenceType.name}` });
        });

    const { data: paymentTypeOptMDMS } = Digit.Hooks.useEnabledMDMS(Digit.ULBService.getStateId(), "ASSET", [{ name: "AssetPaymentType" }], {
        select: (data) => {
            const formattedData = data?.["ASSET"]?.["AssetPaymentType"];
            const activeData = formattedData?.filter((item) => item.active === true);
            return activeData;
        },
    });

    let paymentTypeOpt = [];

    paymentTypeOptMDMS &&
        paymentTypeOptMDMS.map((row) => {
            paymentTypeOpt.push({ i18nKey: `AST_${row.code}`, code: `${row.code}`, value: `${row.name}` });
        });

    const { data: maintenanceCycleOptMDMS } = Digit.Hooks.useEnabledMDMS(Digit.ULBService.getStateId(), "ASSET", [{ name: "AssetMaintenanceCycle" }], {
        select: (data) => {
            const formattedData = data?.["ASSET"]?.["AssetMaintenanceCycle"];
            const activeData = formattedData?.filter((item) => item.active === true);
            return activeData;
        },
    });

    let maintenanceCycleOpt = [];

    maintenanceCycleOptMDMS &&
        maintenanceCycleOptMDMS.map((rowType) => {
            maintenanceCycleOpt.push({ i18nKey: `AST_${rowType.code}`, code: `${rowType.code}`, value: `${rowType.name}` });
        });

    useEffect(() => {
        if (applicationDetails) {
            register("assetId");
            setValue("assetId", applicationDetails?.applicationData?.applicationData?.id);

        }
    }, [applicationDetails]);

    useEffect(() => {
        if (!_.isEqual(part, formValue)) {
            setPart({ ...formValue });
            setMaintenanceDetails((prev) => prev.map((o) => (o.key && o.key === maintenanceDetails.key ? { ...o, ...formValue/*, ..._ownerType*/ } : { ...o })));
            trigger();
        }
    }, [formValue]);

    useEffect(() => {
        if (Object.keys(errors).length && !_.isEqual(formState.errors[config.key]?.type || {}, errors))
            setError(config.key, { type: errors });
        else if (!Object.keys(errors).length && formState.errors[config.key]) clearErrors(config.key);
    }, [errors]);

    const errorStyle = { width: "70%", marginLeft: "30%", fontSize: "12px", marginTop: "-21px" };


    const handleSelect = (value) => {
       
        switch (value.code) {

            case 'IN_WARRANTY':
                register("isWarrantyExpired");
                setValue("isWarrantyExpired", true);
                register("isAMCExpired");
                setValue("isAMCExpired", false);
                break;
            case 'IN_AMC':
                register("isWarrantyExpired");
                setValue("isWarrantyExpired", false);
                register("isAMCExpired");
                setValue("isAMCExpired", true);
                break;
            case 'NA':
                register("isWarrantyExpired");
                setValue("isWarrantyExpired", false);
                register("isAMCExpired");
                setValue("isAMCExpired", false);
                break;
            default:
                break;
        }

        register("warrantyStatus");
        setValue("warrantyStatus", value);
        setWarrantyExp(value);
        return
    };

    // useEffect(() => {
    //     console.log("isWarrantyExpired:", getValues("isWarrantyExpired"));  this is use for testing purpose
    //     console.log("isAMCExpired:", getValues("isAMCExpired"));
    //   }, [warrantyExp]);

    // Common function to handle file upload
    const handleFileUpload = (e, setFileStoreId) => {
        const file = e.target.files[0];
        if (file.size >= 5242880) {

            setError("supportingDocument", { message: t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED") }); // Set error for supportingDocument
            setFileStoreId(null); // Clear previous successful upload
        } else {

            //setError("supportingDocument", { message: "" }); // Clear any previous errors
            Digit.UploadServices.Filestorage("ASSET", file, Digit.ULBService.getStateId())
                .then(response => {
                    if (response?.data?.files?.length > 0) {
                        setFileStoreId(response.data.files[0].fileStoreId);
                    } else {
                        setError("supportingDocument", { message: t("CS_FILE_UPLOAD_ERROR") });
                    }
                })
                .catch(() => setError("supportingDocument", { message: t("CS_FILE_UPLOAD_ERROR") }));
        }
    };

    useEffect(() => {
        register("supportingDocumentFile");
        setValue("supportingDocumentFile", supportingDocumentFile);

        register("preConditionFile");
        setValue("preConditionFile", preConditionFile);

        register("postConditionFile");
        setValue("postConditionFile", postConditionFile);


    }, [supportingDocumentFile, preConditionFile, postConditionFile]);

    const calculateNextDate = (date, cycle) => {

        const currentDate = new Date(date);
        switch (cycle.code) {
            case 'MONTHLY':
                currentDate.setMonth(currentDate.getMonth() + 1)
                    ;
                break;
            case 'QUARTERLY':
                currentDate.setMonth(currentDate.getMonth() + 3);
                break;
            case 'HALF_YEARLY':
                currentDate.setMonth(currentDate.getMonth() + 6);
                break;
            case 'YEARLY':
                currentDate.setFullYear(currentDate.getFullYear() + 1);
                break;
            default:
                break;
        }
        return currentDate.toISOString().split('T')[0]; // Format as YYYY-MM-DD
    };

    const handleSelectLifeAffected = (value) => {
        if (value) {
            register("isLifeOfAssetAffected");
            setValue("isLifeOfAssetAffected", value);
            setIsLifeOfAssetAffected(value)
        }
    }

    const maintenanceIncreasedHandle = [
        { i18nKey: "1 Year", code: "1" },
        { i18nKey: "2 Year", code: "2" },
        { i18nKey: "3 Year", code: "3" },
        { i18nKey: "4 Year", code: "4" },
        { i18nKey: "5 Year", code: "5" }
    ];
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

                    {/* <StatusTable>
                        <Row
                            label={t("AST_ID")}
                            text={`${t(checkForNA(applicationDetails?.applicationData?.applicationData?.id))}`}
                        />
                    </StatusTable> */}
                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_ID")}</CardLabel>
                        <div className="field">
                            <Controller
                                control={control}
                                name={"applicationNo"}
                                defaultValue={maintenanceDetails?.applicationNo}

                                render={(props) => (
                                    <TextInput
                                        value={`${t(checkForNA(applicationDetails?.applicationData?.applicationData?.id))}`}
                                        readOnly // Makes the input field readonly
                                        style={{
                                            border: "none",  // Removes the border
                                            backgroundColor: "transparent",  // Optional: makes the background transparent
                                        }}
                                    />
                                )}
                            />
                        </div>
                    </LabelFieldPair>
                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_APPLICATION_NO")}</CardLabel>
                        <div className="field">
                            <Controller
                                control={control}
                                name={"applicationNo"}
                                defaultValue={maintenanceDetails?.applicationNo}

                                render={(props) => (
                                    <TextInput
                                        value={`${t(checkForNA(applicationDetails?.applicationData?.applicationData?.applicationNo))}`}
                                        readOnly // Makes the input field readonly
                                        style={{
                                            border: "none",  // Removes the border
                                            backgroundColor: "transparent",  // Optional: makes the background transparent
                                        }}
                                    />
                                )}
                            />
                        </div>
                    </LabelFieldPair>
                    {/* <StatusTable>
                        <Row
                            label={t("AST_APPLICATION_NO")}
                            text={`${t(checkForNA(applicationDetails?.applicationData?.applicationData?.applicationNo))}`}
                        />
                    </StatusTable> */}
                    {/* <StatusTable>
                        <Row
                            label={t("AST_NAME")}
                            text={`${t(checkForNA(applicationDetails?.applicationData?.applicationData?.assetName))}`}
                        />
                    </StatusTable> */}
                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_NAME")}</CardLabel>
                        <div className="field">
                            <Controller
                                control={control}
                                name={"applicationNo"}
                                defaultValue={maintenanceDetails?.applicationNo}

                                render={(props) => (
                                    <TextInput
                                        value={`${t(checkForNA(applicationDetails?.applicationData?.applicationData?.assetName))}`}
                                        readOnly // Makes the input field readonly
                                        style={{
                                            border: "none",  // Removes the border
                                            backgroundColor: "transparent",  // Optional: makes the background transparent
                                        }}
                                    />
                                )}
                            />
                        </div>
                    </LabelFieldPair>
                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_PARENT_CATEGORY")}</CardLabel>
                        <div className="field">
                            <Controller
                                control={control}
                                name={"applicationNo"}
                                defaultValue={maintenanceDetails?.applicationNo}

                                render={(props) => (
                                    <TextInput
                                        value={`${t(checkForNA(applicationDetails?.applicationData?.applicationData?.assetParentCategory))}`}
                                        readOnly // Makes the input field readonly
                                        style={{
                                            border: "none",  // Removes the border
                                            backgroundColor: "transparent",  // Optional: makes the background transparent
                                        }}
                                    />
                                )}
                            />
                        </div>
                    </LabelFieldPair>
                    {/* <StatusTable>
                        <Row
                            label={t("AST_LIFE")}
                            text={`${t(checkForNA(applicationDetails?.applicationData?.applicationData?.lifeOfAsset))}`}
                        />
                    </StatusTable> */}
                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_LIFE")}</CardLabel>
                        <div className="field">
                            <Controller
                                control={control}
                                name={"applicationNo"}
                                defaultValue={maintenanceDetails?.applicationNo}

                                render={(props) => (
                                    <TextInput
                                        value={`${t(checkForNA(applicationDetails?.applicationData?.applicationData?.lifeOfAsset))}`}
                                        readOnly // Makes the input field readonly
                                        style={{
                                            border: "none",  // Removes the border
                                            backgroundColor: "transparent",  // Optional: makes the background transparent
                                        }}
                                    />
                                )}
                            />
                        </div>
                    </LabelFieldPair>
                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller" style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                            {t("AST_MAINTENANCE_OPTIONS")} <span style={{ color: "red" }}>*</span>
                            <Tooltip className="tooltip-wrapper" message={t("TOOLTIP_AST_MAINTENANCE_OPTIONS")} style={{ maxWidth: "300px" }}>
                                <InfoBannerIcon style={{ verticalAlign: "middle", cursor: "pointer", fontSize: "16px" }} />
                            </Tooltip>
                        </CardLabel>
                        <div className="field">
                            <RadioButtons
                                t={t}
                                options={[{ i18nKey: "IN_WARRANTY", code: "IN_WARRANTY" }, { i18nKey: "IN_AMC", code: "IN_AMC" }, { i18nKey: "NA", code: "NA" }]}
                                optionsKey="code"
                                name="warrantyStatus"
                                value={warrantyExp}
                                selectedOption={warrantyExp}
                                innerStyles={{ display: "inline-block", marginLeft: "20px", paddingBottom: "2px", marginBottom: "2px" }}
                                onSelect={handleSelect}
                                isDependent={true}
                            />
                        </div>
                    </LabelFieldPair>
                    {/* if select option in warranty then show */}
                    {warrantyExp.code === "IN_WARRANTY" &&
                        (
                            <div>
                                {/* <LabelFieldPair>
                                    <CardLabel className="card-label-smaller">{t("AST_WARRANTY_DESCRIPTION")}</CardLabel>
                                    <div className="field">
                                        <Controller
                                            control={control}
                                            name={"assetWarrantyDescription"}
                                            defaultValue={maintenanceDetails?.assetWarrantyDescription}
                                            rules={{
                                                required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                                                validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                                            }}
                                            render={(props) => (
                                                <TextInput
                                                    value={props.value}
                                                    autoFocus={focusIndex.index === maintenanceDetails?.key && focusIndex.type === "assetWarrantyDescription"}
                                                    onChange={(e) => {
                                                        props.onChange(e.target.value);
                                                        setFocusIndex({ index: maintenanceDetails.key, type: "assetWarrantyDescription" });
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
                                <CardLabelError style={errorStyle}>{localFormState.touched.assetWarrantyDescription ? errors?.assetWarrantyDescription?.message : ""}</CardLabelError> */}

                                <LabelFieldPair>
                                    <CardLabel className="card-label-smaller">{t("AST_WARRANTY_DESCRIPTION")} <span style={{ color: "red" }}>*</span></CardLabel>
                                    <div className="field">
                                        <Controller
                                            control={control}
                                            name={"assetWarrantyDescription"}
                                            defaultValue={maintenanceDetails?.assetWarrantyDescription}
                                            rules={{
                                                required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                                                validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                                            }}
                                            render={(props) => (
                                                <TextArea
                                                    type={"textarea"}
                                                    value={props.value}
                                                    autoFocus={focusIndex.index === maintenanceDetails?.key && focusIndex.type === "assetWarrantyDescription"}
                                                    onChange={(e) => {
                                                        props.onChange(e.target.value);
                                                        setFocusIndex({ index: maintenanceDetails.key, type: "assetWarrantyDescription" });
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
                                <CardLabelError style={errorStyle}>{localFormState.touched.assetWarrantyDescription ? errors?.assignedUser?.assetWarrantyDescription : ""}</CardLabelError>
                            </div>
                        )}
                    {warrantyExp.code === "IN_AMC" &&
                        (
                            <div>
                                {/* <LabelFieldPair>
                                    <CardLabel className="card-label-smaller">{t("AST_AMC_DETAILS")}</CardLabel>
                                    <div className="field">
                                        <Controller
                                            control={control}
                                            name={"amcDetails"}
                                            defaultValue={maintenanceDetails?.amcDetails}
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
                                                    autoFocus={focusIndex.index === maintenanceDetails?.key && focusIndex.type === "amcDetails"}
                                                    onChange={(e) => {
                                                        props.onChange(e.target.value);
                                                        setFocusIndex({ index: maintenanceDetails.key, type: "amcDetails" });
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
                                <CardLabelError style={errorStyle}>{localFormState.touched.amcDetails ? errors?.amcDetails?.message : ""}</CardLabelError> */}

                                <LabelFieldPair>
                                    <CardLabel className="card-label-smaller">{t("AST_AMC_DETAILS")} <span style={{ color: "red" }}>*</span> </CardLabel>
                                    <div className="field">
                                        <Controller
                                            control={control}
                                            name={"amcDetails"}
                                            defaultValue={maintenanceDetails?.amcDetails}
                                            rules={{
                                                required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                                                validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                                            }}
                                            render={(props) => (
                                                <TextArea
                                                    type={"textarea"}
                                                    value={props.value}
                                                    autoFocus={focusIndex.index === maintenanceDetails?.key && focusIndex.type === "amcDetails"}
                                                    onChange={(e) => {
                                                        props.onChange(e.target.value);
                                                        setFocusIndex({ index: maintenanceDetails.key, type: "amcDetails" });
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
                                <CardLabelError style={errorStyle}>{localFormState.touched.amcDetails ? errors?.assignedUser?.amcDetails : ""}</CardLabelError>

                            </div>
                        )}

                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller" style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                            {t("AST_MAINTENANCE_TYPE")} <span style={{ color: "red" }}>*</span>
                            {/* <Tooltip message={t("TOOLTIP_AST_MAINTENANCE_TYPE")}>
                                <InfoBannerIcon style={{ verticalAlign: "middle", cursor: "pointer" }} />
                            </Tooltip> */}
                        </CardLabel>
                        <Controller
                            control={control}
                            name={"maintenanceType"}
                            defaultValue={maintenanceDetails?.maintenanceType}
                            render={(props) => (
                                <Dropdown
                                    className="form-field"
                                    selected={props.value}
                                    select={props.onChange}
                                    onBlur={props.onBlur}
                                    option={maintenanceOpt}
                                    optionKey="i18nKey"
                                    t={t}
                                />
                            )}
                        />
                    </LabelFieldPair>
                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">
                            {t("AST_MAINTENANCE_DATE")} <span style={{ color: "red" }}>*</span>
                        </CardLabel>
                        <div className="field">
                            <Controller
                                control={control}
                                name={"assetMaintenanceDate"}
                                defaultValue={maintenanceDetails?.assetMaintenanceDate}
                                rules={{
                                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                                    validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                                    // validate: (val) => {
                                    //   const selectedDate = new Date(val);
                                    //   const today = new Date();
                                    //   return selectedDate >= today ? true : t("ERR_DATE_MUST_BE_TODAY_OR_FUTURE");
                                    // },
                                }}
                                render={(props) => (
                                    <TextInput
                                        type="date"
                                        value={props.value}
                                        onChange={(e) => {
                                            props.onChange(e.target.value);
                                        }}
                                    // Remove the max attribute to allow future dates
                                    />
                                )}
                            />
                        </div>
                    </LabelFieldPair>
                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller" style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                            {t("AST_MAINTENANCE_CYCLE")} <span style={{ color: "red" }}>*</span>
                            <Tooltip message={t("TOOLTIP_AST_MAINTENANCE_CYCLE")}>
                                <InfoBannerIcon style={{ verticalAlign: "middle", cursor: "pointer" }} />
                            </Tooltip>
                        </CardLabel>

                        <Controller
                            control={control}
                            name={"maintenanceCycle"}
                            defaultValue={maintenanceDetails?.maintenanceCycle}
                            render={(props) => (
                                <Dropdown
                                    className="form-field"
                                    selected={props.value}
                                    select={(value) => {
                                        props.onChange(value);
                                        const date = control.getValues("assetMaintenanceDate");
                                        if (date) {
                                            const nextDate = calculateNextDate(date, value);
                                            control.setValue("assetNextMaintenanceDate", nextDate);
                                        }
                                    }}
                                    onBlur={props.onBlur}
                                    option={maintenanceCycleOpt}
                                    optionKey="i18nKey"
                                    t={t}
                                />
                            )}
                        />
                    </LabelFieldPair>

                    <CardLabelError style={errorStyle}>{localFormState.touched.maintenanceCycle ? errors?.maintenanceCycle?.message : ""}</CardLabelError>
                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller" style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                                {t("AST_NEXT_MAINTENANCE_DATE")} <span style={{ color: "red" }}>*</span>
                            <Tooltip message={t("TOOLTIP_AST_NEXT_MAINTENANCE_DATE")}>
                               <InfoBannerIcon style={{ verticalAlign: "middle", cursor: "pointer" }} />
                            </Tooltip>
                        </CardLabel>
                        <div className="field">
                            <Controller
                                control={control}
                                name={"assetNextMaintenanceDate"}
                                defaultValue={maintenanceDetails?.assetNextMaintenanceDate}
                                rules={{
                                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                                    // validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                                    validate: (val) => {
                                        const selectedDate = new Date(val);
                                        const today = new Date();
                                        return selectedDate >= today ? true : t("ERR_DATE_MUST_BE_TODAY_OR_FUTURE");
                                    },
                                }}
                                render={(props) => (
                                    <TextInput
                                        type="date"
                                        value={props.value}
                                        onChange={(e) => {
                                            props.onChange(e.target.value);
                                        }} s
                                    // Remove the max attribute to allow future dates
                                    />
                                )}
                            />
                        </div>
                    </LabelFieldPair>
                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller" style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                            {t("AST_PAYMENT_TYPE")} <span style={{ color: "red" }}>*</span>
                            <Tooltip message={t("TOOLTIP_AST_PAYMENT_TYPE")}>
                                <InfoBannerIcon style={{ verticalAlign: "middle", cursor: "pointer" }} />
                            </Tooltip>
                        </CardLabel>
                        <Controller
                            control={control}
                            name={"paymentType"}
                            defaultValue={maintenanceDetails?.paymentType}

                            render={(props) => (
                                <Dropdown
                                    className="form-field"
                                    selected={props.value}
                                    select={props.onChange}
                                    onBlur={props.onBlur}
                                    option={paymentTypeOpt}
                                    optionKey="i18nKey"
                                    t={t}
                                />
                            )}
                        />
                    </LabelFieldPair>
                    <CardLabelError style={errorStyle}>{localFormState.touched.paymentType ? errors?.paymentType?.message : ""}</CardLabelError>

                    {/* <LabelFieldPair>
                        <CardLabel className="card-label-smaller" style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                            {t("AST_COST_MAINTENANCE_OVERHEAD")}
                            <Tooltip message={t("TOOLTIP_AST_COST_MAINTENANCE_OVERHEAD")}>
                                <InfoBannerIcon style={{ verticalAlign: "middle", cursor: "pointer" }} />
                            </Tooltip>
                        </CardLabel>
                        <div className="field">
                            <Controller
                                control={control}
                                name={"costOfMaintenance"}
                                defaultValue={maintenanceDetails?.costOfMaintenance}
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
                                        autoFocus={focusIndex.index === maintenanceDetails?.key && focusIndex.type === "costOfMaintenance"}
                                        onChange={(e) => {
                                            props.onChange(e.target.value);
                                            setFocusIndex({ index: maintenanceDetails.key, type: "costOfMaintenance" });
                                        }}
                                        onBlur={(e) => {
                                            setFocusIndex({ index: -1 });
                                            props.onBlur(e);
                                        }}
                                    />
                                )}
                            />
                        </div>
                    </LabelFieldPair> */}
                    <CardLabelError style={errorStyle}>{localFormState.touched.amcDetails ? errors?.amcDetails?.message : ""}</CardLabelError>

                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_MAINTENANCE_DESCRIPTION")} <span style={{ color: "red" }}>*</span> </CardLabel>
                        <div className="field">
                            <Controller
                                control={control}
                                name={"description"}
                                defaultValue={maintenanceDetails?.description}
                                rules={{
                                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                                }}
                                render={(props) => (
                                    <TextArea
                                        type={"textarea"}
                                        value={props.value}
                                        autoFocus={focusIndex.index === maintenanceDetails?.key && focusIndex.type === "description"}
                                        onChange={(e) => {
                                            props.onChange(e.target.value);
                                            setFocusIndex({ index: maintenanceDetails.key, type: "description" });
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
                    <CardLabelError style={errorStyle}>{localFormState.touched.description ? errors?.assignedUser?.description : ""}</CardLabelError>
                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_VENDOR")} <span style={{ color: "red" }}>*</span> </CardLabel>
                        <div className="field">
                            <Controller
                                control={control}
                                name={"vendor"}
                                defaultValue={maintenanceDetails?.vendor}
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
                                        autoFocus={focusIndex.index === maintenanceDetails?.key && focusIndex.type === "vendor"}
                                        onChange={(e) => {
                                            props.onChange(e.target.value);
                                            setFocusIndex({ index: maintenanceDetails.key, type: "vendor" });
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
                    <CardLabelError style={errorStyle}>{localFormState.touched.vendor ? errors?.vendor?.message : ""}</CardLabelError>

                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_PARTS_TO_BE_ADDED")} <span style={{ color: "red" }}>*</span></CardLabel>
                        <div className="field">
                            <Controller
                                control={control}
                                name={"partsAddedOrReplaced"}
                                defaultValue={maintenanceDetails?.partsAddedOrReplaced}
                                rules={{
                                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                                }}
                                render={(props) => (
                                    <TextArea
                                        type={"textarea"}
                                        value={props.value}
                                        autoFocus={focusIndex.index === maintenanceDetails?.key && focusIndex.type === "partsAddedOrReplaced"}
                                        onChange={(e) => {
                                            props.onChange(e.target.value);
                                            setFocusIndex({ index: maintenanceDetails.key, type: "partsAddedOrReplaced" });
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
                    <CardLabelError style={errorStyle}>{localFormState.touched.partsAddedOrReplaced ? errors?.assignedUser?.partsAddedOrReplaced : ""}</CardLabelError>

                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_SUPPORTING_DOCUMENTS")} <span style={{ color: "red" }}>*</span> </CardLabel>
                        <div className="field">
                            <Controller
                                control={control}
                                name={"supportingDocument"}
                                render={(props) => (
                                    <UploadFile
                                        id={"supportingDocument"}
                                        onUpload={(e) => handleFileUpload(e, setSupportingDocumentFile)}
                                        onDelete={() => {
                                            setSupportingDocumentFile(null);
                                            props.onChange(null);
                                        }}
                                        message={supportingDocumentFile ? `1 ${t(`CS_ACTION_FILEUPLOADED`)}` : t(`CS_ACTION_NO_FILEUPLOADED`)}
                                        accept="image/*, .pdf, .png, .jpeg, .jpg"
                                        buttonType="button"
                                        error={uploadError || !supportingDocumentFile} // Show error if uploadError is not empty or no file
                                    />
                                )}
                            />
                        </div>
                    </LabelFieldPair>

                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_PRE_CONDITION_DOC")} <span style={{ color: "red" }}>*</span> </CardLabel>
                        <div className="field" style={{ marginTop: "15px" }}>
                            <Controller
                                control={control}
                                name={"preCondition"}
                                render={(props) => (
                                    <UploadFile
                                        id={"preCondition"}
                                        onUpload={(file) => handleFileUpload(file, setPreConditionFile)}
                                        onDelete={() => {
                                            setPreConditionFile(null);
                                            props.onChange(null);
                                        }}
                                        message={preConditionFile ? `1 ${t(`CS_ACTION_FILEUPLOADED`)}` : t(`CS_ACTION_NO_FILEUPLOADED`)}
                                        accept="image/*, .pdf, .png, .jpeg, .jpg"
                                        buttonType="button"
                                        error={uploadError || !preConditionFile} // Show error if uploadError is not empty or no file
                                    />
                                )}
                            />
                        </div>
                    </LabelFieldPair>
                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_PRE_CONDITION_DESCRIPTION")} <span style={{ color: "red" }}>*</span> </CardLabel>
                        <div className="field">
                            <Controller
                                control={control}
                                name={"preConditionRemarks"}
                                defaultValue={maintenanceDetails?.postConditionRemarks}
                                rules={{
                                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                                    validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                                }}
                                render={(props) => (
                                    <TextArea
                                        type={"textarea"}
                                        value={props.value}
                                        autoFocus={focusIndex.index === maintenanceDetails?.key && focusIndex.type === "preConditionRemarks"}
                                        onChange={(e) => {
                                            props.onChange(e.target.value);
                                            setFocusIndex({ index: maintenanceDetails.key, type: "preConditionRemarks" });
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
                    <CardLabelError style={errorStyle}>{localFormState.touched.preConditionRemarks ? errors?.assignedUser?.preConditionRemarks : ""}</CardLabelError>
                    <div style={{ backgroundColor: "#f1f3f4", borderRadius: "8px", padding: "20px" }}>


                        <LabelFieldPair>
                            <CardLabel className="card-label-smaller">{t("AST_POST_CONDITION_DOC")} <span style={{ color: "red" }}>*</span></CardLabel>
                            <div className="field">
                                <Controller
                                    control={control}
                                    name={"postCondition"}
                                    render={(props) => (
                                        <UploadFile
                                            id={"postCondition"}
                                            onUpload={(file) => handleFileUpload(file, setPostConditionFile)}
                                            onDelete={() => {
                                                setPostConditionFile(null);
                                                props.onChange(null);
                                            }}
                                            message={postConditionFile ? `1 ${t(`CS_ACTION_FILEUPLOADED`)}` : t(`CS_ACTION_NO_FILEUPLOADED`)}
                                            accept="image/*, .pdf, .png, .jpeg, .jpg"
                                            buttonType="button"
                                            error={uploadError || !postConditionFile} // Show error if uploadError is not empty or no file
                                        />
                                    )}
                                />
                            </div>
                        </LabelFieldPair>
                        <LabelFieldPair>
                            <CardLabel className="card-label-smaller">{t("AST_POST_CONDITION_DESCRIPTION")} <span style={{ color: "red" }}>*</span> </CardLabel>
                            <div className="field">
                                <Controller
                                    control={control}
                                    name={"postConditionRemarks"}
                                    defaultValue={maintenanceDetails?.postConditionRemarks}
                                    rules={{
                                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                                        validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                                    }}
                                    render={(props) => (
                                        <TextArea
                                            type={"textarea"}
                                            value={props.value}
                                            autoFocus={focusIndex.index === maintenanceDetails?.key && focusIndex.type === "postConditionRemarks"}
                                            onChange={(e) => {
                                                props.onChange(e.target.value);
                                                setFocusIndex({ index: maintenanceDetails.key, type: "postConditionRemarks" });
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
                        <CardLabelError style={errorStyle}>{localFormState.touched.postConditionRemarks ? errors?.assignedUser?.postConditionRemarks : ""}</CardLabelError>
                        <LabelFieldPair>
                            <CardLabel className="card-label-smaller" style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                                {t("AST_IS_LIFE_OF__ASSET_AFFECTED")} <span style={{ color: "red" }}>*</span>
                                {/* <Tooltip message={t("TOOLTIP_AST_IS_LIFE_OF__ASSET_AFFECTED")}>
                                <InfoBannerIcon style={{ verticalAlign: "middle", cursor: "pointer" }} />
                            </Tooltip> */}
                            </CardLabel>
                            <div className="field">
                                <RadioButtons
                                    t={t}
                                    options={[{ i18nKey: true, code: "TRUE" }, { i18nKey: false, code: "FALSE" }]}
                                    optionsKey="code"
                                    name="isLifeOfAssetAffected"
                                    value={isLifeOfAssetAffected}
                                    selectedOption={isLifeOfAssetAffected}
                                    innerStyles={{ display: "inline-block", marginLeft: "20px", paddingBottom: "2px", marginBottom: "2px" }}
                                    onSelect={handleSelectLifeAffected}
                                    isDependent={true}
                                />
                            </div>
                        </LabelFieldPair>

                        {isLifeOfAssetAffected.code === "TRUE" &&
                            (
                                <div>
                                    <LabelFieldPair>
                                        <CardLabel className="card-label-smaller">{t("AST_MAINTENANCE_INCREASED_NO_OF_YEAR")} <span style={{ color: "red" }}>*</span></CardLabel>
                                        <Controller
                                            control={control}
                                            name={"assetMaintenanceIncreasedYear"}
                                            defaultValue={maintenanceDetails?.assetMaintenanceIncreasedYear || null}
                                            render={(props) => (
                                                <Dropdown
                                                    className="form-field"
                                                    selected={props.value}
                                                    select={props.onChange}
                                                    onBlur={props.onBlur}
                                                    option={maintenanceIncreasedHandle}
                                                    optionKey="i18nKey"
                                                    t={t}
                                                />
                                            )}
                                        />
                                    </LabelFieldPair>
                                    <CardLabelError style={errorStyle}>{localFormState.touched.assetMaintenanceIncreasedYear ? errors?.assetMaintenanceIncreasedYear?.message : ""}</CardLabelError>
                                </div>
                            )}
                    </div>
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