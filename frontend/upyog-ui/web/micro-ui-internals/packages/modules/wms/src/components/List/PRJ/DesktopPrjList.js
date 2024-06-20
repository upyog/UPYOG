import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import DataTable from "./../DataTable";
import { Card, Loader } from "@egovernments/digit-ui-react-components";
import DataLinks from "./../DataLinks";
import SearchData from "./../search";
import { Link } from "react-router-dom";

const DesktopPrjList = ({ tableConfig, filterComponent, ...props }) => {
  const { t } = useTranslation();
  const tenantIds = Digit.SessionStorage.get("WMS_TENANTS");
  const GetCell = (value) => <span className="cell-text">{t(value)}</span>;
  const data = props?.data;
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [FilterComponent, setComp] = useState(() => Digit.ComponentRegistryService?.getComponent(filterComponent));

  const columns = React.useMemo(() => {
    return [
      {
        Header: t("WMS_PRJ_ID_LABEL"),
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.project_id}`);
        },
      },
      {
        Header: t("WMS_PRJ_PROJECT_NAME_EN_LABEL"),
        disableSortBy: false,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.project_name_en}`);
        },
      },
      {
        Header: t("WMS_PRJ_PROJECT_NAME_REG_LABEL"),
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.project_name_reg}`);
        },
      },
      {
        Header: t("WMS_PRJ_PROJECT_NUMBER_LABEL"),
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.project_number}`);
        },
      },
      
      {
        Header: t("WMS_PRJ_APPROVAL_NUMBER_LABEL"),
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.approval_number}`);
        },
      },
      {
        Header: t("WMS_PRJ_CONTROLS_LABEL"),
        disableSortBy: true,
        Cell: ({ row }) => {
          return (
            <span className="link">
              <Link to={`/upyog-ui/citizen/wms/prj-details/${row.original.project_id}`}>{t("WMS_PRJ_EDIT_LABEL")}</Link>
            </span>
          );
        },
      },
    ];
  }, []);

  let result;
  if (props.isLoading) {
    result = <Loader />;
  } else if (data?.length === 0) {
    result = (
      <Card style={{ marginTop: 20 }}>
        {/* TODO Change localization key */}
        {t("COMMON_TABLE_NO_RECORD_FOUND")
          .split("\\n")
          .map((text, index) => (
            <p key={index} style={{ textAlign: "center" }}>
              {text}
            </p>
          ))}
      </Card>
    );
  } else if (data?.length > 0) {
    result = (
      <DataTable
        t={t}
        data={data}
        columns={columns}
        getCellProps={(cellInfo) => {
          return {
            style: {
              maxWidth: cellInfo.column.Header == t("WMS_PRJ_ID_LABEL") ? "150px" : "",
              padding: "20px 18px",
              fontSize: "16px",
              minWidth: "150px",
            },
          };
        }}
        onPageSizeChange={props.onPageSizeChange}
        currentPage={props.currentPage}
        onNextPage={props.onNextPage}
        onPrevPage={props.onPrevPage}
        pageSizeLimit={props.pageSizeLimit}
        onSort={props.onSort}
        disableSort={props.disableSort}
        sortParams={props.sortParams}
        totalRecords={props.totalRecords}
      />
    );
  }

  return (
    <div className="inbox-container">
      {!props.isSearch && (
        <div className="filters-container">
          <DataLinks
            parentRoute={props.parentRoute}
            allLinks={[
              {
                text: t("WMS_COMMON_CREATE_HEADER"),
                link: "/upyog-ui/citizen/wms/prjmst-create",
                businessService: "WMS",
                roles: ["CITIZEN"],
              },
            ]}
            headerText={t("WMS_PRJ_CREATE_HEADER")}
            businessService={props.businessService}
          />
          {/* <div>
            {
              <FilterComponent
                defaultSearchParams={props.defaultSearchParams}
                onFilterChange={props.onFilterChange}
                searchParams={props.searchParams}
                type="desktop"
                tenantIds={tenantIds}
              />
            }
          </div> */}
        </div>
      )}
      <div style={{ flex: 1 }}>
        <SearchData
          defaultSearchParams={props.defaultSearchParams}
          onSearch={props.onSearch}
          type="desktop"
          tenantIds={tenantIds}
          searchFields={props.searchFields}
          isInboxPage={!props?.isSearch}
          searchParams={props.searchParams}
        />
        <div className="result" style={{ marginLeft: !props?.isSearch ? "24px" : "", flex: 1 }}>
          {result}
        </div>
      </div>
    </div>
  );
};

export default DesktopPrjList;
