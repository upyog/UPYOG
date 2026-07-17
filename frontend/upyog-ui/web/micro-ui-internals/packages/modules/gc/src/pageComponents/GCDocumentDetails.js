import React, { useEffect, useState } from "react";
import { CardLabel, Dropdown, UploadFile, Toast, Loader, FormStep, LabelFieldPair, CardSubHeader, CardLabelDesc } from "@nudmcdgnpm/digit-ui-react-components";


/**
 * GCDocumentDetails component allows users to upload required documents
 * for the GC application. It manages document state, validates uploads,
 * and integrates with a document selection dropdown.
 */
const GCDocumentDetails = ({ t, config, onSelect, userType, formData, value = formData?.gcdocuments, renewApplication }) => {

  
    const [documents, setDocuments] = useState(formData?.[config?.key]?.documents || formData?.gcdocuments?.documents || value?.documents || renewApplication?.documents || []);
    const [error, setError] = useState(null);
    const [enableSubmit, setEnableSubmit] = useState(true);
    const [checkRequiredFields, setCheckRequiredFields] = useState(false);

    const stateId = Digit.ULBService.getStateId();

    // Fetch Document requirements from MDMS for Garbage module
    const { isLoading, data } = Digit.Hooks.useCustomMDMS(stateId, "Garbage", [{ name: "Documents" }]);

    // Condition to check if this is an Inheritance Case
    const isInheritance = [
        formData?.owner?.isInheritance,
        formData?.gcspecifications?.isInheritance,
        formData?.GCSpecifications?.isInheritance,
        formData?.garbageSpecification?.isInheritance,
        formData?.isInheritance
    ].some(val => val === true || val === "true");

    // Map validations onto MDMS data
    let finalDocumentsList = data?.Garbage?.Documents?.length > 0 
        ? data.Garbage.Documents.map(doc => {
            const isPhoto = doc.code === "APPLICANT_PHOTO" || doc.code.includes("PHOTO");
            return {
                ...doc,
                accept: isPhoto ? ".jpeg, .jpg, .png" : ".pdf, .jpeg, .jpg, .png",
                validTypes: isPhoto ? ["image/jpeg", "image/png", "image/jpg"] : ["application/pdf", "image/jpeg", "image/png", "image/jpg"]
            };
        }) 
        : [];

    // Filter out Death Certificate if not an inheritance case
    if (!isInheritance) {
        finalDocumentsList = finalDocumentsList?.filter(doc => !doc.code?.toUpperCase().includes("DEATHCERTIFICATE") && !doc.code?.toUpperCase().includes("DEATH_CERTIFICATE"));
    }

    const handleSubmit = () => {
        let documentStep = { ...formData?.[config?.key], ...formData?.gcdocuments, documents: documents };
        onSelect(config.key, documentStep);
    };

    const onSkip = () => onSelect();
    function onAdd() { }

    useEffect(() => {
        let count = 0;
        finalDocumentsList?.map((doc) => {
            let isRequired = false;
            documents?.map((data) => {
                if (doc.required && data?.documentType?.startsWith(doc.code)) isRequired = true;
            });
            if (!isRequired && doc.required) count = count + 1;
        });
        if ((count == "0" || count == 0) && documents.length > 0) setEnableSubmit(false);
        else setEnableSubmit(true);
    }, [documents, checkRequiredFields, finalDocumentsList]);

    return (
        <div>
            {!isLoading ? (
                <FormStep t={t} config={config} onSelect={handleSubmit} onSkip={onSkip} isDisabled={enableSubmit} onAdd={onAdd}>
                    <CardSubHeader>{t(`GC_PROOF_OF_DOCUMENTS`)}</CardSubHeader>
                    <CardLabelDesc>{t(`GC_UPLOAD_RESTRICTIONS_TYPES`)}</CardLabelDesc>
                    <CardLabelDesc>{t(`GC_UPLOAD_RESTRICTIONS_SIZE`)}</CardLabelDesc>
                    {finalDocumentsList?.map((document, index) => {
                        return (
                            <GCSelectDocument
                                key={index}
                                document={document}
                                t={t}
                                error={error}
                                setError={setError}
                                setDocuments={setDocuments}
                                documents={documents}
                                setCheckRequiredFields={setCheckRequiredFields}
                            />
                        );
                    })}
                    {error && <Toast label={error} onClose={() => setError(null)} error />}
                </FormStep>
            ) : (
                <Loader />
            )}
        </div>
    );
};

