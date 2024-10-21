import React, { useEffect, useState } from 'react';
import { useTranslation } from "react-i18next";
import { Loader } from '@nudmcdgnpm/digit-ui-react-components';

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
 * 
 * Note: If the API call fails, an error is logged to the console, and the cards
 * will remain in their loading state.
 * 
 */

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
            { title: t("ES_TOTAL_AMOUNT"), count: response.employeeDashboard.totalAmount || 0, color: "teal" },
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
        {cardData.map(({ title, count, color }, index) => (
          <div key={index} className={`status-card ${color}`}>
            <div className="card-content">
            {count===null?(
                <div>
                <Loader />
                </div>
            ):(
              <React.Fragment>    
              <span className="count">{count}</span>
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