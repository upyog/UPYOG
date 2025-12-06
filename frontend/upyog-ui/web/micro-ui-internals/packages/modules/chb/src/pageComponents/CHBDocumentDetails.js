import React, { useEffect, useState } from "react";
import { CardLabel, Dropdown, UploadFile, Toast, Loader, FormStep, LabelFieldPair,Card,CardSubHeader,CardLabelDesc} from "@upyog/digit-ui-react-components";
import ChbCancellationPolicy from "../components/ChbCancellationPolicy";
import Timeline from "../components/CHBTimeline";
// import { TimerValues } from "../components/TimerValues";


/**
 * CHBDocumentDetails Component
 * 
 * This component is responsible for rendering and managing the document details form for the CHB module.
 * It allows users to upload and manage required documents, such as identity proofs or other supporting files.
 * 
 * Props:
 * - `t`: Translation function for internationalization.
 * - `config`: Configuration object for the form step.
 * - `onSelect`: Callback function triggered when the form step is completed.
 * - `userType`: Type of the user (e.g., employee or citizen).
 * - `formData`: Existing form data to prefill the fields.
 * - `setError`: Function to set form-level errors.
 * - `clearErrors`: Function to clear form-level errors.
 * - `formState`: Current state of the form.
 * - `value`: Default value for the form fields.
 * 
 * State Variables:
 * - `documents`: State variable to manage the list of uploaded documents.
 * - `error`: State variable to manage error messages related to document uploads.
 * - `enableSubmit`: Boolean to enable or disable the submit button.
 * - `checkRequiredFields`: Boolean to check if all required fields are filled.
 * 
 * Hooks:
 * - `Digit.Hooks.chb.useChbDocumentsMDMS`: Custom hook to fetch the list of required documents from MDMS.
 * 
 * Logic:
 * - Fetches the list of required documents based on the state ID and module name.
 * - Allows users to upload documents and manage the list of uploaded files.
 * - Validates required fields and enables/disables the submit button accordingly.
 * - Handles the submission of the document details form by updating the form data with the uploaded documents.
 * 
 * Functions:
 * - `handleSubmit`: Prepares the document data and triggers the `onSelect` callback to proceed to the next step.
 * - `onSkip`: Skips the current step and triggers the `onSelect` callback.
 * - `onAdd`: Placeholder function for adding additional logic (currently empty).
 * 
 * Effects:
 * - `useEffect`: Iterates over the fetched document list and sets additional properties like `hasDropdown` and `isRequired` for each document.
 * 
 * Returns:
 * - A form step component that allows users to upload and manage documents, with validation and error handling.
 */
