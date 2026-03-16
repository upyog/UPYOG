import { CardLabel, CardLabelDesc, Dropdown, FormStep, UploadFile, LinkButton } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import Timeline from "../../components/TLTimeline";
import { stringReplaceAll } from "../../utils";
// import { CardLabel, LabelFieldPair, Dropdown, TextInput, LinkButton, CardLabelError, Loader, DeleteIcon } from "@upyog/digit-ui-react-components";


const TransferProof = ({ t, config, onSelect, userType, formData }) => {
  //let index = window.location.href.charAt(window.location.href.length - 1);
  const { pathname: url } = useLocation();
  const isMutation = url.includes("property-mutation");
  const { data, isLoading } = Digit.Hooks.pt.useMDMS(Digit.ULBService.getStateId(), "PropertyTax", "ReasonForTransfer", {});


  let index = window.location.href.split("/").pop();
  const [uploadedFile, setUploadedFile] = useState(formData?.[config.key]?.fileStoreId || null);
  const [file, setFile] = useState([]);
  const [error, setError] = useState(null);
  const cityDetails = Digit.ULBService.getCurrentUlb();

  const [dropdownValue, setDropdownValue] = useState(formData?.[config.key]?.documentType || null);
  let dropdownData = [];
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const { data: Documentsob } = Digit.Hooks.pt.useMDMS(stateId, "PropertyTax", "MutationDocuments");
  const docs = Documentsob?.PropertyTax?.MutationDocuments;
  const transferReason = Array.isArray(docs) && docs.filter((doc) => doc.code.includes("OWNER.TRANSFERREASONDOCUMENT"));

  if (transferReason.length > 0) {
    dropdownData = transferReason[0]?.dropdownData;
    // .filter((e) => e.code.includes(formData?.additionalDetails?.reasonForTransfer?.code));
    dropdownData.forEach((data) => {
      data.i18nKey = stringReplaceAll(data.code, ".", "_");
    });
  }
  const [fields, setFields] = useState(
    formData?.transferReasonProof || [
      {
        "fileStoreId": "",
        "fileError": "",
        "file": "",
        "documentType": {
          "code": "",
          "active": true,
          "i18nKey": ""
        }
      },
    ]
  );

  function setTypeOfDropdownValue(i,value) {
    let units = [...fields];
    units[i].documentType = value;
    units[i].fileStoreId = "";
    setFields(units);
  }

  // const [transferReasonProof, setTransferReasonProof] = useState(
  //   formData?.transferReasonProof || [
  //     {
  //       "fileStoreId": "",
  //       "file": "",
  //       "documentType": {
  //         "code": "",
  //         "active": true,
  //         "i18nKey": ""
  //       }
  //     },
  //   ]
  // );

  const handleAddDoc = () => {
    const values = [...fields];
    values.push({
      "fileStoreId": "",
      "fileError": "",
      "file": "",
      "documentType": {
        "code": "",
        "active": true,
        "i18nKey": ""
      }  
    });
    setFields(values);
    
  };

  const handleSubmit = () => {
    let fileStoreId = uploadedFile;
    let fileDetails = [];
    let optAr = [];
    
    if(fields && fields.length>0) {
      fields.map((e,i)=>{
        let obj = {
          fileStoreId: e?.fileStoreId || "",
          documentType: e?.documentType || ""
        }
        fileDetails.push(obj);
        if(e?.documentType?.code) {
          
          let code = e?.documentType?.code.split(".").pop();
          if (data) {
            let opt;
            data.PropertyTax.ReasonForTransfer.map((el) => {
                if(code == el.code){
                  el.i18nKey = "PROPERTYTAX_REASONFORTRANSFER_" + el.code;
                  opt = el;
                }
                
              })
              optAr.push(opt)
          }
          

        }
      })
    }
    // onSelect("additionalDetails", { reasonForTransfer:optAr });
    onSelect(config.key, fileDetails, "", index, null, null, { reasonForTransfer:optAr });
  };

  const onSkip = () => onSelect();

  async function selectfile(i,e) {
    if(e) {
      let file = e.target.files[0]
      if (file) {
        if (file.size >= 2000000) {
          setError(t("PT_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
          setFields((prevFiles) => {
            const files = [...prevFiles];
            files[i].file =  "";
            files[i].fileStoreId = "";
            files[i].fileError =  t("PT_MAXIMUM_UPLOAD_SIZE_EXCEEDED");
            return files;
          });
        } else {
          try {
            const response = await Digit.UploadServices.Filestorage("property-upload", file, Digit.ULBService.getStateId());
            if (response?.data?.files?.length > 0) {
              setFields((prevFiles) => {
                const files = [...prevFiles];
                files[i].file = e.target.files[0];
                files[i].fileStoreId = response?.data?.files[0]?.fileStoreId;
                files[i].fileError =  "";
                return files;
              });
            } else {
              setFields((prevFiles) => {
                const files = [...prevFiles];
                files[i].file =  "";
                files[i].fileStoreId = "";
                files[i].fileError =  t("PT_FILE_UPLOAD_ERROR");
                return files;
              });
            }
          } catch (err) {}
        }
      }
    }
  }
  function onDeleteFile (i, e){
    setFields((prevFiles) => {
      const files = [...prevFiles];
      files[i].file = "";
      files[i].fileStoreId = "";
      files[i].fileError =  "";
      return files;
    });
  }

  useEffect(() => {
    (async () => {
      setError(null);
      if (file) {
        if (file.size >= 2000000) {
          setError(t("PT_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
        } else {
          try {
            const response = await Digit.UploadServices.Filestorage("property-upload", file, Digit.ULBService.getStateId());
            if (response?.data?.files?.length > 0) {
              setUploadedFile(response?.data?.files[0]?.fileStoreId);
            } else {
              setError(t("PT_FILE_UPLOAD_ERROR"));
            }
          } catch (err) {}
        }
      }
    })();
  }, [file]);

  return (
    <React.Fragment>
      <Timeline currentStep={3} flow="PT_MUTATE" />
      <FormStep config={config} onSelect={handleSubmit} onSkip={onSkip} t={t} >
        {/* <CardLabelDesc>{t(`PT_UPLOAD_RESTRICTIONS_TYPES`)}</CardLabelDesc> */}
        <CardLabelDesc>{t(`PT_UPLOAD_RESTRICTIONS_SIZE`)}</CardLabelDesc>

        {fields?.map((transferReasonP, index) => (
        <div>
          <CardLabel>{`${t("PT_CATEGORY_DOCUMENT_TYPE")}`}</CardLabel>
          <Dropdown
            t={t}
            isMandatory={false}
            option={dropdownData}
            selected={transferReasonP.documentType}
            optionKey="i18nKey"
            select={(e) => setTypeOfDropdownValue(index, e)}
            placeholder={t(`PT_MUTATION_SELECT_DOC_LABEL`)}
          />
          <UploadFile
            id={"ptm-doc"}
            extraStyleName={"propertyCreate"}
            accept=".jpg,.png,.pdf"
            file={transferReasonP?.file}
            onUpload={(e) => selectfile(index, e)}
            onDelete={(e) => onDeleteFile(index, "")}
            message={transferReasonP?.fileStoreId ? `1 ${t(`PT_ACTION_FILEUPLOADED`)}` : t(`PT_ACTION_NO_FILEUPLOADED`)}
            error={transferReasonP.fileError}
          />
          {transferReasonP.fileError ? <div style={{ height: "20px", width: "100%", fontSize: "20px", color: "red", marginTop: "5px" }}>{transferReasonP.fileError}</div> : ""}
          <div style={{ disabled: "true", height: "20px", width: "100%" }}></div>
        </div>
      ))}
      <LinkButton label={t("Add Document")} onClick={handleAddDoc} style={{ color: "orange", width: "175px" }}></LinkButton>
        
        {/* <CardLabel>{`${t("PT_CATEGORY_DOCUMENT_TYPE")}`}</CardLabel>
        <Dropdown
          t={t}
          isMandatory={false}
          option={dropdownData}
          selected={dropdownValue}
          optionKey="i18nKey"
          select={setTypeOfDropdownValue}
          placeholder={t(`PT_MUTATION_SELECT_DOC_LABEL`)}
        />
        <UploadFile
          id={"ptm-doc"}
          extraStyleName={"propertyCreate"}
          accept=".jpg,.png,.pdf"
          onUpload={selectfile}
          onDelete={() => {
            setUploadedFile(null);
          }}
          message={uploadedFile ? `1 ${t(`PT_ACTION_FILEUPLOADED`)}` : t(`PT_ACTION_NO_FILEUPLOADED`)}
          error={error}
        />
        {error ? <div style={{ height: "20px", width: "100%", fontSize: "20px", color: "red", marginTop: "5px" }}>{error}</div> : ""}
        <div style={{ disabled: "true", height: "20px", width: "100%" }}></div> */}
      </FormStep>
    </React.Fragment>
  );
};

export default TransferProof;
