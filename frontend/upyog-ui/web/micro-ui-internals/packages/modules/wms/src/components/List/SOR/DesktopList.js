import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import DataTable from "../DataTable";
import { Card, Loader } from "@egovernments/digit-ui-react-components";
import DataLinks from "../DataLinks";
import SearchData from "../search";
import { Link } from "react-router-dom";

const DesktopList = ({ tableConfig, filterComponent, ...props }) => {
  const { t } = useTranslation();
  const tenantIds = Digit.SessionStorage.get("WMS_TENANTS");
  const GetCell = (value) => <span className="cell-text">{t(value)}</span>;
  const GetSlaCell = (value) => {
    return value == "INACTIVE" ? <span className="sla-cell-error">{ t(value )|| ""}</span> : <span className="sla-cell-success">{ t(value) || ""}</span>;
  };
  const data = props?.data;
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const [FilterComponent, setComp] = useState(() => Digit.ComponentRegistryService?.getComponent(filterComponent));

  const columns = React.useMemo(() => {
    return [
      {
        Header: t("WMS_SOR_ID_LABEL"),
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.sor_id}`);
        },
      },
      {
        Header: t("WMS_SOR_NAME_LABEL"),
        disableSortBy: false,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.sor_name}`);
        },
      },
      {
        Header: t("WMS_SOR_ITEM_NO_LABEL"),
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.item_no}`);
        },
      },
      {
        Header: t("WMS_SOR_RATE_LABEL"),
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.rate}`);
        },
      },
      
      {
        Header: t("WMS_SOR_UNIT_LABEL"),
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.unit}`);
        },
      },
      {
        Header: t("WMS_SOR_CONTROL_LABEL"),
        disableSortBy: true,
        Cell: ({ row }) => {
          return (
            <span className="link">
              <Link to={`/upyog-ui/citizen/wms/sor-details/${row.original.sor_id}`}>{t("WMS_SOR_VIEW_LABEL")}</Link>
            </span>
          );
        },
      },{
        Header: t("WMS_SOR_CONTROLS_LABEL"),
        disableSortBy: true,
        Cell: ({ row }) => {
          return (
            <span className="link">
              <Link to={`/upyog-ui/citizen/wms/sor-edit/${row.original.sor_id}`}>{t("WMS_SOR_EDIT_LABEL")}</Link>
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
    //alert(props?.isSearch);
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
              maxWidth: cellInfo.column.Header == t("WMS_SOR_ID_LABEL") ? "150px" : "",
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
                link: "/upyog-ui/citizen/wms/sor-create",
                businessService: "WMS",
                roles: ["CITIZEN"],
              },
            ]}
            headerText={t("WMS_SOR_CREATE_HEADER")}
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

export default DesktopList;
