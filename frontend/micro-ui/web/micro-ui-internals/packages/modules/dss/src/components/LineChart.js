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
  const stateTenant = Digit.ULBService.getStateId();
  const [selectedQuarter, setSelectedQuarter] = useState("Whole Financial Year");
  const { isMdmsLoading, data: mdmsData } = Digit.Hooks.useCommonMDMS(
    stateTenant,
    "FSM",
    "FSTPPlantInfo",
    {
      enabled: true, 
    }
  );
  const key = "DSS_FILTERS_CUMILATIVETRANSACTIONS";

  const getInitialRange = () => {
    const data = Digit.SessionStorage.get(key);
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
    enabled: !!selectedQuarter  , //  hook is triggered only when a valid quarter is selected
  }
  );

  function getQuarterDates(selectedQuarter) {
    const currentDate = new Date();
    const currentMonth = currentDate.getMonth(); 
    const currentYear = currentDate.getFullYear();
    let startMonth, endMonth;
    
    // Calculate the financial year based on current month
    let financialYearStart, financialYearEnd;
  
    if (currentMonth >= 0 && currentMonth <= 2) { 
      financialYearStart = currentYear - 1; 
      financialYearEnd = currentYear; 
    } else { 
      financialYearStart = currentYear; 
      financialYearEnd = currentYear + 1; 
    }
  
    // Define the start and end months for each quarter
    switch (selectedQuarter) {
      case "Whole Financial Year":
        startMonth = 3;  // Start from April (financial year start)
        endMonth = 2;    // End in March of next year
        break;
      case "Jan-Mar":
        startMonth = 0; // January (0-indexed)
        endMonth = 2;   // March (0-indexed)
        break;
      case "Apr-Jun":
        startMonth = 3; // April
        endMonth = 5;   // June
        break;
      case "Jul-Sep":
        startMonth = 6; // July
        endMonth = 8;   // September
        break;
      case "Oct-Dec":
        startMonth = 9; // October
        endMonth = 11;  // December
        break;
      default:
        // Default to "Whole Financial Year" if no specific quarter is selected
        startMonth = 3;  // Start from April (financial year start)
        endMonth = 2;    // End in March of next year
        break;
    }
  
    if (!selectedQuarter || selectedQuarter=="Whole Financial Year") {
      return {
        startDate: new Date(financialYearStart, 3, 1), 
        endDate: new Date(financialYearEnd, 2, 31),   
      };
    }
   
    if(selectedQuarter === "Jan-Mar"){
      return{
       startDate : new Date(financialYearEnd, startMonth, 1),
       endDate : new Date(financialYearEnd, endMonth + 1, 0),
      };
    }
   
    const startDate = new Date(financialYearStart, startMonth, 1);
    const endDate = new Date(financialYearStart, endMonth + 1, 0); 
    
    return { startDate, endDate };
  }

  const handleFilters = (event) => {
    const selectedQuarter = event.target.value; 
    setSelectedQuarter(selectedQuarter); 
    
    const { startDate, endDate } = getQuarterDates(selectedQuarter);
    
    const dataWithDates = {
      startDate, 
      endDate,
      interval:"month"   
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

    const currentDate = new Date();
    const twelveMonthsAgo = new Date();
    twelveMonthsAgo.setMonth(currentDate.getMonth() - 12); 

    const filteredData = response?.responseData?.data?.[0]?.plots.filter((item) => {
      const [month, year] = item.name.split("-");
      const itemDate = new Date(`${month} 1, ${year}`);
      return itemDate >= twelveMonthsAgo; 
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
        backgroundColor: "white", 
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
        }}
      >
        Cumulative No. of Transactions
      </h2>
      {/* Quarter Filter Dropdown */}
      <div style={{ marginBottom: '20px' }}>
        <label htmlFor="quarterSelect" style={{ marginRight: '10px' }}>
          Select Quarter:
        </label>
        <select
          id="quarterSelect"
          value={selectedQuarter } // Ensures the value is empty initially
          onChange={handleFilters}
          style={{ padding: '8px', fontSize: '14px' }}
        >
          <option value="Whole Financial Year">Whole Financial Year</option>
          <option value="Jan-Mar">Jan-Mar</option>
          <option value="Apr-Jun">Apr-Jun</option>
          <option value="Jul-Sep">Jul-Sep</option>
          <option value="Oct-Dec">Oct-Dec</option>
        </select>
      </div>

      <ResponsiveContainer width="100%" height={400}>
        <LineChart data={chartDataNew} margin={{ top: 20, right: 30, left: 20, bottom: 20 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="date"  />
          <YAxis
            scale="linear"
            domain={["auto", "auto"]}
            tickFormatter={(value) => {
              return value; // For values less than 1000, just display the value
            }}
            tick={{ fontSize: 12 }} 
            tickMargin={10} 
          />

          <Tooltip
            labelFormatter={(label) => `Month: ${label}`}
            formatter={(value, name, props) => [
              `${value}`, // Display the value (transaction count)
              'noOfTransactions' 
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
