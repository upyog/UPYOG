import { StatusTable, Row, PDFSvg, CardLabel, CardSubHeader,TextInput,TextArea,UploadFile } from "@upyog/digit-ui-react-components";
import React, { Fragment,useEffect,useState } from "react";
import { useTranslation } from "react-i18next";

const ScruntinyDetails = ({ scrutinyDetails, paymentsList=[],additionalDetails,applicationData }) => {
  const isEditApplication =  (window.location.href.includes("editApplication") || applicationData?.status=="FIELDINSPECTION_INPROGRESS") && window.location.href.includes("bpa") ;
  const [development, setDevelopment] = useState()
  const [otherCharges, setOtherCharges] = useState()
  const [lessAdjusment, setLessAdjusment] = useState()
  const state = Digit.ULBService.getStateId();
  const [otherChargesDisc, setOtherChargesDisc] = useState()
  const [uploadedFile, setUploadedFile] = useState();
  const [uploadedFileLess, setUploadedFileLess] = useState([]);
  const [file, setFile] = useState();
  const [uploadMessage, setUploadMessage] = useState("");
  const [errorFile, setError] = useState(null);
  const [docLessAdjustment, setDocuments] = useState({});
  let acceptFormat = ".pdf"
  const styles = {
    buttonStyle: { display: "flex", justifyContent: "flex-start", color: "#a82227" },
    headerStyle: {
      marginTop: "10px",
      fontSize: "16px",
      fontWeight: "700",
      lineHeight: "24px",
      color: " rgba(11, 12, 12, var(--text-opacity))",
    },
  };
  const [showSanctionFee, setShowSanctionFee] = useState(false);

  useEffect(() => {
    (async () => {
      setError(null);
      if (file&& file?.type) {
        if(!(acceptFormat?.split(",")?.includes(`.${file?.type?.split("/")?.pop()}`)))
        {
          setError(t("PT_UPLOAD_FORMAT_NOT_SUPPORTED"));
        }
        else if (file.size >= 2000000) {
          setError(t("PT_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
        } else {
          try {
            const response = await Digit.UploadServices.Filestorage("property-upload", file, Digit.ULBService.getStateId());
            if (response?.data?.files?.length > 0) {
              setUploadedFileLess([...uploadedFileLess,{fileStoreId:response?.data?.files[0]?.fileStoreId, time:new Date()}]);
            } else {
              setError(t("PT_FILE_UPLOAD_ERROR"));
            }
          } catch (err) {
          }
        }
      }
    })();        
  }, [file]);


  useEffect(()=>{
    sessionStorage.setItem("uploadedFileLess",JSON.stringify(uploadedFileLess));
},[uploadedFileLess])

  useEffect(()=>{   
    if (additionalDetails?.lessAdjustmentFeeFiles?.length) {
      const fileStoresIds = additionalDetails?.lessAdjustmentFeeFiles.map((document,index) =>(index===additionalDetails?.lessAdjustmentFeeFiles?.length-1 ? additionalDetails?.lessAdjustmentFeeFiles[additionalDetails?.lessAdjustmentFeeFiles?.length-1]?.fileStoreId : null));
      Digit.UploadServices.Filefetch(fileStoresIds, state).then((res) => setDocuments(res?.data));
    } 
      setDevelopment(additionalDetails?.selfCertificationCharges?.BPA_DEVELOPMENT_CHARGES);
      sessionStorage.setItem("development",additionalDetails?.selfCertificationCharges?.BPA_DEVELOPMENT_CHARGES);
      setOtherCharges(additionalDetails?.selfCertificationCharges?.BPA_OTHER_CHARGES);
      sessionStorage.setItem("otherCharges",additionalDetails?.selfCertificationCharges?.BPA_OTHER_CHARGES);
      setLessAdjusment(additionalDetails?.selfCertificationCharges?.BPA_LESS_ADJUSMENT_PLOT);
      sessionStorage.setItem("lessAdjusment",additionalDetails?.selfCertificationCharges?.BPA_LESS_ADJUSMENT_PLOT);
      setOtherChargesDisc(additionalDetails?.otherFeesDiscription);
      sessionStorage.setItem("otherChargesDisc",additionalDetails?.otherFeesDiscription);
      setUploadedFileLess(additionalDetails?.lessAdjustmentFeeFiles);
      },[additionalDetails])

  const { t } = useTranslation();
  let count = 0;
  const getTextValues = (data) => {
    if (data?.value && data?.isTransLate) return <span style={{color: "#00703C"}}>{t(data?.value)}</span>;
    else if (data?.value && data?.isTransLate) return t(data?.value);
    else if (data?.value) return data?.value;
    else t("NA");
  }
  function setOtherChargesVal(value) {
    if(/^[0-9]*$/.test(value)){
      setOtherCharges(value);
      sessionStorage.setItem("otherCharges",value)
    }
    else{
      //alert("Please enter numbers")
    }      
}
  function setDevelopmentVal(value) {
    if(/^[0-9]*$/.test(value)){
      setDevelopment(value);
      sessionStorage.setItem("development",value)
    }
    else{
      //alert("Please enter numbers")
    }      
  }
  function setLessAdjusmentVal(value) {
  if(/^[0-9]*$/.test(value)){
    setLessAdjusment(value);
    sessionStorage.setItem("lessAdjusment",value)
  }
  else{
    //alert("Please enter numbers")
  }      
  }
  function setOtherChargesDis(value) {
    setOtherChargesDisc(value);
    sessionStorage.setItem("otherChargesDisc",value)  ;
}

function selectfile(e) {
    setUploadedFile(e.target.files[0]);
    setFile(e.target.files[0]);
 }

  return (
    <Fragment>
      {!scrutinyDetails?.isChecklist && <div style={{ background: "#FAFAFA", border: "1px solid #D6D5D4", padding: "8px", borderRadius: "4px", maxWidth: "950px" }}>
        <StatusTable>
          <div>
            {scrutinyDetails?.values?.map((value, index) => {
              if (value?.isUnit) return <Row className="border-none" textStyle={value?.value === "Paid"?{color:"darkgreen"}:(value?.value === "Unpaid"?{color:"red"}:{})} key={`${value.title}`} label={`${t(`${value.title}`)}`} text={value?.value ? `${getTextValues(value)} ${t(value?.isUnit)}` : t("NA")} labelStyle={value?.isHeader ? {fontSize: "20px"} : {}}/>
              else if (value?.isHeader && !value?.isUnit) return <CardSubHeader style={{fontSize: "20px", paddingBottom: "10px"}}>{t(value?.title)}</CardSubHeader>
              else if (value?.isSubTitle && !value?.isUnit) return <CardSubHeader style={{fontSize: "20px", paddingBottom: "10px", margin: "0px"}}>{t(value?.title)}</CardSubHeader>
              else return <Row className="border-none" textStyle={value?.value === "Paid"?{color:"darkgreen", wordBreak: "break-all"}:(value?.value === "Unpaid"?{color:"red", wordBreak: "break-all"}:{wordBreak: "break-all"})} key={`${value.title}`} label={`${t(`${value.title}`)}`} text={getTextValues(value)} labelStyle={value?.isHeader ? {fontSize: "20px"} : {}}/>
            })}
            {scrutinyDetails?.permit?.map((value,ind) => {
              return <CardLabel style={{fontWeight:"400"}}>{value?.title}</CardLabel>
            })}
          </div>
          {window.location.href.includes("employee") && scrutinyDetails?.values[0]?.title=="BPA_APPL_FEES_DETAILS" && 
          <div>
          {!showSanctionFee && (
         <div style={styles.buttonStyle}>
          <button
            type="button"
            onClick={() => {
              setShowSanctionFee(true);
            }}
          >
            {t("SHOW_P2_FEES_DETAILS")}
          </button>
        </div>
      )}
      {showSanctionFee && (
        <div style={styles.buttonStyle}>
          <button
            type="button"
            onClick={() => {
              setShowSanctionFee(false);
            }}
          >
            {t("HIDE_P2_FEES_DETAILS")}
          </button>
        </div>
      )}
      {showSanctionFee &&
      <div>
          <CardSubHeader>{t("BPA_P2_SUMMARY_FEE_EST")}</CardSubHeader>        
            <Row className="border-none" label={t(`BPA_COMMON_MALBA_AMT`)} text={`₹ ${additionalDetails?.selfCertificationCharges?.BPA_MALBA_CHARGES}`} />
            <Row className="border-none" label={t(`BPA_COMMON_LABOUR_AMT`)} text={`₹ ${additionalDetails?.selfCertificationCharges?.BPA_LABOUR_CESS}`} />
            <Row className="border-none" label={t(`BPA_COMMON_WATER_AMT`)} text={`₹ ${additionalDetails?.selfCertificationCharges?.BPA_WATER_CHARGES}`} />
            <Row className="border-none" label={t(`BPA_COMMON_GAUSHALA_AMT`)} text={`₹ ${additionalDetails?.selfCertificationCharges?.BPA_GAUSHALA_CHARGES_CESS}`} />
            <CardSubHeader>{t("BPA_P2_SUMMARY_FEE_EST_MANUAL")}</CardSubHeader>
            <CardLabel>{t("BPA_COMMON_DEVELOPMENT_AMT")}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="development"
              defaultValue={additionalDetails?.selfCertificationCharges?.BPA_DEVELOPMENT_CHARGES || ""}
              value={development}
              disabled={!isEditApplication}
              onChange={(e) => {setDevelopmentVal(e.target.value)}}
              {...{ required: true, pattern: "^[0-9]*$", type: "text" }}
              />
              <CardLabel>{t("BPA_COMMON_OTHER_AMT")}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="otherCharges"
              defaultValue={additionalDetails?.selfCertificationCharges?.BPA_OTHER_CHARGES || ""}
              value={otherCharges}
              disabled={!isEditApplication}
              onChange={(e) => {setOtherChargesVal(e.target.value)}}
              {...{ required: true, pattern: "^[0-9]*$", type: "text" }}
            />
            <CardLabel>{t("BPA_COMMON_OTHER_AMT_DISCRIPTION")}</CardLabel>
            <TextArea
              t={t}
              type={"text"}
              name="otherChargesDiscription"
              defaultValue={additionalDetails?.selfCertificationCharges?.BPA_COMMON_OTHER_AMT_DISCRIPTION }
              value={otherChargesDisc}
              disabled={!isEditApplication}
              onChange={(e) => {setOtherChargesDis(e.target.value)}}
              {...{ required: true }}
            />
            <CardLabel>{t("BPA_COMMON_LESS_AMT")}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="lessAdjusment"
              defaultValue={additionalDetails?.selfCertificationCharges?.BPA_LESS_ADJUSMENT_PLOT || ""}
              value={lessAdjusment}
              disabled={!isEditApplication}
              onChange={(e) => {setLessAdjusmentVal(e.target.value)}}
              {...{ required: true, pattern: "^[0-9]*$", type: "text" }}
            />
            <CardLabel>{t("BPA_COMMON_LESS_AMT_FILE")}</CardLabel>
            <UploadFile
                id={"noc-doc"}
                style={{marginBottom:"200px"}}
                onUpload={selectfile}
                disabled={!isEditApplication}
                onDelete={() => {
                    setUploadedFile(null);
                    setFile("");
                }}
                message={uploadedFile ? `1 ${t(`FILEUPLOADED`)}` : t(`ES_NO_FILE_SELECTED_LABEL`)}
                error={errorFile}
                uploadMessage={uploadMessage}
            />
            {docLessAdjustment?.fileStoreIds?.length && 
            <CardLabel style={{marginTop:"15px"}}>{t("BPA_COMMON_LESS_AMT_PREVIOUS_FILE")}</CardLabel>            
            }
            {docLessAdjustment?.fileStoreIds?.length &&             
              <a   target="_blank" href={docLessAdjustment?.fileStoreIds[docLessAdjustment?.fileStoreIds?.length-1]?.url}>
              <PDFSvg />
            </a>
            }            
            <Row className="border-none"></Row>
       <Row  className="border-none" label={t(`BPA_P2_TOTAL_FEE`)} text={`₹ ${(parseInt(development)+parseInt(otherCharges)+parseInt(additionalDetails?.selfCertificationCharges?.BPA_MALBA_CHARGES)+parseInt(additionalDetails?.selfCertificationCharges?.BPA_LABOUR_CESS)+parseInt(additionalDetails?.selfCertificationCharges?.BPA_WATER_CHARGES)+parseInt(additionalDetails?.selfCertificationCharges?.BPA_GAUSHALA_CHARGES_CESS))-parseInt(lessAdjusment)}`} />
            </div>}
          </div>
          }          
          <div>
            {scrutinyDetails?.scruntinyDetails?.map((report, index) => {
              return (
                <Fragment>
                  <Row className="border-none" label={`${t(report?.title)}`} labelStyle={{width:"150%"}} />
                  <a href={report?.value}> <PDFSvg /> </a>
                  <p style={{ margin: "8px 0px", fontWeight: "bold", fontSize: "16px", lineHeight: "19px", color: "#505A5F" }}>{t(report?.text)}</p>
                </Fragment>
              )
            })}
          </div>
        </StatusTable>
      </div>}
    </Fragment>
  )
}

export default ScruntinyDetails;