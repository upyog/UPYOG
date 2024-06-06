import React from "react";
import { format } from "date-fns";
import { Link } from "react-router-dom";
import { Loader } from "@upyog/digit-ui-react-components";
import ApplicationTable from "./CHBApplicationTable";
import Search from "../../../engagement/src/components/Events/Search";
import EventLink from "../../../engagement/src/components/Events/EventLink";
import Filter from "../../../engagement/src/components/Events/Filter";

const GetCell = (value) => <span className="">{value}</span>;

const GetStatusCell = (value) => value === "Active" ? <span className="sla-cell-success">{value}</span> : <span className="sla-cell-error">{value}</span> 

const DesktopInbox = ({ isLoading, data, t, onSearch, parentRoute, title, iconName, links, globalSearch, searchFields, searchParams, onFilterChange, pageSizeLimit, totalRecords, currentPage, onNextPage, onPrevPage, onPageSizeChange }) => {
  const columns = React.useMemo(() => {
    return [
      {
        Header: t("EVENTS_EVENT_NAME_LABEL"),
        accessor: "name",
        Cell: ({ row }) => {
         
          return (
            <div>
              {/* <span className="link">
                <Link to={`${parentRoute}/event/inbox/event-details/${row.original.id}`}>{row.original["name"]}</Link>
              </span> */}
            </div>
          );
        },
      },
      {
        Header: t("EVENTS_EVENT_CATEGORY_LABEL"),
        // accessor: (row) => {
        //  return GetCell(row?.eventCategory ? t(`MSEVA_EVENTCATEGORIES_${row?.eventCategory}`) : "")
        // }
        },
      {
        Header: t("EVENTS_START_DATE_LABEL"),
        // accessor: (row) => row?.eventDetails?.fromDate ? GetCell(format(new Date(row?.eventDetails?.fromDate), 'dd/MM/yyyy')) : "",
      },
      {
        Header: t("EVENTS_END_DATE_LABEL"),
        // accessor: (row) => row?.eventDetails?.toDate ? GetCell(format(new Date(row?.eventDetails?.toDate), "dd/MM/yyyy")) : "",
      },
      {
        Header: t("EVENTS_POSTEDBY_LABEL"),
        // accessor: row => GetCell(row?.user?.name || "")
      },
      {
        Header: t("EVENTS_STATUS_LABEL"),
        // accessor: row => GetStatusCell(t(row?.status)),
      }
    ]
  })

  let result;
 
  if (isLoading) {
    result = <Loader />
  } else if (data?.length > 0) {
    result = <ApplicationTable
      t={t}
      data={data}
      columns={columns}
      globalSearch={globalSearch}
      onSearch={searchParams}
      pageSizeLimit={pageSizeLimit}
      totalRecords={totalRecords}
      currentPage={currentPage}
      onNextPage={onNextPage}
      onPrevPage={onPrevPage}
      onPageSizeChange={onPageSizeChange}
      getCellProps={(cellInfo) => {
        return {
          style: {
            minWidth: "150px",
            padding: "20px 18px",
            fontSize: "16px",
          },
        };
      }}
    />
  }

  return (
    <div>
      <div>
        <div>
          <Filter onFilterChange={onFilterChange} searchParams={searchParams} />
        </div>
      </div>
        <div className="result" style={{ marginLeft: "24px", flex: 1 }}>
          {result}
        </div>
    </div>
  )
}

export default DesktopInbox;