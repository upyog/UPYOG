import React, { useContext, useEffect, useMemo, useState } from "react";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";
import { useTranslation } from "react-i18next";
import FilterContext from "./FilterContext";
// Sample Data
const data = [
  {
    label: null,
    name: "Oct-2016",
    value: 0,
    strValue: null,
    symbol: "number",
  },
  {
    label: null,
    name: "Nov-2016",
    value: 0,
    strValue: null,
    symbol: "number",
  },
  {
    label: null,
    name: "Dec-2016",
    value: 0,
    strValue: null,
    symbol: "number",
  },
  {
    label: null,
    name: "Jan-2017",
    value: 0,
    strValue: null,
    symbol: "number",
  },
  {
    label: null,
    name: "Feb-2017",
    value: 0,
    strValue: null,
    symbol: "number",
  },
  {
    label: null,
    name: "Dec-2024",
    value: 94237,
    strValue: null,
    symbol: "number",
  },
];

const LineChartWithData = () => {
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
    key: "cumulativenooftransaction",
    type: "metric",
    tenantId,
    requestDate: { ...value?.requestDate, startDate: value?.range?.startDate?.getTime(), endDate: value?.range?.endDate?.getTime() },
    filters: value?.filters,
  });

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
  }, [response]);
  // Transform data for the chart
  const chartDataNew = data.map((item) => ({
    date: item.name, // X-axis label
    value: item.value, // Y-axis value
  }));
console.log("chartDatachartData22323",chartData)
  return (
    <div style={{ backgroundColor: "#d7efc2", padding: "20px", borderRadius: "10px",width:"100%" }}>
      <h3 style={{ textAlign: "center", color: "#fff", backgroundColor: "#36a100", padding: "10px", margin: 0, width:"100%" }}>
        
      </h3>
      <ResponsiveContainer width="100%" height={400}>
        <LineChart data={chartData} margin={{ top: 20, right: 30, left: 20, bottom: 20 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="date" />
          <YAxis tickFormatter={(value) => `${value / 1000}K`} />
          <Tooltip formatter={(value) => `${value} ₹ crore`} />
          <Legend verticalAlign="top" />
          <Line
            type="monotone"
            dataKey="value"
            stroke="#36a100"
            strokeWidth={2}
            dot={{ r: 5, fill: "#36a100" }}
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
};

export default LineChartWithData;
