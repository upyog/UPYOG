import React, { useEffect, useState, useRef } from "react";
import { useTranslation } from "react-i18next";
import ReCAPTCHA from "react-google-recaptcha";
import { useForm, Controller } from "react-hook-form";
import {
    TextInput,
    SubmitBar,
    SearchForm,
    Dropdown,
    SearchField,
    Table,
    Header,
    Toast,
    Loader, 
} from "@upyog/digit-ui-react-components";

/**
 * Description: VSearchCertificate component renders a form having certificate type, certificate number and a captcha for verification and display's a table as a result
 * @author: Khalid Rashid

 * @date: 2021-04-15
 * Recaptcha library used for captcha
 * @github: https://github.com/google-reCAPTCHA/recaptcha-v2
 * npm command: npm install react-google-recaptcha --save
*/

const VSearchCertificate = () => {
    const { t } = useTranslation();
    const tenantId = Digit.ULBService.getCitizenCurrentTenant(true);
    const user = Digit.UserService.getUser();
    const [showToast, setShowToast] = useState(null);

    const isMobile = window.Digit.Utils.browser.isMobile();
    const todaydate = new Date();
    const today = todaydate.toISOString().split("T")[0];

    const [token, setToken] = useState("");
    const [ishuman, setIshuman] = useState(false);
    const [istable, setistable] = useState(false);
    const [certificate_name, setCertificate_name] = useState("");
    const [certificate_No, setCertificate_No] = useState("");
    const [selectedCity, setSelectedCity] = useState("");

    // function to reset captcha
    const resetCaptcha = () => {
        grecaptcha.reset()
    };

    function setcertificate_No(e) {
        setCertificate_No(e.target.value);
    }

    function handleCaptchaChange(value) {
        setToken(value);
    }

    // Initialized form handling using the useForm hook from React Hook Form, setting default values for pagination, sorting, and date range filters.
    const { register, control, handleSubmit, setValue, getValues, reset, formState } = useForm({
        defaultValues: {
            offset: 0,
            limit: !isMobile && 10,
            sortBy: "commencementDate",
            sortOrder: "DESC",
        },
    });

    // Hook to fetch the dropdown data
    const { data: type_of_certificate } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "VerificationSearch", [{ name: "CertificateType" }],
        {
            select: (data) => {
                const formattedData = data?.["VerificationSearch"]?.["CertificateType"].map((details) => {
                    return { i18nKey: `${details.name}`, code: `${details.code}`, active: `${details.active}` };
                  });
                return formattedData;
            },
        });

    // Hook to fetch city data
    const { data: cityData } = Digit.Hooks.useCustomMDMS(
        Digit.ULBService.getStateId(),
        "tenant",
        [{ name: "tenants" }],
        {
            select: (data) => {
                return data?.tenant?.tenants?.map((city) => ({
                    i18nKey: city.name,
                    code: city.code,
                    active: true
                }));
            },
        }
    );
    // sets ishuman to be true based on token
    useEffect(() => {
        if (token) {
            setIshuman(true);
        }
    }, [token]);

    useEffect(() => {
        register("offset", 0);
        register("limit", 10);
        register("sortBy", "commencementDate");
        register("sortOrder", "DESC");
    }, [register, setValue, today]);


    let applicationData;

    /**
     * Asynchronously fetches module data based on the provided certificate name and number.
     * If both certificate_name and certificate_No are provided, it retrieves the application details
     * from the CMServices. If the application exists, it updates the state with the relevant details,
     * including name, mobileNumber, certificate number, issue date, validity date, and status.
     * If the application does not exist, it displays a warning toast notification.
     */
    const mutation = Digit.Hooks.cm.useCMSearch();
    const ModuleData = async () => {
        if (certificate_name && certificate_No) {
            const formdata = {
                ModuleSearchCriteria: {
                    tenantId:selectedCity?.code || tenantId,
                    applicationNumber: certificate_No,
                    moduleName: certificate_name.code
                }
            };
            
            mutation.mutate(formdata, {
                onSuccess: (Details) => {
                    const applicationData = Details?.CommonDetail;
            if (applicationData?.applicationNumber) {
                 setUpdatedData([{
                        name: applicationData?.name || "NA",
                        mobileNumber: applicationData?.mobileNumber || "NA",
                        certificateNumber: applicationData?.applicationNumber || "NA",
                        issueDate: applicationData?.fromDate || "NA",
                        validUpto: applicationData?.toDate || "NA",
                        certificateStatus: applicationData?.status || "NA",
                        }]);
                        setistable(true);
                    } else {
                        setShowToast({ label: t("VS_APPLICATION_DOESNOT_EXIST"), warning: true });
                    }
                },
                onError: (error) => {
                    setShowToast({ label: t("VS_APPLICATION_DOESNOT_EXIST"), warning: true });
                }
            });
        }
    }

    // columns defined to be passed in applicationtable
    const columns = [
        { Header: t("CITIZEN_NAME"), accessor: "name" },
        { Header: t("CITIZEN_MOBILE_NO"), accessor: "mobileNumber" },
        { Header: t("CERTIFICATE_NUMBER"), accessor: "certificateNumber" },
        { Header: t("ISSUE_DATE"), accessor: "issueDate" },
        { Header: t("VALID_UPTO"), accessor: "validUpto" },
        { Header: t("CERTIFICATE_STATUS"), accessor: "certificateStatus" },
    ];

    // defined a state to handle data object
    const [updatedData, setUpdatedData] = useState([
        {
            name: "",
            mobileNumber: "",
            certificateNumber: "",
            issueDate: "",
            validUpto: "",
            certificateStatus: "",
        },
    ]);

    function onSubmit() {
        ModuleData();
        resetCaptcha();
    }

    return (
        <React.Fragment>
            <div style={{ width: "100%", height: "200px", marginLeft: "2%", marginRight: "2%", marginTop: "20px", marginBottom: "20px" }}>
                <div className="h1" style={{ fontSize: "40px", fontFamily: "Roboto Condensed", color: "#582766" }}>{t("SEARCH_CERTIFICATE")}</div>
                <SearchForm onSubmit={onSubmit} handleSubmit={handleSubmit} className="verification-search-form">
                    <SearchField>
                        <label className="astericColor" style={{ fontSize: "19px" }} >{t("CERTIFICATE_TYPE")}</label>
                        <Controller
                            control={control}
                            name="certificateType"
                            render={(props) => (
                                <Dropdown
                                    selected={certificate_name}
                                    select={setCertificate_name}
                                    onBlur={props.onBlur}
                                    option={type_of_certificate}
                                    optionKey="i18nKey"
                                    optionCardStyles={{ overflowY: "auto", maxHeight: "300px" }}
                                    className="verificationDropdown"
                                    t={t}
                                    disable={false}
                                    placeholder={"Please type and select the certificate type"}
                                />
                            )}
                        />
                    </SearchField>
                    {!user?.info && (
                        <SearchField>
                            <label className="astericColor" style={{ fontSize: "19px" }}>
                                {t("CITY")}
                            </label>
                            <Controller
                                control={control}
                                name="city"
                                render={(props) => (
                                    <Dropdown
                                        selected={selectedCity}
                                        select={setSelectedCity}
                                        onBlur={props.onBlur}
                                        option={cityData}
                                        optionKey="i18nKey"
                                        optionCardStyles={{ overflowY: "auto", maxHeight: "300px" }}

                                        className="verificationDropdown"

                                        t={t}
                                        disable={false}
                                        placeholder={"Please select city"}
                                    />
                                )}
                            />
                        </SearchField>
                    )}
                    <SearchField>
                        <label className="astericColor" style={{ fontSize: "19px" }}>{t("CERTIFICATE_NUMBER")}</label>
                        <TextInput
                            name="certificateNo"
                            t={t}
                            type={"text"}
                            optionKey="i18nKey"
                            placeholder={"Please enter unique certificate number"}
                            value={certificate_No}
                            onChange={setcertificate_No}
                            style={{ width: "100%" }}
                            className = "verificationInput"
                        />
                    </SearchField>
                    <SearchField>
                        <ReCAPTCHA
                            // sitekey="6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI" // testing sitekey (can be used if you don't have a real sitekey)
                            sitekey="6LfoM5MqAAAAAAQkwtM6X-IGwdURyY50WwvcaA_g"
                            onChange={handleCaptchaChange}
                        />
                    </SearchField>

                    <SearchField className="submit">
                        <SubmitBar
                            label={t("ES_COMMON_SEARCH")}
                            submit
                            disabled={!ishuman || !certificate_name || !certificate_No || (!user?.info && !selectedCity)}
                        />
                        <p
                            style={{ marginTop: "10px" }}
                            onClick={() => {
                                reset({
                                    applicationNo: "",
                                    fromDate: today,
                                    toDate: today,
                                    status: "",
                                    offset: 0,
                                    limit: 10,
                                    sortBy: "commencementDate",
                                    sortOrder: "DESC",
                                });
                                setShowToast(null);
                                setistable(false);
                                setIshuman(false);
                                setCertificate_name("");
                                setCertificate_No("");
                                setSelectedCity("");
                                resetCaptcha();
                            }}
                        >
                            {t(`ES_COMMON_CLEAR_ALL`)}
                        </p>
                    </SearchField>
                </SearchForm>
                {/* {applicationData !== "" && isLoading && istable && <Loader />} */}
                {istable && (
                    <Table
                        t={t}
                        data={updatedData}
                        columns={columns}
                        getCellProps={(cellInfo) => {
                            return {
                                style: {
                                    minWidth: cellInfo.column.Header === t("CM_INBOX_APPLICATION_NO") ? "240px" : "",
                                    padding: "20px 18px",
                                    fontSize: "16px",
                                },
                            };
                        }}
                        isPaginationRequired={false}
                    />
                )}

                {showToast && (
                    <Toast
                        error={showToast.error}
                        warning={showToast.warning}
                        label={t(showToast.label)}
                        isDleteBtn={true}
                        onClose={() => {
                            setShowToast(null);
                        }}
                    />
                )}
            </div>
        </React.Fragment>
    );
};

export default VSearchCertificate;