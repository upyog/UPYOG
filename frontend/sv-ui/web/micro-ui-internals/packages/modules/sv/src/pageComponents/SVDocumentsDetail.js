import React, { useEffect, useState, Fragment } from "react";
import { CardLabel, Dropdown, UploadFile, Toast, Loader, FormStep, LabelFieldPair } from "@nudmcdgnpm/digit-ui-react-components";
import Timeline from "../components/Timeline";


/**
 * 
 * SVDocumentsDetail component manages the document upload process for street vending applications.
 * It fetches document requirements from the server and allows users to select and upload files.
 * The component maintains state for the uploaded documents, handles file size validation, and updates the parent component with selected documents on submission.
 * The SVDocuments sub-component handles individual document selection and file uploads, displaying appropriate messages based on the upload status.
 * Error handling is implemented to inform users of any issues during file upload.
 */

const SVDocumentsDetail = ({ t, config, onSelect, formData, editdata, previousData }) => {
  const [documents, setDocuments] = useState(formData?.documents?.documents || []);
  const [error, setError] = useState(null);
  const [enableSubmit, setEnableSubmit] = useState(true);
  const [checkRequiredFields, setCheckRequiredFields] = useState(true);

  const stateId = Digit.ULBService.getStateId();
  const { isLoading, data } = Digit.Hooks.sv.useSVDoc(stateId, "StreetVending", "Documents");


  // Utility function to transform documents array into required format
  const transformDocuments = (documents) => {
    if (!Array.isArray(documents)) return [];

    const transformedDocs = documents.map(doc => ({
      applicationId: "",
      documentType: doc.documentType,
      fileStoreId: doc.fileStoreId,
      documentDetailId: doc.documentUid,
      auditDetails: {
        createdBy: "",
        createdTime: 0,
        lastModifiedBy: "",
        lastModifiedTime: 0
      }
    }));
    return transformedDocs;
  };

  //Custom function fo rthe payload whic we can use while goint to next

  const handleSaveasDraft = () => {
    let vendordetails = [];
    let tenantId = Digit.ULBService.getCitizenCurrentTenant(true);
    const createVendorObject = (formData) => ({
      applicationId: "",
      auditDetails: {
        createdBy: "",
        createdTime: 0,
        lastModifiedBy: "",
        lastModifiedTime: 0
      },
      dob: formData?.owner?.units?.[0]?.vendorDateOfBirth,
      userCategory: formData?.owner?.units?.[0]?.userCategory?.code,
      emailId: formData?.owner?.units?.[0]?.email,
      fatherName: formData?.owner?.units?.[0]?.fatherName,
      gender: formData?.owner?.units?.[0]?.gender?.code.charAt(0),
      id: "",
      mobileNo: formData?.owner?.units?.[0]?.mobileNumber,
      name: formData?.owner?.units?.[0]?.vendorName,
      relationshipType: "VENDOR",
      vendorId: null
    });

    const createSpouseObject = (formData) => ({
      applicationId: "",
      auditDetails: {
        createdBy: "",
        createdTime: 0,
        lastModifiedBy: "",
        lastModifiedTime: 0
      },
      dob: formData?.owner?.units?.[0]?.spouseDateBirth,
      userCategory: formData?.owner?.units?.[0]?.userCategory?.code,
      emailId: "",
      isInvolved: formData?.owner?.spouseDependentChecked,
      fatherName: "",
      gender: "O",
      id: "",
      mobileNo: "",
      name: formData?.owner?.units?.[0]?.spouseName,
      relationshipType: "SPOUSE",
      vendorId: null
    });

    const createDependentObject = (formData) => ({
      applicationId: "",
      auditDetails: {
        createdBy: "",
        createdTime: 0,
        lastModifiedBy: "",
        lastModifiedTime: 0
      },
      dob: formData?.owner?.units?.[0]?.dependentDateBirth,
      userCategory: formData?.owner?.units?.[0]?.userCategory?.code,
      emailId: "",
      isInvolved: formData?.owner?.dependentNameChecked,
      fatherName: "",
      gender: formData?.owner?.units?.[0]?.dependentGender?.code.charAt(0),
      id: "",
      mobileNo: "",
      name: formData?.owner?.units?.[0]?.dependentName,
      relationshipType: "DEPENDENT",
      vendorId: null
    });

    // Helper function to check if a string is empty or undefined
    const isEmpty = (str) => !str || str.trim() === '';

    // Main logic
    if (!isEmpty(formData?.owner?.units?.[0]?.vendorName)) {
      const spouseName = formData?.owner?.units?.[0]?.spouseName;
      const dependentName = formData?.owner?.units?.[0]?.dependentName;

      if (isEmpty(spouseName) && isEmpty(dependentName)) {
        // Case 1: Only vendor exists
        vendordetails = [createVendorObject(formData)];
      } else if (!isEmpty(spouseName) && isEmpty(dependentName)) {
        // Case 2: Both vendor and spouse exist
        vendordetails = [
          createVendorObject(formData),
          createSpouseObject(formData)
        ];
      } else if (!isEmpty(spouseName) && !isEmpty(dependentName)) {
        // Case 3: All three exist (vendor, spouse, and dependent)
        vendordetails = [
          createVendorObject(formData),
          createSpouseObject(formData),
          createDependentObject(formData)
        ];
      }
    }

    const daysOfOperations = formData?.businessDetails?.daysOfOperation;
    const vendingOperationTimeDetails = daysOfOperations
      .filter(day => day.isSelected) // Filter only selected days
      .map(day => ({
        applicationId: "", // Add actual applicationId if available
        auditDetails: {
          createdBy: "", // Adjust these fields based on your data
          createdTime: 0,
          lastModifiedBy: "",
          lastModifiedTime: 0,
        },
        dayOfWeek: day.name.toUpperCase(),
        fromTime: day.startTime,
        toTime: day.endTime,
        id: ""
      }));

    const api_response = sessionStorage.getItem("Response");
    const response = JSON.parse(api_response);

    let streetVendingDetail = {
      addressDetails: [
        {
          addressId: "",
          addressLine1: formData?.address?.addressline1,
          addressLine2: formData?.address?.addressline2,
          addressType: "",
          city: formData?.address?.city?.name,
          cityCode: formData?.address?.city?.code,
          doorNo: "",
          houseNo: formData?.address?.houseNo,
          landmark: formData?.address?.landmark,
          locality: formData?.address?.locality?.i18nKey,
          localityCode: formData?.address?.locality?.code,
          pincode: formData?.address?.pincode,
          streetName: "",
          vendorId: ""
        },
        { // sending correspondence address here
          addressId: "",
          addressLine1: formData?.correspondenceAddress?.caddressline1,
          addressLine2: formData?.correspondenceAddress?.caddressline2,
          addressType: "",
          city: formData?.correspondenceAddress?.ccity?.name,
          cityCode: formData?.correspondenceAddress?.ccity?.code,
          doorNo: "",
          houseNo: formData?.correspondenceAddress?.chouseNo,
          landmark: formData?.correspondenceAddress?.clandmark,
          locality: formData?.correspondenceAddress?.clocality?.i18nKey,
          localityCode: formData?.correspondenceAddress?.clocality?.code,
          pincode: formData?.correspondenceAddress?.cpincode,
          streetName: "",
          vendorId: "",
          isAddressSame: formData?.correspondenceAddress?.isAddressSame
        }
      ],
      applicationDate: 0,
      applicationId: "",
      applicationNo: "",
      applicationStatus: "",
      approvalDate: 0,
      auditDetails: {
        createdBy: "",
        createdTime: 0,
        lastModifiedBy: "",
        lastModifiedTime: 0
      },
      bankDetail: {
        accountHolderName: formData?.bankDetails?.accountHolderName,
        accountNumber: formData?.bankDetails?.accountNumber,
        applicationId: "",
        bankBranchName: formData?.bankDetails?.bankBranchName,
        bankName: formData?.bankDetails?.bankName,
        id: "",
        ifscCode: formData?.bankDetails?.ifscCode,
        refundStatus: "",
        refundType: "",
        auditDetails: {
          createdBy: "",
          createdTime: 0,
          lastModifiedBy: "",
          lastModifiedTime: 0
        },
      },
      benificiaryOfSocialSchemes: [],
      applicationCreatedBy: formData?.owner?.applicationCreatedBy,
      locality: formData?.businessDetails?.vendorLocality?.code || "",
      localityValue: "",
      vendingZoneValue: "",
      vendorPaymentFrequency: formData?.businessDetails?.vendingPayment?.code,
      enrollmentId: "",
      cartLatitude: 0,
      cartLongitude: 0,
      certificateNo: null,
      disabilityStatus: "",
      draftId: previousData?.draftId || response?.SVDetail?.draftId,
      documentDetails: transformDocuments(documents),
      localAuthorityName: formData?.businessDetails?.nameOfAuthority,
      tenantId: tenantId,
      termsAndCondition: "Y",
      tradeLicenseNo: formData?.owner?.units?.[0]?.tradeNumber,
      vendingActivity: formData?.businessDetails?.vendingType?.code,
      vendingArea: formData?.businessDetails?.areaRequired || "0",
      vendingLicenseCertificateId: "",
      vendingOperationTimeDetails,
      vendingZone: formData?.businessDetails?.vendingZones?.code,
      vendorDetail: [
        ...vendordetails
      ],
      workflow: {
        action: "APPLY",
        comments: "",
        businessService: "street-vending",
        moduleName: "sv-services",
        businessService: "street-vending",
        moduleName: "sv-services",
        varificationDocuments: [
          {
            additionalDetails: {},
            auditDetails: {
              createdBy: "",
              createdTime: 0,
              lastModifiedBy: "",
              lastModifiedTime: 0
            },
            documentType: "",
            documentUid: "",
            fileStoreId: "",
            id: ""
          }
        ]
      }
    };

    Digit.SVService.create({ streetVendingDetail, draftApplication: true }, tenantId)
      .then(response => {
        sessionStorage.setItem("Response", JSON.stringify(response));
      })
      .catch(error => {
        console.log("Something Went Wrong", error);
      })

  };

  /**
   * Handles the submission of the document form.
   * 
   * - Merges the current form data with the latest documents.
   * - Calls the `onSelect` callback with the updated document step.
   * - If the current URL does not include "edit", triggers saving as draft.
   *
   * @function
   * @returns {void}
   */
  const handleSubmit = () => {
    let documentStep = { ...formData.documents, documents };
    onSelect(config.key, documentStep);
    window.location.href.includes("edit") ? null : handleSaveasDraft();
  };

  const onSkip = () => onSelect();

  useEffect(() => {
    let count = 0;
    data?.StreetVending?.Documents.forEach((doc) => {
      doc.hasDropdown = true;
      let isRequired = documents.some((data) => doc.required && data?.documentType.includes(doc.code));
      if (!isRequired && doc.required) count += 1;
    });
    setEnableSubmit(!(count === 0 && documents.length > 0));
  }, [documents, checkRequiredFields, data]);

  return (
    <div>
      <Timeline currentStep={5} />
      {!isLoading ? (
        <FormStep t={t} config={config} onSelect={handleSubmit} onSkip={onSkip} isDisabled={enableSubmit}>
          {data?.StreetVending?.Documents?.map((document, index) => (
            <SVDocuments
              key={index}
              document={document}
              t={t}
              error={error}
              setError={setError}
              setDocuments={setDocuments}
              documents={documents}
              setCheckRequiredFields={setCheckRequiredFields}
              formData={formData}
              editdata={editdata}
              previousData={previousData}
            />
          ))}
          {error && <Toast label={error} onClose={() => setError(null)} error />}
        </FormStep>
      ) : (
        <Loader />
      )}
    </div>
  );
};

