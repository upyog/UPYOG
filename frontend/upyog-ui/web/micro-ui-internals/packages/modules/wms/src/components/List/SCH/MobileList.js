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
  onScht,
  parentRoute,
  searchParams,
  schtParams,
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
    return data?.SCHApplications?.map((original) => ({
      [t("WMS_SCH_NAME_EN_LABEL")]: GetCell(original?.WmsSchNameEn?.scheme_name_en || ""),
      [t("WMS_SCH_NAME_REG_LABEL")]: GetCell(original?.WmsSchNameReg?.scheme_name_reg || ""),
      [t("WMS_SCH_FUND_LABEL")]: GetCell(original?.WmsSchFund?.fund || ""),
      [t("WMS_SCH_SOURCE_OF_FUND_LABEL")]: GetCell(original?.WmsSchSourceOfFund?.source_of_fund || ""),
      [t("WMS_SCH_DESC_OF_SCHEME_LABEL")]: GetCell(original?.WmsSchDescriptionOfScheme?.description_of_the_scheme || ""),
      [t("WMS_SCH_START_DATE_LABEL")]: GetCell(original?.WmsSchStartDate?.start_date || ""),
      [t("WMS_SCH_END_DATE_LABEL")]: GetCell(original?.WmsSchEndDate?.end_date || ""),
    }));
  };
  const serviceRequestIdKey = (original) => {return `${searchParams?.tenantId}/${original?.[t("WMS_SCH_NAME_EN_LABEL")]}`};

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
            onScht={onScht}
            searchParams={searchParams}
            searchFields={searchFields}
            linkPrefix={linkPrefix}
            schtParams={schtParams}
            filterComponent={filterComponent}
            serviceRequestIdKey={serviceRequestIdKey}
          />
        </div>
      </div>
    </div>
  );
};

export default MobileList;