function GCSelectDocument({
    t,
    document: doc,
    setDocuments,
    setError,
    documents,
    action,
    formData,
    id
}) {
    const filteredDocument = documents?.find((item) => item?.documentType?.startsWith(doc?.code));

    const user = Digit.UserService.getUser().info;
    const dropDownData = React.useMemo(() => {
        return doc?.dropdownData?.map((e) => ({ ...e, i18nKey: e.i18nKey || "GC_" + e.code?.replaceAll(".", "_") })) || [];
    }, [doc?.dropdownData]);

    const [selectedDocument, setSelectedDocument] = useState(() => {
        if (filteredDocument && doc?.hasDropdown) {
            const childCode = filteredDocument.documentType.split('.').pop();
            return dropDownData.find(e => e.code === childCode) || dropDownData[0] || { code: childCode };
        } else if (doc?.hasDropdown && dropDownData.length > 0) {
            return dropDownData[0];
        }
        return {};
    });

    const [file, setFile] = useState(null);
    const [uploadedFile, setUploadedFile] = useState(() => filteredDocument?.fileStoreId || null);
    const [isUploading, setIsUploading] = useState(false);
    const handleGCSelectDocument = (value) => setSelectedDocument(value);

    function selectfile(e) {
        setFile(e.target.files[0]);
    }

    const LoadingSpinner = () => (
        <div className="loading-spinner" />
    );

    useEffect(() => {
        if (selectedDocument?.code || (!doc?.hasDropdown && doc?.code)) {
            const docCode = doc?.hasDropdown ? `${doc?.code}.${selectedDocument?.code}` : doc?.code;
            setDocuments((prev) => {
                const filteredDocumentsByDocumentType = prev?.filter((item) => !item?.documentType?.startsWith(doc?.code));

                if (!uploadedFile) {
                    return filteredDocumentsByDocumentType;
                }

                const filteredDocumentsByFileStoreId = filteredDocumentsByDocumentType?.filter((item) => item?.fileStoreId !== uploadedFile);
                return [
                    ...filteredDocumentsByFileStoreId,
                    {
                        uuid: user?.uuid,
                        documentType: docCode,
                        fileStoreId: uploadedFile,
                        documentUid: uploadedFile,
                    },
                ];
            });
        }
    }, [uploadedFile, selectedDocument]);

    useEffect(() => {
        (async () => {
            setError(null);
            if (file) {
                const validTypes =
                    doc?.code === "APPLICANT_PHOTO" || doc?.code?.includes("PHOTO")
                        ? ["image/jpeg", "image/png", "image/jpg"]
                        : ["application/pdf", "image/jpeg", "image/png", "image/jpg"];
                if (!validTypes.includes(file.type)) {
                    setError(t("CS_INVALID_FILE_TYPE"));
                } else if (file.size >= 5242880) {
                    setError(t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
                } else {
                    try {
                        setUploadedFile(null);
                        setIsUploading(true);
                        const response = await Digit.UploadServices.Filestorage("GC", file, Digit.ULBService.getStateId());
                        if (response?.data?.files?.length > 0) {
                            setUploadedFile(response?.data?.files[0]?.fileStoreId);
                        } else {
                            setError(t("CS_FILE_UPLOAD_ERROR"));
                        }
                    } catch (err) {
                        setError(t("CS_FILE_UPLOAD_ERROR"));
                    }
                    finally {
                        setIsUploading(false);
                    }
                }
            }
        })();
    }, [file]);

    return (
        <div style={{ marginBottom: "24px" }}>
            {doc?.hasDropdown ? (
                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("GC_" + (doc?.code.replaceAll(".", "_")))} {doc?.required ? <span className="check-page-link-button">*</span> : null}</CardLabel>
                    <Dropdown
                        className="form-field"
                        selected={selectedDocument}
                        style={{ width: user?.type === "EMPLOYEE" ? "50%" : "100%" }}
                        placeholder={"Select " + t("GC_" + (doc?.code.replaceAll(".", "_")))}
                        option={dropDownData}
                        select={handleGCSelectDocument}
                        optionKey="i18nKey"
                        t={t}
                    />
                </LabelFieldPair>
            ) : (
                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("GC_" + (doc?.code.replaceAll(".", "_")))} {doc?.required ? <span className="check-page-link-button">*</span> : null}</CardLabel>
                </LabelFieldPair>
            )}
            <LabelFieldPair>
                <CardLabel className="card-label-smaller"></CardLabel>
                <div className="field">
                    <UploadFile
                        onUpload={selectfile}
                        onDelete={() => {
                            setUploadedFile(null);
                        }}
                        id={id}
                        message={isUploading ? (
                            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                                <LoadingSpinner />
                                <span>Uploading...</span>
                            </div>
                        ) : uploadedFile ? "1 File Uploaded" : "No File Uploaded"}
                        textStyles={{ width: "100%" }}
                        inputStyles={{ width: "280px" }}
                        accept={
                            doc?.code === "APPLICANT_PHOTO" || doc?.code?.includes("PHOTO")
                                ? ".jpeg, .jpg, .png"
                                : ".pdf, .jpeg, .jpg, .png"
                        }
                        buttonType="button"
                        error={!uploadedFile}
                    />
                </div>
            </LabelFieldPair>
        </div>
    );
}

export default GCDocumentDetails;