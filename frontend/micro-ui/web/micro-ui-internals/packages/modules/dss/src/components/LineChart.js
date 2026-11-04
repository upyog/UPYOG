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
import {  format } from "date-fns";

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

  const key = "DSS_FILTERS";
  const getInitialRange = () => {
    const data = Digit.SessionStorage.get(key);
    const startDate = data?.range?.startDate ? new Date(data?.range?.startDate) : Digit.Utils.dss.getDefaultFinacialYear().startDate;
    const endDate = data?.range?.endDate ? new Date(data?.range?.endDate) : Digit.Utils.dss.getDefaultFinacialYear().endDate;
    const title = `${format(startDate, "MMM d, yyyy")} - ${format(endDate, "MMM d, yyyy")}`;
    const interval = Digit.Utils.dss.getDuration(startDate, endDate);
    const denomination = data?.denomination || "Lac";
    //const tenantId = data?.filters?.tenantId || [];
    const moduleLevel = data?.moduleLevel || "";
    return { startDate, endDate, title, interval, denomination, moduleLevel };
  };
  const { startDate, endDate, title, interval, denomination } = getInitialRange();

  const { isLoading, data: response } = Digit.Hooks.dss.useGetChart({
    key: "cumulativenooftransaction",
    type: "metric",
    tenantId,
    requestDate: {
      startDate: startDate.getTime(),
      endDate: endDate.getTime(),
      interval: interval,
      title: title,
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
            domain={["auto", "auto"]}
            tickFormatter={(value) => {
              if (value >= 10000000) {
                return `${(value / 10000000).toFixed(1)} Cr`; // Convert to thousands and show one decimal point
              }
              return value; // For values less than 1000, just display the value
            }}
            // label={{
            //   value: "No of Transactions",
            //   angle: -90, // Keep the label vertical, but you can change it to a smaller angle (e.g., -45 or -30)
            //   position: "insideLeft",
            //   offset: 30, // Increased offset to move the label further away from the axis
            // }}
            tick={{ fontSize: 12 }} 
            tickMargin={10} 
          />

          <Tooltip
            labelFormatter={(label) => `Month: ${label}`}
            formatter={(value) => {
              let formattedValue;
              if (value >= 10000000) {
                formattedValue = `${(value / 10000000).toFixed(2)} Cr`; // Convert to crores and format to 2 decimal places
              } else {
                formattedValue = `${value}`; // Keep the original value
              }
              return [formattedValue, 'No of Transactions'];
            }}
          />

          <Legend
            verticalAlign="top"
            content={() => (
              <div style={{ display: 'flex', justifyContent: 'center' }}>
                <div style={{ marginRight: 20, display: 'flex', alignItems: 'center' }}>
                  <div
                    style={{
                      width: 12,
                      height: 12,
                      borderRadius: '50%', 
                      backgroundColor: '#36a100', 
                      marginRight: 5,
                    }}
                  ></div>
                  <span>Total No of Transactions</span>
                </div>
              </div>
            )}
          />
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
