import React, { useEffect, useMemo, useState } from "react";
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from "chart.js";
import { Pie, Bar } from "react-chartjs-2";
import { useTranslation } from "react-i18next";


export const LegacyULBDetails = ({ dashboardData, filteredData, resetTriggered }) => {
    
    const [tableList, setTableList] = useState([]);
    const [tableColumnList, setTableColumnList] = useState([]);
    const [page, setPage] = useState(0);      // zero-based index
    const [limit, setLimit] = useState(10);
    const [totalCount, setTotalCount] = useState(0);
    const [loading, setLoading] = useState(false);

    const [tableKey, setTableKey] = useState('LegacyPropertyReport');
    const [isShowTable, setIsShowTable] = useState(true);
    const [isShowTableName, setIsShowTableName] = useState(false);

    const columnConfig = {
        totalTaxCollected: [
        { key: "slNo", label: "Sl No" },
        { key: "propertyId", label: "UPIN" },
        { key: "ownerName", label: "Name of Owner(s)" },
        { key: "wardNo", label: "Ward No" },
        { key: "localityName", label: "Locality Name" },
        { key: "dateOfPayment", label: "Date of Payment" },
        { key: "totalPtCollected", label: "Total PT Collected" },
        ],

        penalty: [
        { key: "slNo", label: "Sl No" },
        { key: "propertyId", label: "UPIN" },
        { key: "ownerName", label: "Name of Owner(s)" },
        { key: "wardNo", label: "Ward No" },
        { key: "localityName", label: "Locality Name" },
        { key: "dateOfPayment", label: "Date of Payment" },
        { key: "dueTaxAmount", label: "Due Tax Amount" },
        { key: "penaltyAmount", label: "Penalty Amount" },
        { key: "totalCollected", label: "Total Collected" },
        ],
        interest: [
        { key: "slNo", label: "Sl No" },
        { key: "propertyId", label: "UPIN" },
        { key: "ownerName", label: "Name of Owner(s)" },
        { key: "wardNo", label: "Ward No" },
        { key: "localityName", label: "Locality Name" },
        { key: "dateOfPayment", label: "Date of Payment" },
        { key: "dueTaxAmount", label: "Due Tax Amount" },
        { key: "interestAmount", label: "Interest Amount" },
        { key: "totalCollected", label: "Total Collected" },
        ],
        advance: [
        { key: "slNo", label: "Sl No" },
        { key: "propertyId", label: "UPIN" },
        { key: "ownerName", label: "Name of Owner(s)" },
        { key: "wardNo", label: "Ward No" },
        { key: "localityName", label: "Locality Name" },
        { key: "dateOfPayment", label: "Date of Payment" },
        { key: "dueTaxAmount", label: "Due Tax Amount" },
        { key: "advanceAmount", label: "Advance Amount" },
        { key: "totalCollected", label: "Total Collected" },
        ],
        arrears: [
        { key: "slNo", label: "Sl No" },
        { key: "propertyId", label: "UPIN" },
        { key: "ownerName", label: "Name of Owner(s)" },
        { key: "wardNo", label: "Ward No" },
        { key: "localityName", label: "Locality Name" },
        { key: "dateOfPayment", label: "Date of Payment" },
        { key: "dueTaxAmount", label: "Due Tax Amount" },
        { key: "arrearsAmount", label: "Arrears Amount" },
        { key: "totalCollected", label: "Total Collected" },
        ],
    };

    const defaultColumns = [
        { key: "slNo", label: "Sl No" },
        { key: "propertyId", label: "UPIN" },
        { key: "ownerName", label: "Name of Owner(s)" },
        { key: "city", label: "City" },
        { key: "dateOfRegistration", label: "Date of Registration" },
        { key: "totalAmountPaid", label: "Total Amount Paid" },
        { key: "paymentDate", label: "Payment Date" },
    ];
    
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

     const setTableColumns = (key) => {  
        // console.log("Setting table columns for key:", key);
        setTableColumnList(columnConfig[key] || defaultColumns);    
    }
  
    const fetchProperties = async (pageNo = page, pageLimit = limit, key = 'LegacyPropertyReport') => {
        setLoading(true);
        try {
        const offset = pageNo * pageLimit;
        setTableColumns(key)

        // const res = await fetch(
        //   `/api/properties?offset=${offset}&limit=${pageLimit}`
        // );
        let filters = {...filteredData, searchKey: key};
        // console.log("filters==", filters);
        let dashboardPropertySearchData = await Digit.PTService.dashboardPropertySearch(filters, offset, pageLimit);
        // console.log("dashboardPropertySearchData==", dashboardPropertySearchData);
        if(dashboardPropertySearchData && dashboardPropertySearchData.Report && dashboardPropertySearchData.Report.data[0]?.properties && dashboardPropertySearchData.Report.data[0]?.properties.length > 0){
            const formattedProperties = mapProperties(dashboardPropertySearchData.Report.data[0]?.properties, key);
            // console.log(formattedProperties);
            // setTableList(formattedProperties);
            setTableList((prev) => [...prev, ...(formattedProperties || [])]);
            setIsShowTable(true);
            setIsShowTableName(key);
            // console.log("dashboardPropertySearchData?.Report?.data[0]?.total==", dashboardPropertySearchData?.Report?.data[0]?.total);
            setTotalCount(dashboardPropertySearchData?.Report?.data[0]?.total || 0);

        }
        // setTableList(dashboardPropertySearchData?.Report?.data[0]?.properties || []);
        } catch (err) {
        console.error(err);
        } finally {
        setLoading(false);
        }
    };

    function mapProperties(properties = [], key) {
        return properties.map((property) => {
            const address = property.address || {};
            const locality = address.locality || {};
            const owners = property.owners || [];

            // 🏠 Build Address String
            const propertyAddress = [
            address.village && `Village: ${address.village}`,
            address.doorNo && `Door No.: ${address.doorNo}`,
            address.pattaNo && `Patta No.: ${address.pattaNo}`,
            address.street && `Street: ${address.street}`,
            address.commonNameOfBuilding &&
                `Common Name Of Building: ${address.commonNameOfBuilding}`,
            address.principalRoadName &&
                `Principal Road Name: ${address.principalRoadName}`,
            locality.name && `Locality: ${locality.name}`,
            address.city && `City: ${address.city}`,
            address.pincode && `Pin: ${address.pincode}`,
            ]
            .filter(Boolean)
            .join(", ");

            // 👤 Build Owner Details String
            const ownerDetails = owners
            .map((owner, index) => {
                const ownerIndex = owners.length > 1 ? index + 1 : "";
                return [
                `Owner${ownerIndex ? " " + ownerIndex : ""} Name: ${owner.name}`,
                owner.mobileNumber &&
                    `Owner${ownerIndex ? " " + ownerIndex : ""} Contact No.: ${
                    owner.mobileNumber
                    }`,
                ]
                .filter(Boolean)
                .join(", ");
            })
            .join(", ");


            return {
              propertyId: property.propertyId,
              ownerName: owners.map((o) => o.name).join(", "),
              city: address?.city || "N/A",

            //   localityName: locality.name,
              dateOfRegistration: property.auditDetails?.createdTime
                ? new Date(property.auditDetails.createdTime).toLocaleDateString('en-GB')
                : "N/A",
                totalAmountPaid: property?.Payments[0]?.totalAmountPaid || 0,
                paymentDate: property.Payments[0]?.auditDetails?.createdTime
                ? new Date(property.Payments[0]?.auditDetails?.createdTime).toLocaleDateString('en-GB')
                : "N/A",
              // t(`COMMON_PROPTYPE_${property?.propertyType?.split('.').join('_')}`),
              // propertyAddress,
              // ownerDetails,
            };
        });
    }

    // Reset table-related state when parent `filteredData` changes (e.g., when Search is clicked)
    useEffect(() => {
      if (filteredData === null || filteredData === undefined) return;
      setTableList([]);
      setTableColumnList([]);
      setPage(0);
      setLimit(10);
      setTotalCount(0);
      setLoading(false);
      setTableKey('LegacyPropertyReport');
      setIsShowTable(false);
      setIsShowTableName(false);
    }, [filteredData]);

    // Also reset table when parent explicitly triggers a reset
    useEffect(() => {
      if (!resetTriggered) return;
      setTableList([]);
      setTableColumnList([]);
      setPage(0);
      setLimit(10);
      setTotalCount(0);
      setLoading(false);
      setTableKey('LegacyPropertyReport');
      setIsShowTable(false);
      setIsShowTableName(false);
    }, [resetTriggered]);

    const totalPages = Math.ceil(totalCount / limit);

    const goFirst = () => setPage(0);
    const goPrev = () => setPage((p) => Math.max(p - 1, 0));
    const goNext = () => {
        const nextPage = page + 1;

        // fetch only if data not already loaded
        // console.log("tableList.length==", tableList.length, "nextPage==", nextPage, "limit==", limit);
        if (tableList.length <= nextPage * limit) {
        fetchProperties(nextPage, limit, tableKey);
        }
        setPage(nextPage);
        // setPage((p) => Math.min(p + 1, totalPages - 1));
    }
    const goLast = () => setPage(totalPages - 1);

    useEffect(() => {
        if (!tableKey) return;
        fetchProperties(0, limit, tableKey); // Fetch first page of data for the default key when component mounts or tableKey changes
    }, [tableKey]);

    const onShowTable = async (key,value) =>{
        setTableKey(key);
        // fetchProperties(0, limit); // Fetch first page of data for the selected KPI card
        // let filters = {...filteredData, searchKey: key};
        // console.log("filters==", filters);
        // let dashboardPropertySearchData = await Digit.PTService.dashboardPropertySearch(filters);
        // console.log("dashboardPropertySearchData==", dashboardPropertySearchData);
        // if(dashboardPropertySearchData && dashboardPropertySearchData.Report && dashboardPropertySearchData.Report.data[0]?.properties && dashboardPropertySearchData.Report.data[0]?.properties.length > 0){
        //   const formattedProperties = mapProperties(dashboardPropertySearchData.Report.data[0]?.properties);
        //   console.log(formattedProperties);
        //   console.log("Clicked KPI:", key, "Value:", value);
        //   setTableList(formattedProperties);
        //   setIsShowTable(true);
        //   setIsShowTableName(key);
        // }
        
        // Here, you can set the tableList state with actual data fetched based on the key
    }
    const onDownloadToExcel = (tableName) =>{
        // Logic to download the displayed table data as an Excel file
        // console.log("Download Excel for table:", tableName);
        let name = tableName.replace(/([A-Z])/g, " $1")        // Add space before capital letters
        .replace(/^./, (str) => str.toUpperCase()) // Capitalize first letter
        .split(" ")                        // Split into words
        .map(word => word.charAt(0).toUpperCase() + word.slice(1)) // Capitalize each word
        .join(" ");
        const today = new Date();
        const formattedDate = `${String(today.getDate()).padStart(2, "0")}-${String(today.getMonth() + 1).padStart(2, "0")}-${today.getFullYear()}`;

        const fileName = `${name}_${formattedDate}`;
        let exportData = tableList.map((item, index) => {
        let obj = {};
        tableColumnList.forEach((col) => {
            if(col.key === "slNo"){
            obj[col.label] = index + 1;
            } else {
            obj[col.label] = item[col.key];
            }
        });
        return obj;
        });
        // You can use libraries like SheetJS (xlsx) to generate and download Excel files
        Digit.Download.Excel(exportData, fileName);
    }

    const paginatedList = tableList.slice(
        page * limit,
        page * limit + limit
    );

    return (
        <React.Fragment>
        {loading && <p>Loading...</p>}
          {isShowTable && !loading && <div style={{ width: '100%', padding: "10px", boxShadow: "0px 0px 2px 0px", marginTop: "12px", borderRadius: "5px" }} className="mt-4 p-4 box-shadow">
            <div style={{display: "flex", justifyContent: "space-between"}} >
              {/* <h3 style={{paddingTop: "20px", fontWeight: "600", textDecoration: "underline"}}> */}
                <strong>
                  {/* {isShowTableName} */}
                          {tableKey
                              ?.replace(/([A-Z])/g, " $1")        // Add space before capital letters
                              ?.replace(/^./, (str) => str.toUpperCase()) // Capitalize first letter
                              ?.split(" ")                        // Split into words
                              ?.map(word => word.charAt(0).toUpperCase() + word.slice(1)) // Capitalize each word
                              ?.join(" ")}
                      </strong>
                      {/* </h3> */}
              <button onClick={() => onDownloadToExcel(isShowTableName)} className="btn btn-primary" style={{ marginBottom: '10px' }}>Export to Excel</button>
              </div>
              
              <table style={{ width: '100%', border: '1px solid #b7b7b7'}}>
                <thead>
                  <tr style={{background: '#eaeaea', lineHeight: '35px', padding: "0 10px"}}>
                    {tableColumnList.map((e, indx)=>{
                      return (<th key={e.key} style={{paddingLeft: "10px"}}>{e.label}</th>)
                    })}
                  </tr>
                </thead>
                {paginatedList.map((e, indx)=>{
                  return (<tbody key={e?.propertyId}><tr style={{padding: "0px 10px", borderBottom: "1px solid #bbb9b9"}} key={e?.propertyId}>
                    <td style={{paddingLeft: "10px", maxWidth: "50px"}}>{page * limit + indx + 1}</td>
                    {tableColumnList.slice(1).map((clmn, index)=>{
                      return (<td key={clmn.key} style={{paddingLeft: "10px",maxWidth: "140px"}}>{e?.[clmn.key]}</td>)
                    })}
                    
                  </tr></tbody>)
                })}
              </table>
              <div style={{ display: "flex", justifyContent: "space-between", marginTop: 15 }}>
                <div style={{ display: "flex", justifyContent: "flex-start", gap: "15px" }}>
                  <div>
                    <label>Items per page: </label>
                    <select
                      value={limit}
                      onChange={(e) => {
                        const newLimit = Number(e.target.value);
                        setLimit(newLimit);
                        setPage(0);
                        setTableList([]);
                        fetchProperties(0, newLimit, tableKey);
                      }}
                      style={{border: "1px solid"}}
                    >
                      <option value={5}>5</option>
                      <option value={10}>10</option>
                      <option value={20}>20</option>
                      <option value={50}>50</option>
                    </select>
                  </div>
                  <div>
                    {page * limit + 1} - {Math.min((page + 1) * limit, totalCount)} of {totalCount} entries

                  </div>
                </div>
                <div style={{ display: "flex", justifyContent: "flex-end", gap: "20px" }}>
                  <div>
                    {/* <button disabled={page === 0} onClick={goFirst}>
                      ⏮️
                    </button> */}
                    <button disabled={page === 0} onClick={goPrev}>
                      ◀️
                    </button>
                    {/* <button
                      disabled={page === 0}
                      onClick={() => setPage((p) => p - 1)}
                    >
                      Previous
                    </button> */}

                    <span style={{ margin: "0 10px" }}>
                      {page + 1} of {Math.ceil(totalCount / limit)}
                    </span>

                    <button disabled={page + 1 >= totalPages} onClick={goNext}>
                      ▶️
                    </button>

                    {/* <button disabled={page + 1 >= totalPages} onClick={goLast}>
                      ⏭️
                    </button> */}

                    {/* <button
                      disabled={(page + 1) * limit >= totalCount}
                      onClick={() => setPage((p) => p + 1)}
                    >
                      Next
                    </button> */}
                  </div>

                  
                </div>
              </div>
              
          </div>
          }
        </React.Fragment>
    )
}