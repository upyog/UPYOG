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
  wsrtParams,
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
    return data?.WMSWorkStatusReportApplication?.map((original) => ({
      [t("WMS_WSR_ID_LABEL")]: GetCell(original?.WmsWsrId?.wsr_id || ""),
      [t("WMS_WSR_PROJECT_NAME_LABEL")]: GetCell(original?.WmsWsrPrjName?.project_name || ""),
      [t("WMS_WSR_WORK_NAME_LABEL")]: GetCell(original?.WmsWsrWorkName?.work_name || ""),
      [t("WMS_WSR_ACTIVITY_LABEL")]: GetCell(original?.WmsWsrActivity?.activity_name || ""),
      [t("WMS_WSR_EMPLOYEE_NAME_LABEL")]: GetCell(original?.WmsWsrEmployee?.employee_name || ""),
      [t("WMS_WSR_ROLE_LABEL")]: GetCell(original?.WmsWsrRole?.role_name || ""),
      [t("WMS_WSR_START_DATE_LABEL")]: GetCell(original?.WmsWsrStartDate?.start_date || ""),
      [t("WMS_WSR_END_DATE_LABEL")]: GetCell(original?.WmsWsrEndDate?.end_date || ""),
      [t("WMS_WSR_REMARKS_LABEL")]: GetCell(original?.WmsWsrRemarks?.remarks_content || ""),
    }));
  };
  const serviceRequestIdKey = (original) => {return `${searchParams?.tenantId}/${original?.[t("WMS_WSR_NAME_EN_LABEL")]}`};

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
            onWsrt={onWsrt}
            searchParams={searchParams}
            searchFields={searchFields}
            linkPrefix={linkPrefix}
            wsrtParams={wsrtParams}
            filterComponent={filterComponent}
            serviceRequestIdKey={serviceRequestIdKey}
          />
        </div>
      </div>
    </div>
  );
};

export default MobileList;
