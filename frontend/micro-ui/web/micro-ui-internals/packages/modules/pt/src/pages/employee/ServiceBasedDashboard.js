import React, { useEffect, useState } from "react";

import { useTranslation } from "react-i18next";

import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale,
} from "chart.js";
import { Pie, Doughnut, Bar } from "react-chartjs-2";

ChartJS.register(ArcElement, Tooltip, Legend, BarElement, CategoryScale, LinearScale);


export const ServiceBasedDashboard = ({ dashboardData }) => {

    const propertyStats = dashboardData[0]?.services;

    const statusPieData = {
    labels: ["Approved", "Rejected", "Pending Verification", "Registered"],
    datasets: [
      {
        data: [
          propertyStats.propertiesApproved,
          propertyStats.propertiesRejected,
          propertyStats.propertiesPendingWithDocVerifier +
            propertyStats.propertiesPendingWithFieldInspector +
            propertyStats.propertiesPendingWithApprover,
          propertyStats.totalPropertiesRegistered,
        ],
        backgroundColor: ["#4caf50", "#f44336", "#ff9800", "#2196f3"],
        borderWidth: 1,
      },
    ],
  };

  const selfAssessmentData = {
    labels: ["Self Assessed", "Pending Self Assessment"],
    datasets: [
      {
        data: [propertyStats.propertiesSelfAssessed, propertyStats.propertiesPendingSelfAssessment],
        backgroundColor: ["#3f51b5", "#cddc39"],
      },
    ],
  };

  const pipelineData = {
    labels: ["Doc Verifier", "Field Inspector", "Approver"],
    datasets: [
      {
        label: "Pending Properties",
        data: [
          propertyStats.propertiesPendingWithDocVerifier,
          propertyStats.propertiesPendingWithFieldInspector,
          propertyStats.propertiesPendingWithApprover,
        ],
        backgroundColor: "#ff9800",
      },
    ],
  };

  const taxStatusData = {
    labels: ["Registered", "Paid Tax"],
    datasets: [
      {
        label: "Tax Status",
        data: [propertyStats.totalPropertiesRegistered, propertyStats.propertiesPaid],
        backgroundColor: ["#2196f3", "#4caf50"],
      },
    ],
  };

    return (
        <div className="row">
            <div className="col-sm-8">
                {/* style={{ display: "flex", gap: "15px", flexWrap: "wrap", padding: "10px" }} */}
                <div style={{ display: "flex", gap: "15px", flexWrap: "wrap", padding: "0px" }}>
                    <div style={{ width: "350px", boxShadow: "0px 0px 3px 0px", borderRadius: "6px" }}>
                        <h4 style={{paddingLeft: "10px", paddingTop: "10px", fontWeight: "600", textDecoration: "underline"}}>Registration Status</h4>
                        <Pie data={statusPieData} />
                    </div>

                    <div style={{ width: "350px", boxShadow: "0px 0px 3px 0px", borderRadius: "6px" }}>
                        <h4 style={{paddingLeft: "10px", paddingTop: "10px", fontWeight: "600", textDecoration: "underline"}}>Self Assessment</h4>
                        <Doughnut data={selfAssessmentData} />
                    </div>

                    <div style={{ width: "350px", boxShadow: "0px 0px 3px 0px", borderRadius: "6px" }}>
                        <h4 style={{paddingLeft: "10px", paddingTop: "10px", fontWeight: "600", textDecoration: "underline"}}>Processing Pipeline</h4>
                        <Bar data={pipelineData} />
                    </div>

                    <div style={{ width: "350px", boxShadow: "0px 0px 3px 0px", borderRadius: "6px" }}>
                        <h4 style={{paddingLeft: "10px", paddingTop: "10px", fontWeight: "600", textDecoration: "underline"}}>Tax Payment</h4>
                        <Bar data={taxStatusData} />
                    </div>
                </div>
                
            </div>

            <div className="col-sm-4">
                {/* style={{ marginTop: "40px" }} */}
                <h3 style={{paddingTop: "0px", fontWeight: "600", textDecoration: "underline"}}>Service KPI Cards</h3>
                <div style={{ gap: "15px", flexWrap: "wrap", paddingTop: "10px" }}>
                {Object.entries(propertyStats).map(([key, value]) => (
                    <div
                    key={key}
                    style={{
                        background: "#e0e0e0",
                        padding: "5px",
                        borderRadius: "5px",
                        display: "flex",
                        justifyContent: "space-between",
                        marginBottom: "10px"
                        // minWidth: "150px",
                    }}
                    >
                    <strong>
                        {key
                            .replace(/([A-Z])/g, " $1")        // Add space before capital letters
                            .replace(/^./, (str) => str.toUpperCase()) // Capitalize first letter
                            .split(" ")                        // Split into words
                            .map(word => word.charAt(0).toUpperCase() + word.slice(1)) // Capitalize each word
                            .join(" ")}
                    </strong>
                    <div style={{ fontSize: "20px", 
                        color: key.toLocaleLowerCase().includes("pending") ? "#ff9e10" :
                        key.toLocaleLowerCase().includes("rejected") ? "red" : 
                        key.toLocaleLowerCase().includes("approved") ? "#4caf50" :
                        key.toLowerCase().includes("assessed") ? "#3f51b5" :
                        key.toLocaleLowerCase().includes("registered") ? "#2196f3" : 
                        key.toLocaleLowerCase().includes("paid") ? "#4caf50" :
                        "#333", fontWeight: "600" }}>{value}</div>
                    </div>
                ))}
                </div>
            </div>
        </div>
        // <div className="row">
        //     <div className="col-sm-6" >
        //         <div className="card">
        //             <div style={{display: "flex", justifyContent: "space-between"}}>
        //                 <span style={{fontSize: "20px"}}>No. of properties registered</span>
        //                 <span style={{fontSize: "20px",fontWeight: "600"}}>{dashboardData[0]?.services?.totalPropertiesRegistered}</span>
        //             </div>
        //             <div style={{display: "flex", justifyContent: "space-between"}}>
        //                 <span style={{fontSize: "20px"}}>No. of properties pending with doc verifier</span>
        //                 <span style={{fontSize: "20px",fontWeight: "600"}}>{dashboardData[0]?.services?.propertiesPendingWithDocVerifier}</span>
        //             </div>
        //             <div style={{display: "flex", justifyContent: "space-between"}}>
        //                 <span style={{fontSize: "20px"}}>No. of properties pending with field inspector</span>
        //                 <span style={{fontSize: "20px",fontWeight: "600"}}>{dashboardData[0]?.services?.propertiesPendingWithFieldInspector}</span>
        //             </div>
        //             <div style={{display: "flex", justifyContent: "space-between"}}>
        //                 <span style={{fontSize: "20px"}}>No. of properties pending with field approver</span>
        //                 <span style={{fontSize: "20px",fontWeight: "600"}}>{dashboardData[0]?.services?.propertiesPendingWithApprover}</span>
        //             </div>
        //             <div style={{display: "flex", justifyContent: "space-between"}}>
        //                 <span style={{fontSize: "20px"}}>No. of properties approved</span>
        //                 <span style={{fontSize: "20px",fontWeight: "600"}}>{dashboardData[0]?.services?.propertiesApproved}</span>
        //             </div>
        //             <div style={{display: "flex", justifyContent: "space-between"}}>
        //                 <span style={{fontSize: "20px"}}>No. of properties rejected</span>
        //                 <span style={{fontSize: "20px",fontWeight: "600"}}>{dashboardData[0]?.services?.propertiesRejected}</span>
        //             </div>
        //             <div style={{display: "flex", justifyContent: "space-between"}}>
        //                 <span style={{fontSize: "20px"}}>No. of properties self assessed</span>
        //                 <span style={{fontSize: "20px",fontWeight: "600"}}>{dashboardData[0]?.services?.propertiesSelfAssessed}</span>
        //             </div>
        //             <div style={{display: "flex", justifyContent: "space-between"}}>
        //                 <span style={{fontSize: "20px"}}>No. of properties pending for self assessment</span>
        //                 <span style={{fontSize: "20px",fontWeight: "600"}}>{dashboardData[0]?.services?.propertiesPendingSelfAssessment}</span>
        //             </div>
        //             <div style={{display: "flex", justifyContent: "space-between"}}>
        //                 <span style={{fontSize: "20px"}}>No. of properties paid tax</span>
        //                 <span style={{fontSize: "20px",fontWeight: "600"}}>{dashboardData[0]?.services?.propertiesPaid}</span>
        //             </div>
        //             <div style={{display: "flex", justifyContent: "space-between"}}>
        //                 <span style={{fontSize: "20px"}}>No. of appeals submitted</span>
        //                 <span style={{fontSize: "20px",fontWeight: "600"}}>{dashboardData[0]?.services?.propertiesWithAppealSubmitted}</span>
        //             </div>
        //             <div style={{display: "flex", justifyContent: "space-between"}}>
        //                 <span style={{fontSize: "20px"}}>No. of appeals pending </span>
        //                 <span style={{fontSize: "20px",fontWeight: "600"}}>{dashboardData[0]?.services?.appealsPending}</span>
        //             </div>

        //         </div>
                
        //     </div>
        //     <div className="col-sm-6">
        //         <div className="card" style={{ height: '355px', textAlign: "center" }}>
        //             {/* <Doughnut data={data} /> */}

        //         </div>
        //     </div>
        // </div>
    )
}