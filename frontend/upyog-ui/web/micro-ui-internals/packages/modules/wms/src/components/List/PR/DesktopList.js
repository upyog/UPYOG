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
  console.log("props  sss ",props)
  const data = props?.data;
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const [FilterComponent, setComp] = useState(() => Digit.ComponentRegistryService?.getComponent(filterComponent));

  const columns = React.useMemo(() => {
    return [
      // {
      //   Header: t("WMS_PR_ID_LABEL"),
      //   disablePrtBy: true,
      //   Cell: ({ row }) => {
      //     return GetCell(`${row.original?.pr_id}`);
      //   },
      // },
      {
        Header: t("WMS_PR_SCH_NAME_LABEL"),
        disablePrtBy: false,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.scheme_name}`);
        },
      },
      {
        Header: t("WMS_PR_PROJECT_NAME_LABEL"),
        disablePrtBy: false,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.project_name}`);
        },
      },
      {
        Header: t("WMS_PR_WORK_NAME_LABEL"),
        disablePrtBy: false,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.work_name}`);
        },
      },
      {
        Header: t("WMS_PR_WORK_TYPE_LABEL"),
        disablePrtBy: false,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.work_type}`);
        },
      },
      {
        Header: t("WMS_PR_ESTNUMBER_NAME_LABEL"),
        disablePrtBy: true,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.estimated_number}`);
        },
      },
      {
        Header: t("WMS_PR_ESTWORKCOST_NAME_LABEL"),
        disablePrtBy: true,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.estimated_work_cost}`);
        },
      },
      {
        Header: t("WMS_PR_STA_NAME_LABEL"),
        disablePrtBy: true,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.sanctioned_tender_amount}`);
        },
      },
      {
        Header: t("WMS_PR_STATUS_LABEL"),
        disablePrtBy: false,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.status_name}`);
        },
      },
      {
        Header: t("WMS_PR_START_DATE_LABEL"),
        disablePrtBy: false,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.bill_received_till_date}`);
        },
      },
      // ,{
      //   Header: t("WMS_PR_PAYMENT_DATE_LABEL"),
      //   disablePrtBy: false,
      //   Cell: ({ row }) => {
      //     return GetCell(`${row.original?.end_date}`);
      //   },
      // },
      {
        Header: t("WMS_PR_CONTROLS_LABEL"),
        disablePrtBy: true,
        Cell: ({ row }) => {
          return (
            <span className="link">
              <Link to={`/upyog-ui/citizen/wms/pr-details/${row.original.register_id}`}>{t("WMS_PR_EDIT_LABEL")}</Link>
            </span>
          );
        },
      },
      // {
      //   Header: t("WMS_PR_CONTROLS2_LABEL"),
      //   disablePrtBy: true,
      //   Cell: ({ row }) => {
      //     return (
      //       <span className="link">
      //         <Link to={`/upyog-ui/citizen/wms/pma-home/${row.original.register_id}`}>{t("WMS_PR_COMMON_CREATE_ACTIVITY_HEADER")}</Link>
      //       </span>
      //     );
      //   },
      // },
    ];
  }, []);

  let result;
  if (props?.isLoading) {
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
              maxWidth: cellInfo.column.Header == t("WMS_PR_ID_LABEL") ? "150px" : "",
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
        onPrt={props.onPrt}
        disablePrt={props.disablePrt}
        prtParams={props.prtParams}
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
                text: t("WMS_PR_COMMON_CREATE_HEADER"),
                link: "/upyog-ui/citizen/wms/pr-create",
                businessService: "WMS",
                roles: ["CITIZEN"],
              },
/*               {
                text: t("WMS_PR_COMMON_CREATE_ACTIVITY_HEADER"),
                link: "/upyog-ui/citizen/wms/pr-activity-home",
                businessService: "WMS",
                roles: ["CITIZEN"],
              }, */
            ]}
            headerText={t("WMS_PR_CREATE_PAGE_LABEL")}
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
