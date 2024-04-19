import { StatusTable, Row, PDFSvg, CardLabel, CardSubHeader,TextInput  } from "@upyog/digit-ui-react-components";
import React, { Fragment,useEffect,useState } from "react";
import { useTranslation } from "react-i18next";

const ScruntinyDetails = ({ scrutinyDetails, paymentsList=[],additionalDetails }) => {
  const isEditApplication =  window.location.href.includes("editApplication") && window.location.href.includes("bpa") ;
  const [development, setDevelopment] = useState()
  const [otherCharges, setOtherCharges] = useState()
  const [lessAdjusment, setLessAdjusment] = useState()
  useEffect(()=>{    
      setDevelopment(additionalDetails?.selfCertificationCharges?.BPA_DEVELOPMENT_CHARGES);
      sessionStorage.setItem("development",additionalDetails?.selfCertificationCharges?.BPA_DEVELOPMENT_CHARGES);
      setOtherCharges(additionalDetails?.selfCertificationCharges?.BPA_OTHER_CHARGES);
      sessionStorage.setItem("otherCharges",additionalDetails?.selfCertificationCharges?.BPA_OTHER_CHARGES);
      setLessAdjusment(additionalDetails?.selfCertificationCharges?.BPA_LESS_ADJUSMENT_PLOT);
      sessionStorage.setItem("lessAdjusment",additionalDetails?.selfCertificationCharges?.BPA_LESS_ADJUSMENT_PLOT);
      },[additionalDetails])

  const { t } = useTranslation();
  let count = 0;
  const getTextValues = (data) => {
    if (data?.value && data?.isTransLate) return <span style={{color: "#00703C"}}>{t(data?.value)}</span>;
    else if (data?.value && data?.isTransLate) return t(data?.value);
    else if (data?.value) return data?.value;
    else t("NA");
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
          <CardSubHeader>{t("BPA_P2_SUMMARY_FEE_EST")}</CardSubHeader>        
            <Row className="border-none" label={t(`BPA_COMMON_MALBA_AMT`)} text={`₹ ${additionalDetails?.selfCertificationCharges?.BPA_MALBA_CHARGES}`} />
            <Row className="border-none" label={t(`BPA_COMMON_LABOUR_AMT`)} text={`₹ ${additionalDetails?.selfCertificationCharges?.BPA_LABOUR_CESS}`} />
            <Row className="border-none" label={t(`BPA_COMMON_WATER_AMT`)} text={`₹ ${additionalDetails?.selfCertificationCharges?.BPA_WATER_CHARGES}`} />
            <Row className="border-none" label={t(`BPA_COMMON_GAUSHALA_AMT`)} text={`₹ ${additionalDetails?.selfCertificationCharges?.BPA_GAUSHALA_CHARGES_CESS}`} />
            <CardSubHeader>{t("BPA_P2_SUMMARY_FEE_EST_MANUAL")}</CardSubHeader>
            {/* <Row className="border-none" label={t(`BPA_COMMON_DEVELOPMENT_AMT`)} text={`₹ ${additionalDetails?.selfCertificationCharges?.BPA_DEVELOPMENT_CHARGES}`} />
            <Row className="border-none" label={t(`BPA_COMMON_OTHER_AMT`)} text={`₹ ${additionalDetails?.selfCertificationCharges?.BPA_OTHER_CHARGES}`} />
            <Row className="border-none" label={t(`BPA_COMMON_LESS_AMT`)} text={`₹ ${additionalDetails?.selfCertificationCharges?.BPA_LESS_ADJUSMENT_PLOT}`} /> */}
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
              onChange={(e) => {setDevelopment(e.target.value),sessionStorage.setItem("development",e.target.value)}}
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
              onChange={(e) => {setOtherCharges(e.target.value),sessionStorage.setItem("otherCharges",e.target.value)}}
              //disable={userInfo?.info?.emailId && !isOpenLinkFlow ? true : false}
              //disable={editScreen}
              //{...{ required: true, pattern: "[A-Za-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$", type: "email", title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID") }}
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
              onChange={(e) => {setLessAdjusment(e.target.value),sessionStorage.setItem("lessAdjusment",e.target.value)}}
            />
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