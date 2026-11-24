import React, { useState, useEffect, useMemo, useCallback } from 'react';
import { useTranslation } from 'react-i18next';
import { useParams } from "react-router-dom";
import { Loader, Card, Header } from "@nudmcdgnpm/digit-ui-react-components";

const getSearchParamsObj = (field, data, key, t) => {
  let obj = {}
  if (data[key] === undefined || data[key] === '' || field === undefined || data[key] === t('ALL'))
    return
  switch (field?.type) {
    case "string":
      if (data[key] === undefined || data[key] === "")
        return
      obj["name"] = key
      obj["input"] = data[key];
      return obj

    case "singlevaluelist": {
      const defaultValueObj = field.defaultValue || {};
      const selectedVal = typeof data[key] === "object" ? data[key]?.code || data[key]?.name : data[key];
      const prefix = field.localisationPrefix || "";
      const input =
        Object.keys(defaultValueObj).find(
          (el) =>
            t(defaultValueObj[el]) === selectedVal ||
            defaultValueObj[el] === selectedVal ||
            t(`${prefix}${defaultValueObj[el]}`) === selectedVal ||
            t(el) === selectedVal ||
            el === selectedVal
        ) ?? selectedVal;
      if (input === undefined || input === "" || input === t("ALL") || input === "All") return;
      obj["name"] = key;
      obj["input"] = input;
      return obj;
    }

    case "multivaluelist": {
      if (!Array.isArray(data?.[key]) || data[key].length === 0 || data?.[key]?.[0]?.name === "All" || data?.[key]?.[0]?.name === t("ALL"))
        return;
      const defaultValueObj = field.defaultValue || {};
      const prefix = field.localisationPrefix || "";
      const input = data[key]
        .map((selection) => {
          const selName = typeof selection === "object" ? selection?.name : selection;
          if (selection?.code) return selection.code;
          return (
            Object.keys(defaultValueObj).find(
              (el) =>
                t(defaultValueObj[el]) === t(selName) ||
                t(defaultValueObj[el]) === selName ||
                defaultValueObj[el] === selName ||
                t(`${prefix}${defaultValueObj[el]}`) === selName ||
                t(el) === selName ||
                el === selName
            ) ?? selName
          );
        })
        .filter((v) => v !== undefined && v !== "" && v !== "All" && v !== t("ALL"));
      if (input.length === 0) return;
      obj["name"] = key;
      obj["input"] = input;
      return obj;
    }

    case "epoch":
      obj["name"] = key;

      if (key === "fromDate") {
        const from = new Date(data.fromDate);
        from.setHours(0, 0, 0, 0);
        obj["input"] = from.getTime();
      } else {
        const to = new Date(data.toDate);
        to.setHours(23, 59, 59, 999);
        obj["input"] = to.getTime();
      }

      return obj;

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
  const [ulbDefaultValue, setUlbDefaultValue] = useState(null);
  const [allUlbDefaultValue, setAllUlbDefaultValue] = useState({});
  const [isAllUlbLoaded, setIsAllUlbLoaded] = useState(false);

  // Get tenant ID from session or default
  const tenantId = Digit.ULBService.getCurrentTenantId() || "as";
  // Fetch report metadata
  const { isLoading: SearchFormIsLoading, data: SearchFormUIData, error: metaError } =
    Digit.Hooks.reports.useReportMeta.fetchMetaData(moduleName, reportName, tenantId);

  const isMiscDashboardReport =
    reportName === "MiscDashboardReport" ||
    SearchFormUIData?.reportDetails?.reportName === "MiscDashboardReport";

  const fetchUlbsByState = useCallback(
    async (selectedState = "") => {
      const masterDetail = { name: "nationalInfo" };
      if (selectedState) {
        const safeState = String(selectedState).replace(/"/g, '\\"');
        masterDetail.filter = `[?(@.stateCode=="${safeState}")]`;
      }
      const mdmsTenantId = Digit.ULBService.getStateId() || tenantId;
      try {
        const response = await Digit.MDMSService.call(mdmsTenantId, {
          tenantId: mdmsTenantId,
          moduleDetails: [
            {
              moduleName: "tenant",
              masterDetails: [masterDetail],
            },
          ],
        });
        const ulbList = response?.MdmsRes?.tenant?.nationalInfo || [];
        const nextUlbDefaultValue = {};
        ulbList
          .filter((item) => item && item.active !== false)
          .forEach((item) => {
            nextUlbDefaultValue[item.code] = item.name || item.code;
          });
        if (!selectedState) {
          setAllUlbDefaultValue(nextUlbDefaultValue);
          setIsAllUlbLoaded(true);
        }
        setUlbDefaultValue(nextUlbDefaultValue);
      } catch (error) {
        console.error("Error while loading ULB dropdown:", error);
      }
    },
    [tenantId]
  );

  useEffect(() => {
    if (isMiscDashboardReport && SearchFormUIData?.reportDetails && !isAllUlbLoaded) {
      setIsAllUlbLoaded(true);
      fetchUlbsByState("");
    }
  }, [isMiscDashboardReport, SearchFormUIData, isAllUlbLoaded, fetchUlbsByState]);

  const effectiveMetaData = useMemo(() => {
    if (!SearchFormUIData || !isMiscDashboardReport || ulbDefaultValue === null) {
      return SearchFormUIData;
    }
    const cloned = JSON.parse(JSON.stringify(SearchFormUIData));
    const searchParams = cloned?.reportDetails?.searchParams || [];
    const ulbIndex = searchParams.findIndex((field) => field.name === "ulb");
    if (ulbIndex > -1) {
      searchParams[ulbIndex].defaultValue = ulbDefaultValue;
      searchParams[ulbIndex].disabled = false;
    }
    return cloned;
  }, [SearchFormUIData, isMiscDashboardReport, ulbDefaultValue]);

  const handleStateChange = useCallback(
    (selectedValue) => {
      if (!isMiscDashboardReport) return;
      const stateField = effectiveMetaData?.reportDetails?.searchParams?.find((f) => f.name === "state");
      let stateCode = "";
      if (selectedValue && selectedValue !== t("ALL") && selectedValue !== "All") {
        const defaultValueObj = stateField?.defaultValue || {};
        const prefix = stateField?.localisationPrefix || "";
        const rawVal = typeof selectedValue === "object" ? selectedValue?.code || selectedValue?.name : selectedValue;
        stateCode =
          Object.keys(defaultValueObj).find(
            (el) =>
              t(defaultValueObj[el]) === rawVal ||
              defaultValueObj[el] === rawVal ||
              t(`${prefix}${defaultValueObj[el]}`) === rawVal ||
              t(el) === rawVal ||
              el === rawVal
          ) ?? rawVal;
      }
      if (!stateCode) {
        if (Object.keys(allUlbDefaultValue).length > 0) {
          setUlbDefaultValue(allUlbDefaultValue);
        } else {
          fetchUlbsByState("");
        }
      } else {
        fetchUlbsByState(stateCode);
      }
    },
    [isMiscDashboardReport, effectiveMetaData, allUlbDefaultValue, fetchUlbsByState, t]
  );

  const handleClearAll = useCallback(() => {
    if (!isMiscDashboardReport) return;
    if (Object.keys(allUlbDefaultValue).length > 0) {
      setUlbDefaultValue(allUlbDefaultValue);
    } else {
      fetchUlbsByState("");
    }
  }, [isMiscDashboardReport, allUlbDefaultValue, fetchUlbsByState]);

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
    const reportData = effectiveMetaData?.reportDetails?.searchParams || [];
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
    <div className="rpt-enhanced-report-spacing">
      <SearchApplication
        onSubmit={onSubmit}
        isLoading={SearchFormIsLoading}
        data={effectiveMetaData}
        tableData={!isLoadingReportsData && processedReportsData?.reportData?.length > 0 ? processedReportsData : { display: "ES_COMMON_NO_DATA" }}
        isTableDataLoading={isLoadingReportsData}
        Count={processedReportsData?.reportData?.length || 0}
        searchData={searchData}
        reportName={reportName}
        moduleName={moduleName}
        tabLabel={tabLabel}
        updateTabLabel={updateTabLabel}
        tenantId={tenantId}
        onStateChange={isMiscDashboardReport ? handleStateChange : undefined}
        onClearAll={isMiscDashboardReport ? handleClearAll : undefined}
      />
    </div>
  ) : <Loader />;
};

export default EnhancedReport;