/**
 * SVDocuments is a React component for handling document selection and file upload functionality
 * in the Street Vending module. It manages document dropdowns, file uploads, and updates the parent
 * state with the selected and uploaded documents.
 *
 * @component
 * @param {Object} props - The component props.
 * @param {function} props.t - Translation function for i18n.
 * @param {Object} props.document - The document configuration object for the current field.
 * @param {function} props.setDocuments - Setter function to update the documents array in parent state.
 * @param {function} props.setError - Setter function to update error messages in parent state.
 * @param {Array<Object>} props.documents - The current list of uploaded documents.
 * @param {Object} props.formData - The form data object (not directly used in this component).
 * @param {string|number} props.id - Unique identifier for the upload input.
 * @param {Object} props.editdata - Data for editing an existing application.
 * @param {Object} props.previousData - Data from a previous draft or application.
 *
 * @returns {JSX.Element} The rendered document upload and selection UI.
 */
function SVDocuments({
  t,
  document: doc,
  setDocuments,
  setError,
  documents,
  formData,
  id,
  editdata,
  previousData

}) {

  const filteredDocument = documents?.find((item) => item?.documentType?.includes(doc?.code));
  const previousDocument = previousData?.documentDetails?.find((item) => item?.documentType?.includes(doc?.code));
  const editdatas = editdata?.documentDetails?.find((item) => item?.documentType?.includes(doc?.code));
  const user = Digit.UserService.getUser().info;
  const getDocumentData = (document) => {
    return document
      ? {
        ...document,
        active: doc?.active === true,
        code: document?.documentType,
        i18nKey: document?.documentType,
      }
      : null;
  };

  const initialDocument =
    getDocumentData(previousDocument) ||
    getDocumentData(editdatas) ||
    getDocumentData(filteredDocument) ||
    (doc?.dropdownData?.length === 1 ? doc?.dropdownData[0] : {});

  const [selectedDocument, setSelectedDocument] = useState(initialDocument);
  const [file, setFile] = useState(null);
  const [uploadedFile, setUploadedFile] = useState(initialDocument?.fileStoreId || null);
  const [isUploading, setIsUploading] = useState(false);

  const handleSVSelectDocument = (value) => {
    setSelectedDocument(value);
  }

  const handleFileUpload = (e) => {
    const file = e.target.files[0];
    setFile(file);
  };


  useEffect(() => {
    const docs = previousData?.documentDetails || editdata?.documentDetails || [];

    docs?.documentDetails?.map((row, index) => {
      row?.documentType.includes(doc?.code) ? setUploadedFile(row) : null;
    })
  }, [editdata?.documentDetails, previousData?.documentDetails, doc?.code]);

  useEffect(() => {
    if (file) {
      if (file.size >= 5242880) {
        setError(t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
      } else {
        setUploadedFile(null);
        setIsUploading(true);
        Digit.UploadServices.Filestorage("StreetVending", file, Digit.ULBService.getStateId())
          .then(response => {
            if (response?.data?.files?.length > 0) {
              setUploadedFile(response.data.files[0].fileStoreId);
            } else {
              setError(t("CS_FILE_UPLOAD_ERROR"));
            }
          })
          .catch(() => setError(t("CS_FILE_UPLOAD_ERROR")))
          .finally(() => { setIsUploading(false) });
      }
    }
  }, [file, t]);
  useEffect(() => {
    if (selectedDocument?.code && previousData?.draftId?.length > 1) {
      setDocuments((prev) => {
        const filteredDocumentsByDocumentType = prev?.filter((item) => item?.documentType !== selectedDocument?.code);
        if (!uploadedFile) {
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
    else if (selectedDocument?.code && editdata?.applicationNo) {
      setDocuments((prev) => {
        const filteredDocumentsByDocumentType = prev?.filter((item) => item?.documentType !== selectedDocument?.code);
        if (!uploadedFile) {
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
    else if (selectedDocument?.code) {
      setDocuments((prev) => {
        const filteredDocumentsByDocumentType = prev?.filter((item) => item?.documentType !== selectedDocument?.code);
        if (!uploadedFile) {
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
  }, [uploadedFile, selectedDocument, setDocuments]);

  const { dropdownData } = doc;
  let dropDownData = dropdownData;
  const LoadingSpinner = () => (
    <div className="loading-spinner"
    />
  );


  return (
    <div style={{ marginBottom: "24px" }}>
      {doc?.hasDropdown && (
        <LabelFieldPair>
          {/* Condition to check if the current doc is family photo or not and then add a message along with it */}
          <CardLabel className="card-label-smaller" style={{ display: "inline-block", whiteSpace: "normal", width: user?.type === "EMPLOYEE" ? "30%" : "100%" }}>
            <span style={{ display: "flex", alignItems: "center", gap: "0.25rem", flexWrap: "wrap" }}>
              {t(doc?.code.replaceAll(".", "_"))}&nbsp;<span className="astericColor">*</span>
              {doc?.code?.replaceAll(".", "_") === "FAMILY_PHOTO" && (
                <span style={{ color: "#b0242c" }}>{t("SV_ONLY_JPEG_PNG_ALLOWED")}</span>
              )}
            </span>
          </CardLabel>
          <Dropdown
            className="form-field"
            selected={selectedDocument}
            select={handleSVSelectDocument}
            style={{ width: user?.type === "EMPLOYEE" ? "50%" : "100%" }}
            option={dropDownData.map((e) => ({ ...e, i18nKey: e.code?.replaceAll(".", "_") }))}
            optionKey="i18nKey"
            t={t}
            placeholder={"Select"}
          />
        </LabelFieldPair>
      )}
      <LabelFieldPair>
        <div className="field" style={{ marginLeft: user?.type === "EMPLOYEE" ? "30%" : null }}>
          <UploadFile
            onUpload={handleFileUpload}
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
              doc?.code?.replaceAll(".", "_") === "FAMILY_PHOTO"
                ? ".jpeg, .jpg, .png"
                : ".pdf, .jpeg, .jpg, .png"
            } buttonType="button"
            error={!uploadedFile}
          />
        </div>
      </LabelFieldPair>
    </div>
  );
}

export default SVDocumentsDetail;
