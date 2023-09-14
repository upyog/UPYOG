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
  onSort,
  parentRoute,
  searchParams,
  sortParams,
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
    return data?.SORApplications?.map((original) => ({
      [t("WMS_SOR_CHAPTER_LABEL")]: GetCell(original?.WmsChapter?.sorChapter || ""),
      [t("WMS_SOR_DESCRIPTION_OF_ITEM_LABEL")]: GetCell(original?.WmsDescriptionOfItem?.sorDescriptionOfItem || ""),
      [t("WMS_SOR_ITEM_NO_LABEL")]: GetCell(original?.WmsItemNo?.sorItemNo.length || 0),
      [t("WMS_SOR_UNIT_LABEL")]: GetCell(original?.WmsUnit?.sorUnit.length || 0),
      [t("WMS_SOR_RATE_LABEL")]: GetCell(original?.WmsRate?.sorRate.length || 0),
      [t("WMS_SOR_NAME_LABEL")]: GetCell(original?.WmsName?.sorName || ""),
      [t("WMS_SOR_START_DATE_LABEL")]: GetCell(original?.WmsStartDate?.sorStartDate || ""),
      [t("WMS_SOR_END_DATE_LABEL")]: GetCell(original?.WmsEndDate?.sorEndDate || ""),
    }));
  };
  const serviceRequestIdKey = (original) => {return `${searchParams?.tenantId}/${original?.[t("WMS_SOR_NAME_LABEL")]}`};

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
            onSort={onSort}
            searchParams={searchParams}
            searchFields={searchFields}
            linkPrefix={linkPrefix}
            sortParams={sortParams}
            filterComponent={filterComponent}
            serviceRequestIdKey={serviceRequestIdKey}
          />
        </div>
      </div>
    </div>
  );
};

export default MobileList;
