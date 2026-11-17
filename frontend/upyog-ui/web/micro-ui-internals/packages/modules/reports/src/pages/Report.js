import React, { useState, useEffect } from 'react'
import { useTranslation } from 'react-i18next';
import { useParams } from "react-router-dom";

const getSearchParamsObj = (field, data, key, t) => {
  let obj = {}
  if (data[key] === undefined || data[key] === '' || field === undefined || data[key] === t('ALL'))
    return
  switch (field?.type) {
    case "string":
      if(data[key]===undefined || data[key]==="")
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

const Report = () => {
  const [isFormSubmitted, setIsFormSubmitted] = useState(false)
  const { moduleName, reportName } = useParams();
  const { t } = useTranslation()
  const [filter, setFilter] = useState([])
  const [searchData, setSearchData] = useState({})
  const { isLoading: SearchFormIsLoading, data: SearchFormUIData } = Digit.Hooks.reports.useReportMeta.fetchMetaData(moduleName, reportName, "pb.amritsar")

  const { isLoading: isLoadingReportsData, data: ReportsData } = Digit.Hooks.reports.useReportMeta.fetchReportData(moduleName, reportName, "pb.amritsar", filter, {
    //enabled: !!filter.length > 0
    enabled: isFormSubmitted
  })


  const SearchApplication = Digit.ComponentRegistryService.getComponent("ReportSearchApplication");

  const onSubmit = (data) => {
    
    setSearchData(data)   
    const reportData = SearchFormUIData.reportDetails.searchParams;
    let searchParams = []
    Object.keys(data).map((key) => {
      const field = reportData.filter(field => field.name === key)[0]
      const obj = getSearchParamsObj(field, data, key, t)
      if (obj)
        searchParams.push(obj)
    })

    setFilter(searchParams)
    setIsFormSubmitted(true)
  }

  return (
    <SearchApplication
      onSubmit={onSubmit}
      isLoading={SearchFormIsLoading}
      data={SearchFormUIData}
      tableData={!isLoadingReportsData && ReportsData?.reportData.length > 0 ? ReportsData : { display: "ES_COMMON_NO_DATA" }}
      isTableDataLoading={isLoadingReportsData}
      Count={ReportsData?.reportData.length}
      searchData={searchData}
      reportName={reportName}
    />
  )
}

export default Report