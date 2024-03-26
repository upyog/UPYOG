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
      // {
      //   Header: t("WMS_PHM_ID_LABEL"),
      //   disablePhmtBy: true,
      //   Cell: ({ row }) => {
      //     return GetCell(`${row.original?.phm_id}`);
      //   },
      // },
      {
        Header: t("WMS_PHM_PROJECT_NAME_LABEL"),
        disablePhmtBy: false,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.project_name}`);
        },
      },
      {
        Header: t("WMS_PHM_WORK_NAME_LABEL"),
        disablePhmtBy: false,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.work_name}`);
        },
      },
      {
        Header: t("WMS_PHM_ML_NAME_LABEL"),
        disablePhmtBy: true,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.milestone_name}`);
        },
      },
      {
        Header: t("WMS_PHM_PERCENT_NAME_LABEL"),
        disablePhmtBy: true,
        Cell: ({ row }) => {
          console.log("rowrowrow ",row)
          return GetCell(`${row.original?.percentage_weightage}`);
          // return GetCell(`${Math.floor(Math.random() * 100)}`);
        },
      },
      // {
      //   Header: t("WMS_PHM_CONTROLS_LABEL"),
      //   disablePhmtBy: true,
      //   Cell: ({ row }) => {
      //     console.log(row, "it is row");
      //     return (
      //       <span className="link">
      //         <Link to={`/upyog-ui/citizen/wms/phm-details/${row.original.milestone_id}`}>{t("WMS_PHM_EDIT_LABEL")}</Link>
      //       </span>
      //     );
      //   },
      // },
      {
        Header: t("WMS_PHM_CONTROLS2_LABEL"),
        disablePhmtBy: true,
        Cell: ({ row }) => {
          return (
            <span className="link">
              <Link to={`/upyog-ui/citizen/wms/pma-home/${row.original.milestone_id}`}>{t("WMS_PHM_COMMON_CREATE_ACTIVITY_HEADER")}</Link>
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
              maxWidth: cellInfo.column.Header == t("WMS_PHM_ID_LABEL") ? "150px" : "",
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
        onPhmt={props.onPhmt}
        disablePhmt={props.disablePhmt}
        phmtParams={props.phmtParams}
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
                text: t("WMS_PHM_COMMON_CREATE_HEADER"),
                link: "/upyog-ui/citizen/wms/phm-create",
                businessService: "WMS",
                roles: ["CITIZEN"],
              },
/*               {
                text: t("WMS_PHM_COMMON_CREATE_ACTIVITY_HEADER"),
                link: "/upyog-ui/citizen/wms/phm-activity-home",
                businessService: "WMS",
                roles: ["CITIZEN"],
              }, */
            ]}
            headerText={t("WMS_PHM_CREATE_PAGE_LABEL")}
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