const CHBDocumentDetails = ({ t, config, onSelect, userType, formData, setError: setFormError, clearErrors: clearFormErrors, formState,value=formData.slotlist}) => {
  const tenantId = Digit.ULBService.getStateId();
  const [documents, setDocuments] = useState(formData?.documents?.documents || value?.existingDataSet?.documents?.documents  || []);
  const [error, setError] = useState(null);
  const [enableSubmit, setEnableSubmit] = useState(true);
  const [checkRequiredFields, setCheckRequiredFields] = useState(false);

  // const tenantId = Digit.ULBService.getCurrentTenantId();
    const stateId = Digit.ULBService.getStateId();
  

  const { isLoading, data } = Digit.Hooks.chb.useChbDocumentsMDMS(stateId, "CHB", "Documents");
  

  const handleSubmit = () => {
    let document = formData.documents;
    let documentStep;
    documentStep = { ...document, documents: documents };
    onSelect(config.key, documentStep);
  };
  const onSkip = () => onSelect();
  function onAdd() {}

  useEffect(() => {
    let count = 0;
    data?.CHB?.Documents.map((doc) => {
      doc.hasDropdown = true;
      
      let isRequired = false;
      documents.map((data) => {
        if (doc.required && data?.documentType.includes(doc.code)) isRequired = true;
      });
      if (!isRequired && doc.required) count = count + 1;
    });
    if ((count == "0" || count == 0) && documents.length > 0) setEnableSubmit(false);
    else setEnableSubmit(true);
  }, [documents, checkRequiredFields]);

  const formatSlotDetails = (slots) => {
    const sortedSlots = slots.sort((a, b) => new Date(a.bookingDate) - new Date(b.bookingDate));
    const firstDate = sortedSlots[0]?.bookingDate;
    const lastDate = sortedSlots[sortedSlots.length - 1]?.bookingDate;
    if(firstDate===lastDate){
      return `${sortedSlots[0]?.name} (${firstDate})`;
    }
    else{
    return `${sortedSlots[0]?.name} (${firstDate} - ${lastDate})`;
    }
  };

  return (
    <div>
      <Timeline currentStep={5} />
      <Card>
        <CardSubHeader>
          <div style={{display:"flex", justifyContent: "space-between", width: "100%" }}>
          {value?.bookingSlotDetails && value.bookingSlotDetails.length > 0
            ? formatSlotDetails(value.bookingSlotDetails)
            : null}
            {/* <TimerValues timerValues={value?.existingDataSet?.timervalue?.timervalue}  SlotSearchData={value?.Searchdata}/> */}
          </div>
        </CardSubHeader>
        <ChbCancellationPolicy slotDetail={value?.bookingSlotDetails}/>
      </Card>
      {!isLoading ? (
        <FormStep t={t} config={config} onSelect={handleSubmit} onSkip={onSkip} isDisabled={enableSubmit} onAdd={onAdd}>
          <CardSubHeader>{t(`CHB_PROOF_OF_DOCUMENTS`)}</CardSubHeader>
          <CardLabelDesc>{t(`CHB_UPLOAD_RESTRICTIONS_TYPES`)}</CardLabelDesc>
          <CardLabelDesc>{t(`CHB_UPLOAD_RESTRICTIONS_SIZE`)}</CardLabelDesc>
          {data?.CHB?.Documents?.map((document, index) => {
            return (
              <CHBSelectDocument
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


function CHBSelectDocument({
  t,
  document: doc,
  setDocuments,
  setError,
  documents,
  action,
  formData,
  
  id,
  
}) {
  const filteredDocument = documents?.filter((item) => item?.documentType?.includes(doc?.code))[0];
  
  const user = Digit.UserService.getUser().info;
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [selectedDocument, setSelectedDocument] = useState(
    filteredDocument
    ? { ...filteredDocument, active: true, code: filteredDocument?.documentType, i18nKey:"CHB_" +filteredDocument?.documentType.replaceAll(".", "_") }
    : doc?.dropdownData?.length > 0
        ? doc?.dropdownData[0]
        : {}
  );

  const [file, setFile] = useState(null);
  const [uploadedFile, setUploadedFile] = useState(() => filteredDocument?.fileStoreId || null);

  const handleCHBSelectDocument = (value) => setSelectedDocument(value);

  function selectfile(e) {
    setFile(e.target.files[0]);
  }
  const { dropdownData } = doc;
  
  var dropDownData = dropdownData;
   
  const [isHidden, setHidden] = useState(false);

  

  useEffect(() => {
    if (selectedDocument?.code) {
      setDocuments((prev) => {
        const filteredDocumentsByDocumentType = prev?.filter((item) => item?.documentType !== selectedDocument?.code);

        if (uploadedFile?.length === 0 || uploadedFile === null) {
          return filteredDocumentsByDocumentType;
        }

        const filteredDocumentsByFileStoreId = filteredDocumentsByDocumentType?.filter((item) => item?.fileStoreId !== uploadedFile);
        return [
          ...filteredDocumentsByFileStoreId,
          {
            documentType: selectedDocument?.code,
            fileStoreId: uploadedFile,
            documentUid: uploadedFile,
          },
        ];
      });
    }
    
  }, [uploadedFile, selectedDocument]);

  useEffect(() => {
    if (action === "update") {
      const originalDoc = formData?.originalData?.documents?.filter((e) => e.documentType.includes(doc?.code))[0];
      const docType = dropDownData
        .filter((e) => e.code === originalDoc?.documentType)
        .map((e) => ({ ...e, i18nKey: e?.code?.replaceAll(".", "_") }))[0];
      if (!docType) setHidden(true);
      else {
        setSelectedDocument(docType);
        setUploadedFile(originalDoc?.fileStoreId);
      }
    } else if (action === "create") {
    }
  }, []);

  useEffect(() => {
    (async () => {
      setError(null);
      if (file) {
        if (file.size >= 5242880) {
          setError(t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
          // if (!formState.errors[config.key]) setFormError(config.key, { type: doc?.code });
        } else {
          try {
            setUploadedFile(null);
            const response = await Digit.UploadServices.Filestorage("CHB", file, Digit.ULBService.getStateId());
            if (response?.data?.files?.length > 0) {
              setUploadedFile(response?.data?.files[0]?.fileStoreId);
            } else {
              setError(t("CS_FILE_UPLOAD_ERROR"));
            }
          } catch (err) {
            setError(t("CS_FILE_UPLOAD_ERROR"));
          }
        }
      }
    })();
  }, [file]);

  useEffect(() => {
    if (isHidden) setUploadedFile(null);
  }, [isHidden]);

  return (
    <div style={{ marginBottom: "24px" }}>
      {doc?.hasDropdown ? (
        <LabelFieldPair>
          <CardLabel className="card-label-smaller">{t("CHB_"+(doc?.code.replaceAll(".", "_"))) } <span className="check-page-link-button">*</span></CardLabel>
          <Dropdown
            className="form-field"
            selected={selectedDocument}
            style={{width:user.type==="EMPLOYEE"?"50%":"100%"}}
            placeholder={"Select " + t("CHB_"+(doc?.code.replaceAll(".", "_"))) }
            option={dropDownData.map((e) => ({ ...e, i18nKey:"CHB_" + e.code?.replaceAll(".", "_") }))}
            select={handleCHBSelectDocument}
            optionKey="i18nKey"
            t={t}
          />
        </LabelFieldPair>
      ) : null}
      <LabelFieldPair>
        <CardLabel className="card-label-smaller"></CardLabel>
        <div className="field">
          <UploadFile
            onUpload={selectfile}
            onDelete={() => {
              setUploadedFile(null);
            }}
            id={id}
            message={uploadedFile ? `1 ${t(`CS_ACTION_FILEUPLOADED`)}` : t(`CS_ACTION_NO_FILEUPLOADED`)}
            textStyles={{ width: "100%" }}
            inputStyles={{ width: "280px" }}
            accept=".pdf, .jpeg, .jpg, .png"   //  to accept document of all kind
            buttonType="button"
            error={!uploadedFile}
          />
        </div>
      </LabelFieldPair>
    </div>
  );
}

export default CHBDocumentDetails;