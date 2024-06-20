import React from "react";
import { useTranslation } from "react-i18next";
import { DataCard } from "../DataCard";
import  DataLinks  from "../DataLinks";

const MobileList = ({
  data,
  isLoading,
  isSearch,
  searchFields,
  onFilterChange,
  onSearch,
  onPhmt,
  parentRoute,
  searchParams,
  phmtParams,
  linkPrefix,
  tableConfig,
  filterComponent,
  allLinks,
}) => {
  const { t } = useTranslation();
  // const getData = () => {
  //   return data?.Employees?.map((dataObj) => {
  //     const obj = {};
  //     const columns = isSearch ? tableConfig.searchColumns() : tableConfig.inboxColumns();
  //     columns.forEach((el) => {
  //       if (el.mobileCell) obj[el.Header] = el.mobileCell(dataObj);
  //     });
  //     return obj;
  //   });
  // };

  const GetCell = (value) => <span className="cell-text">{t(value)}</span>;
const GetSlaCell = (value) => {
  return value == "INACTIVE" ? <span className="sla-cell-error">{ t(value )|| ""}</span> : <span className="sla-cell-success">{ t(value) || ""}</span>;
};
  const getData = () => {
    return data?.WMSPhysicalFinancialMilestoneApplications?.map((original) => ({
      [t("WMS_PHM_ID_LABEL")]: GetCell(original?.WmsPhmId?.phm_id || ""),
      [t("WMS_PHM_PROJECT_NAME_LABEL")]: GetCell(original?.WmsPhmPrjName?.project_name || ""),
      [t("WMS_PHM_WORK_NAME_LABEL")]: GetCell(original?.WmsPhmWorkName?.work_name || ""),
      [t("WMS_PHM_ML_NAME_LABEL")]: GetCell(original?.WmsPhmMLName?.milestone_name || ""),
      [t("WMS_PHM_PERCENT_LABEL")]: GetCell(original?.WmsPhmPercent?.percent_weightage || ""),
      
    }));
  };
  const serviceRequestIdKey = (original) => {return `${searchParams?.tenantId}/${original?.[t("WMS_PHM_NAME_EN_LABEL")]}`};

  return (
    <div style={{ padding: 0 }}>
      <div className="inbox-container">
        <div className="filters-container">
          {!isSearch && <DataLinks linkPrefix={parentRoute} allLinks={allLinks} isMobile={true} />}
          <DataCard
                t={t}
            data={getData()}
            onFilterChange={onFilterChange}
            isLoading={isLoading}
            isSearch={isSearch}
            onSearch={onSearch}
            onPhmt={onPhmt}
            searchParams={searchParams}
            searchFields={searchFields}
            linkPrefix={linkPrefix}
            phmtParams={phmtParams}
            filterComponent={filterComponent}
            serviceRequestIdKey={serviceRequestIdKey}
          />
        </div>
      </div>
    </div>
  );
};

export default MobileList;
