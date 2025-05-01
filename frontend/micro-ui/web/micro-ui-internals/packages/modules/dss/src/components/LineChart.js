import React, { useContext, useMemo, useState, useEffect } from "react";
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
import { addMonths, endOfYear, format, startOfYear,startOfMonth,subMonths, endOfMonth} from "date-fns";

const LineChartWithData = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { value } = useContext(FilterContext);
  const [totalCapacity, setTotalCapacity] = useState(0);
  const stateTenant = Digit.ULBService.getStateId();
  const [selectedQuarter, setSelectedQuarter] = useState("Last 12 Months");
  const { isMdmsLoading, data: mdmsData } = Digit.Hooks.useCommonMDMS(
    stateTenant,
    "FSM",
    "FSTPPlantInfo",
    {
      enabled: true,
    }
  );
  const key = "DSS_FILTERS_CUMILATIVETRANSACTIONS";

  useEffect(() => {
    const { startDate, endDate } = getQuarterDates("Last 12 Months");
  
    const dataWithDates = {
      startDate,
      endDate,
      interval: "month",
    };
  
    Digit.SessionStorage.set(key, dataWithDates);
  
    setFilters({
      range: { startDate, endDate },
      requestDate: {
        startDate: startDate.getTime(),
        endDate: endDate.getTime(),
        interval: "month",
        title: `${format(startDate, "MMM d, yyyy")} - ${format(endDate, "MMM d, yyyy")}`,
      },
      filters: {
        tenantId: tenantId,
      },
    });
  }, []);  

  const getInitialRange = () => {
    const data = Digit.SessionStorage.get(key);
    //const calculatedStartDate = startOfMonth(subMonths(new Date(), 11));
    //const calculatedEndDate = new Date(); // current date
    const startDate = data?.startDate ? new Date(data?.startDate) : Digit.Utils.dss.getDefaultFinacialYear().startDate;
    const endDate = data?.endDate ? new Date(data?.endDate) : Digit.Utils.dss.getDefaultFinacialYear().endDate;
    const title = `${format(startDate, "MMM d, yyyy")} - ${format(endDate, "MMM d, yyyy")}`;
    const interval = Digit.Utils.dss.getDuration(startDate, endDate);
    const denomination = data?.denomination || "Lac";
    const moduleLevel = data?.moduleLevel || "";
    return { startDate, endDate, title, interval, denomination, moduleLevel };
  };
  
  const [filters, setFilters] = useState(() => {
    const { startDate, endDate, title, interval, denomination, tenantId } = getInitialRange();
    return {
      range: { startDate, endDate, title, interval },
      requestDate: {
        startDate: startDate.getTime(),
        endDate: endDate.getTime(),
        interval: interval,
        title: title,
      },
      filters: {
        tenantId: tenantId,
      },
    };
  });

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
  },
  {
    enabled: selectedQuarter === "Last 12 Months" || ["Q1", "Q2", "Q3", "Q4"].includes(selectedQuarter),
  });


  
  const getLast12Months = () => {
    const months = [];
    const now = new Date();
  
    // Start 11 months before the current month to include current month as last
    for (let i = 11; i >= 0; i--) {
      const date = subMonths(now, i);
      months.push({
        month: date.getMonth(),
        year: date.getFullYear(),
        label: format(date, "MMM yyyy"),
      });
    }
    return months;
  };  

  // Divide the last 12 months into quarters
  const getQuarterDates = (selectedQuarter) => {
    const last12Months = getLast12Months();

    const quarters = [
      last12Months.slice(0, 3), 
      last12Months.slice(3, 6), 
      last12Months.slice(6, 9), 
      last12Months.slice(9, 12), 
    ];

    switch (selectedQuarter) {
      case "Last 12 Months":
      const currentDate = new Date();
      return {
        startDate: startOfMonth(subMonths(currentDate, 11)),
        endDate: currentDate,
      };
      case "Q1":
      return {
        startDate: new Date(quarters[0][0].year, quarters[0][0].month, 1),
        endDate: endOfMonth(new Date(quarters[0][2].year, quarters[0][2].month)),
      };
      case "Q2":
      return {
        startDate: new Date(quarters[1][0].year, quarters[1][0].month, 1),
        endDate: endOfMonth(new Date(quarters[1][2].year, quarters[1][2].month)),
      };
      case "Q3":
      return {
        startDate: new Date(quarters[2][0].year, quarters[2][0].month, 1),
        endDate: endOfMonth(new Date(quarters[2][2].year, quarters[2][2].month)),
      };
      case "Q4":
        return {
          startDate: new Date(quarters[3][0].year, quarters[3][0].month, 1),
          endDate: endOfMonth(new Date(quarters[3][2].year, quarters[3][2].month)),
        };
      default:
        return { startDate: new Date(), endDate: new Date() };
    }
  };

  const handleFilters = (event) => {
    const selectedQuarter = event.target.value;
    setSelectedQuarter(selectedQuarter);

    const { startDate, endDate } = getQuarterDates(selectedQuarter);
    const dataWithDates = {
      startDate,
      endDate,
      interval: "month",
    };
    Digit.SessionStorage.set(key, dataWithDates);

    // Update the filters with the new start and end dates
    setFilters((prevFilters) => ({
      range: { startDate, endDate },
      requestDate: {
        startDate: startDate.getTime(),
        endDate: endDate.getTime(),
        interval: "month",
        title: `${format(startDate, "MMM d, yyyy")} - ${format(endDate, "MMM d, yyyy")}`,
      },
    }));
  };

  // Filter the API response data to include only the last 12 months
  const chartDataNew = useMemo(() => {
    if (!response?.responseData?.data) return [];
    const last12Months = getLast12Months();
    const filteredData = response?.responseData?.data?.[0]?.plots.filter((item) => {
      const [month, year] = item.name.split("-");
      const itemDate = new Date(`${month} 1, ${year}`);
      return itemDate >= new Date(last12Months[0].year, last12Months[0].month, 1) &&
        itemDate <= new Date(last12Months[11].year, last12Months[11].month, 1);
    });

    // Map the filtered data to chart data format
    return filteredData?.map((item) => ({
      date: item.name,
      value: item.value,
    }));
  }, [response]);

  return (
    <div style={{ backgroundColor: "white", padding: "20px", borderRadius: "10px", width: "100%" }}>
      <h2 style={{ textAlign: "center", color: "#rgb(0, 0, 0)", padding: "10px", margin: 0, fontSize: '24px', fontWeight: '500' }}>
        Cumulative No. of Transactions
      </h2>

      {/* Quarter Filter Dropdown */}
      <div style={{ marginBottom: '20px' }}>
        <label htmlFor="quarterSelect" style={{ marginRight: '10px' }}>
          Select Quarter:
        </label>
        <select
        id="quarterSelect"
        value={selectedQuarter}
        onChange={handleFilters}
        style={{ padding: '8px', fontSize: '14px' }}
      >
        <option value="Last 12 Months">Last 12 Months</option>
        <option value="Q1">Q1</option>
        <option value="Q2">Q2</option>
        <option value="Q3">Q3</option>
        <option value="Q4">Q4</option>
      </select>

      </div>

      <ResponsiveContainer width="100%" height={400}>
        <LineChart data={chartDataNew} margin={{ top: 20, right: 30, left: 20, bottom: 20 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="date" />
          <YAxis
            scale="linear"
            domain={["auto", "auto"]}
            tickFormatter={(value) => value} 
            tick={{ fontSize: 12 }}
            tickMargin={10}
          />
          <Tooltip
            labelFormatter={(label) => `Month: ${label}`}
            formatter={(value, name, props) => [
              `${value}`,
              'noOfTransactions',
            ]}
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
