import React, { useEffect, useState } from 'react';
import { useTranslation } from "react-i18next";
import { Loader } from "@nudmcdgnpm/digit-ui-react-components";

/**
 * ============================================================================
 * EMPLOYEE DASHBOARD COMPONENT
 * ============================================================================
 * 
 * @author Shivank Shukla - NIUA
 * @version 2.0
 * @date 2024
 * 
 * PURPOSE:
 * --------
 * Displays role-based dashboard metrics for employees across multiple modules.
 * Shows separate dashboard sections for each module the employee has access to.
 * 
 * HOW IT WORKS:
 * -------------
 * 1. Component makes a single API call to backend with RequestInfo
 * 2. Backend automatically:
 *    - Extracts user roles from RequestInfo
 *    - Maps roles to modules (PT, TL, PETSERVICES, etc.)
 *    - Returns dashboard data for all accessible modules
 * 3. Frontend dynamically renders one dashboard section per module
 * 4. Each section displays 4 metric cards:
 *    - Applications Received (blue)
 *    - Total Amount (teal)
 *    - Applications Pending (purple)
 *    - Applications Approved (green)
 * 
 * SCENARIOS HANDLED:
 * ------------------
 * 1. Multiple Module Access (e.g., PT + TL):
 *    - Shows 2 separate dashboard sections
 *    - Each with its own heading and 4 cards
 * 
 * 2. Single Module Access (e.g., only PT):
 *    - Shows 1 dashboard section with 4 cards
 * 
 * 3. No Module Access:
 *    - Shows "No Dashboard Access" message
 * 
 * NUMBER FORMATTING:
 * ------------------
 * - Numbers ≤ 99,999: Indian comma format (e.g., 12,345)
 * - Numbers ≥ 1,00,000: Lakhs format (e.g., 5.25 Lakhs)
 * - Numbers ≥ 1,00,00,000: Crores format (e.g., 2.50 Crores)
 * - Currency: Prefixed with ₹ symbol
 * 
 * ============================================================================
 */

const formatNumbers = (amount) => {
  if (amount === null || amount === undefined) return '';
  const num = Number(amount);
  const numStr = num.toString();
  
  if (numStr.length <= 5) {
    const lastThree = numStr.substring(numStr.length - 3);
    const otherNums = numStr.substring(0, numStr.length - 3);
    const formatted = otherNums.replace(/\B(?=(\d{2})+(?!\d))/g, ',');
    return otherNums ? formatted + ',' + lastThree : lastThree;
  }
  else if (num >= 10000000) {
    const crores = num / 10000000;
    return crores >= 100 ? `${Math.round(crores)} Crores` : `${crores.toFixed(2)} Crores`;
  } else if (num >= 100000) {
    const lakhs = num / 100000;
    return lakhs >= 100 ? `${Math.round(lakhs)} Lakhs` : `${lakhs.toFixed(2)} Lakhs`;
  }
  return numStr;
};

const formatIndianCurrency = (amount) => {
  if (amount === null || amount === undefined) return '';
  return `₹${formatNumbers(amount)}`;
};

const ModuleDashboardSection = ({ moduleName, data, t }) => {
  return (
    <div className="module-dashboard-container">

      <div className="module-header">
        <span className="module-title">{t(`${moduleName}_DASHBOARD`)}</span>
      </div>

      <div className="module-cards">
        {[
          { title: t("ES_APPLICATION_RECEIVED"), count: data.applicationReceived || 0, color: "received" },
          { title: t("ES_TOTAL_AMOUNT"), count: data.totalAmount || 0, color: "amount", isAmount: true },
          { title: t("ES_APPLICATION_PENDING"), count: data.applicationPending || 0, color: "pending" },
          { title: t("ES_APPLICATION_APPROVED"), count: data.applicationApproved || 0, color: "approved" },
        ].map(({ title, count, color, isAmount }, index) => (
          <div key={index} className={`metric-card ${color}`}>
            <div className="metric-number">
              {isAmount ? formatIndianCurrency(count) : formatNumbers(count)}
            </div>

            <div className="metric-title">
              {title}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

const EmployeeDashboard = () => {
  const { t } = useTranslation();
  const [modulesData, setModulesData] = useState({});
  const [loading, setLoading] = useState(true);
  const user = Digit.UserService.getUser();
  const tenantId=user.info.tenantId;

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        const response = await Digit.EmployeeDashboardService.roleBaseSearch({tenantId});
        
        if (response?.dashboardData) {
          setModulesData(response.dashboardData);
        }
        
        setLoading(false);
      } catch (error) {
        console.error("Error fetching dashboard data:", error);
        setLoading(false);
      }
    };
    
    fetchDashboardData();
  }, []);

  if (loading) {
    return <Loader />;
  }

  return (
    <div className="employee-app-container" style={{ padding: "20px" }}>
      <div style={{ 
        textAlign: "center", 
        fontWeight: "bold", 
        fontSize: "24px", 
        marginBottom: "30px" 
      }}>
        {t("COMMON_ULB_DASHBOARD")}
      </div>
      
      {Object.keys(modulesData).length === 0 ? (
        <div style={{ 
          textAlign: "center", 
          padding: "40px", 
          color: "#505A5F",
          fontSize: "16px" 
        }}>
          {t("NO_DASHBOARD_ACCESS")}
        </div>
      ) : (
        Object.entries(modulesData).map(([moduleName, data]) => (
          <ModuleDashboardSection 
            key={moduleName}
            moduleName={moduleName}
            data={data}
            t={t}
          />
        ))
      )}
    </div>
  );
};

export default EmployeeDashboard;
