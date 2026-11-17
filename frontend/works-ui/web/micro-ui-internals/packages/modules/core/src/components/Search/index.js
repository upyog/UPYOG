import { BackButton, Card, Header, SearchForm, Table, Loader,DownloadBtnCommon } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useCallback, useEffect, useMemo , useState} from "react";
import { Controller, useForm } from "react-hook-form";
import { Link } from "react-router-dom";
import MobileSearchApplication from "./MobileSearchApplication";
import SearchFields from "./SearchFields";

const SearchApplication = ({showLoader,isLoading, tenantId, t, onSubmit, data, count }) => {

  const initialValues = Digit.SessionStorage.get("AUDIT_APPLICATION_DETAIL") || {
    offset: 0,
    limit:10,
    sortOrder: "ASC"
  };

  const { register, control, handleSubmit, setValue, getValues, reset } = useForm({
    defaultValues: initialValues,
  });
  const convertEpochToDate = (dateEpoch) => {
    if (dateEpoch == null || dateEpoch == undefined || dateEpoch == "") {
      return "NA";
    }
    const dateFromApi = new Date(dateEpoch);
    let month = dateFromApi.getMonth() + 1;
    let day = dateFromApi.getDate();
    let year = dateFromApi.getFullYear();
    month = (month > 9 ? "" : "0") + month;
    day = (day > 9 ? "" : "0") + day;
    return `${day}/${month}/${year}`;
  };
  const convertEpochToTimeInHours = (dateEpoch) => {
    if (dateEpoch == null || dateEpoch == undefined || dateEpoch == "") {
      return "NA";
    }
    const dateFromApi = new Date(dateEpoch);
    let hour = dateFromApi.getHours();
    let min = dateFromApi.getMinutes();
    let period = hour > 12 ? "PM" : "AM";
    hour = hour > 12 ? hour - 12 : hour;
    hour = (hour > 9 ? "" : "0") + hour;
    min = (min > 9 ? "" : "0") + min;
    return `${hour}:${min} ${period}`;
  };
  const [tabledata, settabledata] = useState([]);
  const DownloadBtn = (props) => {
    return (
      <div className={props.className || "citizen-audit-download-btn"} onClick={props.onClick}>
        <DownloadBtnCommon />
      </div>
    );
  };
  const handleExcelDownload = (tabData) => {
    if (tabData?.[0] !== undefined) {
      return Digit.Download.Excel(tabData?.[0], "AuditReport");
    }
  };
  const onClear = () => {
    reset({
      offset: 0,
      limit: 10,
      sortOrder: "ASC",
      fromDate: "",
      toDate: "",
    });
    Digit.SessionStorage.del("AUDIT_APPLICATION_DETAIL");
    settabledata([]);
  };
  useEffect(() => {
    register("offset", 0);
    register("limit", 10);
    //register("sortOrder", "DESC");
  }, [register]);
  useEffect(() => {
    if (data?.length > 0) {
      settabledata([
        data?.map((obj) => {
          let returnObject = {};
          returnObject[t("AUDIT_DATE_LABEL")] = convertEpochToDate(obj?.timestamp) || "-";
          returnObject[t("AUDIT_TIME_LABEL")] = convertEpochToTimeInHours(obj?.timestamp) || "-";
          returnObject[t("AUDIT_DATAVIEWED_LABEL")] = obj?.dataView?.join(", ") || "-";
          returnObject[t("AUDIT_DATAVIEWED_BY_LABEL")] = obj?.dataViewedBy;
          returnObject[t("AUDIT_ROLE_LABEL")] = obj?.roles?.map((obj) => obj.name).join(", ") || "-";
          return {
            ...returnObject,
          };
        }),
      ]);
    }
  }, [data]);
  const onSort = useCallback((args) => {
    if (args.length === 0) return;
    setValue("sortBy", args.id);
    setValue("sortOrder", args.desc ? "DESC" : "ASC");
  }, []);

  function onPageSizeChange(e) {
    setValue("limit", Number(e.target.value));
    handleSubmit(onSubmit)();
  }

  function nextPage() {
    setValue("offset", getValues("offset") + getValues("limit"));
    handleSubmit(onSubmit)();
  }
  function previousPage() {
    setValue("offset", getValues("offset") - getValues("limit"));
    handleSubmit(onSubmit)();
  }
  const isMobile = window.Digit.Utils.browser.isMobile();

  if (isMobile) {
    return (
      <MobileSearchApplication
        {...{ Controller, isLoading, showLoader, register, control, t, reset, previousPage, handleSubmit, tenantId, data, onSubmit, onClear }}
      />
    );
  }

  //need to get from workflow
  const GetCell = (value) => <span className="cell-text">{value}</span>;
  const columns = useMemo(
    () => [
      {
        Header: t("AUDIT_DATE_LABEL"),
        disableSortBy: true,
        accessor: (row) => {
          const timestamp = row.timestamp === "NA" ? t("WS_NA") : convertEpochToDate(row.timestamp);
          return GetCell(`${timestamp || "-"}`);
        },
      },
      {
        Header: t("AUDIT_TIME_LABEL"),
        disableSortBy: true,
        accessor: (row) => {
          const timestamp = row.timestamp === "NA" ? t("WS_NA") : convertEpochToTimeInHours(row.timestamp);
          return GetCell(`${timestamp || "-"}`);
        },
      },
      {
        Header: isMobile ? t("AUDIT_DATAVIEWED_LABEL") : t("AUDIT_DATAVIEWED_PRIVACY"),
        disableSortBy: true,
        accessor: (row) => {
          return GetCell(`${row?.dataView?.join(", ") || "-"}`);
        },
      },
      {
        Header: isMobile ? t("AUDIT_DATAVIEWED_BY_LABEL") : t("AUDIT_DATAVIEWED_BY_PRIVACY"),
        disableSortBy: true,
        accessor: (row) => {
          return GetCell(`${row?.dataViewedBy || "-"}`);
        },
      },
      {
        Header: t("AUDIT_ROLE_LABEL"),
        disableSortBy: true,
        accessor: (row) => {
          return GetCell(`${row?.roles?.slice(0, 3)?.map((e) => e.name)?.join(", ") || "-"}`);
        },
      },
    ],
    []
  );

  const hasRecords = Array.isArray(data) && data.length > 0;
  const recordCount = count ?? (hasRecords ? data.length : 0);

  return (
    <React.Fragment>
      <div className="citizen-audit-top-nav">
        <BackButton />
      </div>

      <div className="citizen-audit-header-wrap">
        <div className="citizen-audit-title-block">
          <div className="citizen-audit-header-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
            </svg>
          </div>
          <div className="citizen-audit-title-text">
            <div className="citizen-audit-title-row">
              <Header className="citizen-audit-title">{t("PRIVACY_AUDIT_REPORT")}</Header>
              {hasRecords && <span className="citizen-audit-count-badge">{recordCount}</span>}
            </div>
          </div>
        </div>
      </div>

      <div className="citizen-audit-search-card">
        <SearchForm className="citizen-audit-search-form" onSubmit={onSubmit} handleSubmit={handleSubmit}>
          <SearchFields {...{ register, control, reset, tenantId, t, previousPage, onClear }} />
        </SearchForm>
      </div>

      <div className="citizen-audit-results-section">
        {data?.display ? (
          <div className="citizen-audit-empty-card">
            <div className="citizen-audit-empty-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round">
                <circle cx="11" cy="11" r="8" />
                <line x1="21" y1="21" x2="16.65" y2="16.65" />
              </svg>
            </div>
            {t(data.display)
              .split("\\n")
              .map((text, index) => (
                <p key={index} className="citizen-audit-empty-text">
                  {text}
                </p>
              ))}
          </div>
        ) : data !== "" ? (
          <div className="citizen-audit-table-card">
            <div className="citizen-audit-table-toolbar">
              <div className="citizen-audit-toolbar-meta">
                <span>{t("PRIVACY_AUDIT_REPORT")}</span>
              </div>
              <DownloadBtn className="citizen-audit-download-btn" onClick={() => handleExcelDownload(tabledata)} />
            </div>
            <div className="citizen-audit-table-responsive">
              <Table
                t={t}
                data={data.sort((a, b) => a.timestamp - b.timestamp)}
                totalRecords={count}
                columns={columns}
                getCellProps={(cellInfo) => {
                  return {
                    className:
                      cellInfo.column.Header === t("ES_INBOX_APPLICATION_NO")
                        ? "audit-table-cell audit-table-cell--app-no"
                        : "audit-table-cell",
                  };
                }}
                onPageSizeChange={onPageSizeChange}
                currentPage={getValues("offset") / getValues("limit")}
                onNextPage={nextPage}
                onPrevPage={previousPage}
                pageSizeLimit={getValues("limit")}
                onSort={onSort}
                disableSort={false}
                sortParams={[{ id: getValues("sortBy"), desc: getValues("sortOrder") === "DESC" ? true : false }]}
              />
            </div>
          </div>
        ) : (
          <div className="citizen-audit-loader-card">
            <Loader />
          </div>
        )}
      </div>
    </React.Fragment>
  );
};

export default SearchApplication;