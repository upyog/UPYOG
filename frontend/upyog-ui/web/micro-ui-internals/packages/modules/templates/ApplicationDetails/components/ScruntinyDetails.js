import { StatusTable, Row, PDFSvg, CardLabel, CardSubHeader } from "@nudmcdgnpm/digit-ui-react-components";
import React, { Fragment } from "react";
import { useTranslation } from "react-i18next";

const ScruntinyDetails = ({ scrutinyDetails, paymentsList=[] }) => {
  const { t } = useTranslation();
  let count = 0;
  const getTextValues = (data) => {
    if (data?.value && data?.isTransLate) return <span className="tmpl-scruntiny-details-wrapper">{t(data?.value)}</span>;
    else if (data?.value && data?.isTransLate) return t(data?.value);
    else if (data?.value) return data?.value;
    else t("NA");
  }
  return (
    <Fragment>
      {!scrutinyDetails?.isChecklist && <div className="tmpl-scruntiny-details-container-padding">
        <StatusTable>
          <div>
            {scrutinyDetails?.values?.map((value, index) => {
              if (value?.isUnit) return <Row className="border-none" textStyle={value?.value === "Paid"?{color:"darkgreen"}:(value?.value === "Unpaid"?{color:"red"}:{})} key={`${value.title}`} label={`${t(`${value.title}`)}`} text={value?.value ? `${getTextValues(value)} ${t(value?.isUnit)}` : t("NA")} labelStyle={value?.isHeader ? {fontSize: "20px"} : {}}/>
              else if (value?.isHeader && !value?.isUnit) return <CardSubHeader className="tmpl-scruntiny-details-header">{t(value?.title)}</CardSubHeader>
              else if (value?.isSubTitle && !value?.isUnit) return <CardSubHeader className="tmpl-scruntiny-details-header-2">{t(value?.title)}</CardSubHeader>
              else return <Row className="border-none" textStyle={value?.value === "Paid"?{color:"darkgreen", wordBreak: "break-all"}:(value?.value === "Unpaid"?{color:"red", wordBreak: "break-all"}:{wordBreak: "break-all"})} key={`${value.title}`} label={`${t(`${value.title}`)}`} text={getTextValues(value)} labelStyle={value?.isHeader ? {fontSize: "20px"} : {}}/>
            })}
            {scrutinyDetails?.permit?.map((value,ind) => {
              return <CardLabel className="tmpl-scruntiny-details-card">{value?.title}</CardLabel>
            })}
          </div>
          <div>
            {scrutinyDetails?.scruntinyDetails?.map((report, index) => {
              return (
                <Fragment>
                  <Row className="border-none" label={`${t(report?.title)}`} labelStyle={{width:"150%"}} />
                  <a href={report?.value}> <PDFSvg /> </a>
                  <p className="tmpl-scruntiny-details-spacing">{t(report?.text)}</p>
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