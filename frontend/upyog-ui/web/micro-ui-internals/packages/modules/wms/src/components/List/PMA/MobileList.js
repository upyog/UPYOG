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
  onPmat,
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
    return data?.PMAApplications?.map((original) => ({
      [t("WMS_PMA_ID_LABEL")]: GetCell(original?.WmsPmaId?.pma_id || ""),
      [t("WMS_PMA_DESC_OF_ITEM_LABEL")]: GetCell(original?.WmsPmaDescriptionOfItem?.description_of_the_item || ""),
      [t("WMS_PMA_PERCENT_NAME_LABEL")]: GetCell(original?.WmsPmaPercent?.percentage_weightage || ""),
      [t("WMS_PMA_START_DATE_LABEL")]: GetCell(original?.WmsPmaStartDate?.start_date || ""),
      [t("WMS_PMA_END_DATE_LABEL")]: GetCell(original?.WmsPmaEndDate?.end_date || ""),
      
    }));
  };
  const serviceRequestIdKey = (original) => {return `${searchParams?.tenantId}/${original?.[t("WMS_PMA_NAME_EN_LABEL")]}`};

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
            onPmat={onPmat}
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
