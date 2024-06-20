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
  prtParams,
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

  // WmsPrId,
  // WmsPrBillDate,
  // WmsPrEstNumber,
  // WmsPrEstWorkCost,
  // WmsPrPaymentDate,
  // WmsPrPrjName,
  // WmsPrSchName,
  // WmsPrSTA,
  // WmsPrStatus,
  // WmsPrTypeOfWork,
  // WmsPrWorkName,


  const getData = () => {
    return data?.WMSProjectRegisterApplication?.map((original) => ({
      [t("WMS_PR_ID_LABEL")]: GetCell(original?.WmsPrId?.pr_id || ""),
      [t("WMS_PR_SCH_NAME_LABEL")]: GetCell(original?.WmsPrSchName?.scheme_name || ""),
      [t("WMS_PR_PROJECT_NAME_LABEL")]: GetCell(original?.WmsPrPrjName?.project_name || ""),
      [t("WMS_PR_WORK_NAME_LABEL")]: GetCell(original?.WmsPrWorkName?.work_name || ""),
      [t("WMS_PR_WORK_TYPE_LABEL")]: GetCell(original?.WmsPrTypeOfWork?.work_type || ""),
      [t("WMS_PR_ESTNUMBER_NAME_LABEL")]: GetCell(original?.WmsPrEstNumber?.estimated_number || ""),
      [t("WMS_PR_ESTWORKCOST_NAME_LABEL")]: GetCell(original?.WmsPrEstWorkCost?.estimated_work_cost || ""),
      [t("WMS_PR_STA_NAME_LABEL")]: GetCell(original?.WmsPrSTA?.sanctioned_tender_amount || ""),
      [t("WMS_PR_STATUS_LABEL")]: GetCell(original?.WmsPrStatus?.status_name || ""),
      [t("WMS_PR_START_DATE_LABEL")]: GetCell(original?.WmsPrBillDate?.bill_received_till_date || ""),
      // [t("WMS_PR_PAYMENT_DATE_LABEL")]: GetCell(original?.WmsPrPaymentDate?.payment_received_till_date || ""),

      
    }));
  };
  const serviceRequestIdKey = (original) => {return `${searchParams?.tenantId}/${original?.[t("WMS_PR_NAME_EN_LABEL")]}`};

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
            onPrt={onPrt}
            searchParams={searchParams}
            searchFields={searchFields}
            linkPrefix={linkPrefix}
            prtParams={prtParams}
            filterComponent={filterComponent}
            serviceRequestIdKey={serviceRequestIdKey}
          />
        </div>
      </div>
    </div>
  );
};

export default MobileList;
