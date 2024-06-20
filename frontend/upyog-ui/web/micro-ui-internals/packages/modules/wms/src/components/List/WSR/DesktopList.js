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
        Header: t("WMS_WSR_ID_LABEL"),
        disableDrtBy: true,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.wsr_id}`);
        },
      },
      {
        Header: t("WMS_WSR_PROJECT_NAME_LABEL"),
        disableDrtBy: false,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.project_name}`);
        },
      },
      {
        Header: t("WMS_WSR_WORK_NAME_LABEL"),
        disableDrtBy: false,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.work_name}`);
        },
      },
      {
        Header: t("WMS_WSR_ACTIVITY_LABEL"),
        disableDrtBy: true,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.activity_name}`);
        },
      },

      {
        Header: t("WMS_WSR_EMPLOYEE_NAME_LABEL"),
        disableDrtBy: true,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.employee_name}`);
        },
      },
      
      {
        Header: t("WMS_WSR_ROLE_LABEL"),
        disableDrtBy: true,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.role_name}`);
        },
      },
      
      {
        Header: t("WMS_WSR_START_DATE_LABEL"),
        disableDrtBy: true,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.start_date}`);
        },
      },

      {
        Header: t("WMS_WSR_END_DATE_LABEL"),
        disableDrtBy: true,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.end_date}`);
        },
      },
      
      {
        Header: t("WMS_WSR_REMARKS_LABEL"),
        disableDrtBy: true,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.remarks_content}`);
        },
      },
      {
        Header: t("WMS_WSR_CONTROLS_LABEL"),
        disableDrtBy: true,
        Cell: ({ row }) => {
          return (
            <span className="link">
              <Link to={`/upyog-ui/citizen/wms/wsr-details/${row.original.wsr_id}`}>{t("WMS_WSR_EDIT_LABEL")}</Link>
            </span>
          );
        },
      },
      // {
      //   Header: t("WMS_WSR_CONTROLS2_LABEL"),
      //   disableDrtBy: true,
      //   Cell: ({ row }) => {
      //     return (
      //       <span className="link">
      //         <Link to={`/upyog-ui/citizen/wms/pma-home/${row.original.wsr_id}`}>{t("WMS_WSR_COMMON_CREATE_ACTIVITY_HEADER")}</Link>
      //       </span>
      //     );
      //   },
      // },
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
              maxWidth: cellInfo.column.Header == t("WMS_WSR_ID_LABEL") ? "150px" : "",
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
        onDrt={props.onDrt}
        disableDrt={props.disableDrt}
        wsrtParams={props.wsrtParams}
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
                text: t("WMS_WSR_COMMON_CREATE_HEADER"),
                link: "/upyog-ui/citizen/wms/wsr-create",
                businessService: "WMS",
                roles: ["CITIZEN"],
              },
/*               {
                text: t("WMS_WSR_COMMON_CREATE_ACTIVITY_HEADER"),
                link: "/upyog-ui/citizen/wms/wsr-activity-home",
                businessService: "WMS",
                roles: ["CITIZEN"],
              }, */
            ]}
            headerText={t("WMS_WSR_CREATE_PAGE_LABEL")}
            businessService={props.businessService}
          />         
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
