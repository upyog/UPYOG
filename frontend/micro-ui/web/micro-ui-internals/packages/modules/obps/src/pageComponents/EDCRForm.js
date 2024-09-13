import { CardLabel, Dropdown, FormStep, Loader, TextInput, Toast, UploadFile } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useLocation, useHistory } from "react-router-dom";
import { getPattern, stringReplaceAll, sortDropdownNames  } from "../utils";

const EDCRForm = ({ t, config, onSelect, userType, formData, ownerIndex = 0, addNewOwner, isShowToast, isSubmitBtnDisable, setIsShowToast }) => {
    const { pathname: url } = useLocation();
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const stateId = Digit.ULBService.getStateId();
    const [citymoduleList, setCitymoduleList] = useState([]);
    const [name, setName] = useState(formData?.Scrutiny?.[0]?.applicantName);
    const [tenantIdData, setTenantIdData] = useState(formData?.Scrutiny?.[0]?.tenantIdData);
    const [uploadedFile, setUploadedFile] = useState(() => formData?.Scrutiny?.[0]?.proofIdentity?.fileStoreId || null);
    const [file, setFile] = useState(formData?.owners?.documents?.proofIdentity);
    const [error, setError] = useState(null);
    const [uploadMessage, setUploadMessage] = useState("");
    const [showToast, setShowToast] = useState(null);
    const history = useHistory();
    const [coreArea, setcoreArea] = useState(formData?.Scrutiny?.[0]?.coreArea);

    let validation = { };


    function setApplicantName(e) {
        setName(e.target.value);
    }
    function setCoreArea(value) {
        setcoreArea(value);
    }

    function setTypeOfTenantID(value) {
        setTenantIdData(value);
    }

    function selectfile(e) {
        setUploadedFile(e.target.files[0]);
        setFile(e.target.files[0]);
    }

    const onSkip = () => {
        setUploadMessage("NEED TO DELETE");
    };

    const common = [
        {
          code: "YES",
          i18nKey: "YES"
        },
        {
          code: "NO",
          i18nKey: "NO"
        }
      ]

    const { isLoading, data: citymodules } = Digit.Hooks.obps.useMDMS(stateId, "tenant", ["citymodule"]);

    useEffect(() => {
        if (citymodules?.tenant?.citymodule?.length > 0) {
            const list = citymodules?.tenant?.citymodule?.filter(data => data.code == "BPAAPPLY");
            list?.[0]?.tenants?.forEach(data => {
                data.i18nKey = `TENANT_TENANTS_${stringReplaceAll(data?.code?.toUpperCase(), ".", "_")}`;
            })
            if (Array.isArray(list?.[0]?.tenants)) list?.[0]?.tenants.reverse();
            let sortTenants = sortDropdownNames(list?.[0]?.tenants, "code", t)
            setCitymoduleList(sortTenants);
        }
    }, [citymodules]);

    useEffect(() => {
        if (uploadMessage || isShowToast) {
            setName("");
            setTenantIdData("");
            setcoreArea("");
            setUploadedFile(null);
            setFile("");
            setUploadMessage("");
        }
        if (isShowToast) {
            history.replace(
                `/digit-ui/citizen/obps/edcrscrutiny/apply/acknowledgement`,
                { data: isShowToast?.label ? isShowToast?.label : "BPA_INTERNAL_SERVER_ERROR", type: "ERROR"}
              );
        }
    }, [uploadMessage, isShowToast, isSubmitBtnDisable]);

    function onAdd() {
        setUploadMessage("NEED TO DELETE");
    }

    const handleSubmit = () => {
        const data = { };
        data.tenantId = tenantIdData;
        data.applicantName = name;
        data.file = file;
        data.coreArea = coreArea;
        onSelect(config.key, data);
    };

    if (isLoading || isSubmitBtnDisable) {
        return (
            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
            <Loader />
            <span
              style={{
                display: 'block',
                textAlign: 'center',
                marginTop: '16px',
                fontWeight: 'bold',
                fontSize: '18px' // You can adjust this value as needed
              }}
            >
              {t("BPA_LOADING_TEXT")}
            </span>
          </div>
        )
    }
    
    return (
        <FormStep
            t={t}
            config={config}
            onSelect={handleSubmit}
            onSkip={onSkip}
            isDisabled={!tenantIdData || !name || !coreArea || !file || isSubmitBtnDisable}
            onAdd={onAdd}
            isMultipleAllow={true}
        >
            <CardLabel>{`${t("EDCR_SCRUTINY_CITY")} *`}</CardLabel>
            <Dropdown
                t={t}
                isMandatory={false}
                option={citymoduleList}
                selected={tenantIdData}
                optionKey="i18nKey"
                select={setTypeOfTenantID}
                uploadMessage={uploadMessage}
            />
            <CardLabel>{`${t("EDCR_SCRUTINY_NAME_LABEL")} *`}</CardLabel>
            <TextInput
                isMandatory={false}
                optionKey="i18nKey"
                t={t}
                name="applicantName"
                onChange={setApplicantName}
                uploadMessage={uploadMessage}
                value={name}
                {...(validation = {
                    isRequired: true,
                    //pattern: "^[a-zA-Z]+(( )+[a-zA-z]+)*$",
                    pattern: "^[a-zA-Z ]+$",
                    type: "text",
                    title: t("TL_NAME_ERROR_MESSAGE"),
                })}
            />
            <CardLabel>{`${t("BPA_CORE_AREA")}`}</CardLabel>
            <Dropdown
                t={t}
                isMandatory={false}
                option={common}
                selected={coreArea}
                optionKey="i18nKey"
                select={setCoreArea}
                uploadMessage={uploadMessage}
            />
            <CardLabel>{`${t("BPA_PLAN_DIAGRAM_LABEL")} *`}</CardLabel>
            <UploadFile
                id={"edcr-doc"}
                extraStyleName={"propertyCreate"}
                // accept=".dxf"
                onUpload={selectfile}
                onDelete={() => {
                    setUploadedFile(null);
                    setFile("");
                }}
                message={uploadedFile ? `1 ${t(`PT_ACTION_FILEUPLOADED`)}` : t(`ES_NO_FILE_SELECTED_LABEL`)}
                error={error}
                uploadMessage={uploadMessage}
            />
            <div style={{ disabled: "true", height: "30px", width: "100%", fontSize: "14px" }}>{t("EDCR_UPLOAD_FILE_LIMITS_LABEL")}</div>
            {isShowToast && <Toast error={isShowToast.key} label={t(isShowToast.label)} onClose={() => setIsShowToast(null)} isDleteBtn={true} />}
            {/* {isSubmitBtnDisable ? <Loader /> : null} */}
        </FormStep>
    );
};

export default EDCRForm;
