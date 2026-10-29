import React, { useState, useEffect, useMemo } from 'react';
import { useTranslation } from 'react-i18next';
import { useParams } from "react-router-dom";
import { Loader, Card, Header } from "@upyog/digit-ui-react-components";

const getSearchParamsObj = (field, data, key, t) => {
  let obj = {}
  if (data[key] === undefined || data[key] === '' || field === undefined || data[key] === t('ALL'))
    return
  
  switch (field?.type) {
    case "string":
      if(data[key] === undefined || data[key] === "")
        return
      obj["name"] = key
      obj["input"] = data[key];
      return obj 
      
    case "singlevaluelist":
      var defaultValueObj = field.defaultValue
      var isLoc = field.localisationRequired;
      var input
      if (isLoc) {
        input = Object.keys(defaultValueObj).find(el => t(defaultValueObj[el]) === data[key])
      } else {
        input = Object.keys(defaultValueObj).find(el => defaultValueObj[el] === data[key])
      }
      obj["name"] = key
      obj["input"] = input;
      return obj

    case "multivaluelist":
      if(data?.[key]?.[0]?.name === "All")
       return 
      var defaultValueObj = field.defaultValue
      var isLoc = field.localisationRequired;
      var input
      if (isLoc) {
        input = data[key].map(selection => Object.keys(defaultValueObj).find(el => t(defaultValueObj[el]) === t(selection.name)))
      } else {
        input = data[key].map(selection => Object.keys(defaultValueObj).find(el => defaultValueObj[el] === selection.name))
      }
      obj["name"] = key
      obj["input"] = input;
      return obj
      
    case "epoch":
      obj["name"] = key
      if (key === "fromDate")
        obj["input"] = new Date(data?.fromDate).getTime();
      else
        obj["input"] = new Date(data?.toDate).getTime();
      return obj
      
    default:
      return
  }
}

