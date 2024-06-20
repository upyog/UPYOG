

//#################################################################################################




// //C:\UpyogProjectDev\UPYOG-LIVE\UPYOG\frontend\upyog-ui\web\micro-ui-internals\packages\modules\obps\src\pageComponents\StakeholderDocuments.js
// import React, { useEffect, useState } from "react";
// import {
//     CardLabel,
//     Dropdown,
//     UploadFile,
//     Toast,
//     Loader,
//     FormStep,
//     CitizenInfoLabel,
//     OpenLinkContainer,
//     BackButton
// } from "@egovernments/digit-ui-react-components";
// // import Timeline from "../../components/Timeline";

// const WmsTEUploadDocuments = ({ t, config, onSelect, userType, formData, setError: setFormError, clearErrors: clearFormErrors, formState }) => {
//     // const tenantId = Digit.ULBService.getCurrentTenantId();
//     // const stateId = Digit.ULBService.getStateId();
//     // const [documents, setDocuments] = useState(formData?.documents?.documents || []);
//     // const [error, setError] = useState(null);
//     // const [bpaTaxDocuments, setBpaTaxDocuments] = useState([]);
//     // const [enableSubmit, setEnableSubmit] = useState(true)
//     // const [checkRequiredFields, setCheckRequiredFields] = useState(false);
//     // const isCitizenUrl = Digit.Utils.browser.isMobile()?true:false;
//     // let isopenlink = window.location.href.includes("/openlink/");

//     // if(isopenlink)  
//     // window.onunload = function () {
//     //   sessionStorage.removeItem("Digit.BUILDING_PERMIT");
//     // }

//     // const { data, isLoading } = Digit.Hooks.obps.useMDMS(stateId, "StakeholderRegistraition", "TradeTypetoRoleMapping");
    


//     //######***************############
//     // const [uploadedFile, setUploadedFile] = useState(() => filteredDocument?.fileStoreId || null);
//     const [error, setError] = useState(null);
//     const [checkRequiredFields, setCheckRequiredFields] = useState(false);
//     const [file, setFile] = useState(null);
//     function selectfile(e) {
//         setFile(e.target.files[0]);
//     }
//     useEffect(() => {
//         (async () => {
//             setError(null);
//             if (file) {
//                 const allowedFileTypesRegex = /(.*?)(jpg|jpeg|png|image|pdf)$/i
//                 if (file.size >= 5242880) {
//                     setError(t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
//                 } else if (file?.type && !allowedFileTypesRegex.test(file?.type)) {
//                     setError(t(`NOT_SUPPORTED_FILE_TYPE`))
//                 } else {
//                     try {
//                         setUploadedFile(null);
//                         const response = await Digit.UploadServices.Filestorage("PT", file, tenantId?.split(".")[0]);
//                         if (response?.data?.files?.length > 0) {
//                             setUploadedFile(response?.data?.files[0]?.fileStoreId);
//                         } else {
//                             setError(t("CS_FILE_UPLOAD_ERROR"));
//                         }
//                     } catch (err) {
//                         setError(t("CS_FILE_UPLOAD_ERROR"));
//                     }
//                 }
//             }
//         })();
//     }, [file]);

//     return (
//         <div style={{ marginBottom: "24px" }}>
//             {/* <CardLabel style={{marginBottom: "10px"}}>{doc?.required ? `${t(`BPAREG_HEADER_${doc?.code?.replace('.', '_')}`)} *` : `${t(`BPAREG_HEADER_${doc?.code?.replace('.', '_')}`)}`}</CardLabel>
//             {doc?.info ? <div style={{fontSize: "12px", color: "#505A5F", fontWeight: 400, lineHeight: "15px", marginBottom: "10px"}}>{`${t(doc?.info)}`}</div> : null} */}
//             <UploadFile
//                 extraStyleName={"OBPS"}
//                 // accept="image/*, .pdf, .png, .jpeg, .jpg"
//                 onUpload={selectfile}
//                 onDelete={() => {
//                     setUploadedFile(null);
//                     setCheckRequiredFields(true);
//                 }}
//                 // message={uploadedFile ? `1 ${t(`CS_ACTION_FILEUPLOADED`)}` : t(`CS_ACTION_NO_FILEUPLOADED`)}
//                 iserror={error}
//             />
//         </div>
//     );
// }



// export default WmsTEUploadDocuments;



//################ Working 1 ############################################################

////C:\UpyogProjectDev\UPYOG-LIVE\UPYOG\frontend\upyog-ui\web\micro-ui-internals\packages\modules\tl\src\pageComponents\Proof.js

// import React, { useState, useEffect } from "react";
// import { FormStep, UploadFile, CardLabelDesc, Dropdown, CardLabel } from "@egovernments/digit-ui-react-components";
// // import { stringReplaceAll } from "../utils";
// // import Timeline from "../components/TLTimeline";

