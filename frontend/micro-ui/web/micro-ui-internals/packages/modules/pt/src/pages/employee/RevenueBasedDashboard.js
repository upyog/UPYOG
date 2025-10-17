import React, { useEffect, useMemo, useState } from "react";
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from "chart.js";
import { Pie, Bar } from "react-chartjs-2";
import { useTranslation } from "react-i18next";


export const RevenueBasedDashboard = ({ dashboardData }) => {
    function toFinite(n) {
      var x = Number(n);
      return isFinite(x) ? x : 0;
    }

    const optionsPie = useMemo(() => ({
        responsive: true,
        maintainAspectRatio: true,
        plugins: {
          tooltip: {
            callbacks: {
              label: function (ctx) {
                var rawArr = (ctx && ctx.dataset && ctx.dataset.data) || [];
                var arr = Array.isArray(rawArr) ? rawArr.map(toFinite) : [];
                var total = arr.reduce(function (a, b) { return a + b; }, 0);
    
                var value = toFinite(ctx.parsed);
                if (total <= 0) return (ctx.label || '') + ': ' + value; // no percent when no total
    
                var pct = (value / total) * 100;
                var pctText = isFinite(pct) ? pct.toFixed(1) + '%' : '0%';
                var label = (ctx && ctx.label != null) ? ctx.label : '';
                return label + ': ' + value + ' (' + pctText + ')';
              }
            }
          },
          datalabels: {
            display: function (context) {
              // hide labels when total is 0 or value is 0
              var chart = context && context.chart;
              var data = chart && chart.data;
              var ds0  = data && data.datasets && data.datasets[0];
              var raw  = ds0 && ds0.data;
              var arr  = Array.isArray(raw) ? raw.map(toFinite) : [];
              var total = arr.reduce(function (a, b) { return a + b; }, 0);
              var v = toFinite(context.raw);
              return total > 0 && v > 0;
            },
            formatter: function (value, context) {
              var chart = context && context.chart;
              var data = chart && chart.data;
              var ds0  = data && data.datasets && data.datasets[0];
              var raw  = ds0 && ds0.data;
              var arr  = Array.isArray(raw) ? raw.map(toFinite) : [];
              var total = arr.reduce(function (a, b) { return a + b; }, 0);
    
              if (total <= 0) return '';              // nothing to show
              var v = toFinite(value);
              var pct = (v / total) * 100;
              if (!isFinite(pct)) return '';          // guards Infinity/NaN
              return pct >= 1 ? pct.toFixed(1) + '%' : '<1%';
            },
            color: '#fff',
            font: { weight: 'bold' },
            anchor: 'center',
            align: 'center',
            clip: true
          }
        },
        // optional: improve label placement for small slices
        layout: { padding: 8 },
      }),[]);

    const taxData = dashboardData[0]?.revenue;
    // {
    //     advance: 0,
    //     arrears: 0,
    //     interest: 0,
    //     penalty: 0,
    //     propertyTax: 6027818.13,
    //     refund: 0,
    //     totalTaxCollected: 9644509
    // };

    const pieData = {
        labels: ['Property Tax', 'Interest', 'Penalty', 'Advance', 'Arrears'],
        datasets: [
            {
            data: [
                taxData.propertyTax,
                taxData.interest,
                taxData.penalty,
                taxData.advance,
                taxData.arrears
            ],
            backgroundColor: ['#4CAF50', '#FFC107', '#F44336', '#2196F3', '#9C27B0'],
            }
        ]
    };

    const barData = {
        labels: ['Property Tax', 'Interest', 'Penalty', 'Advance', 'Arrears', 'Refund'],
        datasets: [
            {
            label: 'Tax Amount (INR)',
            data: [
                taxData.propertyTax,
                taxData.interest,
                taxData.penalty,
                taxData.advance,
                taxData.arrears,
                taxData.refund
            ],
            backgroundColor: 'rgba(63, 81, 181, 0.7)',
            }
        ]
    };

    return (
        <div className="row">
            <div className="col-sm-8">
                {/* style={{ display: "flex", gap: "15px", flexWrap: "wrap", padding: "10px" }} */}
                <div style={{ display: "flex", gap: "15px", flexWrap: "wrap", padding: "0px" }}>
                    <div style={{ width: "350px", boxShadow: "0px 0px 3px 0px", borderRadius: "6px" }}>
                        <h4 style={{paddingLeft: "10px", paddingTop: "10px", fontWeight: "600", textDecoration: "underline"}}>Tax Composition (Pie)</h4>
                        <Pie data={pieData} options={optionsPie} />
                    </div>

                    <div style={{ width: "350px", boxShadow: "0px 0px 3px 0px", borderRadius: "6px" }}>
                        <h4 style={{paddingLeft: "10px", paddingTop: "10px", fontWeight: "600", textDecoration: "underline"}}>Tax Breakdown (Bar)</h4>
                        <Bar data={barData} />
                    </div>
                </div>
                
            </div>

            <div className="col-sm-4">
                {/* style={{ marginTop: "40px" }} */}
                <h3 style={{paddingTop: "0px", fontWeight: "600", textDecoration: "underline"}}>Revenue KPI Cards</h3>
                <div style={{ gap: "15px", flexWrap: "wrap", paddingTop: "10px" }}>
                {Object.entries(taxData).map(([key, value]) => (
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
    )
}