const EnhancedReport = (props) => {
  const [isFormSubmitted, setIsFormSubmitted] = useState(false);
  const [tabLabel, setTabLabel] = useState("");
  const { moduleName: moduleNameParam, reportName: reportNameParam } = useParams();
  const moduleName = moduleNameParam || props.moduleName;
  const reportName = reportNameParam || props.reportName;
  const { t } = useTranslation();
  const [filter, setFilter] = useState([]);
  const [searchData, setSearchData] = useState({});
  
  // Get tenant ID from session or default
  const tenantId = Digit.ULBService.getCurrentTenantId() || "as";
  // Fetch report metadata
  const { isLoading: SearchFormIsLoading, data: SearchFormUIData, error: metaError } = 
    Digit.Hooks.reports.useReportMeta.fetchMetaData(moduleName, reportName, tenantId);

  // Fetch report data
  const { isLoading: isLoadingReportsData, data: ReportsData, error: dataError } = 
    Digit.Hooks.reports.useReportMeta.fetchReportData(moduleName, reportName, tenantId, filter, {
      enabled: isFormSubmitted
    });

    console.log("SearchFormUIData", SearchFormUIData, "");

  const SearchApplication = Digit.ComponentRegistryService.getComponent("ReportSearchApplication");

  const processedReportsData = useMemo(() => {
    if (!ReportsData) return ReportsData;
    const data = JSON.parse(JSON.stringify(ReportsData));
    if (data?.reportHeader && data?.reportData) {
      data.reportHeader.forEach((header, index) => {
        if (header.type === 'epoch' || header.type === 'date' || header.type === 'timestamp') {
          data.reportData.forEach(row => {
            if (row[index]) {
              row[index] = Digit.DateUtils.ConvertEpochToDate(row[index]);
            }
          });
        }
      });
    }
    return data;
  }, [ReportsData]);

  const onSubmit = (data) => {
    setSearchData(data);   
    const reportData = SearchFormUIData?.reportDetails?.searchParams || [];
    let searchParams = [];
    
    Object.keys(data).forEach((key) => {
      const field = reportData.find(field => field.name === key);
      const obj = getSearchParamsObj(field, data, key, t);
      if (obj) {
        searchParams.push(obj);
      }
    });

    setFilter(searchParams);
    setIsFormSubmitted(true);
  };

  useEffect(() => {
    if (SearchFormUIData?.reportDetails && !isFormSubmitted) {
      const searchParams = SearchFormUIData?.reportDetails?.searchParams || [];
      const hasMandatory = searchParams.some(param => param.isMandatory);
      if (!hasMandatory) onSubmit({});
    }
  }, [SearchFormUIData]);

  const updateTabLabel = (label) => {
    setTabLabel(label);
  };

  // Error handling
  useEffect(() => {
    if (metaError) {
      console.error("Report metadata error:", metaError);
    }
    if (dataError) {
      console.error("Report data error:", dataError);
    }
  }, [metaError, dataError]);

  if (SearchFormIsLoading) {
    return <Loader />;
  }

  if (metaError) {
    return (
      <Card>
        <Header>{t("REPORTS_ERROR_LOADING_METADATA")}</Header>
        <p>{metaError.message || t("REPORTS_TRY_AGAIN_LATER")}</p>
      </Card>
    );
  }

  return SearchApplication ? (
    <div style={{ margin: "8px" }}>
      <style>{`
        .report-scroll-container {
          width: 100%;
          display: block;
          overflow-x: auto;
          -webkit-overflow-scrolling: touch;
          margin-top: 20px;
          background-color: white;
          box-shadow: 0 2px 4px rgba(0,0,0,0.1);
          padding: 2px;
        }
        .report-scroll-container::-webkit-scrollbar {
          height: 10px;
        }
        .report-scroll-container::-webkit-scrollbar-track {
          background: #f1f1f1; 
        }
        .report-scroll-container::-webkit-scrollbar-thumb {
          background: #c1c1c1; 
        }
        .report-scroll-container::-webkit-scrollbar-thumb:hover {
          background: #a8a8a8; 
        }
        .reports-table {
          width: auto !important;
          min-width: 100%;
          max-width: none !important;
          border-collapse: collapse;
        }
        .reports-table th, .reports-table td {
          border: 1px solid #ddd;
          padding: 12px;
          text-align: left;
          white-space: nowrap !important;
        }
        .reports-table .cell-text {
          white-space: nowrap !important;
        }
        .reports-table th {
          background-color: #a82227;
          color: white;
          font-weight: bold;
        }
        .reports-table td {
          color: #0b0c0c;
        }
        .reports-table tr:hover {
          background-color: #f5f5f5;
        }
        /* Styles for the HTML snippet structure provided */
        .report-result-table {
          margin-top: 20px;
          background: white;
          padding: 16px;
          box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .report-table-header th, .report-header-cell, .dataTable th {
          background-color: #a82227 !important;
          color: white !important;
          padding: 12px !important;
          font-weight: bold !important;
          border: 1px solid #ddd !important;
        }
        .dataTable td {
          padding: 10px !important;
          border: 1px solid #ddd !important;
        }
        .multilink-link-button {
          color: #a82227 !important;
        }
      `}</style>
      <SearchApplication
        onSubmit={onSubmit}
        isLoading={SearchFormIsLoading}
        data={SearchFormUIData}
        tableData={!isLoadingReportsData && processedReportsData?.reportData?.length > 0 ? processedReportsData : { display: "ES_COMMON_NO_DATA" }}
        isTableDataLoading={isLoadingReportsData}
        Count={processedReportsData?.reportData?.length || 0}
        searchData={searchData}
        reportName={reportName}
        moduleName={moduleName}
        tabLabel={tabLabel}
        updateTabLabel={updateTabLabel}
        tenantId={tenantId}
      />
    </div>
  ) : <Loader />;
};

export default EnhancedReport;