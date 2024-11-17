import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel,Dropdown, UploadFile,Toast} from "@nudmcdgnpm/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import Timeline from "../components/Timeline";

const SVSpecialCategory = ({ t, config, onSelect, userType, formData }) => {
  console.log("formDataformData",formData);
  let validation = {};
  const user = Digit.UserService.getUser().info;

  const [ownerCategory, setownerCategory]=useState(formData?.specialCategoryData?.ownerCategory || "");
  const [enrollmentId, setenrollmentId]=useState(formData?.specialCategoryData?.enrollmentId || "");
  const [beneficiary, setbeneficiary] =useState(formData?.specialCategoryData?.beneficiary || "");
  const [documents, setDocuments] = useState(formData?.documents?.documents || []);
  const inputStyles = { width: user.type === "EMPLOYEE" ? "50%" : "100%" };

  const [file, setFile] = useState(null);
//   const filteredDocument = documents?.find((item) => item?.documentType?.includes(doc?.code));
  const [uploadedFile, setUploadedFile] = useState(formData?.specialCategoryData?.uploadedFile||null);
  const [isUploading, setIsUploading] = useState(false);

  const handleFileUpload = (e) => {
    const file = e.target.files[0];
    setFile(file);
  };




  const [showToast, setShowToast] = useState(null);

  const { control } = useForm();


  // TODO: Need To make Master data for special Category inside common-masters so that other modules can use the same instead of using it from PT Master data     
  const { data: specialCategory } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "PropertyTax", [{ name: "OwnerType" }],
    {
      select: (data) => {
        const formattedData = data?.["PropertyTax"]?.["OwnerType"]
        return formattedData;
      },
    });
  let specialcategory = [];
  specialCategory && specialCategory.map((special_category) => {
    specialcategory.push({ i18nKey: `${special_category.name}`, code: `${special_category.code}`, value: `${special_category.name}` })
  })

  const { data: schemes } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "StreetVending", [{ name: "Schemes" }],
    {
      select: (data) => {
        const formattedData = data?.["StreetVending"]?.["Schemes"]
        return formattedData;
      },
    });
  let schemes_data = [];
  schemes && schemes.map((schemesdata) => {
    schemes_data.push({ i18nKey: `${schemesdata.name}`, code: `${schemesdata.code}`, value: `${schemesdata.name}` })
  })

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
                const documentData = [
                    {
                      documentType: ownerCategory?.code,
                      documentUid: response.data.files[0].fileStoreId,
                      fileStoreId: response.data.files[0].fileStoreId,
                    },
                  ];
              setUploadedFile(response.data.files[0].fileStoreId);
              sessionStorage.setItem("CategoryDocument", JSON.stringify(documentData))}
               else {
              setError(t("CS_FILE_UPLOAD_ERROR"));
            }
          })
          .catch(() => setError(t("CS_FILE_UPLOAD_ERROR")))
          .finally(() => {setIsUploading(false)});
      }
    }
  }, [file, t]);

  function setenrollment(e) {
    setenrollmentId(e.target.value);
  }

  
    const goNext = () => {
    let category = formData.specialCategoryData;
    let categoryStep = { ...category, ownerCategory, beneficiary,uploadedFile,enrollmentId };
    onSelect(config.key, categoryStep,false);
  };

  const onSkip = () => onSelect();



    useEffect(() => {
      if (showToast) {
        const timer = setTimeout(() => {
          setShowToast(null);
        }, 2000); // Close toast after 1 seconds
  
        return () => clearTimeout(timer); // Clear timer on cleanup
      }
    }, [showToast]);
    useEffect(() => {
      if (userType === "citizen") {
        goNext();
      }
    }, [ownerCategory, beneficiary]);

    //TODO: Need to make seperate component for this loader
    const LoadingSpinner = () => (
        <div className="loading-spinner"
        />
      );


  return (
    <React.Fragment>
      {
        <Timeline currentStep={6} />
      }
      <div>

        <FormStep
          config={config}
          onSelect={goNext}
          onSkip={onSkip}
          t={t}
          isDisabled={!ownerCategory}
        >

          <div>
            <CardLabel>{`${t("SV_CATEGORY")}`} <span className="astericColor">*</span></CardLabel>
            <Controller
              control={control}
              name={"ownerCategory"}
              defaultValue={ownerCategory}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={ownerCategory}
                  select={setownerCategory}
                  option={specialcategory}
                  style={inputStyles}
                  optionKey="i18nKey"
                  t={t}
                  placeholder={"Select"}
                />
              )}
            />
            {ownerCategory&&ownerCategory?.code!=="NONE"&&(
            <div className="field" style={{marginRight:user?.type==="EMPLOYEE"?"50%":null,marginBottom:"10px"}}>
            <UploadFile
            onUpload={handleFileUpload}
            onDelete={() => {
              setUploadedFile(null);
            }}
            id={"SV"}
            message={isUploading ? (
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <LoadingSpinner />
                  <span>Uploading...</span>
                </div>
              ) : uploadedFile ? "1 File Uploaded" : "No File Uploaded"}
            accept=".pdf, .jpeg, .jpg, .png"
            buttonType="button"
            error={!uploadedFile}
          />
          </div>
          )}

            <CardLabel>{`${t("SV_BENEFICIARY_SCHEMES")}`}</CardLabel>
            <Controller
              control={control}
              name={"beneficiary"}
              defaultValue={beneficiary}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={beneficiary}
                  select={setbeneficiary}
                  style={inputStyles}
                  optionCardStyles={{ overflowY: "auto", maxHeight: "315px" }}
                  option={schemes_data}
                  optionKey="i18nKey"
                  t={t}
                  placeholder={"Select"}
                />
              )}
            />
            {beneficiary&&(
                <React.Fragment>
                    <CardLabel>{`${t("SV_ENROLLMENT_APPLICATION_NUMBER")}`}</CardLabel>
                    <TextInput
                    t={t}
                    type={"text"}
                    isMandatory={false}
                    optionKey="i18nKey"
                    name="enrollmentId"
                    value={enrollmentId}
                    onChange={setenrollment}
                    style={inputStyles}
                    ValidationRequired={false}
                    {...(validation = {
                        isRequired: false,
                        pattern: "^[a-zA-Z0-9-/ ]*$",
                        type: "text",
                        title: t("SV_INPUT_DID_NOT_MATCH"),
                    })}
                    />
                </React.Fragment>
            )}
          </div>
        </FormStep>
        {showToast && (
        <Toast
          error={showToast.error}
          warning={showToast.warning}
          label={t(showToast.label)}
          onClose={() => {
            setShowToast(null);
          }}
        />
      )}
        </div>
      </React.Fragment>
    );
  };

export default SVSpecialCategory;