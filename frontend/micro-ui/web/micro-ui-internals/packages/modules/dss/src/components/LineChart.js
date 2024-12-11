import React, { useContext, useMemo, useState } from "react";
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

const LineChartWithData = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { value } = useContext(FilterContext);
  const [totalCapacity, setTotalCapacity] = useState(0);
  const [totalWaste, setTotalWaste] = useState(0);
  const [keysArr, setKeysArr] = useState([]);

  const [manageChart, setmanageChart] = useState("Area");
  const stateTenant = Digit.ULBService.getStateId();

  const { isMdmsLoading, data: mdmsData } = Digit.Hooks.useCommonMDMS(
    stateTenant,
    "FSM",
    "FSTPPlantInfo",
    {
      enabled: true, // Adjust condition as needed
    }
  );

  const { isLoading, data: response } = Digit.Hooks.dss.useGetChart({
    key: "cumulativenooftransaction",
    type: "metric",
    tenantId,
    requestDate: {
      ...value?.requestDate,
      startDate: value?.range?.startDate?.getTime(),
      endDate: value?.range?.endDate?.getTime(),
    },
    filters: value?.filters,
  });

  const chartData = useMemo(() => {
    if (response?.responseData?.data?.length === 1) {
      setmanageChart("Area");
      if (response?.responseData?.data?.[0]?.id !== "fsmCapacityUtilization") {
        let data = response?.responseData?.data?.[0]?.plots.map((plot, index) => {
          return index === 0
            ? { ...plot, difference: 0 }
            : {
                ...plot,
                difference: plot.value - response?.responseData?.data?.[0]?.plots[index - 1].value,
              };
        });
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
          keys[t(Digit.Utils.locale.getTransformedLocale(ob.headerName))] = t(
            Digit.Utils.locale.getTransformedLocale(ob.headerName)
          );
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

  // Filter the API response data to include only the last 12 months
  const chartDataNew = useMemo(() => {
    if (!response?.responseData?.data) return [];

    const currentDate = new Date();
    const twelveMonthsAgo = new Date();
    twelveMonthsAgo.setMonth(currentDate.getMonth() - 12); // Get the date for 12 months ago

    // Map the data and filter out data older than 12 months
    const filteredData = response?.responseData?.data?.[0]?.plots.filter((item) => {
      const [month, year] = item.name.split("-");
      const itemDate = new Date(`${month} 1, ${year}`);
      return itemDate >= twelveMonthsAgo; // Only include data from the last 12 months
    });

    // Map the filtered data to chart data format
    return filteredData?.map((item) => ({
      date: item.name,
      value: item.value,
    }));
  }, [response]);

  return (
    <div
      style={{
        backgroundColor: "white", // Graph background color is white
        padding: "20px",
        borderRadius: "10px",
        width: "100%",
      }}
    >
      <h2
        style={{
          textAlign: "center",
          color: "#rgb(0, 0, 0)",
          padding: "10px",
          margin: 0,
          fontSize:'24px',
          fontWeight:'500',
          //fontFamily:'Roboto, sans-serifto',
        }}
      >
        Cumulative No. of Transactions
      </h2>

      <ResponsiveContainer width="100%" height={400}>
        <LineChart data={chartDataNew} margin={{ top: 20, right: 30, left: 20, bottom: 20 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="date"  />
          <YAxis
            scale="linear"
            domain={["auto", "auto"]} // Automatically calculate the Y-axis domain
            tickFormatter={(value) => value} // Display as integer (transaction count)
            //label={{ value: "No of Transactions", angle: -90, position: "insideLeft" }}
          />
          <Tooltip formatter={(value) => `${value}`} />
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