// const WmsTEUploadDocuments = ({ t, config, onSelect, userType, formData }) => {
//   //let index = window.location.href.charAt(window.location.href.length - 1);
//   const [uploadedFile, setUploadedFile] = useState(formData?.owners?.documents?.OwnerPhotoProof?.fileStoreId || null);
//   const [file, setFile] = useState(formData?.owners?.documents?.OwnerPhotoProof);
//   const [error, setError] = useState(null);
//   const cityDetails = Digit.ULBService.getCurrentUlb();
//   let acceptFormat = ".jpg,.png,.pdf,.jpeg";

//   const [dropdownValue, setDropdownValue] = useState(formData?.owners?.documents?.OwnerPhotoProof?.documentType || null);
//   // let dropdownData = [];
//   const tenantId = Digit.ULBService.getCurrentTenantId();
//   const stateId = Digit.ULBService.getStateId();
//   const { data: Documentsob = {} } = Digit.Hooks.pt.usePropertyMDMS(stateId, "PropertyTax", "Documents");
//   const docs = Documentsob?.PropertyTax?.Documents;
//   const ownerPhotoProof = Array.isArray(docs) && docs.filter((doc) => doc.code.includes("ADDRESSPROOF"));
//   // if (ownerPhotoProof.length > 0) {
//   //   dropdownData = ownerPhotoProof[0]?.dropdownData;
//   //   dropdownData.forEach((data) => {
//   //     data.i18nKey = stringReplaceAll(data.code, ".", "_");
//   //   });
//   // }

//   // function setTypeOfDropdownValue(dropdownValue) {
//   //   setDropdownValue(dropdownValue);
//   // }
//   useEffect(() => {
//     localStorage.setItem("TLAppSubmitEnabled", "true");
//   }, []);
//   const handleSubmit = () => {
//     let fileStoreId = uploadedFile;
//     let fileDetails = file;
//     if (fileDetails) fileDetails.documentType = "OWNERPHOTO";
//     if (fileDetails) fileDetails.fileStoreId = fileStoreId ? fileStoreId : null;
//     let owners = formData?.owners;
//     if (owners && owners.documents) {
//       owners.documents["OwnerPhotoProof"] = fileDetails;
//     } else {
//       owners["documents"] = [];
//       owners.documents["OwnerPhotoProof"] = fileDetails;
//     }
//     onSelect(config.key, owners);
//     // onSelect(config.key, { specialProofIdentity: fileDetails }, "", index);
//   };
//   const onSkip = () => onSelect();

//   function selectfile(e) {
//     setUploadedFile(null);
//     setFile(e.target.files[0]);
//   }

//   useEffect(() => {
//     (async () => {
//       setError(null);
//       if (file && file?.type) {
//         if (!acceptFormat?.split(",")?.includes(`.${file?.type?.split("/")?.pop()}`)) {
//           setError(t("PT_UPLOAD_FORMAT_NOT_SUPPORTED"));
//         } else if (file.size >= 2000000) {
//           setError(t("PT_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
//         } else {
//           try {
//             const response = await Digit.UploadServices.Filestorage("property-upload", file, Digit.ULBService.getStateId());
//             if (response?.data?.files?.length > 0) {
//               setUploadedFile(response?.data?.files[0]?.fileStoreId);
//             } else {
//               setError(t("PT_FILE_UPLOAD_ERROR"));
//             }
//           } catch (err) {}
//         }
//       }
//     })();
//   }, [file]);

//   return (
//     <React.Fragment>
//       {/* {window.location.href.includes("/citizen") ? <Timeline currentStep={3} /> : null} */}
//       <FormStep config={config} onSelect={handleSubmit} onSkip={onSkip} t={t} isDisabled={!uploadedFile || error}>
//         <CardLabelDesc style={{ fontWeight: "unset" }}>{t(`TL_UPLOAD_PHOTO_RESTRICTIONS_TYPES`)}</CardLabelDesc>
//         <CardLabelDesc style={{ fontWeight: "unset" }}>{t(`TL_UPLOAD_RESTRICTIONS_SIZE`)}</CardLabelDesc>
//         <CardLabel>{`${t("TL_CATEGORY_DOCUMENT_TYPE")}`}</CardLabel>
//         {/* <Dropdown
//         t={t}
//         isMandatory={false}
//         option={dropdownData}
//         selected={dropdownValue}
//         optionKey="i18nKey"
//         select={setTypeOfDropdownValue}
//         //placeholder={t(`PT_MUTATION_SELECT_DOC_LABEL`)}
//       /> */}
//         <UploadFile
//           id={"tl-doc"}
//           extraStyleName={"propertyCreate"}
//           accept=".jpg,.png,.pdf,.jpeg"
//           onUpload={selectfile}
//           onDelete={() => {
//             setUploadedFile(null);
//           }}
//           message={uploadedFile ? `1 ${t(`TL_ACTION_FILEUPLOADED`)}` : t(`TL_ACTION_NO_FILEUPLOADED`)}
//           error={error}
//         />
//         {error ? <div style={{ height: "20px", width: "100%", fontSize: "20px", color: "red", marginTop: "5px" }}>{error}</div> : ""}
//         <div style={{ disabled: "true", height: "20px", width: "100%" }}></div>
//       </FormStep>
//     </React.Fragment>
//   );
// };

