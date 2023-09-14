import React from "react";
import { useTranslation } from "react-i18next";
import { DataCard } from "./../DataCard";
import  DataLinks  from "./../DataLinks";

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
  const GetCell = (value) => <span className="cell-text">{t(value)}</span>;
  const getData = () => {
    return data?.ProjectApplications?.map((original) => ({
      
      [t("WMS_PRJ_PROJECT_ID_LABEL")]: GetCell(original?.WmsPrjId?.project_id || ""),
      
      [t("WMS_PRJ_PROJECT_NAME_EN_LABEL")]: GetCell(original?.WmsPrjNameEn?.project_name_en || ""),
      [t("WMS_PRJ_PROJECT_NAME_REG_LABEL")]: GetCell(original?.WmsPrjNameReg?.project_name_reg || ""),
      [t("WMS_PRJ_PROJECT_NUMBER_LABEL")]: GetCell(original?.WmsPrjNumber?.project_number || ""),
      [t("WMS_PRJ_PROJECT_DESCRIPTION_LABEL")]: GetCell(original?.WmsPrjDescription?.project_description || ""),
      [t("WMS_PRJ_APPROVAL_NUMBER_LABEL")]: GetCell(original?.WmsPrjApprovalNo?.approval_number || ""),
      [t("WMS_PRJ_DEPARTMENT_LABEL")]: GetCell(original?.WmsPrjDepartment?.department || ""),
      [t("WMS_PRJ_PROJECT_TIMELINE_LABEL")]: GetCell(original?.WmsPrjTimeLine?.project_timeline || ""),
      [t("WMS_PRJ_SCHEME_NAME_LABEL")]: GetCell(original?.WmsPrjSchemeName?.scheme_name || ""),
      [t("WMS_PRJ_SCHEME_NO_LABEL")]: GetCell(original?.WmsPrjSchemeNo?.scheme_no || ""),
      [t("WMS_PRJ_SOURCE_OF_FUND_LABEL")]: GetCell(original?.WmsPrjSourceOfFund?.source_of_fund || ""),
      [t("WMS_PRJ_APPROVAL_DATE_LABEL")]: GetCell(original?.WmsPrjApprovalDate?.approval_date || ""),
      [t("WMS_PRJ_PROJECT_START_DATE_LABEL")]: GetCell(original?.WmsPrjStartDate?.project_start_date || ""),
      [t("WMS_PRJ_PROJECT_END_DATE_LABEL")]: GetCell(original?.WmsPrjEndDate?.project_end_date || ""),
      [t("WMS_PRJ_STATUS_LABEL")]: GetCell(original?.WmsPrjStatus?.status || ""),
    }));
  };
  const serviceRequestIdKey = (original) => {return `${searchParams?.tenantId}/${original?.[t("WMS_PRJ_PROJECT_ID_LABEL")]}`};

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
