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
  onDet,
  parentRoute,
  searchParams,
  drtParams,
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
    return data?.DRApplications?.map((original) => ({
      [t("WMS_DR_ID_LABEL")]: GetCell(original?.WmsDrId?.dr_id || ""),
      [t("WMS_DR_PROJECT_NAME_LABEL")]: GetCell(original?.WmsDrPrjName?.project_name || ""),
      [t("WMS_DR_WORK_NAME_LABEL")]: GetCell(original?.WmsDrWorkName?.work_name || ""),
      [t("WMS_DR_ML_NAME_LABEL")]: GetCell(original?.WmsDrMLName?.milestone_name || ""),
      [t("WMS_DR_PERCENT_LABEL")]: GetCell(original?.WmsDrPercent?.percent_weightage || ""),
      
    }));
  };
  const serviceRequestIdKey = (original) => {return `${searchParams?.tenantId}/${original?.[t("WMS_DR_NAME_EN_LABEL")]}`};

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
            onDrt={onDrt}
            searchParams={searchParams}
            searchFields={searchFields}
            linkPrefix={linkPrefix}
            drtParams={drtParams}
            filterComponent={filterComponent}
            serviceRequestIdKey={serviceRequestIdKey}
          />
        </div>
      </div>
    </div>
  );
};

export default MobileList;
