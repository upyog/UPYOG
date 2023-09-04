import { Loader } from "@egovernments/digit-ui-react-components";
import { getDaysInMonth } from "date-fns";
import React, { useContext, useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { Area, AreaChart, CartesianGrid, Legend, Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis ,ComposedChart, Bar} from "recharts";
import FilterContext from "./FilterContext";
import NoData from "./NoData";
const COLORS = ["#048BD0", "#FBC02D", "#8E29BF", "#EA8A3B", "#0BABDE", "#6E8459", "#D4351C", "#0CF7E4", "#F80BF4", "#22F80B"];
const increasedHeightCharts = [
  "nssOBPSTotalPermitsVsTotalOCSubmittedVsTotalOCIssued",
  "nssNOCApplicationVsProvisionalVsActual",
  "nocApplicationVsProvisionalVsActual",
  "permitsandOCissued",
  "cumulativeCollectionOverview"
];
const getColors = (index = 0) => {
  index = COLORS.length > index ? index : 0;
  return COLORS[index];
};

const getDenominatedValue = (denomination, plotValue,plot) => {
  if(plot?.COLLECTIONS_NONTAX || plot?.COLLECTIONS_TAX || plot?.COLLECTIONS)
  {
    return Number((plotValue / 10000000).toFixed(2));
  }
  else {
  switch (denomination) {
    case "Unit":
      return plotValue;
    case "Lac":
      return Number((plotValue / 100000).toFixed(2));
    case "Cr":
      return Number((plotValue / 10000000).toFixed(2));
    default:
      return "";
  }
}
};

const getValue = (plot) => plot.value;

const renderUnits = (t, denomination, symbol) => {
  if (symbol == "percentage") {
    return " %";
  } else if (symbol == "number") {
    return "";
  }
  switch (denomination) {
    case "Unit":
      return `(${t("DSS_UNIT")})`;
    case "Lac":
      return `(${t("DSS_LAC")})`;
    case "Cr":
      return `(${t("DSS_CR")})`;
    default:
      return null;
  }
};

const CustomAreaChart = ({ xDataKey = "name", yDataKey = getValue, data, setChartDenomination }) => {
  const lineLegend = {
    margin: "10px",
  };
  const { t } = useTranslation();
  const { id } = data;
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { value } = useContext(FilterContext);
  const [totalCapacity, setTotalCapacity] = useState(0);
  const [totalWaste, setTotalWaste] = useState(0);
  const [keysArr, setKeysArr] = useState([]);

  const [manageChart, setmanageChart] = useState("Area");
  const stateTenant = Digit.ULBService.getStateId();
  const { isMdmsLoading, data: mdmsData } = Digit.Hooks.useCommonMDMS(stateTenant, "FSM", "FSTPPlantInfo", {
    enabled: id === "fssmCapacityUtilization",
  });
  const { isLoading, data: response } = Digit.Hooks.dss.useGetChart({
    key: id,
    type: "metric",
    tenantId,
    requestDate: { ...value?.requestDate, startDate: value?.range?.startDate?.getTime(), endDate: value?.range?.endDate?.getTime() },
    filters: value?.filters,
  });

  useEffect(() => {
    if (mdmsData) {
      let fstpPlants = mdmsData;
      if (value?.filters?.tenantId?.length > 0) {
        fstpPlants = mdmsData.filter((plant) => value?.filters?.tenantId?.some((tenant) => plant?.ULBS.includes(tenant)));
      }
      const totalCapacity = fstpPlants.reduce((acc, plant) => acc + Number(plant?.PlantOperationalCapacityKLD), 0);
      setTotalCapacity(totalCapacity);
    }
  }, [mdmsData, value]);

  useEffect(() => {
    if (response) {
      const totalWaste = Math.round(response?.responseData?.data?.[0]?.plots[response?.responseData?.data?.[0]?.plots.length - 1]?.value);
      setTotalWaste(totalWaste);
      setChartDenomination(response?.responseData?.data?.[0]?.headerSymbol);
    }
  }, [response]);

  const chartData = useMemo(() => {
    if (response?.responseData?.data?.length == 1) {
      setmanageChart("Area");
      if (id !== "fsmCapacityUtilization") {
        let data = response?.responseData?.data?.[0]?.plots.map((plot, index) => {
          return index === 0 ? { ...plot, difference: 0 } : { ...plot, difference: plot.value - response?.responseData?.data?.[0]?.plots[index - 1].value }
        })
        return data;
      }
      return response?.responseData?.data?.[0]?.plots.map((plot) => {
        const [month, year] = plot?.name.split("-");
        const totalDays = getDaysInMonth(Date.parse(`${month} 1, ${year}`));
        const value = Math.round((plot?.value / (totalCapacity * totalDays)) * 100);
        return { ...plot, value };
      });
    } else if (response?.responseData?.data?.length > 1) {
      setmanageChart("Line");
      let keys = {};
      const mergeObj = response?.responseData?.data?.[0]?.plots.map((x, index) => {
        let newObj = {};
        response?.responseData?.data.map((ob) => {
          keys[t(Digit.Utils.locale.getTransformedLocale(ob.headerName))] = t(Digit.Utils.locale.getTransformedLocale(ob.headerName));
          newObj[t(Digit.Utils.locale.getTransformedLocale(ob.headerName))] = ob?.plots[index].value;
        });
        return {
          label: null,
          name: response?.responseData?.data?.[0]?.plots[index].name,
          strValue: null,
          symbol: response?.responseData?.data?.[0]?.plots[index].symbol,
          ...newObj,
        };
      });
      setKeysArr(Object.values(keys));
      return mergeObj;
    }
  }, [response, totalCapacity]);

  const renderPlot = (plot, key) => {

    const plotValue = key ? plot?.[key] : plot?.value || 0;
    if (id === "fssmCapacityUtilization" || id === "fsmCapacityUtilization" ){
      return Number(plotValue.toFixed(1));
    }
    if (key === "Reopened Complaints")
    {
      return null
    }
    if(id === "cumulativeCollectionv3")
    {
      const { denomination } = value;
      return getDenominatedValue(denomination, plotValue,plot);
    }
   if(id == "cumulativeCollectionOverview")
    {
      return getDenominatedValue("Cr", plotValue,plot);
    }
    if(id =="totalApplication&ClosedApplicationOverview")
    {
       return Number(plotValue.toFixed(1));
    }
    else if (plot?.symbol?.toLowerCase() === "amount") {
      const { denomination } = value;
      return getDenominatedValue(denomination, plotValue,plot);
    } else if (plot?.symbol?.toLowerCase() === "number") {
      return Number(plotValue.toFixed(1));
    } 
    else {
      return plotValue;
    }
  };

  const renderLegend = () => <span style={{ fontSize: "14px", color: "#505A5F" }}>{t(`DSS_${Digit.Utils.locale.getTransformedLocale(id)}`)}</span>;

  const renderLegendForLine = (ss, sss, index) => {
    return (
      <ul>
 <span style={{ fontSize: "14px", color: "#505A5F" }}>{keysArr?.[index]}</span>
      </ul>
    )
  }
  
  const tickFormatter = (value) => {
    if (typeof value === "string") {
      return value.replace("-", ", ");
    }
    return value;
  };

  const renderTooltip = ({ payload, label, unit }) => {
    let formattedLabel = tickFormatter(label);

    let payloadObj = payload?.[0] || {};
    const difference = Object.keys(payloadObj).length !== 0?getDenominatedValue(value.denomination, payloadObj.payload["difference"]):""
    return (
      <div
        style={{
          margin: "0px",
          padding: "10px",
          backgroundColor: "rgb(255, 255, 255)",
          border: "1px solid rgb(204, 204, 204)",
          whiteSpace: "nowrap",
        }}
      >
        {payloadObj?.payload?.symbol?.toLowerCase() === "amount" && (
          <p>{`${formattedLabel} : ${value?.denomination === "Unit" ? " ₹" : ""} ${payloadObj?.value}${
            value?.denomination !== "Unit" ? t(Digit.Utils.locale.getTransformedLocale(`ES_DSS_${value?.denomination}`)) : ""
          }`}</p>
        )}
       {payloadObj?.payload?.symbol?.toLowerCase() === "amount" && (
          <p>{`Collection Increased: ${value?.denomination === "Unit" ? " ₹" : ""} ${difference}${
            value?.denomination !== "Unit" ?  t(Digit.Utils.locale.getTransformedLocale(`ES_DSS_${value?.denomination}`)) : ""
          }`}</p>
        )}
        {payloadObj?.payload?.symbol?.toLowerCase() === "percentage" && <p>{`${formattedLabel} : ${payloadObj?.value} %`}</p>}
        {payloadObj?.payload?.symbol?.toLowerCase() === "number" && <p>{`${formattedLabel} : ${payloadObj?.value} `}</p>}
        {!payloadObj?.payload?.symbol && <p>{`${formattedLabel} : ${payloadObj?.value} `}</p>}
      </div>
    );
  };

  const renderTooltipForLine = ({ payload, label, unit }) => {
    console.log("payloadpayload",payload)
    let payloadObj = payload?.[0] || {};
    let prefix = payloadObj?.payload?.symbol?.toLowerCase() === "amount" && value?.denomination === "Unit" ? " ₹" : " ";
    let postfix =
      payloadObj?.payload?.symbol?.toLowerCase() === "percentage"
        ? " %"
        : payloadObj?.payload?.symbol?.toLowerCase() === "amount" && value?.denomination !== "Unit"
        ? t(Digit.Utils.locale.getTransformedLocale(`ES_DSS_${value?.denomination}`))
        : "";
    let newPayload = { ...payloadObj?.payload };
    delete newPayload?.label;
    delete newPayload?.strValue;
    delete newPayload?.symbol;
    let newObjArray = [newPayload?.name];
    delete newPayload?.name;
    console.log("sssssssss",payloadObj)
if(payloadObj?.payload?.["Non Tax Collection"])
{
  Object.keys(newPayload).map((key) => {
    newObjArray.push(
      `${key} -${prefix}${ 
       getDenominatedValue("Cr", newPayload?.[key],payloadObj?.payload)
      }Cr `
    );
  });
}
else {
  Object.keys(newPayload).map((key) => {
    newObjArray.push(
      `${key} -${prefix}${ 
        payloadObj?.payload?.COLLECTIONS_NONTAX ?getDenominatedValue(value?.denomination, newPayload?.[key],payloadObj?.payload):
        payloadObj?.payload?.symbol?.toLowerCase() === "amount" ? getDenominatedValue(value?.denomination, newPayload?.[key])+": Diiference " : newPayload?.[key]
      } ${postfix}`
    );
  });
}
     
    return (
      <div
        style={{
          margin: "0px",
          padding: "10px",
          backgroundColor: "rgb(255, 255, 255)",
          border: "1px solid rgb(204, 204, 204)",
          whiteSpace: "nowrap",
        }}
      >
        {newObjArray.map((ele, i) => (
          <p key={i}>{ele}</p>
        ))}
      </div>
    );
  };

  if (isLoading) {
    return <Loader />;
  }
  return (
    <div style={{ display: "flex", flexDirection: "column", justifyContent: "center", alignItems: "center", height: "100%" }}>
      {(id === "fssmCapacityUtilization"  ||id === "fsmCapacityUtilization"  )&& (
        <p>
          {t("DSS_FSM_TOTAL_SLUDGE_TREATED")} - {totalWaste} {t("DSS_KL")}
        </p>
      )}
      <ResponsiveContainer width="94%" height={increasedHeightCharts.includes(id) ? 700 : 450}>
        {!chartData || chartData?.length === 0 ? (
          <NoData t={t} />
        ) : manageChart == "Area" ? (
          <AreaChart width="100%" height="100%" data={chartData} margin={{ left: 30, top: 10 }}>
            <defs>
              <linearGradient id="colorUv" x1=".5" x2=".5" y2="1">
                <stop stopColor="#048BD0" stopOpacity={0.5} />
                <stop offset="1" stopColor="#048BD0" stopOpacity={0} />
              </linearGradient>
            </defs>
            <CartesianGrid strokeDasharray="3 3" />
            <Tooltip content={renderTooltip} />
            <XAxis dataKey={xDataKey} tick={{ fontSize: "14px", fill: "#505A5F" }} tickFormatter={tickFormatter} />
            <YAxis
              /*
              label={{
                value: `${t(`DSS_Y_${response?.responseData?.data?.[0]?.headerName.replaceAll(" ", "_").toUpperCase()}`)} ${
                  renderUnits(t, value.denomination,response?.responseData?.data?.[0]?.headerSymbol) 
                }`,
                angle: -90,
                position: "insideLeft",
                dy: 40,
                offset: -10,
                fontSize: "14px",
                fill: "#505A5F",
              }}
              */
              tick={{ fontSize: "14px", fill: "#505A5F" }}
            />
            <Legend formatter={renderLegend} iconType="circle" />
            <Area type="monotone" dataKey={renderPlot} stroke="#048BD0" fill="url(#colorUv)" dot={true} />
          </AreaChart>
        ) : id == "pgrCumulativeClosedCompla" ? (
      
              <ComposedChart
            width="100%"
            height="100%"
            margin={{
              top: 15,
              right: 5,
              left: 20,
              bottom: 5,
            }}
            data={chartData}
          >
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="name" />
            <YAxis yAxisId="left"  type={"number"} orientation="left" stroke="black" tickCount={10} />
            <YAxis yAxisId="right" orientation="right" stroke="#54d140" tickCount={10}/>
            <Tooltip content={renderTooltipForLine} />
            <Legend
            />
          <Bar yAxisId="right" dataKey="Opened Complaints" fill="#54d140" />
          <Line
                yAxisId="left"
                  type="monotone"
                  dataKey={"Closed Complaints"}
                  stroke={getColors(0)}
                  activeDot={{ r: 8 }}
                  strokeWidth={2}
                  key={0}
                  dot={{ stroke: getColors(0), strokeWidth: 1, r: 2, fill: getColors(0) }}
                />
                  <Line
                yAxisId="left"
                  type="monotone"
                  dataKey={"Total Complaints"}
                  stroke={getColors(2)}
                  activeDot={{ r: 8 }}
                  strokeWidth={2}
                  key={2}
                  dot={{ stroke: getColors(2), strokeWidth: 1, r: 2, fill: getColors(2) }}
                />
                
            {/* {keysArr?.map((key, i) => {
              return (
                <Line
                yAxisId="left"
                  type="monotone"
                  dataKey={(plot) => renderPlot(plot, key)}
                  stroke={getColors(i)}
                  activeDot={{ r: 8 }}
                  strokeWidth={2}
                  key={i}
                  dot={{ stroke: getColors(i), strokeWidth: 1, r: 2, fill: getColors(i) }}
                />
              );
            })} */}
          
          </ComposedChart>
        ):(
          <LineChart
            width={500}
            height={300}
            data={chartData}
            margin={{
              top: 15,
              right: 5,
              left: 20,
              bottom: 5,
            }}
          >
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="name" />
            <YAxis/>
            <Tooltip content={renderTooltipForLine} />
            <Legend
              layout="horizontal"
              formatter={renderLegendForLine}
              verticalAlign="bottom"
              align="center"
              iconType="circle"
              className={lineLegend}
            />
            {keysArr?.map((key, i) => {
              return (
                <Line
                  type="monotone"
                  dataKey={(plot) => renderPlot(plot, key)}
                  stroke={getColors(i)}
                  activeDot={{ r: 8 }}
                  strokeWidth={2}
                  key={i}
                  dot={{ stroke: getColors(i), strokeWidth: 1, r: 2, fill: getColors(i) }}
                />
              );
            })}
            {/* <Line
              type="monotone"
              dataKey={response?.responseData?.data?.[0]?.headerName}
              stroke="#8884d8"
              activeDot={{ r: 8 }}
            />
            <Line type="monotone" dataKey={response?.responseData?.data?.[1]?.headerName} stroke="#82ca9d" /> */}
          </LineChart>
        )}
      </ResponsiveContainer>
    </div>
  );
};

export default CustomAreaChart;