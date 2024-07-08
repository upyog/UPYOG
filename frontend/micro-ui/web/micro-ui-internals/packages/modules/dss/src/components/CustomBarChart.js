import { Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React, { Fragment, useContext, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { Bar, BarChart, CartesianGrid, ResponsiveContainer, XAxis, YAxis } from "recharts";
import FilterContext from "./FilterContext";
import NoData from "./NoData";
import { checkCurrentScreen } from "./DSSCard";

const formatValue = (value, symbol,type) => {
  if (symbol?.toLowerCase() === "percentage") {
    /*   Removed by  percentage formatter.
    const Pformatter = new Intl.NumberFormat("en-IN", { maximumSignificantDigits: 3 });
    return `${Pformatter.format(Number(value).toFixed(2))}`;
    */
   
    return `${Number(value).toFixed()}`;
  }
  else if(type =="revenue")
  {
    return   Number(((value) / 1000000000).toFixed(2) || 0);
  }
  else if(type =="population")
  {
    return   Number(((value) / 100).toFixed(2) || 0);
  }
  else {
    return  Number((value).toFixed(4) || 0);
  }
};
let flag= 0
let flag2=0
const CustomLabel = ({ x, y, name, stroke, value, maxValue ,data}) => {
  console.log("hhhhhh",maxValue,data)

  const currencyFormatter = new Intl.NumberFormat("en-IN", { currency: "INR" });
  const { t } = useTranslation();
  
  let possibleValues = ["pttopPerformingStatesRevenue","ptbottomPerformingStatesRevenue","tltopPerformingStatesRevenue","tlbottomPerformingStatesRevenue","obpstopPerformingStatesRevenue","obpsbottomPerformingStatesRevenue","noctopPerformingStatesRevenue","nocbottomPerformingStatesRevenue","wstopPerformingStatesRevenue","wsbottomPerformingStatesRevenue","OverviewtopPerformingStates","OverviewbottomPerformingStates"]
if( possibleValues.includes(data?.id) ) 
{

  return (
    <>
      <text
        x={x}
        y={y}
        dx={0}
        dy={30}
        fill={stroke}
        width="35"
        style={{ fontSize: "medium", textAlign: "right", fontVariantNumeric: "proportional-nums" }}
      >
        {`₹ ${maxValue?.[t(name)]} ${t("ES_DSS_CR")}`}
      </text>
      <text x={x} y={y} dx={-200} dy={10}>
        {t(`DSS_TB_${Digit.Utils.locale.getTransformedLocale(name)}`)}
      </text>
    </>
  );
}
else if(data?.id.includes("GDP") )
{
  
  Object.keys(maxValue)?.forEach(key => { 
    console.log("reee123",maxValue[key],name)
    maxValue[key] = maxValue[key];
  });
  return (
    <>
      <text
        x={x}
        y={y}
        dx={0}
        dy={30}
        fill={stroke}
        width="35"
        style={{ fontSize: "medium", textAlign: "right", fontVariantNumeric: "proportional-nums" }}
      >
        {`${maxValue?.[t(name)]} %`}
      </text>
      <text x={x} y={y} dx={-200} dy={10}>
        {t(`DSS_TB_${Digit.Utils.locale.getTransformedLocale(name)}`)}
      </text>
    </>
  );
}
else if (data?.id.includes("Population") || data?.id.includes("Household"))
{
  return (
    <>
      <text
        x={x}
        y={y}
        dx={0}
        dy={30}
        fill={stroke}
        width="35"
        style={{ fontSize: "medium", textAlign: "right", fontVariantNumeric: "proportional-nums" }}
      >
        {`₹ ${maxValue?.[t(name)]}`}
      </text>
      <text x={x} y={y} dx={-200} dy={10}>
        {t(`DSS_TB_${Digit.Utils.locale.getTransformedLocale(name)}`)}
      </text>
    </>
  );
}
else {

return (
    <>
      <text
        x={x}
        y={y}
        dx={0}
        dy={30}
        fill={stroke}
        width="35"
        style={{ fontSize: "medium", textAlign: "right", fontVariantNumeric: "proportional-nums" }}
      >
        {`${maxValue?.[t(name)]}`}
      </text>
      <text x={x} y={y} dx={-200} dy={10}>
        {t(`DSS_TB_${Digit.Utils.locale.getTransformedLocale(name)}`)}
      </text>
    </>
  );
}
};
const COLORS = { RED: "#00703C", GREEN: "#D4351C", default: "#00703C" };

const CustomBarChart = ({
  xDataKey = "value",
  xAxisType = "number",
  yAxisType = "category",
  yDataKey = "name",
  hideAxis = true,
  layout = "vertical",
  fillColor = "default",
  showGrid = false,
  showDrillDown = false,
  data,
  title,
  setChartDenomination,
  moduleCode,
}) => {
  const { id } = data;
  const { t } = useTranslation();
  const history = useHistory();
  const { value } = useContext(FilterContext);
  const [maxValue, setMaxValue] = useState({});
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { isLoading, data: response } = Digit.Hooks.dss.useGetChart({
    key: id,
    type: "metric",
    tenantId,
    requestDate: { ...value?.requestDate, startDate: value?.range?.startDate?.getTime(), endDate: value?.range?.endDate?.getTime() },
    filters: value?.filters,
    moduleLevel: value?.moduleLevel || moduleCode,
  });
  const chartData = useMemo(() => {
    if (!response) return null;
    let possibleValues = ["pttopPerformingStatesRevenue","ptbottomPerformingStatesRevenue","tltopPerformingStatesRevenue","tlbottomPerformingStatesRevenue","obpstopPerformingStatesRevenue","obpsbottomPerformingStatesRevenue","noctopPerformingStatesRevenue","nocbottomPerformingStatesRevenue","wstopPerformingStatesRevenue","wsbottomPerformingStatesRevenue","OverviewtopPerformingStates","OverviewbottomPerformingStates"]
   
    setChartDenomination("number");
    const dd = response?.responseData?.data?.map((bar) => {
      let plotValue = bar?.plots?.[0].value || 0;
      let type =""
      if(possibleValues.includes(data?.id))
      {
        type="revenue"
        return {
          name: t(bar?.plots?.[0].name),
          value: formatValue(plotValue, bar?.plots?.[0].symbol,type),
          // value: Digit.Utils.dss.formatter(plotValue, bar?.plots?.[0].symbol),
        };
      }
      else if (data.id.includes("Population") || data.id.includes("Household"))
      {
        type="population"
        return {
          name: t(bar?.plots?.[0].name),
          value: formatValue(plotValue, bar?.plots?.[0].symbol,type),
          // value: Digit.Utils.dss.formatter(plotValue, bar?.plots?.[0].symbol),
        };
      }
      else {

      type="others"
      return {
        name: t(bar?.plots?.[0].name),
        value: formatValue(plotValue, bar?.plots?.[0].symbol,type),
        // value: Digit.Utils.dss.formatter(plotValue, bar?.plots?.[0].symbol),
      };}
    })
    let newMax = Math.max(...dd.map((e) => Number(e.value)));
    let newObj = {};
    let newReturn = dd.map((ele) => {
      newObj[ele.name] = ele.value;
      return { ...ele, value: (Number(ele.value) / newMax) * 100 };
    });
    setMaxValue(newObj);
    return newReturn;
  }, [response]);

  const goToDrillDownCharts = () => {
    history.push(
      `/digit-ui/employee/dss/drilldown?chart=${response?.responseData?.visualizationCode}&ulb=${
        value?.filters?.tenantId
      }&title=${title}&fromModule=${Digit.Utils.dss.getCurrentModuleName()}&type=performing-metric&fillColor=${fillColor}&isNational=${
        checkCurrentScreen() ? "YES" : "NO"
      }`
    );
  };
  if (isLoading) {
    return <Loader />;
  }
  if (chartData?.length === 0 || !chartData) {
    return <NoData t={t} />;
  }
  console.log("Loading chart",data)
  let url=window.location.href
  return (
    <Fragment>
      <ResponsiveContainer width="98%" height={url.includes("drilldown")?730:400}>
        <BarChart
          width="70%"
          height="100%"
          data={showDrillDown ? chartData?.slice(0, 3) : chartData}
          layout={layout}
          maxBarSize={8}
          margin={{ left: 200 }}
          barGap={50}
        >
          {showGrid && <CartesianGrid />}
          <XAxis hide={hideAxis} dataKey={xDataKey} type={xAxisType} domain={[0, 90]} />
          <YAxis dataKey={yDataKey} hide={hideAxis} type={yAxisType} padding={{ right: 60 }} />
          <Bar
            dataKey={xDataKey}
            fill={COLORS[fillColor]}
            background={{ fill: "#D6D5D4", radius: 8 }}
            label={<CustomLabel stroke={COLORS[fillColor]} maxValue={maxValue} data={data}/>}
            radius={[8, 8, 8, 8]}
            isAnimationActive={false}
          />
        </BarChart>
      </ResponsiveContainer>
      {chartData?.length > 3 && showDrillDown && (
        <p className="showMore" onClick={goToDrillDownCharts}>
          {t("DSS_SHOW_MORE")}
        </p>
      )}
    </Fragment>
  );
};

export default CustomBarChart;
