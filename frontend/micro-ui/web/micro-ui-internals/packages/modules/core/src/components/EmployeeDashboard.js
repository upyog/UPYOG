import React, { useEffect, useState } from 'react';
import { useTranslation } from "react-i18next";
import { Loader } from "@upyog/digit-ui-react-components";

/**
 * @author - Shivank Shukla - NIUA
 * 
 * This component displays a dashboard with key metrics for employees.
 * 
 * How it works:
 * 1. The component initially renders cards with loaders.
 * 2. When the component loads, it fetches dashboard data from an API.
 * 3. It displays four cards showing different metrics:
 *    - Applications Received
 *    - Total Amount
 *    - Pending Applications
 *    - Approved Applications
 * 
 * Technical details:
 * - Fetches data using the Digit.EmployeeDashboardService.search method.
 * - Uses the current tenant ID from Digit.ULBService.getCurrentUlb().
 * - Each card has its own loading state, replaced by data when available.
 * - Smart number formatting for large values (lakhs/crores)
 * 
 * Note: If the API call fails, an error is logged to the console, and the cards
 * will remain in their loading state.
 * 
 */

const formatNumbers = (amount) => {
  if (amount === null || amount === undefined) return '';
  
  const num = Number(amount);
  const numStr = num.toString();
  
  // If number has 5 digits or less, show exact value with Indian comma formatting
  if (numStr.length <= 5) {
    const lastThree = numStr.substring(numStr.length - 3);
    const otherNums = numStr.substring(0, numStr.length - 3);
    const formatted = otherNums.replace(/\B(?=(\d{2})+(?!\d))/g, ',');
    return otherNums ? formatted + ',' + lastThree : lastThree;
  }
  
  // For numbers greater than 5 digits
  else if (num >= 10000000) { // 1 crore and above
    const crores = num / 10000000;
    if (crores >= 100) {
      return `${Math.round(crores)} Crores`;
    } else if (crores >= 10) {
      return `${(crores).toFixed(1)} Crores`;
    } else {
      return `${(crores).toFixed(2)} Crores`;
    }
  } else if (num >= 100000) { // 1 lakh and above
    const lakhs = num / 100000;
    if (lakhs >= 100) {
      return `${Math.round(lakhs)} Lakhs`;
    } else if (lakhs >= 10) {
      return `${(lakhs).toFixed(1)} Lakhs`;
    } else {
      return `${(lakhs).toFixed(2)} Lakhs`;
    }
  }
  
  // Fallback for edge cases
  return numStr;
};

const formatIndianCurrency = (amount) => {
  if (amount === null || amount === undefined) return '';
  
  const formattedNumber = formatNumbers(amount);
  return `₹${formattedNumber}`;
};

const EmployeeDashboard = ({modules}) => {
  const { t } = useTranslation();
  const [cardData, setCardData] = useState([
    { title: "", count: null, color: "blue" },
    { title: "", count: null, color: "teal" },
    { title: "", count: null, color: "purple" },
    { title: "", count: null, color: "green" },
  ]);
  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        const tenantId = Digit.ULBService.getCurrentUlb().code;
        const payload = {
          tenantId: tenantId,
          moduleName: "ALL"
        };
        
        const response = await Digit.EmployeeDashboardService.search(payload);
        if (response && response.employeeDashboard) {
          setCardData([
            { title: t("ES_APPLICATION_RECEIVED"), count: response.employeeDashboard.applicationReceived || 0, color: "blue" },
            { title: t("ES_TOTAL_AMOUNT"), count: response.employeeDashboard.totalAmount || 0, color: "teal", isAmount: true },
            { title: t("ES_APPLICATION_PENDING"), count: response.employeeDashboard.applicationPending || 0, color: "purple" },
            { title: t("ES_APPLICATION_APPROVED"), count: response.employeeDashboard.applicationApproved || 0, color: "green" },
          ]);
        }
      } catch (error) {
        console.error("Error fetching dashboard data:", error);
      }
    };
    fetchDashboardData();
  }, [t]);

  return (
    <React.Fragment>
      <div style={{marginLeft:"42%", fontWeight:"bold", fontSize:"22px", marginBottom:"5px"}}>
        {t("COMMON_ULB_DASHBOARD")}
      </div>
      <div className="ground-container moduleCardWrapper gridModuleWrapper">
        {cardData.map(({ title, count, color, isAmount }, index) => (
          <div key={index} className={`status-card ${color}`}>
            <div className="card-content">
            {count === null ? (
                <div>
                  <Loader />
                </div>
            ) : (
              <React.Fragment>    
                <span className="count">
                  {isAmount ? formatIndianCurrency(count) : formatNumbers(count)}
                </span>
                <span className="title">{title}</span>
              </React.Fragment>  
            )}
            </div>
          </div>
        ))}
      </div>
    </React.Fragment>
  );
};

export default EmployeeDashboard;