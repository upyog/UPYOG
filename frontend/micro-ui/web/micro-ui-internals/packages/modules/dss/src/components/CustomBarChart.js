import { Loader } from "@egovernments/digit-ui-react-components";
import React, { Fragment, useContext, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { Bar, BarChart, CartesianGrid, ResponsiveContainer, XAxis, YAxis } from "recharts";
import FilterContext from "./FilterContext";
import NoData from "./NoData";
import { checkCurrentScreen } from "./DSSCard";

const formatValue = (value, symbol) => {
  if (symbol?.toLowerCase() === "percentage") {
    /*   Removed by  percentage formatter.
    const Pformatter = new Intl.NumberFormat("en-IN", { maximumSignificantDigits: 3 });
    return `${Pformatter.format(Number(value).toFixed(2))}`;
    */
   
    return `${Number(value).toFixed()}`;
  } else {
    return value;
  }
};

const CustomLabel = ({ x, y, name, stroke, value, maxValue ,data}) => {
  console.log("hhhhhh",maxValue,data)
  const currencyFormatter = new Intl.NumberFormat("en-IN", { currency: "INR" });
  const { t } = useTranslation();
  let possibleValues = ["pttopPerformingStatesRevenue","ptbottomPerformingStatesRevenue","tltopPerformingStatesRevenue","tlbottomPerformingStatesRevenue","obpstopPerformingStatesRevenue","obpsbottomPerformingStatesRevenue","noctopPerformingStatesRevenue","nocbottomPerformingStatesRevenue","wstopPerformingStatesRevenue","wsbottomPerformingStatesRevenue"]
if(data?.tabName == "Revenue" || possibleValues.includes(data?.id))
{
  console.log("reee",maxValue,name)
  Object.keys(maxValue)?.forEach(key => {
    console.log("ddddd",maxValue[key])
    if(maxValue[key] > 10000000)
    maxValue[key] =  `${((maxValue[key] / 1000000000).toFixed(2) || 0)}`;
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
        {`₹ ${maxValue?.[t(name)]} ${t("ES_DSS_CR")}`}
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
    setChartDenomination(response?.responseData?.data?.[0]?.headerSymbol);
    const dd = response?.responseData?.data?.map((bar) => {
      let plotValue = bar?.plots?.[0].value || 0;
      return {
        name: t(bar?.plots?.[0].name),
        value: formatValue(plotValue, bar?.plots?.[0].symbol),
        // value: Digit.Utils.dss.formatter(plotValue, bar?.plots?.[0].symbol),
      };
    });
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
      <ResponsiveContainer width="98%" height={url.includes("drilldown")?730:350}>
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