// export default WmsTEUploadDocuments;








//################ Working 2 ############################################################

//C:\UpyogProjectDev\UPYOG-LIVE\UPYOG\frontend\upyog-ui\web\micro-ui-internals\packages\modules\ws\src\pageComponents\WSActivationSupportingDocuments.js

import React, { useEffect, useState } from "react";
import { CardLabel, LabelFieldPair, Dropdown, UploadFile, Toast, Loader } from "@egovernments/digit-ui-react-components";
import { useLocation } from "react-router-dom";

const WmsTEUploadDocuments = ({ t, config, userType, formData, onSelect }) => {
    const stateId = Digit.ULBService.getStateId();
    const tenantIdCode = Digit.ULBService.getCurrentTenantId();
    const [documents, setDocuments] = useState(formData?.supportingDocuments?.[0] ? [formData?.supportingDocuments?.[0]] : []);
    const [error, setError] = useState(null);
    const [uploadedFile, setUploadedFile] = useState(null);
    const [file, setFile] = useState(null);
console.log({file,error,uploadedFile,config, userType, formData, onSelect}," Image")
    const goNext = () => {
        onSelect(config.key, { documents });
    };

    function selectfile(e) {
        setFile(e.target.files[0]);
    }

    useEffect(() => {
        goNext();
    }, [documents]);

    useEffect(() => {
        // if (userType === "employee") {
            onSelect(config.key, { ...formData[config.key], ...documents });
        // }
    }, [documents]);

    useEffect(() => {
        (async () => {
            setError(null);
            if (file) {
                const allowedFileTypesRegex = /(.*?)(jpg|jpeg|png|image|pdf)$/i
                if (file.size >= 5242880) {
                  setError(t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
                } else if (file?.type && !allowedFileTypesRegex.test(file?.type)) {
                  setError(t(`NOT_SUPPORTED_FILE_TYPE`))
                } else {
try {
    var formdata = new FormData();
formdata.append("file", file);
formdata.append("tenantId", "pg");
formdata.append("module", "wms");
    let  requestOptions = {
        method: 'POST',
        body: formdata,
        redirect: 'follow'
      };

    fetch("http://10.216.36.152:8484/wms/wms-services/v1/tenderentry/_upload", requestOptions)
    .then(response => response.text())
    .then(result => {
        return (
            setUploadedFile(result),

            localStorage.setItem("imagePath",result),
        console.log("image uploaded ", result)
    )})
    .catch(error => console.log('errorrrrrr ', error));
} catch (error) {
    alert("Something wrong!")
}

                    {/*
                    IT ONLY WILL BE USE FOR IMAGE UPLOAD
                    */} 
                    // try {
                    //     // C:\UpyogProjectDev\UPYOG-LIVE\UPYOG\frontend\upyog-ui\web\micro-ui-internals\packages\libraries\src\services\atoms\UploadServices.js
                    //     const response = await Digit.UploadServices.Filestorage("WS", file, tenantIdCode);
                    //     console.log("response ",response )
                    //     if (response?.data?.files?.length > 0) {
                    //         setUploadedFile(response?.data?.files[0]);
                    //         // localStorage.setItem("imagePath",JSON.stringify(response?.data?.files[0]?.fileStoreId))
                    //         // setUploadedFile(response?.data?.files[0]?.fileStoreId);
                    //     } else {
                    //         setError(t("CS_FILE_UPLOAD_ERROR"));
                    //     }
                    // } catch (err) {
                    //     setError(t("CS_FILE_UPLOAD_ERROR"));
                    // }
                    
                }
            }
        })();
    }, [file]);

    useEffect(() => {
        if (uploadedFile) {
          setDocuments((prev) => {
            return [{ fileStoreId: uploadedFile, documentUid: uploadedFile }];
          });
        }
      }, [uploadedFile]);

    return (
        <div>
            <LabelFieldPair>
                <CardLabel className="card-label-smaller" style={{ fontWeight: "700"}}>{t(`WF_APPROVAL_UPLOAD_HEAD`)}:</CardLabel>
                <div className="field">
                    <UploadFile
                        // id={id}
                        onUpload={(e) => { selectfile(e, "DOCUMENT-!") }}
                        onDelete={() => { setUploadedFile(null); }}
                        message={uploadedFile ? `1 ${t(`CS_ACTION_FILEUPLOADED`)}` : t(`CS_ACTION_NO_FILEUPLOADED`)}
                        textStyles={{ width: "100%" }}
                        inputStyles={{ width: "250px" }}
                        buttonType="button"
                        accept= "image/*, .pdf, .png, .jpeg, .jpg"
                        iserror={error}
                    />
                    <img src={formData?.WmsTEUploadDocuments?.upload_document} width="25" />
                </div>
            </LabelFieldPair>
        </div>
    );
};

export default WmsTEUploadDocuments;
