import { DownwardArrow, Rating, UpwardArrow } from "@nudmcdgnpm/digit-ui-react-components";
import React, { Fragment, useContext, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import FilterContext from "./FilterContext";
//import {ReactComponent as Arrow_Downward} from "../images/Arrow_Downward.svg";
import { ArrowDownwardElement } from "./ArrowDownward";
import { ArrowUpwardElement } from "./ArrowUpward";

const MetricData = ({ t, data, code, indexValuesWithStar }) => {
  const { value } = useContext(FilterContext);
  const insight = data?.insight?.value?.replace(/[+-]/g, "")?.split("%");

  return (
    <div>
      <p className="heading-m" style={{ paddingTop: "0px", whiteSpace: "nowrap" }}>
        {indexValuesWithStar?.includes(code) ? (
          <Rating toolTipText={t("COMMON_RATING_LABEL")} currentRating={Math.round(data?.headerValue * 10) / 10} styles={{ width: "unset", marginBottom:"unset" }} starStyles={{ width: "25px" }} />
        ) : data?.headerName.includes("AVG") ? (
          `${Digit.Utils.dss.formatter(data?.headerValue, data?.headerSymbol, "Unit", true)} ${
             code === "fsmtotalsludgetreated" || code === "totalSludgeTreated" ? t(`DSS_KL`) : ""
          }`
        ):
        data?.headerName.includes("DSS_STATE_GDP_REVENUE_COLLECTION")
       ? Digit.Utils.dss.formatter(data.headerValue, data.headerSymbol, "UnitGDP", true, t):
        
        (
          `${Digit.Utils.dss.formatter(data?.headerValue, data?.headerSymbol, value?.denomination, true, t)} ${
            code === "fsmtotalsludgetreated" || code === "totalSludgeTreated"? t(`DSS_KL`) : ""
          }`
        )}
      </p>
      {/* {data?.insight && (
        <div
          style={{
            width: "100%",
            display: "flex",
            
          }}
        >
          {data?.insight?.indicator === "upper_green" ? ArrowUpwardElement("10px") : ArrowDownwardElement("10px")}
          <p className={`${data?.insight.colorCode}`} style={{ whiteSpace: "pre" }}>
            {insight?.[0] &&
              `${Digit.Utils.dss.formatter(insight[0], "number", value?.denomination, true, t)}% ${t(
                Digit.Utils.locale.getTransformedLocale("DSS" + insight?.[1] || "")
              )}`}
          </p>
        </div>
      )} */}
    </div>
  );
};

const MetricChartRow = ({ data, setChartDenomination, index, moduleCode, indexValuesWithStar,imageSrc }) => {
  const { id, chartType } = data;
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { t } = useTranslation();
  const { value } = useContext(FilterContext);
  const [showDate, setShowDate] = useState({});
  const isMobile = window.Digit.Utils.browser.isMobile();
  const { isLoading, data: response } = Digit.Hooks.dss.useGetChart({
    key: id,
    type: chartType,
    tenantId,
    requestDate: { ...value?.requestDate, startDate: value?.range?.startDate?.getTime(), endDate: value?.range?.endDate?.getTime() },
    filters: value?.filters,
    moduleLevel: value?.moduleLevel || moduleCode,
  });

  useEffect(() => {
    if (response) {
      let plots = response?.responseData?.data?.[0]?.plots || null;
      if (plots && Array.isArray(plots) && plots.length > 0 && plots?.every((e) => e.value))
        setShowDate((oldstate) => ({
          ...oldstate,
          [id]: {
            todaysDate: Digit.DateUtils.ConvertEpochToDate(plots?.[0]?.value),
            lastUpdatedTime: Digit.DateUtils.ConvertEpochToTimeInHours(plots?.[1]?.value),
          },
        }));
      index === 0 && setChartDenomination(response?.responseData?.data?.[0]?.headerSymbol);
      if (response?.responseData?.visualizationCode.includes("todaysLastYearCollectionv3")) {

        const today = new Date();
        const previousYearDate = new Date(today.getFullYear() - 1, today.getMonth(), today.getDate());
        const previousYear = previousYearDate.getFullYear();
        const previousMonth = previousYearDate.getMonth() + 1; // Month is zero-based, so adding 1
        const previousDay = previousYearDate.getDate();
        const formattedPreviousYearDate = `${previousDay < 10 ? '0' + previousDay : previousDay}/${previousMonth < 10 ? '0' + previousMonth : previousMonth}/${previousYear}`;
       setShowDate(oldstate=>({...oldstate,[id]:{
         todaysDate: formattedPreviousYearDate,
         lastUpdatedTime: "",
       }}));
     }
     if(response?.responseData?.visualizationCode == "StateGDPwiseTotalRevenueCollection")
     {
      response.responseData.data[0].headerSymbol = "amount"
     }
    } else {
      
      setShowDate({});
    }
  }, [response]);
  useEffect(() => {
    if (response) {
     if(response?.responseData?.visualizationCode == "StateGDPwiseTotalRevenueCollection")
     {
      response.responseData.data[0].headerSymbol = "amount"
     }
     
    } 
  }, []);

  if (isLoading) {
    return false;
  }

  if (!response) {
    return (
      <div className="row" style={{"width":"40%", margin:"10%"}}>
        <div className={`tooltip`}>
          {t(data.name)}
          <span
            className="tooltiptext"
            style={{
              fontSize: "medium",
              width: t(`TIP_${data.name}`).length < 50 ? "fit-content" : 400,
              height: 50,
              whiteSpace: "normal",
            }}
          >
            <span style={{ fontWeight: "500", color: "white" }}>{t(`TIP_${data.name}`)}</span>
          </span>
         
        </div>
        <span style={{ whiteSpace: "pre" }}>{t("DSS_NO_DATA")}</span>
      </div>
    );
  }
  let name = t(data?.name) || "";

  const getWidth = (data) => {
    if (isMobile) return "auto";
    else return t(`TIP_${data.name}`).length < 50 ? "fit-content" : 400;
    // if (isMobile) return t(`TIP_${data.name}`).length < 50 ? "fit-content" : 300;
    // else return t(`TIP_${data.name}`).length < 50 ? "fit-content" : 400;
  };

  const getHeight = (data) => {
    if (isMobile) return "auto";
    else return 50;
    // if (isMobile) return t(`TIP_${data.name}`).length < 50 ? 50 : "auto";
    // else return 50;
  };
  return (
    <div className="row" style={{display:"flex",flexDirection:"column",width:"45%", height:"100px",margin:"2%",padding:"2%",backgroundColor:"white",boxShadow:"0 14px 28px rgba(0,0,0,0.25), 0 10px 10px rgba(0,0,0,0.22)"}}>
      <div style={{display:"flex"}}>
        <div style={{width:"85%"}}>
      <div className={`tooltip`}>
        {typeof name == "string" && name}
        {Array.isArray(name) && name?.filter((ele) => ele)?.map((ele) => <div style={{ whiteSpace: "pre" }}>{ele}</div>)}
        <span className="dss-white-pre" >
          {" "}
          {showDate?.[id]?.todaysDate}
        </span>
        <span
          className="tooltiptext"
          style={{
            fontSize: "medium",
            width: getWidth(data),
            height: getHeight(data),
            whiteSpace: "normal",
          }}
        >
          <span style={{ fontWeight: "500", color: "white" }}>{t(`TIP_${data.name}`)}</span>
          <span style={{ color: "white" }}> {showDate?.[id]?.lastUpdatedTime}</span>
        </span>
      </div>
      <MetricData t={t} data={response?.responseData?.data?.[0]} code={response?.responseData?.visualizationCode} indexValuesWithStar={indexValuesWithStar} />
      {/* <div>{`${displaySymbol(response.headerSymbol)} ${response.headerValue}`}</div> */}
      </div>
      <div style={{width:"15%"}}><a href="https://imgbb.com/"><img src={imageSrc} alt="8" border="0" style={{width:"100%", height :"100%"}} /></a>
      </div>
      </div>
    </div>
  );
};

const MetricChartNew = ({ data, setChartDenomination, moduleCode }) => {
  const { charts } = data;
  const indexValuesWithStar = [
    "citizenAvgRating",
    "nssOverviewCitizenFeedbackScore",
    "nssPtCitizenFeedbackScore",
    "sdssPtCitizenFeedbackScore",
    "sdssOverviewCitizenFeedbackScore",
    "AvgCitizenRating"
  ];

  let url =["https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/7.png","https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/11.png","https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/8.png","https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/12.png","https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/9.png","https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/13.png","https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/10.png","https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/14.png"]

  let url2=["https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/2.png","https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/3.png","https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/1.png","https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/4.png","https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/6.png","https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/6.png"]
  return (
    <>
      <span className="chart-metric-wrapper">
        {charts.map((chart, index) => (
          <MetricChartRow data={chart} key={index} index={index} moduleCode={moduleCode} setChartDenomination={setChartDenomination} indexValuesWithStar={indexValuesWithStar} imageSrc={charts.length == 8 ?url[index]:url2[index]}/>
        ))}
      </span>
    </>
  );
};

export default MetricChartNew;
